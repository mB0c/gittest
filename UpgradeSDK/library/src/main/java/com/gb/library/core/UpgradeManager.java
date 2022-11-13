package com.gb.library.core;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gb.library.core.base.IDownloadCallback;
import com.gb.library.core.base.ISignVerifier;
import com.gb.library.impl.DefaultNetImpl;
import com.gb.library.core.base.IDataCallback;
import com.gb.library.core.base.INetProxy;
import com.gb.library.core.base.UpgradeConfig;
import com.gb.library.core.base.UpgradeException;
import com.gb.library.core.data.RequestData;
import com.gb.library.core.data.ResponseData;
import com.gb.library.core.data.UpgradeItem;
import com.gb.library.impl.DefaultSignVerifier;
import com.gb.library.utils.Utils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpgradeManager {

    private static final String TAG = UpgradeManager.class.getSimpleName();

    private static volatile UpgradeManager mInstance;

    private final Context mContext;
    private UpgradeConfig mConfig;

    private final Handler mHandler;

    private INetProxy mDefaultNetImpl;

    private UpgradeManager(Context ctx){
        this.mContext = ctx.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static UpgradeManager getInstance(Context ctx) {
        if(mInstance == null) {
            synchronized (UpgradeManager.class) {
                if(mInstance == null) {
                    mInstance = new UpgradeManager(ctx);
                }
            }
        }
        return mInstance;
    }

    /**
     * 配置版本检测地址、国家代码、网络、数据转换、验签代理类
     * @param config
     */
    public void setConfig(@NonNull UpgradeConfig config){
        this.mConfig = config;
    }


    /**
     * 检测最新版本
     * @param curWebVersion 当前web离线包版本
     * @param curMiniProgramVersion 当前小程序包版本
     * @param callback 结果回调 UI线程
     */
    public void checkVersion(long curWebVersion, long curMiniProgramVersion,
                             @NonNull IDataCallback<ResponseData> callback) {

        IDataCallback<ResponseData> dataCallback = new IDataCallback<ResponseData>() {
            @Override
            public void onSuccess(ResponseData data) {
                mHandler.post(() -> callback.onSuccess(data));
            }

            @Override
            public void onFail(@NonNull UpgradeException exception) {
                mHandler.post(() -> callback.onFail(exception));
            }
        };

        if(mConfig == null) {
            dataCallback.onFail(
                    new UpgradeException(UpgradeException.CODE_NOT_SET_FETCH_URL,"fetchLatestVerUrl not found!"));
            return;
        }

        getSingleThreadExecutorForFetch(dataCallback).execute(() ->
                getNetProxyImpl().fetchLatestVersion(mConfig.getFetchLatestVerUrl(),
                        new RequestData(mContext.getPackageName(),
                                Utils.getAppVersionCode(mContext),
                                curWebVersion, curMiniProgramVersion, mConfig.getCountry()),
                        dataCallback));

    }


    /**
     * 下载升级包
     * 签名类型及签名值都不存在时 则不验签
     * @param item
     * @param callback
     */
    public void download(@NonNull UpgradeItem item, @NonNull IDownloadCallback callback){

        IDownloadCallback downloadCallback = new IDownloadCallback() {
            @Override
            public void onStart() {
                mHandler.post(callback::onStart);
            }
            @Override
            public void onProgress(int size, int downloadedSize) {
                mHandler.post(() -> callback.onProgress(size, downloadedSize));
            }
            @Override
            public void onSuccess(Uri downloadFileUri) {
                //校验下载文件包
                if(checkDownloadFile(item, downloadFileUri)) {
                    mHandler.post(() -> callback.onSuccess(downloadFileUri));
                } else {
                    mHandler.post(() -> callback.onFail(new UpgradeException(UpgradeException.CODE_DOWNLOAD_SIGN_ERR)));
                }
            }
            @Override
            public void onFail(@NonNull UpgradeException exception) {
                mHandler.post(() -> callback.onFail(exception));
            }
        };

        getSingleThreadExecutorForDownload(callback).execute(() -> {
            Uri downloadDir = makeDownloadDir();
            if(downloadDir == null) {
                downloadCallback.onFail(new UpgradeException(UpgradeException.CODE_DOWNLOAD_DIR_ERR));
                return;
            }
            getNetProxyImpl().download(item, downloadDir.toString(), downloadCallback);
        });

    }

    private ExecutorService getSingleThreadExecutorForFetch(@NonNull IDataCallback<ResponseData> callback){
        return Executors.newSingleThreadExecutor(runnable -> {

            Thread result = new Thread(runnable, "Thread-" + TAG + "-fetch");
            result.setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                Log.e(TAG, "thread:"+t.getName()+" is shutdown! error: " + e.getMessage());
                callback.onFail(new UpgradeException(UpgradeException.CODE_FETCH_SYSTEM_ERROR));
            });
            result.setDaemon(false);
            return result;

        });
    }

    private ExecutorService getSingleThreadExecutorForDownload(@NonNull IDownloadCallback callback){
        return Executors.newSingleThreadExecutor(runnable -> {

            Thread result = new Thread(runnable, "Thread-" + TAG + "-download");
            result.setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                Log.e(TAG, "thread:"+t.getName()+" is shutdown! error: " + e.getMessage());
                callback.onFail(new UpgradeException(UpgradeException.CODE_DOWNLOAD_SYSTEM_ERR));
            });
            result.setDaemon(false);
            return result;

        });
    }

    /**
     * 校验下载文件签名
     * @param item  UpgradeItem
     * @param downloadFileUri 下载文件存储路径
     * @return true-成功 false=失败
     */
    private boolean checkDownloadFile(UpgradeItem item, Uri downloadFileUri) {

        //如果没有签名信息 默认不验签
        if(TextUtils.isEmpty(item.getSignType()) && TextUtils.isEmpty(item.getSignature())) {
            return true;
        }

        ISignVerifier signVerifier = getSignVerifierImpl();
        if(signVerifier.checkSupport(item.getSignType())) {
            return signVerifier.checkSign(item.getSignType(), item.getSignature(), downloadFileUri);
        }
        return false;
    }

    private synchronized INetProxy getNetProxyImpl(){
        if(mConfig != null && mConfig.getNetProxy() != null) {
            return mConfig.getNetProxy();
        } else {
            if(mDefaultNetImpl == null) {
                mDefaultNetImpl = new DefaultNetImpl(mContext, mConfig);
            }
            return mDefaultNetImpl;
        }
    }

    private ISignVerifier getSignVerifierImpl(){
        if(mConfig != null && mConfig.getSignVerifier() != null) {
            return mConfig.getSignVerifier();
        } else {
            return new DefaultSignVerifier();
        }
    }

    /**
     * 准备下载目录
     * @return Uri
     */
    private Uri makeDownloadDir(){
        File downloadDir = new File(mContext.getFilesDir(),mConfig.getDownloadDir());
        if(!downloadDir.exists()) {
            if(!downloadDir.mkdirs()) {
                return null;
            }
        }
        return Uri.fromFile(downloadDir);
    }

}
