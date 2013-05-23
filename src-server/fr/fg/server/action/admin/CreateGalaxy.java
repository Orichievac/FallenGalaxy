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

package fr.fg.server.action.admin;

import java.util.Map;


import fr.fg.server.contract.NpcHelper;
import fr.fg.server.core.AiTools;
import fr.fg.server.core.GeneratorTools;
import fr.fg.server.dao.DataLayer;
import fr.fg.server.data.Ally;
import fr.fg.server.data.AllyInfluence;
import fr.fg.server.data.AllyNews;
import fr.fg.server.data.AllyTreaty;
import fr.fg.server.data.AllyVote;
import fr.fg.server.data.AllyVoter;
import fr.fg.server.data.Applicant;
import fr.fg.server.data.Area;
import fr.fg.server.data.Ban;
import fr.fg.server.data.BankAccount;
import fr.fg.server.data.Contact;
import fr.fg.server.data.ContractArea;
import fr.fg.server.data.ContractAttendee;
import fr.fg.server.data.ContractParameter;
import fr.fg.server.data.ContractParameterValue;
import fr.fg.server.data.ContractRelationship;
import fr.fg.server.data.ContractReward;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Election;
import fr.fg.server.data.ElectionVoter;
import fr.fg.server.data.Event;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.FleetLink;
import fr.fg.server.data.Marker;
import fr.fg.server.data.Message;
import fr.fg.server.data.MessageOfTheDay;
import fr.fg.server.data.Planet;
import fr.fg.server.data.Player;
import fr.fg.server.data.Report;
import fr.fg.server.data.ReportAction;
import fr.fg.server.data.ReportDamage;
import fr.fg.server.data.ReportSlot;
import fr.fg.server.data.ReportSlotState;
import fr.fg.server.data.Research;
import fr.fg.server.data.Sector;
import fr.fg.server.data.SpaceStation;
import fr.fg.server.data.StarSystem;
import fr.fg.server.data.StellarObject;
import fr.fg.server.data.StorehouseResources;
import fr.fg.server.data.Structure;
import fr.fg.server.data.StructureModule;
import fr.fg.server.data.StructureSkill;
import fr.fg.server.data.StructureSpaceshipYard;
import fr.fg.server.data.Tactic;
import fr.fg.server.data.Tradecenter;
import fr.fg.server.data.Treaty;
import fr.fg.server.data.VisitedArea;
import fr.fg.server.data.Ward;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Utilities;

