package me.ybbbno.radio.item;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RadioItemListener implements Listener {

    private final RadioItemManager item;

    public RadioItemListener(RadioItemManager manager) {
        this.item = manager;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getFirstItem();

        if (event.getResult() == null) return;

        if (first != null && first.getType() == Material.PLAYER_HEAD && item.isItemRadio(first)) {
            String name = PlainTextComponentSerializer.plainText().serialize(event.getResult().displayName()).replace("[","").replace("]","");
            event.setResult(item.changeNameOfItem(name, first.clone()));
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack.getType() != Material.PLAYER_HEAD || !item.isItemRadio(itemStack)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (cursor.getType() != Material.PLAYER_HEAD || !item.isItemRadio(cursor)) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }
}
