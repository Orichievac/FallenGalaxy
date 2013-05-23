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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.MouseWheelListener;
import com.google.gwt.user.client.ui.MouseWheelListenerCollection;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.SourcesMouseWheelEvents;
import com.google.gwt.user.client.ui.Widget;

/**
 * The <code>Canvas</code> is a widget that you use to draw arbitrary graphics.
 * <p>
 * When you want to draw a shape, you set the current path to that shape and
 * then paint the shape by stroking, filling, or both stroking and filling.
 * Stroking is the process of painting a line along the current path. Filling
 * is the process of painting the area contained within the path.
 * <p>
 * You use paths to draw both simple shapes (for example, lines, circles, or
 * rectangles) and complex shapes (such as the silhouette of a mountain range)
 * in a canvas. You can use a path to both draw the outline of a shape and fill
 * the inside of a shape. Prior to building the path, you can define properties
 * such as fillStyle or strokeStyle that can be used by drawing primitives to
 * fill or stroke the path.
 * <p>
 * When you close the path, the canvas connects the end of the last line
 * segment to the start of the first segment and terminates the current
 * subpath. If you don’t close the path by calling closePath before painting,
 * the path is implicitly closed for you by drawing primitives that fill or
 * clip (but it is not closed for stroking).
 * 
 * @see http://developer.mozilla.org/en/docs/Canvas_tutorial
 */
