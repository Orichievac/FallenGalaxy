/*
 * Copyright 2008 Oliver Zoran
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwt.canvas.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

/**
 * The default implementation of the canvas widget.
 * 
 * @see http://www.whatwg.org/specs/web-apps/current-work/#the-canvas
 * @see http://www.w3.org/html/wg/html5/#the-canvas
 * @see http://canvex.lazyilluminati.com/tests/tests/results.html
 */
public class CanvasImpl {
	protected Element element;

	protected String backgroundColor;

	private JavaScriptObject context;

	public void init(Canvas canvas) {
		this.element = canvas.getElement();
		init();
	}

	protected native void init() /*-{
		this.@gwt.canvas.client.CanvasImpl::context = this.@gwt.canvas.client.CanvasImpl::element.getContext("2d");
	}-*/;

	protected void drawLine(int x1, int y1, int x2, int y2) {
		beginPath();
		moveTo(x1, y1);
		lineTo(x2, y2);
		closePath();
		stroke();
	}

	protected void drawCircle(int x, int y, double radius) {
		beginPath();
		arc(x, y, radius, 0, 2 * Math.PI, true);
		closePath();
		stroke();
	}
	
	protected native void cancelSelections() /*-{
		try {
			$wnd.getSelection().removeAllRanges();
		} catch (e) {
			// do nothing
		}
	}-*/;

	public void setBackgroundColor(String color) {
		backgroundColor = color;
		DOM.setStyleAttribute(element, "backgroundColor", backgroundColor);
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setHeight(int height) {
		DOM.setElementAttribute(element, "height", String.valueOf(height));
	}

	public void setWidth(int width) {
		DOM.setElementAttribute(element, "width", String.valueOf(width));
	}

	public void onMouseDown(Event event) {
		cancelSelections();
		DOM.eventPreventDefault(event);
	}

	public void onMouseUp() {
		// method stub to be overridden by IE implementation
	}

	/////////////////////////////////////////////////////////////////
	// CANVAS STATE METHODS
	/////////////////////////////////////////////////////////////////

	public native void restore() /*-{
		this.@gwt.canvas.client.CanvasImpl::context.restore();
	}-*/;

	public native void save() /*-{
		this.@gwt.canvas.client.CanvasImpl::context.save();
	}-*/;

	public native void rotate(double angle) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.rotate(angle);
	}-*/;

	public native void scale(double x, double y) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.scale(x, y);
	}-*/;

	public native void translate(double x, double y) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.translate(x, y);
	}-*/;

	public native void transform(double m11, double m12, double m21, double m22, double dx, double dy) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.transform(m11, m12, m21, m22, dx, dy);
	}-*/;

	public native void setTransform(double m11, double m12, double m21, double m22, double dx, double dy) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.setTransform(m11, m12, m21, m22, dx, dy);
	}-*/;

	/////////////////////////////////////////////////////////////////
	// WORKING WITH PATHS
	/////////////////////////////////////////////////////////////////

	public native void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.arc(x, y, radius, startAngle, endAngle, anticlockwise);
	}-*/;

	public native void cubicCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
	}-*/;

	public native void quadraticCurveTo(double cpx, double cpy, double x, double y) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.quadraticCurveTo(cpx, cpy, x, y);
	}-*/;

	public native void beginPath() /*-{
		this.@gwt.canvas.client.CanvasImpl::context.beginPath();
	}-*/;

	public native void closePath() /*-{
		this.@gwt.canvas.client.CanvasImpl::context.closePath();
	}-*/;

	public native void moveTo(double x, double y) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.moveTo(x, y);
	}-*/;

	public native void lineTo(double x, double y) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.lineTo(x, y);
	}-*/;

	public native void rect(double x, double y, double w, double h) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.rect(x, y, w, h);
	}-*/;

	/////////////////////////////////////////////////////////////////
	// STROKING AND FILLING
	/////////////////////////////////////////////////////////////////

	public native void clear() /*-{
		this.@gwt.canvas.client.CanvasImpl::context.clearRect(-1e4, -1e4, 2e4, 2e4);
	}-*/;

	public native void fill() /*-{
		this.@gwt.canvas.client.CanvasImpl::context.fill();
	}-*/;

	public native void stroke() /*-{
		this.@gwt.canvas.client.CanvasImpl::context.stroke();
	}-*/;

	public native void clearRect(double x, double y, double w, double h) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.clearRect(x, y, w, h);
	}-*/;

	public native void fillRect(double x, double y, double w, double h) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.fillRect(x, y, w, h);
	}-*/;

	public native void strokeRect(double x, double y, double w, double h) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.strokeRect(x, y, w, h);
	}-*/;

	/////////////////////////////////////////////////////////////////
	// GRADIENT AND PATTERN STYLES
	/////////////////////////////////////////////////////////////////

	public native Object createLinearGradient(double x0, double y0, double x1, double y1) /*-{
		// TODO implement and test
		return null;
	}-*/;

	public native Object createPattern(Image image, String repetition) /*-{
		// TODO implement and test
		return null;
	}-*/;

	public native Object createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) /*-{
		// TODO implement and test
		return null;
	}-*/;

	/////////////////////////////////////////////////////////////////
	// DRAWING IMAGES
	/////////////////////////////////////////////////////////////////

	public native void drawImage(Image image, double x, double y) /*-{
		// TODO implement and test
	}-*/;

	public native void drawImage(Image image, double x, double y, double width, double height) /*-{
		// TODO implement and test
	}-*/;

	public native void drawImage(Image image, double sx, double sy, double swidth, double sheight,
			double dx, double dy, double dwidth, double dheight)
	/*-{
		// TODO implement and test
	}-*/;

	/////////////////////////////////////////////////////////////////
	// SETTERS AND GETTERS
	/////////////////////////////////////////////////////////////////

	public native void setGlobalAlpha(double globalAlpha) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.globalAlpha = globalAlpha;
	}-*/;

	public native double getGlobalAlpha() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.globalAlpha;
	}-*/;

	public native void setGlobalCompositeOperation(String globalCompositeOperation) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.globalCompositeOperation = globalCompositeOperation;
	}-*/;

	public native String getGlobalCompositeOperation() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.globalCompositeOperation;
	}-*/;

	public native void setStrokeStyle(String strokeStyle) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.strokeStyle = strokeStyle;
	}-*/;

	public native String getStrokeStyle() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.strokeStyle;
	}-*/;

	public native void setFillStyle(String fillStyle) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.fillStyle = fillStyle;
	}-*/;

	public native String getFillStyle() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.fillStyle;
	}-*/;

	public native void setLineWidth(double lineWidth) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.lineWidth = lineWidth;
	}-*/;

	public native double getLineWidth() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.lineWidth;
	}-*/;

	public native void setLineCap(String lineCap) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.lineCap = lineCap;
	}-*/;

	public native String getLineCap() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.lineCap;
	}-*/;

	public native void setLineJoin(String lineJoin) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.lineJoin = lineJoin;
	}-*/;

	public native String getLineJoin() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.lineJoin;
	}-*/;

	public native void setMiterLimit(double miterLimit) /*-{
		this.@gwt.canvas.client.CanvasImpl::context.miterLimit = miterLimit;
	}-*/;

	public native double getMiterLimit() /*-{
		return this.@gwt.canvas.client.CanvasImpl::context.miterLimit;
	}-*/;
}
