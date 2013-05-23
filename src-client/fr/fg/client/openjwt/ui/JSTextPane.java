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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.RichTextArea.Justification;

import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.Callback;
import fr.fg.client.openjwt.animation.CallbackHandler;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;

public class JSTextPane extends JSRowLayout implements ClickListener, Callback {
	// ------------------------------------------------------- CONSTANTES -- //
	
	public final static String UI_CLASS_ID = "TextPane";
	
	static {
		setDefaultProperty(UI_CLASS_ID, OpenJWT.ELEMENT,			"div");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.INNER_HTML,			"");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_WIDTH,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.DEFAULT_HEIGHT,		"-1");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.LINE_HEIGHT,		"false");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.HORIZONTAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.VERTICAL_MARGIN,	"0");
		setDefaultProperty(UI_CLASS_ID, OpenJWT.CSS_CLASS,			"textpane");
	}
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private JSButton boldBt, italicBt, underlineBt, justifyLeftBt,
		justifyCenterBt, justifyRightBt, insertImgBt;
	
	private SimplePanel container;
	
	private RichTextArea richTextArea;
	
	private boolean loaded;
	
	private int tries;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public JSTextPane() {
		loaded = false;
		tries = 0;
		
		container = new SimplePanel();
		OpenJWT.setElementFloat(container.getElement(), "left");
		
		richTextArea = new RichTextArea();
		richTextArea.setSize("100%", "100%");
		
		container.add(richTextArea);
		
		boldBt = new JSButton();
		boldBt.addStyleName("bold");
		boldBt.setPixelWidth(30);
		boldBt.addClickListener(this);
		
		italicBt = new JSButton();
		italicBt.addStyleName("italic");
		italicBt.setPixelWidth(30);
		italicBt.addClickListener(this);
		
		underlineBt = new JSButton();
		underlineBt.addStyleName("underline");
		underlineBt.setPixelWidth(30);
		underlineBt.addClickListener(this);
		
		justifyLeftBt = new JSButton();
		justifyLeftBt.addStyleName("justifyLeft");
		justifyLeftBt.setPixelWidth(30);
		justifyLeftBt.addClickListener(this);
		
		justifyCenterBt = new JSButton();
		justifyCenterBt.addStyleName("justifyCenter");
		justifyCenterBt.setPixelWidth(30);
		justifyCenterBt.addClickListener(this);
		
		justifyRightBt = new JSButton();
		justifyRightBt.addStyleName("justifyRight");
		justifyRightBt.setPixelWidth(30);
		justifyRightBt.addClickListener(this);
		
		insertImgBt = new JSButton();
		insertImgBt.addStyleName("insertImg");
		insertImgBt.setPixelWidth(30);
		insertImgBt.addClickListener(this);
		
		addComponent(boldBt);
		addComponent(italicBt);
		addComponent(underlineBt);
		addComponent(justifyLeftBt);
		addComponent(justifyCenterBt);
		addComponent(justifyRightBt);
		if (richTextArea.getExtendedFormatter() != null)
			addComponent(insertImgBt);;
		addRow();
		addComponent(container);
		
		addStyleName("textPane");
	}
	
	// --------------------------------------------------------- METHODES -- //

	public String getHTML() {
		return richTextArea.getHTML();
	}
	
	public void setHTML(String html) {
		richTextArea.setHTML(html);
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		if (!loaded) {
			loaded = true;
			TimerManager.registerCallback(new CallbackHandler(this, 1000));
			TimerManager.registerCallback(new CallbackHandler(this, 5000));
		}
	}
	
	public void run() {
		if (!attachStyleSheet(richTextArea.getElement(),
				Config.getTheme() + "/style.css", tries > 0) ||
				!hasStyleSheet(richTextArea.getElement())) {
			tries++;
			TimerManager.registerCallback(new CallbackHandler(this, 500));
		}
	}
	
	public void setPixelWidth(int width) {
		container.setWidth(width + "px");
		richTextArea.setWidth(width - getUIPropertyInt(
				OpenJWT.HORIZONTAL_MARGIN) + "px");
		
		super.setPixelWidth(width);
	}

	public void setPixelHeight(int height) {
		container.setHeight(height - boldBt.getPixelHeight() + "px");
		richTextArea.setHeight(height - boldBt.getPixelHeight() -
			getUIPropertyInt(OpenJWT.VERTICAL_MARGIN) + "px");
		
		super.setPixelHeight(height);
	}
	
	public void onClick(Widget sender) {
		if (sender == boldBt) {
			richTextArea.getBasicFormatter().toggleBold();
		} else if (sender == italicBt) {
			richTextArea.getBasicFormatter().toggleItalic();
		} else if (sender == underlineBt) {
			richTextArea.getBasicFormatter().toggleUnderline();
		} else if (sender == justifyLeftBt) {
			richTextArea.getBasicFormatter().setJustification(Justification.LEFT);
		} else if (sender == justifyCenterBt) {
			richTextArea.getBasicFormatter().setJustification(Justification.CENTER);
		} else if (sender == justifyRightBt) {
			richTextArea.getBasicFormatter().setJustification(Justification.RIGHT);
		} else if (sender == insertImgBt) {
			JSOptionPane.showInputDialog("Entrez l'URL de l'image",
				"Insérer une image",
				JSOptionPane.OK_OPTION | JSOptionPane.CANCEL_OPTION,
				JSOptionPane.QUESTION_MESSAGE, new OptionPaneListener() {
					public void optionSelected(Object option) {
						if (option != null)
							richTextArea.getExtendedFormatter(
									).insertImage((String) option);
					}
				}, "");
		}
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private native boolean hasStyleSheet(Element iframe) /*-{
		var idocument = iframe.contentWindow.document;
		var head = idocument.getElementsByTagName('head');
		
		if (head != null && head.length > 0) {
			head = head[0];
			for (var i = 0; i < head.childNodes.length; i++)
				if ((head.childNodes[i].nodeName + '').toLowerCase() == 'link')
					return true;
		}
		return false;
	}-*/;
	
	private native boolean attachStyleSheet(Element iframe, String url, boolean createHead) /*-{
		var idocument = iframe.contentWindow.document;
		var head = idocument.getElementsByTagName('head');
		
		// Crée un élément <head> pour chrome, qui n'en génère pas
		if (createHead && head.length == 0) {
			var element = idocument.createElement('head');
			idocument.getElementsByTagName('html')[0].insertBefore(element, idocument.body);
			head[0] = element;
		}
		
		if (head != null && head.length > 0) {
			var element = idocument.createElement('link');
			element.rel = 'stylesheet';
			element.href = url;
			element.type = 'text/css';
			
			head[0].appendChild(element);
			
			idocument.body.className = 'textPaneFrame';
			
			return true;
		} else {
			return false;
		}
	}-*/;
}
