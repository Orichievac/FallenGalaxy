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

package fr.fg.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface StaticMessages extends Messages {
	// ------------------------------------------------------- CONSTANTES -- //
	// --------------------------------------------------------- METHODES -- //
	
	public String ok();
	public String cancel();
	public String yes();
	public String no();
	public String close();
	public String error();
	public String experience();
	public String level(int level);
	public String colonizationPoints(String points);
	
	public final static StaticMessages INSTANCE = (StaticMessages) GWT.create(StaticMessages.class);

	@Key("day")
	public String day(int day);
	public String days(int days);

	@Key("base.galaxy")
	public String galaxy();

	@Key("base.login")
	public String login();

	@Key("base.login.tooltip")
	public String loginToolTip();

	@Key("base.password")
	public String password();

	@Key("base.password.tooltip")
	public String passwordToolTip();

	@Key("base.password.nomatch")
	public String passwordNoMatch();

	@Key("base.connect")
	public String connect();

	@Key("base.register")
	public String register();

	@Key("base.passwordForgotten")
	public String passwordForgotten();

	@Key("base.gameConnection")
	public String gameConnection();

	@Key("base.confirmPassword")
	public String confirmPassword();
	
	@Key("base.email")
	public String email();

	@Key("base.email.tooltip")
	public String emailToolTip();

	@Key("base.birthday")
	public String birthday();

	@Key("base.birthday.tooltip")
	public String birthdayToolTip();

	@Key("base.birthday.format")
	public String birthdayFormat();

	@Key("base.agreeTos")
	public String agreeTos(String tos);

	@Key("base.termsOfService")
	public String termsOfService();

	@Key("base.checkTermsOfService")
	public String checkTermsOfService();

	@Key("base.license")
	public String license();

	@Key("base.emailSent")
	public String emailSent();

	@Key("base.recoverPassword")
	public String recoverPassword();

	@Key("base.recoverPassword.help")
	public String recoverPasswordHelp();

	@Key("base.recoverEmailSent")
	public String recoverEmailSent();

	@Key("unit.k")
	public String unitK();

	@Key("unit.m")
	public String unitM();
	
	@Key("area.vacantSystem")
	public String vacantSystem();
	
	@Key("area.uncolonizableSystem")
	public String uncolonizableSystem();
	
	@Key("area.gate")
	public String gate();
	
	@Key("area.gate.desc")
	public String gateDesc();

	@Key("area.gravityWell")
	public String gravityWell();
	
	@Key("area.gravityWell.desc")
	public String gravityWellDesc();

	@Key("area.hyperspaceSignature")
	public String hyperspaceSignature();
	
	@Key("area.hyperspaceSignature.desc")
	public String hyperspaceSignatureDesc();
	
	@Key("area.blackhole")
	public String blackhole();
	
	@Key("area.blackhole.desc")
	public String blackholeDesc();
	
	@Key("area.wormhole")
	public String wormhole();
	
	@Key("area.wormhole.desc")
	public String wormholeDesc();

	@Key("area.openingWormhole")
	public String openingWormhole();
	
	@Key("area.openingWormhole.desc")
	public String openingWormholeDesc();
	
	@Key("area.pirateFleet")
	public String pirateFleet();

	@Key("area.asteroids")
	public String asteroids();

	@Key("area.asteroids.desc")
	public String asteroidsDesc(String percentage);

	@Key("area.asteroidsDense")
	public String asteroidsDense();

	@Key("area.asteroidsDense.desc")
	public String asteroidsDenseDesc(String percentage);
	
	@Key("area.asteroidsLow")
	public String asteroidsLow();
	
	@Key("area.asteroidsLow.desc")
	public String asteroidsLowDesc(String percentage, String resource);

	@Key("area.asteroidsAvg")
	public String asteroidsAvg();
	
	@Key("area.asteroidsAvg.desc")
	public String asteroidsAvgDesc(String percentage, String resource);

	@Key("area.asteroidsHigh")
	public String asteroidsHigh();
	
	@Key("area.asteroidsHigh.desc")
	public String asteroidsHighDesc(String percentage, String resource);

	@Key("area.asteroidsVein")
	public String asteroidsVein();
	
	@Key("area.asteroidsVein.desc")
	public String asteroidsVeinDesc(String percentage, String resource);

	@Key("area.asteroidsLowc")
	public String asteroidsLowc();
	
	@Key("area.asteroidsLowc.desc")
	public String asteroidsLowcDesc(String percentage, String resource);

	@Key("area.asteroidsMediumc")
	public String asteroidsMediumc();
	
	@Key("area.asteroidsMediumc.desc")
	public String asteroidsMediumcDesc(String percentage, String resource);

	@Key("area.asteroidsImportant")
	public String asteroidsImportant();
	
	@Key("area.asteroidsImportant.desc")
	public String asteroidsImportantDesc(String percentage, String resource);

	@Key("area.asteroidsAbondant")
	public String asteroidsAbondant();
	
	@Key("area.asteroidsAbondant.desc")
	public String asteroidsAbondantDesc(String percentage, String resource);

	@Key("area.asteroidsPure")
	public String asteroidsPure();
	
	@Key("area.asteroidsPure.desc")
	public String asteroidsPureDesc(String percentage, String resource);
	
	@Key("area.asteroidsConcentrate")
	public String asteroidsConcentrate();
	
	@Key("area.asteroidsConcentrate.desc")
	public String asteroidsConcentrateDesc(String percentage, String resource);

	@Key("area.bank")
	public String bank();

	@Key("area.bank.desc")
	public String bankDesc();
	
	@Key("area.lottery")
	public String lottery();
	
	@Key("area.lottery.desc")
	public String lotteryDesc();

	@Key("area.tradeCenter")
	public String tradeCenter();

	@Key("area.tradeCenter.desc")
	public String tradeCenterDesc();

	@Key("area.doodad.desc")
	public String doodadDesc();

	@Key("qualifier.several")
	public String qualifier1(String qualified);
	
	@Key("qualifier.dozen")
	public String qualifier2(String qualified);
	
	@Key("qualifier.hundreds")
	public String qualifier3(String qualified);
	
	@Key("qualifier.squadron")
	public String qualifier4(String qualified);
	
	@Key("qualifier.thousands")
	public String qualifier5(String qualified);
	
	@Key("qualifier.multitude")
	public String qualifier6(String qualified);
	
	@Key("qualifier.legion")
	public String qualifier7(String qualified);
	
	@Key("qualifier.armada")
	public String qualifier8(String qualified);
	
	@Key("qualifier.millions")
	public String qualifier9(String qualified);
	
	@Key("skill.offensiveLink")
	public String offensiveLink();
	
	@Key("skill.defensiveLink")
	public String defensiveLink();

	@Key("advancement.colonizationPoints")
	public String advancementColonizationPoints();

	@Key("advancement.buildings")
	public String advancementBuildings();

	@Key("advancement.tiles")
	public String advancementTiles();
	
	@Key("ability.frontLine")
	public String frontLine();
	
	@Key("ability.backLine")
	public String backLine();
	
	@Key("fleet.movement")
	public String movement(String movement);
	
	@Key("fleet.maxMovement")
	public String maxMovement(String time);
	
	@Key("fleet.enteringHyperspace.notime")
	public String enteringHyperspace();
	
	@Key("fleet.leavingHyperspace.notime")
	public String leavingHyperspace();
	
	@Key("fleet.systemCapture.notime")
	public String systemCapture();
	
	@Key("fleet.jumpReload")
	public String jumpReload(String time);

	@Key("fleet.enteringHyperspace")
	public String enteringHyperspace(String time);

	@Key("fleet.leavingHyperspace")
	public String leavingHyperspace(String time);

	@Key("fleet.level")
	public String fleetLevel(String level, String xp);

	@Key("fleet.power")
	public String fleetPower(String power);
	
	@Key("fleet.colonization")
	public String colonization(String time);

	@Key("fleet.systemCapture")
	public String systemCapture(String time);
	
	@Key("fleet.systemMigration")
	public String systemMigration(String time);
	
	@Key("fleet.skillLevel")
	public String skillLevel(int level);
	
	@Key("ladder.players")
	public String ladderPlayers();

	@Key("ladder.allies")
	public String ladderAllies();
	
	@Key("ladder.player")
	public String ladderPlayer();
	
	@Key("ladder.ally")
	public String ladderAlly();
	
	@Key("ladder.points")
	public String ladderPoints();
	
	@Key("ladder.allyOrganization")
	public String ladderAllyOrganization();
	
	@Key("ladder.allyMembers")
	public String ladderAllyMembers();

	@Key("ladder.noAlly")
	public String ladderNoAlly();

	@Key("ladder.level")
	public String ladderLevel();
	
	@Key("menu.options")
	public String options();
	
	@Key("menu.shortcut")
	public String shortcut(String key);
	
	@Key("menu.help")
	public String help();
	
	@Key("menu.forum")
	public String forum();
	
	@Key("menu.exit")
	public String exit();
	
	@Key("menu.confirmExit")
	public String confirmExit();
	
	@Key("menu.resume")
	public String resume();
	
	@Key("menu.ally")
	public String ally();
	
	@Key("menu.research")
	public String research();
	
	@Key("menu.diplomacy")
	public String diplomacy();
	
	@Key("menu.galaxyMap")
	public String galaxyMap();
	
	@Key("menu.messages")
	public String messages();
	
	@Key("menu.events")
	public String events();
	
	@Key("menu.eventsLog")
	public String eventsLog();
	
	@Key("menu.menu")
	public String menu();
	
	@Key("menu.ladder")
	public String ladder();

	@Key("messages.inbox")
	public String inbox();

	@Key("messages.sentbox")
	public String sentbox();

	@Key("messages.archives")
	public String archives();

	@Key("messages.write")
	public String write();

	@Key("messages.answer")
	public String answer();

	@Key("messages.delete")
	public String delete();

	@Key("messages.bookmark")
	public String bookmark();

	@Key("messages.title")
	public String title();

	@Key("messages.receiver")
	public String receiver();

	@Key("messages.send")
	public String send();

	@Key("messages.dateTimeFormat")
	public String dateTimeFormat();

	@Key("messages.dateFormat")
	public String dateFormat();

	@Key("messages.timeFormat")
	public String timeFormat();

	@Key("messages.msgSent")
	public String msgSent();

	@Key("messages.confirmDel")
	public String confirmDel();

	@Key("battle.previousRound")
	public String previousRound();
	
	@Key("battle.nextRound")
	public String nextRound();
	
	@Key("battle.replayRound")
	public String replayRound();
	
	@Key("battle.title")
	public String battleTitle();
	
	@Key("battle.start.label")
	public String battleStartLabel();
	
	@Key("battle.start.message")
	public String battleStartMessage();

	@Key("battle.round.label")
	public String battleRoundLabel(int round);
	
	@Key("battle.round.message")
	public String battleRoundMessage(int round);
	
	@Key("battle.end.label")
	public String battleEndLabel();
	
	@Key("battle.end.message")
	public String battleEndMessage();
	
	@Key("battle.retreat.message")
	public String battleRetreatMessage();

	@Key("battle.miss")
	public String miss();

	@Key("battle.criticalHit")
	public String criticalHitShot();

	@Key("battle.dodged")
	public String dodged();
	
	@Key("battle.phased")
	public String phased();

	@Key("ship.targets")
	public String shipTargets(String classes);
	
	@Key("product1.desc")
	public String product1Desc(String bonus);

	@Key("product2.desc")
	public String product2Desc(String bonus);

	@Key("product3.desc")
	public String product3Desc(String bonus);
	
	@Key("product4.desc")
	public String product4Desc(String bonus);
	
	@Key("product5.desc")
	public String product5Desc(String bonus);
	
	@Key("product6.desc")
	public String product6Desc(String bonus);
	
	@Key("galaxy.search")
	public String search();
	
	@Key("galaxy.areaMap")
	public String areaMap(String areaName);
	
	@Key("galaxy.unknownSector")
	public String unknownSector();
	
	@Key("galaxy.unknownArea")
	public String unknownArea();
	
	@Key("galaxy.coordinates")
	public String coordinates(String value);
	
	@Key("build.level")
	public String buildLevel(int level);
	
	@Key("research.allow")
	public String researchAllow();
	
	@Key("research.technology")
	public String technology();
	
	@Key("research.building")
	public String building();
	
	@Key("research.ability")
	public String ability();
	
	@Key("research.finished")
	public String researchFinished();
	
	@Key("research.length")
	public String researchLength(String length);
	
	@Key("swap.transfer")
	public String transfer();
	
	@Key("swap.transferLimit")
	public String transferLimit();
	
	@Key("swap.transferLimit.help")
	public String transferLimitHelp();
	
	@Key("swap.power")
	public String swapPower();
	
	@Key("swap.power.value")
	public String swapPower(String power);
	
	@Key("swap.power.help")
	public String swapPowerHelp();
	
	@Key("swap.lockPower.help")
	public String swapLockPowerHelp();
	
	@Key("swap.payload")
	public String payload();
	
	@Key("swap.payload.help")
	public String payloadHelp();
	
	public String premium();
	
	@Key("options.grid")
	public String optionGrid();
	
	@Key("options.proxy")
	public String optionProxy();
	
	@Key("options.proxy.help")
	public String optionProxyHelp();
	
	@Key("options.brightness")
	public String optionBrightness();
	
	@Key("options.chat")
	public String optionChat();
	
	@Key("options.censorship")
	public String optionCensorship();
	
	@Key("options.censorship.help")
	public String optionCensorshipHelp();
	
	@Key("options.fleetsSkin")
	public String optionFleetsSkin();
	
	@Key("options.fleetsName")
	public String optionFleetsName();
	
	@Key("options.fleetsName.help")
	public String optionFleetsNameHelp();
	
	@Key("options.password")
	public String optionPassword();
	
	@Key("options.change")
	public String optionChange();
	
	@Key("options.theme")
	public String optionTheme();
	
	@Key("options.theme.help")
	public String optionThemeHelp();
	
	@Key("options.generalVolume")
	public String optionGeneralVolume();

	@Key("options.musicVolume")
	public String optionMusicVolume();

	@Key("options.soundVolume")
	public String optionSoundVolume();

	@Key("options.graphicsQuality")
	public String optionGraphicsQuality();

	@Key("options.graphicsQuality.help")
	public String optionGraphicsQualityHelp();
	
	@Key("options.graphicsQuality.low")
	public String optionGraphicsQualityLow();

	@Key("options.graphicsQuality.average")
	public String optionGraphicsQualityAverage();

	@Key("options.graphicsQuality.high")
	public String optionGraphicsQualityHigh();

	@Key("options.graphicsQuality.max")
	public String optionGraphicsQualityMax();
	
	
	
}
