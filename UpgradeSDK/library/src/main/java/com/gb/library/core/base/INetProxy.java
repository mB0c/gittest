package com.gb.library.core.base;

import androidx.annotation.NonNull;

import com.gb.library.core.data.RequestData;
import com.gb.library.core.data.ResponseData;
import com.gb.library.core.data.UpgradeItem;

public interface INetProxy {

    void fetchLatestVersion(String requestUrl,
                            RequestData requestData,
                            @NonNull IDataCallback<ResponseData> callback);

    void download(UpgradeItem item, String downloadPath, IDownloadCallback callback);
}
