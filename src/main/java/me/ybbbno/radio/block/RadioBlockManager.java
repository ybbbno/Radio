package me.ybbbno.radio.block;

import me.deadybbb.ybmj.BasicManagerHandler;
import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.radio.block.blockpart.BlockPartManager;
import me.ybbbno.radio.block.data.RadioData;
import me.ybbbno.radio.block.data.RadioDataConfigManager;
import me.ybbbno.radio.block.data.RadioState;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RadioBlocksManager extends BasicManagerHandler {
    private final List<RadioBlock> radioBlocks;
    private final BlockPartManager blockPartsM;
    private final RadioDataConfigManager config;

    public RadioBlocksManager(PluginProvider plugin) {
        super(plugin);
        radioBlocks = new ArrayList<>();
        config = new RadioDataConfigManager(plugin);
        blockPartsM = new BlockPartManager(plugin);
    }

    @Override
    protected void onInit() {
        blockPartsM.init();
        radioBlocks.clear();
        config.getRadioData().forEach(data -> {
            RadioBlock block = new RadioBlock(data, blockPartsM.getBlockParts(data), blockPartsM.getFrequencyIndicator(data));
            radioBlocks.add(block);
        });
    }

    @Override
    protected void onDeinit() {
        config.setRadioData(radioBlocks.stream().map(RadioBlock::getData).toList());
        blockPartsM.deinit();
    }

    public void setBlock(Location loc, RadioState state, int frequency) {
        if (!is_init) return;
        radioBlocks.removeIf(block -> block.getData().getLocation() == loc);
        RadioData data = new RadioData(loc, state, frequency);
        RadioBlock block = new RadioBlock(data, blockPartsM.getBlockParts(data), blockPartsM.getFrequencyIndicator(data));
        radioBlocks.add(block);
    }

    public RadioBlock getBlock(Location loc) {
        if (!is_init) return null;
        return radioBlocks.stream().filter(block -> block.getData().getLocation().equals(loc)).findFirst().orElse(null);
    }

    public void removeBlock(Location loc) {
        if (!is_init) return;
        radioBlocks.removeIf(block -> block.getData().getLocation() == loc);
    }

    public List<RadioBlock> getAllBlocks() {
        if (!is_init) return null;
        return List.copyOf(radioBlocks);
    }
}
