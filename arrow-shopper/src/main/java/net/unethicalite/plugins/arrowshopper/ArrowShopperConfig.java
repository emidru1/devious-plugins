package net.unethicalite.plugins.arrowshopper;

import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Button;

@ConfigGroup("Arrow-Shopper")
public interface ArrowShopperConfig extends Config
{

    @ConfigItem(keyName = "startPlugin", name = "Start/Stop", description = "", position = 1, title = "startPlugin")
    default Button startPlugin()
    {
        return new Button();
    }

    @ConfigItem(
            keyName = "showOverlay",
            name = "Show UI",
            description = "Show the UI on screen",
            position = 2
    )
    default boolean showOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "botType",
            name = "Choose bot type",
            description = "Choose bot type for either Buying or Selling Rune Arrows",
            position = 3
    )
    default ArrowShopperType shopperType()
    {
        return ArrowShopperType.BUY;
    }

    @ConfigItem(
            keyName = "shopQuantity",
            name = "Arrow quantity",
            description = "Select quantity of arrows you want to sell/buy per tick",
            position = 4
    )
    default ArrowShopperQuantity shopperQuantity()
    {
        return ArrowShopperQuantity.ONE;
    }
}