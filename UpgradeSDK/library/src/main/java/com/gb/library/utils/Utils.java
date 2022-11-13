package com.gb.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class Utils {

   public static long getAppVersionCode(Context context) {
      long appVersionCode = 0;
      try {
         PackageInfo packageInfo = context.getApplicationContext()
                 .getPackageManager()
                 .getPackageInfo(context.getPackageName(), 0);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            appVersionCode = packageInfo.getLongVersionCode();
         } else {
            appVersionCode = packageInfo.versionCode;
         }
      } catch (PackageManager.NameNotFoundException e) {
         Log.e("", e.getMessage());
      }
      return appVersionCode;
   }

   /**
    * 0-apk 1-web离线包 2-mini小程序包
    * @param type
    * @return
    */
   public static String getMimeTypeByType(int type){
      if(type == 0) {
         return "application/vnd.android.package-archive";
      }
      return "";
   }

}
