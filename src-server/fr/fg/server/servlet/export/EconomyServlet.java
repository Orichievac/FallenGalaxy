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

package fr.fg.server.servlet.export;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import fr.fg.server.data.Bank;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Tradecenter;
import fr.fg.server.servlet.BaseServlet;
import fr.fg.server.util.Utilities;

public class EconomyServlet extends BaseServlet {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private static final long serialVersionUID = 8600600362295682616L;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private static double[] tradeRates, tradeRatesVariation,
		bankRates, bankRatesVariation;
	
	private static long validityDate = 0;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		updateValues();
		
		Element root = new Element("economy");
		
		// Commerce
		Element tradeElement = new Element("trade");
		root.addContent(tradeElement);
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
			double variation = ((int) Math.round(tradeRatesVariation[i] * 1000)) / 10.;
			
			Element resourceElement = new Element("resource");
			resourceElement.setAttribute("index", String.valueOf(i));
			resourceElement.setAttribute("rate", String.valueOf(
				(int) Math.round(tradeRates[i] * 100)));
			resourceElement.setAttribute("variation",
				(variation >= 0 ? "+" : "") + String.valueOf(variation));
			tradeElement.addContent(resourceElement);
		}
		
		// Banques
		Element bankElement = new Element("bank");
		root.addContent(bankElement);
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
			double variation = ((int) Math.round(bankRatesVariation[i] * 1000)) / 10.;
			
			Element resourceElement = new Element("resource");
			resourceElement.setAttribute("index", String.valueOf(i));
			resourceElement.setAttribute("rate", String.valueOf(
				((int) Math.round(bankRates[i] * 3600 * 24 * 7 * 1000)) / 10.));
			resourceElement.setAttribute("variation",
				(variation >= 0 ? "+" : "") + String.valueOf(variation));
			bankElement.addContent(resourceElement);
		}
		
		// Sortie en XML
		writeXML(response, root);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static synchronized void updateValues() {
		if (validityDate > Utilities.now())
			return;
		
		double[] previousTradeRates = new double[GameConstants.RESOURCES_COUNT];
		double[] previousBankRates = new double[GameConstants.RESOURCES_COUNT];
		
		if (validityDate != 0) {
			// Sauvegarde les valeurs précédentes
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
				previousTradeRates[i] = tradeRates[i];
				previousBankRates[i] = bankRates[i];
			}
		}
		
		tradeRates = new double[GameConstants.RESOURCES_COUNT];
		tradeRatesVariation = new double[GameConstants.RESOURCES_COUNT];
		bankRates = new double[GameConstants.RESOURCES_COUNT];
		bankRatesVariation = new double[GameConstants.RESOURCES_COUNT];
		
		List<Tradecenter> tradecenters = new ArrayList<Tradecenter>(
				DataAccess.getAllTradecenters());
		
		for (Tradecenter tradecenter : tradecenters)
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				tradeRates[i] += tradecenter.getRate(i);
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			tradeRates[i] /= tradecenters.size();
		
		List<Bank> banks = new ArrayList<Bank>(DataAccess.getAllBanks());
		
		for (Bank bank : banks) {
			double[] rates = bank.getRates();
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
				bankRates[i] += rates[i];
		}
		
		for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++)
			bankRates[i] /= banks.size();
		
		if (validityDate == 0) {
			for (int i = 0; i < GameConstants.RESOURCES_COUNT; i++) {
				tradeRatesVariation[i] = 0;
				bankRatesVariation[i] = 0;
			}
		} else {
			for (int i = 0; i < bankRates.length; i++) {
				tradeRatesVariation[i] = 1 - tradeRates[i] / previousTradeRates[i];
				bankRatesVariation[i] = 1 - bankRates[i] / previousBankRates[i];
			}
		}
		
		validityDate = Utilities.now() + 3600;
	}
}
