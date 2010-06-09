package com.android.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;

public class IconHighlights {
	public IconHighlights(Context context) {
		// TODO Auto-generated constructor stub
	}
	private static Drawable newSelector(Context context){
		GradientDrawable mDrawPressed;
		GradientDrawable mDrawSelected;
		StateListDrawable drawable=new StateListDrawable();
		int selectedColor=AlmostNexusSettingsHelper.getHighlightsColorFocus(context);
		int pressedColor=AlmostNexusSettingsHelper.getHighlightsColor(context);
		int stateFocused = android.R.attr.state_focused;
		int statePressed = android.R.attr.state_pressed;
		int stateWindowFocused = android.R.attr.state_window_focused;
		 
		mDrawSelected = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
		        new int[] { 0x77FFFFFF, selectedColor,selectedColor,selectedColor,selectedColor, 0x77000000 });
		mDrawSelected.setShape(GradientDrawable.RECTANGLE);
		mDrawSelected.setGradientRadius((float)(Math.sqrt(2) * 60));
		mDrawSelected.setCornerRadius(8);
		mDrawPressed = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
				new int[] { 0x77FFFFFF, pressedColor,pressedColor,pressedColor,pressedColor, 0x77000000 });
		mDrawPressed.setShape(GradientDrawable.RECTANGLE);
		mDrawPressed.setGradientRadius((float)(Math.sqrt(2) * 60));
		mDrawPressed.setCornerRadius(8);

		drawable.addState(new int[]{ statePressed}, mDrawPressed);
		drawable.addState(new int[]{ stateFocused, stateWindowFocused}, mDrawSelected);
		drawable.addState(new int[]{stateFocused, -stateWindowFocused}, null);
		drawable.addState(new int[]{-stateFocused, stateWindowFocused}, null);
		drawable.addState(new int[]{-stateFocused, -stateWindowFocused}, null);
		return drawable;
	}
	private static Drawable oldSelector(Context context){
		int selectedColor=AlmostNexusSettingsHelper.getHighlightsColorFocus(context);
		int pressedColor=AlmostNexusSettingsHelper.getHighlightsColor(context);
		int stateFocused = android.R.attr.state_focused;
		int statePressed = android.R.attr.state_pressed;
		int stateWindowFocused = android.R.attr.state_window_focused;
		Drawable mDrawPressed;
		Drawable mDrawSelected;
		StateListDrawable drawable=new StateListDrawable();
		
		Resources res = context.getResources();
		mDrawPressed=res.getDrawable(R.drawable.pressed_application_background);
		mDrawSelected=res.getDrawable(R.drawable.focused_application_background);
		//TODO:ADW This doesn't work and i don't know why
		//So for now i'll use only one colour for tinting both drawables
		//mDrawPressed.setColorFilter(pressedColor, Mode.SRC_ATOP);
		//mDrawSelected.setColorFilter(selectedColor, Mode.SRC_ATOP);
		
		drawable.addState(new int[]{ statePressed}, mDrawPressed);
		drawable.addState(new int[]{ stateFocused, stateWindowFocused}, mDrawSelected);
		drawable.addState(new int[]{stateFocused, -stateWindowFocused}, null);
		drawable.addState(new int[]{-stateFocused, stateWindowFocused}, null);
		drawable.addState(new int[]{-stateFocused, -stateWindowFocused}, null);
		drawable.setColorFilter(pressedColor, Mode.SRC_ATOP);
		return drawable;
	}
	public static Drawable getDrawable(Context context){
		if(AlmostNexusSettingsHelper.getUINewSelectors(context)){
			return newSelector(context);
		}else{
			return oldSelector(context);
		}
	}
}
