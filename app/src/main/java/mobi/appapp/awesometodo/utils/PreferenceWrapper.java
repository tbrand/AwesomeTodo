package mobi.appapp.awesometodo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by taicsuzu on 2017/05/21.
 */

public class PreferenceWrapper {

    private static PreferenceWrapper preferenceWrapper;

    public static PreferenceWrapper getInstance(Context context){
        if(preferenceWrapper == null){
            preferenceWrapper = new PreferenceWrapper(context);
        }

        return preferenceWrapper;
    }

    private SharedPreferences preferences;

    private PreferenceWrapper(Context context){
        preferences = context.getSharedPreferences("awesome_todo_preference", Context.MODE_PRIVATE);
    }

    public void setMode(boolean mode){
        preferences.edit().putBoolean("mode", mode).apply();
    }

    public boolean getMode(){
        return preferences.getBoolean("mode", true);
    }
}
