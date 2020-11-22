package net.runelite.client.plugins.alchitems;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemService {

    @Inject
    Utilities utilities;

    public List<AlchItem> getItemList() throws Exception {
        NatureRune natureRune = getNatureRune("https://oldschool.runescape.wiki/w/Nature_rune");
        return scrapeDataFromTable("https://oldschool.runescape.wiki/w/RuneScape:Grand_Exchange_Market_Watch/Alchemy", natureRune.getGePrice());
    }

    public List<AlchItem> scrapeDataFromTable(String url, int natureRunePrice) throws Exception {
        List<AlchItem> results = new ArrayList<>();
        try {
            HtmlPage page = getWebPage(url);
            List<Object> object = page.getByXPath("//*[@id=\"mw-content-text\"]/div/table[1]");
            HtmlTable table = (HtmlTable) object.get(0);

            for (int i = 1; i < table.getRowCount(); i++) { //Ignore first row - headings
                HtmlTableRow row = table.getRow(i);

                String itemName = row.getCell(1).getVisibleText();
                int gePrice = utilities.formatStringToInt(row.getCell(2).getVisibleText());
                int highAlchPrice = utilities.formatStringToInt(row.getCell(3).getVisibleText());
                int buyLimit = utilities.formatStringToInt(row.getCell(6).getVisibleText());
                int dailyVolume = getVolumeOfItem(itemName);
                String memberItem = row.getCell(8).getFirstChild().getAttributes().item(1).getNodeValue();
                boolean members = memberItem.equalsIgnoreCase("Free-to-play") ? false : true;
                AlchItem alchItem = new AlchItem(itemName, gePrice, highAlchPrice, buyLimit, members, dailyVolume, natureRunePrice);
                results.add(alchItem);
                //System.out.println("Item fetched: " + alchItem.getItemName());
            }
        } catch (Exception e) {

        }
        return results;
    }

    private HtmlPage getWebPage(String url) throws IOException {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        return client.getPage(url);
    }

    private int getVolumeOfItem(String itemName) throws Exception {
        try {
            HtmlPage page = getWebPage("https://oldschool.runescape.wiki/w/" + itemName);
            DomElement volumeElement = page.getFirstByXPath("//*[@id=\"mw-content-text\"]/div/table[1]/tbody/tr[29]/td");
            if(volumeElement.getFirstChild() == null) {
                volumeElement = page.getFirstByXPath("//*[@id=\"mw-content-text\"]/div/table[1]/tbody/tr[28]/td");
            }
            String volumeTradedString = volumeElement.getVisibleText();
            return utilities.formatStringToInt(volumeTradedString);
        } catch (Exception e) {
            return -1;
        }
    }

    private NatureRune getNatureRune(String url) throws Exception {
        HtmlPage page = getWebPage(url);

        DomElement itemNameElement = page.getElementById("firstHeading");
        String itemName = itemNameElement.getVisibleText();

        DomElement gePriceElement = page.getFirstByXPath("//span[@class='infobox-quantity-replace']");
        String gePriceString = gePriceElement.getVisibleText();
        int gePrice = utilities.formatStringToInt(gePriceString);

        DomElement buyLimitElement = page.getFirstByXPath("//*[@id=\"mw-content-text\"]/div/table[3]/tbody/tr[27]/td");
        String buyLimitString = buyLimitElement.getVisibleText();
        int buyLimit = utilities.formatStringToInt(buyLimitString);

        System.out.println("Current nature rune price: " + gePrice + " buy limit: " + buyLimit);
        NatureRune natureRune = new NatureRune(itemName, gePrice, buyLimit);
        return natureRune;
    }

    public List<AlchItem> getMockItemList() {
        List<AlchItem> arrayList = new ArrayList<>();
        AlchItem alchItem = new AlchItem("Rune Dagger", 5000,5200, 400, false, 470, 193);
        AlchItem alchItem2 = new AlchItem("Rune Crossbow", 50000,52000, 2000, true, 3000, 193);
        AlchItem alchItem3 = new AlchItem("Rune Scimitar", 28000,3000, 1000, false, 44000, 193);
        AlchItem alchItem4 = new AlchItem("Rune Bolts", 200,405, 4000, true, 157003, 193);
        arrayList.add(alchItem);
        arrayList.add(alchItem2);
        arrayList.add(alchItem3);
        arrayList.add(alchItem4);
        arrayList.add(alchItem);
        arrayList.add(alchItem2);
        arrayList.add(alchItem3);
        arrayList.add(alchItem4);
        arrayList.add(alchItem);
        arrayList.add(alchItem2);
        arrayList.add(alchItem3);
        arrayList.add(alchItem4);
        arrayList.add(alchItem);
        arrayList.add(alchItem2);
        arrayList.add(alchItem3);
        arrayList.add(alchItem4);
        return arrayList;
    }
}
