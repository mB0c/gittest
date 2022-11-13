package com.gb.library.core.base;

import android.net.Uri;

public interface ISignVerifier {

    boolean checkSupport(String type);
    boolean checkSign(String signType, String originSign, Uri filePath);
    
}
