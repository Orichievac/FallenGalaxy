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

package fr.fg.client.map.impl;

import com.google.gwt.core.client.JavaScriptObject;

import fr.fg.client.data.AsteroidsData;
import fr.fg.client.data.BankData;
import fr.fg.client.data.LotteryData;
import fr.fg.client.data.BlackHoleData;
import fr.fg.client.data.DoodadData;
import fr.fg.client.data.EffectData;
import fr.fg.client.data.FakeStructureData;
import fr.fg.client.data.FakeWardData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GateData;
import fr.fg.client.data.GravityWellData;
import fr.fg.client.data.HyperspaceSignatureData;
import fr.fg.client.data.MarkerData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.data.StructureData;
import fr.fg.client.data.TradeCenterData;
import fr.fg.client.data.WardData;
import fr.fg.client.map.UIItem;
import fr.fg.client.map.UIItemRenderingHints;
import fr.fg.client.map.item.AsteroidsItem;
import fr.fg.client.map.item.BankItem;
import fr.fg.client.map.item.LotteryItem;
import fr.fg.client.map.item.BlackHoleItem;
import fr.fg.client.map.item.DoodadItem;
import fr.fg.client.map.item.EffectItem;
import fr.fg.client.map.item.FakeStructureItem;
import fr.fg.client.map.item.FakeWardItem;
import fr.fg.client.map.item.FleetItem;
import fr.fg.client.map.item.GateItem;
import fr.fg.client.map.item.GravityWellItem;
import fr.fg.client.map.item.HyperspaceSignatureItem;
import fr.fg.client.map.item.MarkerItem;
import fr.fg.client.map.item.SpaceStationItem;
import fr.fg.client.map.item.StarSystemItem;
import fr.fg.client.map.item.StructureItem;
import fr.fg.client.map.item.TradeCenterItem;
import fr.fg.client.map.item.WardItem;

public class UIItemFactory {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private UIItemRenderingHints hints;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public UIItemFactory(UIItemRenderingHints hints) {
		this.hints = hints;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public UIItemRenderingHints getRenderingHints() {
		return hints;
	}
	
	public void setRenderingHints(UIItemRenderingHints hints) {
		this.hints = hints;
	}
	
	public String getHashCode(Object data, String dataClass) {
		if (data instanceof JavaScriptObject)
			return getNativeHashCode(data, dataClass);
		else
			return String.valueOf(dataClass.hashCode());
	}
	
	public UIItem createItem(Object data, String dataClass) {
		if (dataClass.equals(StarSystemData.CLASS_NAME))
			return new StarSystemItem((StarSystemData) data, hints);
		else if (dataClass.equals(AsteroidsData.CLASS_NAME))
			return new AsteroidsItem((AsteroidsData) data, hints);
		else if (dataClass.equals(DoodadData.CLASS_NAME))
			return new DoodadItem((DoodadData) data, hints);
		else if (dataClass.equals(GateData.CLASS_NAME))
			return new GateItem((GateData) data, hints);
		else if (dataClass.equals(BlackHoleData.CLASS_NAME))
			return new BlackHoleItem((BlackHoleData) data, hints);
		else if (dataClass.equals(MarkerData.CLASS_NAME))
			return new MarkerItem((MarkerData) data, hints);
		else if (dataClass.equals(FleetData.CLASS_NAME))
			return new FleetItem((FleetData) data, hints);
		else if (dataClass.equals(BankData.CLASS_NAME))
			return new BankItem((BankData) data, hints);
		else if (dataClass.equals(TradeCenterData.CLASS_NAME))
			return new TradeCenterItem((TradeCenterData) data, hints);
		else if (dataClass.equals(SpaceStationData.CLASS_NAME))
			return new SpaceStationItem((SpaceStationData) data, hints);
		else if (dataClass.equals(HyperspaceSignatureData.CLASS_NAME))
			return new HyperspaceSignatureItem((HyperspaceSignatureData) data, hints);
		else if (dataClass.equals(WardData.CLASS_NAME))
			return new WardItem((WardData) data, hints);
		else if (dataClass.equals(EffectData.CLASS_NAME))
			return new EffectItem((EffectData) data, hints);
		else if (dataClass.equals(StructureData.CLASS_NAME))
			return new StructureItem((StructureData) data, hints);
		else if (dataClass.equals(FakeStructureData.CLASS_NAME))
			return new FakeStructureItem((FakeStructureData) data, hints);
		else if (dataClass.equals(FakeWardData.CLASS_NAME))
			return new FakeWardItem((FakeWardData) data, hints);
		else if (dataClass.equals(GravityWellData.CLASS_NAME))
			return new GravityWellItem((GravityWellData) data, hints);
		else if (dataClass.equals(LotteryData.CLASS_NAME))
			return new LotteryItem((LotteryData) data, hints);
		return null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	public native String getNativeHashCode(Object data, String dataClass) /*-{
		return dataClass + data.a;
	}-*/;
}
