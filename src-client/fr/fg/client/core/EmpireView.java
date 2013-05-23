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
import java.util.HashSet;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;

import fr.fg.client.data.PlayerAreaData;
import fr.fg.client.data.PlayerFleetData;
import fr.fg.client.data.PlayerGeneratorData;
import fr.fg.client.data.PlayerSectorData;
import fr.fg.client.data.PlayerStarSystemData;
import fr.fg.client.empire.Tree;
import fr.fg.client.empire.View;
import fr.fg.client.empire.view.AreaView;
import fr.fg.client.empire.view.EmptyView;
import fr.fg.client.empire.view.FleetView;
import fr.fg.client.empire.view.GalaxyView;
import fr.fg.client.empire.view.GeneratorView;
import fr.fg.client.empire.view.ScrollView;
import fr.fg.client.empire.view.SectorView;
import fr.fg.client.empire.view.StarSystemView;
import fr.fg.client.openjwt.OpenJWT;

public class EmpireView extends FlowPanel implements WindowResizeListener, Tree {
	// ------------------------------------------------------- CONSTANTES -- //
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private ArrayList<PlayerFleetData> fleets;
	
	private ArrayList<PlayerStarSystemData> systems;
	
	private ArrayList<PlayerGeneratorData> generators;
	
	private long lastSystemsUpdate, lastGeneratorsUpdate;
	
	private ArrayList<AbsolutePanel> empireRows;
	
	private ArrayList<View> views;
	
	private HashMap<Integer, FleetView> fleetViews;
	
	private HashMap<Integer, Long> lastFleetUpdates;
	
	private int rowOffset;
	
	private ScrollView scrollUp, scrollDown;
	
	private int maxRowsCount;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public EmpireView() {
		getElement().setId("empireView");
		getElement().setAttribute("unselectable", "on");
		
		this.maxRowsCount = computeMaxRowsCount();
		this.fleets = new ArrayList<PlayerFleetData>();
		this.systems = new ArrayList<PlayerStarSystemData>();
		this.generators = new ArrayList<PlayerGeneratorData>();
		this.lastSystemsUpdate = 0;
		this.empireRows = new ArrayList<AbsolutePanel>();
		this.scrollUp = new ScrollView(this, true);
		this.scrollDown = new ScrollView(this, false);
		this.views = new ArrayList<View>();
		this.lastFleetUpdates = new HashMap<Integer, Long>();
		this.fleetViews = new HashMap<Integer, FleetView>();
		
		Window.addWindowResizeListener(this);
		
		sinkEvents(Event.ONCLICK | Event.ONDBLCLICK | Event.ONMOUSEWHEEL);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		switch (event.getTypeInt()) {
		case Event.ONMOUSEWHEEL:
			int wheelDelta = DOM.eventGetMouseWheelVelocityY(event);
			
			if (wheelDelta < 0)
				this.setRowOffset(rowOffset - maxRowsCount - 2);
			else
				this.setRowOffset(rowOffset + maxRowsCount - 2);
			break;
		}
	}
	
	public void setData(ArrayList<PlayerStarSystemData> systems,
			ArrayList<PlayerFleetData> fleets,
			ArrayList<PlayerGeneratorData> generators) {
		this.lastSystemsUpdate = Utilities.getCurrentTime();
		this.lastGeneratorsUpdate = Utilities.getCurrentTime();
		this.systems = systems;
		this.generators = generators;
		updateFleets(fleets);
		updateView();
	}
	
	public ArrayList<PlayerFleetData> getFleets() {
		return fleets;
	}
	
	public PlayerFleetData getFleetById(int id) {
		for (PlayerFleetData fleet : fleets)
			if (fleet.getId() == id)
				return fleet;
		return null;
	}
	
	public void updateFleet(PlayerFleetData fleetData) {
		FleetView fleetView = fleetViews.get(fleetData.getId());
		
		if (fleetView == null) {
			fleetViews.put(fleetData.getId(), new FleetView(fleetData));
		} else {
			fleetView.onDataUpdate(fleetData);
			for (PlayerFleetData fleetData2 : fleets) {
				if (fleetData2.getId() == fleetData.getId()) {
					fleets.remove(fleetData2);
					break;
				}
			}
		}
		fleets.add(fleetData);
		lastFleetUpdates.put(fleetData.getId(), Utilities.getCurrentTime());
	}
	
