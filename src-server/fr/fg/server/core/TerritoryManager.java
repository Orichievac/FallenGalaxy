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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import megamu.mesh.Delaunay;
import megamu.mesh.Hull;
import megamu.mesh.MPolygon;
import megamu.mesh.Voronoi;

import fr.fg.server.data.Ally;
import fr.fg.server.data.Area;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.Sector;
import fr.fg.server.servlet.Action;
import fr.fg.server.util.ImageCache;
import fr.fg.server.util.LoggingSystem;

public class TerritoryManager {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static int MAP_SCALE = 10;
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static TerritoryManager instance = new TerritoryManager();
	
	private Map<Integer, ImageCache> territoriesMaps;
	
	private Map<String, Integer> territoriesHash;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private TerritoryManager() {
		this.territoriesMaps = Collections.synchronizedMap(
			new HashMap<Integer, ImageCache>());
		this.territoriesHash = Collections.synchronizedMap(
			new HashMap<String, Integer>());
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public synchronized String getTerritoryHashBySector(int idSector) {
		synchronized (territoriesHash) {
			for (String key : territoriesHash.keySet())
				if (territoriesHash.get(key) == idSector)
					return key;
		}
		return null;
	}
	
	public synchronized Integer getSectorByTerritoryHash(String hash) {
		return territoriesHash.get(hash);
	}
	
	public synchronized ImageCache getTerritoryMap(int idSector) {
		ImageCache territory = territoriesMaps.get(idSector);
		if (territory == null) {
			territory = new ImageCache(createTerritoryMap(idSector));
			territoriesMaps.put(idSector, territory);
		}
		return territory;
	}
	
	public void updateAllTerritoriesMap() {
		List<Sector> sectors = new ArrayList<Sector>(DataAccess.getAllSectors());
		
		for (Sector sector : sectors) 
			updateTerritoryMap(sector.getId());
	}
	
	public synchronized void updateTerritoryMap(int idSector) {
		ImageCache territory = new ImageCache(createTerritoryMap(idSector));
		
		String hash;
		do {
			hash = RandomStringUtils.randomAlphanumeric(32);
		} while (territoriesHash.get(hash) != null);
		
		String oldHash = getTerritoryHashBySector(idSector);
		
		territoriesMaps.put(idSector, territory);
		territoriesHash.put(hash, idSector);
		territoriesHash.remove(oldHash);
	}
	
	public final static TerritoryManager getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static BufferedImage createTerritoryMap(int idSector) {
		List<Area> areas = new ArrayList<Area>(
				DataAccess.getAreasBySector(idSector));
		
		float[][] points = new float[areas.size()][2];
		int[] dominatingAllies = new int[areas.size()];
		int i = 0;
		for (Area area : areas) {
			points[i][0] = area.getX() * MAP_SCALE;
			points[i][1] = area.getY() * MAP_SCALE;
			dominatingAllies[i] = area.getIdDominatingAlly();
			i++;
		}
		
		Hull hull = new Hull(points);
		MPolygon hullPolygon = hull.getRegion();
		float[][] newPoints = new float[points.length + hullPolygon.count()][2];
		System.arraycopy(points, 0, newPoints, 0, points.length);
		
		float[][] hullCoords = hullPolygon.getCoords();
		
		for (i = 0; i < hullPolygon.count(); i++) {
			double angle = Math.atan2(hullCoords[i][1], hullCoords[i][0]);
			double length = Math.sqrt(hullCoords[i][0] * hullCoords[i][0] +
					hullCoords[i][1] * hullCoords[i][1]);
			
			newPoints[i + points.length][0] = (float) (Math.cos(angle) * (length + 8 * MAP_SCALE));
			newPoints[i + points.length][1] = (float) (Math.sin(angle) * (length + 8 * MAP_SCALE));
		}
		
		points = newPoints;
		
		Voronoi voronoi = new Voronoi(points);
		Delaunay delaunay = new Delaunay(points);
		
		// Découpage en régions
		MPolygon[] regions = voronoi.getRegions();
		
		// Calcule le rayon de la galaxie
		int radius = 0;
		
		for (Area area : areas) {
			radius = Math.max(radius,
				area.getX() * area.getX() +
				area.getY() * area.getY());
		}
		
		radius = (int) Math.floor(Math.sqrt(radius) * MAP_SCALE) + 10 * MAP_SCALE;
		int diameter = 2 * radius + 1;
		
		// Construit l'image avec les quadrants
		BufferedImage territoriesImage = new BufferedImage(
				diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D) territoriesImage.getGraphics();
		
		// Affecte une couleur à chaque alliance
		HashMap<Integer, Color> alliesColors = new HashMap<Integer, Color>();
		
		for (Area area : areas) {
			int idDominatingAlly = area.getIdDominatingAlly();
			if (idDominatingAlly != 0)
				alliesColors.put(idDominatingAlly,
					Ally.TERRITORY_COLORS[DataAccess.getAllyById(
						idDominatingAlly).getColor()]);
		}
		
		Polygon[] polygons = new Polygon[regions.length];
		for (i = 0; i < areas.size(); i++) {
			if (dominatingAllies[i] != 0) {
				polygons[i] = createPolygon(regions[i].getCoords(), radius + 1, 3);
			}
		}
		
		// Dessine tous les secteurs
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		for (i = 0; i < areas.size(); i++) {
			if (dominatingAllies[i] == 0)
				continue;
			
			Polygon p = polygons[i];
			
			// Dessine le polygone
			g.setColor(alliesColors.get(dominatingAllies[i]));
			g.fill(p);
			
			// Rempli les espaces entre les polygones adjacents qui
			// correspondent au territoire d'une même alliance
			int[] linkedRegions = delaunay.getLinked(i);
			for (int j = 0; j < linkedRegions.length; j++) {
				int linkedRegion = linkedRegions[j];
				
				if (linkedRegion >= areas.size())
					continue;
				
				if (dominatingAllies[i] == dominatingAllies[linkedRegion]) {
					if (linkedRegion <= i)
						continue;
					
					float[][] coords1 = regions[i].getCoords();
					float[][] coords2 = regions[linkedRegion].getCoords();
					
					int junctionIndex = 0;
					int[][] junctions = new int[2][2];
					
					search:for (int k = 0; k < coords1.length; k++) {
						for (int l = 0; l < coords2.length; l++) {
							if (coords1[k][0] == coords2[l][0] &&
									coords1[k][1] == coords2[l][1]) {
								junctions[junctionIndex][0] = k;
								junctions[junctionIndex][1] = l;
								
								junctionIndex++;
								
								if (junctionIndex == 2) {
									int[] xpts = new int[]{
										polygons[i].xpoints[junctions[0][0]],
										polygons[linkedRegion].xpoints[junctions[0][1]],
										polygons[linkedRegion].xpoints[junctions[1][1]],
										polygons[i].xpoints[junctions[1][0]],
									};
									int[] ypts = new int[]{
										polygons[i].ypoints[junctions[0][0]],
										polygons[linkedRegion].ypoints[junctions[0][1]],
										polygons[linkedRegion].ypoints[junctions[1][1]],
										polygons[i].ypoints[junctions[1][0]],
									};
									
									Polygon border = new Polygon(xpts, ypts, 4);
									g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
									g.fill(border);
									g.draw(border);
									break search;
								}
								break;
							}
						}
					}
				}
			}
		}
		
		// Dessine des lignes de contours des territoires
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (i = 0; i < areas.size(); i++) {
			if (dominatingAllies[i] == 0)
				continue;
			
			g.setStroke(new BasicStroke(1.5f));
			g.setColor(alliesColors.get(dominatingAllies[i]).brighter().brighter());
			
			float[][] coords1 = regions[i].getCoords();
			
			lines:for (int j = 0; j < coords1.length; j++) {
				int[] linkedRegions = delaunay.getLinked(i);
				for (int k = 0; k < linkedRegions.length; k++) {
					int linkedRegion = linkedRegions[k];
					
					if (linkedRegion >= areas.size())
						continue;
					
					if (dominatingAllies[i] == dominatingAllies[linkedRegion]) {
						float[][] coords2 = regions[linkedRegion].getCoords();
						
						for (int m = 0; m < coords2.length; m++) {
							if (coords1[j][0] == coords2[m][0] &&
									coords1[j][1] == coords2[m][1] && ((
									coords1[(j + 1) % coords1.length][0] ==
									coords2[(m + 1) % coords2.length][0] &&
									coords1[(j + 1) % coords1.length][1] ==
									coords2[(m + 1) % coords2.length][1]) || (
									coords1[(j + 1) % coords1.length][0] ==
									coords2[(m - 1 + coords2.length) % coords2.length][0] &&
									coords1[(j + 1) % coords1.length][1] ==
									coords2[(m - 1 + coords2.length) % coords2.length][1]
									))) {
								continue lines;
							}
						}
					}
				}
				
				g.drawLine(
					Math.round(polygons[i].xpoints[j]),
					Math.round(polygons[i].ypoints[j]),
					Math.round(polygons[i].xpoints[(j + 1) % coords1.length]),
					Math.round(polygons[i].ypoints[(j + 1) % coords1.length])
				);
			}
			
			for (int j = 0; j < coords1.length; j++) {
				int neighbours = 0;
				int lastNeighbourRegion = -1;
				int neighbourCoordsIndex = -1;
				
				int[] linkedRegions = delaunay.getLinked(i);
				for (int k = 0; k < linkedRegions.length; k++) {
					int linkedRegion = linkedRegions[k];
					
					if (linkedRegion >= areas.size())
						continue;
					
					if (dominatingAllies[i] == dominatingAllies[linkedRegion]) {
						float[][] coords2 = regions[linkedRegion].getCoords();
						
						for (int m = 0; m < coords2.length; m++) {
							if (coords1[j][0] == coords2[m][0] &&
									coords1[j][1] == coords2[m][1]) {
								neighbours++;
								lastNeighbourRegion = linkedRegion;
								neighbourCoordsIndex = m;
								break;
							}
						}
					}
				}
				
				if (neighbours == 1) {
					g.drawLine(
						Math.round(polygons[i].xpoints[j]),
						Math.round(polygons[i].ypoints[j]),
						Math.round(polygons[lastNeighbourRegion].xpoints[neighbourCoordsIndex]),
						Math.round(polygons[lastNeighbourRegion].ypoints[neighbourCoordsIndex])
					);
				}
			}
		}
		
		
		
		
		BufferedImage finalImage = new BufferedImage(
				diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		
		g = (Graphics2D) finalImage.getGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, .15f));
		g.drawImage(territoriesImage, 0, 0, null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, .5f));
		
