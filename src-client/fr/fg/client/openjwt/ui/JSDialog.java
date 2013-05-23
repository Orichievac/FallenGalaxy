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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.ClassNameUpdater;
import fr.fg.client.openjwt.animation.OpacityUpdater;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.core.Point;
import fr.fg.client.openjwt.core.SoundManager;
import fr.fg.client.openjwt.core.TextManager;
import fr.fg.client.openjwt.core.TextManager.OutlineText;

public class JSDialog extends JSComponent implements EventListener, EventPreview {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "Dialog";
	
	public final static int
		DO_NOTHING_ON_CLOSE = 0,
		HIDE_ON_CLOSE = 1,
		DESTROY_ON_CLOSE = 2;
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"<div id=\"${titlebar}\"><div id=\"${close}\"></div><div id=\"${title}\"></div></div><div id=\"${content}\"></div>");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"dialog");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION,		"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_X,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.FX_DECORATION_OFFSET_Y,	"0");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	// Profondeur du dialogue au premier plan
	private static int foregroundZIndex = 100;
	
	private int defaultCloseOperation;
	
	private boolean modal, movable, closable;
	
	private Element titleBarElement, titleElement, closeElement, contentElement;
	
	private boolean mouseDown;
	
	private int mouseX, mouseY;
	
	private int dialogX, dialogY;
	
	private Element blockerElement;
	
	private Widget clearer;
	
	private DialogListenerCollection dialogListeners;
	
	private OpacityUpdater updater;
	
	private ClassNameUpdater closeElementUpdater;
	
	private OutlineText outlineText;
	
	private String title;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSDialog(String title, boolean modal, boolean movable, boolean closable) {
		super(UI_CLASS_ID);
		
		this.defaultCloseOperation = HIDE_ON_CLOSE;
		this.mouseDown = false;
		this.dialogX = 0;
		this.dialogY = 0;
		
		loadInnerHTML(new String[]{"titlebar", "close", "content", "title"});
		
		this.titleBarElement = getSubElementById("titlebar");
		this.titleElement = getSubElementById("title");
		this.closeElement = getSubElementById("close");
		this.contentElement = getSubElementById("content");
		
		setTitle(title);
		setModal(modal);
		setMovable(movable);
		setClosable(closable);
		
		clearer = new Clearer();
		add(clearer);
		
		super.setVisible(false);
		
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEMOVE | Event.ONMOUSEUP |
				Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}
	
	// --------------------------------------------------------- METHODES -- //

	public void addDialogListener(DialogListener listener) {
		if (dialogListeners == null)
			dialogListeners = new DialogListenerCollection();
		dialogListeners.add(listener);
	}

	public void removeDialogListener(DialogListener listener) {
		if (dialogListeners != null)
			dialogListeners.remove(listener);
	}
	
	public int getDefaultCloseOperation() {
		return this.defaultCloseOperation;
	}
	
	public void setDefaultCloseOperation(int operation) {
		this.defaultCloseOperation = operation;
	}
	
	public Point getLocation() {
		return new Point(dialogX, dialogY);
	}
	
	/**
	 * Déplace le dialogue à une position donnée sur l'écran.
	 *
	 * @param x L'abscisse du coin supérieur gauche du dialogue par rapport au
	 * coin supérieur de la page, en pixels.
	 * @param y L'ordonnée du coin supérieur gauche du dialogue par rapport au
	 * coin supérieur de la page, en pixels.
	 * @param withinPage Indique si la fenêtre doit rester dans les
	 * limites de la page. Vaut false par défaut.
	 */
	public void setLocation(int x, int y, boolean withinPage) {
		if (withinPage) {
			int clientWidth  = Window.getClientWidth();
			int clientHeight = Window.getClientHeight();
			int width = getPixelWidth();
			int height = getPixelHeight();
			
			// Vérifie que le dialogue ne sort pas de la page
			if (x + width >= clientWidth)
				x = clientWidth - width - 1;
			if (x < 0)
				x = 0;
			if (y + height >= clientHeight)
				y = clientHeight - height - 1;
			if (y < 0)
				y = 0;
		}
		
		this.dialogX = x;
		this.dialogY = y;
		getElement().getStyle().setProperty("left", x + "px");
		getElement().getStyle().setProperty("top",  y + "px");
	}
	
	/**
	 * Centre le dialogue à l'écran.
	 */
	public void centerOnScreen() {
		// Dimensions de l'écran
		int clientWidth  = Window.getClientWidth();
		int clientHeight = Window.getClientHeight();
		
		if (!isAttached()) {
			boolean visible = isVisible();
			super.setVisible(true);
			RootPanel.get().add(this, 0, 0);
			
			// Centre le dialogue
			setLocation(
					(clientWidth - getPixelWidth()) / 2,
					(clientHeight - getPixelHeight()) / 2, true);
			
			RootPanel.get().remove(this);
			super.setVisible(visible);
		} else {
			// Centre le dialogue
			setLocation(
					(clientWidth - getPixelWidth()) / 2,
					(clientHeight - getPixelHeight()) / 2, true);
		}
	}
	
	public boolean isVisible() {
		if (updater != null && !updater.isFinished())
			return updater.getTargetOpacity() != 0;
		else
			return super.isVisible();
	}
	
	/**
	 * Affiche ou masque le dialogue.
	 *
	 * @param visible Vaut true si le dialogue doit être affiché.
	 */
	public void setVisible(boolean visible) {
		if (isVisible() == visible)
			return;
		
		if (Config.getGraphicsQuality() >=
				Config.VALUE_QUALITY_AVERAGE) {
			if (updater != null) {
				if (updater.isFinished()) {
					updater = new OpacityUpdater(getElement(),
							visible ? 0 : 1, 5,
							defaultCloseOperation == DESTROY_ON_CLOSE ?
							OpacityUpdater.REMOVE_WHEN_TRANSPARENT :
							OpacityUpdater.HIDE_WHEN_TRANSPARENT);
					TimerManager.register(updater);
				}
				
				updater.setTargetOpacity(visible ? 1 : 0, true);
			} else {
				updater = new OpacityUpdater(getElement(),
						visible ? 0 : 1, 5,
						defaultCloseOperation == DESTROY_ON_CLOSE ?
						OpacityUpdater.REMOVE_WHEN_TRANSPARENT :
						OpacityUpdater.HIDE_WHEN_TRANSPARENT);
				TimerManager.register(updater);
				updater.setTargetOpacity(visible ? 1 : 0, true);
			}
			
			if (visible)
				getElement().getStyle().setProperty("display", "");
		} else {
			if (defaultCloseOperation == DESTROY_ON_CLOSE && !visible && getParent() != null)
				removeFromParent();
			
			getElement().getStyle().setProperty("display", visible ? "" : "none");
		}
		
		if (!visible) {
			if (isModal())
				RootPanel.getBodyElement().removeChild(blockerElement);
			
			fireDialogClosed();
		} else {
			if (this.isModal()) {
				blockerElement = DOM.createDiv();
				blockerElement.setAttribute("unselectable", "on");
				blockerElement.setClassName("dialog-blocker");
				blockerElement.getStyle().setProperty("zIndex", String.valueOf(foregroundZIndex));
				RootPanel.getBodyElement().appendChild(blockerElement);
				blockerElement.setInnerHTML("<img src=\"" + Config.getMediaUrl() +
						"images/misc/blank.gif\" unselectable=\"on\"/><div unselectable=\"on\"></div>");
				foregroundZIndex++;
			}
			
			focus();
			
			if (!isAttached())
				RootPanel.get().add(this, dialogX, dialogY);
		}
	}
	
	public boolean onEventPreview(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEMOVE:
			if (mouseDown) {
				// Position actuelle de la différence
				int mouseX = OpenJWT.eventGetPointerX(event);
				int mouseY = OpenJWT.eventGetPointerY(event);
				
				Point location = getLocation();
				
				// Déplace le dialogue de la différence entre la dernière position de
				// la souris et sa position actuelle
				this.setLocation(
						location.getX() + mouseX - this.mouseX,
						location.getY() + mouseY - this.mouseY, true);
				
				// Remplace la dernière position de la souris par sa position actuelle
				this.mouseX = mouseX;
				this.mouseY = mouseY;

				event.cancelBubble(true);
				event.preventDefault();
			}
			break;
		case Event.ONMOUSEUP:
			if (mouseDown) {
				DOM.removeEventPreview(this);
				mouseDown = false;
				titleBarElement.getStyle().setProperty("cursor", "");

				event.cancelBubble(true);
				event.preventDefault();
			}
			break;
		}
		
		return false;
	}
	
	@Override
	public void onUnload() {
		super.onUnload();
		
		if (updater != null) {
			TimerManager.unregister(updater);
			updater = null;
		}
		
		if (closeElementUpdater != null) {
			TimerManager.unregister(closeElementUpdater);
			closeElementUpdater = null;
		}
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
			focus();
			
			if (closeElement.isOrHasChild(event.getTarget())) {
				SoundManager.getInstance().playSound(SOUND_CLICK);
				switch (defaultCloseOperation) {
				case HIDE_ON_CLOSE:
					setVisible(false);
					break;
				case DESTROY_ON_CLOSE:
					setVisible(false);
					removeFromParent();
					break;
				}
			} else if (titleBarElement.isOrHasChild(event.getTarget())) {
				if (!mouseDown && movable) {
					titleBarElement.getStyle().setProperty("cursor", "move");
					
					// Position de la souris au moment de la pression sur le bouton
					mouseX = OpenJWT.eventGetPointerX(event);
					mouseY = OpenJWT.eventGetPointerY(event);
					
					mouseDown = true;
					
					DOM.addEventPreview(this);
				}
			}
			break;
		case Event.ONMOUSEOVER:
			if (Config.getGraphicsQuality() >=
					Config.VALUE_QUALITY_AVERAGE &&
					closeElement.isOrHasChild(event.getTarget())) {
				if (closeElementUpdater != null) {
					if (closeElementUpdater.isFinished()) {
						closeElementUpdater = new ClassNameUpdater(
								closeElement, "dialog-close", "state", 0, 10);
						TimerManager.register(closeElementUpdater);
					}
					closeElementUpdater.setTargetClass(4, false);
					closeElementUpdater.setIncrement(20);
				} else {
					closeElementUpdater = new ClassNameUpdater(
							closeElement, "dialog-close", "state", 0, 10);
					TimerManager.register(closeElementUpdater);
					closeElementUpdater.setTargetClass(4, false);
					closeElementUpdater.setIncrement(20);
				}
			}
			break;
		case Event.ONMOUSEOUT:
			if (!closeElement.isOrHasChild(event.getToElement())) {
				if (closeElementUpdater != null && !closeElementUpdater.isFinished()) {
					closeElementUpdater.setTargetClass(0, true);
					closeElementUpdater.setIncrement(10);
				}
			}
			break;
		}
	}
	
	public void setComponent(Widget widget) {
		clear();
		add(widget, contentElement);
		add(clearer, contentElement);
	}
	
	/**
	 * Renvoie le titre du dialogue.
	 *
	 * @return Le titre du dialogue.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Modifie le titre du dialogue.
	 *
	 * @param title Le nouveau titre du dialogue, pouvant contenir du HTML.
	 */
	public void setTitle(String title) {
		String decoration = getUIProperty(OpenJWT.FX_DECORATION);
		if (title == null)
			title = "";
		if (title.startsWith("<"))
			title = title.substring(0, title.lastIndexOf(">")) +
				title.substring(title.lastIndexOf(">")).replace(" ", "&nbsp;");
		else
			title = title.replace(" ", "&nbsp;");
		this.title = title;
		
		if (decoration.equals("")) {
			titleElement.setInnerHTML(title);
		} else {
			outlineText = TextManager.getText(title, decoration, new Point(
				getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_X),
				getUIPropertyInt(OpenJWT.FX_DECORATION_OFFSET_Y)), true);
			
			titleElement.setInnerHTML("");
			titleElement.appendChild(outlineText.getElement());
		}
	}
	
	public boolean isModal() {
		return this.modal;
	}
	
	public void setModal(boolean modal) {
		this.modal = modal;
	}
	
	public boolean isMovable() {
		return this.movable;
	}
	
	public void setMovable(boolean movable) {
		this.movable = movable;
	}
	
	/**
	 * Renvoie <code>true</code> si le dialogue comporte un bouton dans la
	 * barre de titre pour fermer le dialogue.
	 *
	 * @return <code>true</code> si le dialogue peut être fermé par un bouton
	 * dans la barre de titre.
	 */
	public boolean isClosable() {
		return this.closable;
	}
	
	public void setClosable(boolean closable) {
		this.closable = closable;
		closeElement.getStyle().setProperty("display", closable ? "" : "none");
	}
	
	/**
	 * Déplace la fenêtre au premier-plan.
	 */
	public void focus() {
		getElement().getStyle().setProperty("zIndex", String.valueOf(foregroundZIndex));
		foregroundZIndex++;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	protected void fireDialogClosed() {
		if (dialogListeners != null)
			dialogListeners.fireDialogClosed(this);
	}
	
	// -------------------------------------------------- CLASSES PRIVEES -- //
	
	private class Clearer extends Widget {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public Clearer() {
			setElement(DOM.createDiv());
			addStyleName("clearer");
		}
		
		// ----------------------------------------------------- METHODES -- //
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
