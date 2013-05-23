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

package fr.fg.client.data;

import com.google.gwt.core.client.GWT;

import fr.fg.client.i18n.DynamicMessages;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.core.Config;

public class AbilityData {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		TYPE_CRITICAL_HIT			= 1,
		TYPE_PASSIVE_LEECH			= 2,
		TYPE_FORCE_FIELD			= 4,
		TYPE_REPAIR					= 5,
		TYPE_DODGE					= 6,
		TYPE_FRAG_CHARGE			= 7,
		TYPE_FOCUSED_FIRE			= 8,
		TYPE_ACTIVE_LEECH			= 9,
		TYPE_BARRAGE				= 10,
		TYPE_SACRIFICE				= 11,
		TYPE_RETRIBUTION			= 12,
		TYPE_HARASSMENT				= 13,
		TYPE_RAPID_FIRE				= 14,
		TYPE_TAUNT					= 15,
		TYPE_DAMAGE_RETURN_AURA		= 16,
		TYPE_HEAT					= 17,
		TYPE_TIME_SHIFT				= 18,
		TYPE_DEVASTATE				= 19,
		TYPE_RESISTANCE				= 20,
		TYPE_BREACH					= 21,
		TYPE_PHASE					= 22,
		TYPE_RIFT					= 23,
		TYPE_DEFLECTOR				= 24,
		TYPE_INHIBITOR_FIELD		= 25,
		TYPE_RAGE					= 26,
		TYPE_OUTFLANKING			= 27,
		TYPE_UNBREAKABLE			= 28,
		TYPE_SUBLIMATION			= 29,
		TYPE_FUSION					= 30,
		TYPE_SQUADRON				= 31,
		TYPE_ECM					= 32,
		TYPE_OVERLOAD				= 33,
		TYPE_QUANTUM_ERROR			= 34,
		TYPE_MIMICRY				= 35,
		TYPE_CURSE					= 36,
		TYPE_IMMATERIAL				= 37,
		TYPE_CONFUSION				= 38,
		TYPE_LOTTERY				= 39,
		TYPE_INCOHESION				= 40,
		TYPE_HULL_ENERGY_TRANSFER	= 41,
		TYPE_DAMAGE_ENERGY_TRANSFER	= 42,
		TYPE_PARTICLE_PROJECTION	= 43,
		TYPE_DETONATION				= 44,
		TYPE_TERMINATOR				= 45,
		TYPE_PILLAGE				= 46,
		TYPE_EVADE					= 47,
		TYPE_COLLATERAL				= 48,
		TYPE_OVERHEAT				= 49,
		TYPE_CRITICALV2 			= 50,
		TYPE_PROXIMITY_CHARGE		= 51,
		TYPE_RAPID_FIRE_V2			= 52,
		TYPE_RECUP					= 53,
		TYPE_ALL_RESISTANCE			= 54,
		TYPE_SUBLIMATION_V2			= 55,
		TYPE_ABSORBTION				= 56,
		TYPE_CANALISE				= 57,
		TYPE_ANTICIPATE				= 58,
		TYPE_DAMAGE_RETURN_PASSIF	= 59,
		TYPE_DODGE_V2				= 60,
		TYPE_INCOHERENCE			= 61,
		TYPE_DAMAGE_REPARTITION		= 62,
		TYPE_BARRAGE_V2				= 63,
		TYPE_WEAKNESS				= 64,
		TYPE_REPERCUTE				= 65,
		TYPE_ARTILLERY_SHOT			= 66,
		TYPE_FIERCENESS             = 67;
	
	
	public final static String
		CATEGORY_BUFF			= "buff",
		CATEGORY_DEBUFF			= "debuff",
		CATEGORY_SPECIAL		= "special";
	
	public final static int[] GRAPHICS;
	
