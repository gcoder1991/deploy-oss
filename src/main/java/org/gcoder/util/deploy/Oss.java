package org.gcoder.util.deploy;

import com.aliyun.oss.OSSClient;

public class Oss extends OSSClient {

    public static Oss INSTANCE = new Oss();

    private Oss(){
        super(Config.getString("oss.endpoint"),
                Config.getString("oss.accessKeyId"),
                Config.getString("oss.secretAccessKey"));
    }

    public static Oss getInstance() {
        return INSTANCE;
    }







}
