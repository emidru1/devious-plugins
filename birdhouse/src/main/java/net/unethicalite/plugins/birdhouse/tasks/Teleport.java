package net.unethicalite.plugins.birdhouse.tasks;

import net.unethicalite.plugins.birdhouse.BirdHouse;
import net.unethicalite.plugins.birdhouse.Config;
import net.unethicalite.plugins.utils.enums.TeleportLocation;
import net.unethicalite.plugins.utils.tasks.Task;
import javax.inject.Inject;
import net.unethicalite.api.items.Inventory;

public class Teleport extends Task {

  @Inject
  private BirdHouse plugin;
  @Inject
  private Config config;

  @Override
  public String getStatus() {
    return "Teleporting";
  }

  @Override
  public boolean validate() {
    return config.tpLocation() != TeleportLocation.NOWHERE
        && !config.farmSeaweed()
        && plugin.getEmptied().size() == 4;
  }

  @Override
  public void execute() {
    config.tpLocation().teleport(config.goThroughHouse(), config.goThroughHouseFallback());

    Inventory.getAll((i) -> i.hasAction("Search")).forEach((i) -> i.interact("Search"));

    plugin.stop();
  }
}
