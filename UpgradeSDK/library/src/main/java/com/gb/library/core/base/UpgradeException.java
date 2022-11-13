package com.gb.library.core.base;

public class UpgradeException extends Exception {

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

   private final int code;
   public static final int CODE_DEFAULT = 1;

   public UpgradeException(int code, String msg) {
      super(msg);
      this.code = code;
   }

   public UpgradeException(int code,String message, Throwable cause) {
      super(message, cause);
      this.code = code;
   }

   public UpgradeException(Throwable cause) {
      super(cause);
      this.code = CODE_DEFAULT;
   }

   public UpgradeException(int code) {
      super();
      this.code = code;
   }

   public int getCode() {
      return code;
   }

}
