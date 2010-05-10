package com.android.launcher;

import java.util.Calendar;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class MyLauncherSettings extends PreferenceActivity implements OnPreferenceChangeListener {
    private static final String ALMOSTNEXUS_PREFERENCES = "launcher.preferences.almostnexus";
    private boolean shouldRestart=false;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		//TODO: ADW should i read stored values after addPreferencesFromResource?
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(ALMOSTNEXUS_PREFERENCES);
        addPreferencesFromResource(R.xml.launcher_settings);
        dlgSeekBarPreference desktopScreens= (dlgSeekBarPreference) findPreference("desktopScreens");
        desktopScreens.setMin(2);
        desktopScreens.setOnPreferenceChangeListener(this);
        dlgSeekBarPreference defaultScreen= (dlgSeekBarPreference) findPreference("defaultScreen");
        defaultScreen.setMin(1);
        defaultScreen.setMax(AlmostNexusSettingsHelper.getDesktopScreens(this)-1);
        defaultScreen.setOnPreferenceChangeListener(this);
        Preference drawerFast = (Preference) findPreference("drawerFast");
        drawerFast.setOnPreferenceChangeListener(this);
        Preference drawerNew = (Preference) findPreference("drawerNew");
        drawerNew.setOnPreferenceChangeListener(this);
        dlgSeekBarPreference columnsPortrait= (dlgSeekBarPreference) findPreference("drawerColumnsPortrait");
        columnsPortrait.setMin(1);
        dlgSeekBarPreference rowsPortrait= (dlgSeekBarPreference) findPreference("drawerRowsPortrait");
        rowsPortrait.setMin(1);
        dlgSeekBarPreference columnsLandscape= (dlgSeekBarPreference) findPreference("drawerColumnsLandscape");
        columnsLandscape.setMin(1);
        dlgSeekBarPreference rowsLandscape= (dlgSeekBarPreference) findPreference("drawerRowsLandscape");
        rowsLandscape.setMin(1);
    }
	@Override
	protected void onPause(){
		if(shouldRestart){
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
		}
		super.onPause();
	}
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals("desktopScreens")) {
			dlgSeekBarPreference pref = (dlgSeekBarPreference) findPreference("defaultScreen");
			pref.setMax((Integer) newValue+1);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("This setting will cause launcher to restart")
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
			builder.setMessage("This setting will cause launcher to restart")
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
			builder.setMessage("This setting will cause launcher to restart")
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							shouldRestart=true;
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}else if(preference.getKey().equals("drawerFast")){
			boolean val=Boolean.parseBoolean(newValue.toString());
			boolean newDrawer=AlmostNexusSettingsHelper.getDrawerNew(getApplicationContext());
			if(!val && !newDrawer){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Setting this to OFF will cause launcher to restart")
				       .setCancelable(false)
				       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
								shouldRestart=true;
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
        return true;  
	}
    
}
