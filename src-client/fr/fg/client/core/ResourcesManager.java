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

package fr.fg.client.core;

import java.util.ArrayList;
import java.util.HashMap;

import fr.fg.client.core.settings.Settings;
import fr.fg.client.data.BuildingData;
import fr.fg.client.data.PlayerStarSystemData;

public class ResourcesManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ArrayList<PlayerStarSystemData> systems;
	
	// Date de dernière mise à jour des systèmes du joueur
	private long lastSystemsUpdate;
	
	// Temps restant avant que les stocks du système soient plein
	private HashMap<Integer, Double> fullStockTimeCache;
	
	// Production du système pour chaque ressource
	private HashMap<Integer, double[]> productionCache;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ResourcesManager() {
		this.systems = new ArrayList<PlayerStarSystemData>();
		this.fullStockTimeCache = new HashMap<Integer, Double>();
		this.productionCache = new HashMap<Integer, double[]>();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setSystems(ArrayList<PlayerStarSystemData> systems) {
		this.systems = systems;
		this.lastSystemsUpdate = Utilities.getCurrentTime();
		this.updateCache();
	}
	
	public long getCurrentCredits() {
		// Calcule les crédits et ressources du joueur
		long credits = (long) systems.get(0).getCredits();
		
		// Temps écoulé depuis la dernière mise à jour des systèmes
		long dt = Utilities.getCurrentTime() - lastSystemsUpdate;
		
		for (PlayerStarSystemData system : systems) {
			double maxPopulation = BuildingData.getProduction(
					BuildingData.CIVILIAN_INFRASTRUCTURES, system);
			
			// Calcule la croissance de la population du système, jusqu'à
    		// l'heure de la mise à jour
			double frame = system.getLastPopulationUpdate() + dt;
			double growth = PlayerStarSystemData.POPULATION_GROWTH * Settings.getTimeUnit() *
				frame * BuildingData.getProduction(BuildingData.CIVILIAN_INFRASTRUCTURES, system);
			
	    	// Limite la population en fonction du nombre d'infrastructures
			// civiles
			double growthCoef = .5;
			if (system.getPopulation() + growth > maxPopulation) {
				growthCoef = .5 * (maxPopulation - system.getPopulation()) / growth +
					(1 - (maxPopulation - system.getPopulation()) / growth);
				growth = maxPopulation - system.getPopulation();
			}
			
			// Calcule les crédits gagnés avec les corporations
			credits += (system.getPopulation() + growth * growthCoef) *
				frame * BuildingData.getProduction(BuildingData.CORPORATIONS, system) *
				system.getProductionModifier() *
				BuildingData.EXPLOITATION_RATE *
				Settings.getTimeUnit();
		}
		
		return credits;
	}
	
	public double getCurrentPopulation(int systemId) {
		long dt = Utilities.getCurrentTime() - lastSystemsUpdate;
		
		for (PlayerStarSystemData system : systems) {
			if (system.getId() == systemId) {
				double maxPopulation = BuildingData.getProduction(
						BuildingData.CIVILIAN_INFRASTRUCTURES, system);
				
				// Calcule la croissance de la population du système, jusqu'à
	    		// l'heure de la mise à jour
				double frame = system.getLastPopulationUpdate() + dt;
				double growth = PlayerStarSystemData.POPULATION_GROWTH * Settings.getTimeUnit() *
					frame * BuildingData.getProduction(BuildingData.CIVILIAN_INFRASTRUCTURES, system);
				
				return Math.min(system.getPopulation() + growth, maxPopulation);
			}
		}
		
		return 0;
	}
	
	public long getCurrentResource(int systemId, int index) {
		// Temps écoulé depuis la dernière mise à jour des systèmes
		long dt = Utilities.getCurrentTime() - lastSystemsUpdate;
		
		for (PlayerStarSystemData system : systems) {
			// Met à jour les ressources du système
			if (system.getId() == systemId) {
				return (long) Math.floor(system.getResourceAt(index) +
					(productionCache.get(systemId)[index] * BuildingData.EXPLOITATION_RATE *
					Settings.getTimeUnit()) *
					Math.min(system.getLastUpdate() + dt, fullStockTimeCache.get(systemId)));
			}
		}
		
		return 0;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	private void updateCache() {
		fullStockTimeCache.clear();
		productionCache.clear();
		
		for (PlayerStarSystemData system : systems) {
			// Temps restant avant que les stocks du système soient plein
			fullStockTimeCache.put(system.getId(), getFullStockRemainingTime(system));
			
			// Production du système pour chaque ressource
			double[] production = new double[4];
			for (int i = 0; i < 4; i++) {
				production[i] =
					BuildingData.getProduction(BuildingData.EXPLOITATION + i, system) *
					BuildingData.getProduction(BuildingData.REFINERY, system) *
					system.getProductionModifier();
			}
			productionCache.put(system.getId(), production);
		}
	}
	
	private double getFullStockRemainingTime(PlayerStarSystemData systemData) {
		double stock = BuildingData.getProduction(BuildingData.STOREHOUSE, systemData);
		
		// Calcule la production de toutes les exploitations
		double production = 0;
		for (int i = 0; i < 4; i++)
			production +=
				BuildingData.getProduction(BuildingData.EXPLOITATION + i, systemData) *
				BuildingData.getProduction(BuildingData.REFINERY, systemData) *
				systemData.getProductionModifier();
		
		if (production == 0)
			return 0;
		
		// Calcule le nombre de ressources sur le système
		double resourcesSum = 0;
		for (int i = 0; i < 4; i++)
			resourcesSum += systemData.getResourceAt(i);
		
		// Calcule le temps restant avant que le système soit complet
		return Math.max(0, (stock - resourcesSum) / (production * Settings.getTimeUnit() / 60));
	}
}
