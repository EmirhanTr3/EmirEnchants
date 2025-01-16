package xyz.emirdev.emirenchants;

import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigHandler {
    private static final File DATA_FILE = new File("plugins/EmirEnchants/config.yml");

    private YamlFile yamlFile;

    public ConfigHandler() {
        loadFile();

        this.yamlFile.setComment("disabled_enchants", "A list of disabled enchantments.");
        this.yamlFile.addDefault("disabled_enchants", List.of("example"));

        saveFile();
    }

    public void loadFile() {
        this.yamlFile = new YamlFile(DATA_FILE);
        try {
            this.yamlFile.createOrLoadWithComments();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFile() {
        try {
            this.yamlFile.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getDisabledEnchants() {
        return this.yamlFile.getStringList("disabled_enchants");
    }
}