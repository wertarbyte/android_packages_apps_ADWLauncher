package demo.multitouch.controller;

/**
 * MultiTouchController.java
 * 
 * (c) Luke Hutchison (luke.hutch@mit.edu)
 * 
 * Modified for official level 5 API by Cyanogen (shade@chemlab.org)
 *
 * Support for compilation under Android 1.5/1.6 added my mmin of handyCalc. 
 * 
 * Released under the Apache License v2.
 */

import java.lang.reflect.Method;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

/**
 * A class that simplifies the implementation of multitouch in applications. Subclass this and read the fields here as needed in
 * subclasses.
 * 
 * @author Luke Hutchison
 */
public class MultiTouchController<T> {

	/**
	 * Time in ms required after a change in event status (e.g. putting down or lifting off the second finger) before events
	 * actually do anything -- helps eliminate noisy jumps that happen on change of status
	 */
	private static final long EVENT_SETTLE_TIME_INTERVAL = 0; //Default: 100. Wysie: If set to 100, quick pinches are not triggered. 20 is nice too.

	// The biggest possible abs val of the change in x or y between multitouch events
	// (larger dx/dy events are ignored) -- helps eliminate jumping on finger 2 up/down
	private static final float MAX_MULTITOUCH_POS_JUMP_SIZE = 30.0f;

	// The biggest possible abs val of the change in multitouchWidth or multitouchHeight between
	// multitouch events (larger-jump events are ignored) -- helps eliminate jumping on finger 2 up/down
	private static final float MAX_MULTITOUCH_DIM_JUMP_SIZE = 40.0f;

	// The smallest possible distance between multitouch points (used to avoid div-by-zero errors and display glitches)
	private static final float MIN_MULTITOUCH_SEPARATION = 30.0f;

	// --

	MultiTouchObjectCanvas<T> objectCanvas;

	private PointInfo currPt, prevPt;

	// --

	private T draggedObject = null;

	private long dragStartTime, dragSettleTime;

	// Conversion from object coords to screen coords, and from drag width to object scale
	private float objDraggedPointX, objDraggedPointY, objStartScale;

	private PositionAndScale objPosAndScale = new PositionAndScale();

	// --

	/** Whether to handle single-touch events/drags before multi-touch is initiated or not; if not, they are handled by subclasses */
	private boolean handleSingleTouchEvents;

	// --

	private static final int MODE_NOTHING = 0;

	private static final int MODE_DRAG = 1;

	private static final int MODE_STRETCH = 2;

	private int dragMode = MODE_NOTHING;

	// ------------------------------------------------------------------------------------

	/** Constructor that sets handleSingleTouchEvents to true */
	public MultiTouchController(MultiTouchObjectCanvas<T> objectCanvas, Resources res) {
		this(objectCanvas, res, true);
	}

	/** Full constructor */
	public MultiTouchController(MultiTouchObjectCanvas<T> objectCanvas, Resources res, boolean handleSingleTouchEvents) {
		this.currPt = new PointInfo(res);
		this.prevPt = new PointInfo(res);
		this.handleSingleTouchEvents = handleSingleTouchEvents;
		this.objectCanvas = objectCanvas;
	}

	// ------------------------------------------------------------------------------------

	/**
	 * Whether to handle single-touch events/drags before multi-touch is initiated or not; if not, they are handled by subclasses.
	 * Default: true
	 */
	protected void setHandleSingleTouchEvents(boolean handleSingleTouchEvents) {
		this.handleSingleTouchEvents = handleSingleTouchEvents;
	}

	/**
	 * Whether to handle single-touch events/drags before multi-touch is initiated or not; if not, they are handled by subclasses.
	 * Default: true
	 */
	protected boolean getHandleSingleTouchEvents() {
		return handleSingleTouchEvents;
	}

	// ------------------------------------------------------------------------------------

	public static final boolean multiTouchSupported;
	private static Method m_getPointerCount;
	private static Method m_findPointerIndex;
	private static Method m_getPressure;
	private static Method m_getHistoricalX;
	private static Method m_getHistoricalY;
	private static Method m_getHistoricalPressure;
	private static Method m_getX;
	private static Method m_getY;

