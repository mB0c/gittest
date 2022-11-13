package com.gb.library.impl;

import android.net.Uri;
import android.text.TextUtils;

import com.gb.library.core.base.ISignVerifier;

import java.util.Collections;
import java.util.List;

public class DefaultSignVerifier implements ISignVerifier {

   private final List<String> SUPPORT_TYPES = Collections.singletonList("md5");
   @Override
   public boolean checkSupport(String type) {
      if(TextUtils.isEmpty(type)) return false;
      return SUPPORT_TYPES.contains(type.toLowerCase());
   }

   @Override
   public boolean checkSign(String signType, String originSign, Uri filePath) {
      return TextUtils.equals(originSign, getMd5(filePath));
   }

   private String getMd5(Uri filePath) {
      return "";
   }

}
