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

/**
 * The Opera implementation of the canvas widget.
 */
public class CanvasImplOpera extends CanvasImpl {

	public void clear() {
		super.beginPath();
		super.clear();
	}

	public void clearRect(double x, double y, double w, double h) {
		super.beginPath();
		super.clearRect(x, y, w, h);
	}

	public void fillRect(double x, double y, double w, double h) {
		super.beginPath();
		super.fillRect(x, y, w, h);
	}

	public void strokeRect(double x, double y, double w, double h) {
		super.beginPath();
		super.strokeRect(x, y, w, h);
	}
}