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

package fr.fg.client.animation;

import com.google.gwt.user.client.Element;

import fr.fg.client.core.Utilities;
import fr.fg.client.i18n.Formatter;

public class ToolTipTimeUpdater extends ToolTipTextUpdater {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private boolean showTimeIfDate;
	
	private double timestamp;
	
	private double time;
	
	private boolean hideWhenFinished;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ToolTipTimeUpdater(Element element, String id, double timestamp) {
		this(element, id, timestamp, true);
	}
	
	public ToolTipTimeUpdater(Element element, String id, double timestamp,
			boolean showTimeIfDate) {
		this(element, id, timestamp, showTimeIfDate, false);
	}
	
	public ToolTipTimeUpdater(Element element, String id, double timestamp,
			boolean showTimeIfDate, boolean hideWhenFinished) {
		super(element, id);
		this.timestamp = timestamp;
		this.showTimeIfDate = showTimeIfDate;
		this.time = Utilities.getCurrentTime();
		this.hideWhenFinished = hideWhenFinished;
		
		setInnerHTML(Formatter.formatDate(Math.ceil(timestamp), showTimeIfDate));
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void synchronize() {
		long now = Utilities.getCurrentTime();
		timestamp -= now - time;
		time = now;
		update(0);
	}
	
	public void update(int interpolation) {
		timestamp -= interpolation / 1000.;
		time += interpolation / 1000.;
		
		if (timestamp < 0)
			timestamp = 0;
		if (timestamp == 0 && hideWhenFinished)
			setInnerHTML("");
		else
			setInnerHTML(Formatter.formatDate(Math.ceil(timestamp), showTimeIfDate));
	}
	
	public boolean isFinished() {
		return timestamp <= 0;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
