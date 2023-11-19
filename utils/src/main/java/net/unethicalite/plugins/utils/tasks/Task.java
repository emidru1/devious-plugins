package net.unethicalite.plugins.utils.tasks;

import net.unethicalite.plugins.utils.api.Activity;

public abstract class Task {

  public Activity getActivity() {
    return Activity.IDLE;
  }

  public abstract String getStatus();

  public abstract boolean validate();

  public abstract void execute();
}
