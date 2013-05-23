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


import fr.fg.server.i18n.Messages;

public class Ability {
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
		TYPE_REPERCUTE 				= 65,
		TYPE_ARTILLERY_SHOT			= 66,
		TYPE_FIERCENESS             = 67,
		TYPE_FLIGHT                 = 68;
	
	public final static String
		CATEGORY_BUFF			= "buff",
		CATEGORY_DEBUFF			= "debuff",
		CATEGORY_SPECIAL		= "special";
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int type;
	
	private int[] requirements;
	
	private int cooldown;
	
	private boolean passive;
	
	private String category;
	
	private String[] args;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private Ability(int type, int cooldown, boolean passive,
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
	
	public String getDesc(int shipId) {
		String desc = Messages.getString("ability" + getType() + ".desc");
		
		desc = desc.replace("%shipName%", Messages.getString("ships" + shipId));
		
		for (int i = 0; i < args.length; i++) {
			if (desc.contains("%" + i + "%"))
				desc = desc.replace("%" + i + "%", args[i]);
			if (desc.contains("%" + i + ".line%"))
				desc = desc.replace("%" + i + ".line%",
					Boolean.parseBoolean(args[i]) ?
						Messages.getString("ability.frontLine") :
						Messages.getString("ability.backLine"));
			if (desc.contains("%" + i + ".percent%"))
				desc = desc.replace("%" + i + ".percent%",
					(int) Math.round(100 * Double.parseDouble(args[i])) + "%");
			if (desc.contains("%" + i + ".percent>100%"))
				desc = desc.replace("%" + i + ".percent>100%",
					(int) Math.round(100 * (Double.parseDouble(args[i]) - 1)) + "%");
			if (desc.contains("%" + i + ".percent<100%"))
				desc = desc.replace("%" + i + ".percent<100%",
					(int) Math.round(100 * (1 - Double.parseDouble(args[i]))) + "%");
				if (desc.contains("%" + i + ".signed%"))
				desc = desc.replace("%" + i + ".signed%",
					(Double.parseDouble(args[i]) >= 0 ? "+" : "") + args[i]);
			if (desc.contains("%" + i + ".shipClass%"))
				desc = desc.replace("%" + i + ".shipClass%",
					Messages.getString("ship.classes" + args[i]));
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
	// TYPE_DAMAGE_ENERGY_TRANSFER
	
	public double getEnergyTransferHullModifier() {
		return Double.parseDouble(args[0]);
	}
	
	public double getEnergyTransferDamageModifier() {
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
	
	public static Ability getCriticalHitAbility(int[] requirements, double chances, double multiplier) {
		return new Ability(TYPE_CRITICAL_HIT, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(chances), String.valueOf(multiplier));
	}
	
	public static Ability getPassiveLeechAbility(int[] requirements, double value) {
		return new Ability(TYPE_PASSIVE_LEECH, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static Ability getForceFieldAbility(int cooldown, int[] requirements, int lowProtectionModifier, int highProtectionModifier) {
		return new Ability(TYPE_FORCE_FIELD, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(lowProtectionModifier), String.valueOf(highProtectionModifier));
	}
	
	public static Ability getRepairAbility(int cooldown, int[] requirements, double value) {
		return new Ability(TYPE_REPAIR, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}

	public static Ability getDodgeAbility(int[] requirements, double chances) {
		return new Ability(TYPE_DODGE, 0, true, CATEGORY_BUFF, requirements, String.valueOf(chances));
	}

	public static Ability getFragChargeAbility(int cooldown, int[] requirements, double bonus) {
		return new Ability(TYPE_FRAG_CHARGE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(bonus));
	}

	public static Ability getFocusFireAbility(int cooldown, int[] requirements, double bonus) {
		return new Ability(TYPE_FOCUSED_FIRE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(bonus));
	}

	public static Ability getActiveLeechAbility(int cooldown, int[] requirements, double value) {
		return new Ability(TYPE_ACTIVE_LEECH, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static Ability getBarrageAbility(int cooldown, int[] requirements, double frontMalus, double backMalus, int classes) {
		return new Ability(TYPE_BARRAGE, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(frontMalus), String.valueOf(backMalus), String.valueOf(classes));
	}
	
	public static Ability getSacrificeAbility(int cooldown, int[] requirements, int lowModifier, int highModifier, int selfModifier) {
		return new Ability(TYPE_SACRIFICE, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(lowModifier), String.valueOf(highModifier), String.valueOf(selfModifier));
	}
	
	public static Ability getRetributionAbility(int cooldown, int[] requirements, int length, double maxReturnedDamagePercentage) {
		return new Ability(TYPE_RETRIBUTION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(length), String.valueOf(maxReturnedDamagePercentage));
	}
	
	public static Ability getHarassmentAbility(int[] requirements, double value) {
		return new Ability(TYPE_HARASSMENT, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(value));
	}
	
	public static Ability getRapidFireAbility(int cooldown, int[] requirements, double value) {
		return new Ability(TYPE_RAPID_FIRE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static Ability getTauntAbility(int cooldown, int[] requirements, int length, int protectionModifier) {
		return new Ability(TYPE_TAUNT, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(length), String.valueOf(protectionModifier));
	}
	
	public static Ability getDamageReturnAuraAbility(int cooldown, int[] requirements, double value) {
		return new Ability(TYPE_DAMAGE_RETURN_AURA, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(value));
	}
	
	public static Ability getHeatAbility(int[] requirements, int modifier) {
		return new Ability(TYPE_HEAT, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(modifier));
	}

	public static Ability getTimeShiftAbility(int cooldown, int[] requirements, int actions) {
		return new Ability(TYPE_TIME_SHIFT, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(actions));
	}

	public static Ability getDevastateAbility(int cooldown, int[] requirements, double value) {
		return new Ability(TYPE_DEVASTATE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}

	public static Ability getResistanceAbility(int cooldown, int[] requirements, int classes, double value) {
		return new Ability(TYPE_RESISTANCE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(value));
	}

	public static Ability getBreachAbility(int[] requirements, double value) {
		return new Ability(TYPE_BREACH, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(value));
	}
	
	public static Ability getPhaseAbility(int[] requirements) {
		return new Ability(TYPE_PHASE, 0, true, CATEGORY_SPECIAL, requirements);
	}
	
	public static Ability getRiftAbility(int cooldown, int[] requirements) {
		return new Ability(TYPE_RIFT, cooldown, false, CATEGORY_SPECIAL, requirements);
	}
	
	public static Ability getDeflectorAbility(int cooldown, int[] requirements, int classes, int value) {
		return new Ability(TYPE_DEFLECTOR, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(value));
	}
	
	public static Ability getInhibitorFieldAbility(int cooldown, int[] requirements) {
		return new Ability(TYPE_INHIBITOR_FIELD, cooldown, false, CATEGORY_BUFF, requirements);
	}
	
	public static Ability getRageAbility(int cooldown, int[] requirements, int protectionModifier) {
		return new Ability(TYPE_RAGE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(protectionModifier));
	}
	
	public static Ability getOutflankingAbility(int[] requirements, double value) {
		return new Ability(TYPE_OUTFLANKING, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static Ability getUnbreakableAbility(int[] requirements) {
		return new Ability(TYPE_UNBREAKABLE, 0, true, CATEGORY_BUFF, requirements);
	}

	public static Ability getSublimationAbility(int[] requirements, double chances) {
		return new Ability(TYPE_SUBLIMATION, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(chances));
	}

	public static Ability getFusionAbility(int cooldown, int[] requirements, int length) {
		return new Ability(TYPE_FUSION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(length));
	}

	public static Ability getSquadronAbility(int[] requirements, int classes, double extraDamage) {
		return new Ability(TYPE_SQUADRON, 0, true, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(extraDamage));
	}

	public static Ability getEcmAbility(int[] requirements, double maxLosses) {
		return new Ability(TYPE_ECM, 0, true, CATEGORY_BUFF, requirements, String.valueOf(maxLosses));
	}
	
	//TODO
	public static Ability getEvadeAbility(int[] requirements, double percent) {
		return new Ability(TYPE_EVADE, 0 , true, CATEGORY_BUFF, requirements, String.valueOf(percent));
	}
	
	public static Ability getCollateralAbility(int[] requirements, int protectionModifier) {
		return new Ability(TYPE_COLLATERAL, 0 , true, CATEGORY_SPECIAL, requirements, String.valueOf(protectionModifier));
	}

	public static Ability getOverheatingAbility(int cooldown, int[] requirements, double damageModifier, double hullModifier) {
		return new Ability(TYPE_OVERHEAT, cooldown , false, CATEGORY_SPECIAL, requirements, String.valueOf(damageModifier), String.valueOf(hullModifier));
	}
	
	public static Ability getCriticalHitV2Ability(int[] requirements, double chance1, double chance2, double multiplier1, double multiplier2) {
		return new Ability(TYPE_CRITICALV2, 0 , true, CATEGORY_SPECIAL, requirements, String.valueOf(chance1),
				String.valueOf(chance2), String.valueOf(multiplier1), String.valueOf(multiplier2));
	}

	public static Ability getProximityChargeAbility(int cooldown, int[] requirements, double value) {
		return new Ability(TYPE_PROXIMITY_CHARGE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(value));
	}
	
	public static Ability getRapidFireV2Ability(int cooldown, int[] requirements, double damageModifier, boolean allLines) {
		return new Ability(TYPE_RAPID_FIRE_V2, cooldown, false, CATEGORY_SPECIAL, requirements,
				String.valueOf(damageModifier), String.valueOf(allLines));
	}
	
	public static Ability getRecupAbility(int cooldown, int[] requirements, double value) {
		return new Ability(TYPE_RECUP, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(value));
	}
	
	public static Ability getAllResistanceAbility(int cooldown, int[] requirements, double value, int length) {
		return new Ability(TYPE_ALL_RESISTANCE, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(value), String.valueOf(length));
	}
	
	
	public static Ability getSublimationV2Ability(int[] requirements, double chances1, double chances2, double value1, double value2) {
		return new Ability(TYPE_SUBLIMATION_V2, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(chances1),
				String.valueOf(chances2),String.valueOf(value1),String.valueOf(value2));
	}
	
	public static Ability getAbsorbtionAbility(int[] requirements, double hullModifier) {
		return new Ability(TYPE_ABSORBTION, 0, true, CATEGORY_BUFF, requirements, String.valueOf(hullModifier));
	}
	
	public static Ability getCanaliseAbility(int[] requirements, int protectionBonus, double percentKill) {
		return new Ability(TYPE_CANALISE, 0, true, CATEGORY_BUFF, requirements, String.valueOf(protectionBonus), String.valueOf(percentKill));
	}
	
	public static Ability getAnticipationAbility(int[] requirements, int classes, double chances) {
		return new Ability(TYPE_ANTICIPATE, 0 , true, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(chances));
	}
	
	public static Ability getDamageRetunPassifAbility(int[] requirements, double value) {
		return new Ability(TYPE_DAMAGE_RETURN_PASSIF, 0 , true, CATEGORY_BUFF, requirements, String.valueOf(value));
	}
	
	
	public static Ability getDodgeV2Ability(int[] requirements, double chances_lower, double chances_upper) {
		return new Ability(TYPE_DODGE_V2, 0, true, CATEGORY_BUFF, requirements,
				String.valueOf(chances_lower),String.valueOf(chances_upper));
	}
	
	public static Ability getIncoherenceAbility(int[] requirements, double chance, int protectionModifier) {
		return new Ability(TYPE_INCOHERENCE, 0, true, CATEGORY_SPECIAL, requirements,
				String.valueOf(chance),String.valueOf(protectionModifier));
	}
	
	//
	public static Ability getDamageRepartitionAbility(int cooldown, int[] requirements, double damageReparted) {
		return new Ability(TYPE_DAMAGE_REPARTITION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(damageReparted));
	}
	
	// Diminue les dégats des vaisseaux ennemis quelque soit leurs classe et ligne
	// Plus efficace si shipName en première ligne
	public static Ability getBarrageV2Ability(int cooldown, int[] requirements, double frontMalus, double backMalus) {
		return new Ability(TYPE_BARRAGE_V2, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(frontMalus), String.valueOf(backMalus));
	}
	
	// Lors d'un tir, diminue les dégâts de la cible
	public static Ability getWeaknessAbility(int[] requirements, double damageModifier) {
		return new Ability(TYPE_WEAKNESS, 0 , true, CATEGORY_DEBUFF, requirements, String.valueOf(damageModifier));
	}
	
	public static Ability getRepercuteAbility(int cooldown, int[] requirements, double damagePercent) {
		return new Ability(TYPE_REPERCUTE, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(damagePercent));
	}
	
	
	
	//FIN TODO

	public static Ability getOverloadAbility(int[] requirements, int divisor, int protectionModifier) {
		return new Ability(TYPE_OVERLOAD, 0, true, CATEGORY_BUFF, requirements, String.valueOf(divisor), String.valueOf(protectionModifier));
	}
	
	public static Ability getQuantumErrorAbility(int[] requirements, int divisor, double damageModifier) {
		return new Ability(TYPE_QUANTUM_ERROR, 0, true, CATEGORY_BUFF, requirements, String.valueOf(divisor), String.valueOf(damageModifier));
	}
	
	public static Ability getMimicryAbility(int cooldown, int[] requirements) {
		return new Ability(TYPE_MIMICRY, cooldown, false, CATEGORY_SPECIAL, requirements);
	}
	
	public static Ability getCurseAbility(int[] requirements, int protectionModifier) {
		return new Ability(TYPE_CURSE, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(protectionModifier));
	}
	
	public static Ability getImmaterialAbility(int[] requirements, double shotDamageModifier, double abilityDamageModifier) {
		return new Ability(TYPE_IMMATERIAL, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(shotDamageModifier), String.valueOf(abilityDamageModifier));
	}
	
	public static Ability getConfusionAbility(int cooldown, int[] requirements, boolean frontLine) {
		return new Ability(TYPE_CONFUSION, cooldown, false, CATEGORY_DEBUFF, requirements, String.valueOf(frontLine));
	}
	
	public static Ability getLotteryAbility(int cooldown, int[] requirements) {
		return new Ability(TYPE_LOTTERY, cooldown, false, CATEGORY_SPECIAL, requirements);
	}
	
	public static Ability getIncohesionAbility(int[] requirements, int evenRoundProtectionModifier, double evenRoundDamageModifier, int oddRoundProtectionModifier, double oddRoundDamageModifier) {
		return new Ability(TYPE_INCOHESION, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(evenRoundProtectionModifier), String.valueOf(evenRoundDamageModifier), String.valueOf(oddRoundProtectionModifier), String.valueOf(oddRoundDamageModifier));
	}
	
	public static Ability getHullEnergyTransferAbility(int cooldown, int[] requirements, double hullModifier, double damageModifier) {
		return new Ability(TYPE_HULL_ENERGY_TRANSFER, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(hullModifier), String.valueOf(damageModifier));
	}
	
	public static Ability getDamageEnergyTransferAbility(int cooldown, int[] requirements, double hullModifier, double damageModifier) {
		return new Ability(TYPE_DAMAGE_ENERGY_TRANSFER, cooldown, false, CATEGORY_BUFF, requirements, String.valueOf(hullModifier), String.valueOf(damageModifier));
	}
	
	public static Ability getParticleProjectionAbility(int cooldown, int[] requirements, int damage, int antimatterCost) {
		return new Ability(TYPE_PARTICLE_PROJECTION, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(damage), String.valueOf(antimatterCost));
	}
	
	public static Ability getDetonationAbility(int[] requirements, double hullModifier) {
		return new Ability(TYPE_DETONATION, 0, true, CATEGORY_DEBUFF, requirements, String.valueOf(hullModifier));
	}
	
	public static Ability getTerminatorAbility(int[] requirements, double damageModifier) {
		return new Ability(TYPE_TERMINATOR, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(damageModifier));
	}
	
	public static Ability getPillageAbility(int[] requirements, double stealCoef) {
		return new Ability(TYPE_PILLAGE, 0, true, CATEGORY_SPECIAL, requirements, String.valueOf(stealCoef));
	}
	

	public static Ability getArtilleryShotAbility(int cooldown, int[] requirements, double percent) {
		return new Ability(TYPE_ARTILLERY_SHOT, cooldown, false, CATEGORY_SPECIAL, requirements, String.valueOf(percent));
	}
	
	public static Ability getFiercenessAbility(int couldown, int[] requirements, int classes, double bonus) {
		return new Ability(TYPE_FIERCENESS, 5, false, CATEGORY_BUFF, requirements, String.valueOf(classes), String.valueOf(bonus));
	}
	
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