public class CreateGalaxy extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected synchronized String execute(Player player,
			Map<String, Object> params, Session session) throws Exception {
		
		
		// Efface la galaxie actuelle
		LoggingSystem.getServerLogger().info("Erasing current galaxy...");
		
		DataLayer.getInstance().truncate(Ally.class);
		DataLayer.getInstance().truncate(AllyInfluence.class);
		DataLayer.getInstance().truncate(AllyNews.class);
		DataLayer.getInstance().truncate(AllyTreaty.class);
		DataLayer.getInstance().truncate(AllyVote.class);
		DataLayer.getInstance().truncate(AllyVoter.class);
		DataLayer.getInstance().truncate(Applicant.class);
		DataLayer.getInstance().truncate(Area.class);
		DataLayer.getInstance().truncate(BankAccount.class);
		//DataLayer.getInstance().truncate(Connection.class);
		DataLayer.getInstance().truncate(Contact.class);
		DataLayer.getInstance().truncate(Election.class);
		DataLayer.getInstance().truncate(ElectionVoter.class);
		DataLayer.getInstance().truncate(Event.class);
		DataLayer.getInstance().truncate(Fleet.class);
		DataLayer.getInstance().truncate(FleetLink.class);
		DataLayer.getInstance().truncate(Marker.class);
		DataLayer.getInstance().truncate(Message.class);
		DataLayer.getInstance().truncate(StellarObject.class);
		DataLayer.getInstance().truncate(Planet.class);
		DataLayer.getInstance().truncate(Player.class);
		DataLayer.getInstance().truncate(Report.class);
		DataLayer.getInstance().truncate(ReportAction.class);
		DataLayer.getInstance().truncate(ReportDamage.class);
		DataLayer.getInstance().truncate(ReportSlot.class);
		DataLayer.getInstance().truncate(ReportSlotState.class);
		DataLayer.getInstance().truncate(Research.class);
		DataLayer.getInstance().truncate(Sector.class);
		DataLayer.getInstance().truncate(SpaceStation.class);
		DataLayer.getInstance().truncate(StarSystem.class);
		DataLayer.getInstance().truncate(Tradecenter.class);
		DataLayer.getInstance().truncate(Treaty.class);
		DataLayer.getInstance().truncate(VisitedArea.class);
		DataLayer.getInstance().truncate(Ward.class);
		DataLayer.getInstance().truncate(Tactic.class);
		DataLayer.getInstance().truncate(StructureSpaceshipYard.class);
		DataLayer.getInstance().truncate(StructureSkill.class);
		DataLayer.getInstance().truncate(StructureModule.class);
		DataLayer.getInstance().truncate(Structure.class);
		DataLayer.getInstance().truncate(ContractArea.class);
		DataLayer.getInstance().truncate(ContractAttendee.class);
		DataLayer.getInstance().truncate(ContractParameter.class);
		DataLayer.getInstance().truncate(ContractParameterValue.class);
		DataLayer.getInstance().truncate(ContractRelationship.class);
		DataLayer.getInstance().truncate(ContractReward.class);
		//DataLayer.getInstance().truncate(ItemContainer.class);
		DataLayer.getInstance().truncate(MessageOfTheDay.class);
		DataLayer.getInstance().truncate(StorehouseResources.class);
		DataLayer.getInstance().truncate(Ban.class);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		LoggingSystem.getServerLogger().info("Current galaxy erased.");
		
		LoggingSystem.getServerLogger().info("Starting galaxy generation...");
		
		// Génère l'administrateur
		Player admin = new Player("admin", Utilities.encryptPassword(
				Config.getBypassPassword()), "contact@fallengalaxy.com", "", "");
		admin.setRights(Player.PREMIUM | Player.MODERATOR |
				Player.ADMINISTRATOR | Player.SUPER_ADMINISTRATOR);
		admin.setAi(true);
		admin.save();
		
		//Génère baha
		Player baha = new Player("baha", Utilities.encryptPassword(
				"Test25846tesT"), "tw_bahamut@hotmail.fr", "", "");
		baha.setRights(Player.PREMIUM | Player.MODERATOR |
				Player.ADMINISTRATOR | Player.SUPER_ADMINISTRATOR);
		baha.save();
		
		// Génère les joueurs AI s'ils n'existent pas
		AiTools.createPiratePlayers();
		NpcHelper.initializeFactions();
		
		// Office Diplomatique Galactique
		Player gdo = DataAccess.getPlayerByLogin(
				Messages.getString("mission.npc.gdo"));
		if (gdo == null) {
			gdo = new Player(
				Messages.getString("mission.npc.gdo"), "", "", "", "");
			gdo.setAi(true);
			gdo.save();
		}
		
		// Réseau Autonome
		Player network = DataAccess.getPlayerByLogin(
				Messages.getString("mission.npc.network"));
		if (network == null) {
			network = new Player(
				Messages.getString("mission.npc.network"), "", "", "", "");
			network.setAi(true);
			network.save();
		}
		Thread t = new Thread() {
		      public void run() {
				LoggingSystem.getServerLogger().info("Galaxy generation thread : start.");	  
				GeneratorTools.createGalaxy(30);
				LoggingSystem.getServerLogger().info("Galaxy generation thread : finish.");
		      }
		};
		t.start();
		
		if (Config.isDebug()) {
			Player jayjay = new Player("JayJay", Utilities.encryptPassword("jayjay"), "jeremiegottero@gmail.com", "", "");
			jayjay.setRights(Player.PREMIUM | Player.MODERATOR | Player.ADMINISTRATOR | Player.SUPER_ADMINISTRATOR);
			jayjay.setCredits(100000);
			jayjay.save();
		}
		
		LoggingSystem.getServerLogger().info("Galaxy generation completed.");
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
