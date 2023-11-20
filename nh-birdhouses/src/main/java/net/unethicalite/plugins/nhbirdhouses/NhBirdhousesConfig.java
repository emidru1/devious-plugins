package net.unethicalite.plugins.nhbirdhouses;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Config;
import net.unethicalite.plugins.nhbirdhouses.utils.Log;

@ConfigGroup("nhbirdhouses")
public interface NhBirdhousesConfig extends Config {
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

    @ConfigItem(keyName = "startPlugin", name = "Start/Stop", description = "", position = 1, title = "startPlugin")
    default Button startPlugin()
    {
        return new Button();
    }
}
