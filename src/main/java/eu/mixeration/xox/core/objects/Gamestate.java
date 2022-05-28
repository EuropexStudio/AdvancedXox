

package eu.mixeration.xox.core.objects;

import eu.mixeration.xox.core.plugin.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Gamestate {
    NONE(Locale.GAMESTATE_NONE), WAITING(Locale.GAMESTATE_WAITING), INGAME(Locale.GAMESTATE_INGAME), END(Locale.GAMESTATE_END);

    @Getter
    private final String displayName;
}
