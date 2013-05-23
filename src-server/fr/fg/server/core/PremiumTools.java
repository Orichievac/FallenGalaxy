/*
Copyright 2010 Jeremie Gottero

This file is part of Fallen Galaxy.

Fallen Galaxy is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Fallen Galaxy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Fallen Galaxy. If not, see <http://www.gnu.org/licenses/>.
*/

package fr.fg.server.core;

import java.util.List;
import java.util.TreeMap;

import apikit.api.AllopassAPI;
import apikit.model.AllopassKeyword;
import apikit.model.AllopassMarket;
import apikit.model.AllopassPhoneNumber;
import apikit.model.AllopassPricepoint;
import apikit.model.OnetimePricingResponse;
import apikit.model.OnetimeValidateCodesResponse;
import fr.fg.client.data.CountryData;
import fr.fg.client.data.PremiumStateData;
import fr.fg.client.data.PricingData;
import fr.fg.client.data.PaymentOptionData;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Event;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.PremiumTransaction;
import fr.fg.server.util.Config;
import fr.fg.server.util.JSONStringer;
import fr.fg.server.util.LoggingSystem;

public class PremiumTools {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String
		TYPE_PREMIUM_SMS = "premium-sms",
		TYPE_PREMIUM_CALLING = "premium-calling",
		TYPE_CREDIT_CARD = "credit-card";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@SuppressWarnings("unchecked")
	public static JSONStringer getPremiumState(JSONStringer json, Player player, String ip) {
		if (json == null)
			json = new JSONStringer();
		
		json.object().
			key(PremiumStateData.FIELD_ACTIVE).				value(player.isPremium()).
			key(PremiumStateData.FIELD_REMAINING_HOURS).	value(player.getPremiumHours()).
			key(PremiumStateData.FIELD_PRICING);
		
		AllopassAPI api = new AllopassAPI();
		TreeMap<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("site_id", Config.getAllopassIdSite());
		parameters.put("product_id", Config.getAllopassIdProduct());
		if (ip != null)
			parameters.put("customer_ip",  ip);
		try {
			OnetimePricingResponse response = (OnetimePricingResponse) api.getOnetimePricing(parameters);
			
			json.object().
				key(PricingData.FIELD_PLAYER_COUNTRY).	value(response.getCustomerCountry()).
				key(PricingData.FIELD_COUNTRIES).		array();
			
			List<AllopassMarket> markets = response.getMarkets();
			for (AllopassMarket market : markets) {
				boolean appendCountry = true;
				
				List<AllopassPricepoint> pricepoints = market.getPricepoints();
				
				for (AllopassPricepoint pricepoint : pricepoints) {
					String description;
					
					if (pricepoint.getType().equals(TYPE_PREMIUM_CALLING)) {
						List<AllopassPhoneNumber> phoneNumbers = pricepoint.getPhoneNumbers();
						
						if (phoneNumbers.size() == 0)
							continue;
						
						description = phoneNumbers.get(0).getDescription();
					} else if (pricepoint.getType().equals(TYPE_PREMIUM_SMS)) {
						List<AllopassKeyword> keywords = pricepoint.getKeywords();
						
						if (keywords.size() != 1)
							continue;
						if (!keywords.get(0).getOperators().equals("*"))
							continue;
						
						description = keywords.get(0).getDescription();
					} else {
						continue;
					}
					
					if (appendCountry) {
						json.object().
							key(CountryData.FIELD_COUNTRY_CODE).		value(market.getCountryCode()).
							key(CountryData.FIELD_COUNTRY_NAME).		value(market.getCountry()).
							key(CountryData.FIELD_PAYMENT_OPTIONS).	array();
						appendCountry = false;
					}
					
					json.object().
						key(PaymentOptionData.FIELD_ID).			value(pricepoint.getId()).
						key(PaymentOptionData.FIELD_TYPE).			value(pricepoint.getType()).
						key(PaymentOptionData.FIELD_DESCRIPTION).	value(description).
						key(PaymentOptionData.FIELD_LEGAL).			value(pricepoint.getDescription()).
						key(PaymentOptionData.FIELD_PRICE).			value(pricepoint.getPrice().getAmount()).
						key(PaymentOptionData.FIELD_CURRENCY).		value(pricepoint.getPrice().getCurrency()).
						endObject();
				}
				
				if (!appendCountry) {
					json.endArray().
						endObject();
				}
			}
			
			json.endArray().
				endObject();
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error("Failed to retrieve Allopass pricings.", e);
			
			json.object().
				key(PricingData.FIELD_PLAYER_COUNTRY).	value("").
				key(PricingData.FIELD_COUNTRIES).		array().endArray().
				endObject();
		}
		
		json.endObject();
		
		return json;
	}
	
	public static boolean validateCode(Player player, String ip, String code) {
		AllopassAPI api = new AllopassAPI();
		TreeMap<String, Object> codes = new TreeMap<String, Object>();
		codes.put("code", code);
		
		TreeMap<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("site_id", Config.getAllopassIdSite());
		parameters.put("product_id", Config.getAllopassIdProduct());
		parameters.put("code", codes);
		
		try {
			OnetimeValidateCodesResponse response = (OnetimeValidateCodesResponse) api.validateCodes(parameters);
			
			boolean success = response.getStatus() == 0;
			
			LoggingSystem.getPremiumLogger().info("Player [id=" + player.getId() + ",login=" + player.getLogin() + ",ip=" + ip + "] - Validation code result [code=" + code + ",result=" + (success ? "success" : "failure") + "]");
			
			if (success) {
				int boughtHours = GameConstants.ALLOPASS_CODE_PREMIUM_HOURS + 1; // Heure en cours offerte !
				PremiumTransaction transaction = new PremiumTransaction(player.getId(),
					code, player.getPremiumHours(), boughtHours);
				transaction.save();
				
				boolean newPremium = !player.isPremium();
				
				synchronized (player.getLock()) {
					player = DataAccess.getEditable(player);
					player.setRights(player.getRights() | Player.PREMIUM);
					player.setPremiumHours(player.getPremiumHours() + boughtHours);
					player.save();
				}
				
				if (newPremium) {
					Event event = new Event(Event.EVENT_PREMIUM_START, Event.TARGET_PLAYER,
						player.getId(), 0, -1, -1, String.valueOf(boughtHours / 24));
					event.save();
				} else {
					Event event = new Event(Event.EVENT_PREMIUM_EXTENDED, Event.TARGET_PLAYER,
						player.getId(), 0, -1, -1, String.valueOf(boughtHours / 24),
						String.valueOf(player.getPremiumHours() / 24));
					event.save();
				}
			}
			
			return success;
		} catch (Exception e) {
			LoggingSystem.getServerLogger().error("Failed to validate Allopass code [player=" + player.getLogin() + ",code=" + code + "].", e);
		}
		
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(validateCode(DataAccess.getPlayerByLogin("jayjay"), "193.47.80.25", "X389D438"));
//		JSONObject json = new JSONObject(getPremiumState(null, DataAccess.getPlayerByLogin("jayjay"), "193.47.80.25").toString());
//		System.out.println(json.toString(2));
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
