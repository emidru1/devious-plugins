package net.unethicalite.plugins.nhbirdhouses;
import com.google.inject.Provides;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.openosrs.client.util.Groups;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.items.Shop;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.plugins.nhbirdhouses.utils.Constants;
import org.pf4j.Extension;
import net.runelite.api.NPC;
import net.unethicalite.api.entities.TileObjects;
import static net.unethicalite.api.commons.Time.sleep;
import static net.unethicalite.api.commons.Time.sleepUntil;
@Extension
@PluginDescriptor(
        name = "nhBirdhouses",
        description = "Automatic birdhouse runner plugin",
        enabledByDefault = false)

@Singleton
public class NhBirdhousesPlugin extends LoopedPlugin
{
    @Inject
    NhBirdhousesConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Client client;
    private Logger log = Logger.getLogger(getName());
    boolean startPlugin;
    Instant botTimer;
    private boolean isCurrentTaskComplete = true;
    NhBirdhousesState currentState;
    //Add list of tasks, fill list of tasks with different tasks stored in different classes with loop conditions exec?
    @Provides
    public NhBirdhousesConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(NhBirdhousesConfig.class);
    }

    public NhBirdhousesPlugin()
    {
        botTimer = null;
        startPlugin = false;
        currentState = NhBirdhousesState.START_BANK;
    }

    private void reset()
    {
        startPlugin = false;
        botTimer = null;
        currentState = NhBirdhousesState.START_BANK;
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("nhbirdhouses"))
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
                }
            }
            else
            {
                reset();
            }
        }
    }

    public void startBank() {
        TileObject bankObject = TileObjects.getNearest(Predicates.ids(Constants.BANK_OBJECT_IDS));
        if (bankObject == null) {
            //log.info("Bank object is null");
            return;
        }
        if (bankObject != null) {
            //log.info("Bank object is NOT null");
            bankObject.interact("Use");
            sleepUntil(() -> Bank.isOpen(), 1000);
        } else {
            NPC bankNpc = NPCs.getNearest(Predicates.ids(Constants.BANK_NPC_IDS));

            if (bankNpc == null) {
                log.info("Bank NPC is null");
                return;
            }
            if (bankNpc.hasAction("Bank")) {
                bankNpc.interact("Bank");
                sleepUntil(() -> Bank.isOpen(), 1000);
            } else {
                GameThread.invoke(() -> bankNpc.interact(0));
                sleepUntil(() -> Bank.isOpen(), 1000);
            }
        }


    }
    public void withdrawItems() {
        /*
        * If teleport widget is open, player is teleported as expected. Actions need task scheduler to process asynchronous tasks
        * Currently asynchronous tasks upon completion cannot update plugin state successfully, so it gets stuck and state changes to STOP
        * Upon starting the plugin (Startbutton clicked), task list should be loaded (check melxin's zulrah plugin for reference)
        * */
        Bank.openMainTab();
        if(!inventoryIsEmpty()) {
            Bank.depositInventory();
        }
        //Bank.depositEquipment(); // disabled for testing purposes for tele out
        Bank.withdraw(
                i -> i.getId() == ItemID.IMCANDO_HAMMER || i.getId() == ItemID.HAMMER,
                1,
                Bank.WithdrawMode.ITEM);
        Bank.withdraw(ItemID.CHISEL, 1, Bank.WithdrawMode.ITEM);
        Bank.withdraw(config.logs().getId(), 4, Bank.WithdrawMode.ITEM);
        Bank.withdraw(Predicates.ids(Constants.DIGSITE_PENDANT_IDS), 1, Bank.WithdrawMode.ITEM);
        Bank.withdraw(getSeedId(), 40, Bank.WithdrawMode.ITEM);

        log.info("Inventory has everything needed, switching state");
    }
    public boolean inventoryHasEverything() {
        return (Inventory.contains(ItemID.IMCANDO_HAMMER, ItemID.HAMMER) || Equipment.contains(
                ItemID.IMCANDO_HAMMER))
                && Inventory.contains(ItemID.CHISEL)
                && (Inventory.contains(Predicates.ids(Constants.DIGSITE_PENDANT_IDS))
                && Inventory.getCount(config.logs().getId()) == 4
                && Inventory.getCount(true, Predicates.ids(Constants.BIRD_HOUSE_SEED_IDS)) == 40);
    }
    public void teleportUsingPendant() {
        closeBank();

        final Item pendant = Inventory.getFirst(Predicates.ids(Constants.DIGSITE_PENDANT_IDS));
        if(pendant == null) {
            return;
        }
        pendant.interact("Rub");
        Time.sleep(1000);
        Time.sleepTicksUntil(Dialog::isViewingOptions, 5);
        Dialog.chooseOption(2);
    }
    public boolean isMushroomTreeNearby() {
        TileObject tree = TileObjects.getNearest(i -> Constants.MAGIC_MUSHTREE_IDS.contains(i.getId()));
        if (tree == null) {
            return false;
        }
        return true;
    }
    public void teleportUsingTreeToValley() {
        TileObject tree = TileObjects.getNearest(i -> Constants.MAGIC_MUSHTREE_IDS.contains(i.getId()));
        if (tree == null) {
            return;
        }

        GameThread.invoke(() -> tree.interact(0));
        Time.sleepTicksUntil(
                () -> Widgets.isVisible(Widgets.get(WidgetInfo.FOSSIL_MUSHROOM_TELEPORT)), 15);

        Widget mushroomValleyWidget = Widgets.get(WidgetInfo.FOSSIL_MUSHROOM_VALLEY);
        if (!Widgets.isVisible(mushroomValleyWidget)) {
            return;
        }

        Time.sleepTicks(5);
        mushroomValleyWidget.interact(
                0,
                MenuAction.WIDGET_CONTINUE.getId(),
                mushroomValleyWidget.getIndex(),
                mushroomValleyWidget.getId());

    }
    public int getSeedId() {
        for (int id : Constants.BIRD_HOUSE_SEED_IDS) {
            if (Bank.getCount(true, id) >= 40) {
                return id;
            }
        }
        return 0;
    }
    public boolean bankIsOpen() {
        return Bank.isOpen();
    }
    public void closeBank() {
        Bank.close();
    }
    public boolean inventoryIsEmpty() {
        return Inventory.isEmpty();
    }
    public boolean isPlayerInVerdantValley() {

        return true;
    }
    public boolean isPlayerInHouseOnTheHill() {
        return Players.getLocal().getWorldLocation().getRegionID() == 14908;
    }
    public boolean isValleyFirstBirdhouseBuilt() {
        return false;
    }
    public boolean verdantValleyBirdhousesBuilt() {
        return true && true;
    }
    public boolean inventoryHasEverythingForMeadow() {
        return true && true && true;
    }
    public NhBirdhousesState getCurrentState() {
        log.info("Checking next state from current state: " + currentState);
        if(currentState == NhBirdhousesState.START_BANK && !inventoryHasEverything()) {
            return NhBirdhousesState.START_BANK;
        }
        if(bankIsOpen() && !inventoryHasEverything()) {
            return NhBirdhousesState.WITHDRAW_ITEMS;
        }
        if(inventoryHasEverything() && !isPlayerInHouseOnTheHill()) {
            return NhBirdhousesState.TELEPORT_DIGSITE;
        }
        if(isPlayerInHouseOnTheHill() && isMushroomTreeNearby()) {
            return NhBirdhousesState.USE_MUSHROOM_TREE_HOUSE;
        }
        //Works until here, birdhouse emptying -> building logic is not implemented yet
        // Create method to empty birdhouse by ID, and build it in empty spot nearby.
        // Upon teleporting to verdant valley, state changes back to TELEPORT_DIGSITE, ending up in a loop.
        if(isPlayerInVerdantValley() && inventoryHasEverything()) {
            return NhBirdhousesState.VALLEY_HOUSE_1;
        }
        if(isPlayerInVerdantValley() && inventoryHasEverything() && isValleyFirstBirdhouseBuilt()) {
            return NhBirdhousesState.VALLEY_HOUSE_2;
        }
        if(verdantValleyBirdhousesBuilt() && inventoryHasEverythingForMeadow()) {
            return NhBirdhousesState.USE_MUSHROOM_TREE_VALLEY;
        }
        return NhBirdhousesState.STOP;
    }
    @Override
    protected void startUp()
    {

    }

    @Override
    public int loop() {
        final Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null)
        {
            return -1;
        }
        currentState = getCurrentState();

        switch(currentState) {
            case START_BANK:
                startBank();
            case WITHDRAW_ITEMS:
                if(bankIsOpen()) {
                    withdrawItems();
                }
                return -1;
            case TELEPORT_DIGSITE:
                if (inventoryHasEverything()) {
                    teleportUsingPendant();
                }
                return -1;
            case USE_MUSHROOM_TREE_HOUSE:
                if (isPlayerInHouseOnTheHill()) {
                    teleportUsingTreeToValley();
                }
                return -1;
            case VALLEY_HOUSE_1:
                log.info("Valley house 1 state reached");

                return -1;
            case VALLEY_HOUSE_2:
                log.info("Valley house 2 state reached");
                return -1;
            case USE_MUSHROOM_TREE_VALLEY:
                log.info("Use mushroom tree in verdant valley state reached");
                return -1;
            case STOP:
                return -1;
        }
        return -1;

    }
    @Subscribe
    public void onGameTick(GameTick event)
    {

    }
    @Override
    protected void shutDown()
    {
        reset();
    }
}