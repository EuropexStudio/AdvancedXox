

package eu.mixeration.xox.core.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginConfigData {
    String configFilename();
}