	static {
		boolean succeed = false;
		try {
			m_getPointerCount = MotionEvent.class.getMethod("getPointerCount");
			m_findPointerIndex = MotionEvent.class.getMethod("findPointerIndex", Integer.TYPE);
			m_getPressure = MotionEvent.class.getMethod("getPressure", Integer.TYPE);
			m_getHistoricalX = MotionEvent.class.getMethod("getHistoricalX", Integer.TYPE, Integer.TYPE);
			m_getHistoricalY = MotionEvent.class.getMethod("getHistoricalY", Integer.TYPE, Integer.TYPE);
			m_getHistoricalPressure = MotionEvent.class.getMethod("getHistoricalPressure", Integer.TYPE, Integer.TYPE);
			m_getX = MotionEvent.class.getMethod("getX", Integer.TYPE);
			m_getY = MotionEvent.class.getMethod("getY", Integer.TYPE);
			succeed = true;
		} catch(Exception e) {
		}
		multiTouchSupported = succeed;
	}

	// ------------------------------------------------------------------------------------

	/** Process incoming touch events */
	public boolean onTouchEvent(MotionEvent event) {
		if(!multiTouchSupported)
			return false;
		try {
			int pointerCount = (Integer) m_getPointerCount.invoke(event);
			if (dragMode == MODE_NOTHING && !handleSingleTouchEvents && pointerCount == 1)
				// Not handling initial single touch events, just pass them on
				return false;

			// Handle history first, if any (we sometimes get history with ACTION_MOVE events)
			int histLen = event.getHistorySize() / pointerCount;

			// Don't try to fetch second touchpoint if only one exists
			int secondPointerIndex = (Integer) m_findPointerIndex.invoke(event, 1);

			for (int i = 0; i < histLen; i++) {
				float pressureI = (Float) m_getPressure.invoke(event, i);
				if (secondPointerIndex >= 0) {
					float historicalX = (Float) m_getHistoricalX.invoke(event, secondPointerIndex, i);
					float historicalY = (Float) m_getHistoricalY.invoke(event, secondPointerIndex, i);
					float historicalPressure = (Float) m_getHistoricalPressure.invoke(event, secondPointerIndex, i);
					decodeTouchEvent(event.getHistoricalX(i), event.getHistoricalY(i), pressureI, pointerCount,
							historicalX, historicalY, historicalPressure, MotionEvent.ACTION_MOVE, true, event
									.getHistoricalEventTime(i));
				} else {
					// Can't read from invalid pointer index
					decodeTouchEvent(event.getHistoricalX(i), event.getHistoricalY(i), pressureI, pointerCount,
							0, 0, 0.0f, MotionEvent.ACTION_MOVE, true, event.getHistoricalEventTime(i));
				}
			}

			// Handle actual event at end of history
			if (secondPointerIndex >= 0) {
				float pressure = (Float) m_getPressure.invoke(event, secondPointerIndex);
				float x = (Float) m_getX.invoke(event, secondPointerIndex);
				float y = (Float) m_getY.invoke(event, secondPointerIndex);
				decodeTouchEvent(event.getX(), event.getY(), event.getPressure(), pointerCount, x, y, pressure, event
						.getAction(), event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_CANCEL,
						event.getEventTime());
			} else {
				// Can't read from invalid pointer index
				decodeTouchEvent(event.getX(), event.getY(), event.getPressure(), pointerCount, 0, 0, 0.0f, event
						.getAction(), event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_CANCEL,
						event.getEventTime());
			}
			return true;
		} catch (Exception e) {
			// Just ignore and continue
			return false;
		}
	}

	private void decodeTouchEvent(float x, float y, float pressure, int pointerCount, float x2, float y2, float pressure2,
			int action, boolean down, long eventTime) {

		prevPt.set(currPt);
		currPt.set(x, y, pressure, pointerCount, x2, y2, pressure2, action, down, eventTime);
		multiTouchController();
	}

	// ------------------------------------------------------------------------------------

