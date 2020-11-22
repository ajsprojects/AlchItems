package net.runelite.client.plugins.alchitems;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@PluginDescriptor(
        name = "Alch Items",
        description = "Shows you good items to alch",
        tags = {"alching", "high alch", "alch"}
)
public class AlchItemsPlugin extends Plugin
{
    @Inject
    private Client client;

    private NavigationButton navButton;

    @Inject
    private ClientToolbar clientToolbar;

    private AlchItemsPanel panel;

    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "icon.png");

        final AlchItemsPanel panel = injector.getInstance(AlchItemsPanel.class);
        panel.init();

        navButton = NavigationButton.builder()
                .tooltip("Alch Items")
                .icon(icon)
                .panel(panel)
                .priority(5)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    @Provides
    AlchItemsPluginConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AlchItemsPluginConfig.class);
    }

}
