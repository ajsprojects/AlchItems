package net.runelite.client.plugins.alchitems;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    Utilities utilities;

    @Inject
    AlchItemsPlugin plugin;

    public void init() {
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
        plugin.fetchItemsHandlerASync();
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
        SpinnerModel sm = new SpinnerNumberModel(limit, 0, 500, 1); //default value,lower bound,upper bound,increment by
        JSpinner spinner = new JSpinner(sm);
        spinner.setValue(limit);
        spinner.setSize(80,20);
        JLabel tabLabel = new JLabel("Limit results to ");
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(tabLabel, BorderLayout.NORTH);
        panel.add(spinner,BorderLayout.NORTH);

        spinner.addChangeListener(event ->
        {
            limit = (int) spinner.getValue();
            refreshItemsPanelDisplay();
        });
        return panel;
    }

    public JPanel buildComboBox() {
        JPanel comboPanel = new JPanel();
        String [] sortOptions = { "Profit", "Daily Volume", "Buy Limit", "F2P Items"};
        JComboBox<String> tabSelectionCombo  = new JComboBox<>(sortOptions);
        JLabel tabLabel = new JLabel("Sort by ");
        comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.LINE_AXIS));
        comboPanel.add(tabLabel, BorderLayout.NORTH);
        comboPanel.add(tabSelectionCombo,BorderLayout.NORTH);

        tabSelectionCombo.addItemListener(event ->
        {
            sortBy = (String) event.getItem();
            refreshItemsPanelDisplay();
        });
        return comboPanel;
    }

    public JButton buildRefreshButton() {

        JButton refreshButton = new JButton("Refresh Prices");
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(event -> plugin.fetchItemsHandlerASync());
        return refreshButton;
    }

    public void refreshItemsPanelDisplay() {
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
        JPanel itemListPanel = buildBasePanel();
        if(alchItemsList == null || alchItemsList.isEmpty()) {
            itemListPanel.add(buildErrorItem());
            return itemListPanel;
        }
        log.debug("List size: " + alchItemsList.size() + " | Display limit: " + limit);
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
        return buildCustomPanel("Error fetching items!", "Try refreshing items or report the issue", Color.RED);
    }

    public JPanel buildFetchingItems() {
        JPanel itemsList = buildBasePanel();
        itemsList.add(buildCustomPanel("Please wait", "Scraping and fetching items", Color.WHITE));
        return itemsList;
    }

    private JPanel buildCustomPanel(String titleText, String messageText, Color color) {
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
        final Color hoverColor = ColorScheme.MEDIUM_GRAY_COLOR;

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
        Color buyLimitColour = getColour(alchItem.getBuyLimit());
        buyLimit.setForeground(buyLimitColour);
        buyLimit.setFont(FontManager.getRunescapeSmallFont());

        String volume = "N/A";
        if(alchItem.getDailyVolume() != -1) {
            volume = utilities.formatNumber(alchItem.getDailyVolume());
        }
        JLabel dailyVolume = new JLabel("Daily Volume: " + volume);
        Color dailyVolumeColour = getColour(alchItem.getDailyVolume());
        dailyVolume.setForeground(dailyVolumeColour);
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

    private Color getColour(int number) {
        Color customColour = Color.GREEN;
        if(number < 800) {
            customColour = Color.RED;
        }
        if(number < 5000 && number >= 800) {
            customColour = Color.YELLOW;
        }
        if(number == -1) {
            customColour = Color.WHITE;
        }
        return customColour;
    }

    public JPanel getItemsContainerPanel() {
        return itemsContainerPanel;
    }
    public JPanel getItemsPanel() {
        return itemsPanel;
    }
    public void setItemsPanel(JPanel itemsPanel) {
        this.itemsPanel = itemsPanel;
    }
    public void setAlchItemsList(List<AlchItem> alchItemsList) {
        this.alchItemsList = alchItemsList;
    }
}
