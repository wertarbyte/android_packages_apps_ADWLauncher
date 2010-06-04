package org.adw.launcher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class IconHighlights extends StateListDrawable {
	private GradientDrawable mDrawPressed;
	private GradientDrawable mDrawSelected;
	public IconHighlights(Context context) {
		// TODO Auto-generated constructor stub
		newSelector(context);
	}
	private void newSelector(Context context){
		int selectedColor=AlmostNexusSettingsHelper.getHighlightsColor(context);
		int pressedColor=AlmostNexusSettingsHelper.getHighlightsColor(context);
		int stateFocused = android.R.attr.state_focused;
		int statePressed = android.R.attr.state_pressed;
		int stateWindowFocused = android.R.attr.state_window_focused;
		 
		mDrawSelected = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
		        new int[] { 0x77FFFFFF, selectedColor, 0x77000000 });
		mDrawSelected.setShape(GradientDrawable.RECTANGLE);
		mDrawSelected.setGradientRadius((float)(Math.sqrt(2) * 60));
		mDrawSelected.setCornerRadius(8);
		mDrawPressed = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
				new int[] { 0x77FFFFFF, pressedColor,	0x77000000 });
		mDrawPressed.setShape(GradientDrawable.RECTANGLE);
		mDrawPressed.setGradientRadius((float)(Math.sqrt(2) * 60));
		mDrawPressed.setCornerRadius(8);

        addState(new int[]{ statePressed}, mDrawPressed);
        addState(new int[]{ stateFocused, stateWindowFocused}, mDrawSelected);
		addState(new int[]{stateFocused, -stateWindowFocused}, null);
		addState(new int[]{-stateFocused, stateWindowFocused}, null);
		addState(new int[]{-stateFocused, -stateWindowFocused}, null);
	}
}