		// Charge la police pour afficher le nom des alliances
		try {
			Font textFont = Font.createFont(Font.TRUETYPE_FONT,
					Action.class.getClassLoader().getResourceAsStream(
							"fr/fg/server/resources/TinDog.ttf"));
			textFont = textFont.deriveFont(12f).deriveFont(Font.BOLD);
			g.setFont(textFont);
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn("Could not load quadrant map font.", e);
		}
		FontMetrics fm = g.getFontMetrics();
		
		ArrayList<Integer> closedRegions = new ArrayList<Integer>();
		
		for (i = 0; i < areas.size(); i++) {
			if (dominatingAllies[i] == 0 || closedRegions.contains(i))
				continue;
			
			ArrayList<Integer> allyRegions = new ArrayList<Integer>();
			ArrayList<Integer> openRegions = new ArrayList<Integer>();
			
			openRegions.add(i);
			
			while (openRegions.size() > 0) {
				int currentRegion = openRegions.remove(0);
				allyRegions.add(currentRegion);
				closedRegions.add(currentRegion);
				
				int[] linkedRegions = delaunay.getLinked(currentRegion);
				
				for (int k = 0; k < linkedRegions.length; k++) {
					int linkedRegion = linkedRegions[k];
					
					if (linkedRegion >= areas.size() ||
							openRegions.contains(linkedRegion) || allyRegions.contains(linkedRegion))
						continue;
					
					if (dominatingAllies[i] == dominatingAllies[linkedRegion])
						openRegions.add(linkedRegion);
				}
			}
			
			Area area = areas.get(i);
			long xsum = 0;
			long ysum = 0;
			
			for (int k = 0; k < allyRegions.size(); k++) {
				int allyRegion = allyRegions.get(k);
				area = areas.get(allyRegion);
				
				xsum += area.getX();
				ysum += area.getY();
			}
			
			int x = (int) (xsum / allyRegions.size()) * MAP_SCALE + radius + 1;
			int y = (int) (-ysum / allyRegions.size()) * MAP_SCALE + radius + 1;;
			
			Point point = new Point(x, y);
			boolean validLocation = false;
			for (int k = 0; k < allyRegions.size(); k++) {
				int allyRegion = allyRegions.get(k);
				
				if (polygons[allyRegion].contains(point)) {
					validLocation = true;
					break;
				}
			}
			
			if (validLocation) {
				if (allyRegions.size() == 1)
					y -= 14;
			} else {
				int xmid = (int) (xsum / allyRegions.size());
				int ymid = (int) (ysum / allyRegions.size());
				
				area = areas.get(i);
				int dx = area.getX() - xmid;
				int dy = area.getY() - ymid;
				int distance = dx * dx + dy * dy;
				
				int nearestAreaIndex = i;
				int nearestDistance = distance;
				
				for (int k = 0; k < allyRegions.size(); k++) {
					int allyRegion = allyRegions.get(k);
					
					area = areas.get(allyRegion);
					dx = area.getX() - xmid;
					dy = area.getY() - ymid;
					distance = dx * dx + dy * dy;
					
					if (distance < nearestDistance) {
						nearestAreaIndex = allyRegion;
						nearestDistance = distance;
					}
				}
				
				area = areas.get(nearestAreaIndex);
				x = area.getX() * MAP_SCALE + radius + 1;
				y = -area.getY() * MAP_SCALE + radius - 13;
			}
			
			// Dessine le tag de l'alliance
			String allyTag = "[ " + DataAccess.getAllyById(dominatingAllies[i]).getTag() + " ]";
			g.setColor(Color.BLACK);
			g.drawString(allyTag, x - fm.stringWidth(allyTag) / 2 + 1, y);
			g.setColor(alliesColors.get(dominatingAllies[i]));
			g.drawString(allyTag, x - fm.stringWidth(allyTag) / 2, y);
		}
		
