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

package fr.fg.server.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import fr.fg.client.data.ReportActionAbilityData;
import fr.fg.client.data.ReportActionData;
import fr.fg.client.data.ReportDamageData;
import fr.fg.client.data.ReportData;
import fr.fg.client.data.ReportSlotData;
import fr.fg.client.data.ReportSlotStateData;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.GameConstants;
import fr.fg.server.data.Player;
import fr.fg.server.data.Report;
import fr.fg.server.data.ReportAction;
import fr.fg.server.data.ReportActionAbility;
import fr.fg.server.data.ReportDamage;
import fr.fg.server.data.ReportSlot;
import fr.fg.server.data.ReportSlotState;
import fr.fg.server.util.JSONStringer;

public class ReportTools {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public static JSONStringer getReport(JSONStringer json,
			Player player, Report report) throws Exception {
		
		if (json == null)
			json = new JSONStringer();
		
		json.object().
			key(ReportData.FIELD_DELUDE).					value(false).
			key(ReportData.FIELD_ID).						value(report.getId()).
			key(ReportData.FIELD_NEBULA).					value(report.getArea().getSector().getNebula()).
			key(ReportData.FIELD_ATTACKER_ENVIRONMENT).		value(report.getAttackerEnvironment()).
			key(ReportData.FIELD_DEFENDER_ENVIRONMENT).		value(report.getDefenderEnvironment()).
			key(ReportData.FIELD_ATTACKER_DAMAGE_FACTOR).	value(report.getAttackerDamageFactor()).
			key(ReportData.FIELD_DEFENDER_DAMAGE_FACTOR).	value(report.getDefenderDamageFactor()).
			key(ReportData.FIELD_RETREAT).					value(report.isRetreat()).
			key(ReportData.FIELD_ATTACKER_XP_FACTOR).		value(report.getAttackerXpGain()).
			key(ReportData.FIELD_DEFENDER_XP_FACTOR).		value(report.getDefenderXpGain());
		
		if (player != null && player.hasRight(Player.PREMIUM)) {
			json.key(ReportData.FIELD_GENERAL_VOLUME).		value(player.getSettingsGeneralVolume()).
				key(ReportData.FIELD_SOUND_VOLUME).			value(player.getSettingsSoundVolume()).
				key(ReportData.FIELD_GRAPHICS_QUALITY).		value(player.getSettingsGraphics());
		}
		
		// Slots au début du combat
		List<ReportSlot> reportSlots = new ArrayList<ReportSlot>(
				DataAccess.getReportSlotsByReportId(report.getId()));
		
		Collections.sort(reportSlots, new Comparator<ReportSlot>() {
			public int compare(ReportSlot r1, ReportSlot r2) {
				if (r1.getPosition() < r2.getPosition())
					return -1;
				return 1;
			}
		});
		
		for (ReportSlot reportSlot : reportSlots) {
			if (reportSlot.getPosition() == 0)
				json.key(ReportData.FIELD_FLEET_1).array();
			if (reportSlot.getPosition() == GameConstants.FLEET_SLOT_COUNT)
				json.key(ReportData.FIELD_FLEET_2).array();
			
			json.object().
				key(ReportSlotData.FIELD_ID).					value(reportSlot.getSlotId()).
				key(ReportSlotData.FIELD_COUNT).				value(reportSlot.getSlotCount()).
				key(ReportSlotData.FIELD_FRONT).				value(reportSlot.isSlotFront()).
				key(ReportSlotData.FIELD_AVAILABLE_ABILITIES).	value(reportSlot.getAvailableAbilities()).
				endObject();
			
			if (reportSlot.getPosition() == GameConstants.FLEET_SLOT_COUNT - 1)
				json.endArray();
			if (reportSlot.getPosition() == 2 * GameConstants.FLEET_SLOT_COUNT - 1)
				json.endArray();
		}
		
		// Actions entreprises durant le combat
		List<ReportAction> reportActions = new ArrayList<ReportAction>(
				DataAccess.getReportActionsByReportId(report.getId()));
		
		Collections.sort(reportActions, new Comparator<ReportAction>() {
			public int compare(ReportAction r1, ReportAction r2) {
				if (r1.getActionIndex() < r2.getActionIndex())
					return -1;
				else if (r1.getActionIndex() > r2.getActionIndex())
					return 1;
				else
					return r1.getSlotIndex() > r2.getSlotIndex() ? -1 : 1;
			}
		});
		
		json.key(ReportData.FIELD_ACTIONS).	array();
		
		for (ReportAction reportAction : reportActions) {
			json.object().
				key(ReportActionData.FIELD_ACTION_INDEX).		value(reportAction.getActionIndex()).
				key(ReportActionData.FIELD_SLOT_INDEX).			value(reportAction.getSlotIndex()).
				key(ReportActionData.FIELD_MODIFIERS).			value(reportAction.getModifiers()).
				key(ReportActionData.FIELD_FRONT_SLOTS).		value(reportAction.getFrontSlots()).
				key(ReportActionData.FIELD_ACTION_ABILITIES).	array();
			
			// Actions / capacités
			List<ReportActionAbility> reportActionAbilities = new ArrayList<ReportActionAbility>(
				DataAccess.getReportActionAbilitiesByAction(reportAction.getId()));
			
			Collections.sort(reportActionAbilities, new Comparator<ReportActionAbility>() {
				public int compare(ReportActionAbility r1,
						ReportActionAbility r2) {
					return r1.getPosition() < r2.getPosition() ? -1 : 1;
				}
			});
			
			for (ReportActionAbility reportActionAbility : reportActionAbilities) {
				json.object().
					key(ReportActionAbilityData.FIELD_SLOT_ID).		value(reportActionAbility.getSlotId()).
					key(ReportActionAbilityData.FIELD_ABILITY).		value(reportActionAbility.getAbility()).
					endObject();
			}
			
			// Etat des slots
			json.endArray().
				key(ReportActionData.FIELD_SLOT_STATES).	array();
			
			List<ReportSlotState> reportSlotStates = DataAccess.getReportSlotStatesByAction(reportAction.getId());
			
			for (ReportSlotState reportSlotState : reportSlotStates) {
				json.object().
					key(ReportSlotStateData.FIELD_SLOT_INDEX).			value(reportSlotState.getPosition()).
					key(ReportSlotStateData.FIELD_DAMAGE_MODIFIER).		value(reportSlotState.getDamageModifier()).
					key(ReportSlotStateData.FIELD_PROTECTION_MODIFIER).	value(reportSlotState.getProtectionModifier()).
					key(ReportSlotStateData.FIELD_HULL_MODIFIER).		value(reportSlotState.getHullModifier()).
					endObject();
			}
			
			// Dégâts
			json.endArray().
				key(ReportActionData.FIELD_DAMAGE).		array();
			
			List<ReportDamage> reportDamages = DataAccess.getReportDamagesByAction(reportAction.getId());
			
			for(ReportDamage reportDamage : reportDamages) {
				json.object().
					key(ReportDamageData.FIELD_TARGET_SLOT).		value(reportDamage.getTargetPosition()).
					key(ReportDamageData.FIELD_AMOUNT).				value(reportDamage.getDamage()).
					key(ReportDamageData.FIELD_KILLS).				value(reportDamage.getKills()).
					key(ReportDamageData.FIELD_HULL_DAMAGE).		value(reportDamage.getHullDamage()).
					key(ReportDamageData.FIELD_MODIFIERS).			value(reportDamage.getModifiers()).
					key(ReportDamageData.FIELD_STEALED_RESOURCES).	array();
				
				for (int j = 0; j < GameConstants.RESOURCES_COUNT; j++)
					json.value(reportDamage.getStealedResource(j));
				
				json.endArray().
					endObject();
			}
			
			json.endArray().
				endObject();
		}
		
		json.endArray().
			endObject();
		
		return json;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
