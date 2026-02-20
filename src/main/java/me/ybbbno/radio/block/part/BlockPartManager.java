package me.ybbbno.radio.block.part;

import me.deadybbb.ybmj.BasicManagerHandler;
import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.customskulls.CustomSkullAPI;
import me.ybbbno.customskulls.display.SkullDisplay;
import me.ybbbno.customskulls.display.SkullDisplayManager;
import me.ybbbno.radio.block.RadioBlock;
import me.ybbbno.radio.block.data.RadioData;
import me.ybbbno.radio.block.data.RadioState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BlockPartManager extends BasicManagerHandler {
    private final SkullDisplayManager managerD;

    public BlockPartManager(PluginProvider plugin) {
        super(plugin);
        this.managerD = CustomSkullAPI.api().manager();
    }

    @Override
    protected void onInit() { }

    @Override
    protected void onDeinit() { }

    public List<BlockPart> getBlockParts(RadioData data) {
        Location loc = data.location().clone().add(0.5, 0.5, 0.5);

        if (!loc.isChunkLoaded()) {
            return List.of();
        }

        return getBlockParts(data.location().clone().add(0.5, 0.5, 0.5), data.state());
    }

    public List<BlockPart> getBlockParts(Location loc, RadioState state) {
        List<BlockPart> parts = new ArrayList<>();

        if (loc.getWorld() == null || !loc.isChunkLoaded()) return parts;

        for (SkullDisplay skull : managerD.getDisplays().stream().filter(d -> d.name().contains("radio-block-part")).toList()){
            int index = RadioState.valueOf(state);
            ItemDisplay display = managerD.getItemDisplayBySkull(skull, loc, index);

            if (display != null && display.isValid()) {
                parts.add(new BlockPart(display, skull, index));
            }
        }
        return parts;
    }

    public void updateParts(RadioBlock block) {
        if (!block.data().location().isChunkLoaded()) return;
        changeBlockParts(block);
        changeFrequencyIndicator(block);
    }

    public void changeBlockParts(RadioBlock block) {
        changeBlockParts(block.parts(), block.data().state());
    }

    public void changeBlockParts(List<BlockPart> parts, RadioState state) {
        if (parts == null) return;

        for (BlockPart part : parts) {
            if (part.display() != null && part.display().isValid()) {
                part.skin_index(RadioState.valueOf(state));
                changeBlockPart(part);
            }
        }
    }

    public void changeBlockPart(BlockPart part) {
        SkullDisplay skull = part.skull();

        part.display().setItemStack(managerD.getItemBySkull(skull, part.skin_index()));
    }

    public TextDisplay getFrequencyIndicator(RadioData data) {
        Location loc = data.location().clone().add(0.5, 0.5, 0.5);

        if (!loc.isChunkLoaded()) {
            return null;
        }

        return getFrequencyIndicator(loc, data.frequency());
    }

    public TextDisplay getFrequencyIndicator(Location loc, int frequency) {
        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class);
        display.text(Component.text(String.valueOf(frequency), NamedTextColor.DARK_RED));
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setViewRange(512.0F);
        Vector3f scale = new Vector3f(1.5F, 1.435F, 0.0F);
        Vector3f translation = new Vector3f(-0.501F, -0.01F, -0.0185F);
        Quaternionf leftRot = (new Quaternionf()).rotateY((float)Math.toRadians((double)270.0F));
        SkullDisplayManager.updateTransformation(display, translation, leftRot, scale, null);
        return display;
    }

    public void changeFrequencyIndicator(RadioBlock block) {
        TextDisplay indicator = block.indicator();
        if (indicator != null && indicator.isValid()) {
            changeFrequencyIndicator(indicator, block.data().frequency());
        }
    }

    public void changeFrequencyIndicator(TextDisplay display, int frequency) {
        display.text(Component.text(String.valueOf(frequency), NamedTextColor.DARK_RED));
    }
}
