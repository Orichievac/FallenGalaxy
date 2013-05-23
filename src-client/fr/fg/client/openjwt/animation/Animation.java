/*
Copyright 2011 jgottero

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

package fr.fg.client.openjwt.animation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Duration;
import com.google.gwt.user.client.Timer;

import fr.fg.client.core.Utilities;

/**
 * An {@link Animation} is a continuous event that updates progressively over
 * time at a non-fixed frame rate.
 */
public abstract class Animation {
	// ------------------------------------------------------- CONSTANTES -- //

	/**
	 * The default time in milliseconds between frames.
	 */
	private static final int DEFAULT_FRAME_DELAY = 25;

	// -------------------------------------------------------- ATTRIBUTS -- //

	/**
	 * The {@link Animation Animations} that are currently in progress.
	 */
	private static List<Animation> animations = null;

	/**
	 * The {@link Timer} that applies the animations.
	 */
	private static Timer animationTimer = null;

	private static double lastFrameTime = -1;

	/**
	 * The duration of the {@link Animation} in milliseconds.
	 */
	private int duration = -1;

	/**
	 * Is the {@link Animation} running, even if {@link #onStart()} has not yet
	 * been called.
	 */
	private boolean running = false;

	/**
	 * Has the {@link Animation} actually started.
	 */
	private boolean started = false;

	/**
	 * The start time of the {@link Animation}.
	 */
	private double startTime = -1;
	
	private boolean loop = false;
	
	private boolean paused = false;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //

	public boolean isPaused() {
		return paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	/**
	 * Immediately cancel this animation. If the animation is running or is
	 * scheduled to run, {@link #onCancel()} will be called.
	 */
	public void cancel() {
		// Ignore if the animation is not currently running
		if (!running) {
			return;
		}

		animations.remove(this);
		onCancel();
		started = false;
		running = false;
	}

	/**
	 * Immediately run this animation. If the animation is already running, it
	 * will be canceled first.
	 * 
	 * @param duration
	 *            the duration of the animation in milliseconds
	 */
	public void run(int duration) {
		run(duration, isRunning() && lastFrameTime != -1 ? lastFrameTime : Duration.currentTimeMillis());
	}

	/**
	 * Run this animation at the given startTime. If the startTime has already
	 * passed, the animation will be synchronize as if it started at the
	 * specified start time. If the animation is already running, it will be
	 * canceled first.
	 * 
	 * @param duration
	 *            the duration of the animation in milliseconds
	 * @param startTime
	 *            the synchronized start time in milliseconds
	 */
	public void run(int duration, double startTime) {
		// Cancel the animation if it is running
		cancel();

		// Save the duration and startTime
		this.running = true;
		this.duration = duration;
		this.startTime = startTime;

		// Start synchronously if start time has passed
		double curTime = Duration.currentTimeMillis();
		if (update(curTime, Math.max(0, curTime - startTime))) {
			return;
		}

		// Add to the list of animations

		// We use a static list of animations and a single timer, and create
		// them
		// only if we are the only active animation. This is safe since JS is
		// single-threaded.
		if (animations == null) {
			animations = new ArrayList<Animation>();
			animationTimer = new Timer() {
				@Override
				public void run() {
					updateAnimations();
				}
			};
			animationTimer.schedule(DEFAULT_FRAME_DELAY);
		}
		
		animations.add(this);
	}
	
	public boolean isRunning() {
		return running;
	}

	public int getDuration() {
		return duration;
	}
	
	public void setProgress(double progress) {
		if (!running) {
			return;
		}
		this.startTime = Duration.currentTimeMillis() - duration * progress;
	}
	
	/**
	 * Interpolate the linear progress into a more natural easing function.
	 * 
	 * Depending on the {@link Animation}, the return value of this method can
	 * be less than 0.0 or greater than 1.0.
	 * 
	 * @param elapsedTime
	 *            elapsed time in milliseconds
	 * @param duration
	 *            the duration of the animation in milliseconds
	 * 
	 * @return the interpolated progress
	 */
	protected abstract double interpolate(double elapsedTime, double duration);

	/**
	 * Called immediately after the animation is canceled.
	 */
	protected void onCancel() {
		
	}

	/**
	 * Called immediately after the animation completes.
	 */
	protected void onComplete(double elapsedTime) {
		onUpdate(interpolate(duration, duration), elapsedTime);
	}

	/**
	 * Called immediately before the animation starts.
	 */
	protected void onStart(double elapsedTime) {
		onUpdate(interpolate(0, duration), elapsedTime);
	}
	
	public void onDestroy() {
		// A rédéfinir si nécessaire
	}
	
	public final void destroy() {
		cancel();
		onDestroy();
	}
	
	/**
	 * Called when the animation should be updated.
	 * 
	 * The value of progress is between 0.0 and 1.0 (inclusive) (unless you
	 * override the {@link #interpolate(double)} method to provide a wider range
	 * of values). You can override {@link #onStart()} and {@link #onComplete()}
	 * to perform setup and tear down procedures.
	 * 
	 * @param progress
	 *            a double, normally between 0.0 and 1.0 (inclusive)
	 */
	protected abstract void onUpdate(double progress, double elapsedTime);

	public static int getRunningAnimationsCount() {
		return animations == null ? 0 : animations.size();
	}
	
	public static List<Animation> getRunningAnimations() {
		return animations;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //

	/**
	 * Update the {@link Animation}.
	 * 
	 * @param curTime
	 *            the current time
	 * @return true if the animation is complete, false if still running
	 */
	private boolean update(double curTime, double elapsedTime) {
		if (paused) {
			startTime += elapsedTime;
		}
		boolean finished = curTime >= startTime + duration;
		if (started && !finished) {
			// Animation is in progress.
			if (!paused) {
				onUpdate(interpolate(curTime - startTime, duration), elapsedTime);
			}
			return false;
		}
		if (!started && curTime >= startTime) {
			// Start the animation.
			started = true;
			onStart(curTime - startTime);
			// Intentional fall through to possibly end the animation.
		}
		if (finished) {
			// Animation is complete.
			onComplete(elapsedTime);
			if (loop) {
				this.startTime += duration * (int) Math.floor((curTime - startTime) / duration);
				onStart(Math.max(0, curTime - startTime));
				return false;
			} else {
				started = false;
				running = false;
				return true;
			}
		}
		return false;
	}

	/**
	 * Update all {@link Animation Animations}.
	 */
	private static void updateAnimations() {
		double curTime = Duration.currentTimeMillis();
		double elapsedTime = lastFrameTime == -1 ? 0 : curTime - lastFrameTime;
		lastFrameTime = curTime;
		
		// Duplicate the animations list in case it changes as we iterate over
		// it
		Animation[] curAnimations = new Animation[animations.size()];
		curAnimations = animations.toArray(curAnimations);

		// Iterator through the animations
		for (Animation animation : curAnimations) {
			try {
				if (animation.running && animation.update(curTime, elapsedTime)) {
					// We can't just remove the animation at the index, because
					// calling
					// animation.update may have the side effect of canceling this
					// animation, running new animations, or canceling other
					// animations.
					animations.remove(animation);
				}
			} catch (Exception e) {
				Utilities.log("Animation error: " + (animation != null ?
					animation.getClass() + " " + animation.toString() : "<null>") +
					" (animation removed)", e);
				animations.remove(animation);
			}
		}

		// Reschedule the timer
		animationTimer.schedule(DEFAULT_FRAME_DELAY);
	}
}