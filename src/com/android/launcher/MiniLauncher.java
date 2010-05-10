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

import java.util.ArrayList;

import com.android.launcher.CellLayout.LayoutParams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.Toast;


public class MiniLauncher extends ViewGroup implements View.OnLongClickListener, DropTarget, DragController.DragListener {
    private static final int HORIZONTAL=1;
    private static final int VERTICAL=0;
	private Launcher mLauncher;
    private DragLayer mDragLayer;
    private View mDeleteView;
    private int mOrientation=HORIZONTAL;
    private int mNumCells=4;
    private int mCellWidth=20;
    private int mCellHeight=20;
    private TransitionDrawable mBackground;
    public MiniLauncher(Context context) {
        super(context);
    }

    public MiniLauncher(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniLauncher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
		
		//this.setOnClickListener(this);
		//this.setOnLongClickListener(this);
		setHapticFeedbackEnabled(true);
		TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.MiniLauncher,defStyle,0);
		mOrientation=a.getInt(R.styleable.MiniLauncher_orientation, mOrientation);
		mNumCells=a.getInt(R.styleable.MiniLauncher_cells, mNumCells);
		mCellWidth=a.getDimensionPixelSize(R.styleable.MiniLauncher_cellWidth, mCellWidth);
		mCellHeight=a.getDimensionPixelSize(R.styleable.MiniLauncher_cellHeight, mCellHeight);
		//Log.d("MINILAUNCHER","We have XCELLS"+mNumCells);
    }

    public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
		return true;
    }
    
    public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo, Rect recycle) {
        return null;
    }
    /**
     * Adds the specified child in the specified screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param screen The screen in which to add the child.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     * @param insert When true, the child is inserted at the beginning of the children list.
     */
      
    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
        //TODO:ADW Limit to X items till i manage to add scroll, removing, etc
        if(getChildCount()>=mNumCells){
        	Toast t=Toast.makeText(getContext(), "sorry, "+mNumCells+" items max... atm :-)", Toast.LENGTH_SHORT);
        	t.show();
        	return;
        }
    	ItemInfo info = (ItemInfo) dragInfo;
        switch (info.itemType) {
        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
        case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
        case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
        	//we do accept those
        	break;
        case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
        	Toast t=Toast.makeText(getContext(), "Widgets not supported... sorry :-)", Toast.LENGTH_SHORT);
        	t.show();
        	return;
        default:
        	Toast t2=Toast.makeText(getContext(), "Unknown item. We can't add unknown item types :-)", Toast.LENGTH_SHORT);
        	t2.show();
        	return;
        }
        info.cellX=getChildCount();
        //add it to launcher database
        final LauncherModel model = Launcher.getModel();
        model.addDesktopItem(info);
        LauncherModel.addOrMoveItemInDatabase(mLauncher, info,
                LauncherSettings.Favorites.CONTAINER_DOCKBAR, -1, getChildCount(), -1);        
        addItemInDockBar(info);
    }

    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
    	mBackground.startTransition(200);
    }

    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
    }

    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
            Object dragInfo) {
    	mBackground.resetTransition();
    }

    public void onDragStart(View v, DragSource source, Object info, int dragAction) {

    }

    public void onDragEnd() {

    }
    public void addItemInDockBar(ItemInfo info){
    	View view=null;
        switch (info.itemType) {
        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
            if (info.container == NO_ID) {
                // Came from all apps -- make a copy
                info = new ApplicationInfo((ApplicationInfo) info);
            }
            view = mLauncher.createSmallShortcut(R.layout.small_application, this,
                    (ApplicationInfo) info);
            break;
        case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
            view = mLauncher.createSmallLiveFolder(R.layout.small_application, this,
                    (LiveFolderInfo) info);
            break;
        case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
            view = mLauncher.createSmallFolder(R.layout.small_application, this,
                    (UserFolderInfo) info);
            break;
        case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
        	//Toast t=Toast.makeText(getContext(), "Widgets not supported... sorry :-)", Toast.LENGTH_SHORT);
        	//t.show();
        	return;
        default:
        	//Toast t2=Toast.makeText(getContext(), "Unknown item. We can't add unknown item types :-)", Toast.LENGTH_SHORT);
        	//t2.show();
        	return;
            //throw new IllegalStateException("Unknown item type: " + info.itemType);
        }
        view.setLongClickable(true);
        view.setOnLongClickListener(this);
        addView(view);
        invalidate();
    }

	public boolean onLongClick(View v) {
		//Log.d("DockBar","We are LONGclicking this view!"+v);
		mDeleteView=v;
		new AlertDialog.Builder(getContext())
			  .setTitle("Confirm")
		      .setMessage("Confirm delete item?")
		      .setPositiveButton("Yes", deleteShortcut)
			  .setNegativeButton("No", cancelDelete)
		      .show();
		return true;
	}
	
	DialogInterface.OnClickListener deleteShortcut =
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//ADW Delete the item and reposition the remaining ones
				//FIRS DELETE deleteView
				ItemInfo item=(ItemInfo) mDeleteView.getTag();
		        final LauncherModel model = Launcher.getModel();
	            if (item instanceof LauncherAppWidgetInfo) {
	                model.removeDesktopAppWidget((LauncherAppWidgetInfo) item);
	            } else {
	                model.removeDesktopItem(item);
	            }
		        if (item instanceof UserFolderInfo) {
		            final UserFolderInfo userFolderInfo = (UserFolderInfo)item;
		            LauncherModel.deleteUserFolderContentsFromDatabase(mLauncher, userFolderInfo);
		            model.removeUserFolder(userFolderInfo);
		        } else if (item instanceof LauncherAppWidgetInfo) {
		            final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
		            final LauncherAppWidgetHost appWidgetHost = mLauncher.getAppWidgetHost();
		            if (appWidgetHost != null) {
		                appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
		            }
		        }
		        LauncherModel.deleteItemFromDatabase(mLauncher, item);
		        detachViewFromParent(mDeleteView);
		        removeView(mDeleteView);
				//Now we need to update database (and position) for remainint items
				final int count=getChildCount();
				for(int i=0;i<count;i++){
					final View cell=getChildAt(i);
					final ItemInfo info = (ItemInfo) cell.getTag();
	                if(info.cellX>item.cellX){
	                	info.cellX-=1;
	                	cell.setTag(info);
		                LauncherModel.moveItemInDatabase(mLauncher, info,
		                        LauncherSettings.Favorites.CONTAINER_DOCKBAR, -1, info.cellX, -1);
	                }
	                Log.d("MINILAUNCHER","We've removed children!!!");
                }
				requestLayout();
				mDeleteView=null;
			}
	};

	DialogInterface.OnClickListener cancelDelete =
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
	};
	
    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }

    void setDragController(DragLayer dragLayer) {
        mDragLayer = dragLayer;
    }


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBackground=(TransitionDrawable) getBackground();
		mBackground.setCrossFadeEnabled(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
        	child.measure(mCellWidth, mCellHeight);
        }		
		
		if(mOrientation==HORIZONTAL){
			super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mCellHeight, MeasureSpec.AT_MOST));
		}else{
			super.onMeasure(MeasureSpec.makeMeasureSpec(mCellWidth, MeasureSpec.AT_MOST),heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int count = getChildCount();
		
		
		int marginLeft=((getMeasuredWidth())/2)-(((count*mCellWidth)/2));
		int marginTop=((getMeasuredHeight())/2)-(((count*mCellHeight)/2));
		
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ItemInfo item=(ItemInfo) child.getTag();
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                //int childLeft=(mOrientation==HORIZONTAL)?marginLeft+(i*mCellWidth):0;
                //int childTop = (mOrientation==VERTICAL)?marginTop+(i*mCellHeight):0;
                int childLeft=(mOrientation==HORIZONTAL)?marginLeft+(item.cellX*mCellWidth):0;
                int childTop = (mOrientation==VERTICAL)?marginTop+(item.cellX*mCellHeight):0;
                int childRight = childLeft+mCellWidth;
                int childBottom = childTop+mCellHeight;
                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }
		
	}
}
