## 升级sdk

## 协议

### 版本检测
request:
|参数|类型|是否可空|描述|
|---|---|---|---|
|pname|String|否|当前app包名
|vc_apk|long|否|当前app最新版本
|vc_web|long|是|当前web离线包最新版本
|vc_mini|long|是|当前小程序最新版本
|country|String|否|国家代码

response:
|参数|类型|是否可空|描述|
|---|---|---|---|
|code|int|否|响应结果
|data|List&lt;UpgradeItem&gt;|是|失败或者无更新为空，非空则需升级
|msg|String|是|结果描述

UpgradeItem:
|参数|类型|是否可空|描述|
|---|---|---|---|
|type|int|否|类型 0-apk 1-web 2-mini
|latestVersion|int|否|当前最新版本
|size|int|否|升级包大小 单位：Byte
|downloadUrl|String|否|最新版本下载地址
|signType|String|否|签名类型 默认:md5
|signature|String|否|签名值
|mimeType|String|是|文件类型


## 使用

### 版本检测地址配置及相关代理类

```
        UpgradeConfig config = new UpgradeConfig.Builder()
                .setConnectTimeout(10*1000)
                .setReadTimeout(10 * 1000)
                .setCountry("CN")
                .setDownloadDir("downloadDir")
                .setNetProxy(netProxy)
                .setDataParser(dataParser)
                .setSignVerifier(signVerifier)
                .build(url);

        UpgradeManager.getInstance(context).setConfig(config);
```

### 版本检测
```
        UpgradeManager.getInstance(context).checkVersion(curWebVersion, curMiniProgramVersion, new IDataCallback<ResponseData>() {
            @Override
            public void onSuccess(ResponseData data) {
                // do somthing on UI thread
                // 获取到需要更新的包后 按需调用UpgradeManager.getInstance(this).download 进行下载升级
            }

            @Override
            public void onFail(@NonNull UpgradeException exception) {
                   // do somthing on UI thread
            }
        });
```

### 发起版本下载
```
        UpgradeManager.getInstance(context).download(upgradeItem, new IDownloadCallback() {
            @Override
            public void onStart() {
                
            }

            @Override
            public void onProgress(int size, int downloadedSize) {

            }

            @Override
            public void onSuccess(Uri downloadFileUri) {
                // 下载成功后的文件uri
            }

            @Override
            public void onFail(UpgradeException exception) {

            }
        });
```

### 错误码
```
   /** 未设置版本更新接口地址 */
   public static final int CODE_NOT_SET_FETCH_URL = 1001;
   /** 拉最新版本失败 可能是网络异常 */
   public static final int CODE_FETCH_FAIL = 1002;
   /** 请求失败  参考异常msg*/
   public static final int CODE_FETCH_RESPONSE_FAIL = 1003;
   /** 请求失败  系统异常*/
   public static final int CODE_FETCH_SYSTEM_ERROR = 1004;

   /** 下载失败 下载链接无效 */
   public static final int CODE_DOWNLOAD_URL_INVALID = 2001;
   /** 系统异常 */
   public static final int CODE_DOWNLOAD_SYSTEM_ERR = 2002;
   /** 下载目录创建失败 */
   public static final int CODE_DOWNLOAD_DIR_ERR = 2003;
   /** 进度查询失败 */
   public static final int CODE_DOWNLOAD_PROGRESS = 2004;
   /** 下载终断 */
   public static final int CODE_DOWNLOAD_ABORT = 2005;
   /** 下载成功 验签失败 */
   public static final int CODE_DOWNLOAD_SIGN_ERR = 2006;
```