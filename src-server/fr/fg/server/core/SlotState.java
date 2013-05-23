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

package fr.fg.server.core;

public class SlotState {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static int
		NORMAL_DAMAGE = 0,
		MINIMUM_DAMAGE = 1,
		MAXIMUM_DAMAGE = 2,
		FIXED_DAMAGE = 3;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Cible sur laquelle le vaisseau est obligée de tirer
	private int lockedTarget;
	
	// Indique si le vaisseau peut tirer sur d'autres cibles si la cible sur
	// laquelle il doit tirer est détruire
	private boolean allowOtherTargets;
	
	// Modificateur de dégâts
	private double damageMultiplier;
	
	// Modificateur de dégâts sur la prochaine attaque
	private double nextDamageMultiplier;
	
	// Bonus / malus de protection
	private int protectionModifier;
	
	// % de points de vie regagnés pour chaque point de dégât infligé
	private double leech;
	
	// Actions restantes avant la fin du renvoi de dégats
	private int retributionLength;
	
	// Maximum de dégats renvoyés avec vengeance
	private long retributionMaxReturnedDamage;
	
	// Actions restantes avant la fin de fusion
	private int fusionLength;
	
	// Actions restantes avant la fin du taunt
	private int tauntLength;
	
	// Bonus de protection avec le taunt
	private int tauntProtectionModifier;
	
	// % de dégâts renvoyés lorsque le vaisseaux est attaqué
	private double damageReturn;
	
	// Modificateur de dégats reçus
	private double sufferedDamageMultiplier;
	
	// Indique si le déphasage a déjà été utilisé
	private boolean phased;
	
	// Mode de dégats (normal, minimum, maximum)
	private int damageMode;
	
	// Modificateur de dégâts avec la capacité escadrille
	private double squadronDamageModifier;
	
	// Modificateur de protection avec la capacité surcharge
	private int overloadProtectionModifier;
	
	// Modificateur de dégâts avec la capacité erreur quantique
	private double quantumErrorDamageMultiplier;
	
	// Indique si la cible est sous l'effet de confusion
	private boolean confused;
	
	// Modificateur de protection avec la capacité incohésion
	private int incohesionProtectionModifier;
	
	// Modificateur de dégâts avec la capacité 
	private double incohesionDamageModifier;
	
	// Modificateur de dégâts avec la capacité résistance
	private double resistanceDamageModifier;
	
	// Classe concernée par résistance
	private int resistanceTargetClass;
	
	// Dégats avec projection de particules
	private int fixedDamageValue;
	
	// Resistance globale %
	private double allResistanceValue;
	
	// Actions restantes avant la fin de la resistance
	private int allResistanceLength;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public SlotState() {
		this.lockedTarget = -1;
		this.allowOtherTargets = true;
		this.damageMultiplier = 1;
		this.nextDamageMultiplier = 1;
		this.protectionModifier = 0;
		this.tauntProtectionModifier = 0;
		this.leech = 0;
		this.retributionLength = 0;
		this.retributionMaxReturnedDamage = 0;
		this.tauntLength = 0;
		this.damageReturn = 0;
		this.sufferedDamageMultiplier = 1;
		this.phased = false;
		this.damageMode = NORMAL_DAMAGE;
		this.squadronDamageModifier = 0;
		this.overloadProtectionModifier = 0;
		this.quantumErrorDamageMultiplier = 1;
		this.confused = false;
		this.incohesionDamageModifier = 1;
		this.incohesionProtectionModifier = 0;
		this.resistanceTargetClass = 0;
		this.resistanceDamageModifier = 1;
		this.allResistanceValue = 0;
		this.allResistanceLength = 0;

	}
	
	// --------------------------------------------------------- METHODES -- //

	public int getProtectionModifier() {
		return protectionModifier + tauntProtectionModifier +
			overloadProtectionModifier + incohesionProtectionModifier;
	}
	
	public void addProtectionModifier(int modifier) {
		this.protectionModifier += modifier;
	}
	
	public void addTauntProtectionModifier(int modifier) {
		this.tauntProtectionModifier += modifier;
	}
	
	public double getDamageMultiplier() {
		return damageMultiplier;
	}
	
	public void multiplyDamage(double multiplier) {
		this.damageMultiplier *= multiplier;
	}

	public double getNextDamageMultiplier() {
		return nextDamageMultiplier;
	}
	
	public void resetNextDamageMultiplier() {
		this.nextDamageMultiplier = 1;
		this.confused = false;
	}
	
	public void multiplyNextDamage(double multiplier) {
		this.nextDamageMultiplier *= multiplier;
	}

	public double getTotalDamageMultiplier() {
		return nextDamageMultiplier * damageMultiplier *
			quantumErrorDamageMultiplier * incohesionDamageModifier;
	}
	
	public double getLeech() {
		return leech;
	}
	
	public void addLeech(double leech) {
		this.leech += leech;
	}
	
	public boolean isRetributionActivated() {
		return retributionLength > 0;
	}
	
	public long getRetributionMaxReturnedDamage() {
		return retributionMaxReturnedDamage;
	}
	
