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

import fr.fg.client.data.FakeWardData;
import fr.fg.client.map.UIItemRenderingHints;

public class FakeWardItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private FakeWardData fakeWardData;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FakeWardItem(FakeWardData fakeWardData, UIItemRenderingHints hints) {
		super(fakeWardData.getX(), fakeWardData.getY(), hints);
		
		this.fakeWardData = fakeWardData;
		
		getElement().setClassName("ward ward-" +
				fakeWardData.getType().replace("_", "-")); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		getElement().setInnerHTML("<div class=\"setupMask\"></div>");
		
		updateData(fakeWardData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		FakeWardData newFakeWardData = (FakeWardData) newData;
		
		if (fakeWardData.getX() != newFakeWardData.getX() ||
				fakeWardData.getY() != newFakeWardData.getY())
			setLocation(newFakeWardData.getX(), newFakeWardData.getY());
		
		if (fakeWardData.isValid() != newFakeWardData.isValid())
			updateData(newFakeWardData);
		
		fakeWardData = newFakeWardData;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		fakeWardData = null;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(FakeWardData fakeWardData) {
		if (fakeWardData.isValid())
			getElement().getFirstChildElement().setClassName("setupMask");
		else
			getElement().getFirstChildElement().setClassName("setupMask invalid");
	}
}
