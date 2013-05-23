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

package fr.fg.client.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallback;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.CountryData;
import fr.fg.client.data.PaymentOptionData;
import fr.fg.client.data.PremiumStateData;
import fr.fg.client.data.PricingData;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
import fr.fg.client.openjwt.ui.JSScrollPane;
import fr.fg.client.openjwt.ui.JSTabbedPane;
import fr.fg.client.openjwt.ui.JSTextField;
import fr.fg.client.openjwt.ui.SelectionListener;

public class PremiumDialog extends JSDialog implements SelectionListener, ClickListener, ActionCallback {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSTabbedPane tabs;
	
	private JSScrollPane container;
	
	private HTMLPanel premiumBenefitsPanel;
	
	private JSRowLayout paymentLayout;
	
	private HTMLPanel premiumStatePanel;
	
	private JSComboBox countriesComboBox;
	
	private JSLabel codeLabel;
	
	private JSTextField codeField;
	
	private JSButton premiumCallingBt, premiumSmsBt, okBt;
	
	private PricingData pricing;
	
	private HTMLPanel paymentPanel;
	
	private Action action;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PremiumDialog() {
		super("Compte premium", false, true, true);
		
		tabs = new JSTabbedPane();
		tabs.setPixelWidth(620);
		tabs.addTab("Présentation");
		tabs.addTab("Devenir premium");
		tabs.addSelectionListener(this);

		premiumBenefitsPanel = new HTMLPanel(
			"<table>" +
//			"<tr>" +
//			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part0.png\"/></td>" +
//			"<td><div class=\"title\">Un plus grand choix de missions</div>" +
//			"<div></div></td>" +
//			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part1.png\"/></td>" +
			"<td><div class=\"title\">Nouveaux thèmes</div>" +
			"<div></div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part2.png\"/></td>" +
			"<td><div class=\"title\">Sauvegardez vos tactiques</div>" +
			"<div></div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part3.png\"/></td>" +
			"<td><div class=\"title\">Graphismes enrichis</div>" +
			"<div></div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part4.png\"/></td>" +
			"<td><div class=\"title\">Examinez les bidules</div>" +
			"<div>Que peuvent bien cacher ces étranges objets ? Attendez-vous à des surprises... bonnes ou mauvaises !</div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part5.png\"/></td>" +
			"<td><div class=\"title\">Sons et musique</div>" +
			"<div></div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part6.png\"/></td>" +
			"<td><div class=\"title\">Liste d'amis</div>" +
			"<div></div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part7.png\"/></td>" +
			"<td><div class=\"title\">Messagerie améliorée</div>" +
			"<div>Vos messages sont conservés 15 jours au lieu de 5 jours. " +
			"Vous pouvez également archiver vos messages pour qu'ils ne soit pas perdus.</div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part8.png\"/></td>" +
			"<td><div class=\"title\">Canaux de discussion personnalisés</div>" +
			"<div>Créez des canaux sur le chat pour y discuter avec vos " +
			"partenaires de mission, organiser des réunions...</div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part10.png\"/></td>" +
			"<td><div class=\"title\">Files de production plus longues</div>" +
			"<div>Programmez la création de bâtiments et de vaisseaux plus facilement, ainsi que vos recherches en cours ! </div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part11.png\"/></td>" +
			"<td><div class=\"title\">Precisions sur les sauts inter-quadrants</div>" +
			"<div>Evitez de tomber dans un secteur ennemi ! Atterrissez dans le secteur voulu si vous prenez votre temps !</div></td>" +
			"</tr>" +
			
			"<tr>" +
			"<td><img style=\"padding: 2px 6px;\" src=\"" + Config.getMediaUrl() + "images/premium/part9.png\"/></td>" +
			"<td><div class=\"title\">Soutenez Fallen Galaxy</div>" +
			"<div>Les comptes premium permettent de financer les serveurs nécessaires au bon fonctionnement du jeu. " +
			"Ne tardez plus pour soutenir votre jeu favori !</div></td>" +
			"</tr>" +
			
			"</table>"
		);
		
		container = new JSScrollPane();
		container.setView(premiumBenefitsPanel);
		container.setPixelSize(620, 400);
		
		paymentLayout = new JSRowLayout();
		
		premiumStatePanel = new HTMLPanel("");
		premiumStatePanel.addStyleName("messageHeader");
		
		JSLabel countryLabel = new JSLabel("1. Sélectionnez votre pays");
		countryLabel.setPixelWidth(250);
		countriesComboBox = new JSComboBox();
		countriesComboBox.setPixelWidth(200);
		countriesComboBox.addSelectionListener(this);
		
		JSLabel paymentOptionLabel = new JSLabel("2. Choisissez un moyen de paiement");
		paymentOptionLabel.setPixelWidth(250);
		
		premiumCallingBt = new JSButton("");
		premiumCallingBt.setPixelWidth(180);
		premiumCallingBt.addClickListener(this);

		premiumSmsBt = new JSButton("");
		premiumSmsBt.setPixelWidth(180);
		premiumSmsBt.addClickListener(this);
		
		paymentPanel = new HTMLPanel("");
		paymentPanel.addStyleName("premiumPayment");

		codeLabel = new JSLabel("4. Saisissez votre code");
		codeLabel.setPixelWidth(250);
		
		codeField = new JSTextField();
		codeField.setPixelWidth(100);
		codeField.setMaxLength(8);
		
		okBt = new JSButton("OK");
		okBt.setPixelWidth(80);
		okBt.addClickListener(this);
		
		JSLabel allopassLabel = new JSLabel("Solution de paiement opérée par Allopass");
		allopassLabel.addStyleName("small");
		allopassLabel.setPixelWidth(620);
		allopassLabel.setAlignment(JSLabel.ALIGN_CENTER);
		
		paymentLayout.addRowSeparator(5);
		paymentLayout.addComponent(premiumStatePanel);
		paymentLayout.addRowSeparator(15);
		paymentLayout.addComponent(countryLabel);
		paymentLayout.addComponent(countriesComboBox);
		paymentLayout.addRowSeparator(24);
		paymentLayout.addComponent(paymentOptionLabel);
		paymentLayout.addComponent(premiumCallingBt);
		paymentLayout.addComponent(premiumSmsBt);
		paymentLayout.addRowSeparator(24);
		paymentLayout.addComponent(paymentPanel);
		paymentLayout.addRowSeparator(12);
		paymentLayout.addComponent(codeLabel);
		paymentLayout.addComponent(codeField);
		paymentLayout.addComponent(okBt);
		paymentLayout.addRowSeparator(12);
		paymentLayout.addComponent(allopassLabel);
//		paymentLayout.addComponent(new JSLabel("Les comptes premiums ne sont pas disponibles durant la bêta-test."));
		
		JSRowLayout layout = new JSRowLayout();
		layout.addComponent(tabs);
		layout.addRow();
		layout.addComponent(container);
		
		setComponent(layout);
		centerOnScreen();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void onSuccess(AnswerData data) {
		PremiumStateData premiumState = data.getPremiumState();
		
		if (premiumState.isActive()) {
			String remainingTime = "";
			int days = premiumState.getRemainingHours() / 24;
			if (days > 0)
				remainingTime += days > 1 ? days + " jours" : days + " jour";
			int hours = premiumState.getRemainingHours() % 24;
			remainingTime += (remainingTime.length() > 0 ? " " : "") + hours + " heure" + (hours > 1 ? "s" : "");
			premiumStatePanel.getElement().setInnerHTML("Vous disposez d'un compte Premium pendant " + remainingTime +
				". Vous pouvez à tout moment prolonger votre compte Premium de <span class=\"emphasize\">30 jours</span> en achetant un code.");
		} else {
			premiumStatePanel.getElement().setInnerHTML("Achetez un code pour bénéficier des avantages du compte Premium pendant <span class=\"emphasize\">30 jours</span> !");
		}
		
		pricing = data.getPremiumState().getPricing();
		
		while (countriesComboBox.getItemsCount() > 0)
			countriesComboBox.removeItemAt(0);
		
		List<CountryData> countries = new ArrayList<CountryData>();
		for (int i = 0; i < pricing.getCountriesCount(); i++)
			countries.add(pricing.getCountryAt(i));
		
		Collections.sort(countries, new Comparator<CountryData>() {
			public int compare(CountryData c1, CountryData c2) {
				return c1.getCountryName().compareToIgnoreCase(c2.getCountryName());
			}
		});
		
		String playerCountry = pricing.getPlayerCountry().length() > 0 ? pricing.getPlayerCountry() : "FR";
		
		int i = 0;
		for (CountryData country : countries) {
			countriesComboBox.addItem(new CountryUI(country));
			if (country.getCountryCode().equals(playerCountry))
				countriesComboBox.setSelectedIndex(i);
			i++;
		}
		
		codeField.setText("");
		
		updatePaymentOptions();
	}
	
	public void onFailure(String error) {
		ActionCallbackAdapter.onFailureDefaultBehavior(error);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			new Action("premium/getstate", Action.NO_PARAMETERS, this);
		}
	}
	
