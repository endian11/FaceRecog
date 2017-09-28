package facemeet.cigit.com.facedemet.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lizhanwei on 17/9/21.
 */

public class SharedUtil {
    public static final String IP = "sp_ip";
    public static final String PORT ="sp_port" ;

    public static void setSharedprefer(String action, Object value, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("aaa",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        if (value instanceof String){

            editor.putString(action, (String) value);

        }else if (value instanceof Integer){
            editor.putInt(action, (Integer) value);
        }else if (value instanceof Boolean){
            editor.putBoolean(action, (Boolean) value);
        }else if (value instanceof Float){
            editor.putFloat(action, (Float) value);
        }else if (value instanceof Long){
            editor.putLong(action, (Long) value);
        }

        editor.commit();

    }


    public static <T>T getValue(Context context,String action,T defaultvalue,ShareType shareType){
        SharedPreferences sharedPreferences = context.getSharedPreferences("aaa",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        Object value = null;
        switch (shareType){
            case STRING:
                value = sharedPreferences.getString(action, (String) defaultvalue);
                break;
            case INTEGER:
                value = sharedPreferences.getInt(action, (Integer) defaultvalue);
                break;
            case LONG:
                value = sharedPreferences.getLong(action, (Long) defaultvalue);
                break;
            case FLOAT:
                value = sharedPreferences.getFloat(action, (Float) defaultvalue);
                break;
            case BOOLEAN:
                value = sharedPreferences.getBoolean(action, (Boolean) defaultvalue);
                break;
        }
        return (T)value;
    }
    public enum ShareType{
        STRING,INTEGER,LONG,FLOAT,BOOLEAN
    }
}
