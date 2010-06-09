package com.android.launcher;
import android.content.Context;
import android.content.SharedPreferences;

public final class AlmostNexusSettingsHelper {
	private static final String ALMOSTNEXUS_PREFERENCES = "launcher.preferences.almostnexus";
	
	public static int getDesktopScreens(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int screens = sp.getInt("desktopScreens", context.getResources().getInteger(R.integer.config_desktopScreens))+2;
		return screens;
	}
	public static int getDefaultScreen(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int def_screen = sp.getInt("defaultScreen", context.getResources().getInteger(R.integer.config_defaultScreen));
		return def_screen;
	}
	public static int getColumnsPortrait(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int screens = sp.getInt("drawerColumnsPortrait", context.getResources().getInteger(R.integer.config_drawerColumnsPortrait))+1;
		return screens;
	}
	public static int getRowsPortrait(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int screens = sp.getInt("drawerRowsPortrait", context.getResources().getInteger(R.integer.config_drawerRowsPortrait))+1;
		return screens;
	}
	public static int getColumnsLandscape(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int screens = sp.getInt("drawerColumnsLandscape", context.getResources().getInteger(R.integer.config_drawerColumnsLandscape))+1;
		return screens;
	}
	public static int getRowsLandscape(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int screens = sp.getInt("drawerRowsLandscape", context.getResources().getInteger(R.integer.config_drawerRowsLandscape))+1;
		return screens;
	}
	public static boolean getDrawerAnimated(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean animated = sp.getBoolean("drawerAnimated", context.getResources().getBoolean(R.bool.config_drawerAnimated));
		return animated;
	}
	public static boolean getDrawerNew(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("drawerNew", context.getResources().getBoolean(R.bool.config_drawerNew));
		return newD;
	}
	public static boolean getDesktopRotation(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("desktopRotation",context.getResources().getBoolean(R.bool.config_desktopRotation));
		return newD;
	}
	public static boolean getHideStatusbar(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("hideStatusbar", context.getResources().getBoolean(R.bool.config_hideStatusbar));
		return newD;
	}
	public static boolean getNewPreviews(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("previewsNew", context.getResources().getBoolean(R.bool.config_previewsNew));
		return newD;
	}
	public static boolean getFullScreenPreviews(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("previewsFullScreen", context.getResources().getBoolean(R.bool.config_previewsFullScreen));
		return newD;
	}
	public static int getHomeBinding(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = Integer.valueOf(sp.getString("homeBinding", context.getResources().getString(R.string.config_homeBinding)));
		return newD;
	}
	public static boolean getUIDots(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiDots", context.getResources().getBoolean(R.bool.config_uiDots));
		return newD;
	}
	public static boolean getUIDockbar(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiDockbar", context.getResources().getBoolean(R.bool.config_uiDockbar));
		return newD;
	}
	public static boolean getUICloseDockbar(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiCloseDockbar", context.getResources().getBoolean(R.bool.config_uiCloseDockbar));
		return newD;
	}
	public static boolean getUILAB(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiLAB", context.getResources().getBoolean(R.bool.config_uiLAB));
		return newD;
	}
	public static boolean getUIRAB(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiRAB", context.getResources().getBoolean(R.bool.config_uiRAB));
		return newD;
	}
	public static boolean getUITint(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiTint", context.getResources().getBoolean(R.bool.config_uiTint));
		return newD;
	}
	public static int getDesktopSpeed(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("desktopSpeed", context.getResources().getInteger(R.integer.config_desktopSpeed));
		return newD;
	}
	public static int getDesktopBounce(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("desktopBounce", context.getResources().getInteger(R.integer.config_desktopBounce));
		return newD;
	}
	public static boolean getUIAppsBg(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiAppsBg", context.getResources().getBoolean(R.bool.config_uiAppsBg));
		return newD;
	}
	public static boolean getUIABBg(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiABBg", context.getResources().getBoolean(R.bool.config_uiABBg));
		return newD;
	}
	public static int getZoomSpeed(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("zoomSpeed", context.getResources().getInteger(R.integer.config_zoomSpeed))+300;
		return newD;
	}
	public static float getuiScaleAB(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("uiScaleAB", context.getResources().getInteger(R.integer.config_uiScaleAB))+1;
		float scale=(float)newD/10f;
		return scale;
	}
	public static boolean getUIHideLabels(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiHideLabels", context.getResources().getBoolean(R.bool.config_uiHideLabels));
		return newD;
	}
	public static boolean getWallpaperHack(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("wallpaperHack", context.getResources().getBoolean(R.bool.config_wallpaperHack));
		return newD;
	}
	public static int getDrawerAlpha(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("drawerAlpha", context.getResources().getInteger(R.integer.config_drawerAlpha));
		return newD;
	}
	public static int getHighlightsColor(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("highlights_color", context.getResources().getInteger(R.integer.config_highlights_color));
		return newD;
	}
	public static int getHighlightsColorFocus(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("highlights_color_focus", context.getResources().getInteger(R.integer.config_highlights_color_focus));
		return newD;
	}
	public static boolean getUINewSelectors(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiNewSelectors", context.getResources().getBoolean(R.bool.config_new_selectors));
		return newD;
	}
	public static int getDrawerColor(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, Context.MODE_PRIVATE);
		int newD = sp.getInt("drawer_color", context.getResources().getInteger(R.integer.config_drawer_color));
		return newD;
	}
}
