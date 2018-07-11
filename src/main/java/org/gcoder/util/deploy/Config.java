package org.gcoder.util.deploy;

import com.typesafe.config.ConfigFactory;

public class Config {

    public static final String BUCKET_NAME = "oss.bucketName";
    public static final String ENDPOINT = "oss.endpoint";
    public static final String ACCESS_KEY_ID = "oss.accessKeyId";
    public static final String SECRET_ACCESS_KEY = "oss.secretAccessKey";

    public static final String DIST_DIR = "dist";


    public static final Config INSTANCE = new Config();

    private com.typesafe.config.Config cfg;

    private Config() {
        cfg = ConfigFactory.load();
    }

    public static Config getInstance(){
        return INSTANCE;
    }

    com.typesafe.config.Config getCfg() {
        return  cfg;
    }

    public static int getInt(String key) {
        return INSTANCE.getCfg().getInt(key);
    }

    public static String getString(String key) {
        return INSTANCE.getCfg().getString(key);
    }

}
