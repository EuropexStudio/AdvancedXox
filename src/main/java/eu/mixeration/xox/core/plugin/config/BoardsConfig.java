/*
 * MIT License
 *
 * Copyright (c) 2020 Luke Anderson (stuntguy3000)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.mixeration.xox.core.plugin.config;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.Board;
import eu.mixeration.xox.core.plugin.PluginConfig;
import eu.mixeration.xox.core.plugin.PluginConfigData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

/**
 * Represents the configuration file to store board locations
 */
@EqualsAndHashCode(callSuper = true)
@PluginConfigData(configFilename = "boards")
@Getter
@Setter
public class BoardsConfig extends PluginConfig {
    private HashMap<UUID, Board> savedBoards = new HashMap<>();

    public BoardsConfig() {
        super("boards");
    }

    public static BoardsConfig getConfig() {
        return (BoardsConfig) PluginMain.getInstance().getConfigHandler().getConfig(BoardsConfig.class.getAnnotation(PluginConfigData.class).configFilename());
    }

    @Override
    public PluginConfig getSampleConfig() {
        return new BoardsConfig();
    }
}