public class Canvas extends Widget implements SourcesClickEvents,
	SourcesMouseEvents, SourcesMouseWheelEvents {

	/**
	 * Use this constant as a parameter for the
	 * {@link #setGlobalCompositeOperation(String)} method.
	 */
	public final static String SOURCE_OVER = "source-over";

	/**
	 * Use this constant as a parameter for the
	 * {@link #setGlobalCompositeOperation(String)} method.
	 */
	public final static String DESTINATION_OVER = "destination-over";

	/**
	 * Use this constant as a parameter for the
	 * {@link #setLineCap(String)} method.
	 */
	public final static String BUTT = "butt";

	/**
	 * Use this constant as a parameter for the
	 * {@link #setLineCap(String)} method.
	 */
	public final static String SQUARE = "square";

	/**
	 * Use this constant either as a parameter for the
	 * {@link #setLineCap(String)} or the {@link #setLineJoin(String)} method.
	 */
	public final static String ROUND = "round";

	/**
	 * Use this constant as a parameter for the
	 * {@link #setLineJoin(String)} method.
	 */
	public final static String BEVEL = "bevel";

	/**
	 * Use this constant as a parameter for the
	 * {@link #setLineJoin(String)} method.
	 */
	public final static String MITER = "miter";

	/**
	 * Use this constant as a parameter for the
	 * {@link #setBackgroundColor(String)} method.
	 */
	public final static String TRANSPARENT = "";

	private CanvasImpl impl = (CanvasImpl) GWT.create(CanvasImpl.class);

	private ClickListenerCollection clickListeners;

	private MouseListenerCollection mouseListeners;

	private MouseWheelListenerCollection mouseWheelListeners;

	private boolean preventSelection = false;

	private int width;

	private int height;

	/**
	 * Creates a canvas widget with an initial drawing area of 300x150 pixels.
	 * <p>
	 * It's background color is an opaque white by default. The default CSS
	 * class selector is named <code>gwt-Canvas</code>. It may be used to
	 * apply additional style rules to the canvas widget.
	 */
	public Canvas() {
		this(300, 150);
	}

	/**
	 * Creates a canvas widget with the specified drawing area size.
	 * <p>
	 * It's background color is an opaque white by default. The default CSS
	 * class selector is named <code>gwt-Canvas</code>. It may be used to
	 * apply additional style rules to the canvas widget.
	 * 
	 * @param width the width of the canvas drawing area in pixels
	 * @param height the height of the canvas drawing area in pixels
	 */
	public Canvas(int width, int height) {
		setElement(DOM.createElement("canvas"));
		impl.init(this);
		setStyleName("gwt-Canvas");
		setBackgroundColor("#fff");
		setWidth(width);
		setHeight(height);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		impl.drawLine(x1, y1, x2, y2);
	}

	public void drawCircle(int x, int y, double radius) {
		impl.drawCircle(x, y, radius);
	}
	
	/**
	 * Sets the background color of the canvas widget.
	 * <p>
	 * You can set it in several different ways depending on the color space
	 * you intend to use. For web-safe colors, pass a web color specification
	 * string of the form <code>"#RRGGBB"</code>, which represents an RGB color
	 * using hexidecimal numbers. If you want to make the background
	 * transparent, simply pass <code>Canvas.TRANSPARENT</code> as a parameter.
	 * <p>
	 * Please note that using a transparent background will break the
	 * {@link #clearRect(double, double, double, double)} method. If you need this
	 * particular method, you must not use a transparent background at the
	 * same time. As a workaround please either use an opaque background color
	 * or refrain from using the {@link #clearRect(double, double, double, double)}
	 * method and use {@link #clear()} instead.
	 * <p>
	 * The widget's background color is <b>not</b> part of the drawing state.
	 * 
	 * @param color <code>"#RRGGBB"</code> to specify a color or
	 * <code>Canvas.TRANSPARENT</code> if a transparent background is wanted
	 */
	public void setBackgroundColor(String color) {
		if (color == null) {
			throw new IllegalArgumentException();
		}
		color = color.trim();
		if (color.startsWith("rgba(")) {
			int end = color.indexOf(")", 12);
			if (end > -1) {
				String[] guts = color.substring(5, end).split(",");
				if (guts.length >= 3) {
					color = "rgb(" + guts[0] + "," + guts[1] + "," + guts[2] + ")";
				}
			}
		}
		impl.setBackgroundColor(color);
	}

	/**
	 * Returns the current background color of the canvas.
	 * <p>
	 * If the canvas background is transparent, <code>Canvas.TRANSPARENT</code>
	 * will be returned.
	 * 
	 * @return the current background color or <code>Canvas.TRANSPARENT</code>
	 */
	public String getBackgroundColor() {
		return impl.getBackgroundColor();
	}

	/**
	 * This method is deprecated. Please use {@link #setHeight(int)} instead.
	 * 
	 * @param height the height of the drawing area
	 */
	@Deprecated
	public void setHeight(String height) {
		if (height == null) {
			throw new IllegalArgumentException();
		}
		height = height.trim();
		int h = 0;
		if (height.endsWith("px")) {
			h = 2;
		}
		try {
			h = Integer.parseInt(height.substring(0, height.length() - h));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		setHeight(h);
	}

	/**
	 * Specifies the height of the drawing area.
	 * <p>
	 * You can specify this value as a fixed number of pixels only.
	 * 
	 * @param height the height of the drawing area
	 */
	public void setHeight(int height) {
		this.height = height;
		impl.setHeight(height);
	}

	/**
	 * Returns the height of the canvas drawing area in pixels.
	 * <p>
	 * Unlike {@link #getOffsetHeight()} the returned value represents the
	 * height of the actual drawing area, excluding decorations such as
	 * border, margin, and padding.
	 * 
	 * @return the height of the drawing area
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * This method is deprecated. Please use {@link #setWidth(int)} instead.
	 * 
	 * @param width the width of the drawing area
	 */
	@Deprecated
	public void setWidth(String width) {
		if (width == null) {
			throw new IllegalArgumentException();
		}
		width = width.trim();
		int w = 0;
		if (width.endsWith("px")) {
			w = 2;
		}
		try {
			w = Integer.parseInt(width.substring(0, width.length() - w));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		setWidth(w);
	}

	/**
	 * Specifies the width of the drawing area.
	 * <p>
	 * You can specify this value as a fixed number of pixels only.
	 * 
	 * @param width the width of the drawing area
	 */
	public void setWidth(int width) {
		this.width = width;
		impl.setWidth(width);
	}

	/**
	 * Returns the width of the canvas drawing area in pixels.
	 * <p>
	 * Unlike {@link #getOffsetWidth()} the returned value represents the
	 * width of the actual drawing area, excluding decorations such as
	 * border, margin, and padding.
	 * 
	 * @return the width of the drawing area
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Specifies the browsers selection behavior on mouse events.
	 * <p>
	 * If you press the mouse button on the canvas surface and move the cursor
	 * outside the canvas while holding the button down, the browser usually
	 * selects whatever comes underneath the mouse cursor. If this behavior is
	 * not wanted, it can be suppressed by passing <code>true</code>, and be
	 * restored to it's default by passing <code>false</code> as a parameter.
	 * 
	 * @param value
	 */
	public void preventSelection(boolean value) {
		preventSelection = value;
	}

	/**
	 * Returns <code>true</code> if selections are prevented on mouse events,
	 * or <code>false</code> if they are not prevented (the default behavior).
	 * 
	 * @return
	 */
	public boolean isSelectionPrevented() {
		return preventSelection;
	}

	/////////////////////////////////////////////////////////////////
	// EVENT HANDLING
	/////////////////////////////////////////////////////////////////

	/**
	 * Fired whenever a browser event is received.
	 * 
	 * @param event
	 * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(Event)
	 */
	public void onBrowserEvent(Event event) {
		if (event == null) {
			throw new IllegalArgumentException();
		}
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
			if (preventSelection) {
				impl.onMouseDown(event);
			}
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEOVER:
		case Event.ONMOUSEOUT:
			if (mouseListeners != null) {
				mouseListeners.fireMouseEvent(this, event);
			}
			break;
		case Event.ONMOUSEUP:
			if (preventSelection) {
				impl.onMouseUp();
			}
			if (mouseListeners != null) {
				mouseListeners.fireMouseEvent(this, event);
			}
			break;
		case Event.ONMOUSEWHEEL:
			if (mouseWheelListeners != null) {
				mouseWheelListeners.fireMouseWheelEvent(this, event);
			}
			break;
		case Event.ONCLICK:
			if (clickListeners != null) {
				clickListeners.fireClick(this);
			}
			break;
		}
	}

	/**
	 * Adds a listener interface to receive mouse click events such as
	 * <code>Event.ONCLICK</code>.
	 * 
	 * @param listener
	 * @see com.google.gwt.user.client.ui.SourcesClickEvents#addClickListener(ClickListener)
	 */
	public void addClickListener(ClickListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (clickListeners == null) {
			clickListeners = new ClickListenerCollection();
			sinkEvents(Event.ONCLICK);
		}
		clickListeners.add(listener);
	}

	/**
	 * Removes a previously added listener interface.
	 * 
	 * @param listener
	 * @see com.google.gwt.user.client.ui.SourcesClickEvents#removeClickListener(ClickListener)
	 */
	public void removeClickListener(ClickListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (clickListeners != null) {
			clickListeners.remove(listener);
		}
	}

	/**
	 * Adds a listener interface to receive mouse events such as:
	 * <ul>
	 * <li><code>Event.ONMOUSEDOWN</code>
	 * <li><code>Event.ONMOUSEUP</code>
	 * <li><code>Event.ONMOUSEMOVE</code>
	 * <li><code>Event.ONMOUSEOVER</code>
	 * <li><code>Event.ONMOUSEOUT</code>
	 * </ul>
	 * 
	 * @param listener
	 * @see com.google.gwt.user.client.ui.SourcesMouseEvents#addMouseListener(MouseListener)
	 */
	public void addMouseListener(MouseListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (mouseListeners == null) {
			mouseListeners = new MouseListenerCollection();
			sinkEvents(Event.MOUSEEVENTS);
		}
		mouseListeners.add(listener);
	}

	/**
	 * Removes a previously added listener interface.
	 * 
	 * @param listener
	 * @see com.google.gwt.user.client.ui.SourcesMouseEvents#removeMouseListener(MouseListener)
	 */
	public void removeMouseListener(MouseListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (mouseListeners != null) {
			mouseListeners.remove(listener);
		}
	}

	/**
	 * Adds a listener interface to receive mouse wheel events such as
	 * <code>Event.ONMOUSEWHEEL</code>.
	 * 
	 * @param listener
	 * @see com.google.gwt.user.client.ui.SourcesMouseWheelEvents#addMouseWheelListener(MouseWheelListener)
	 */
	public void addMouseWheelListener(MouseWheelListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (mouseWheelListeners == null) {
			mouseWheelListeners = new MouseWheelListenerCollection();
			sinkEvents(Event.ONMOUSEWHEEL);
		}
		mouseWheelListeners.add(listener);
	}

	/**
	 * Removes a previously added listener interface.
	 * 
	 * @param listener
	 * @see com.google.gwt.user.client.ui.SourcesMouseWheelEvents#removeMouseWheelListener(MouseWheelListener)
	 */
	public void removeMouseWheelListener(MouseWheelListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (mouseWheelListeners != null) {
			mouseWheelListeners.remove(listener);
		}
	}

	/////////////////////////////////////////////////////////////////
	// CANVAS STATE METHODS
	/////////////////////////////////////////////////////////////////

	/**
	 * Restores the current graphics state to the state most recently saved.
	 * <p>
	 * If you wish to save the settings of the current drawing environment,
	 * that is, the current graphics state, you can call the {@link #save()}
	 * method. When you call {@link #save()}, the canvas saves the current
	 * graphics state to the top of the graphics state stack.
	 * <p>
	 * To restore your drawing environment to a previously saved state, you can
	 * use this method. When you call {@link #restore()}, the canvas removes
	 * the most recently saved graphics state from the top of the stack and
	 * uses that state’s saved settings for the current graphics state.
	 */
	public void restore() {
		impl.restore();
	}

	/**
	 * Saves a copy of the current graphics state.
	 * <p>
	 * The graphics state contains data describing the current drawing
	 * environment. Methods that draw to the canvas use the graphics state
	 * settings to determine how to render their results.
	 * <p>
	 * Each canvas maintains a stack of graphics states. If you wish to save
	 * the settings of the current drawing environment, that is, the current
	 * graphics state, you can call the {@link #save()} method. When you call
	 * {@link #save()}, the canvas object saves the current graphics state
	 * to the top of the graphics state stack.
	 * <p>
	 * To restore your drawing environment to a previously saved state, you can
	 * use the {@link #restore()} method. When you call {@link #restore()},
	 * the canvas removes the most recently saved graphics state from the top
	 * of the stack and uses that state’s saved settings for the current
	 * graphics state.
	 * <p>
	 * Note that not all aspects of the current drawing environment are
	 * elements of the graphics state. For example, the current path is not
	 * considered part of the graphics state and is therefore not saved when
	 * you call this method.
	 */
	public void save() {
		impl.save();
	}

	/**
	 * Rotates the user coordinate system of the canvas.
	 * <p>
	 * The current transformation matrix (CTM) specifies the mapping from
	 * device-independent user space coordinates to a device space. By
	 * modifying the CTM, you can modify (scale, translate, rotate) the objects
	 * you draw. It’s important to note the order of events necessary to
	 * transform an object in a graphics context. Prior to drawing the object,
	 * you must first transform the coordinate space of the context (by calling
	 * this method), and then draw the object.
	 * <p>
	 * For example, to rotate an image, you must call this method to rotate the
	 * coordinate space of the context before drawing the image. When you draw
	 * the image, the canvas draws to the window using the rotated coordinate
	 * system. You specify both the magnitude and direction of the rotation by
	 * specifying an angle of adjustment in radians.
	 * <p>
	 * To restore the previous coordinate space, you must save the graphics
	 * state before modifying the CTM, and restore the graphics state after
	 * drawing.
	 * 
	 * @param angle specifies the amount of coordinate-space rotation and is
	 * measured in radians
	 */
	public void rotate(double angle) {
		impl.rotate(angle);
	}

	/**
	 * Changes the scale of the canvas coordinate system.
	 * <p>
	 * The current transformation matrix (CTM) specifies the mapping from
	 * device independent user space coordinates to a device space. By
	 * modifying the CTM, you can modify (scale, translate, rotate) the objects
	 * you draw. It is important to note the order of events necessary to
	 * transform an object in a graphics context. Prior to drawing the object,
	 * you must first transform the coordinate space of the context (by calling
	 * this method), and then draw the object.
	 * <p>
	 * Scaling operations modify the x- and y-coordinates by a given scaling
	 * factor. The magnitude of the x and y factors governs whether the new
	 * coordinates are larger or smaller than the original. For example,
	 * specifying the value 2.0 for the <code>x</code> parameter causes
	 * subsequently drawn objects to appear at twice their specified width. In
	 * addition, by making the x factor negative, you can flip the coordinates
	 * about the y-axis; similarly, you can flip coordinates about the x-axis
	 * by making the y factor negative.
	 * <p>
	 * To restore the previous coordinate space, you must save the graphics
	 * state before modifying the CTM, and restore the graphics state after
	 * drawing.
	 * 
	 * @param x contains a double value with the x-axis scale factor
	 * @param y contains a double value with the y-axis scale factor
	 */
	public void scale(double x, double y) {
		impl.scale(x, y);
	}

	/**
	 * Changes the origin of the canvas coordinate system.
	 * <p>
	 * The current transformation matrix (CTM) specifies the mapping from
	 * device-independent user space coordinates to a device space. By
	 * modifying the CTM, you can modify (scale, translate, rotate) the objects
	 * you draw. It’s important to note the order of events necessary to
	 * transform an object in a graphics context. Prior to drawing the object,
	 * you must first transform the coordinate space of the page (by calling
	 * this method), and then draw the object.
	 * <p>
	 * This method displaces the x- and y-axes (thus, the origin) of the
	 * coordinate system by the values you specify. When you draw into this
	 * adjusted coordinate space, the x- and y-coordinates of your drawing
	 * are also displaced.
	 * <p>
	 * To restore the previous coordinate space, you must save the graphics
	 * state before modifying the CTM, and restore the graphics state after
	 * drawing.
	 * 
	 * @param x contains a double value with the x-axis translation value
	 * @param y contains a double value with the y-axis translation value
	 */
	public void translate(double x, double y) {
		impl.translate(x, y);
	}

	/**
	 * THIS METHOD IS NOT SUPPORTED! DO NOT USE IT!
	 * <p>
	 * Multiplies the given matrix with the current transformation matrix.
	 * 
	 * @param m11
	 * @param m12
	 * @param m21
	 * @param m22
	 * @param dx
	 * @param dy
	 */
	@Deprecated
	public void transform(double m11, double m12, double m21, double m22, double dx, double dy) {
		impl.transform(m11, m12, m21, m22, dx, dy);
	}

	/**
	 * THIS METHOD IS NOT SUPPORTED! DO NOT USE IT!
	 * <p>
	 * Sets the current transformation matrix to the given matrix.
	 * 
	 * @param m11
	 * @param m12
	 * @param m21
	 * @param m22
	 * @param dx
	 * @param dy
	 */
	@Deprecated
	public void setTransform(double m11, double m12, double m21, double m22, double dx, double dy) {
		impl.setTransform(m11, m12, m21, m22, dx, dy);
	}

	/////////////////////////////////////////////////////////////////
	// WORKING WITH PATHS
	/////////////////////////////////////////////////////////////////

	/**
	 * Adds an arc of a circle to the current subpath.
	 * <p>
	 * The arc is built based on the circle whose origin and radius are
	 * specified by the <code>x</code>, <code>y</code>, and <code>radius</code>
	 * parameters. The <code>startAngle</code> parameter specifies the angle of
	 * the starting point of the arc, measured in radians from the positive
	 * x-axis. The <code>endAngle</code> parameter specifies the angle of the
	 * endpoint of the arc, measured in radians from the positive x-axis.
	 * Specify <code>false</code> for the <code>anticlockwise</code> parameter
	 * to draw the arc in a clockwise direction; otherwise specify
	 * <code>true</code>.
	 * <p>
	 * If the current path already contains a subpath, the canvas appends a
	 * straight line segment from the current point to the starting point of
	 * the arc. If the current path is empty, the canvas creates a new subpath
	 * for the arc and does not add an initial line segment. After adding the
	 * arc, the current point is set to the endpoint of the arc.
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @param startAngle
	 * @param endAngle
	 * @param anticlockwise
	 */
	public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
		impl.arc(x, y, radius, startAngle, endAngle, anticlockwise);
	}

	/**
	 * Appends a cubic Bézier curve to the current path.
	 * <p>
	 * A cubic curve segment has a start point, two control points, and an
	 * endpoint. The start point is the current endpoint of the open path. The
	 * <code>cp1x</code>, <code>cp1y</code>, <code>cp2x</code>, and
	 * <code>cp2y</code> parameters specify the two control points for the
	 * path. The <code>x</code> and <code>y</code> parameters specify the new
	 * endpoint for the path. After adding the segment, the current point is
	 * reset from the beginning of the new segment to the endpoint of that
	 * segment.
	 * 
	 * @param cp1x
	 * @param cp1y
	 * @param cp2x
	 * @param cp2y
	 * @param x
	 * @param y
	 */
	public void cubicCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
		impl.cubicCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
	}

	/**
	 * Appends a quadratic Bézier curve to the current path.
	 * <p>
	 * A quadratic curve segment has a start point, one control point, and an
	 * endpoint. The start point is the current point of the canvas. The
	 * <code>cpx</code> and <code>cpy</code> parameters specify the control
	 * point. The <code>x</code> and <code>y</code> parameters specify the new
	 * endpoint. After adding the segment, the current point is reset from the
	 * beginning of the new segment to the endpoint of that segment.
	 * 
	 * @param cpx
	 * @param cpy
	 * @param x
	 * @param y
	 */
	public void quadraticCurveTo(double cpx, double cpy, double x, double y) {
		impl.quadraticCurveTo(cpx, cpy, x, y);
	}

	/**
	 * Creates a new empty path in the canvas.
	 * <p>
	 * You use paths to draw both simple shapes (for example, lines, circles,
	 * or rectangles) and complex shapes (such as the silhouette of a mountain
	 * range) in a canvas. You can use a path to both draw the outline of a
	 * shape and fill the inside of a shape.
	 * <p>
	 * Before painting a shape, you must create the shape to be painted using
	 * the current path. You build a path from a set of subpaths, each of which
	 * is a list of one or more segments, either straight lines or curves.
	 * <p>
	 * A canvas can have only a single path in use at any time. Therefore, if
	 * the specified context already contains a current path when you call this
	 * method, the canvas replaces the previous current path with the new path.
	 * In this case, the canvas discards the old path and any data associated
	 * with it.
	 * <p>
	 * Note: The current path is not part of the graphics state. Consequently,
	 * saving and restoring the graphics state has no effect on the current
	 * path.
	 */
	public void beginPath() {
		impl.beginPath();
	}

	/**
	 * Closes and terminates an open subpath.
	 * <p>
	 * When a subpath is open and you call this method, the canvas closes the
	 * subpath (draws a straight line that connects the current point to the
	 * starting point), and terminates the subpath (the current point is no
	 * longer defined).
	 * <p>
	 * If no subpath is open, calling this method does nothing.
	 * <p>
	 * Note: You can stroke along an open subpath. When a subpath is open and
	 * you fill, however, the canvas implicitly closes the subpath for you.
	 */
	public void closePath() {
		impl.closePath();
	}

	/**
	 * Begins a new subpath at the point you specify.
	 * <p>
	 * Before painting a shape in the canvas, you must create the shape to be
	 * painted using the current path. You build a path from a set of subpaths,
	 * each of which is a list of one or more segments, either straight lines
	 * or curves.
	 * <p>
	 * This method begins a newsubpath starting at the point you specify with
	 * the <code>x</code> and <code>y</code> parameters. This point is defined
	 * to be the "current" point, and it defines the starting point of the next
	 * line segment. The canvas sets the current point in one of two ways:
	 * <ul>
	 * <li>Explicitly, when you call this method to begin a new subpath at a
	 * given point
	 * <li>Implicitly, when you add a new curve or straight line segment to the
	 * subpath; after adding the segment, the current point is reset from the
	 * beginning of the new segment to the endpoint of that segment
	 * </ul>
	 * 
	 * @param x
	 * @param y
	 */
	public void moveTo(double x, double y) {
		impl.moveTo(x, y);
	}

	/**
	 * Appends a straight line segment from the current point to the point you specify.
	 * <p>
	 * You can use straight line segments, cubic and quadratic Bézier curve
	 * segments, and rectangles to specify a path. You can append a single
	 * straight line segment to the current subpath using this method. After
	 * adding the line segment, the current point is reset from the beginning
	 * of the new line segment to the endpoint of that line segment, as
	 * specified by the <code>x</code> and <code>y</code> parameters.
	 * 
	 * @param x
	 * @param y
	 */
	public void lineTo(double x, double y) {
		impl.lineTo(x, y);
	}

	/**
	 * Adds a new subpath consisting of a single rectangle to the canvas.
	 * <p>
	 * The parameters of this method all contain double values.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void rect(double x, double y, double w, double h) {
		impl.rect(x, y, w, h);
	}

	/////////////////////////////////////////////////////////////////
	// STROKING AND FILLING
	/////////////////////////////////////////////////////////////////

	/**
	 * Clears the entire canvas.
	 * <p>
	 * When you call this method, the canvas effectively "erases" all of it's
	 * contents, if any.
	 */
	public void clear() {
		impl.clear();
	}

	/**
	 * Paints the area within the current path.
	 * <p>
	 * The fill color is an attribute of the graphics state. You can set the
	 * current fill color by setting a value with the
	 * {@link #setFillStyle(String)} method.
	 * <p>
	 * When you fill the current path, the canvas fills each subpath
	 * independently. Any subpath that has not been explicitly closed is closed
	 * implicitly by the fill routines.
	 */
	public void fill() {
		impl.fill();
	}

	/**
	 * Paints a line along the current path.
	 * <p>
	 * To modify the behavior of this method, you can change any of the
	 * following graphics state properties with these methods:
	 * <ul>
	 * <li>{@link #setLineWidth(double)}
	 * <li>{@link #setLineJoin(String)}
	 * <li>{@link #setLineCap(String)}
	 * <li>{@link #setMiterLimit(double)}
	 * <li>{@link #setStrokeStyle(String)}
	 * <li>{@link #setGlobalAlpha(double)}
	 * </ul>
	 */
	public void stroke() {
		impl.stroke();
	}

	/**
	 * Clears the contents of the specified rectangle.
	 * <p>
	 * When you call this method, the canvas effectively "erases"
	 * the contents of the specified rectangle. Like
	 * {@link #strokeRect(double, double, double, double)} and
	 * {@link #fillRect(double, double, double, double)}, all transformations
	 * will be applied to the parameters of this method.
	 * <p>
	 * Please note that this method only works correctly if a) the global
	 * composite operation is set to <code>Canvas.SOURCE_OVER</code>
	 * and b) the canvas background is set to a fully opaque color.
	 * If you prefer to use a transparent background and/or work in
	 * <code>Canvas.DESTINATION_OVER</code> mode, you must not use this method
	 * (however, you may use {@link #clear()} instead)!
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void clearRect(double x, double y, double w, double h) {
		String gco = impl.getGlobalCompositeOperation();
		String bgc = impl.getBackgroundColor();
		if (!gco.equalsIgnoreCase(Canvas.SOURCE_OVER) || bgc.equals(Canvas.TRANSPARENT)) {
			throw new IllegalStateException();
		}
		impl.clearRect(x, y, w, h);
	}

	/**
	 * Paints the area within the specified rectangle.
	 * <p>
	 * This method uses the current fill color to paint the area of the
	 * specified rectangle. The parameters of this method all contain double
	 * values.
	 * <p>
	 * As a side effect of calling this method, the canvas clears the current
	 * path.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void fillRect(double x, double y, double w, double h) {
		impl.fillRect(x, y, w, h);
	}

	/**
	 * Paints an outline of a rectangle.
	 * <p>
	 * This method uses the current stroke color to paint the path represented
	 * by the specified rectangle. The parameters of this method all contain
	 * double values.
	 * <p>
	 * To alter the appearance of the painted outline, you can modify the
	 * following attributes of the graphics state:
	 * <ul>
	 * <li>{@link #setLineWidth(double)}
	 * <li>{@link #setLineJoin(String)}
	 * <li>{@link #setLineCap(String)}
	 * <li>{@link #setMiterLimit(double)}
	 * <li>{@link #setStrokeStyle(String)}
	 * <li>{@link #setGlobalAlpha(double)}
	 * </ul>
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void strokeRect(double x, double y, double w, double h) {
		impl.strokeRect(x, y, w, h);
	}

	/////////////////////////////////////////////////////////////////
	// GRADIENT AND PATTERN STYLES
	/////////////////////////////////////////////////////////////////

	/**
	 * METHOD NOT IMPLEMENTED YET!
	 * <p>
	 * Returns a gradient object representing a linear gradient.
	 * <p>
	 * This method takes in two coordinates, <code>(x0, y0)</code> and
	 * <code>(x1, y1)</code>, and returns an object that represents a gradient
	 * between them.
	 * <p>
	 * Use <code>addColorStop()</code> to add colors and offsets to a gradient.
	 * The 0 offset in this case is the start of the gradient
	 * <code>(x0, y0)</code> while the 1 offset is the end of the gradient
	 * <code>(x1, y1)</code>.
	 * <p>
	 * The returned object can be assigned to the <code>fillStyle</code> and
	 * <code>strokeStyle</code> properties or used in comparisons with them.
	 * 
	 * @return
	 */
	public Object createLinearGradient(double x0, double y0, double x1, double y1) {
		return impl.createLinearGradient(x0, y0, x1, y1);
	}

	/**
	 * METHOD NOT IMPLEMENTED YET!
	 * <p>
	 * Returns a pattern object representing a repeating pattern.
	 * <p>
	 * This method takes an image and a repetition style and produces a pattern
	 * style that you can use when filling in your shapes. The repetition
	 * parameter accepts a string as its value:
	 * <ul>
	 * <li><code>"repeat"</code>
	 * <li><code>"repeat-x"</code>
	 * <li><code>"repeat-y"</code>
	 * <li><code>"no-repeat"</code>
	 * </ul>
	 * The returned object can be assigned to the <code>fillStyle</code> and
	 * <code>strokeStyle</code> properties or used in comparisons with them.
	 * 
	 * @return
	 */
	public Object createPattern(Image image, String repetition) {
		return impl.createPattern(image, repetition);
	}

	/**
	 * METHOD NOT IMPLEMENTED YET!
	 * <p>
	 * Returns a gradient object representing a radial gradient.
	 * <p>
	 * This method takes in two coordinates, <code>(x0, y0)</code> and
	 * <code>(x1, y0)</code> and corresponding radii. It creates two circles
	 * using the coordinates and the radii provided and returns an object that
	 * has a gradient between the edges of the circles.
	 * <p>
	 * Use <code>addColorStop()</code> to add colors and offsets to a gradient.
	 * The 0 offset in this case is the circumference of the first circle
	 * <code>(x0, y0, r0)</code> while the 1 offset is the circumference of the
	 * second circle <code>(x1, y1, r1)</code>.
	 * <p>
	 * The returned object can be assigned to the <code>fillStyle</code> and
	 * <code>strokeStyle</code> properties or used in comparisons with them.
	 * 
	 * @return
	 */
	public Object createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
		return impl.createRadialGradient(x0, y0, r0, x1, y1, r1);
	}

	/////////////////////////////////////////////////////////////////
	// DRAWING IMAGES
	/////////////////////////////////////////////////////////////////

	/**
	 * METHOD NOT IMPLEMENTED YET!
	 * <p>
	 * See {@link #drawImage(Image, double, double, double, double, double, double, double, double)}
	 * for a fully detailed description.
	 */
	public void drawImage(Image image, double x, double y) {
		impl.drawImage(image, x, y);
	}

	/**
	 * METHOD NOT IMPLEMENTED YET!
	 * <p>
	 * See {@link #drawImage(Image, double, double, double, double, double, double, double, double)}
	 * for a fully detailed description.
	 */
	public void drawImage(Image image, double x, double y, double width, double height) {
		impl.drawImage(image, x, y, width, height);
	}

	/**
	 * METHOD NOT IMPLEMENTED YET!
	 * <p>
	 * Draws an image in the specified rectangle.
	 * <p>
	 * This method is overloaded with three variants, used to draw the contents
	 * of a JavaScript Image object into the context.
	 * <p>
	 * The first of these, <code>drawImage(image, x, y)</code>, draws the image
	 * at the <code>x</code> and <code>y</code> coordinates within the context.
	 * The image is sized as it is in the object.
	 * <p>
	 * The second method, <code>drawImage(image, x, y, width, height)</code>,
	 * is where <code>x</code>, <code>y</code>, <code>width</code>, and
	 * <code>height</code> parameters contain values representing the bounding
	 * rectangle for the image. These values are specified in the coordinate
	 * system of the canvas. If the specified coordinates lie outside the
	 * canvas bounds, the image will be clipped.
	 * <p>
	 * The third method, <code>context.drawImage(image, sx, sy, swidth,
	 * sheight, dx, dy, dwidth, dheight)</code>, draws the portion of the image
	 * specified by the source rectangle (<code>sx, sy, swidth</code>, and
	 * <code>sheight</code>) onto the canvas at the specified destination
	 * rectangle (<code>dx, dy, dwidth, dheight</code>). The source rectangle
	 * is specified in the image coordinate space and the destination rectangle
	 * is specified in the canvas coordinate space.
	 */
	public void drawImage(Image image, double sx, double sy, double swidth, double sheight,
			double dx, double dy, double dwidth, double dheight) {
		impl.drawImage(image, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
	}

	/////////////////////////////////////////////////////////////////
	// SETTERS AND GETTERS
	/////////////////////////////////////////////////////////////////

	/**
	 * A double value indicating the alpha channel value, which determines the
	 * opacity of content drawn on the canvas. The range of values is between
	 * 0.0 (fully transparent) and 1.0 (no additional transparency). By
	 * default, this parameter’s value is 1.0.
	 * <p>
	 * The canvas uses the alpha value in the current graphics state to
	 * determine how to composite newly painted objects.
	 * 
	 * @param globalAlpha
	 */
	public void setGlobalAlpha(double globalAlpha) {
		impl.setGlobalAlpha(globalAlpha);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setGlobalAlpha(double)
	 */
	public double getGlobalAlpha() {
		return impl.getGlobalAlpha();
	}

	/**
	 * Determines how the canvas is displayed relative to any background
	 * content. The string identifies the desired compositing mode. If you do
	 * not set this value explicitly, the canvas uses the
	 * <code>Canvas.SOURCE_OVER</code> compositing mode.
	 * <p>
	 * The valid compositing operators are:
	 * <ul>
	 * <li><code>Canvas.SOURCE_OVER</code>
	 * <li><code>Canvas.DESTINATION_OVER</code>
	 * </ul>
	 * <p>
	 * Please note that using the <code>Canvas.DESTINATION_OVER</code>
	 * operation may break the {@link #clearRect(double, double, double, double)}
	 * method in Internet Explorer when a new shape is drawn underneath a
	 * previously cleared area.
	 * As a workaround please only use <code>Canvas.DESTINATION_OVER</code>
	 * in conjunction with the {@link #clear()} method OR simply stick to the
	 * <code>Canvas.SOURCE_OVER</code> operation if you'd like to use the
	 * {@link #clearRect(double, double, double, double)} method.
	 * 
	 * @param globalCompositeOperation
	 */
	public void setGlobalCompositeOperation(String globalCompositeOperation) {
		if (globalCompositeOperation == null) {
			throw new IllegalArgumentException();
		}
		impl.setGlobalCompositeOperation(globalCompositeOperation);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setGlobalCompositeOperation(String)
	 */
	public String getGlobalCompositeOperation() {
		return impl.getGlobalCompositeOperation();
	}

	/**
	 * The color or style the canvas applies when stroking paths. When you set
	 * this property, the canvas sets the stroke style parameter of the
	 * graphics state.
	 * <p>
	 * If you intend for the stroke style to be a color, you can set it in
	 * several different ways depending on the color space you intend to use.
	 * For web-safe colors, pass a web color specification string of the form
	 * <code>"#RRGGBB"</code>, which represents an RGB color using hexidecimal
	 * numbers.
	 * <p>
	 * If you want the shape stroke to have an alpha, use the CSS
	 * <code>rgba(r, g, b, alpha)</code> functional-notation style. Use double
	 * values between 0 and 255 for the <code>r</code>, <code>g</code>, and
	 * <code>b</code> parameters. The <code>alpha</code> parameter contains a
	 * double value, between 0.0 and 1.0, indicating the alpha channel value,
	 * which determines the opacity of the color.
	 * <p>
	 * You can also set the stroke style to be a gradient or pattern. Use the
	 * <code>createLinearGradient</code>, <code>createRadialGradient</code>,
	 * and <code>createPattern</code> methods to define a style that you can
	 * apply to this property.
	 * 
	 * @param strokeStyle
	 */
	public void setStrokeStyle(String strokeStyle) {
		if (strokeStyle == null) {
			throw new IllegalArgumentException();
		}
		impl.setStrokeStyle(strokeStyle);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setStrokeStyle(String)
	 */
	public String getStrokeStyle() {
		return impl.getStrokeStyle();
	}

	/**
	 * The color or style the canvas applies when filling paths. When you set
	 * this property, the canvas sets the fill style parameter of the graphics
	 * state.
	 * <p>
	 * If you intend for the fill style to be a color, you can set it in
	 * several different ways depending on the color space you intend to use.
	 * For web-safe colors, pass a web color specification string of the form
	 * <code>"#RRGGBB"</code>, which represents an RGB color using hexidecimal
	 * numbers.
	 * <p>
	 * If you want the shape fill to have an alpha, use the CSS
	 * <code>rgba(r, g, b, alpha)</code> functional-notation style. Use integer
	 * values between 0 and 255 for the <code>r</code>, <code>g</code>, and
	 * <code>b</code> parameters. The <code>alpha</code> parameter contains a
	 * double value, between 0.0 and 1.0, indicating the alpha channel value,
	 * which determines the opacity of the color.
	 * <p>
	 * You can also set the fill style to be a gradient or pattern. Use the
	 * <code>createLinearGradient</code>, <code>createRadialGradient</code>,
	 * and <code>createPattern</code> methods to define a style that you can
	 * apply to this property.
	 * 
	 * @param fillStyle
	 */
	public void setFillStyle(String fillStyle) {
		if (fillStyle == null) {
			throw new IllegalArgumentException();
		}
		impl.setFillStyle(fillStyle);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setFillStyle(String)
	 */
	public String getFillStyle() {
		return impl.getFillStyle();
	}

	/**
	 * A double value indicating the line width for drawing operations. This
	 * value must be greater than 0. You can affect the width of lines and
	 * curves that the canvas draws by modifying the line width property of the
	 * graphics state. The line width is the total width of the line, expressed
	 * in units of the user space. The line surrounds the center of the path,
	 * with half of the total width on either side.
	 * 
	 * @param lineWidth
	 */
	public void setLineWidth(double lineWidth) {
		impl.setLineWidth(lineWidth);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setLineWidth(double)
	 */
	public double getLineWidth() {
		return impl.getLineWidth();
	}

	/**
	 * A string value that determines the end style used when drawing a line.
	 * Specify the string <code>Canvas.BUTT</code> for a flat edge that is
	 * perpendicular to the line itself, <code>Canvas.ROUND</code> for round
	 * endpoints, or <code>Canvas.SQUARE</code> for square endpoints. If you
	 * do not set this value explicitly, the canvas uses the
	 * <code>Canvas.BUTT</code> line cap style.
	 * 
	 * @param lineCap
	 */
	public void setLineCap(String lineCap) {
		if (lineCap == null) {
			throw new IllegalArgumentException();
		}
		impl.setLineCap(lineCap);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setLineCap(String)
	 */
	public String getLineCap() {
		return impl.getLineCap();
	}

	/**
	 * A string value that determines the join style between lines. Specify
	 * the string <code>Canvas.ROUND</code> for round joins,
	 * <code>Canvas.BEVEL</code> for beveled joins, or
	 * <code>Canvas.MITER</code> for miter joins. If you do not set this
	 * value explicitly, the canvas uses the <code>Canvas.MITER</code>
	 * line join style.
	 * 
	 * @param lineJoin
	 */
	public void setLineJoin(String lineJoin) {
		if (lineJoin == null) {
			throw new IllegalArgumentException();
		}
		impl.setLineJoin(lineJoin);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setLineJoin(String)
	 */
	public String getLineJoin() {
		return impl.getLineJoin();
	}

	/**
	 * A double value with the new miter limit. You use this property to specify
	 * how the canvas draws the juncture between connected line segments. If
	 * the line join is set to <code>Canvas.MITER</code>, the canvas uses the
	 * miter limit to determine whether the lines should be joined with a bevel
	 * instead of a miter. The canvas divides the length of the miter by the
	 * line width. If the result is greater than the miter limit, the style is
	 * converted to a bevel.
	 * 
	 * @param miterLimit
	 */
	public void setMiterLimit(double miterLimit) {
		impl.setMiterLimit(miterLimit);
	}

	/**
	 * See setter method for a fully detailed description.
	 * 
	 * @return
	 * @see Canvas#setMiterLimit(double)
	 */
	public double getMiterLimit() {
		return impl.getMiterLimit();
	}
}
