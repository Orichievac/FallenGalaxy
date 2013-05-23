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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import fr.fg.client.animation.BlinkUpdater;
import fr.fg.client.data.LotteryData;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;

public class LotteryItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private BlinkUpdater blinkUpdater;
	
	private Element sign;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public static final double LOTTERY_RADIUS = 4.5;

	public LotteryItem(LotteryData lotteryData,
			UIItemRenderingHints hints) {
		super(lotteryData.getX(), lotteryData.getY(), hints);
		
		setStylePrimaryName("lottery"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		
		setToolTipText("<div class=\"title\">" + messages.lottery() + "</div>" + //$NON-NLS-1$ //$NON-NLS-2$
				"<div class=\"justify\">" + messages.lotteryDesc() + "</div>", 200); //$NON-NLS-1$ //$NON-NLS-2$
		
		sign = buildSign(); 
		getElement().appendChild(sign);
		
		if (Config.getGraphicsQuality() >= Config.VALUE_QUALITY_HIGH | Config.getGraphicsQuality() >= Config.VALUE_QUALITY_LOW |
				Config.getGraphicsQuality() >= Config.VALUE_QUALITY_AVERAGE | Config.getGraphicsQuality() >= Config.VALUE_QUALITY_MAXIMUM) {
			blinkUpdater = new BlinkUpdater(sign, 850, 950);
			if (!isCulled())
				TimerManager.register(blinkUpdater);
		}
		
	
	}
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (blinkUpdater != null) {
			TimerManager.unregister(blinkUpdater);
			blinkUpdater = null;
		}
	}
		
		public void onCullStateUpdate(boolean culled) {
			super.onCullStateUpdate(culled);

			if (blinkUpdater != null) {
				if (culled) {
					TimerManager.unregister(blinkUpdater);
				} else {
					TimerManager.register(blinkUpdater);
				}
			}
		}
		
		private Element buildSign() {
			Element sign = DOM.createDiv();
			sign.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
			sign.setClassName("sign");

			return sign;
		}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	
}
