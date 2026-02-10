package me.ybbbno.radio.block.part;

import me.ybbbno.customskulls.display.SkullDisplay;
import org.bukkit.entity.ItemDisplay;

public class BlockPart {
    private ItemDisplay display;
    private SkullDisplay skull;
    private int skin_index;

    public BlockPart(ItemDisplay display, SkullDisplay skull, int skin_index) {
        this.display = display;
        this.skull = skull;
        this.skin_index = skin_index < skull.skull().skins().size() ? skin_index : 0;
    }

    public ItemDisplay display() { return display;}
    public void display(ItemDisplay display) { this.display = display; }
    public SkullDisplay skull() { return skull; }
    public void skull(SkullDisplay skull) { this.skull = skull; }
    public int skin_index() { return skin_index; }
    public void skin_index(int skin_index) { this.skin_index = skin_index < skull.skull().skins().size() ? skin_index : 0; }
}