	/** Start dragging/stretching, or reset drag/stretch to current point if something goes out of range */
	private void resetDrag() {
		if (draggedObject == null)
			return;

		// Get dragged object position and scale
		objectCanvas.getPositionAndScale(draggedObject, objPosAndScale);

		// Figure out the object coords of the drag start point's screen coords.
		// All stretching should be around this point in object-coord-space.
		float scaleInv = (objPosAndScale.scale == 0.0f ? 1.0f : 1.0f / objPosAndScale.scale);
		objDraggedPointX = (currPt.getX() - objPosAndScale.xOff) * scaleInv;
		objDraggedPointY = (currPt.getY() - objPosAndScale.yOff) * scaleInv;

		// Figure out ratio between object scale factor and multitouch diameter (they are linearly correlated)
		float diam = currPt.getMultiTouchDiameter();
		objStartScale = objPosAndScale.scale / (diam == 0.0f ? 1.0f : diam);
	}

	/** Drag/stretch the dragged object to the current touch position and diameter */
	private void performDrag() {
		// Don't do anything if we're not dragging anything
		if (draggedObject == null)
			return;

		// Calc new position of dragged object
		float scale = (objPosAndScale.scale == 0.0f ? 1.0f : objPosAndScale.scale);
		float newObjPosX = currPt.getX() - objDraggedPointX * scale;
		float newObjPosY = currPt.getY() - objDraggedPointY * scale;

		// Get new drag diameter (avoiding divsion by zero), and calculate new drag scale
		float diam;
		if (!currPt.isMultiTouch) {
			// Single-touch, no change in scale
			diam = 1.0f;
		} else {
			diam = currPt.getMultiTouchDiameter();
			if (diam < MIN_MULTITOUCH_SEPARATION)
				diam = MIN_MULTITOUCH_SEPARATION;
		}
		float newScale = diam * objStartScale;

		// Get the new obj coords and scale, and set them (notifying the subclass of the change)
		objPosAndScale.set(newObjPosX, newObjPosY, newScale);
		boolean success = objectCanvas.setPositionAndScale(draggedObject, objPosAndScale, currPt);
		if (!success)
			; // If we could't set those params, do nothing currently
	}

	/** The main single-touch and multi-touch logic */
	private void multiTouchController() {

		switch (dragMode) {
		case MODE_NOTHING:
			// Not doing anything currently
			if (currPt.isDown()) {
				// Start a new single-point drag
				draggedObject = objectCanvas.getDraggableObjectAtPoint(currPt);
				if (draggedObject != null) {
					// Started a new single-point drag
					dragMode = MODE_DRAG;
					objectCanvas.selectObject(draggedObject, currPt);
					resetDrag();
					// Don't need any settling time if just placing one finger, there is no noise
					dragStartTime = dragSettleTime = currPt.getEventTime();
				}
			}
			break;

		case MODE_DRAG:
			// Currently in a single-point drag
			if (!currPt.isDown()) {
				// First finger was released, stop dragging
				dragMode = MODE_NOTHING;
				objectCanvas.selectObject((draggedObject = null), currPt);

			} else if (currPt.isMultiTouch()) {
				// Point 1 was already down and point 2 was just placed down
				dragMode = MODE_STRETCH;
				// Restart the drag with the new drag position (that is at the midpoint between the touchpoints)
				resetDrag();
				// Need to let events settle before moving things, to help with event noise on touchdown
				dragStartTime = currPt.getEventTime();
				dragSettleTime = dragStartTime + EVENT_SETTLE_TIME_INTERVAL;

			} else {
				// Point 1 is still down and point 2 did not change state, just do single-point drag to new location
				if (currPt.getEventTime() < dragSettleTime) {
					// Ignore the first few events if we just stopped stretching, because if finger 2 was kept down while
					// finger 1 is lifted, then point 1 gets mapped to finger 2. Restart the drag from the new position.
					resetDrag();
				} else {
					// Keep dragging, move to new point
					performDrag();
				}
			}
			break;

		case MODE_STRETCH:
			// Two-point stretch
			if (!currPt.isMultiTouch() || !currPt.isDown()) {
				// Dropped one or both points, stop stretching

				if (!currPt.isDown()) {
					// Dropped both points, go back to doing nothing
					dragMode = MODE_NOTHING;
					objectCanvas.selectObject((draggedObject = null), currPt);

				} else {
					// Just dropped point 2, downgrade to a single-point drag
					dragMode = MODE_DRAG;
					// Restart the drag with the single-finger position
					resetDrag();
					// Ignore the first few events after the drop, in case we dropped finger 1 and left finger 2 down
					dragStartTime = currPt.getEventTime();
					dragSettleTime = dragStartTime + EVENT_SETTLE_TIME_INTERVAL;
				}
			} else {
				// Keep stretching

				if (Math.abs(currPt.getX() - prevPt.getX()) > MAX_MULTITOUCH_POS_JUMP_SIZE
						|| Math.abs(currPt.getY() - prevPt.getY()) > MAX_MULTITOUCH_POS_JUMP_SIZE
						|| Math.abs(currPt.getMultiTouchWidth() - prevPt.getMultiTouchWidth()) * .5f > MAX_MULTITOUCH_DIM_JUMP_SIZE
						|| Math.abs(currPt.getMultiTouchHeight() - prevPt.getMultiTouchHeight()) * .5f > MAX_MULTITOUCH_DIM_JUMP_SIZE) {
					// Jumped too far, probably event noise, reset and ignore events for a bit
					resetDrag();
					dragStartTime = currPt.getEventTime();
					dragSettleTime = dragStartTime + EVENT_SETTLE_TIME_INTERVAL;

				} else if (currPt.eventTime < dragSettleTime) {
					// Events have not yet settled, reset
					resetDrag();
				} else {
					// Stretch to new position and size
					performDrag();
				}
			}
			break;
		}
	}

