

package eu.mixeration.xox.core.plugin;

import eu.mixeration.xox.PluginMain;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class PluginConfig {
    @Getter
    private transient String configName;

    public abstract PluginConfig getSampleConfig();

    public void saveConfig() {
        PluginMain.getInstance().getConfigHandler().saveConfiguration(this);
    }
}
