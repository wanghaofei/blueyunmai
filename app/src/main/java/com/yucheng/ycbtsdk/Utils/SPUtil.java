package com.yucheng.ycbtsdk.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
  private static Context spContext;
  private static String spFileName = "ycblespinfo";

  public static void init(Context context){
    spContext = context;
  }

  public static void saveBindedDeviceMac(String mac){
    SharedPreferences sharedPreferences = spContext.getSharedPreferences(spFileName,
            Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("ycble_bindedmac", mac);
    editor.commit();

  }

  public static String getBindedDeviceMac(){
    SharedPreferences sharedPreferences = spContext.getSharedPreferences(spFileName,
            Context.MODE_PRIVATE);

    return sharedPreferences.getString("ycble_bindedmac", "");
  }


  public static void saveBindedDeviceName(String deviceName){
    SharedPreferences sharedPreferences = spContext.getSharedPreferences(spFileName,
            Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("ycble_bindedname", deviceName);
    editor.commit();

  }

  public static String getBindedDeviceName(){
    SharedPreferences sharedPreferences = spContext.getSharedPreferences(spFileName,
            Context.MODE_PRIVATE);

    return sharedPreferences.getString("ycble_bindedname", "");
  }
}

    
