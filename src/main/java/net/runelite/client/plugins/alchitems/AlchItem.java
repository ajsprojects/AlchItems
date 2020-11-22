package net.runelite.client.plugins.alchitems;

import lombok.Getter;

@Getter
public class AlchItem {

    private String itemName;
    private int gePrice;
    private int highAlchPrice;
    private int profit;
    private int buyLimit;
    private boolean members;
    private int dailyVolume;

    public AlchItem(String itemName, int gePrice, int highAlchPrice, int buyLimit, boolean members, int dailyVolume, int natureRunePrice) {
        this.itemName = itemName;
        this.gePrice = gePrice;
        this.highAlchPrice = highAlchPrice;
        this.profit = highAlchPrice - (gePrice + natureRunePrice);
        this.buyLimit = buyLimit;
        this.members = members;
        this.dailyVolume = dailyVolume;
    }
}
