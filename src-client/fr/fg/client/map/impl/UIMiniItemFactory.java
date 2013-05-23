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
import fr.fg.client.data.BlackHoleData;
import fr.fg.client.data.ContractMarkerData;
import fr.fg.client.data.FleetData;
import fr.fg.client.data.GateData;
import fr.fg.client.data.GravityWellData;
import fr.fg.client.data.HyperspaceSignatureData;
import fr.fg.client.data.LotteryData;
import fr.fg.client.data.MarkerData;
import fr.fg.client.data.SpaceStationData;
import fr.fg.client.data.StarSystemData;
import fr.fg.client.data.StructureData;
import fr.fg.client.data.TradeCenterData;
import fr.fg.client.data.WardData;
import fr.fg.client.map.UIMiniItem;
import fr.fg.client.map.UIMiniItemRenderingHints;
import fr.fg.client.map.miniitem.AsteroidsMiniItem;
import fr.fg.client.map.miniitem.BankMiniItem;
import fr.fg.client.map.miniitem.BlackHoleMiniItem;
import fr.fg.client.map.miniitem.FleetMiniItem;
import fr.fg.client.map.miniitem.GateMiniItem;
import fr.fg.client.map.miniitem.GravityWellMiniItem;
import fr.fg.client.map.miniitem.HyperspaceSignatureMiniItem;
import fr.fg.client.map.miniitem.LotteryMiniItem;
import fr.fg.client.map.miniitem.MarkerMiniItem;
import fr.fg.client.map.miniitem.MissionMarkerMiniItem;
import fr.fg.client.map.miniitem.SpaceStationMiniItem;
import fr.fg.client.map.miniitem.StarSystemMiniItem;
import fr.fg.client.map.miniitem.StructureMiniItem;
import fr.fg.client.map.miniitem.TradeCenterMiniItem;
import fr.fg.client.map.miniitem.WardMiniItem;

public class UIMiniItemFactory {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private UIMiniItemRenderingHints hints;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public UIMiniItemFactory(UIMiniItemRenderingHints hints) {
		this.hints = hints;
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public UIMiniItemRenderingHints getRenderingHints() {
		return hints;
	}
	
	public void setRenderingHints(UIMiniItemRenderingHints hints) {
		this.hints = hints;
	}

	public String getHashCode(Object data, String dataClass) {
		if (data instanceof JavaScriptObject)
			return getNativeHashCode(data, dataClass);
		else
			return String.valueOf(dataClass.hashCode());
	}
	
	public UIMiniItem createMiniItem(Object data, String dataClass) {
		if (dataClass.equals(StarSystemData.CLASS_NAME))
			return new StarSystemMiniItem((StarSystemData) data, hints);
		else if (dataClass.equals(GateData.CLASS_NAME))
			return new GateMiniItem((GateData) data, hints);
		else if (dataClass.equals(BlackHoleData.CLASS_NAME))
			return new BlackHoleMiniItem((BlackHoleData) data, hints);
		else if (dataClass.equals(MarkerData.CLASS_NAME))
			return new MarkerMiniItem((MarkerData) data, hints);
		else if (dataClass.equals(FleetData.CLASS_NAME))
			return new FleetMiniItem((FleetData) data, hints);
		else if (dataClass.equals(BankData.CLASS_NAME))
			return new BankMiniItem((BankData) data, hints);
		else if (dataClass.equals(TradeCenterData.CLASS_NAME))
			return new TradeCenterMiniItem((TradeCenterData) data, hints);
		else if (dataClass.equals(SpaceStationData.CLASS_NAME))
			return new SpaceStationMiniItem((SpaceStationData) data, hints);
		else if (dataClass.equals(HyperspaceSignatureData.CLASS_NAME))
			return new HyperspaceSignatureMiniItem((HyperspaceSignatureData) data, hints);
		else if (dataClass.equals(ContractMarkerData.CLASS_NAME))
			return new MissionMarkerMiniItem((ContractMarkerData) data, hints);
		else if (dataClass.equals(WardData.CLASS_NAME))
			return new WardMiniItem((WardData) data, hints);
		else if (dataClass.equals(AsteroidsData.CLASS_NAME))
			return new AsteroidsMiniItem((AsteroidsData) data, hints);
		else if (dataClass.equals(StructureData.CLASS_NAME))
			return new StructureMiniItem((StructureData) data, hints);
		else if (dataClass.equals(GravityWellData.CLASS_NAME))
			return new GravityWellMiniItem((GravityWellData) data, hints);
		else if (dataClass.equals(LotteryData.CLASS_NAME))
			return new LotteryMiniItem((LotteryData) data, hints);
		
		return null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	private native String getNativeHashCode(Object data, String dataClass) /*-{
		return dataClass + data.a;
	}-*/;
}
