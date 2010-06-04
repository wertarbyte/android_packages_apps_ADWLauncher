package com.android.launcher;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
public final class AlmostNexusSettingsHelper {
	private static final String ALMOSTNEXUS_PREFERENCES = "launcher.preferences.almostnexus";
	
	public static int getDesktopScreens(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int screens = sp.getInt("desktopScreens", 3)+2;
		return screens;
	}
	public static int getDefaultScreen(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int screens= getDesktopScreens(context);
		int def_screen = sp.getInt("defaultScreen", 2);
		return def_screen;
	}
	public static int getColumnsPortrait(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int screens = sp.getInt("drawerColumnsPortrait", 3)+1;
		return screens;
	}
	public static int getRowsPortrait(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int screens = sp.getInt("drawerRowsPortrait", 3)+1;
		return screens;
	}
	public static int getColumnsLandscape(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int screens = sp.getInt("drawerColumnsLandscape", 4)+1;
		return screens;
	}
	public static int getRowsLandscape(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int screens = sp.getInt("drawerRowsLandscape", 2)+1;
		return screens;
	}
	public static boolean getDrawerAnimated(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean animated = sp.getBoolean("drawerAnimated", true);
		return animated;
	}
	public static boolean getDrawerFast(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean fast = sp.getBoolean("drawerFast", true);
		return fast;
	}
	public static boolean getDrawerNew(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("drawerNew", true);
		return newD;
	}
	public static boolean getDesktopRotation(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("desktopRotation", true);
		return newD;
	}
	public static boolean getHideStatusbar(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("hideStatusbar", false);
		return newD;
	}
	public static boolean getNewPreviews(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("previewsNew", true);
		return newD;
	}
	public static boolean getFullScreenPreviews(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("previewsFullScreen", true);
		return newD;
	}
	public static int getHomeBinding(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int newD = Integer.valueOf(sp.getString("homeBinding", "3"));
		return newD;
	}
	public static boolean getUIDots(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiDots", true);
		return newD;
	}
	public static boolean getUIDockbar(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiDockbar", true);
		return newD;
	}
	public static boolean getUICloseDockbar(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiCloseDockbar", false);
		return newD;
	}
	public static boolean getUILAB(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiLAB", true);
		return newD;
	}
	public static boolean getUIRAB(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiRAB", true);
		return newD;
	}
	public static boolean getUITint(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiTint", false);
		return newD;
	}
	public static int getDesktopSpeed(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int newD = sp.getInt("desktopSpeed", 750);
		return newD;
	}
	public static int getDesktopBounce(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int newD = sp.getInt("desktopBounce", 40);
		return newD;
	}
	public static boolean getUIAppsBg(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiAppsBg", false);
		return newD;
	}
	public static boolean getUIABBg(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiABBg", false);
		return newD;
	}
	public static int getZoomSpeed(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int newD = sp.getInt("zoomSpeed", 400)+300;
		return newD;
	}
	public static float getuiScaleAB(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int newD = sp.getInt("uiScaleAB", 6)+1;
		float scale=(float)newD/10f;
		return scale;
	}
	public static boolean getUIHideLabels(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("uiHideLabels", false);
		return newD;
	}
	public static boolean getWallpaperHack(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		boolean newD = sp.getBoolean("wallpaperHack", true);
		return newD;
	}
	public static int getDrawerAlpha(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int newD = sp.getInt("drawerAlpha", 255);
		return newD;
	}
	public static int getHighlightsColor(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ALMOSTNEXUS_PREFERENCES, context.MODE_PRIVATE);
		int newD = sp.getInt("highlights_color", 0xffff6600);
		return newD;
	}
}
