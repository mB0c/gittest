package com.gb.library.core.base;

import android.net.Uri;
import android.text.TextUtils;

public class UpgradeConfig {

    /**
     * 业务方设置版本检测url
     */
    private String fetchLatestVerUrl;

    /**
     * 网络库代理 支持业务方切换
     */
    private INetProxy mNetProxy;

    /**
     * 数据报解析
     */
    private IDataConverter mDataConverter;

    /**
     * 文件验签
     */
    private ISignVerifier mSignVerifier;

    private int readTimeout;

    private int connectTimeout;

    /**
     * /data/{packageName}/cache/{your dir}
     *  默认：upgradeSdk
     */
    private String downloadDir;

    /** 国家代码 */
    private String country;

    private UpgradeConfig(
            String fetchLatestVerUrl,
            int readTimeout,
            int connectTimeout,
            INetProxy mNet,
            IDataConverter mDataParser,
            ISignVerifier mSignVerifier,
            String downloadDir,
            String country) {

        if(TextUtils.isEmpty(fetchLatestVerUrl)) {
            throw new IllegalArgumentException("Error: fetchLatestVerUrl must not be empty!");
        }

        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
        this.fetchLatestVerUrl = fetchLatestVerUrl;
        this.mNetProxy = mNet;
        this.mDataConverter = mDataParser;
        this.mSignVerifier = mSignVerifier;
        this.downloadDir = downloadDir;
        this.country = country;

    }

    public String getFetchLatestVerUrl() {
        return fetchLatestVerUrl;
    }

    public INetProxy getNetProxy() {
        return mNetProxy;
    }

    public IDataConverter getDataConverter() {
        return mDataConverter;
    }

    public ISignVerifier getSignVerifier() {
        return mSignVerifier;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public String getCountry() {
        return country;
    }

    public static final class Builder {

        private int readTimeout = 10 * 1000;
        private int connectTimeout = 10 * 1000;
        private INetProxy mNetProxy;
        private IDataConverter mDataParser;
        private ISignVerifier mSignVerifier;
        private String downloadDir = "upgradeSdk";
        private String country;


        public UpgradeConfig build(String fetchLatestVerUrl) {
            return new UpgradeConfig(fetchLatestVerUrl, this.readTimeout, this.connectTimeout,
                    this.mNetProxy, this.mDataParser, this.mSignVerifier, this.downloadDir, this.country);
        }

        /**
         * 设置读取超时时间
         * @param readTimeout 毫秒
         * @return
         */
        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * 连接超时时间
         * @param connectTimeout 毫秒
         * @return
         */
        public Builder setConnectTimeout (int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * 设置网络接口代理
         * @param net
         * @return
         */
        public Builder setNetProxy (INetProxy net) {
            this.mNetProxy = net;
            return this;
        }

        /**
         * 设置数据转换器，适配自有业务数据协议
         * @param dataParser
         * @return
         */
        public Builder setDataParser(IDataConverter dataParser) {
            this.mDataParser = dataParser;
            return this;
        }

        /**
         * 设置下载文件验签代理，SDK默认支持md5
         * @param signVerifier
         * @return
         */
        public Builder setSignVerifier(ISignVerifier signVerifier) {
            this.mSignVerifier = signVerifier;
            return this;
        }

        /**
         * 设置文件下载位置
         * 父目录：context.getFilesDir()
         * @param downloadDir 默认：upgradeSdk
         * @return
         */
        public Builder setDownloadDir(String downloadDir) {
            this.downloadDir = downloadDir;
            return this;
        }

        /**
         * 设置当前国家代码
         * @param country
         * @return
         */
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }



    }
}
