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

import fr.fg.client.core.Client;
import fr.fg.client.data.EffectData;
import fr.fg.client.data.Sounds;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.SoundManager;

public class EffectItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private EffectData effectData;
	
	private EffectUpdater updater;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public EffectItem(EffectData explosionData, UIItemRenderingHints hints) {
		super(explosionData.getX(), explosionData.getY(), hints);
		
		this.effectData = explosionData;
		
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		setStylePrimaryName("effect " + explosionData.getType()); //$NON-NLS-1$
		
		updater = new EffectUpdater(this);
		TimerManager.register(updater);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		TimerManager.unregister(updater);
		updater = null;
		effectData = null;
		Client.getInstance().getAreaContainer().getElement().getStyle().setProperty("margin", "");
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static class EffectUpdater implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private EffectItem item;
		
		private int offset;
		
		private boolean finished;
		
		private int lastFrame;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public EffectUpdater(EffectItem item) {
			this.item = item;
			this.offset = 0;
			this.finished = false;
			this.lastFrame = -1;
			update(0);
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void update(int interpolation) {
			offset += interpolation;
			String type = item.effectData.getType();
			boolean shakeFinished = false, animationFinished = false;
			
			if (type.startsWith(EffectData.TYPE_FLEET_DESTRUCTION) ||
					type.startsWith(EffectData.TYPE_STATION_DESTRUCTION) ||
					type.startsWith(EffectData.TYPE_LARGE_STRUCTURE_DESTRUCTION) ||
					type.startsWith(EffectData.TYPE_SMALL_STRUCTURE_DESTRUCTION)) {
				// Secousse
				int length, startShake, endShake;
				if (type.equals(EffectData.TYPE_FLEET_DESTRUCTION) ||
						type.equals(EffectData.TYPE_SMALL_STRUCTURE_DESTRUCTION)) {
					length = 2500;
					startShake = 11;
					endShake = 5;
				} else {
					length = 4500;
					startShake = 25;
					endShake = 9;
				}
				
				if (offset < length && !item.isCulled()) {
					String margin = offset > length / 2 ?
							(int) (Math.random() * endShake - endShake / 2) + "px 0 0 " + (int) (Math.random() * endShake - endShake / 2) + "px" :
							(int) (Math.random() * startShake - startShake / 2) + "px 0 0 " + (int) (Math.random() * startShake - startShake / 2) + "px";
					Client.getInstance().getAreaContainer().getElement().getStyle().setProperty("margin", margin);
				} else {
					Client.getInstance().getAreaContainer().getElement().getStyle().setProperty("margin", "");
				}
				
				shakeFinished = offset >= length;
			} else {
				shakeFinished = true;
			}
			
			// Animation
			int df = offset / 200;
			int offsetX = -1, offsetY = -1;
			
			String html = null;
			
			if (type.equals(EffectData.TYPE_WARD_DESTRUCTION)) {
				// Destruction de balise
				if (df < 12) {
					offsetX = df * 32;
					offsetY = 450;
					html = "<div class=\"sprite sprite-explosion\" style=\"background-position: -" + offsetX + "px -" + offsetY + "px;\"></div>";
				} else {
					animationFinished = true;
				}
				
				if (!item.isCulled() && df == 0 && df != lastFrame)
					SoundManager.getInstance().playSound(Sounds.IMPACT);
			} else if (type.equals(EffectData.TYPE_FLEET_DESTRUCTION) ||
					type.equals(EffectData.TYPE_SMALL_STRUCTURE_DESTRUCTION)) {
				// Destruction de flotte / petite structure
				if (df < 12) {
					offsetX = (df % 5) * 100;
					offsetY = 150 + (df / 5) * 100;
					html = "<div class=\"sprite sprite-destruction\" style=\"background-position: -" + offsetX + "px -" + offsetY + "px;\"></div>";
				} else {
					animationFinished = true;
				}
				
				if (!item.isCulled() && df == 0 && df != lastFrame)
					SoundManager.getInstance().playSound(Sounds.EXPLOSION);
			} else if (type.equals(EffectData.TYPE_EMP)) {
				// EMP
				if (df < 10) {
					offsetX = (df % 5) * 70;
					offsetY = 482;
					html = "<div class=\"sprite sprite-emp\" style=\"background-position: -" + offsetX + "px -" + offsetY + "px;\"></div>";
				} else {
					animationFinished = true;
				}
			} else if (type.equals(EffectData.TYPE_LARGE_STRUCTURE_DESTRUCTION) ||
					type.equals(EffectData.TYPE_STATION_DESTRUCTION)) {
				// Destruction de station
				StringBuffer buffer = new StringBuffer();
				int tileSize = item.hints.getTileSize();
				int[] timeline = {0, 2, 6, 8, 10};
				int[][] locations = {{0, 0}, {2 * tileSize, tileSize},
						{-2 * tileSize, -tileSize}, {tileSize, 0}, {-tileSize, tileSize}};
				
				for (int i = 0; i < timeline.length; i++) {
					if (df >= timeline[i] && df < 12 + timeline[i]) {
						int tmp = df - timeline[i];
						offsetX = (tmp % 5) * 100;
						offsetY = 150 + (tmp / 5) * 100;
						int x = locations[i][0] - 50;
						int y = locations[i][1] - 50;
						buffer.append("<div class=\"sprite sprite-destruction\" style=\"background-position: -" + offsetX + "px -" + offsetY + "px; margin: " + y + "px 0 0 " + x + "px;\"></div>");
					}
				}
				
				timeline = new int[]{1, 2, 2, 4, 6, 9, 11, 12};
				locations = new int[][]{{3 * tileSize, tileSize}, {-tileSize, -2 * tileSize},
						{-2 * tileSize, 2 * tileSize}, {-3 * tileSize, -2 * tileSize},
						{-tileSize, tileSize}, {tileSize, -3 * tileSize},
						{0, 0}, {-tileSize, 2 * tileSize}};
				
				for (int i = 0; i < timeline.length; i++) {
					if (df >= timeline[i] && df < 6 + timeline[i]) {
						int tmp = df - timeline[i];
						offsetX = tmp * 32;
						offsetY = 450;
						int x = locations[i][0] - 16;
						int y = locations[i][1] - 16;
						buffer.append("<div class=\"sprite sprite-explosion\" style=\"background-position: -" + offsetX + "px -" + offsetY + "px; margin: " + y + "px 0 0 " + x + "px;\"></div>");
					}
				}
				
				html = buffer.toString();
				
				if (!item.isCulled()) {
					if ((df == 0 || df == 6 || df == 10) && df != lastFrame)
						SoundManager.getInstance().playSound(Sounds.EXPLOSION);
					if ((df == 1 || df == 5 || df == 8) && df != lastFrame)
						SoundManager.getInstance().playSound(Sounds.IMPACT);
				}
				
				if (df >= 22)
					animationFinished = true;
			} else {
				animationFinished = true;
			}
			
			if (html != null)
				item.getElement().setInnerHTML(html);
			else if (animationFinished)
				item.getElement().getStyle().setProperty("display", "none");
			
			lastFrame = df;
			this.finished = shakeFinished && animationFinished;
		}
		
		public boolean isFinished() {
			return finished;
		}
		
		public void destroy() {
			item = null;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
