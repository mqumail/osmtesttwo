package com.example.osmtesttwo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class Utils {

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * AutoCompleteTextView taking its list of values from a shared preference.
     *
     * Use the static method storePreference(value) to add an entry in these preferences
     * (typically when the user "uses" a value he just entered in this edittext view).
     *
     * @author M.Kergall
     */
    public static class AutoCompleteOnPreferences extends AppCompatAutoCompleteTextView
    {

        public AutoCompleteOnPreferences(Context context) {
            super(context);
        }

        public AutoCompleteOnPreferences(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
        }

        public AutoCompleteOnPreferences(Context arg0, AttributeSet arg1, int arg2) {
            super(arg0, arg1, arg2);
        }

        @Override public boolean enoughToFilter() {
            return true;
        }

        @Override protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            if (focused){
                setPreferences();
                if (getAdapter()!=null) {
                    performFiltering(getText(), 0);
                }
            }
        }

        protected String mAppKey, mKey;

        /**
         * Specify which application key and which preference name will be used.
         * @param appKey Shared Preferences application key
         * @param prefName Preference name
         */
        public void setPrefKeys(String appKey, String prefName){
            mAppKey = appKey;
            mKey = prefName;
        }

        protected void setPreferences(){
            String[] prefs = getPreferences();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, prefs);
            this.setAdapter(adapter);
        }

        protected String[] getPreferences(){
            SharedPreferences prefs = getContext().getSharedPreferences(mAppKey, Context.MODE_PRIVATE);
            String prefString = prefs.getString(mKey, "[]");
            try {
                JSONArray prefArray = new JSONArray(prefString);
                String[] result = new String[prefArray.length()];
                for (int i=0; i<prefArray.length(); i++){
                    result[i] = prefArray.getString(i);
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return new String[0];
            }
        }

        /**
         * Add a value in a list of preferences referenced by an appKey and a prefName.
         * @param context
         * @param value to add in the list.
         * @param appKey application key
         * @param prefName name of the preferences list
         */
        public static void storePreference(Context context, String value, String appKey, String prefName){
            SharedPreferences prefs = context.getSharedPreferences(appKey, Context.MODE_PRIVATE);
            String prefValues = prefs.getString(prefName, "[]");
            JSONArray prefValuesArray;
            try {
                prefValuesArray = new JSONArray(prefValues);
                LinkedList<String> prefValuesList = new LinkedList<String>();

                for (int i=0; i<prefValuesArray.length(); i++){
                    String prefValue = prefValuesArray.getString(i);
                    if (!prefValue.equals(value))
                        prefValuesList.addLast(prefValue);
                    //else, don't add it => it will be added at the beginning, as a new one...
                }
                //add the new one at the beginning:
                prefValuesList.addFirst(value);
                //remove last entry if too much:
                if (prefValuesList.size()>20)
                    prefValuesList.removeLast();

                //Rebuild JSON string:
                prefValuesArray = new JSONArray();
                for (String s:prefValuesList){
                    prefValuesArray.put(s);
                }
                prefValues = prefValuesArray.toString();
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString(prefName, prefValues);
                ed.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

