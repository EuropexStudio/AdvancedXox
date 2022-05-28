package eu.mixeration.xox.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.plugin.PluginConfig;
import eu.mixeration.xox.core.plugin.MinecraftPlugin;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHandler {
    @Getter
    private final HashMap<String, PluginConfig> configClasses = new HashMap<>();
    @Getter
    private final Gson gson;
    @Getter
    private final List<String> loadedConfigs = new ArrayList<>();

    private final MinecraftPlugin plugin;

    public ConfigHandler() {
        this.plugin = PluginMain.getInstance();

        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        gson = builder.create();
    }

    public void registerConfiguration(PluginConfig pluginConfig) {
        configClasses.put(pluginConfig.getConfigName(), pluginConfig);
    }

    public void loadConfigurations() {
        for (Map.Entry<String, PluginConfig> config : new HashMap<>(configClasses).entrySet()) {
            File configFile = new File(plugin.getDataFolder() + File.separator + config.getKey() + ".json");

            try {
                if (!configFile.exists()) {
                    saveConfiguration(config.getValue());
                    loadConfigurations();
                    return;
                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);

                    configClasses.put(config.getKey(), gson.fromJson(inputStreamReader, config.getValue().getClass()));
                    loadedConfigs.add(config.getKey());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public PluginConfig getConfig(String name) {
        for (Map.Entry<String, PluginConfig> minecraftConfig : getConfigClasses().entrySet()) {
            if (minecraftConfig.getKey().equalsIgnoreCase(name)) {
                return minecraftConfig.getValue();
            }
        }

        return null;
    }

    public void saveConfiguration(PluginConfig pluginConfig) {
        File configFile = new File(plugin.getDataFolder() + File.separator + pluginConfig.getConfigName() + ".json");

        if (!loadedConfigs.contains(pluginConfig.getConfigName())) {
            loadedConfigs.add(pluginConfig.getConfigName());
        }

        String json = gson.toJson(pluginConfig);
        FileOutputStream outputStream;

        try {
            if (!configFile.exists()) {
                json = gson.toJson(pluginConfig.getSampleConfig());
                configFile.createNewFile();
            }
            outputStream = new FileOutputStream(configFile);
            assert json != null;
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

