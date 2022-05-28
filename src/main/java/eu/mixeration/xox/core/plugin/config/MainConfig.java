
package eu.mixeration.xox.core.plugin.config;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.plugin.PluginConfig;
import eu.mixeration.xox.core.plugin.PluginConfigData;
import lombok.Getter;

@Getter
@PluginConfigData(configFilename = "main")
public class MainConfig extends PluginConfig {
    private boolean blockProtection = true;
    private boolean playerMoveEvents = true;
    private int maxPlayerBoardDistance = 10;
    private int endOfRoundSeconds = 3;

    public MainConfig() {
        super("main");
    }

    public static MainConfig getConfig() {
        return (MainConfig) PluginMain.getInstance().getConfigHandler().getConfig(MainConfig.class.getAnnotation(PluginConfigData.class).configFilename());
    }

    @Override
    public PluginConfig getSampleConfig() {
        return new MainConfig();
    }
}
