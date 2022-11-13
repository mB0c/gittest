package com.gb.library.impl;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gb.library.core.base.IDataCallback;
import com.gb.library.core.base.IDataConverter;
import com.gb.library.core.base.IDownloadCallback;
import com.gb.library.core.base.INetProxy;
import com.gb.library.core.base.UpgradeConfig;
import com.gb.library.core.base.UpgradeException;
import com.gb.library.core.data.RequestData;
import com.gb.library.core.data.ResponseData;
import com.gb.library.core.data.UpgradeItem;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DefaultNetImpl implements INetProxy {

   private OkHttpClient client;

   private IDataConverter mDataConverter;

   public static final MediaType JSON = MediaType.get("application/json");

   private Context mContext;

   /** 监听DownloadManager下载状态 */
   private BroadcastReceiver downloadReceiver;

   /** 轮询下载进度 */
   private Timer timer;

   private IDataConverter getDataConverterImpl(UpgradeConfig mConfig) {
      if(mConfig != null && mConfig.getDataConverter() != null) {
         return mConfig.getDataConverter();
      }
      return new DefaultDataConverterImpl();
   }

   public DefaultNetImpl(Context ctx, UpgradeConfig config){
      this.mContext = ctx;
      client = new OkHttpClient.Builder()
              .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
              .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
              .build();
      this.mDataConverter = getDataConverterImpl(config);
   }

   @Override
   public void fetchLatestVersion(String requestUrl,
                                  RequestData requestData,
                                  @NonNull IDataCallback<ResponseData> callback) {


      RequestBody body = RequestBody.create(mDataConverter.convertRequest(requestData), JSON);

      Request request = new Request.Builder()
              .url(requestUrl)
              .post(body)
              .build();

      ResponseData retData = null;
      UpgradeException exception = null;

      try(Response res = client.newCall(request).execute()) {
         if(res.isSuccessful() && res.body() != null) {
            String resStr = res.body().string();
            retData = mDataConverter.convertResponse(resStr);
            callback.onSuccess(retData);
            return;
         } else {
            exception = new UpgradeException(UpgradeException.CODE_FETCH_RESPONSE_FAIL, res.code()+" and response.body() may be null");
         }
      } catch (IOException e) {
         exception = new UpgradeException(UpgradeException.CODE_FETCH_FAIL, e.getMessage());
      }

      callback.onFail(exception);
   }

   @Override
   public void download(UpgradeItem item, String downloadPath, IDownloadCallback callback) {

      if(!checkItem(item, callback)) {
         return;
      }

      DownloadManager.Request dReq = new DownloadManager.Request(Uri.parse(item.getDownloadUrl()));

      dReq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN); // 隐藏通知栏

      if(!TextUtils.isEmpty(item.getMimeType())) {
         dReq.setMimeType(item.getMimeType());
      }

      String fileName = item.getDownloadUrl().substring(item.getDownloadUrl().lastIndexOf('/') + 1);
      Uri fileUri = Uri.fromFile(new File(downloadPath, item.getLatestVersion() + fileName));

      dReq.setDestinationUri(fileUri);

      DownloadManager dm = (DownloadManager) this.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
      callback.onStart();
      long reqId = dm.enqueue(dReq);

      // 启动进度轮询
      pollDownloadProgress(reqId, fileUri, callback);

   }

   private boolean checkItem(UpgradeItem item, IDownloadCallback callback) {
      if(TextUtils.isEmpty(item.getDownloadUrl())) {
         callback.onFail(new UpgradeException(UpgradeException.CODE_DOWNLOAD_URL_INVALID));
         return false;
      }
      return true;
   }

   private void pollDownloadProgress(long reqId, Uri fileUri, IDownloadCallback callback) {

      if(timer != null) {
         timer.cancel();
         timer = null;
      }
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            queryDownloadStatus(reqId, fileUri, callback);
         }
      }, 0, 1000);

   }

   private void queryDownloadStatus(long reqId, Uri fileUri, IDownloadCallback callback) {
      DownloadManager.Query query = new DownloadManager.Query().setFilterById(reqId);
      Cursor cursor = null;

      DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
      try {
         cursor = dm.query(query);
         if(cursor != null && cursor.moveToFirst()) {

            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            if(status == DownloadManager.STATUS_RUNNING) {
               int downloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
               int total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

               callback.onProgress(total, downloaded);

            } else if(status == DownloadManager.STATUS_SUCCESSFUL) {
               cancelTimer();
               callback.onSuccess(fileUri);
            } else if(status != DownloadManager.STATUS_PENDING) {
               cancelTimer();
               callback.onFail(new UpgradeException(UpgradeException.CODE_DOWNLOAD_ABORT));
            }
         }

      } catch (Exception exception) {
         callback.onFail(new UpgradeException(UpgradeException.CODE_DOWNLOAD_PROGRESS));
         cancelTimer();
      } finally {
        if(cursor != null) {
           cursor.close();
        }
      }
   }

   private void cancelTimer() {
      if(timer != null) {
         timer.cancel();
         timer = null;
      }
   }

}
