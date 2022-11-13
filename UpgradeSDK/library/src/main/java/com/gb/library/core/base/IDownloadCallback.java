package com.gb.library.core.base;

import android.net.Uri;

public interface IDownloadCallback {
    void onStart();
    void onProgress(int size, int downloadedSize);
    void onSuccess(Uri downloadFileUri);
    void onFail(UpgradeException exception);
}
