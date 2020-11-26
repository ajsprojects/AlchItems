package net.runelite.client.plugins.alchitems;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemService {

    @Inject
    Utilities utilities;
    private WebClient webClient;

    public ItemService() {
        this.webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setGeolocationEnabled(false);
    }

    public List<AlchItem> scrapeDataFromTable(String url, int natureRunePrice) throws Exception {
        List<AlchItem> results = new ArrayList<>();
        try {
            HtmlPage page = webClient.getPage(url);
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
                //log.debug("Item fetched: " + alchItem.getItemName());
            }
        } catch (Exception exception) {
            throw(exception);
        }
        return results;
    }

    private int getVolumeOfItem(String itemName) throws Exception {
        try {
            HtmlPage page = webClient.getPage("https://oldschool.runescape.wiki/w/" + itemName);
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
        HtmlPage page = webClient.getPage(url);

        DomElement itemNameElement = page.getElementById("firstHeading");
        String itemName = itemNameElement.getVisibleText();

        DomElement gePriceElement = page.getFirstByXPath("//span[@class='infobox-quantity-replace']");
        String gePriceString = gePriceElement.getVisibleText();
        int gePrice = utilities.formatStringToInt(gePriceString);

        DomElement buyLimitElement = page.getFirstByXPath("//*[@id=\"mw-content-text\"]/div/table[3]/tbody/tr[27]/td");
        String buyLimitString = buyLimitElement.getVisibleText();
        int buyLimit = utilities.formatStringToInt(buyLimitString);

        log.debug("Current nature rune price: " + gePrice + " buy limit: " + buyLimit);
        return new NatureRune(itemName, gePrice, buyLimit);
    }

    public List<AlchItem> getItemList() throws Exception {
        NatureRune natureRune = getNatureRune("https://oldschool.runescape.wiki/w/Nature_rune");
        return scrapeDataFromTable("https://oldschool.runescape.wiki/w/RuneScape:Grand_Exchange_Market_Watch/Alchemy", natureRune.getGePrice());
    }

    public List<AlchItem> getMockItemListEmpty() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<AlchItem> getMockItemList() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<AlchItem> arrayList = new ArrayList<>();
        AlchItem alchItem = new AlchItem("Rune Dagger", 5000,5200, 400, false, 470, 193);
        AlchItem alchItem2 = new AlchItem("Rune Crossbow", 50000,52000, 2000, true, 3000, 193);
        AlchItem alchItem3 = new AlchItem("Rune Scimitar", 28000,3000, 1000, false, 44000, 193);
        AlchItem alchItem4 = new AlchItem("Rune Bolts", 200,405, 40000, true, 157003, 193);
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
