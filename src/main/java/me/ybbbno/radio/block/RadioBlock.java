package me.ybbbno.radio.data;

import me.ybbbno.radio.data.blockdisplay.BlockPart;
import org.bukkit.entity.TextDisplay;

import java.util.List;

public class RadioBlock {
    private RadioData data;
    private List<BlockPart> parts;
    private TextDisplay indicator;

    public RadioBlock(RadioData data, List<BlockPart> parts, TextDisplay indicator) {
        this.data = data;
        this.parts = parts;
        this.indicator = indicator;
    }

    public RadioData getData() { return data; }
    public void setData(RadioData data) { this.data = data; }
    public List<BlockPart> getParts() { return parts; }
    public void setParts(List<BlockPart> parts) { this.parts = parts; }
    public TextDisplay getIndicator() { return indicator; }
    public void setIndicator(TextDisplay indicator) { this.indicator = indicator; }
}
