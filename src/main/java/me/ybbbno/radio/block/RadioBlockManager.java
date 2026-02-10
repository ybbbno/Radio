package me.ybbbno.radio.block;

import de.maxhenkel.voicechat.api.Position;
import me.deadybbb.ybmj.BasicManagerHandler;
import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.customsounds.CustomSoundsAPI;
import me.ybbbno.customsounds.VoicechatManager;
import me.ybbbno.radio.block.part.BlockPartManager;
import me.ybbbno.radio.block.data.RadioData;
import me.ybbbno.radio.block.data.RadioDataConfigManager;
import me.ybbbno.radio.block.data.RadioState;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RadioBlockManager extends BasicManagerHandler {
    private final List<RadioBlock> radioBlocks = new ArrayList<>();
    private final BlockPartManager blockPartsM;
    private final RadioDataConfigManager config;
    private VoicechatManager managerS;

    public RadioBlockManager(PluginProvider plugin) {
        super(plugin);
        config = new RadioDataConfigManager(plugin);
        blockPartsM = new BlockPartManager(plugin);
        managerS = CustomSoundsAPI.api().manager();

        managerS.addListener(new RadioBlockVoicechatListener(plugin, this));
    }

    @Override
    protected void onInit() {
        blockPartsM.init();
        config.getRadioData().forEach(data -> {
            RadioBlock block = new RadioBlock(data, blockPartsM.getBlockParts(data), blockPartsM.getFrequencyIndicator(data));
            radioBlocks.add(block);
        });
    }

    @Override
    protected void onDeinit() {
        config.setRadioData(radioBlocks.stream().map(RadioBlock::data).toList());
        blockPartsM.deinit();
        removeAllBlocks();
    }

    public void setBlock(RadioData data) {
        if (data == null) return;
        setBlock(data.name(), data.location(), data.state(), data.frequency());
    }

    public void setBlock(String name, Location loc, RadioState state, Integer frequency) {
        if (loc == null || state == null || frequency == null) return;
        radioBlocks.removeIf(block -> block.data().location().distance(loc) == 0);
        RadioData data = new RadioData(name, loc, state, frequency);
        RadioBlock block = new RadioBlock(data, blockPartsM.getBlockParts(data), blockPartsM.getFrequencyIndicator(data));
        managerS.createAudioPlayer(block.data().location(), 16, RadioBlockVoicechatListener.RADIO_VOLUME_CATEGORY);
        radioBlocks.add(block);
    }

    public RadioBlock getBlock(Location loc) {
        return radioBlocks.stream()
                .filter(block -> block.data().location().getWorld().equals(loc.getWorld()))
                .filter(block -> block.data().location().distance(loc) == 0).findFirst().orElse(null);
    }

    public void removeAllBlocks() {
        radioBlocks.forEach(block -> {
            block.parts().forEach(part -> part.display().remove());
            block.indicator().remove();
            managerS.destroyByLocation(block.data().location());
        });

        radioBlocks.clear();
    }

    public void removeBlock(Location loc) {
        radioBlocks.forEach(block -> {
            if (block.data().location().distance(loc) == 0) {
                block.parts().forEach(part -> part.display().remove());
                block.indicator().remove();
                managerS.destroyByLocation(block.data().location());
            }
        });

        radioBlocks.removeIf(block -> block.data().location().distance(loc) == 0);
    }

    public void updateBlock(RadioBlock block) {
        blockPartsM.updateParts(block);
    }

    public List<RadioBlock> getAllBlocks() {
        return List.copyOf(radioBlocks);
    }

    public List<RadioBlock> getBlocksNearPosition(Position pos, float distance) {
        return radioBlocks.stream().filter(block -> {
            Location loc = block.data().location();
            double outDistance = Math.sqrt(
                    Math.pow(loc.getX() + 0.5 - pos.getX(), 2.0) +
                    Math.pow(loc.getY() + 0.5 - pos.getY(), 2.0) +
                    Math.pow(loc.getZ() + 0.5 - pos.getZ(), 2.0));

            return outDistance <= distance;
        }).toList();
    }
}
