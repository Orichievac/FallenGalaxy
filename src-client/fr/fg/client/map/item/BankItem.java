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

package fr.fg.client.map.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import fr.fg.client.data.BankData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;

public class BankItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static double BANK_RADIUS = 4.5;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public BankItem(BankData bankData,
			UIItemRenderingHints hints) {
		super(bankData.getX(), bankData.getY(), hints);
		
		setStylePrimaryName("bank"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		Element shield = DOM.createDiv();
		shield.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		shield.setClassName("shield"); //$NON-NLS-1$
		getElement().appendChild(shield);
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText("<div class=\"title\">" + messages.bank() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.bankDesc() + "</div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// --------------------------------------------------------- METHODES -- //
	// ------------------------------------------------- METHODES PRIVEES -- //
}
