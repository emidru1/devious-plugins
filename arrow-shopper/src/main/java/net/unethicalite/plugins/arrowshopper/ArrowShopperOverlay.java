package net.unethicalite.plugins.arrowshopper;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class ArrowShopperOverlay extends OverlayPanel
{

    private final ArrowShopperPlugin plugin;
    private final ArrowShopperConfig config;
    private final Client client;

    @Inject
    private ArrowShopperOverlay(final Client client, final ArrowShopperPlugin plugin, final ArrowShopperConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Arrow-Shopper"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {

        if (plugin.botTimer == null)
        {
            return null;
        }
        panelComponent.getChildren().clear();

        /* Title and width */
        String title = "Nh Arrow Shopper";
        panelComponent.getChildren().add(TitleComponent.builder().text(title).color(Color.YELLOW).build());
        panelComponent.setBackgroundColor(Color.DARK_GRAY);
        panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(title) + 80, 0));
        panelComponent.getChildren().add(LineComponent.builder().left("").build());

        /* Runtime */
        Duration duration = Duration.between(plugin.botTimer, Instant.now());
        panelComponent.getChildren().add(LineComponent.builder().left("Runtime: ").right((duration.toHours() > 0 ? (duration.toHours() + ":") : ("")) + (new SimpleDateFormat("mm:ss").format(new Date(duration.toMillis())))).build());


        /* Render */
        return panelComponent.render(graphics);
    }

}