	public void updateFleets(ArrayList<PlayerFleetData> fleetsData) {
		long now = Utilities.getCurrentTime();
		HashSet<Integer> fleetsId = new HashSet<Integer>();
		
		for (PlayerFleetData fleetData : fleetsData) {
			FleetView fleetView = fleetViews.get(fleetData.getId());
			fleetsId.add(fleetData.getId());
			
			if (fleetView == null)
				fleetViews.put(fleetData.getId(), new FleetView(fleetData));
			else
				fleetView.onDataUpdate(fleetData);
			lastFleetUpdates.put(fleetData.getId(), now);
		}
		
		for (int idFleet : fleetViews.keySet()) {
			if (!fleetsId.contains(idFleet)) {
				FleetView fleetView = fleetViews.remove(idFleet);
				fleetView.destroy();
				lastFleetUpdates.remove(idFleet);
			}
		}
		
		this.fleets = fleetsData;
		updateView();
	}

	public ArrayList<PlayerGeneratorData> getGenerators() {
		return generators;
	}
	
	public PlayerGeneratorData getGeneratorById(int id) {
		for (PlayerGeneratorData generator : generators)
			if (generator.getId() == id)
				return generator;
		return null;
	}
	
	public void setGenerators(ArrayList<PlayerGeneratorData> generators) {
		this.lastGeneratorsUpdate = Utilities.getCurrentTime();
		this.generators = generators;
		updateView();
	}
	
	public ArrayList<PlayerStarSystemData> getSystems() {
		return this.systems;
	}
	
	public PlayerStarSystemData getSystemById(int id) {
		for (PlayerStarSystemData system : systems)
			if (system.getId() == id)
				return system;
		return null;
	}

	public void setSystems(ArrayList<PlayerStarSystemData> systems) {
		this.lastSystemsUpdate = Utilities.getCurrentTime();
		this.systems = systems;
		updateView();
	}
	
	public long getLastSystemsUpdate() {
		return lastSystemsUpdate;
	}
	
	public long getLastFleetUpdate(int idFleet) {
		Long lastFleetUpdate = lastFleetUpdates.get(idFleet);
		return lastFleetUpdate == null ? 0 : lastFleetUpdate;
	}
	
	public long getLastGeneratorsUpdate() {
		return lastGeneratorsUpdate;
	}
	
	public void updateAllFleets(){
		for (Integer mapKey : fleetViews.keySet()) {
			fleetViews.get(mapKey).update();
		}
	}
	
	public void updateFleetSelection(){
		for (Integer mapKey : fleetViews.keySet()) {
			fleetViews.get(mapKey).updateSelection();
		}
	}
	
