package net.unethicalite.plugins.birdhouse.tasks;

import net.unethicalite.plugins.birdhouse.BirdHouseSpace;
import net.unethicalite.plugins.birdhouse.BirdHouseState;
import net.unethicalite.plugins.utils.Constants;
import net.unethicalite.plugins.utils.tasks.Task;
import net.runelite.api.Item;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Inventory;

public class AddSeeds extends Task {

  private TileObject emptyBirdhouse;

  @Override
  public String getStatus() {
    return "Adding seeds";
  }

  @Override
  public boolean validate() {
    emptyBirdhouse = BirdHouseSpace.getNearest(BirdHouseState.BUILT);

    return emptyBirdhouse != null;
  }

  @Override
  public void execute() {
    final Item seeds = Inventory.getFirst((i) -> Constants.BIRD_HOUSE_SEED_IDS.contains(i.getId()));
    if (seeds == null) {
      return;
    }

    final int quantity = seeds.getQuantity();
    GameThread.invoke(() -> seeds.useOn(emptyBirdhouse));

    Time.sleepTicksUntil(
        () ->
            Inventory.getCount(true, (i) -> Constants.BIRD_HOUSE_SEED_IDS.contains(i.getId()))
                < quantity,
        5);
  }
}
