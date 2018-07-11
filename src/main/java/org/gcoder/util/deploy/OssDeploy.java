package org.gcoder.util.deploy;

import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 主要用来部署vue项目到阿里云oss
 */
public class OssDeploy {

    public static void main(String[] args) {

        String bucketName = Config.getString(Config.BUCKET_NAME);
        String bakBucketName = bucketName + "-bak";

        Oss oss = Oss.getInstance();

//        if (true) {
//            ObjectListing objectListing = oss.listObjects(bakBucketName);
//            while (!objectListing.getObjectSummaries().isEmpty()){
//                objectListing.getObjectSummaries().forEach(i->oss.deleteObject(bakBucketName, i.getKey()));
//                objectListing = oss.listObjects(bakBucketName);
//            }
//            return;
//        }


        if(!oss.doesBucketExist(bucketName)){
            System.err.println("BUCKET NOT EXIST");
            return;
        }

        if (!oss.doesBucketExist(bakBucketName)){
            oss.createBucket(bakBucketName);
        }



        LocalDateTime time = LocalDateTime.now();

        ObjectListing objs = oss.listObjects(bucketName);
        if (objs.getObjectSummaries().isEmpty()) {
            System.err.println("BUCKET IS EMPTY!");
        }

        try {
            while (!objs.getObjectSummaries().isEmpty()) {
                List<String> baks = new ArrayList<>();
                List<OSSObjectSummary> summaries = objs.getObjectSummaries();
                for (OSSObjectSummary summary : summaries) {
                    //oss.restoreObject(bakBucketName, summary.getKey());
                    Date lastModified = summary.getLastModified();
                    CopyObjectResult cpyRet = oss.copyObject(bucketName, summary.getKey(), bakBucketName,
                            time + "/" + summary.getKey());
                    if (cpyRet.getLastModified().equals(lastModified)) {
                        System.err.println("BAK FAILED, EXIT!");
                        return;
                    }
                    baks.add(summary.getKey());
                }
                baks.forEach(i->oss.deleteObject(bucketName, i));
                objs = oss.listObjects(bucketName);
            }
        } catch (Throwable t) {
            // TODO BAK THE DATA
        }

        Path path = Paths.get(Config.getString(Config.DIST_DIR));
        Map<String, Path> files = new HashMap<>();
        listFiles(path, files);

        try {
            for (Map.Entry<String,Path> file : files.entrySet()) {
                oss.putObject(bucketName, file.getKey(), Files.newInputStream(file.getValue()));
            }
        } catch (IOException e) {
            System.err.println("UPLOAD FAILED!");
            return;
        }

        System.out.println("SUCCESS END.");
    }

    public static void listFiles(Path p, Map<String, Path> m, String... r) {

        try {
        String head = "";

        for (String s : r) {
            head += s;
        }
        if (r.length > 0) {
            head += "/";
        }

        List<Path> collect = null;

        collect = Files.list(p).collect(Collectors.toList());

        for (Path path : collect) {
            if(Files.isDirectory(path)) {
                listFiles(path, m, head + path.getFileName());
            } else {
                m.put(head + path.getFileName(), path);
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