	public void updateView() {
		// Supprime le contenu de la vue de l'empire
		empireRows.clear();
		clear();
		
		for (View view : views)
			if (!(view instanceof FleetView))
				view.destroy();
		views.clear();
		
		HashMap<Integer, PlayerAreaData> areas =
			new HashMap<Integer, PlayerAreaData>();
		ArrayList<Integer> areasId = new ArrayList<Integer>();
		HashMap<Integer, PlayerSectorData> sectors =
			new HashMap<Integer, PlayerSectorData>();
		ArrayList<Integer> sectorsId = new ArrayList<Integer>();
		boolean showGalaxy = false;
		
		int currentSectorId = Client.getInstance().getAreaContainer(
				).getArea().getSector().getId();
		
		for (PlayerStarSystemData system : systems) {
			PlayerSectorData sector = system.getArea().getSector();
			
			if (sector.getId() == currentSectorId) {
				int id = system.getArea().getId();
				if (areas.get(id) == null) {
					areas.put(id, system.getArea());
					areasId.add(id);
				}
			}
			
			if (!sectorsId.contains(sector.getId())) {
				sectors.put(sector.getId(), sector);
				sectorsId.add(sector.getId());
			}
		}
		
		for (PlayerGeneratorData generator : generators) {
			PlayerSectorData sector = generator.getArea().getSector();
			
			if (sector.getId() == currentSectorId) {
				int id = generator.getArea().getId();
				if (areas.get(id) == null) {
					areas.put(id, generator.getArea());
					areasId.add(id);
				}
			}
			
			if (!sectorsId.contains(sector.getId())) {
				sectors.put(sector.getId(), sector);
				sectorsId.add(sector.getId());
			}
		}
		
		for (PlayerFleetData fleet : fleets) {
			PlayerSectorData sector = fleet.getArea().getSector();
			
			if (sector.getId() == currentSectorId) {
				if (fleet.getTag() != 3) {
					int id = fleet.getArea().getId();
					if (areas.get(id) == null) {
						areas.put(id, fleet.getArea());
						areasId.add(id);
					}
				}
			}
			
			if (fleet.getTag() == 3) {
				showGalaxy = true;
			} else if (!sectorsId.contains(sector.getId())) {
				sectors.put(sector.getId(), sector);
				sectorsId.add(sector.getId());
			}
		}
		
		int systemsUpdate = (int) (Utilities.getCurrentTime() - lastSystemsUpdate);
		int generatorsUpdate = (int) (Utilities.getCurrentTime() - lastGeneratorsUpdate);
		
		// Affiche les quadrants
		for (Integer sectorId : sectorsId) {
			if (sectors.get(sectorId).getId() == currentSectorId) {
				// Affiche la liste des secteurs
				for (Integer areaId : areasId) {
					AbsolutePanel row = new AbsolutePanel();
					row.setStyleName("empireRow");
					
					View areaView = new AreaView(areas.get(areaId));
					row.add(areaView);
					views.add(areaView);
					
					// Affiche le contenu du secteur
					int count = 0;
					for (PlayerStarSystemData system : systems) {
						if (system.getArea().getId() == areaId) {
							if (count == 6) {
								row.setWidth(count * 40 + 80 + "px");
								empireRows.add(row);
								
								row = new AbsolutePanel();
								row.setStyleName("empireRow");
								
								View emptyView = new EmptyView();
								row.add(emptyView);
								views.add(emptyView);
								
								count = 0;
							}
							
							View systemView = new StarSystemView(system, systemsUpdate);
							row.add(systemView);
							views.add(systemView);
							count++;
						}
					}
					
					for (PlayerGeneratorData generator : generators) {
						if (generator.getArea().getId() == areaId) {
							if (count == 6) {
								row.setWidth(count * 40 + 80 + "px");
								empireRows.add(row);
								
								row = new AbsolutePanel();
								row.setStyleName("empireRow");
								
								View emptyView = new EmptyView();
								row.add(emptyView);
								views.add(emptyView);
								
								count = 0;
							}
							
							View generatorView = new GeneratorView(generator, generatorsUpdate);
							row.add(generatorView);
							views.add(generatorView);
							count++;
						}
					}
					
					for (PlayerFleetData fleet : fleets)
						if (fleet.getArea().getId() == areaId && fleet.getTag() != 3) {
							if (count == 6) {
								row.setWidth(count * 40 + 80 + "px");
								empireRows.add(row);
								
								row = new AbsolutePanel();
								row.setStyleName("empireRow");
								
								View emptyView = new EmptyView();
								row.add(emptyView);
								views.add(emptyView);
								
								count = 0;
							}
							
							View fleetView = fleetViews.get(fleet.getId());
							row.add(fleetView);
							views.add(fleetView);
							count++;
						}
					
					row.setWidth(count * 40 + 80 + "px");
					empireRows.add(row);
				}
			} else {
				AbsolutePanel row = new AbsolutePanel();
				row.setStyleName("empireRow");
				
				View sectorView = new SectorView(sectors.get(sectorId));
				row.add(sectorView);
				views.add(sectorView);
				
				// Affiche le contenu du secteur
				int count = 0;
				for (PlayerStarSystemData system : systems)
					if (system.getArea().getSector().getId() == sectorId) {
						if (count == 6) {
							row.setWidth(count * 40 + 80 + "px");
							empireRows.add(row);
							
							row = new AbsolutePanel();
							row.setStyleName("empireRow");
							
							View emptyView = new EmptyView();
							row.add(emptyView);
							views.add(emptyView);
							
							count = 0;
						}
						
						View systemView = new StarSystemView(system, systemsUpdate);
						row.add(systemView);
						views.add(systemView);
						count++;
					}
				
				for (PlayerGeneratorData generator : generators)
					if (generator.getArea().getSector().getId() == sectorId) {
						if (count == 6) {
							row.setWidth(count * 40 + 80 + "px");
							empireRows.add(row);
							
							row = new AbsolutePanel();
							row.setStyleName("empireRow");
							
							View emptyView = new EmptyView();
							row.add(emptyView);
							views.add(emptyView);
							
							count = 0;
						}
						
						View generatorView = new GeneratorView(generator, generatorsUpdate);
						row.add(generatorView);
						views.add(generatorView);
						count++;
					}
				
				for (PlayerFleetData fleet : fleets)
					if (fleet.getArea().getSector().getId() == sectorId && fleet.getTag() != 3) {
						if (count == 6) {
							row.setWidth(count * 40 + 80 + "px");
							empireRows.add(row);
							
							row = new AbsolutePanel();
							row.setStyleName("empireRow");
							
							View emptyView = new EmptyView();
							row.add(emptyView);
							views.add(emptyView);
							
							count = 0;
						}
						
						View fleetView = fleetViews.get(fleet.getId());
						row.add(fleetView);
						views.add(fleetView);
						count++;
					}
				
				row.setWidth(count * 40 + 80 + "px");
				empireRows.add(row);
			}
		}
		
		if (showGalaxy) {
			AbsolutePanel row = new AbsolutePanel();
			row.setStyleName("empireRow");
			
			View galaxyView = new GalaxyView();
			row.add(galaxyView);
			views.add(galaxyView);
			
			int count = 0;
			for (PlayerFleetData fleet : fleets)
				if (fleet.getTag() == 3) {
					if (count == 6) {
						row.setWidth(count * 40 + 80 + "px");
						empireRows.add(row);
						
						row = new AbsolutePanel();
						row.setStyleName("empireRow");
						
						View emptyView = new EmptyView();
						row.add(emptyView);
						views.add(emptyView);
						
						count = 0;
					}
					
					View fleetView = fleetViews.get(fleet.getId());
					row.add(fleetView);
					views.add(fleetView);
					count++;
				}
			
			row.setWidth(count * 40 + 80 + "px");
			empireRows.add(row);
		}
		
		setRowOffset(rowOffset);
	}
	
