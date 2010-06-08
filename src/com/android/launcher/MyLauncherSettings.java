package com.android.launcher;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import java.util.Calendar;

public class MyLauncherSettings extends PreferenceActivity implements OnPreferenceChangeListener {
    private static final String ALMOSTNEXUS_PREFERENCES = "launcher.preferences.almostnexus";
    private boolean shouldRestart=false;
//    private static final String FROYOMSG="Changing this setting will make the Launcher restart itself";
//    private static final String NORMALMSG="Changing this setting will make the Launcher restart itself";
    private String mMsg;
    private Context mContext;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		//TODO: ADW should i read stored values after addPreferencesFromResource?
        if (Build.VERSION.SDK_INT >= 8)
            mMsg = getString(R.string.pref_message_restart_froyo);
        else
            mMsg = getString(R.string.pref_message_restart_normal);
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(ALMOSTNEXUS_PREFERENCES);
        addPreferencesFromResource(R.xml.launcher_settings);
        DialogSeekBarPreference desktopScreens= (DialogSeekBarPreference) findPreference("desktopScreens");
        desktopScreens.setMin(2);
        desktopScreens.setOnPreferenceChangeListener(this);
        DialogSeekBarPreference defaultScreen= (DialogSeekBarPreference) findPreference("defaultScreen");
        defaultScreen.setMin(1);
        defaultScreen.setMax(AlmostNexusSettingsHelper.getDesktopScreens(this)-1);
        defaultScreen.setOnPreferenceChangeListener(this);
        Preference drawerNew = (Preference) findPreference("drawerNew");
        drawerNew.setOnPreferenceChangeListener(this);
        DialogSeekBarPreference columnsPortrait= (DialogSeekBarPreference) findPreference("drawerColumnsPortrait");
        columnsPortrait.setMin(1);
        DialogSeekBarPreference rowsPortrait= (DialogSeekBarPreference) findPreference("drawerRowsPortrait");
        rowsPortrait.setMin(1);
        DialogSeekBarPreference columnsLandscape= (DialogSeekBarPreference) findPreference("drawerColumnsLandscape");
        columnsLandscape.setMin(1);
        DialogSeekBarPreference rowsLandscape= (DialogSeekBarPreference) findPreference("drawerRowsLandscape");
        rowsLandscape.setMin(1);
        DialogSeekBarPreference zoomSpeed= (DialogSeekBarPreference) findPreference("zoomSpeed");
        zoomSpeed.setMin(300);
        DialogSeekBarPreference uiScaleAB= (DialogSeekBarPreference) findPreference("uiScaleAB");
        uiScaleAB.setMin(1);
        Preference donateLink = (Preference) findPreference("donatePref");
        donateLink.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=9S8WKFETUYRHG";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);				
				return true;
			}
		});
        Preference uiHideLabels = (Preference) findPreference("uiHideLabels");
        uiHideLabels.setOnPreferenceChangeListener(this);
        Preference uiNewSelectors = (Preference) findPreference("uiNewSelectors");
        uiNewSelectors.setOnPreferenceChangeListener(this);
        mContext=this;
    }
	@Override
	protected void onPause(){
		if(shouldRestart){
			if(Build.VERSION.SDK_INT<=7){
				Intent intent = new Intent(getApplicationContext(), Launcher.class);
	            PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),0, intent, 0);
	
	            // We want the alarm to go off 30 seconds from now.
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTimeInMillis(System.currentTimeMillis());
	            calendar.add(Calendar.SECOND, 1);
	
	            // Schedule the alarm!
	            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	   			ActivityManager acm = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		        acm.restartPackage("com.android.launcher");
			}else{
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}
		super.onPause();
	}
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals("desktopScreens")) {
			DialogSeekBarPreference pref = (DialogSeekBarPreference) findPreference("defaultScreen");
			pref.setMax((Integer) newValue+1);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(mMsg)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}else if (preference.getKey().equals("defaultScreen")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(mMsg)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();		
		}else if(preference.getKey().equals("drawerNew")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(mMsg)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}else if(preference.getKey().equals("uiHideLabels")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(mMsg)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}else if(preference.getKey().equals("uiNewSelectors")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(mMsg)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
        return true;  
	}

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("highlights_color")) {
        	ColorPickerDialog cp = new ColorPickerDialog(this,mHighlightsColorListener,readHighlightsColor());
        	cp.show();
        }else if(preference.getKey().equals("highlights_color_focus")) {
        	ColorPickerDialog cp = new ColorPickerDialog(this,mHighlightsColorFocusListener,readHighlightsColorFocus());
        	cp.show();
        }
        return false;
	}
    private int readHighlightsColor() {
    	return AlmostNexusSettingsHelper.getHighlightsColor(this);
    }

    ColorPickerDialog.OnColorChangedListener mHighlightsColorListener =
    	new ColorPickerDialog.OnColorChangedListener() {
    	public void colorChanged(int color) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(mMsg)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
    		getPreferenceManager().getSharedPreferences().edit().putInt("highlights_color", color).commit();
    	}
    };
    private int readHighlightsColorFocus() {
    	return AlmostNexusSettingsHelper.getHighlightsColor(this);
    }

    ColorPickerDialog.OnColorChangedListener mHighlightsColorFocusListener =
    	new ColorPickerDialog.OnColorChangedListener() {
    	public void colorChanged(int color) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(mMsg)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
    		getPreferenceManager().getSharedPreferences().edit().putInt("highlights_color_focus", color).commit();
    	}
    };
}

