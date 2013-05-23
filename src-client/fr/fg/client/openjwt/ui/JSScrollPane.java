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

package fr.fg.client.openjwt.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseWheelListener;
import com.google.gwt.user.client.ui.MouseWheelListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseWheelEvents;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;

public class JSScrollPane extends JSComponent implements ClickListener,
		SourcesMouseWheelEvents, EventPreview, MouseListener {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "ScrollPane";
	
	public final static int 
		SCROLLBAR_AS_NEEDED = 0,
		SCROLLBAR_NEVER = 1,
		SCROLLBAR_ALWAYS = 2;
	
	/**
	 * Vitesse de défilement du scrolling, en pixels.
	 */
	public final static int SCROLL_SPEED = 50;
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,					"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,					"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,				"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,				"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,				"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,			"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,			"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,					"scrollPane");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.SCROLL_BUBBLE_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.SCROLL_BUBBLE_MIN_HEIGHT,	"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.SCROLL_UP_BUTTON_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.SCROLL_UP_BUTTON_HEIGHT,	"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.SCROLL_DOWN_BUTTON_WIDTH,	"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.SCROLL_DOWN_BUTTON_HEIGHT,	"-1");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private MouseWheelListenerCollection mouseWheelListeners;
	
	private JSButton scrollUpButton, scrollDownButton, scrollBubble;
	
	private AbsolutePanel scrollTrack, viewPort, scrollBar;
	
	private int scrollBarPolicy;
	
	private double top;
	
	private double bubbleHeight;
	
	private boolean dragging;
	
	private int mouseY;
	
	private int viewHeight;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSScrollPane() {
		super(UI_CLASS_ID);
		
		this.scrollBarPolicy = SCROLLBAR_AS_NEEDED;
		this.dragging = false;
		this.viewHeight = -1;
		
		// Boutons pour scroller
		scrollUpButton = new JSButton();
		String style = scrollUpButton.getStylePrimaryName();
		scrollUpButton.setStylePrimaryName("scrollUp");
		scrollUpButton.addStyleName(style);
		scrollUpButton.addClickListener(this);
		scrollUpButton.setPixelSize(
				getUIPropertyInt(OpenJWT.SCROLL_UP_BUTTON_WIDTH),
				getUIPropertyInt(OpenJWT.SCROLL_UP_BUTTON_HEIGHT));
		
		scrollDownButton = new JSButton();
		scrollDownButton.setStylePrimaryName("scrollDown");
		scrollDownButton.addStyleName(style);
		scrollDownButton.addClickListener(this);
		scrollDownButton.setPixelSize(
				getUIPropertyInt(OpenJWT.SCROLL_DOWN_BUTTON_WIDTH),
				getUIPropertyInt(OpenJWT.SCROLL_DOWN_BUTTON_HEIGHT));
		
		// Scroll bubble
		scrollBubble = new JSButton();
		scrollBubble.setStylePrimaryName("scrollBubble");
		scrollBubble.addStyleName(style);
		scrollBubble.setPixelWidth(
				getUIPropertyInt(OpenJWT.SCROLL_BUBBLE_WIDTH));
		scrollBubble.addMouseListener(this);
		
		// Scroll track
		scrollTrack = new AbsolutePanel();
		scrollTrack.getElement().setAttribute("unselectable", "on");
		scrollTrack.addStyleName("scrollTrack");
		scrollTrack.add(scrollBubble, 0, 0);
		
		// Scroll bar
		scrollBar = new AbsolutePanel();
		scrollBar.getElement().setAttribute("unselectable", "on");
		scrollBar.addStyleName("scrollBar");
		scrollBar.add(scrollTrack, -1, -1);
		scrollBar.add(scrollUpButton, -1, -1);
		scrollBar.add(scrollDownButton, -1, -1);
		
		viewPort = new AbsolutePanel();
		viewPort.addStyleName("viewport");
		scrollBar.getElement().setAttribute("unselectable", "on");
		
		add(viewPort, -1, -1);
		add(scrollBar, -1, -1);
		
		sinkEvents(Event.ONMOUSEWHEEL);
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public Widget getView() {
		return viewPort.getWidgetCount() > 0 ? viewPort.getWidget(0) : null;
	}
	
	public void setView(Widget widget) {
		if (viewPort.getWidgetCount() > 0)
			viewPort.remove(0);
		if (widget != null)
			viewPort.add(widget, 0, 0);
		if (isAttached())
			update();
	}
	
	public double getScroll() {
		double scrollableHeight = viewHeight - getPixelHeight();
		return scrollableHeight > 0 ? -this.top / scrollableHeight : 0;
	}
	
	public double getScrollOffset() {
		return top;
	}
	
	public void setScrollOffset(double offset) {
		if (offset < 0)
			offset = 0;
		
		this.top = offset;
		updateBubble();
	}
	
	public void scrollUp(double dy) {
		if (dy < 0)
			dy = SCROLL_SPEED;
		
		this.top += dy;
		updateBubble();
	}
	
	public void scrollDown(double dy) {
		if (dy < 0)
			dy = SCROLL_SPEED;
		
		this.top -= dy;
		updateBubble();
	}
	
	public int getScrollBarPolicy() {
		return scrollBarPolicy;
	}
	
	public void setScrollBarPolicy(int scrollBarPolicy) {
		this.scrollBarPolicy = scrollBarPolicy;
		updateBubble();
	}
	
	public void onLoad() {
		super.onLoad();
		update();
	}
	
	public void onClick(Widget sender) {
		JSComponent component = (JSComponent) sender;
		
		if (component.getId().equals(scrollUpButton.getId())) {
			scrollUp(SCROLL_SPEED);
		} else if (component.getId().equals(scrollDownButton.getId())) {
			scrollDown(SCROLL_SPEED);
		}
	}
	
	public void setPixelWidth(int width) {
		super.setPixelWidth(width);
		if (isAttached())
			update();
	}

	public void setPixelHeight(int height) {
		super.setPixelHeight(height);
		if (isAttached())
			update();
	}
	
	public boolean onEventPreview(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEMOVE:
			if (dragging) {
				int mouseY = DOM.eventGetClientY(event);
				
				// Déplacement en fonction de la hauteur de l'ascenseur
				int trackHeight = getPixelHeight() -
					scrollUpButton.getPixelHeight() -
					scrollDownButton.getPixelHeight();
				double factor = trackHeight / this.bubbleHeight;
				
				if (mouseY > this.mouseY)
					scrollDown((mouseY - this.mouseY) * factor);
				else if (mouseY < this.mouseY)
					scrollUp((this.mouseY - mouseY) * factor);
				
				this.mouseY = mouseY;
			}
			break;
		case Event.ONMOUSEUP:
			if (dragging) {
				dragging = false;
				DOM.removeEventPreview(this);
			}
			break;
		}
		
		return true;
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEWHEEL:
			update();
			int wheelDelta = DOM.eventGetMouseWheelVelocityY(event);
			
			if (wheelDelta < 0)
				this.scrollUp(SCROLL_SPEED);
			else
				this.scrollDown(SCROLL_SPEED);
			
			if (mouseWheelListeners != null)
				mouseWheelListeners.fireMouseWheelEvent(this, event);
			
			DOM.eventPreventDefault(event);
			DOM.eventCancelBubble(event, true);
			break;
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEUP:
			DOM.eventPreventDefault(event);
			DOM.eventCancelBubble(event, true);
			break;
		}
	}
	
	public void addMouseWheelListener(MouseWheelListener listener) {
		if (mouseWheelListeners == null)
			mouseWheelListeners = new MouseWheelListenerCollection();
		mouseWheelListeners.add(listener);
	}
	
	public void removeMouseWheelListener(MouseWheelListener listener) {
		if (mouseWheelListeners != null)
			mouseWheelListeners.remove(listener);
	}
	
	public void onMouseDown(Widget sender, int x, int y) {
		if (!dragging) {
			DOM.addEventPreview(this);
			this.mouseY = y + DOM.getAbsoluteTop(scrollBubble.getElement());
			this.dragging = true;
			update();
		}
	}

	public void onMouseEnter(Widget sender) {
		// Sans effet
	}

	public void onMouseLeave(Widget sender) {
		// Sans effet
	}

	public void onMouseMove(Widget sender, int x, int y) {
		// Sans effet
	}

	public void onMouseUp(Widget sender, int x, int y) {
		// Sans effet
	}
	
	public void update() {
		int scrollUpButtonWidth = scrollUpButton.getPixelWidth();
		int scrollBubbleWidth = scrollBubble.getPixelWidth();
		int scrollDownButtonWidth = scrollDownButton.getPixelWidth();
		
		viewHeight = getViewHeight();
		scrollTrack.setWidth(scrollBubbleWidth + "px");
		
		int maxWidth = Math.max(Math.max(scrollUpButtonWidth,
				scrollBubbleWidth), scrollDownButtonWidth);
		int viewWidth = Math.max(0, getPixelWidth() - maxWidth);
		
		scrollBar.setSize(maxWidth + "px", getPixelHeight() + "px");
		viewPort.setSize(viewWidth + "px", getPixelHeight() + "px");
		
		Widget view = getView();
		if (view != null)
			view.setWidth(viewWidth + "px");
		
		updateBubble();
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void updateBubble() {
		// Hauteur des éléments du scrollpane
		int scrollPaneHeight = getPixelHeight();
		int trackHeight = scrollPaneHeight -
			scrollUpButton.getPixelHeight() -
			scrollDownButton.getPixelHeight();
		if (trackHeight < 0)
			trackHeight = 0;
		
		Widget view = getView();
		if (viewHeight == -1)
			viewHeight = getViewHeight();
		
		// Positionne l'ascenseur en fonction de la position des éléments du
		// scrollpane
		this.bubbleHeight = scrollPaneHeight >= viewHeight ? trackHeight :
				trackHeight * scrollPaneHeight / (double) viewHeight;
		int bubbleMinHeight = getUIPropertyInt(OpenJWT.SCROLL_BUBBLE_MIN_HEIGHT);
		
		if (this.top < -viewHeight + scrollPaneHeight)
			this.top = -viewHeight + scrollPaneHeight;
		if (this.top > 0)
			this.top = 0;
		
		if (view != null)
			viewPort.setWidgetPosition(getView(), 0, (int) top);
		
		if (bubbleHeight < bubbleMinHeight) {
			int offset = (bubbleMinHeight - (int) Math.ceil(bubbleHeight)) / 2;
			scrollBubble.setPixelHeight(bubbleMinHeight);
			
			int position = (int) Math.floor(-this.top * (
					viewHeight > 0 ? trackHeight / (double) viewHeight : 1)) - offset;
			if (position < 0)
				position = 0;
			if (position + bubbleMinHeight > trackHeight)
				position = trackHeight - bubbleMinHeight;
			
			scrollTrack.setWidgetPosition(scrollBubble, 0, position);
		} else {
			scrollBubble.setPixelHeight((int) Math.ceil(bubbleHeight));
			scrollTrack.setWidgetPosition(scrollBubble, 0, (int) Math.floor(
					-this.top * (viewHeight > 0 ? trackHeight / (double) viewHeight : 1)));
		}
		
		// Affiche ou masque la scrollbar
		switch (scrollBarPolicy) {
		case SCROLLBAR_AS_NEEDED:
			scrollBar.setVisible(scrollPaneHeight < viewHeight);
			break;
		case SCROLLBAR_NEVER:
			scrollBar.setVisible(false);
			break;
		case SCROLLBAR_ALWAYS:
			scrollBar.setVisible(true);
			break;
		}
		
		// Recalcule l'espace disponible pour la vue
		int viewWidth = getPixelWidth() - (scrollBar.isVisible() ?
				scrollUpButton.getPixelWidth() : 0);
		viewPort.setWidth(viewWidth + "px");
		if (view != null)
			view.setWidth(viewWidth + "px");
	}
	
	private int getViewHeight() {
		Widget view = getView();
		
		if (view != null) {
			if (view instanceof JSComponent)
				return ((JSComponent) view).getPixelHeight();
			else
				return view.getOffsetHeight();
		} else {
			return 0;
		}
	}
}