	public void addRetributionMaxReturnedDamage(long maxReturnedDamage) {
		this.retributionMaxReturnedDamage = Math.max(0,
			this.retributionMaxReturnedDamage - maxReturnedDamage);
	}
	
	public void setRetribution(int length, long maxReturnedDamage) {
		this.retributionLength = length;
		this.retributionMaxReturnedDamage = maxReturnedDamage;
	}
	
	public boolean isFusionActivated() {
		return fusionLength > 0;
	}
	
	public void setFusion(int length) {
		this.fusionLength = length;
	}
	
	public boolean isTauntActivated() {
		return tauntLength > 0;
	}
	
	public void setTaunt(int length) {
		this.tauntLength = length;
	}
	
	public void newAction() {
		if (retributionLength > 0)
			retributionLength--;
		if (tauntLength > 0) {
			tauntLength--;
			if (tauntLength == 0)
				tauntProtectionModifier = 0;
		}
		if (fusionLength > 0)
			fusionLength--;
		if(allResistanceLength > 0)
			allResistanceLength--;
	}
	
	public int getLockedTarget() {
		return lockedTarget;
	}
	
	public boolean areOtherTargetsAllowed() {
		return allowOtherTargets;
	}
	
	public void setLockedTarget(int lockedTarget, boolean allowOtherTargets) {
		this.lockedTarget = lockedTarget;
		this.allowOtherTargets = allowOtherTargets;
	}

	public double getDamageReturn() {
		return damageReturn;
	}
	
	public void addDamageReturn(double damageReturn) {
		this.damageReturn += damageReturn;
	}

	public double getSufferedDamageMultiplier() {
		return sufferedDamageMultiplier;
	}
	
	public void multiplySufferedDamage(double multiplier) {
		this.sufferedDamageMultiplier *= multiplier;
	}
	
	public boolean hasPhased() {
		return phased;
	}
	
	public void setPhased(boolean phased) {
		this.phased = phased;
	}

	public int getDamageMode() {
		return damageMode;
	}

	public void setDamageMode(int damageMode) {
		this.damageMode = damageMode;
	}
	
	public void revert() {
		this.damageMultiplier = 1 / this.damageMultiplier;
		this.nextDamageMultiplier = 1 / this.nextDamageMultiplier;
		this.protectionModifier = -this.protectionModifier;
		this.tauntProtectionModifier = -this.tauntProtectionModifier;
		this.incohesionDamageModifier = 1 / this.incohesionDamageModifier;
		this.incohesionProtectionModifier = -this.incohesionProtectionModifier;
		this.sufferedDamageMultiplier = 1 / this.sufferedDamageMultiplier;
	}

	public double getSquadronDamageModifier() {
		return squadronDamageModifier;
	}

	public void setSquadronDamageModifier(int squadronDamageModifier) {
		this.squadronDamageModifier = squadronDamageModifier;
	}
	
	public void addSquadronDamageModifier(double squadronDamageModifier) {
		this.squadronDamageModifier += squadronDamageModifier;
	}

	public int getOverloadProtectionModifier() {
		return overloadProtectionModifier;
	}

	public void setOverloadProtectionModifier(int overloadProtectionModifier) {
		this.overloadProtectionModifier = overloadProtectionModifier;
	}

	public double getQuantumErrorDamageMultiplier() {
		return quantumErrorDamageMultiplier;
	}

	public void setQuantumErrorDamageMultiplier(double quantumErrorDamageMultiplier) {
		this.quantumErrorDamageMultiplier = quantumErrorDamageMultiplier;
	}
	
	public boolean isConfused() {
		return confused;
	}
	
	public void setConfused(boolean confused) {
		this.confused = confused;
	}

	public int getIncohesionProtectionModifier() {
		return incohesionProtectionModifier;
	}

	public void setIncohesionProtectionModifier(int incohesionProtectionModifier) {
		this.incohesionProtectionModifier = incohesionProtectionModifier;
	}

	public double getIncohesionDamageModifier() {
		return incohesionDamageModifier;
	}

	public void setIncohesionDamageModifier(double incohesionDamageModifier) {
		this.incohesionDamageModifier = incohesionDamageModifier;
	}
	
	public double getResistanceDamageModifier() {
		return resistanceDamageModifier;
	}
	
	public int getResistanceTargetClass() {
		return resistanceTargetClass;
	}
	
	public void setResistance(int resistanceTargetClass, double resistanceDamageModifier) {
		this.resistanceTargetClass = resistanceTargetClass;
		this.resistanceDamageModifier = resistanceDamageModifier;
	}

	public int getFixedDamageValue() {
		return fixedDamageValue;
	}
	
	public void setFixedDamageValue(int fixedDamageValue) {
		this.fixedDamageValue = fixedDamageValue;
	}
	
	public boolean isAllResistanceActivated() {
		return allResistanceLength > 0;
	}
	
	public double getAllResistanceValue() {
		return allResistanceValue;
	}
	
	public void setAllResistance(int length, double value) {
		this.allResistanceLength = length;
		this.allResistanceValue = value;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
