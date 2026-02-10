package me.ybbbno.radio.block.blockpart;

import me.ybbbno.radio.skullhandle.Skull;
import org.bukkit.entity.ItemDisplay;

public class BlockPart {
    private ItemDisplay display;
    private Skull skull;

    public BlockPart(ItemDisplay display, Skull skull) {
        this.display = display;
        this.skull = skull;
    }

    public ItemDisplay getDisplay() { return display;}
    public void setDisplay(ItemDisplay display) {this.display = display;}
    public Skull getSkull() { return skull; }
    public void setSkull(Skull skull) { this.skull = skull; }
}
