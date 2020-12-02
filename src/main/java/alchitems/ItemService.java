package alchitems;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemService {

    @Inject
    Utilities utilities;

    public List<AlchItem> getItemList() {
        List<AlchItem> results = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://oldschool.runescape.wiki/w/RuneScape:Grand_Exchange_Market_Watch/Alchemy")
                    .userAgent("AlchItemsPlugin https://github.com/ajsprojects/AlchItemsPlugin")
                    .get();

            Element table = doc.select(".wikitable").get(0);

            for (Element row : table.select("tr")) {
                Elements column = row.getElementsByTag("td");

                if(!column.isEmpty() && !column.text().isEmpty()) {
                    String itemName = column.get(1).text();
                    int gePrice = utilities.formatStringToInt(column.get(2).text());
                    int highAlchPrice = utilities.formatStringToInt(column.get(3).text());
                    int profit = utilities.formatStringToInt(column.get(4).text());
                    int buyLimit = utilities.formatStringToInt(column.get(6).text());
                    //int dailyVolume = getVolumeOfItem(itemName);
                    boolean members = column.get(8).attributes().dataset().values().contains("1");
                    AlchItem alchItem = new AlchItem(itemName, gePrice, highAlchPrice, buyLimit, members, -1, profit);
                    results.add(alchItem);
                    //log.info("Item fetched: " + alchItem.getItemName());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
    //TODO figure out best way to get volume
   /* private int getVolumeOfItem(String itemName) throws Exception {
        try {
            Document doc = Jsoup.connect("https://oldschool.runescape.wiki/w/" + itemName).get();
            Element table = doc.select(".infobox").get(0);
            for (Element row : table.select("tr")) {
                System.out.println(row);
            }
            //String volumeTradedString = volumeElement.getVisibleText();
            return utilities.formatStringToInt("2");
        } catch (Exception e) {
            return -1;
        }
    }*/

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

    public List<AlchItem> getMockItemListEmpty() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<AlchItem>();
    }
}
