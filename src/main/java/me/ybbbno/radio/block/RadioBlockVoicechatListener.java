package me.ybbbno.radio.block;

import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStoppedEvent;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.customsounds.CustomSoundsAPI;
import me.ybbbno.customsounds.VoicechatLifecycleListener;
import me.ybbbno.customsounds.VoicechatManager;
import me.ybbbno.radio.block.data.RadioState;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public class RadioBlockVoicechatListener implements VoicechatLifecycleListener {
    private final PluginProvider plugin;
    private final RadioBlockManager manager;
    private final VoicechatManager managerS;

    private static VoicechatServerApi api;
    public static final String RADIO_VOLUME_CATEGORY = "radio";

    public RadioBlockVoicechatListener(PluginProvider plugin, RadioBlockManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.managerS = CustomSoundsAPI.api().manager();
    }

    @Override
    public void onVoicechatServerStarted(VoicechatServerStartedEvent event) {
        api = event.getVoicechat();

        VolumeCategory radioVolume = api.volumeCategoryBuilder()
                .setId(RADIO_VOLUME_CATEGORY)
                .setName("Радио")
                .setDescription("Настройка громкости радио")
                .setIcon(getIcon())
                .build();

        api.registerVolumeCategory(radioVolume);

        manager.getAllBlocks().forEach(block -> {
            managerS.createAudioPlayer(block.data().location(), 16, RADIO_VOLUME_CATEGORY);
        });
    }

    @Override
    public void onVoicechatServerStopped(VoicechatServerStoppedEvent event) {
        manager.getAllBlocks().forEach(block -> {
            managerS.destroyByLocation(block.data().location());
        });
    }

    @Override
    public void onMicrophonePacket(MicrophonePacketEvent event) {
        try {
            VoicechatConnection connection = event.getSenderConnection();
            if (connection == null) return;
            sendPacket(connection.getPlayer().getPosition(), event.getPacket());
        } catch (Exception e) {
            plugin.logger.severe(e.getMessage());
        }
    }

    private void sendPacket(Position position, MicrophonePacket packet) {
        List<RadioBlock> near = manager.getBlocksNearPosition(position, 16);

        List<RadioBlock> inputs = near.stream().filter(block -> block.data().state().equals(RadioState.INPUT)).toList();
        if (inputs.isEmpty()) return;

        List<RadioBlock> outputs = manager.getAllBlocks().stream().filter(block -> block.data().state().equals(RadioState.OUTPUT)).toList();
        if (outputs.isEmpty()) return;

        inputs.forEach(input -> {
                    outputs.forEach(output -> {
                        if (input.data().frequency() != output.data().frequency()) return;

                        Location loc = output.data().location();
                        if (!loc.isChunkLoaded()) return;

                        managerS.sendData(packet, loc);

                        if (System.currentTimeMillis() % 500L < 20L) {
                            loc.getWorld().spawnParticle(Particle.NOTE, loc.getX() + 0.5,
                                    loc.getY() + 1, loc.getZ() + 0.5, 1,
                                    0, 0, 0, 0, null, true);
                        }
                    });
                }
        );
    }

    @Nullable
    private int[][] getIcon() {
        try {
            InputStream stream = plugin.getResource("radio-icon.png");
            if (stream == null) return null;

            BufferedImage bufferedImage = ImageIO.read(stream);
            if (bufferedImage.getWidth() != 16) return null;
            if (bufferedImage.getHeight() != 16) return null;
            int[][] image = new int[16][16];
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    image[x][y] = bufferedImage.getRGB(x, y);
                }
            }
            return image;
        } catch (Exception e) {
            plugin.logger.severe(e.getMessage());
        }
        return null;
    }
}
