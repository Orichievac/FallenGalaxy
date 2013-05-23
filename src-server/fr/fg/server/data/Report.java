/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

import org.apache.commons.lang.RandomStringUtils;

import fr.fg.server.data.base.ReportBase;
import fr.fg.server.i18n.Badwords;

public class Report extends ReportBase {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ArrayList<ReportSlot> reportSlots;
	
	private ArrayList<ReportAction> reportActions;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public Report() {
		// Nécessaire pour la construction par réflection
	}
	
	public Report(int idPlayerAttacking, int idPlayerDefending, long date,
			int hits, long lastView, String attackerEnvironment,
			String defenderEnvironment, double attackerDamageFactor,
			double defenderDamageFactor, int idArea) {
		setIdPlayerAttacking(idPlayerAttacking);
		setIdPlayerDefending(idPlayerDefending);
		setDate(date);
		setHits(hits);
		setLastView(lastView);
		setAttackerEnvironment(attackerEnvironment);
		setDefenderEnvironment(defenderEnvironment);
		setAttackerDamageFactor(attackerDamageFactor);
		setDefenderDamageFactor(defenderDamageFactor);
		setRetreat(false);
		setIdArea(idArea);
	}
	
	public Report(int idPlayerAttacking, int idPlayerDefending, long date,
			int hits, long lastView, String attackerEnvironment,
			String defenderEnvironment, double attackerDamageFactor,
			double defenderDamageFactor, int idArea, 
			int attackerXpGain, int defenderXpGain) {
		setIdPlayerAttacking(idPlayerAttacking);
		setIdPlayerDefending(idPlayerDefending);
		setDate(date);
		setHits(hits);
		setLastView(lastView);
		setAttackerEnvironment(attackerEnvironment);
		setDefenderEnvironment(defenderEnvironment);
		setAttackerDamageFactor(attackerDamageFactor);
		setDefenderDamageFactor(defenderDamageFactor);
		setRetreat(false);
		setIdArea(idArea);
		setAttackerXpGain(attackerXpGain);
		setDefenderXpGain(defenderXpGain);
	}
	
	// --------------------------------------------------------- METHODES -- //	
	
	public Area getArea() {
		return DataAccess.getAreaById(getIdArea());
	}
	
	public ArrayList<ReportSlot> getReportSlots() {
		return reportSlots;
	}

	public ArrayList<ReportAction> getReportActions() {
		return reportActions;
	}
	
	public void addReportSlot(ReportSlot reportSlot) {
		if (reportSlots == null)
			reportSlots = new ArrayList<ReportSlot>();
		reportSlots.add(reportSlot);
	}

	public void addReportAction(ReportAction reportAction) {
		if (reportActions == null)
			reportActions = new ArrayList<ReportAction>();
		reportActions.add(reportAction);
	}
	
	public void save() {
		// Génère un code de hash unique
		String hash;
		do {
			hash = RandomStringUtils.randomAlphanumeric(16);
		} while (DataAccess.getReportByHash(hash) != null ||
				Badwords.containsBadwords(hash));
		setHash(hash);
		
		super.save();
		
		for (ReportSlot reportSlot : reportSlots) {
			reportSlot.setIdReport(getId());
			reportSlot.save();
		}
		reportSlots = null;
		
		if (reportActions != null) {
			for (ReportAction reportAction : reportActions) {
				reportAction.setIdReport(getId());
				reportAction.save();
			}
		}
		reportActions = null;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