	// ------------------------------------------------------------------------------------

	/** A class that packages up all MotionEvent information with all derived multitouch information (if available) */
	public static class PointInfo {
		private float x, y, dx, dy, size, diameter, diameterSq, angle, pressure, pressure2;

		private boolean down, isMultiTouch, diameterSqIsCalculated, diameterIsCalculated, angleIsCalculated;

		private int action;

		private long eventTime;

		// --

		private int displayWidth, displayHeight;

		// --

		public PointInfo(Resources res) {
			DisplayMetrics metrics = res.getDisplayMetrics();
			this.displayWidth = metrics.widthPixels;
			this.displayHeight = metrics.heightPixels;
		}

		// --

		public PointInfo(PointInfo other) {
			this.set(other);
		}

		/** Copy all fields */
		public void set(PointInfo other) {
			this.displayWidth = other.displayWidth;
			this.displayHeight = other.displayHeight;
			this.x = other.x;
			this.y = other.y;
			this.dx = other.dx;
			this.dy = other.dy;
			this.size = other.size;
			this.diameter = other.diameter;
			this.diameterSq = other.diameterSq;
			this.angle = other.angle;
			this.pressure = other.pressure;
			this.pressure2 = other.pressure2;
			this.down = other.down;
			this.action = other.action;
			this.isMultiTouch = other.isMultiTouch;
			this.diameterIsCalculated = other.diameterIsCalculated;
			this.diameterSqIsCalculated = other.diameterSqIsCalculated;
			this.angleIsCalculated = other.angleIsCalculated;
			this.eventTime = other.eventTime;
		}

		private void set(float x, float y, float pressure, int pointerCount, float x2, float y2, float pressure2, int action,
				boolean down, long eventTime) {

			//	Log.i("Multitouch", "x: " + x + " y: " + y + " pointerCount: " + pointerCount +
			//			" x2: " + x2 + " y2: " + y2 + " action: " + action + " down: " + down);

			this.eventTime = eventTime;
			this.action = action;
			this.x = x;
			this.y = y;
			this.pressure = pressure;
			this.pressure2 = pressure2;
			this.down = down;
			this.isMultiTouch = pointerCount == 2;
			
			if (isMultiTouch) {
				float xMid = (x2 + x) * .5f;
				float yMid = (y2 + y) * .5f;
				dx = Math.abs(x2 - x);
				dy = Math.abs(y2 - y);
				this.x = xMid;
				this.y = yMid;

			} else {
				// Single-touch event
				dx = dy = 0.0f;
			}
			// Need to re-calculate the expensive params if they're needed
			diameterSqIsCalculated = diameterIsCalculated = angleIsCalculated = false;
		}

