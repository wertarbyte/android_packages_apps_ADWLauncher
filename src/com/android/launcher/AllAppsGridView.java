/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class AllAppsGridView extends GridView implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, DragSource {

    private DragController mDragger;
    private Launcher mLauncher;
    private Paint mPaint;
    //ADW: Animation vars
	private final static int CLOSED=1;
	private final static int OPEN=2;
	private final static int CLOSING=3;
	private final static int OPENING=4;
	private int mStatus=CLOSED;
	private boolean isAnimating;
	private long startTime;
	private float mScaleFactor;
	private int mIconSize=0;
	private int mBgAlpha=255;
	private int mTargetAlpha=255;
	private Paint mLabelPaint;
	private boolean shouldDrawLabels=false;
	private int mAnimationDuration=800;
    public AllAppsGridView(Context context) {
        super(context);
    }

    public AllAppsGridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public AllAppsGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();
        mPaint.setDither(false); 
        mLabelPaint=new Paint();
        mLabelPaint.setDither(false);
    }

    public boolean isOpaque() {
    	if(mBgAlpha>=255)return true;
    	else return false;
    }

    @Override
    protected void onFinishInflate() {
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
        mLauncher.startActivitySafely(app.intent);
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!view.isInTouchMode()) {
            return false;
        }

        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
        app = new ApplicationInfo(app);

        mDragger.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
        mLauncher.closeAllApplications();

        return true;
    }

    public void setDragger(DragController dragger) {
        mDragger = dragger;
    }

    public void onDropCompleted(View target, boolean success) {
    }

    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
        setSelector(new IconHighlights(mLauncher));
    }
    /**
     * ADW: easing functions for animation
     */
	static float easeOut (float time, float begin, float end, float duration) {
		float change=end- begin;
		return change*((time=time/duration-1)*time*time + 1) + begin;
	}
	static float easeIn (float time, float begin, float end, float duration) {
		float change=end- begin;
		return change*(time/=duration)*time*time + begin;
	}
	static float easeInOut (float time, float begin, float end, float duration) {
		float change=end- begin;
		if ((time/=duration/2.0f) < 1) return change/2.0f*time*time*time + begin;
		return change/2.0f*((time-=2.0f)*time*time + 2.0f) + begin;
	}
	/**
	 * ADW: Override drawing methods to do animation
	 */
	@Override
	public void draw(Canvas canvas) {
		long currentTime;
		if(startTime==0){
			startTime=SystemClock.uptimeMillis();
			currentTime=0;
		}else{
			currentTime=SystemClock.uptimeMillis()-startTime;
		}
		if(mStatus==OPENING){
			mScaleFactor=easeOut(currentTime, 3.0f, 1.0f, mAnimationDuration);
		}else if (mStatus==CLOSING){
			mScaleFactor=easeIn(currentTime, 1.0f, 3.0f, mAnimationDuration);
		}
		if(currentTime>=mAnimationDuration){
			isAnimating=false;
			if(mStatus==OPENING){
				mStatus=OPEN;
			}else if(mStatus==CLOSING){
				mStatus=CLOSED;
				mLauncher.getWorkspace().clearChildrenCache();
				setVisibility(View.GONE);
			}
		}
		shouldDrawLabels=(currentTime>mAnimationDuration/2 && mStatus==OPENING)||(currentTime<mAnimationDuration/2 && mStatus==CLOSING);
		float porcentajeScale=1.0f;
		if(isAnimating){
			porcentajeScale=1.0f-((mScaleFactor-1)/3.0f);
			if(porcentajeScale>0.9f)porcentajeScale=1f;
			if(porcentajeScale<0)porcentajeScale=0;
			mBgAlpha=(int)(porcentajeScale*255);
		}
		mPaint.setAlpha(mBgAlpha);
		if(getVisibility()==View.VISIBLE){
    		canvas.drawARGB((int)(porcentajeScale*mTargetAlpha),0, 0, 0);
			super.draw(canvas);
		}

	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		int saveCount = canvas.save();
		if(mIconSize==0){
			Drawable[] tmp=((TextView)child).getCompoundDrawables();
			mIconSize=tmp[1].getIntrinsicHeight()+child.getPaddingTop();
		}
		if(child.getDrawingCache()==null){
			child.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
			child.setDrawingCacheEnabled(true);
		}
		Bitmap cache=child.getDrawingCache();
		if(isAnimating){
			postInvalidate();
			if(cache!=null){
				float x;
				float y;
				int distH=(child.getLeft()+(child.getWidth()/2))-(getWidth()/2);
				int distV=(child.getTop()+(child.getHeight()/2))-(getHeight()/2);
				x=child.getLeft()+(distH*(mScaleFactor-1))*(mScaleFactor);
				y=child.getTop()+(distV*(mScaleFactor-1))*(mScaleFactor);
				float width=child.getWidth()*mScaleFactor;
				float height=(child.getHeight()-(child.getHeight()-mIconSize))*mScaleFactor;
				Rect r1=new Rect(0, 0, cache.getWidth(), cache.getHeight()-(child.getHeight()-mIconSize));
				Rect r2=new Rect((int)x, (int)y, (int)x+(int)width, (int)y+(int)height);
				if(shouldDrawLabels){
					//ADW: try to manually draw labels
					Rect rl1=new Rect(0,mIconSize,cache.getWidth(),cache.getHeight());
					Rect rl2=new Rect(child.getLeft(),child.getTop()+mIconSize,child.getLeft()+cache.getWidth(),child.getTop()+cache.getHeight());
					mLabelPaint.setAlpha(mBgAlpha);
					canvas.drawBitmap(cache, rl1, rl2, mLabelPaint);
				}
				canvas.drawBitmap(cache, r1, r2, mPaint);
			}else{
				child.draw(canvas);
			}
		}else{
			if(cache!=null){
				mPaint.setAlpha(255);
				canvas.drawBitmap(cache, child.getLeft(), child.getTop(), mPaint);
			}else{
				child.draw(canvas);
			}
		}
		canvas.restoreToCount(saveCount);
		return true;
	}
	/**
	 * Open/close public methods
	 */
	public void open(boolean animate){
		mTargetAlpha=AlmostNexusSettingsHelper.getDrawerAlpha(mLauncher);
		if(animate){
			mBgAlpha=0;
			isAnimating=true;
			mStatus=OPENING;
		}else{
			mBgAlpha=mTargetAlpha;
			isAnimating=false;
			mStatus=OPEN;
		}
		startTime=0;
		this.setVisibility(View.VISIBLE);
		invalidate();
	}
	public void close(boolean animate){
		if(animate){
			mStatus=CLOSING;
			isAnimating=true;
		}else{
			mStatus=CLOSED;
			isAnimating=false;
			mLauncher.getWorkspace().clearChildrenCache();
			setVisibility(View.GONE);
		}
		startTime=0;
		invalidate();
	}
	public void setAnimationSpeed(int speed){
		mAnimationDuration=speed;
	}}
