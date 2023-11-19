package net.unethicalite.plugins.birdhouse;

import com.google.inject.Provides;
import net.unethicalite.plugins.birdhouse.tasks.AddSeeds;
import net.unethicalite.plugins.birdhouse.tasks.BuildBirdHouse;
import net.unethicalite.plugins.birdhouse.tasks.CraftBirdhouse;
import net.unethicalite.plugins.birdhouse.tasks.Deposit;
import net.unethicalite.plugins.birdhouse.tasks.EmptyBirdHouse;
import net.unethicalite.plugins.birdhouse.tasks.GetTools;
import net.unethicalite.plugins.birdhouse.tasks.GoToBirdHouse;
import net.unethicalite.plugins.birdhouse.tasks.GoToIsland;
import net.unethicalite.plugins.birdhouse.tasks.GoToMushroomMeadow;
import net.unethicalite.plugins.birdhouse.tasks.GoToVerdantValley;
import net.unethicalite.plugins.birdhouse.tasks.HandleBank;
import net.unethicalite.plugins.birdhouse.tasks.HarvestSeaweed;
import net.unethicalite.plugins.birdhouse.tasks.NoteSeaweed;
import net.unethicalite.plugins.birdhouse.tasks.PickupSpore;
import net.unethicalite.plugins.birdhouse.tasks.PlantSeaweed;
import net.unethicalite.plugins.birdhouse.tasks.StartRun;
import net.unethicalite.plugins.birdhouse.tasks.Teleport;
import net.unethicalite.plugins.utils.Constants;
import net.unethicalite.plugins.utils.TickScript;
import net.unethicalite.plugins.utils.Utils;
import net.unethicalite.plugins.utils.tasks.Run;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import org.pf4j.Extension;
import org.slf4j.Logger;

@PluginDescriptor(
    name = "Chaos Bird House",
    description = "Mass bird slaughter",
    enabledByDefault = false)
@PluginDependency(Utils.class)
@Slf4j
@Extension
public class BirdHouse extends TickScript implements KeyListener {
  public static final WorldArea HILL_HOUSE = new WorldArea(3755, 3861, 20, 16, 1);
  public static final WorldPoint ISLAND = new WorldPoint(3769, 3898, 0);
  public static final int TOOL_WIDGET_ID = 125;
  public static final Supplier<Widget> TOOLS = () -> Widgets.get(TOOL_WIDGET_ID, 0);
  public static final Supplier<Widget> CLOSE = () -> Widgets.get(TOOL_WIDGET_ID, 1, 11);
  private static final int UNDERWATER_REGION = 15008;
  @Getter private final List<Integer> emptied = new ArrayList<>();
  @Inject private Config config;
  @Getter @Setter private boolean manuallyStarted;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    super.onStart();

    emptied.clear();

    tasks.add(new Run());
      addTask(StartRun.class);
      tasks.add(new AddSeeds());
      tasks.add(new HandleBank(config));
      tasks.add(new BuildBirdHouse());
      tasks.add(new CraftBirdhouse());
      tasks.add(new EmptyBirdHouse(this));
      tasks.add(new GetTools(this));
      tasks.add(new GoToBirdHouse(this));
      tasks.add(new GoToMushroomMeadow());
      tasks.add(new GoToVerdantValley());
      tasks.add(new GoToIsland(this, config));
      tasks.add(new PickupSpore(this, config));
      tasks.add(new PlantSeaweed(this));
      tasks.add(new NoteSeaweed(this));
      tasks.add(new HarvestSeaweed(this, config));
      tasks.add(new Deposit(this));
  }

  @Subscribe
  private void onConfigButtonPressed(ConfigButtonClicked event) {
    String name = this.getName().replaceAll(" ", "").toLowerCase(Locale.ROOT);

    if (event.getGroup().equals(name) && event.getKey().equals("startButton")) {
      manuallyStarted = true;
    }
  }

  @Subscribe
  private void onGameTick(GameTick event) {
    if (!Utils.isLoggedIn()) {
      return;
    }

    if (!isRunning()
        && HILL_HOUSE.contains(Players.getLocal())
        && Inventory.getCount((i) -> Constants.LOG_IDS.contains(i.getId())) == 4) {
      start();
    } else if (isRunning() && !manuallyStarted && Inventory.isEmpty() && !Bank.isOpen()) {
      stop();
    }
  }

  public void emptied(int spaceId) {
    emptied.add(spaceId);
  }

  public boolean isEmptied(int spaceId) {
    return emptied.contains(spaceId);
  }

  public boolean isUnderwater() {
    return Players.getLocal().getWorldLocation().getRegionID() == UNDERWATER_REGION;
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (config.birdhouseHotkey().matches(e)) {
      e.consume();
      manuallyStarted = true;
      start();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}
}
