package alchitems;

import lombok.Getter;

@Getter
public class NatureRune {

    private int gePrice;
    private String itemName;
    private int geLimit;

    public NatureRune(String itemName, int gePrice, int geLimit) {
        this.gePrice = gePrice;
        this.itemName = itemName;
        this.geLimit = geLimit;
    }
}
