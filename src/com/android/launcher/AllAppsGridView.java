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

import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.R.bool;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AllAppsGridView extends GridView implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, DragSource {

    private DragController mDragger;
    private Launcher mLauncher;
    private Bitmap mTexture;
    private Paint mPaint;
    private int mTextureWidth;
    private int mTextureHeight;
    //ADW:Hack the texture thing to make scrolling faster
    private boolean forceOpaque=false;
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
	private Paint mLabelPaint;
	private boolean shouldDrawLabels=false;
    public AllAppsGridView(Context context) {
        super(context);
    }

    public AllAppsGridView(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.gridViewStyle);
    }

    public AllAppsGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AllAppsGridView, defStyle, 0);
        //TODO: ADW-Check if it's necessary
        boolean bootOpaque=AlmostNexusSettingsHelper.getDrawerFast(context);
        final int textureId = a.getResourceId(R.styleable.AllAppsGridView_texture, 0);
        setForceOpaque(bootOpaque);
        if(!forceOpaque){
	        if (textureId != 0) {
	            mTexture = BitmapFactory.decodeResource(getResources(), textureId);
	            mTextureWidth = mTexture.getWidth();
	            mTextureHeight = mTexture.getHeight();
	        }
        }
        a.recycle();
        mPaint = new Paint();
        mPaint.setDither(false); 
        mLabelPaint=new Paint();
        mLabelPaint.setDither(false);
    }

    @Override
    public boolean isOpaque() {
        if(forceOpaque) return mBgAlpha>=250;
        else return !mTexture.hasAlpha();
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
    }
    /**
     * ADW: modify the cachecolorhint and the drawing cache
     * when we're not using a background ("fast drawer settings")
     * @param value
     */
    public void setForceOpaque(boolean value){
    	if(value!=forceOpaque){
	    	forceOpaque=value;
	    	if(value){
	    		//this.setBackgroundColor(0xFF000000);
	    		this.setCacheColorHint(0xFF000000);
	    		this.setDrawingCacheBackgroundColor(0xFF000000);
	    		setScrollingCacheEnabled(true);
	    	}else{
	    		//this.setBackgroundDrawable(null);
	    		this.setCacheColorHint(Color.TRANSPARENT);
	    		super.setCacheColorHint(Color.TRANSPARENT);
	    		this.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
	    		setScrollingCacheEnabled(true);
	    	}
    	}
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
			mScaleFactor=easeOut(currentTime, 3.0f, 1.0f, 800);
		}else if (mStatus==CLOSING){
			mScaleFactor=easeIn(currentTime, 1.0f, 3.0f, 800);
		}
		if(currentTime>=800){
			isAnimating=false;
			if(mStatus==OPENING){
				mStatus=OPEN;
				clearChildrenCache();
				if(forceOpaque){
					setCacheColorHint(0xFF000000);
					setDrawingCacheBackgroundColor(0xFF000000);
			        setHorizontalFadingEdgeEnabled(true);
			        setVerticalFadingEdgeEnabled(true);
				}
				setChildrenDrawingCacheEnabled(true);
				setDrawingCacheEnabled(true);
			}else if(mStatus==CLOSING){
				mStatus=CLOSED;
				setVisibility(View.GONE);
			}
		}
		shouldDrawLabels=(currentTime>400 && mStatus==OPENING)||(currentTime<400 && mStatus==CLOSING);
		if(isAnimating){
			float porcentajeScale=1.0f-((mScaleFactor-1)/4.0f);
			if(porcentajeScale>1)porcentajeScale=1;
			if(porcentajeScale<0)porcentajeScale=0;
			mBgAlpha=(int)(porcentajeScale*255);
		}
		mPaint.setAlpha(mBgAlpha);
		if(getVisibility()==View.VISIBLE){
			//canvas.drawARGB(alpha,0, 0, 0);
	    	if(!forceOpaque){
		        final Bitmap texture = mTexture;
		        final Paint paint = mPaint;
		
		        final int width = getWidth();
		        final int height = getHeight();
		
		        final int textureWidth = mTextureWidth;
		        final int textureHeight = mTextureHeight;
		
		        int x = 0;
		        int y;
		
		        while (x < width) {
		            y = 0;
		            while (y < height) {
		                canvas.drawBitmap(texture, x, y, paint);
		                y += textureHeight;
		            }
		            x += textureWidth;
		        }
	    	}else{
	    		canvas.drawARGB(mBgAlpha,0, 0, 0);
	    	}
			super.draw(canvas);
		}

	}
	private void clearChildrenCache(){
		for(int i=0;i<getChildCount();i++){
			getChildAt(i).destroyDrawingCache();
		}
	}
	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		int saveCount = canvas.save();
		if(mIconSize==0){
			Drawable[] tmp=((TextView)child).getCompoundDrawables();
			mIconSize=tmp[1].getIntrinsicHeight()+child.getPaddingTop();
		}
		child.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
		child.setDrawingCacheEnabled(true);
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
					mLabelPaint.setAlpha(mBgAlpha-50);
					canvas.drawBitmap(cache, rl1, rl2, mLabelPaint);
				}
				canvas.drawBitmap(cache, r1, r2, mPaint);
			}else{
				child.draw(canvas);
			}
		}else{
			canvas.drawBitmap(cache, child.getLeft(), child.getTop(), mPaint);
		}
		canvas.restoreToCount(saveCount);
		return true;
	}
	/**
	 * Open/close public methods
	 */
	public void open(boolean animate){
		Log.d("ALLAPPSGRID","OPEN: children="+getChildCount());
		setCacheColorHint(0);
        setDrawingCacheBackgroundColor(0);
		clearChildrenCache();
		setChildrenDrawingCacheEnabled(true);
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(false);
		if(animate){
			isAnimating=true;
			mStatus=OPENING;
		}else{
			isAnimating=false;
			mStatus=OPEN;
		}
		startTime=0;
		this.setVisibility(View.VISIBLE);
		invalidate();
	}
	public void close(boolean animate){
        setCacheColorHint(0);
        setDrawingCacheBackgroundColor(0);
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(false);
		clearChildrenCache();
		setChildrenDrawingCacheEnabled(true);
		if(animate){
			mStatus=CLOSING;
			isAnimating=true;
		}else{
			mStatus=CLOSED;
			isAnimating=false;
			setVisibility(View.GONE);
		}
		startTime=0;
		invalidate();
	}
}