	static {
		GRAPHICS = new int[100];
		GRAPHICS[TYPE_FOCUSED_FIRE] = 9;
		GRAPHICS[TYPE_ACTIVE_LEECH] = 3;
		GRAPHICS[TYPE_BARRAGE] = 2;
		GRAPHICS[TYPE_FRAG_CHARGE] = 4;
		GRAPHICS[TYPE_SACRIFICE] = 7;
		GRAPHICS[TYPE_RETRIBUTION] = 5;
		GRAPHICS[TYPE_FORCE_FIELD] = 6;
		GRAPHICS[TYPE_REPAIR] = 8;
		GRAPHICS[TYPE_RAPID_FIRE] = 1;
		GRAPHICS[TYPE_TAUNT] = 10;
		GRAPHICS[TYPE_DAMAGE_RETURN_AURA] = 11;
		GRAPHICS[TYPE_TIME_SHIFT] = 12;
		GRAPHICS[TYPE_DEVASTATE] = 13;
		GRAPHICS[TYPE_RIFT] = 14;
		GRAPHICS[TYPE_DEFLECTOR] = 16;
		GRAPHICS[TYPE_INHIBITOR_FIELD] = 15;
		GRAPHICS[TYPE_RAGE] = 17;
		GRAPHICS[TYPE_FUSION] = 18;
		GRAPHICS[TYPE_MIMICRY] = 19;
		GRAPHICS[TYPE_CONFUSION] = 20;
		GRAPHICS[TYPE_LOTTERY] = 21;
		GRAPHICS[TYPE_HULL_ENERGY_TRANSFER] = 22;
		GRAPHICS[TYPE_DAMAGE_ENERGY_TRANSFER] = 22;
		GRAPHICS[TYPE_RESISTANCE] = 23;
		GRAPHICS[TYPE_PARTICLE_PROJECTION] = 24;
		GRAPHICS[TYPE_OVERHEAT] = 13;
		GRAPHICS[TYPE_PROXIMITY_CHARGE] = 4;
		GRAPHICS[TYPE_RAPID_FIRE_V2] = 1;
		GRAPHICS[TYPE_RECUP] = 26;
		GRAPHICS[TYPE_ALL_RESISTANCE] = 23;
		GRAPHICS[TYPE_DAMAGE_REPARTITION] = 27;
		GRAPHICS[TYPE_BARRAGE_V2] = 2;
		GRAPHICS[TYPE_REPERCUTE] = 25;
		GRAPHICS[TYPE_ARTILLERY_SHOT] = 28;
		GRAPHICS[TYPE_FIERCENESS] = 29;
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private int[] requirements;
	
	private int cooldown;
	
	private boolean passive;
	
	private String category;
	
	private String[] args;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private AbilityData(int type, int cooldown, boolean passive,
			String category, int[] requirements, String... args) {
		this.type = type;
		this.cooldown = cooldown;
		this.passive = passive;
		this.category = category;
		this.requirements = requirements;
		this.args = args;
	}
	
	// --------------------------------------------------------- METHODES -- //

	public int getType() {
		return type;
	}
	
	public String[] getArgs() {
		return args;
	}

	public String getCategory() {
		return category;
	}
	
	public int[] getRequirements() {
		return requirements;
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public boolean isPassive() {
		return passive;
	}
	
	public String getName() {
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		return dynamicMessages.getString("ability" + getType());
	}
	
	public String getDesc(int shipId) {
		StaticMessages messages =
			(StaticMessages) GWT.create(StaticMessages.class);
		DynamicMessages dynamicMessages =
			(DynamicMessages) GWT.create(DynamicMessages.class);
		
		String desc = dynamicMessages.getString("ability" + getType() + "Desc");
		
		desc = desc.replace("%hull%", "<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" unselectable=\"on\" class=\"stat s-struct\"/>");
		desc = desc.replace("%protection%", "<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" unselectable=\"on\" class=\"stat s-shield\"/>");
		desc = desc.replace("%damage%", "<img src=\"" + Config.getMediaUrl() +
			"images/misc/blank.gif\" unselectable=\"on\" class=\"stat s-damage\"/>");
		desc = desc.replace("%shipName%", dynamicMessages.getString("ships" + shipId));
		
		for (int i = 0; i < 4; i++)
			desc = desc.replace("%resource" + i + "%", "<img src=\"" + Config.getMediaUrl() +
				"images/misc/blank.gif\" unselectable=\"on\" class=\"resource r" + i + "\"/>");
		
		for (int i = 0; i < args.length; i++) {
			if (desc.contains("%" + i + "%"))
				desc = desc.replace("%" + i + "%",
					"<span unselectable=\"on\" class=\"emphasize\">" + args[i] + "</span>");
			if (desc.contains("%" + i + ".line%"))
				desc = desc.replace("%" + i + ".line%",
					"<span unselectable=\"on\" class=\"emphasize\">" + (Boolean.parseBoolean(
					args[i]) ? messages.frontLine() : messages.backLine()) + "</span>");
			if (desc.contains("%" + i + ".percent%"))
				desc = desc.replace("%" + i + ".percent%",
					"<span unselectable=\"on\" class=\"emphasize\">" + //$NON-NLS-1$
					(int) Math.round(100 * Double.parseDouble(args[i])) + "%</span>");
			if (desc.contains("%" + i + ".percent>100%"))
				desc = desc.replace("%" + i + ".percent>100%",
					"<span unselectable=\"on\" class=\"emphasize\">" + //$NON-NLS-1$
					(int) Math.round(100 * (Double.parseDouble(args[i]) - 1)) + "%</span>");
			if (desc.contains("%" + i + ".percent<100%"))
				desc = desc.replace("%" + i + ".percent<100%",
					"<span unselectable=\"on\" class=\"emphasize\">" + //$NON-NLS-1$
					(int) Math.round(100 * (1 - Double.parseDouble(args[i]))) + "%</span>");
			if (desc.contains("%" + i + ".signed%"))
				desc = desc.replace("%" + i + ".signed%",
					"<span unselectable=\"on\" class=\"emphasize\">" +
					(Double.parseDouble(args[i]) >= 0 ? "+" : "") + args[i] + "</span>");
			if (desc.contains("%" + i + ".shipClass%"))
				desc = desc.replace("%" + i + ".shipClass%",
					"<span unselectable=\"on\" class=\"emphasize\">" +
					dynamicMessages.getString("shipClasses" + args[i]) + "</span>");
		}
		
		return desc;
	}
	
	// TYPE_CRITICAL_HIT
	
	public double getCriticalHitChances() {
		return Double.parseDouble(args[0]);
	}
	
	public double getCriticalHitMultiplier() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_PASSIVE_LEECH
	
	public double getPassiveLeechValue() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_FORCE_FIELD
	
	public int getForceFieldLowProtectionModifier() {
		return Integer.parseInt(args[0]);
	}
	
	public int getForceFieldHighProtectionModifier() {
		return Integer.parseInt(args[1]);
	}
	
	// TYPE_REPAIR
	
	public double getRepairValue() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_DODGE
	
	public double getDodgeChances() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_FRAG_CHARGE
	
	public double getFragChargeBonus() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_FOCUSED_FIRE
	
	public double getFocusFireBonus() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_ACTIVE_LEECH

	public double getActiveLeechValue() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_BARRAGE

	public double getBarrageFrontMalus() {
		return Double.parseDouble(args[0]);
	}

	public double getBarrageBackMalus() {
		return Double.parseDouble(args[1]);
	}

	public int getBarrageClassTarget() {
		return Integer.parseInt(args[2]);
	}

	// TYPE_RETRIBUTION

	public int getRetributionLength() {
		return Integer.parseInt(args[0]);
	}

	public double getRetributionMaxReturnedDamagePercentage() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_SACRIFICE

	public int getSacrificeLowProtectionModifier() {
		return Integer.parseInt(args[0]);
	}

	public int getSacrificeHighProtectionModifier() {
		return Integer.parseInt(args[1]);
	}

	public int getSacrificeSelfProtectionModifier() {
		return Integer.parseInt(args[2]);
	}

	// TYPE_HARASSMENT
	
	public double getHarassmentDamageModifier() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_RAPID_FIRE
	
	public double getRapidFireDamageModifier() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_TAUNT
	
	public int getTauntLength() {
		return Integer.parseInt(args[0]);
	}
	
	public int getTauntProtectionModifier() {
		return Integer.parseInt(args[1]);
	}
	
	// TYPE_DAMAGE_RETURN_AURA
	
	public double getDamageReturnAuraValue() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_HEAT
	
	public int getHeatProtectionModifier() {
		return Integer.parseInt(args[0]);
	}

	// TYPE_DEVASTATE
	
	public double getDevastateExtraDamage() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_RESISTANCE
	
	public int getResistanceClassTarget() {
		return Integer.parseInt(args[0]);
	}
	
	public double getResistanceDamageModifier() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_BREACH
	
	public double getBreachExtraDamage() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_DEFLECTOR
	
	public int getDeflectorClassesTarget() {
		return Integer.parseInt(args[0]);
	}
	
	public int getDeflectorProtectionModifier() {
		return Integer.parseInt(args[1]);
	}
	
	// TYPE_RAGE
	
	public int getRageProtectionModifier() {
		return Integer.parseInt(args[0]);
	}
	
	// TYPE_OUTFLANKING
	
	public double getOutflankingLeechedValue() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_TIME_SHIFT
	
	public int getTimeShiftActionsCancelled() {
		return Integer.parseInt(args[0]);
	}

	// TYPE_SUBLIMATION
	
	public double getSublimationChances() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_FUSION
	
	public int getFusionLength() {
		return Integer.parseInt(args[0]);
	}
	
	// TYPE_SQUADRON
	
	public int getSquadronTargetClasses() {
		return Integer.parseInt(args[0]);
	}
	
	public double getSquadronExtraDamage() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_ECM

	public double getEcmMaxLosses() {
		return Double.parseDouble(args[0]);
	}
	
	//TODO
	
	// TYPE_EVADE

	public double getEvadePercent() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_COLLATERAL

	public int getCollateralProtectionModifier() {
		return Integer.parseInt(args[0]);
	}
	
	
	// TYPE_OVERHEAT
	
	public double getOverHeatDamageModifier() {
		return Double.parseDouble(args[0]);
	}
	
	public double getOverHeatHullModifier() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_CRITICALV2
	
	public double getCriticalV2Chance1() {
		return Double.parseDouble(args[0]);
	}
	
	public double getCriticalV2Chance2() {
		return Double.parseDouble(args[1]);
	}
	
	public double getCriticalV2Multiplier1() {
		return Double.parseDouble(args[2]);
	}
	
	public double getCriticalV2Multiplier2() {
		return Double.parseDouble(args[3]);
	}
	
	// TYPE_PROXIMITY_CHARGE
	public double getProximityChargeValue() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_RAPID_FIRE_V2
	public double getRapidFireV2DamageModifier() {
		return Double.parseDouble(args[0]);
	}
	
	public boolean getRapidFireV2AllLines() {
		return Boolean.parseBoolean(args[1]);
	}
	
	//TYPE_RECUP

	public double getRecupValue() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_ALL_RESISTANCE
	
	public double getAllResistanceValue() {
		return Double.parseDouble(args[0]);
	}
	
	public int getAllResistanceLength() {
		return Integer.parseInt(args[1]);
	}
	
	// TYPE_SUBLIMATION_V2
	
	public double getSublimationV2Chances1() {
		return Double.parseDouble(args[0]);
	}
	
	public double getSublimationV2Chances2() {
		return Double.parseDouble(args[1]);
	}
	
	public double getSublimationV2Value1() {
		return Double.parseDouble(args[2]);
	}
	
	public double getSublimationV2Value2() {
		return Double.parseDouble(args[3]);
	}
	
	
	// TYPE_ABSORBTION
	
	public double getAbsorbtionHullModifier() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_CANALISE
	
	public int getCalaniseProtectionBonus(){
		return Integer.parseInt(args[0]);
	}
	
	public double getCalanisePercentKill() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_ANTICIPATE
	
	public int getAnticipationClasses(){
		return Integer.parseInt(args[0]);
	}
	
	public double getAnticipationChances() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_DAMAGE_RETURN_PASSIF
	
	public double getDamageRetunPassifValue() {
		return Double.parseDouble(args[0]);
	}
	
	
	// TYPE_DODGE_V2
	
	public double getDodgeV2Lower(){
		return Double.parseDouble(args[0]);
	}
	
	public double getDodgeV2Upper(){
		return Double.parseDouble(args[1]);
	}
	
	
	// TYPE_INCOHERENCE
	
	public double getIncoherenceChance(){
		return Double.parseDouble(args[0]);
	}
	
	public int getIncoherenceProtectionModifier(){
		return Integer.parseInt(args[1]);
	}
	
	
	// TYPE_DAMAGE_REPARTITION
	
	public double getDamageRepartionDamages(){
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_BARRAGE_V2

	public double getBarrageV2FrontMalus() {
		return Double.parseDouble(args[0]);
	}

	public double getBarrageV2BackMalus() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_WEAKNESS
	
	public double getWeaknessDamageModifier() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_REPERCUTE
	
	public double getRepercuteDamagePercent() {
		return Double.parseDouble(args[0]);
	}
	
	//FIN TODO
	
	// TYPE_ENERGETIC_VARIATION
	
	public int getOverloadDivisor() {
		return Integer.parseInt(args[0]);
	}
	
	public int getOverloadProtectionModifier() {
		return Integer.parseInt(args[1]);
	}
	
	// TYPE_QUANTUM_INSTABILITY
	
	public int getQuantumErrorDivisor() {
		return Integer.parseInt(args[0]);
	}
	
	public double getQuantumErrorDamageModifier() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_CURSE
	
	public int getCurseProtectionModifier() {
		return Integer.parseInt(args[0]);
	}
	
	// TYPE_IMMATERIAL
	
	public double getImmaterialShotDamageModifier() {
		return Double.parseDouble(args[0]);
	}
	
	public double getImmaterialAbilityDamageModifier() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_CONFUSION
	
	public boolean isConfusionFrontLine() {
		return Boolean.parseBoolean(args[0]);
	}
	
	// TYPE_INCOHESION
	
	public int getIncohesionEvenRoundProtectionModifier() {
		return Integer.parseInt(args[0]);
	}
	
	public double getIncohesionEvenRoundDamageModifier() {
		return Double.parseDouble(args[1]);
	}
	
	public int getIncohesionOddRoundProtectionModifier() {
		return Integer.parseInt(args[2]);
	}
	
	public double getIncohesionOddRoundDamageModifier() {
		return Double.parseDouble(args[3]);
	}
	
	// TYPE_HULL_ENERGY_TRANSFER
	
	public double getHullEnergyTransferHullModifier() {
		return Double.parseDouble(args[0]);
	}
	
	public double getHullEnergyTransferDamageModifier() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_DAMAGE_ENERGY_TRANSFER
	
	public double getDamageEnergyTransferHullModifier() {
		return Double.parseDouble(args[0]);
	}
	
	public double getDamageEnergyTransferDamageModifier() {
		return Double.parseDouble(args[1]);
	}
	
	// TYPE_PARTICLE_PROJECTION
	
	public int getParticleProjectionDamage() {
		return Integer.parseInt(args[0]);
	}
	
	public int getParticleProjectionAntimatterCost() {
		return Integer.parseInt(args[1]);
	}
	
	// TYPE_DETONATION
	
	public double getDetonationHullModifier() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_TERMINATOR
	
	public double getTerminatorDamageModifier() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_PILLAGE
	
	public double getPillageStealCoef() {
		return Double.parseDouble(args[0]);
	}

	// TYPE_ARTILLERY_SHOT
	
	public double getArtilleryShotDamageMultiplier() {
		return Double.parseDouble(args[0]);
	}
	
	// TYPE_FIERCENESS
    
		public int getFiercenessClassTarget() {
			return Integer.parseInt(args[0]);
		}
		
			public double getFiercenessBonus() {
				return Double.parseDouble(args[1]);
			}

	
	// Constructeurs static
	
	public static AbilityData getCriticalHitAbility(int[] requirements, double chances, double multiplier) {
		return new AbilityData(TYPE_CRITICAL_HIT, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(chances), String.valueOf(multiplier));
	}
	
	public static AbilityData getPassiveLeechAbility(int[] requirements, double value) {
		return new AbilityData(TYPE_PASSIVE_LEECH, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static AbilityData getForceFieldAbility(int cooldown, int[] requirements, int lowProtectionModifier, int highProtectionModifier) {
		return new AbilityData(TYPE_FORCE_FIELD, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(lowProtectionModifier), String.valueOf(highProtectionModifier));
	}
	
	public static AbilityData getRepairAbility(int cooldown, int[] requirements, double value) {
		return new AbilityData(TYPE_REPAIR, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}

	public static AbilityData getDodgeAbility(int[] requirements, double chances) {
		return new AbilityData(TYPE_DODGE, 0, true, CATEGORY_BUFF, requirements, String.valueOf(chances));
	}

	public static AbilityData getFragChargeAbility(int cooldown, int[] requirements, double bonus) {
		return new AbilityData(TYPE_FRAG_CHARGE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(bonus));
	}

	public static AbilityData getFocusFireAbility(int cooldown, int[] requirements, double bonus) {
		return new AbilityData(TYPE_FOCUSED_FIRE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(bonus));
	}

	public static AbilityData getActiveLeechAbility(int cooldown, int[] requirements, double value) {
		return new AbilityData(TYPE_ACTIVE_LEECH, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}

	public static AbilityData getBarrageAbility(int cooldown, int[] requirements, double frontMalus, double backMalus, int classes) {
		return new AbilityData(TYPE_BARRAGE, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(frontMalus), String.valueOf(backMalus), String.valueOf(classes));
	}
	
	public static AbilityData getSacrificeAbility(int cooldown, int[] requirements, int lowModifier, int highModifier, int selfModifier) {
		return new AbilityData(TYPE_SACRIFICE, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(lowModifier), String.valueOf(highModifier), String.valueOf(selfModifier));
	}
	
	public static AbilityData getRetributionAbility(int cooldown, int[] requirements, int length, double maxReturnedDamagePercentage) {
		return new AbilityData(TYPE_RETRIBUTION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(length), String.valueOf(maxReturnedDamagePercentage));
	}
	
	public static AbilityData getHarassmentAbility(int[] requirements, double value) {
		return new AbilityData(TYPE_HARASSMENT, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(value));
	}
	
	public static AbilityData getRapidFireAbility(int cooldown, int[] requirements, double value) {
		return new AbilityData(TYPE_RAPID_FIRE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static AbilityData getTauntAbility(int cooldown, int[] requirements, int length, int protectionModifier) {
		return new AbilityData(TYPE_TAUNT, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(length), String.valueOf(protectionModifier));
	}
	
	public static AbilityData getDamageReturnAuraAbility(int cooldown, int[] requirements, double value) {
		return new AbilityData(TYPE_DAMAGE_RETURN_AURA, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(value));
	}
	
	public static AbilityData getHeatAbility(int[] requirements, int modifier) {
		return new AbilityData(TYPE_HEAT, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(modifier));
	}
	
	public static AbilityData getTimeShiftAbility(int cooldown, int[] requirements, int actions) {
		return new AbilityData(TYPE_TIME_SHIFT, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(actions));
	}

	public static AbilityData getDevastateAbility(int cooldown, int[] requirements, double value) {
		return new AbilityData(TYPE_DEVASTATE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}

	public static AbilityData getResistanceAbility(int cooldown, int[] requirements, int classes, double value) {
		return new AbilityData(TYPE_RESISTANCE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(value));
	}
	
	public static AbilityData getBreachAbility(int[] requirements, double value) {
		return new AbilityData(TYPE_BREACH, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(value));
	}
	
	public static AbilityData getPhaseAbility(int[] requirements) {
		return new AbilityData(TYPE_PHASE, 0, true, CATEGORY_SPECIAL, requirements);
	}
	
	public static AbilityData getRiftAbility(int cooldown, int[] requirements) {
		return new AbilityData(TYPE_RIFT, cooldown, false, CATEGORY_SPECIAL, requirements);
	}
	
	public static AbilityData getDeflectorAbility(int cooldown, int[] requirements, int classes, int value) {
		return new AbilityData(TYPE_DEFLECTOR, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(value));
	}
	
	public static AbilityData getInhibitorFieldAbility(int cooldown, int[] requirements) {
		return new AbilityData(TYPE_INHIBITOR_FIELD, cooldown, false, CATEGORY_SPECIAL, requirements);
	}
	
	public static AbilityData getRageAbility(int cooldown, int[] requirements, int protectionModifier) {
		return new AbilityData(TYPE_RAGE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(protectionModifier));
	}
	
	public static AbilityData getOutflankingAbility(int[] requirements, double value) {
		return new AbilityData(TYPE_OUTFLANKING, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static AbilityData getUnbreakableAbility(int[] requirements) {
		return new AbilityData(TYPE_UNBREAKABLE, 0, true, CATEGORY_BUFF, requirements);
	}

	public static AbilityData getSublimationAbility(int[] requirements, double chances) {
		return new AbilityData(TYPE_SUBLIMATION, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(chances));
	}

	public static AbilityData getFusionAbility(int cooldown, int[] requirements, int length) {
		return new AbilityData(TYPE_FUSION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(length));
	}

	public static AbilityData getSquadronAbility(int[] requirements, int classes, double extraDamage) {
		return new AbilityData(TYPE_SQUADRON, 0, true, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(extraDamage));
	}
	
	public static AbilityData getEcmAbility(int[] requirements, double maxLosses) {
		return new AbilityData(TYPE_ECM, 0, true, CATEGORY_BUFF, requirements, String.valueOf(maxLosses));
	}
	
	//TODO
	// Permet d'esquiver un tir ennemie selon le nombre de chasseur
	public static AbilityData getEvadeAbility(int[] requirements, double percent) {
		return new AbilityData(TYPE_EVADE, 0 , true, CATEGORY_BUFF, requirements, String.valueOf(percent));
	}
	
	public static AbilityData getCollateralAbility(int[] requirements, int protectionModifier) {
		return new AbilityData(TYPE_COLLATERAL, 0 , true, CATEGORY_SPECIAL, requirements, String.valueOf(protectionModifier));
	}

	// Surchauffe - dégats +X %, diminue les PV
	public static AbilityData getOverheatingAbility(int cooldown, int[] requirements, double damageModifier, double hullModifier) {
		return new AbilityData(TYPE_OVERHEAT, cooldown , false, CATEGORY_SPECIAL, requirements, String.valueOf(damageModifier), String.valueOf(hullModifier));
	}
	
	// Critical strike avec plusieur %
	public static AbilityData getCriticalHitV2Ability(int[] requirements, double chance1, double chance2, double multiplier1, double multiplier2) {
		return new AbilityData(TYPE_CRITICALV2, 0 , true, CATEGORY_SPECIAL, requirements, String.valueOf(chance1), String.valueOf(chance2), String.valueOf(multiplier1), String.valueOf(multiplier2));
	}

	// charge de proximité - renvoie de dégâts
	public static AbilityData getProximityChargeAbility(int cooldown, int[] requirements, double value) {
		return new AbilityData(TYPE_PROXIMITY_CHARGE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(value));
	}
	
	// Tir en rafale V2- execute un tir sur la ligne de front ennemie (et la ligne arrière)
	public static AbilityData getRapidFireV2Ability(int cooldown, int[] requirements, double damageModifier, boolean allLines) {
		return new AbilityData(TYPE_RAPID_FIRE_V2, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(damageModifier), String.valueOf(allLines));
	}
	
	// Récupération  - répare tout les vaisseaux excepté les valkyries
	public static AbilityData getRecupAbility(int cooldown, int[] requirements, double value) {
		return new AbilityData(TYPE_RECUP, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	// Résistance globale - réduit les dégâts subis
	// contre toutes les classes pendant X tours
	public static AbilityData getAllResistanceAbility(int cooldown, int[] requirements, double value, int length) {
		return new AbilityData(TYPE_ALL_RESISTANCE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(value),String.valueOf(length));
	}
	
	// Capacité - sublimation V2, 2 type de sublimation possible
	public static AbilityData getSublimationV2Ability(int[] requirements, double chances1, double chances2, double value1, double value2) {
		return new AbilityData(TYPE_SUBLIMATION_V2, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(chances1),
				String.valueOf(chances2),String.valueOf(value1),String.valueOf(value2));
	}
	
	// Capacité - Absorbtion : augmente les PV lors des dégats reçus
	public static AbilityData getAbsorbtionAbility(int[] requirements, double hullModifier) {
		return new AbilityData(TYPE_ABSORBTION, 0, true, CATEGORY_BUFF, requirements, String.valueOf(hullModifier));
	}
	
	// Capacité - Canalisation : si le vaisseau subit trop de gétas, il gagne du bouclier
	public static AbilityData getCanaliseAbility(int[] requirements, int protectionBonus, double percentKill) {
		return new AbilityData(TYPE_CANALISE, 0, true, CATEGORY_BUFF, requirements, String.valueOf(protectionBonus), String.valueOf(percentKill));
	}
	
	// Capacité - Anticipation : esquive selon le nombre de vaisseau d'une classe de la flotte ennemie
	public static AbilityData getAnticipationAbility(int[] requirements, int classes, double chances) {
		return new AbilityData(TYPE_ANTICIPATE, 0 , true, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(chances));
	}
	
	// Capacité - pénitence passive
	public static AbilityData getDamageRetunPassifAbility(int[] requirements, double value) {
		return new AbilityData(TYPE_DAMAGE_RETURN_PASSIF, 0 , true, CATEGORY_BUFF, requirements, String.valueOf(value));
	}
	
	// Capacité - Esquive v2 : varie selon le nombre de vaisseau perdu
	public static AbilityData getDodgeV2Ability(int[] requirements, double chances_lower, double chances_upper) {
		return new AbilityData(TYPE_DODGE_V2, 0, true, CATEGORY_BUFF, requirements,
				String.valueOf(chances_lower),String.valueOf(chances_upper));
	}
	
	// Capacité - Incohérence : % de prendre X bouclier lors d'un tir
	public static AbilityData getIncoherenceAbility(int[] requirements, double chance, int protectionModifier) {
		return new AbilityData(TYPE_INCOHERENCE, 0, true, CATEGORY_SPECIAL, requirements,
				String.valueOf(chance),String.valueOf(protectionModifier));
	}
	
	// Capacité - Répartition : Heal les vaisseaux de la première ligne
	// selon les pv perdu du shipName
	public static AbilityData getDamageRepartitionAbility(int cooldown, int[] requirements, double damageReparted) {
		return new AbilityData(TYPE_DAMAGE_REPARTITION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(damageReparted));
	}
	
	// Diminue les dégats des vaisseaux ennemis quelque soit leurs classe et ligne
	// Plus efficace si shipName en première ligne
	public static AbilityData getBarrageV2Ability(int cooldown, int[] requirements, double frontMalus, double backMalus) {
		return new AbilityData(TYPE_BARRAGE_V2, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(frontMalus), String.valueOf(backMalus));
	}
	
	// Lors d'un tir, diminue les dégâts de la cible
	public static AbilityData getWeaknessAbility(int[] requirements, double damageModifier) {
		return new AbilityData(TYPE_WEAKNESS, 0 , true, CATEGORY_DEBUFF, requirements, String.valueOf(damageModifier));
	}
	
	public static AbilityData getRepercuteAbility(int cooldown, int[] requirements, double damagePercent) {
		return new AbilityData(TYPE_REPERCUTE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(damagePercent));
	}
	
	
	//FIN TODO
	
	public static AbilityData getOverloadAbility(int[] requirements, int divisor, int protectionModifier) {
		return new AbilityData(TYPE_OVERLOAD, 0, true, CATEGORY_BUFF, requirements, String.valueOf(divisor), String.valueOf(protectionModifier));
	}
	
	public static AbilityData getQuantumErrorAbility(int[] requirements, int divisor, double damageModifier) {
		return new AbilityData(TYPE_QUANTUM_ERROR, 0, true, CATEGORY_BUFF, requirements, String.valueOf(divisor), String.valueOf(damageModifier));
	}
	
	public static AbilityData getMimicryAbility(int cooldown, int[] requirements) {
		return new AbilityData(TYPE_MIMICRY, cooldown, false, CATEGORY_SPECIAL, requirements);
	}
	
	public static AbilityData getCurseAbility(int[] requirements, int protectionModifier) {
		return new AbilityData(TYPE_CURSE, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(protectionModifier));
	}
	
	public static AbilityData getImmaterialAbility(int[] requirements, double shotDamageModifier, double abilityDamageModifier) {
		return new AbilityData(TYPE_IMMATERIAL, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(shotDamageModifier), String.valueOf(abilityDamageModifier));
	}
	
	public static AbilityData getConfusionAbility(int cooldown, int[] requirements, boolean frontLine) {
		return new AbilityData(TYPE_CONFUSION, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(frontLine));
	}
	
	public static AbilityData getLotteryAbility(int cooldown, int[] requirements) {
		return new AbilityData(TYPE_LOTTERY, cooldown, false, CATEGORY_SPECIAL, requirements);
	}
	
	public static AbilityData getIncohesionAbility(int[] requirements, int evenRoundProtectionModifier, double evenRoundDamageModifier, int oddRoundProtectionModifier, double oddRoundDamageModifier) {
		return new AbilityData(TYPE_INCOHESION, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(evenRoundProtectionModifier), String.valueOf(evenRoundDamageModifier), String.valueOf(oddRoundProtectionModifier), String.valueOf(oddRoundDamageModifier));
	}
	
	public static AbilityData getHullEnergyTransferAbility(int cooldown, int[] requirements, double hullModifier, double damageModifier) {
		return new AbilityData(TYPE_HULL_ENERGY_TRANSFER, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(hullModifier), String.valueOf(damageModifier));
	}
	
	public static AbilityData getDamageEnergyTransferAbility(int cooldown, int[] requirements, double hullModifier, double damageModifier) {
		return new AbilityData(TYPE_DAMAGE_ENERGY_TRANSFER, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(hullModifier), String.valueOf(damageModifier));
	}
	
	public static AbilityData getParticleProjectionAbility(int cooldown, int[] requirements, int damage, int antimatterCost) {
		return new AbilityData(TYPE_PARTICLE_PROJECTION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(damage), String.valueOf(antimatterCost));
	}

	public static AbilityData getDetonationAbility(int[] requirements, double hullModifier) {
		return new AbilityData(TYPE_DETONATION, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(hullModifier));
	}
	
	public static AbilityData getTerminatorAbility(int[] requirements, double damageModifier) {
		return new AbilityData(TYPE_TERMINATOR, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(damageModifier));
	}
	
	public static AbilityData getPillageAbility(int[] requirements, double stealCoef) {
		return new AbilityData(TYPE_PILLAGE, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(stealCoef));
	}

	public static AbilityData getArtilleryShotAbility(int cooldown, int[] requirements, double percent) {
		return new AbilityData(TYPE_ARTILLERY_SHOT, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(percent));
	}
	
	public static AbilityData getFiercenessAbility(int couldown, int[] requirements, int classes, double bonus) {
		return new AbilityData(TYPE_FIERCENESS, couldown, false, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(bonus));
	}
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