	public void selectionChanged(Widget sender, int newValue, int oldValue) {
		if (sender == tabs) {
			switch (newValue) {
			case 0:
				container.setView(premiumBenefitsPanel);
				break;
			case 1:
				container.setView(paymentLayout);
				break;
			}
		} else if (sender == countriesComboBox) {
			updatePaymentOptions();
			
		}
	}
	
	public void onClick(Widget sender) {
		if (sender == premiumCallingBt) {
			premiumCallingBt.setSelected(true);
			premiumSmsBt.setSelected(false);
			
			CountryUI country = (CountryUI) countriesComboBox.getSelectedItem();
			
			for (int i = 0; i < country.getData().getPaymentOptionsCount(); i++) {
				PaymentOptionData option = country.getData().getPaymentOptionAt(i);
				
				if (option.getType().equals(PaymentOptionData.TYPE_PREMIUM_CALLING)) {
					paymentPanel.getElement().setInnerHTML(
						"<div style=\"padding-bottom: 14px;\">3. " + option.getDescription() + "</div>" +
						"<div>" + option.getLegal() + "</div>");

					codeLabel.setVisible(true);
					codeField.setVisible(true);
					okBt.setVisible(true);
					break;
				}
			}
			
			paymentLayout.update();
		} else if (sender == premiumSmsBt) {
			premiumSmsBt.setSelected(true);
			premiumCallingBt.setSelected(false);
			
			CountryUI country = (CountryUI) countriesComboBox.getSelectedItem();
			
			for (int i = 0; i < country.getData().getPaymentOptionsCount(); i++) {
				PaymentOptionData option = country.getData().getPaymentOptionAt(i);
				
				if (option.getType().equals(PaymentOptionData.TYPE_PREMIUM_SMS)) {
					paymentPanel.getElement().setInnerHTML(
							"<div style=\"padding-bottom: 14px;\">3. " + option.getDescription() + "</div>" +
							"<div>" + option.getLegal() + "</div>");

					codeLabel.setVisible(true);
					codeField.setVisible(true);
					okBt.setVisible(true);
					break;
				}
			}
			
			paymentLayout.update();
		} else if (sender == okBt) {
			if (codeField.getText().length() == 0) {
				JSOptionPane.showMessageDialog("Saisissez un code", "Erreur",
					JSOptionPane.OK_OPTION, JSOptionPane.ERROR_MESSAGE, null);
				return;
			}
			
			if (action != null && action.isPending())
				return;
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("code", codeField.getText());
			
			action = new Action("premium/validatecode", params, this);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private String formatPrice(double price, String currency) {
		String symbol = null;
		boolean front = false;
		String separator = ",";
		
		if ("EUR".equals(currency))
			symbol = "€";
		else if ("USD".equals(currency) || "CAD".equals(currency)) {
			symbol = "$";
			front = true;
			separator = ".";
		} else if ("DKK".equals(currency) || "SEK".equals(currency))
			symbol = "kr";
		else if ("LVL".equals(currency))
			symbol = "Ls";
		else if ("LTL".equals(currency))
			symbol = "Lt";
		else if ("HUF".equals(currency))
			symbol = "Ft";
		else if ("PLN".equals(currency))
			symbol = "zł";
		else if ("GBP".equals(currency)) {
			symbol = "£";
			front = true;
			separator = ".";
		} else if ("CZK".equals(currency))
			symbol = "Kč";
		else if ("BGN".equals(currency))
			symbol = "лв";
		if (symbol == null)
			symbol = currency;
		
		String value = String.valueOf(toFixed(price, 2));
		if (value.contains(".")) {
			value = value.replace(".", separator);
			int length = value.length() - value.indexOf(separator) - 1;
			while (length < 2) {
				length++;
				value += "0";
			}
		}
		
		return (front ? symbol : "") + value + (front ? "" : symbol);
	}
	
    private static native String toFixed(double val, int places) /*-{
        return val.toFixed(places);
    }-*/;
    
	private void updatePaymentOptions() {
		premiumCallingBt.setVisible(false);
		premiumSmsBt.setVisible(false);
		
		if (countriesComboBox.getSelectedIndex() != -1) {
			CountryUI country = (CountryUI) countriesComboBox.getSelectedItem();
			
			for (int i = 0; i < country.getData().getPaymentOptionsCount(); i++) {
				PaymentOptionData option = country.getData().getPaymentOptionAt(i);
				
				if (option.getType().equals(PaymentOptionData.TYPE_PREMIUM_CALLING)) {
					premiumCallingBt.setLabel("<img src=\"" + Config.getMediaUrl() +
							"images/misc/blank.gif\" class=\"iconPremiumCalling\"/>" +
							"&nbsp;Téléphone (" + formatPrice(option.getPrice(), option.getCurrency()) + ")");
					premiumCallingBt.setVisible(true);
				} else if (option.getType().equals(PaymentOptionData.TYPE_PREMIUM_SMS)) {
					premiumSmsBt.setLabel("<img src=\"" + Config.getMediaUrl() +
							"images/misc/blank.gif\" class=\"iconPremiumSms\"/>" +
							"&nbsp;SMS (" + formatPrice(option.getPrice(), option.getCurrency()) + ")");
					premiumSmsBt.setVisible(true);
				}
			}
		}
		
		premiumCallingBt.setSelected(false);
		premiumSmsBt.setSelected(false);
		paymentPanel.getElement().setInnerHTML("");
		
		codeLabel.setVisible(false);
		codeField.setVisible(false);
		okBt.setVisible(false);
		
		paymentLayout.update();
	}
	
	public static class CountryUI {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private CountryData data;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public CountryUI(CountryData data) {
			this.data = data;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public CountryData getData() {
			return data;
		}
		
		@Override
		public String toString() {
			return "<img src=\"" + Config.getMediaUrl() + "images/flags/" +
				data.getCountryCode().toLowerCase() +
				".png\" style=\"vertical-align: middle;\"> " +
				data.getCountryName();
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
