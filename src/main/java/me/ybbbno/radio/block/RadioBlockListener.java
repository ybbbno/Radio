package me.ybbbno.radio.block;

import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.radio.block.data.RadioData;
import me.ybbbno.radio.block.data.RadioState;
import me.ybbbno.radio.item.RadioItemManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class RadioBlockListener implements Listener {
    private final PluginProvider plugin;
    private final RadioItemManager itemM;
    private final RadioBlockManager blockM;

    public RadioBlockListener(PluginProvider plugin, RadioItemManager itemM, RadioBlockManager blockM) {
        this.plugin = plugin;
        this.itemM = itemM;
        this.blockM = blockM;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        if (!itemStack.getType().equals(Material.PLAYER_HEAD) || !itemM.isItemRadio(itemStack)) return;

        Location center = event.getBlock().getLocation();
        float yaw = Math.round(event.getPlayer().getYaw() / 90) * 90;
        center.setPitch(0);
        center.setYaw(yaw + 90);
        blockM.setBlock(itemM.getDataFromItem(center, itemStack));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            center.getBlock().setBlockData(Bukkit.createBlockData(Material.WAXED_COPPER_GRATE), true);
        }, 1L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        RadioBlock radio = blockM.getBlock(loc);
        if (radio == null) return;

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(loc, itemM.getItem(radio.data()));
        }

        blockM.removeBlock(loc);
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (blockM.getBlock(block.getLocation()) != null) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (blockM.getBlock(block.getLocation()) != null) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> blockM.getBlock(block.getLocation()) != null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> blockM.getBlock(block.getLocation()) != null);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) ||
            event.getHand() == null ||
            !event.getHand().equals(EquipmentSlot.HAND) ||
            block == null) return;

        RadioBlock radio = blockM.getBlock(block.getLocation());
        if (radio == null) return;

        Player player = event.getPlayer();
        RadioData data = radio.data();

        if (player.isSneaking()) {
            int freq = data.frequency() + 1;
            data.frequency(freq > 15 ? 1 : freq);
            player.getWorld().playSound(data.location().clone().add(0.5, 0.5, 0.5), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 3.0F, 2.0F);
        } else {
            data.state(RadioState.next(radio.data().state()));
            player.getWorld().playSound(data.location().clone().add(0.5, 0.5, 0.5), Sound.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.MASTER, 3.0F, 0.0F);
        }

        blockM.updateBlock(radio);
    }
}
