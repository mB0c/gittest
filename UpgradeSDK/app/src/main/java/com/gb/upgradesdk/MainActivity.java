package com.gb.upgradesdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.gb.library.core.UpgradeManager;
import com.gb.library.core.base.IDataCallback;
import com.gb.library.core.base.IDownloadCallback;
import com.gb.library.core.base.UpgradeConfig;
import com.gb.library.core.base.UpgradeException;
import com.gb.library.core.data.ResponseData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "";
        UpgradeConfig config = new UpgradeConfig.Builder()
                .setConnectTimeout(10*1000)
                .setReadTimeout(10 * 1000)
                .setCountry("CN")
                .setDownloadDir("downloadDir")
                .setNetProxy(netProxy)
                .setDataParser(dataParser)
                .setSignVerifier(signVerifier)
                .build(url);

        UpgradeManager.getInstance(this).setConfig(config);

        UpgradeManager.getInstance(this).checkVersion(curWebVersion, curMiniProgramVersion, new IDataCallback<ResponseData>() {
            @Override
            public void onSuccess(ResponseData data) {

            }

            @Override
            public void onFail(@NonNull UpgradeException exception) {

            }
        });

        UpgradeManager.getInstance(this).download(upgradeItem, new IDownloadCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int size, int downloadedSize) {

            }

            @Override
            public void onSuccess(Uri downloadFileUri) {

            }

            @Override
            public void onFail(UpgradeException exception) {

            }
        });

    }
}