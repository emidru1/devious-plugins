package net.unethicalite.plugins.nhbirdhouses;
import com.google.inject.Provides;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.inject.Inject;

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
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import org.pf4j.Extension;

@PluginDescriptor(
        name = "Nh Bird Houses",
        description = "Automatic birdhouse runner plugin",
        enabledByDefault = false)
@Slf4j
public class NhBirdhousesPlugin extends Plugin {
    @Inject
    NhBirdhousesConfig config;
    @Inject
    private Client client;
    NhBirdhousesState currentState;
    boolean startPlugin;
    Instant botTimer;
    @Provides
    public NhBirdhousesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(NhBirdhousesConfig.class);
    }

    private void reset()
    {

    }
    @Override
    protected void startUp()
    {

    }
    public void initialize()
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
            //if start, else reset
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {

    }

    @Override
    protected void shutDown()
    {
        reset();
    }
}
