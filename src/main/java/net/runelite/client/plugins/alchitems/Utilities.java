package net.runelite.client.plugins.alchitems;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utilities {

    public int formatStringToInt(String string) throws Exception {
        string = string.replaceAll(",", "");
        string = string.replaceAll(" coins", "");
        return Integer.parseInt(string);
    }

    public List<AlchItem> sortItemsByProfit(List<AlchItem> alchItems) {
        Collections.sort(alchItems, Comparator.comparingInt(AlchItem::getProfit).reversed());
        return alchItems;
    }

    public List<AlchItem> sortItemsByVolume(List<AlchItem> alchItems) {
        Collections.sort(alchItems, Comparator.comparingInt(AlchItem::getDailyVolume).reversed());
        return alchItems;
    }

    public List<AlchItem> sortItemsByBuyLimit(List<AlchItem> alchItems) {
        Collections.sort(alchItems, Comparator.comparingInt(AlchItem::getBuyLimit).reversed());
        return alchItems;
    }

    private static String[] suffix = new String[]{"","k", "m", "b", "t"};
    private static int MAX_LENGTH = 4;
    public String formatNumber(double number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
        }
        return r;
    }
}
