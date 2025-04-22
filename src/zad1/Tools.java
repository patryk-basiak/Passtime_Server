/**
 *
 *  @author Basiak Patryk S30757
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Tools {
    public static Options createOptionsFromYaml(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = Files.newInputStream(Paths.get(fileName));
        HashMap yamlMap = yaml.load(inputStream);
        String host = (String)  yamlMap.get("host");
        int port = (int)  yamlMap.get("port");
        boolean concurMode = (boolean)  yamlMap.get("concurMode");
        boolean showSendRes = (boolean)  yamlMap.get("showSendRes");
        HashMap clientsMap = (HashMap) yamlMap.get("clientsMap");
        return new Options(host, port, concurMode, showSendRes, clientsMap);
    }
}
