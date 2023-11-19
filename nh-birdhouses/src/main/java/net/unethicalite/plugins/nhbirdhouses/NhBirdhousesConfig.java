package net.unethicalite.plugins.nhbirdhouses;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.unethicalite.plugins.nhbirdhouses.utils.Log;

@ConfigGroup("nhbirdhouses")
public interface NhBirdhousesConfig extends net.runelite.client.config.Config {
    @ConfigItem(
            keyName = "birdhouseRun",
            name = "Birdhouse run",
            description = "Do a birdhouse run",
            position = 2)
    default boolean birdhouseRun() {
        return true;
    }

    @ConfigItem(
            keyName = "logs",
            name = "Logs",
            description = "Select which logs to use.",
            position = 4)
    default Log logs() {
        return Log.YEW;
    }

    @ConfigItem(
            keyName = "startButton",
            name = "Force Start/Stop",
            description =
                    "The script should automatically start and stop. Use this button for manual "
                            + "overrides.",
            position = Integer.MAX_VALUE)
    default Button startButton() {
        return new Button();
    }
}
