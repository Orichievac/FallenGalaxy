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

package fr.fg.server.data;

import java.util.ArrayList;

import fr.fg.server.data.base.ReportActionBase;

public class ReportAction extends ReportActionBase {
	// ------------------------------------------------------- CONSTANTES -- //

	public final static int
		INHIBITED			= 1 << 0;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ArrayList<ReportActionAbility> reportActionAbilities;
	
	private ArrayList<ReportDamage> reportDamages;
	
	private ArrayList<ReportSlotState> reportSlotStates;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ReportAction() {
		// Nécessaire pour la construction par réflection
	}
	
	public ReportAction(int actionIndex, int slotIndex) {
		setActionIndex(actionIndex);
		setSlotIndex(slotIndex);
		setModifiers(0);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void addModifier(int modifier) {
		setModifiers(getModifiers() | modifier);
	}
	
	public void addReportActionAbility(ReportActionAbility reportActionAbility) {
		if (reportActionAbilities == null)
			reportActionAbilities = new ArrayList<ReportActionAbility>();
		reportActionAbilities.add(reportActionAbility);
	}
	
	public ArrayList<ReportActionAbility> getReportActionAbilities() {
		return reportActionAbilities;
	}
	
	public ReportActionAbility getLastReportActionAbility() {
		return reportActionAbilities == null || reportActionAbilities.size() == 0 ?
				null : reportActionAbilities.get(reportActionAbilities.size() - 1);
	}
	
	public void addReportDamage(ReportDamage reportDamage) {
		if (reportDamages == null)
			reportDamages = new ArrayList<ReportDamage>();
		reportDamages.add(reportDamage);
	}
	
	public ArrayList<ReportDamage> getReportDamages() {
		return reportDamages;
	}
	
	public void addReportSlotState(ReportSlotState reportSlotState) {
		if (reportSlotStates == null)
			reportSlotStates = new ArrayList<ReportSlotState>();
		reportSlotStates.add(reportSlotState);
	}
	
	public void save() {
		super.save();
		
		if (reportActionAbilities != null) {
			for (ReportActionAbility reportActionAbility : reportActionAbilities) {
				reportActionAbility.setIdReportAction(getId());
				reportActionAbility.save();
			}
		}
		
		reportActionAbilities = null;
		
		if (reportDamages != null) {
			for (ReportDamage reportDamage : reportDamages) {
				reportDamage.setIdReportAction(getId());
				reportDamage.save();
			}
		}
		
		reportDamages = null;
		
		if (reportSlotStates != null) {
			for (ReportSlotState reportSlotState : reportSlotStates) {
				reportSlotState.setIdReportAction(getId());
				reportSlotState.save();
			}
		}
		
		reportSlotStates = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
