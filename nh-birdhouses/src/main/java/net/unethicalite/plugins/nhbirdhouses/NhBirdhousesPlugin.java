package net.unethicalite.plugins.nhbirdhouses;
import com.google.inject.Provides;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.plugins.nhbirdhouses.utils.Constants;
import org.pf4j.Extension;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.unethicalite.api.entities.TileObjects;

@Extension
@PluginDescriptor(
        name = "nhBirdhouses",
        description = "Automatic birdhouse runner plugin",
        enabledByDefault = false)
@Slf4j
@Singleton
public class NhBirdhousesPlugin extends Plugin {
    @Inject
    NhBirdhousesConfig config;
    @Inject
    private Client client;
    NhBirdhousesState currentState;
    Instant botTimer;
    boolean start;
    int actionRetryCount = 0;
    @Provides
    public NhBirdhousesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(NhBirdhousesConfig.class);
    }

    public NhBirdhousesPlugin() {
        botTimer = null;
        start = false;
        currentState = null;
    }
    private void reset()
    {
        start = false;
        botTimer = null;
        currentState = null;
    }
    @Override
    protected void startUp()
    {

    }


    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked) {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("nhbirdhouses"))
        {
            return;
        }
        if (configButtonClicked.getKey().equals("startButton"))
        {
            /* Currently after clicking start button, the script does not start. Nothing is logged.
                Check config variable values, fix naming, compare initialization to arrow shopper
            * */
            if (!start)
            {
                Player player = client.getLocalPlayer();
                if (client != null && player != null && client.getGameState() == GameState.LOGGED_IN)
                {
                    start = true;
                    botTimer = Instant.now();
                    currentState = NhBirdhousesState.START_BANK;
                    log.debug("Starting Birdhouse Runner");
                }
            }
            else
            {
                reset();
            }

        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if(start) {
            switch(currentState) {
                case START_BANK:
                    TileObject bankObject = TileObjects.getNearest(Predicates.ids(Constants.BANK_OBJECT_IDS));
                    if(bankObject == null) {
                        return;
                    }
                    GameThread.invoke(() -> bankObject.interact("Bank"));
                    break;
                case WITHDRAW_ITEMS:
                    break;
                case TELEPORT_DIGSITE:
                    break;
                case USE_MUSHROOM_TREE_HOUSE:
                    break;
                case MEADOW_HOUSE_1:
                    break;
                case MEADOW_HOUSE_2:
                    break;
                case USE_MUSHROOM_TREE_MEADOW:
                    break;
                case VALLEY_HOUSE_1:
                    break;
                case VALLEY_HOUSE_2:
            }
        }
        else {
            return;
        }
    }

    @Override
    protected void shutDown()
    {
        reset();
    }
}