		return finalImage;
	}
	
	private static Polygon createPolygon(float[][] coords, int offset, int reduction) {
		int[] xpts = new int[coords.length], ypts = new int[coords.length];
		
		for (int j = 0; j < coords.length; j++) {
			// Calcule l'angle formé par le segment suivant le point
			float dx1 = coords[(j + 1) % coords.length][0] - coords[j][0];
			float dy1 = coords[(j + 1) % coords.length][1] - coords[j][1];
			double angle1 = Math.atan2(dy1, dx1);
			
			// Calcule l'angle formé par le segment précédent le point
			float dx2 = coords[(j - 1 + coords.length) % coords.length][0] - coords[j][0];
			float dy2 = coords[(j - 1 + coords.length) % coords.length][1] - coords[j][1];
			double angle2 = Math.atan2(dy2, dx2);
			
			// Détermine l'angle entre les deux segments, en supposant que le
			// polygone est convexe (angles < pi)
			double angle = (- angle1 - angle2) / 2;
			if (Math.abs(angle1 - angle2) > Math.PI)
				angle += Math.PI;
			
			// Décale le point vers l'intérieur du polygone selon l'angle formé
			// par les deux segments
			xpts[j] = (int) (Math.round(coords[j][0]) + Math.cos(angle) * reduction + offset);
			ypts[j] = (int) (Math.round(-coords[j][1]) + Math.sin(angle) * reduction + offset);
		}
		
		return new Polygon(xpts, ypts, coords.length);
	}
}
