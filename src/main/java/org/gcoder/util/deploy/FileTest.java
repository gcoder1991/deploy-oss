package org.gcoder.util.deploy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileTest {

    public static void main(String[] args) {

        try {
            Map<String, Path> map = new HashMap<>();
            Path path = Paths.get(Config.getString("dist"));
            listFiles(path, map);

            for (Map.Entry<String, Path> stringPathEntry : map.entrySet()) {
                System.out.println(stringPathEntry.getKey() + " == ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listFiles(Path p, Map<String, Path> m, String... r) throws IOException {

        String head = "";

        for (String s : r) {
            head += s;
        }
        if (r.length > 0) {
            head += "/";
        }

        List<Path> collect = Files.list(p).collect(Collectors.toList());
        for (Path path : collect) {
            if(Files.isDirectory(path)) {
                try {
                    listFiles(path, m, head + path.getFileName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                m.put(head + path.getFileName(), path);
            }
        }

    }

}
