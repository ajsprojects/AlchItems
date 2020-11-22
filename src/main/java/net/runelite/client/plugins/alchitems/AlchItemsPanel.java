package net.runelite.client.plugins.alchitems;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AlchItemsPanel extends PluginPanel {

    private String sortBy = "profit";
    private JPanel settingsPanel;
    private JPanel itemsContainerPanel;
    private JPanel itemsPanel;
    private JPanel spinner;
    private JButton refreshButton;
    private JPanel comboBox;
    private int limit = 50;

    private List<AlchItem> alchItemsList;

    @Inject
    ItemService itemService;

    @Inject
    Utilities utilities;

    public void init() throws Exception {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(8, 8, 8, 8)); //DO NOT CHANGE

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BorderLayout(5,5));

        itemsContainerPanel = new JPanel();
        itemsContainerPanel.setLayout(new BorderLayout(5,5));

        refreshButton = buildRefreshButton();
        comboBox = buildComboBox();
        spinner = buildSpinner();
        itemsPanel = new JPanel();
        fetchItemsHandlerASync();
        buildPanels();
    }

    public void buildPanels() {
        getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        settingsPanel.add(refreshButton, BorderLayout.NORTH);
        settingsPanel.add(comboBox, BorderLayout.CENTER);
        settingsPanel.add(spinner, BorderLayout.SOUTH);
        itemsContainerPanel.add(itemsPanel, BorderLayout.NORTH);
        add(settingsPanel, BorderLayout.NORTH);
        add(itemsContainerPanel, BorderLayout.SOUTH);
    }

    public JPanel buildSpinner() {
        JPanel panel = new JPanel();
        JSpinner spinner = new JSpinner();
        spinner.setValue(limit);
        spinner.setSize(80,20);
        JLabel tabLabel = new JLabel("Limit results to ");
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(tabLabel, BorderLayout.NORTH);
        panel.add(spinner,BorderLayout.NORTH);

        spinner.addChangeListener((event) ->
        {
            int value = (int) spinner.getValue();
            limit = value;
            refreshItemsPanelDisplay();
        });
        return panel;
    }

    public JPanel buildComboBox() {
        JPanel comboPanel = new JPanel();
        String [] sortOptions = { "Profit", "Daily Volume", "Buy Limit", "F2P Items"};
        JComboBox<String> tabSelectionCombo  = new JComboBox<>(sortOptions);
        JLabel tabLabel = new JLabel("Sort By ");
        comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.LINE_AXIS));
        comboPanel.add(tabLabel, BorderLayout.NORTH);
        comboPanel.add(tabSelectionCombo,BorderLayout.NORTH);

        tabSelectionCombo.addItemListener((event) ->
        {
            sortBy = (String) event.getItem();
            refreshItemsPanelDisplay();
        });
        return comboPanel;
    }

    public JButton buildRefreshButton() {

        JButton refreshButton = new JButton("Refresh Prices");
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener((event) ->
        {
            fetchItemsHandlerASync();
        });
        return refreshButton;
    }

    private void fetchItemsHandlerASync() {
        itemsContainerPanel.remove(itemsPanel);
        itemsPanel = buildFetchingItems();
        itemsContainerPanel.add(itemsPanel);
        updateUI();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                getItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    private void getItems() throws Exception {
        System.out.println("Fetching items...");
        alchItemsList = itemService.getItemList();
        System.out.println("Finished fetching items");
        SwingUtilities.invokeLater(() -> {
            System.out.println("Refreshing displayed items");
            refreshItemsPanelDisplay();
        });
    }

    private void refreshItemsPanelDisplay() {
        itemsContainerPanel.remove(itemsPanel);
        itemsPanel = buildItems(alchItemsList, sortBy, limit);
        itemsContainerPanel.add(itemsPanel);
        updateUI();
    }

    private JPanel buildBasePanel() {
        JPanel basePanel = new JPanel();
        basePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        basePanel.setLayout(new GridLayout(0, 1, 0, 6));
        return basePanel;
    }

    private JPanel buildItems(List<AlchItem> alchItemsList, String sortBy, int limit) {
        System.out.println("List size: " + alchItemsList.size() + " | Display limit: " + limit);
        JPanel itemListPanel = buildBasePanel();
        if(alchItemsList == null || alchItemsList.isEmpty()) {
            itemListPanel.add(buildErrorItem());
            return itemListPanel;
        }

        if(sortBy.equalsIgnoreCase("profit") || sortBy.equalsIgnoreCase("f2p items")) {
            alchItemsList = utilities.sortItemsByProfit(alchItemsList);
        }
        if(sortBy.equalsIgnoreCase("daily volume")) {
            alchItemsList = utilities.sortItemsByVolume(alchItemsList);
        }
        if(sortBy.equalsIgnoreCase("buy limit")) {
            alchItemsList = utilities.sortItemsByBuyLimit(alchItemsList);
        }

        for(AlchItem item : alchItemsList) {
            if (itemListPanel.getComponentCount() >= limit) {
                break;
            }
            if (sortBy.equalsIgnoreCase("f2p items")) {
                if (!item.isMembers()) {
                    itemListPanel.add(buildItem(item));
                }
            } else {
                itemListPanel.add(buildItem(item));
            }
        }
        return itemListPanel;
    }

    private JPanel buildErrorItem() {
        return buildSpecialItem("Error fetching items!", "Try refreshing items or report the issue", Color.RED);
    }

    private JPanel buildFetchingItems() {
        JPanel itemsList = buildBasePanel();
        itemsList.add(buildSpecialItem("Please wait", "Scraping and fetching items", Color.WHITE));
        return itemsList;
    }

    private JPanel buildSpecialItem(String titleText, String messageText, Color color) {
        JPanel container = new JPanel();
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel textContainer = new JPanel();
        textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textContainer.setLayout(new GridLayout(2, 1));
        textContainer.setBorder(new EmptyBorder(2, 2, 2, 2));

        JLabel title = new JLabel(titleText);
        title.setForeground(color);
        title.setFont(FontManager.getRunescapeBoldFont());
        JLabel message = new JLabel(messageText);
        message.setForeground(color);
        message.setFont(FontManager.getRunescapeSmallFont());
        textContainer.add(title);
        textContainer.add(message);
        container.add(textContainer);
        return container;
    }

    private JPanel buildItem(AlchItem alchItem) {
        final Color hoverColor = ColorScheme.LIGHT_GRAY_COLOR;

        JPanel container = new JPanel();
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel textContainer = new JPanel();
        textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textContainer.setLayout(new GridLayout(3, 1));
        textContainer.setBorder(new EmptyBorder(2, 2, 2, 2));

        container.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                container.setToolTipText(alchItem.getItemName());
                container.setBackground(hoverColor);
                textContainer.setBackground(hoverColor);
                container.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                container.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JLabel itemName = new JLabel(alchItem.getItemName());
        if(alchItem.isMembers()) {
            itemName.setForeground(Color.ORANGE);
        } else {
            itemName.setForeground(new Color(168, 164, 163));
        }
        itemName.setFont(FontManager.getRunescapeSmallFont());

        String price = utilities.formatNumber(alchItem.getGePrice());
        JLabel itemPrice = new JLabel("Item Price: " + price);
        itemPrice.setForeground(Color.WHITE);
        itemPrice.setFont(FontManager.getRunescapeSmallFont());

        JLabel profit = new JLabel("Profit: " + alchItem.getProfit());
        profit.setForeground(Color.GREEN);
        profit.setFont(FontManager.getRunescapeSmallFont());

        String limit = utilities.formatNumber(alchItem.getBuyLimit());
        JLabel buyLimit = new JLabel("Buy Limit: " + limit);
        buyLimit.setForeground(Color.WHITE);
        buyLimit.setFont(FontManager.getRunescapeSmallFont());

        String volume = "N/A";
        if(alchItem.getDailyVolume() != -1) {
            volume = utilities.formatNumber(alchItem.getDailyVolume());
        }
        JLabel dailyVolume = new JLabel("Daily Volume: " + volume);
        Color customColour = getColour(alchItem);
        dailyVolume.setForeground(customColour);
        dailyVolume.setFont(FontManager.getRunescapeSmallFont());

        textContainer.add(itemName);
        textContainer.add(new JLabel(""));
        textContainer.add(itemPrice);
        textContainer.add(profit);
        textContainer.add(buyLimit);
        textContainer.add(dailyVolume);
        container.add(textContainer);
        return container;
    }

    private static Color getColour(AlchItem alchItem) {
        Color customColour = Color.GREEN;
        int volume = alchItem.getDailyVolume();
        if(volume < 1000) {
            customColour = Color.RED;
        }
        if(volume < 5000 && volume >= 1000) {
            customColour = Color.YELLOW;
        }
        if(volume == -1) {
            customColour = Color.WHITE;
        }
        return customColour;
    }

}
