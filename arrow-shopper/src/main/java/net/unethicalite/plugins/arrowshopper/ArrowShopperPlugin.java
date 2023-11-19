package net.unethicalite.plugins.arrowshopper;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.NPC;
import net.runelite.api.GameState;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.items.Shop;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;
import net.unethicalite.api.entities.NPCs;


@Extension
@PluginDescriptor(
        name = "nhArrowShopper",
        description = "Buy/Sell Rune arrows",
        enabledByDefault = false,
        tags = {"example"}
)

@Singleton
public class ArrowShopperPlugin extends Plugin
{
    @Inject
    ArrowShopperOverlay overlay;
    @Inject
    ArrowShopperConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Client client;
    private Logger log = Logger.getLogger(getName());
    boolean startPlugin;
    Instant botTimer;
    boolean initialized = false;
    ArrowShopperType type;
    @Provides
    ArrowShopperConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ArrowShopperConfig.class);
    }

    public ArrowShopperPlugin()
    {
        botTimer = null;
        startPlugin = false;
        type = null;
    }

    private void reset()
    {
        startPlugin = false;
        overlayManager.remove(overlay);
        botTimer = null;
        type = null;
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("Arrow-Shopper"))
        {
            return;
        }
        if (configButtonClicked.getKey().equals("startPlugin"))
        {
            if (!startPlugin)
            {
                Player player = client.getLocalPlayer();
                if (client != null && player != null && client.getGameState() == GameState.LOGGED_IN)
                {
                    startPlugin = true;
                    botTimer = Instant.now();
                    overlayManager.add(overlay);
                    type = config.shopperType();
                }
            }
            else
            {
                reset();
            }
        }
    }

    @Override
    protected void startUp()
    {

    }

    public void initialize()
    {
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            NPC seller = NPCs.getNearest("Market seller");
            if (seller == null)
            {
                shutDown();
                return;
            }
            seller.interact("Trade");
        }
        initialized = true;
    }
    
    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (startPlugin)
        {
            if (!initialized)
            {
                initialize();
            }
            if (type.equals(ArrowShopperType.BUY))
            {
                if (!Shop.isOpen()) return;
                List<Integer> items = Shop.getItems();
                if (Shop.getStock(892) > 0)
                {
                    switch (config.shopperQuantity())
                    {
                        case ONE:
                            Shop.buyOne(892);
                            break;
                        case FIVE:
                            Shop.buyFive(892);
                            break;
                        case TEN:
                            Shop.buyTen(892);
                            break;
                        case FIFTY:
                            Shop.buyFifty(892);
                            break;
                    }

                }
            }
            if (type.equals(ArrowShopperType.SELL))
            {
                if (!Shop.isOpen()) return;
                List<Integer> items = Shop.getItems();
                if (Shop.getStock(892) < 5)
                {
                        switch (config.shopperQuantity())
                        {
                            case ONE:
                                Shop.sellOne(892);
                                break;
                            case FIVE:
                                Shop.sellFive(892);
                                break;
                            case TEN:
                                Shop.sellTen(892);;
                                break;
                            case FIFTY:
                                Shop.sellFifty(892);
                                break;
                        }
                }
            }
        }
    }
    @Override
    protected void shutDown()
    {
        reset();
    }
}