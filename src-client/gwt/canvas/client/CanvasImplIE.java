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

import java.util.ArrayList;
import java.util.Stack;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * The Internet Explorer implementation of the canvas widget.
 * 
 * @see http://msdn2.microsoft.com/en-us/library/bb250524(VS.85).aspx
 * @see http://en.wikipedia.org/wiki/Transformation_matrix
 * @see http://code.google.com/p/explorercanvas/
 */
public class CanvasImplIE extends CanvasImpl {

	private abstract class Path {
	}

	private class ClosePath extends Path {
		public String toString() {
			return " x";
		}
	}

	private class MoveTo extends Path {
		protected double x;
		protected double y;
		public MoveTo(double x, double y) {
			this.x = x;
			this.y = y;
			currentX = x;
			currentY = y;
		}
		public String toString() {
			return " m " + getCoordX(x, y) + "," + getCoordY(x, y);
		}
	}

	private class LineTo extends MoveTo {
		public LineTo(double x, double y) {
			super(x, y);
		}
		public String toString() {
			return " l " + getCoordX(x, y) + "," + getCoordY(x, y);
		}
	}

	private class BezierCurveTo extends MoveTo {
		protected double c1x;
		protected double c1y;
		protected double c2x;
		protected double c2y;
		public BezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
			super(x, y);
			c1x = cp1x;
			c1y = cp1y;
			c2x = cp2x;
			c2y = cp2y;
		}
		public String toString() {
			return " c " +
				getCoordX(c1x, c1y) + "," + getCoordY(c1x, c1y) + "," +
				getCoordX(c2x, c2y) + "," + getCoordY(c2x, c2y) + "," +
				getCoordX(x, y)     + "," + getCoordY(x, y);
		}
	}

	private class Arc extends Path {
		protected String type;
		protected double x;
		protected double y;
		protected double ar;
		protected double startX;
		protected double startY;
		protected double endX;
		protected double endY;
		public Arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
			this.x = x;
			this.y = y;
			if (anticlockwise) {
				type = " at ";
			} else {
				type = " wa ";
			}
			ar = radius * 10;
			startX = x + Math.cos(startAngle) * ar - 5;
			startY = y + Math.sin(startAngle) * ar - 5;
			endX   = x + Math.cos(endAngle)   * ar - 5;
			endY   = y + Math.sin(endAngle)   * ar - 5;
			if (startX == endX && !anticlockwise) {
				startX += 0.125;
			}
		}
		public String toString() {
			int cx = getCoordX(x, y);
			int cy = getCoordY(x, y);
			double arcX = context.arcScaleX * ar;
			double arcY = context.arcScaleY * ar;
			return type +
				((int) Math.floor(cx - arcX + 0.5)) + "," +
				((int) Math.floor(cy - arcY + 0.5)) + " " +
				((int) Math.floor(cx + arcX + 0.5)) + "," +
				((int) Math.floor(cy + arcY + 0.5)) + " " +
				getCoordX(startX, startY) + "," +
				getCoordY(startX, startY) + " " +
				getCoordX(endX, endY) + "," +
				getCoordY(endX, endY);
		}
	}

	public final static String SOURCE_OVER = "beforeEnd";

	public final static String DESTINATION_OVER = "afterBegin";

	public final static String BUTT = "flat";

	private ArrayList<Integer> clearRectIndices = new ArrayList<Integer>();

	private Stack<VMLContext> contextStack = new Stack<VMLContext>();

	private Stack<Path> pathStack = new Stack<Path>();

	private Canvas canvas;

	private VMLContext context;

	private double[] matrix;

	private double currentX = 0.0;

	private double currentY = 0.0;

	public void init(Canvas canvas) {
		this.element = canvas.getElement();
		this.canvas = canvas;
		DOM.setStyleAttribute(element, "position",  "relative");
		DOM.setStyleAttribute(element, "display",   "inline-block");
		DOM.setStyleAttribute(element, "overflow",  "hidden");
		DOM.setStyleAttribute(element, "textAlign", "left");
		DOM.setStyleAttribute(element, "cursor",    "default");
		init();
		context = new VMLContext();
		matrix = context.matrix;
	}
	
	protected void drawLine(int x1, int y1, int x2, int y2) {
		String strbuf =
			"<vml:line style=\"position:absolute;\" from=\"" + x1 + "," + y1 + "\" " +
			"to=\"" + x2 + "," + y2 + "\" strokecolor=\"" +
			context.strokeStyle + "\" strokeweight=\"" + context.lineWidth + "px\">" +
			"<vml:stroke opacity=\"" + (context.globalAlpha * context.strokeAlpha) +
			"\" miterlimit=\"" + context.miterLimit +
			"\" joinstyle=\"" + context.lineJoin +
			"\" endcap=\"" + context.lineCap +
			"\"></vml:stroke></vml:line>";
		insert(context.globalCompositeOperation, strbuf);
	}
	
	protected void drawCircle(int x, int y, double radius) {
		String strbuf =
			"<vml:oval style=\"position:absolute; top:" + x + "px; " +
			"left:" + x + "px; width:" + radius + "px; height:" + radius + "px;\" " +
			"strokecolor=\"" + context.strokeStyle + "\" strokeweight=\"" + context.lineWidth + "px\">" +
			"<vml:stroke opacity=\"" + (context.globalAlpha * context.strokeAlpha) +
			"\" miterlimit=\"" + context.miterLimit +
			"\" joinstyle=\"" + context.lineJoin +
			"\" endcap=\"" + context.lineCap +
			"\"></vml:stroke></vml:line>";
		insert(context.globalCompositeOperation, strbuf);
	}

	protected native void init() /*-{
		if (!$doc.namespaces["vml"]) {
			$doc.namespaces.add("vml", "urn:schemas-microsoft-com:vml", "#default#VML");
//			$doc.createStyleSheet().cssText = "vml\\:line,vml\\:shape,vml\\:rect,vml\\:oval,vml\\:stroke{behavior:url(#default#VML);}";
		}
	}-*/;

	protected native void cancelSelections() /*-{
		try {
			$doc.selection.empty();
		} catch (e) {
			// do nothing
		}
	}-*/;

	private native void insert(String gco, String html) /*-{
		this.@gwt.canvas.client.CanvasImpl::element.insertAdjacentHTML(gco, html);
	}-*/;

	private int getCoordX(double x, double y) {
		return (int) Math.floor(10 * (matrix[0] * x + matrix[3] * y + matrix[6]) - 4.5);
	}

	private int getCoordY(double x, double y) {
		return (int) Math.floor(10 * (matrix[1] * x + matrix[4] * y + matrix[7]) - 4.5);
	}

	public void setBackgroundColor(String color) {
		if (!clearRectIndices.isEmpty() && color.equals(Canvas.TRANSPARENT)) {
			throw new IllegalStateException();
		}
		super.setBackgroundColor(color);
		for (Integer index : clearRectIndices) {
			Element elem = DOM.getChild(element, index);
			DOM.setElementAttribute(elem, "fillcolor", backgroundColor);
		}
	}

	public void setHeight(int height) {
		DOM.setInnerHTML(element, "");
		DOM.setStyleAttribute(element, "height", height + "px");
	}

	public void setWidth(int width) {
		DOM.setInnerHTML(element, "");
		DOM.setStyleAttribute(element, "width", width + "px");
	}

	public void onMouseDown(Event event) {
		cancelSelections();
		DOM.setCapture(element);
	}

	public void onMouseUp() {
		DOM.releaseCapture(element);
	}

	/////////////////////////////////////////////////////////////////
	// CANVAS STATE METHODS
	/////////////////////////////////////////////////////////////////

	public void restore() {
		if (!contextStack.isEmpty()) {
			context = contextStack.pop();
			matrix = context.matrix;
		}
	}

	public void save() {
		contextStack.push(context);
		context = new VMLContext(context);
		matrix = context.matrix;
	}

	public void rotate(double angle) {
		double s = Math.sin(angle);
		double c = Math.cos(angle);
		double a = matrix[0];
		double b = matrix[3];
		matrix[0] =  a * c + b * s;
		matrix[3] = -a * s + b * c;
		a = matrix[1];
		b = matrix[4];
		matrix[1] =  a * c + b * s;
		matrix[4] = -a * s + b * c;
	}

	public void scale(double x, double y) {
		context.arcScaleX *= x;
		context.arcScaleY *= y;
		matrix[0] *= x;
		matrix[1] *= x;
		matrix[3] *= y;
		matrix[4] *= y;
	}

	public void translate(double x, double y) {
		matrix[6] += matrix[0] * x + matrix[3] * y;
		matrix[7] += matrix[1] * x + matrix[4] * y;
	}

	public void transform(double m11, double m12, double m21, double m22, double dx, double dy) {
		double a = matrix[0];
		double b = matrix[3];
		matrix[0]  = a * m11 + b * m12;
		matrix[3]  = a * m21 + b * m22;
		matrix[6] += a * dx  + b * dy;
		a = matrix[1];
		b = matrix[4];
		matrix[1]  = a * m11 + b * m12;
		matrix[4]  = a * m21 + b * m22;
		matrix[7] += a * dx  + b * dy;
	}

	public void setTransform(double m11, double m12, double m21, double m22, double dx, double dy) {
		matrix[0] = m11; matrix[1] = m12;
		matrix[3] = m21; matrix[4] = m22;
		matrix[6] = dx;  matrix[7] = dy;
	}

	/////////////////////////////////////////////////////////////////
	// WORKING WITH PATHS
	/////////////////////////////////////////////////////////////////

	public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
		pathStack.push(new Arc(x, y, radius, startAngle, endAngle, anticlockwise));
	}

	public void cubicCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
		pathStack.push(new BezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y));
	}

	public void quadraticCurveTo(double cpx, double cpy, double x, double y) {
		double cp1x = currentX + 2.0 / 3.0 * (cpx - currentX);
		double cp1y = currentY + 2.0 / 3.0 * (cpy - currentY);
		double cp2x = cp1x + (x - currentX) / 3.0;
		double cp2y = cp1y + (y - currentY) / 3.0;
		pathStack.push(new BezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y));
	}

	public void beginPath() {
		pathStack.clear();
	}

	public void closePath() {
		pathStack.push(new ClosePath());
	}

	public void moveTo(double x, double y) {
		pathStack.push(new MoveTo(x, y));
	}

	public void lineTo(double x, double y) {
		pathStack.push(new LineTo(x, y));
	}

	public void rect(double x, double y, double w, double h) {
		pathStack.push(new MoveTo(x    , y));
		pathStack.push(new LineTo(x + w, y));
		pathStack.push(new LineTo(x + w, y + h));
		pathStack.push(new LineTo(x    , y + h));
		pathStack.push(new ClosePath());
	}

	/////////////////////////////////////////////////////////////////
	// STROKING AND FILLING
	/////////////////////////////////////////////////////////////////

	public void clear() {
		pathStack.clear();
		clearRectIndices.clear();
		DOM.setInnerHTML(element, "");
	}

	public void fill() {
		if (pathStack.isEmpty()) {
			return;
		}
		StringBuffer strbuf = new StringBuffer(
				"<vml:shape style=\"position:absolute;width:10;height:10;\" coordsize=\"100,100\" fillcolor=\"" +
				context.fillStyle + "\" stroked=\"f\" path=\""
		);
		for (Path path : pathStack) {
			strbuf.append(path);
		}
		strbuf.append(
				" e\"><vml:fill opacity=\"" + (context.globalAlpha * context.fillAlpha) +
				"\"></vml:fill></vml:shape>"
		);
		insert(context.globalCompositeOperation, strbuf.toString());
	}

	public void stroke() {
		if (pathStack.isEmpty()) {
			return;
		}
		StringBuffer strbuf = new StringBuffer(
				"<vml:shape style=\"position:absolute;width:10;height:10;\" coordsize=\"100,100\" filled=\"f\" strokecolor=\"" +
				context.strokeStyle + "\" strokeweight=\"" + context.lineWidth + "px\" path=\""
		);
		for (Path path : pathStack) {
			strbuf.append(path);
		}
		strbuf.append(
				" e\"><vml:stroke opacity=\"" + (context.globalAlpha * context.strokeAlpha) +
				"\" miterlimit=\"" + context.miterLimit +
				"\" joinstyle=\"" + context.lineJoin +
				"\" endcap=\"" + context.lineCap +
				"\"></vml:stroke></vml:shape>"
		);
		insert(context.globalCompositeOperation, strbuf.toString());
	}

	public void clearRect(double x, double y, double w, double h) {
		pathStack.clear();
		int index = DOM.getChildCount(element);
		if (index <= 0) {
			return;
		}
		w += x;
		h += y;
		if (((int) Math.floor((matrix[0] * x + matrix[3] * y + matrix[6]) + 0.5)) <= 0 &&
			((int) Math.floor((matrix[1] * x + matrix[4] * y + matrix[7]) + 0.5)) <= 0 &&
			((int) Math.floor((matrix[0] * w + matrix[3] * h + matrix[6]) + 0.5)) >= canvas.getWidth() &&
			((int) Math.floor((matrix[1] * w + matrix[4] * h + matrix[7]) + 0.5)) >= canvas.getHeight())
		{
			clearRectIndices.clear();
			DOM.setInnerHTML(element, "");
			return;
		}
		clearRectIndices.add(index);
		insert(context.globalCompositeOperation,
				"<vml:shape style=\"position:absolute;width:10;height:10;\" coordsize=\"100,100\" fillcolor=\"" +
				backgroundColor + "\" stroked=\"f\" path=\"m " +
				getCoordX(x, y) + "," + getCoordY(x, y) + " l " +
				getCoordX(x, h) + "," + getCoordY(x, h) + " " +
				getCoordX(w, h) + "," + getCoordY(w, h) + " " +
				getCoordX(w, y) + "," + getCoordY(w, y) + " x e\"><vml:fill opacity=\"1\"></vml:fill></vml:shape>"
		);
	}

	public void fillRect(double x, double y, double w, double h) {
		w += x;
		h += y;
		insert(context.globalCompositeOperation,
				"<vml:shape style=\"position:absolute;width:10;height:10;\" coordsize=\"100,100\" fillcolor=\"" +
				context.fillStyle + "\" stroked=\"f\" path=\"m " +
				getCoordX(x, y) + "," + getCoordY(x, y) + " l " +
				getCoordX(x, h) + "," + getCoordY(x, h) + " " +
				getCoordX(w, h) + "," + getCoordY(w, h) + " " +
				getCoordX(w, y) + "," + getCoordY(w, y) + " x e\"><vml:fill opacity=\"" +
				(context.globalAlpha * context.fillAlpha) + "\"></vml:fill></vml:shape>"
		);
		pathStack.clear();
	}

	public void strokeRect(double x, double y, double w, double h) {
		w += x;
		h += y;
		insert(context.globalCompositeOperation,
				"<vml:shape style=\"position:absolute;width:10;height:10;\" coordsize=\"100,100\" filled=\"f\" strokecolor=\"" +
				context.strokeStyle + "\" strokeweight=\"" + context.lineWidth + "px\" path=\"m " +
				getCoordX(x, y) + "," + getCoordY(x, y) + " l " +
				getCoordX(x, h) + "," + getCoordY(x, h) + " " +
				getCoordX(w, h) + "," + getCoordY(w, h) + " " +
				getCoordX(w, y) + "," + getCoordY(w, y) + " x e\"><vml:stroke opacity=\"" +
				(context.globalAlpha * context.strokeAlpha) + "\" miterlimit=\"" +
				context.miterLimit + "\" joinstyle=\"" +
				context.lineJoin + "\" endcap=\"" +
				context.lineCap + "\"></vml:stroke></vml:shape>"
		);
		pathStack.clear();
	}

	/////////////////////////////////////////////////////////////////
	// SETTERS AND GETTERS
	/////////////////////////////////////////////////////////////////

	public void setGlobalAlpha(double globalAlpha) {
		context.globalAlpha = globalAlpha;
	}

	public double getGlobalAlpha() {
		return context.globalAlpha;
	}

	public void setGlobalCompositeOperation(String gco) {
		gco = gco.trim();
		if (gco.equalsIgnoreCase(Canvas.SOURCE_OVER)) {
			context.globalCompositeOperation = CanvasImplIE.SOURCE_OVER;
		} else if (gco.equalsIgnoreCase(Canvas.DESTINATION_OVER)) {
			if (!clearRectIndices.isEmpty()) {
				throw new IllegalStateException();
			}
			context.globalCompositeOperation = CanvasImplIE.DESTINATION_OVER;
		}
	}

	public String getGlobalCompositeOperation() {
		if (context.globalCompositeOperation == CanvasImplIE.DESTINATION_OVER) {
			return Canvas.DESTINATION_OVER;
		} else {
			return Canvas.SOURCE_OVER;
		}
	}

	public void setStrokeStyle(String strokeStyle) {
		strokeStyle = strokeStyle.trim();
		if (strokeStyle.startsWith("rgba(")) {
			int end = strokeStyle.indexOf(")", 12);
			if (end > -1) {
				String[] guts = strokeStyle.substring(5, end).split(",");
				if (guts.length == 4) {
					context.strokeAlpha  = Double.parseDouble(guts[3]);
					context.strokeStyle  = "rgb(" + guts[0] + "," + guts[1] + "," + guts[2] + ")";
					context.strokeStyle2 = strokeStyle;
				}
			}
		} else {
			context.strokeAlpha  = 1.0;
			context.strokeStyle  = strokeStyle;
			context.strokeStyle2 = null;
		}
	}

	public String getStrokeStyle() {
		if (context.strokeStyle2 == null) {
			return context.strokeStyle;
		} else {
			return context.strokeStyle2;
		}
	}

	public void setFillStyle(String fillStyle) {
		fillStyle = fillStyle.trim();
		if (fillStyle.startsWith("rgba(")) {
			int end = fillStyle.indexOf(")", 12);
			if (end > -1) {
				String[] guts = fillStyle.substring(5, end).split(",");
				if (guts.length == 4) {
					context.fillAlpha  = Double.parseDouble(guts[3]);
					context.fillStyle  = "rgb(" + guts[0] + "," + guts[1] + "," + guts[2] + ")";
					context.fillStyle2 = fillStyle;
				}
			}
		} else {
			context.fillAlpha  = 1.0;
			context.fillStyle  = fillStyle;
			context.fillStyle2 = null;
		}
	}

	public String getFillStyle() {
		if (context.fillStyle2 == null) {
			return context.fillStyle;
		} else {
			return context.fillStyle2;
		}
	}

	public void setLineWidth(double lineWidth) {
		context.lineWidth = lineWidth;
	}

	public double getLineWidth() {
		return context.lineWidth;
	}

	public void setLineCap(String lineCap) {
		if (lineCap.trim().equalsIgnoreCase(Canvas.BUTT)) {
			context.lineCap = CanvasImplIE.BUTT;
		} else {
			context.lineCap = lineCap;
		}
	}

	public String getLineCap() {
		if (context.lineCap == CanvasImplIE.BUTT) {
			return Canvas.BUTT;
		}
		return context.lineCap;
	}

	public void setLineJoin(String lineJoin) {
		context.lineJoin = lineJoin;
	}

	public String getLineJoin() {
		return context.lineJoin;
	}

	public void setMiterLimit(double miterLimit) {
		context.miterLimit = miterLimit;
	}

	public double getMiterLimit() {
		return context.miterLimit;
	}
}
