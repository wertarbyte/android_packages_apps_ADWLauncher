package com.android.launcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.launcher.DragController.DragListener;

public class ActionButton extends ImageView implements DropTarget, DragListener {
	private Launcher mLauncher;
    private DragLayer mDragLayer;
	private int mIdent=LauncherSettings.Favorites.CONTAINER_LAB;
	private ItemInfo mCurrentInfo;
	
	public ActionButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ActionButton(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public ActionButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setHapticFeedbackEnabled(true);
		TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.ActionButton,defStyle,0);
		mIdent=a.getInt(R.styleable.ActionButton_ident, mIdent);
	}

	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub
		return true;
	}

	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, Object dragInfo, Rect recycle) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub
		setPressed(true);
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub
		setPressed(false);

	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub

	}

	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub
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
        final LauncherModel model = Launcher.getModel();
        //TODO:ADW check this carefully
        //We need to remove current item from database before adding the new one
        if(mCurrentInfo!=null){
        	model.removeDesktopItem(mCurrentInfo);
        	LauncherModel.deleteItemFromDatabase(mLauncher, mCurrentInfo);
        }
        model.addDesktopItem(info);
        LauncherModel.addOrMoveItemInDatabase(mLauncher, info,
                mIdent, -1, -1, -1);        
        UpdateLaunchInfo(info);
	}
	protected void UpdateLaunchInfo(ItemInfo info){
    	mCurrentInfo=info;
		//TODO:ADW extract icon and put it as the imageview src...
		Drawable myIcon=null;
        switch (info.itemType) {
        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
            if (info.container == NO_ID) {
                // Came from all apps -- make a copy
                info = new ApplicationInfo((ApplicationInfo) info);
            }
            myIcon = mLauncher.createSmallActionButtonIcon(info);
            break;
        case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
        case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
            myIcon = mLauncher.createSmallActionButtonIcon(info);
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
        setImageDrawable(myIcon);
        invalidate();
	}

	public void onDragEnd() {
		// TODO Auto-generated method stub

	}

	public void onDragStart(View v, DragSource source, Object info,
			int dragAction) {
		// TODO Auto-generated method stub

	}
    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }

    void setDragController(DragLayer dragLayer) {
        mDragLayer = dragLayer;
    }

	@Override
	public Object getTag() {
		// TODO Auto-generated method stub
		return mCurrentInfo;
	}
	public void updateIcon(){
    	if(mCurrentInfo!=null){
			ItemInfo info=mCurrentInfo;
			Drawable myIcon=null;
	        switch (info.itemType) {
	        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
	        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
	            if (info.container == NO_ID) {
	                // Came from all apps -- make a copy
	                info = new ApplicationInfo((ApplicationInfo) info);
	            }
	            myIcon = mLauncher.createSmallActionButtonIcon(info);
	            break;
	        case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
	        case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
	            myIcon = mLauncher.createSmallActionButtonIcon(info);
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
	        setImageDrawable(myIcon);
	        invalidate();
    	}
	}
}