	public int getRowOffset() {
		return rowOffset;
	}
	
	public int getMaxRowsCount() {
		return maxRowsCount;
	}
	
	public void setRowOffset(int rowOffset) {
		clear();
		
		if (empireRows.size() <= maxRowsCount)
			rowOffset = 0;
		else
			rowOffset = Math.max(0, Math.min(rowOffset, empireRows.size() - maxRowsCount + 1));
		
		this.rowOffset = rowOffset;
		boolean showTopScroll = rowOffset > 0;
		boolean showBottomScroll = rowOffset + maxRowsCount < empireRows.size();
		
		if (showTopScroll) {
			AbsolutePanel row = new AbsolutePanel();
			row.setStyleName("empireRow");
			row.setWidth("80px");
			add(row);
			
			row.add(scrollUp);
		}
		
		int rowsCount = Math.max(1, Math.min(empireRows.size(), maxRowsCount) -
			(showTopScroll ? 1 : 0) - (showBottomScroll ? 1 : 0));
		
		for (int i = 0; i < rowsCount; i++)
			add(empireRows.get(i + rowOffset));
		
		if (showBottomScroll) {
			AbsolutePanel row = new AbsolutePanel();
			row.setStyleName("empireRow");
			row.setWidth("80px");
			add(row);
			
			row.add(scrollDown);
		}
		
		AbsolutePanel row = new AbsolutePanel();
		row.setStyleName("empireRow");
		add(row);
	}
	
	public void onWindowResized(int width, int height) {
		int maxRowsCount = computeMaxRowsCount();
		
		if (maxRowsCount != this.maxRowsCount) {
			this.maxRowsCount = maxRowsCount;
			setRowOffset(rowOffset);
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private int computeMaxRowsCount() {
		int clientWidth = Window.getClientWidth();
		return (OpenJWT.getClientHeight() - (
				clientWidth <= 1024 ? 160 : 230)) / 40;
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
}
