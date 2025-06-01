package xyz.sirblobman.votifier.tester;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteFormData {
    String hostName;
    int port;
    String publicKey;
    String serviceName;
    String username;
    String address;
    long timestamp;
    boolean saveAfterClose;

    void save() {
        File file = new File("votifier_tester.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create votifier_tester.yml file", e);
            }
        }
        Yaml yaml = new Yaml();
        try {
            yaml.dump(this, new java.io.FileWriter(file));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save data to votifier_tester.yml", e);
        }

    }
    public static VoteFormData fromYaml(String yaml) {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setTagInspector(tag -> tag.getClassName().equals(VoteFormData.class.getName()));
        Constructor constructor = new Constructor(VoteFormData.class, loaderOptions);
        Yaml yamlParser = new Yaml(constructor);
        try {
            return yamlParser.loadAs(yaml, VoteFormData.class);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Failed to parse YAML data", e);
        }
    }
}
