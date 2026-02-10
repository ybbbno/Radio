package me.ybbbno.radio.item;

import me.deadybbb.ybmj.LegacyTextHandler;
import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.customskulls.CustomSkullAPI;
import me.ybbbno.customskulls.display.SkullDisplayManager;
import me.ybbbno.customskulls.skull.Skull;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class RadioItem {
    private NamespacedKey key;

    private final PluginProvider plugin;
    private final SkullDisplayManager manager;

    public static final String keyS = "radio-item";

    public RadioItem(PluginProvider plugin) {
        this.plugin = plugin;
        this.manager = CustomSkullAPI.api().manager();
        key = new NamespacedKey(plugin, "radio");
        registerCraft();
    }

    public ItemStack getItem() {
        if (manager == null) return null;

        Skull skull = manager.getSkullByName(keyS);
        if (skull == null) {
            manager.addSkull(skullOfItem());
            skull = manager.getSkullByName(keyS);
        }

        if (skull == null) return null;

        ItemStack item = manager.getItemBySkull(skull, 0);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(LegacyTextHandler.parseText("Радио").color(TextColor.fromHexString("#f57542")));
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isItemRadio(ItemStack stack) {
        return Objects.equals(stack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN), true);
    }

    public boolean isSkullRadio(org.bukkit.block.Skull skull) {
        return Objects.equals(skull.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN), true);
    }

    public void setRadioToSkull(org.bukkit.block.Skull skull) {
        if (isSkullRadio(skull)) return;
        skull.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        skull.update();
    }

    public NamespacedKey key() { return key; }

    private Skull skullOfItem() {
        return new Skull(keyS, List.of("ewogICJ0aW1lc3RhbXAiIDogMTc1OTMwNjQ4NjQ2MiwKICAicHJvZmlsZUlkIiA6ICI4MTFkNjM0NzUwY2Y0ZDI0OTJmZDcxYTViZjZhZjI3MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGlmZnRvcGlhbW1wMSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84Mjc1NjA2YTA3MmYzODRjZWIyMTNjMWVlMmIzMTAxNWQ1ZGY0ZjY4MDFmNjRlMGFjMGM5ZGJkOGM1NTEwMWZjIgogICAgfQogIH0KfQ=="));
    }

    private void registerCraft() {

        ItemStack item = getItem();
        if (item == null) return;

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("CHC", "IJI", "RNR");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('H', Material.CHISELED_COPPER);
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('J', Material.JUKEBOX);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('R', Material.REDSTONE);

        plugin.getServer().addRecipe(recipe);
    }
}
