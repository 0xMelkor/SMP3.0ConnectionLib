package stacksmashers.smp30connectionlib.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import stack_smashers.smpconnectionutilitieslib.R;

/**
 * Created by a.simeoni on 10/03/2017.
 */

public class SharedPreferenceAdapter {

    private final static String APP_ACCOUNT_PREFS_NAME = R.class.getPackage().getName() + ".prefs.setting";
    public final static String  SHARED_PREFS_KEY_SMP_CID = R.class.getPackage().getName() + ".smp_cid";
    public final static String  SHARED_PREFS_KEY_SMP_USERNAME = R.class.getPackage().getName() + ".smp_username";
    public final static String  SHARED_PREFS_KEY_SMP_PASSWORD = R.class.getPackage().getName() + ".smp_password";

    public static void setValueForKey(Context ctx, String key, String value){
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getValueForKey(Context ctx, String key){
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        return prefs.getString(key, null);
    }

    public static void clearAll(Context ctx){
        setValueForKey(ctx, SHARED_PREFS_KEY_SMP_CID, null);
        setValueForKey(ctx, SHARED_PREFS_KEY_SMP_USERNAME, null);
        setValueForKey(ctx, SHARED_PREFS_KEY_SMP_PASSWORD, null);
    }
}
