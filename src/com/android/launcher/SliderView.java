package com.android.launcher;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.os.Vibrator;
public class SliderView extends ImageView {

    private static final int HORIZONTAL = 0; // as defined in attrs.xml
    private static final int VERTICAL = 1;
    private static final int REST = 2;
    
    private static final int STATE_NORMAL = 0;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_ACTIVE = 2;

    private static final long VIBRATE_SHORT = 30;
    private static final long VIBRATE_LONG = 40;

    private static final int ANIM_DURATION = 250; // Time for most animations (in ms)
    private static final int ANIM_TARGET_TIME = 500; // Time to show targets (in ms)
    private OnTriggerListener mOnTriggerListener;
    private boolean mGrabbedState;
    private boolean mTriggered = false;
    private int mOrientation=REST;
    private int mSlideDirections=OnTriggerListener.UP;
    private boolean mAnimating=false;
	private Rect mTmpRect;
	private boolean mTracking;
	private int mCurrentState;
	private ArrayList<ImageView> mTargets;
	private int mTargetDistance=50;
	private Rect mTargetRect;
	private Rect mLimitRect;
	private Point initPosition;
	private boolean mSliding;
	private int mPreviousX;
	private int mPreviousY;
	private int securityMargin=15;	
	private Launcher mLauncher;
	private long mTouchTime;
	private boolean mSlidingEnabled=true;
	public SliderView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public SliderView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
		init();
	}

	public SliderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderView,
                defStyle, 0);

        mSlideDirections = a.getInt(
            R.styleable.SliderView_slideDirections, mSlideDirections);
        mTargetDistance = a.getDimensionPixelSize(
                R.styleable.SliderView_targetDistance, mTargetDistance);
		init();
	}
	private void init(){
		//mSlideDirections=OnTriggerListener.UP|OnTriggerListener.DOWN;
		mLimitRect=new Rect();
		mTmpRect = new Rect();
		mTargetRect=new Rect();
		getHitRect(mTmpRect);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		if(!mAnimating && !mTracking){
			super.onLayout(changed, left, top, right, bottom);
			getHitRect(mTmpRect);
			initPosition=new Point(getLeft(), getTop());
			mLimitRect.set(mTmpRect);
		}
	}

    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if(mSlidingEnabled){
	            	mTracking = true;
	                mTriggered = false;
	                setGrabbedState(true);
	                mPreviousX=(int) x;
	                mPreviousY=(int) y;
	                showTarget();
                }
                setState(STATE_PRESSED);
                mTouchTime=System.currentTimeMillis();
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if(mTracking){
            		if(mOrientation==REST){
                		if((((mSlideDirections&OnTriggerListener.UP)==OnTriggerListener.UP)||((mSlideDirections&OnTriggerListener.DOWN)==OnTriggerListener.DOWN))  && (Math.abs(mPreviousX-x)*2<Math.abs(mPreviousY-y)) ){
                			mOrientation=VERTICAL;
	                	}else if((((mSlideDirections&OnTriggerListener.LEFT)==OnTriggerListener.LEFT)||((mSlideDirections&OnTriggerListener.RIGHT)==OnTriggerListener.RIGHT)) && (Math.abs(mPreviousX-x)>Math.abs(mPreviousY-y)*2)){
                    		mOrientation=HORIZONTAL;
	                	}
                	}
                	if(mOrientation!=REST){
	                	mSliding=true;
	                	moveHandle(x, y);
	                    getHitRect(mTmpRect);
	                    boolean thresholdReached=false;
	                    int targetReached=-1;
	                    for(ImageView v:mTargets){
		                    v.getHitRect(mTargetRect);
		                    if(mTargetRect.intersect(mTmpRect)){
		                    	thresholdReached=true;
		                    	targetReached=(Integer)v.getTag();
		                    }
	                    }
	                    if (!mTriggered && thresholdReached) {
	                        mTriggered = true;
	                        mTracking = false;
	                        setState(STATE_ACTIVE);
	                        dispatchTriggerEvent(targetReached);
	                        hideTarget();
	                        reset(true);
	                        setGrabbedState(false);
	                    }
                	}
                }
                break;
            case MotionEvent.ACTION_UP:
            	final long upTime=System.currentTimeMillis();
        		final boolean shortTap=((upTime-mTouchTime)<350);
        		if((!mSliding && mSlidingEnabled) ||(shortTap&&!mTriggered)){
            		performClick();
            	}
            case MotionEvent.ACTION_CANCEL:
                mTracking = false;
                mTriggered = false;
                mSliding=false;
                mOrientation=REST;
                reset(true);
                hideTarget();
                setGrabbedState(false);
                break;
        }

        return mTracking || super.onTouchEvent(event);
    }
    private void moveHandle(float x, float y) {
    	int deltaX=0;
    	int deltaY=0;
        boolean moved=false;
        if (isHorizontal()) {
            deltaX = (int) x- (getWidth() / 2);
            if((deltaX<0 && getLeft()>mLimitRect.left) || (deltaX>0 && getRight()<mLimitRect.right)){
            	offsetLeftAndRight(deltaX);
            	moved=true;
            }
        } else {
            deltaY = (int) y- (getHeight() / 2);
            if((deltaY<0 && getTop()>mLimitRect.top) || (deltaY>0 && getBottom()<mLimitRect.bottom)){
            	offsetTopAndBottom(deltaY);
            	moved=true;
            }
        }
    	if(moved){
	        Rect rect=new Rect(getLeft()-deltaX , getTop()-deltaY, getRight()-deltaX, getBottom()-deltaY);
	        ViewGroup v=(ViewGroup)getParent();
	        v.invalidate(rect);
    	}
    }

    private void onAnimationDone() {
        reset(false);
        mAnimating = false;
    }
    
    void reset(boolean animate) {
        setState(STATE_NORMAL);
        int dx= initPosition.x-getLeft();
        int dy= initPosition.y-getTop();

        if (animate && getVisibility()==View.VISIBLE) {
            TranslateAnimation trans = new TranslateAnimation(0, dx, 0, dy);
            trans.setDuration(ANIM_DURATION);
            trans.setAnimationListener(mAnimationDoneListener);
            mAnimating=true;
            startAnimation(trans);
        } else {
            offsetLeftAndRight(dx);
            offsetTopAndBottom(dy);
            clearAnimation();
            invalidate();
        }
    }
  
    void setState(int state) {
        setPressed(state == STATE_PRESSED);
        mCurrentState = state;
    }
    public void hideTarget() {
        if(mTargets!=null){
	    	for(ImageView v:mTargets){
		    	v.clearAnimation();
		        v.setVisibility(View.INVISIBLE);
	        }
        }
    }
    void showTarget() {
    	if(mTargets==null){
    		createTargets();
    	}else{
    		updateLimits();
    	}
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(ANIM_TARGET_TIME);
        
        for(ImageView v:mTargets){
        	v.startAnimation(alphaAnim);
        	v.setVisibility(View.VISIBLE);
        }
    }
    
    private boolean isHorizontal() {
        return mOrientation == HORIZONTAL;
    }
    
    /*private synchronized void vibrate(long duration) {
        if (mVibrator == null) {
            mVibrator = (android.os.Vibrator)
                    getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
        mVibrator.vibrate(duration);
    }*/
    /**
     * Sets the current grabbed state, and dispatches a grabbed state change
     * event to our listener.
     */
    private void setGrabbedState(boolean newState) {
        if (newState != mGrabbedState) {
            mGrabbedState = newState;
            if (mOnTriggerListener != null) {
                mOnTriggerListener.onGrabbedStateChange(this, mGrabbedState);
            }
        }
    }
    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }

    /**
     * Dispatches a trigger event to listener. Ignored if a listener is not set.
     * @param whichHandle the handle that triggered the event.
     */
    private void dispatchTriggerEvent(int whichHandle) {
    	performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
    		    HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        if (mOnTriggerListener != null) {
            mOnTriggerListener.onTrigger(this, whichHandle);
        }
    }
    
    /**
     * Interface definition for a callback to be invoked when a tab is triggered
     * by moving it beyond a threshold.
     */
    public interface OnTriggerListener {
        public static final int UP=1;
        public static final int DOWN=2;
        public static final int LEFT=4;
        public static final int RIGHT=8;

        /**
         * Called when the user moves a handle beyond the threshold.
         *
         * @param v The view that was triggered.
         * @param whichHandle  Which "target" the user hit,
         *        either {@link #LEFT}, {@link #RIGHT}, {@link #TOP}, {@link #BOTTOM}.
         */
        void onTrigger(View v, int whichHandle);

        /**
         * Called when the "grabbed state" changes (i.e. when the user either grabs or releases
         * one of the handles.)
         *
         * @param v the view that was triggered
         * @param grabbedState the new state: true/false
         */
        void onGrabbedStateChange(View v, boolean grabbedState);
    }
    /**
     * Listener used to reset the view when the current animation completes.
     */
    private final AnimationListener mAnimationDoneListener = new AnimationListener() {
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            onAnimationDone();
        }
    };
    private void createTargets(){
    	mTargets=new ArrayList<ImageView>();
    	FrameLayout p=(FrameLayout)getParent();
    	final int l=getLeft();
    	final int r=getRight();
    	final int t=getTop();
    	final int b=getBottom();
    	FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)getLayoutParams();
        final int horizontalGravity = lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        final int verticalGravity = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
        Starter starter=new Starter();
        if((mSlideDirections&OnTriggerListener.UP)==OnTriggerListener.UP) {
        	ImageView v1 =new ImageView(getContext());
			v1.setBackgroundResource(R.drawable.sliding_target_top);
			AnimationDrawable frameAnimation = (AnimationDrawable) v1.getBackground();
			//frameAnimation.start();
			starter.addAnimation(frameAnimation);
	    	v1.setTag(OnTriggerListener.UP);
	    	mTargets.add(v1);
	    	lp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	lp.gravity=horizontalGravity;
	    	lp.topMargin=getTop()-mTargetDistance-frameAnimation.getIntrinsicHeight();
	    	p.addView(v1, lp);
	    	mLimitRect.top=getTop()-mTargetDistance-frameAnimation.getIntrinsicHeight()-securityMargin;
    	}
		if((mSlideDirections&OnTriggerListener.DOWN)==OnTriggerListener.DOWN){
	    	ImageView v2 =new ImageView(getContext());
			v2.setBackgroundResource(R.drawable.sliding_target_bottom);
			AnimationDrawable frameAnimation = (AnimationDrawable) v2.getBackground();
			starter.addAnimation(frameAnimation);
	    	v2.setTag(OnTriggerListener.DOWN);
	    	mTargets.add(v2);
	    	lp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	lp.gravity=horizontalGravity;
	    	lp.topMargin=getBottom()+mTargetDistance;
	    	p.addView(v2, lp);
	    	mLimitRect.bottom=getBottom()+mTargetDistance+frameAnimation.getIntrinsicHeight()+securityMargin;
		}
		if((mSlideDirections&OnTriggerListener.LEFT)==OnTriggerListener.LEFT){
	    	ImageView v3 =new ImageView(getContext());
			v3.setBackgroundResource(R.drawable.sliding_target_left);
			AnimationDrawable frameAnimation = (AnimationDrawable) v3.getBackground();
			starter.addAnimation(frameAnimation);
	    	v3.setTag(OnTriggerListener.LEFT);
	    	mTargets.add(v3);
	    	lp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	lp.gravity=verticalGravity;
	    	lp.leftMargin=getLeft()-mTargetDistance-frameAnimation.getIntrinsicWidth();
	    	p.addView(v3, lp);
	    	mLimitRect.left=getLeft()-mTargetDistance-frameAnimation.getIntrinsicWidth()-securityMargin;
		}
		if((mSlideDirections&OnTriggerListener.RIGHT)==OnTriggerListener.RIGHT){
	    	ImageView v4 =new ImageView(getContext());
			v4.setBackgroundResource(R.drawable.sliding_target_right);
			AnimationDrawable frameAnimation = (AnimationDrawable) v4.getBackground();
			starter.addAnimation(frameAnimation);
	    	v4.setTag(OnTriggerListener.RIGHT);
	    	mTargets.add(v4);
	    	lp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	lp.gravity=verticalGravity;
	    	lp.leftMargin=getRight()+mTargetDistance;
	    	p.addView(v4, lp);
	    	mLimitRect.right=getRight()+mTargetDistance+frameAnimation.getIntrinsicWidth()+securityMargin;
		}
		post(starter);
    }
    private void updateLimits(){
    	for(ImageView v:mTargets){
    		AnimationDrawable frameAnimation = (AnimationDrawable) v.getBackground();
    		FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams) v.getLayoutParams();
    		switch ((Integer)v.getTag()) {
			case OnTriggerListener.UP:
				lp.topMargin=getTop()-mTargetDistance-frameAnimation.getIntrinsicHeight();
				mLimitRect.top=getTop()-mTargetDistance-frameAnimation.getIntrinsicHeight()-securityMargin;
				break;
			case OnTriggerListener.DOWN:
				lp.topMargin=getBottom()+mTargetDistance;
				mLimitRect.bottom=getBottom()+mTargetDistance+frameAnimation.getIntrinsicHeight()+securityMargin;
				break;
			case OnTriggerListener.LEFT:
				lp.leftMargin=getLeft()-mTargetDistance-frameAnimation.getIntrinsicWidth();
				mLimitRect.left=getLeft()-mTargetDistance-frameAnimation.getIntrinsicWidth()-securityMargin;
				break;
			case OnTriggerListener.RIGHT:
		    	lp.leftMargin=getRight()+mTargetDistance;
				mLimitRect.right=getRight()+mTargetDistance+frameAnimation.getIntrinsicWidth()+securityMargin;
				break;
			default:
				break;
			}
    		v.setLayoutParams(lp);
    	}
    }
    public void setSlidingEnabled(boolean enable){
    	mSlidingEnabled=enable;
    }
    //TODO:ADW dirty hack to force the animation start
    //found here:http://code.google.com/p/android/issues/detail?id=1818
    class Starter implements Runnable {
    	public ArrayList<AnimationDrawable> animations;
		public void run() {
			for(AnimationDrawable anim: animations){
				anim.start();
			}
		}
		public void addAnimation(AnimationDrawable anim){
			if(animations==null){
				animations=new ArrayList<AnimationDrawable>();
			}
			animations.add(anim);
		}
    }
    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }
    
}