		// Fast integer sqrt, by Jim Ulery. Should be faster than Math.sqrt()
		private int julery_isqrt(int val) {
			int temp, g = 0, b = 0x8000, bshft = 15;
			do {
				if (val >= (temp = (((g << 1) + b) << bshft--))) {
					g += b;
					val -= temp;
				}
			} while ((b >>= 1) > 0);
			return g;
		}

		/** Calculate the squared diameter of the multitouch event, and cache it. Use this if you don't need to perform the sqrt. */
		public float getMultiTouchDiameterSq() {
			if (!diameterSqIsCalculated) {
				diameterSq = (isMultiTouch ? dx * dx + dy * dy : 0.0f);
				diameterSqIsCalculated = true;
			}
			return diameterSq;
		}

		/** Calculate the diameter of the multitouch event, and cache it. Uses fast int sqrt but gives accuracy to 1/16px. */
		public float getMultiTouchDiameter() {
			if (!diameterIsCalculated) {
				// Get 1/16 pixel's worth of subpixel accuracy, works on screens up to 2048x2048
				// before we get overflow (at which point you can reduce or eliminate subpix
				// accuracy, or use longs in julery_isqrt())
				float diamSq = getMultiTouchDiameterSq();
				diameter = (diamSq == 0.0f ? 0.0f : (float) julery_isqrt((int) (256 * diamSq)) / 16.0f);
				// Make sure diameter is never less than dx or dy, for trig purposes
				if (diameter < dx)
					diameter = dx;
				if (diameter < dy)
					diameter = dy;
				diameterIsCalculated = true;
			}
			return diameter;
		}

		/**
		 * Calculate the angle of a multitouch event, and cache it. Actually gives the smaller of the two angles between the x
		 * axis and the line between the two touchpoints, so range is [0,Math.PI/2]. Uses Math.atan2().
		 */
		public float getMultiTouchAngle() {
			if (!angleIsCalculated) {
				angle = (float) Math.atan2(dy, dx);
				angleIsCalculated = true;
			}
			return angle;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public float getMultiTouchWidth() {
			return dx;
		}

		public float getMultiTouchHeight() {
			return dy;
		}

		public float getPressure() {
			return pressure;
		}

		public float getPressure2() {
			return pressure2;
		}

		public boolean isDown() {
			return down;
		}

		public int getAction() {
			return action;
		}

		public boolean isMultiTouch() {
			return isMultiTouch;
		}

		public long getEventTime() {
			return eventTime;
		}
	}

	// ------------------------------------------------------------------------------------

	/**
	 * A class that is used to store scroll offsets and scale information for objects that are managed by the multitouch
	 * controller
	 */
	public static class PositionAndScale {
		private float xOff, yOff, scale;

		public PositionAndScale() {
		}

		public void set(float xOff, float yOff, float scale) {
			this.xOff = xOff;
			this.yOff = yOff;
			this.scale = scale;
		}

		public float getXOff() {
			return xOff;
		}

		public float getYOff() {
			return yOff;
		}

		public float getScale() {
			return scale;
		}
	}

	// ------------------------------------------------------------------------------------

	public static interface MultiTouchObjectCanvas<T> {

		/** See if there is a draggable object at the current point. Returns the object at the point, or null if nothing to drag. */
		public T getDraggableObjectAtPoint(PointInfo pt);

		/**
		 * Get the screen coords of the dragged object's origin, and scale multiplier to convert screen coords to obj coords. Call
		 * the .set() method on the passed PositionAndScale object.
		 */
		public void getPositionAndScale(T obj, PositionAndScale objPosAndScaleOut);

		/**
		 * Set the position and scale of the dragged object, in object coords. Return true for success, or false if those
		 * parameters are out of range.
		 */
		public boolean setPositionAndScale(T obj, PositionAndScale newObjPosAndScale, PointInfo touchPoint);

		/**
		 * Select an object at the given point. Can be used to bring the object to top etc. Only called when first touchpoint goes
		 * down, not when multitouch is initiated. Also called with null when drag op stops.
		 */
		public void selectObject(T obj, PointInfo pt);
	}
}
