package me.ybbbno.radio.block;

import me.ybbbno.radio.block.part.BlockPart;
import me.ybbbno.radio.block.data.RadioData;
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

    public RadioData data() { return data; }
    public void data(RadioData data) { this.data = data; }
    public List<BlockPart> parts() { return parts; }
    public void parts(List<BlockPart> parts) { this.parts = parts; }
    public TextDisplay indicator() { return indicator; }
    public void indicator(TextDisplay indicator) { this.indicator = indicator; }
}
