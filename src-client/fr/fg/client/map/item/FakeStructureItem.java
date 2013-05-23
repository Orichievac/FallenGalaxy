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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import fr.fg.client.data.FakeStructureData;
import fr.fg.client.data.StructureData;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.openjwt.core.Dimension;

public class FakeStructureItem extends AnimatedItem {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private FakeStructureData fakeStructureData;
	
	private Element deactivationElement;
	
	private Element setupMaskElement;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public FakeStructureItem(FakeStructureData fakeStructureData, UIItemRenderingHints hints) {
		super(fakeStructureData.getX(), fakeStructureData.getY(), hints);
		
		this.fakeStructureData = fakeStructureData;
		
		getElement().setClassName("structure fakeStructure"); //$NON-NLS-1$
		getElement().setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		
		deactivationElement = DOM.createDiv();
		deactivationElement.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		deactivationElement.setClassName("structure-deactivated");
		deactivationElement.getStyle().setProperty("display", "none");
		getElement().appendChild(deactivationElement);
		
		setupMaskElement = DOM.createDiv();
		setupMaskElement.setAttribute("unselectable", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		setupMaskElement.setClassName("setupMask");
		getElement().appendChild(setupMaskElement);
		
		updateRendering(fakeStructureData);
		updateData(fakeStructureData);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onDataUpdate(Object newData) {
		FakeStructureData newFakeStructureData = (FakeStructureData) newData;
		
		if (fakeStructureData.getX() != newFakeStructureData.getX() ||
				fakeStructureData.getY() != newFakeStructureData.getY())
			setLocation(newFakeStructureData.getX(), newFakeStructureData.getY());
		
		if (fakeStructureData.isValid() != newFakeStructureData.isValid() ||
				fakeStructureData.isActivated() != newFakeStructureData.isActivated()) {
			updateData(newFakeStructureData);
			updateRendering(newFakeStructureData);
		}
		
		fakeStructureData = newFakeStructureData;
	}
	
	@Override
	public void onRenderingHintsUpdate() {
		super.onRenderingHintsUpdate();
		
		updateRendering(fakeStructureData);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		fakeStructureData = null;
		deactivationElement = null;
		setupMaskElement = null;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private void updateData(FakeStructureData fakeStructureData) {
		if (fakeStructureData.isValid())
			setupMaskElement.setClassName("setupMask");
		else
			setupMaskElement.setClassName("setupMask invalid");
		
		deactivationElement.getStyle().setProperty(
			"display", fakeStructureData.isActivated() ? "none" : "");
	}
	
	private void updateRendering(FakeStructureData fakeStructureData) {
		Dimension size = StructureData.getSize(fakeStructureData.getType());
		
		setPixelSize(
			(int) (size.getWidth() * hints.getTileSize() * hints.getZoom()),
			(int) (size.getHeight() * hints.getTileSize() * hints.getZoom())
		);
		
		int margin = (int) ((size.getWidth() / 2) * hints.getTileSize() * hints.getZoom());
		getElement().getStyle().setProperty("margin", "-" + margin + "px 0 0 -" + margin + "px");
		
		double coef = 0;
		if (hints.getZoom() == .5)
			coef = 1;
		else if (hints.getZoom() == .25)
			coef = 1.5;
		else if (hints.getZoom() == .125)
			coef = 1.75;
		
		if (fakeStructureData.isValid() &&
				fakeStructureData.getType() == StructureData.TYPE_GENERATOR) {
			coef += 1.75 * ((fakeStructureData.getX() + fakeStructureData.getY()) % 3);
		}
		
		getElement().getStyle().setProperty("backgroundPosition", "-" +
			(int) (size.getWidth() * hints.getTileSize() * coef) + "px -" +
			(250 + StructureData.getBackgroundOffset(fakeStructureData.getType()) * hints.getTileSize()) + "px");
	}
}
