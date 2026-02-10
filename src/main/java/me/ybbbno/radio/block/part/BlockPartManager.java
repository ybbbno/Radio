package me.ybbbno.radio.block.blockpart;

import me.deadybbb.ybmj.BasicManagerHandler;
import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.radio.block.data.RadioData;
import me.ybbbno.radio.skullhandle.Skull;
import me.ybbbno.radio.skullhandle.SkullHandler;
import me.ybbbno.radio.skullhandle.SkullType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BlockPartManager extends BasicManagerHandler {
    private final BlockPartsConfigManager config;
    private final List<Skull> skulls;

    public BlockPartManager(PluginProvider plugin) {
        super(plugin);
        this.config = new BlockPartsConfigManager(plugin);
        skulls = new ArrayList<>();
    }

    @Override
    protected void onInit() {
        skulls.clear();
        skulls.addAll(config.getItemDisplays());
    }

    @Override
    protected void onDeinit() {}

    public List<BlockPart> getBlockParts(RadioData data) {
        return getBlockParts(data.getLocation());
    }

    public List<BlockPart> getBlockParts(Location loc) {
        if (!is_init) return List.of();
        List<BlockPart> parts = new ArrayList<>();
        for (Skull skull : skulls) {
            ItemDisplay display = SkullHandler.getItemDisplayBySkull(loc, skull);
            parts.add(new BlockPart(display, skull));
        }
        return parts;
    }

    public void changeBlockParts(List<BlockPart> parts, int index) {
        if (!is_init) return;
        for (BlockPart part : parts) {
            changeBlockPart(part, index);
        }
    }

    public void changeBlockPart(BlockPart part, int index) {
        if (!is_init) return;
        ItemDisplay display = part.getDisplay();
        Skull skull = part.getSkull();
        if (skull.getType() != SkullType.BLOCK_PART) return;

        ItemStack stack = SkullHandler.getItemBySkull(skull, index);
        display.setItemStack(stack);
    }

    public TextDisplay getFrequencyIndicator(RadioData data) {
        return getFrequencyIndicator(data.getLocation(), data.getFrequency());
    }

    public TextDisplay getFrequencyIndicator(Location loc, int frequency) {
        if (!is_init) return null;
        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class);
        display.text(Component.text(String.valueOf(frequency), NamedTextColor.DARK_RED));
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setViewRange(512.0F);
        Vector3f scale = new Vector3f(1.5F, 1.435F, 0.0F);
        Vector3f translation = new Vector3f(-0.501F, -0.01F, -0.0185F);
        Quaternionf leftRot = (new Quaternionf()).rotateY((float)Math.toRadians((double)270.0F));
        SkullHandler.updateTransformation(display, translation, leftRot, scale, null);
        return display;
    }

    public void changeFrequencyIndicator(TextDisplay display, int frequency) {
        if (!is_init) return;
        display.text(Component.text(String.valueOf(frequency), NamedTextColor.DARK_RED));
    }
}
