package com.android.launcher;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.util.Log;

public class dlgSeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
  private static final String androidns="http://schemas.android.com/apk/res/android";

  private SeekBar mSeekBar;
  private TextView mValueText;
  private Context mContext;
  private String mDialogMessage, mSuffix;
  private int mDefault, mMax, mMin, mValue = 0;

  public dlgSeekBarPreference (Context context, AttributeSet attrs) { 
    super(context,attrs); 
    setPersistent(true);
    mContext = context;

    mDialogMessage = attrs.getAttributeValue(androidns,"dialogMessage");
    mSuffix = attrs.getAttributeValue(androidns,"text");
    mDefault = attrs.getAttributeIntValue(androidns,"defaultValue", 0);
    mMax = attrs.getAttributeIntValue(androidns,"max", 100);
    setDialogLayoutResource(R.layout.my_seekbar_preference);
  }
  @Override 
  protected void onBindDialogView(View v) {
    super.onBindDialogView(v);
    TextView dialogMessage=(TextView) v.findViewById(R.id.dialogMessage);
    dialogMessage.setText(mDialogMessage);
    mValueText=(TextView) v.findViewById(R.id.actualValue);
    mValue = getPersistedInt(mDefault);
    mSeekBar=(SeekBar) v.findViewById(R.id.myBar);
    mSeekBar.setOnSeekBarChangeListener(this);
    mSeekBar.setMax(mMax);
    mSeekBar.setProgress(mValue);
    String t = String.valueOf(mValue+mMin);
    mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));    
    
  }
  @Override
  protected void onSetInitialValue(boolean restore, Object defaultValue)  
  {
    super.onSetInitialValue(restore, defaultValue);
    if (restore) 
      mValue = getPersistedInt(mDefault);
    else 
      mValue = (Integer)defaultValue;
  }
  @Override
  protected void onDialogClosed(boolean positiveResult) {
      super.onDialogClosed(positiveResult);
      
      if (positiveResult) {
          int value = mSeekBar.getProgress();
          if (callChangeListener(value)) {
              setValue(value);
          }
      }
  }
  public void setValue(int value){
	  if(value>mMax){
		  value=mMax;
	  }else if(value<0){
		  value=0;
	  }
	  mValue=value;
	  persistInt(value);
  }
  public void setMax(int max){
	  mMax=max;
	  if(mValue>mMax){
		  setValue(mMax);
	  }
  }
  public void setMin(int min){
	  if(min<mMax){
		  mMin=min;
	  }
  }
  public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
  {
    String t = String.valueOf(value+mMin);
    mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
  }
  public void onStartTrackingTouch(SeekBar seek) {}
  public void onStopTrackingTouch(SeekBar seek) {}
  

}