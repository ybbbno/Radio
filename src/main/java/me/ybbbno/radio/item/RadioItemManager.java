package me.ybbbno.radio.item;

import me.deadybbb.ybmj.LegacyTextHandler;
import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.customskulls.CustomSkullAPI;
import me.ybbbno.customskulls.display.SkullDisplayManager;
import me.ybbbno.customskulls.skull.Skull;
import me.ybbbno.radio.block.data.RadioData;
import me.ybbbno.radio.block.data.RadioDataConfigManager;
import me.ybbbno.radio.block.data.RadioState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;

import java.util.List;
import java.util.Objects;

public class RadioItemManager {
    private final NamespacedKey radioMarkerKey;
    private final NamespacedKey radioStateKey;
    private final NamespacedKey radioFreqKey;
    private final NamespacedKey radioCustomName;

    private final PluginProvider plugin;
    private final SkullDisplayManager manager;

    public static final String keyS = "radio-item";

    public RadioItemManager(PluginProvider plugin) {
        this.plugin = plugin;
        this.manager = CustomSkullAPI.api().manager();
        this.radioMarkerKey = new NamespacedKey(plugin, "radio");
        this.radioStateKey = new NamespacedKey(plugin, "radio-state");
        this.radioFreqKey = new NamespacedKey(plugin, "radio-freq");
        this.radioCustomName = new NamespacedKey(plugin, "radio-custom-name");
        registerCraft();
    }

    public ItemStack getItem() {
        ItemStack stack = getItemInstance();
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(radioStateKey, PersistentDataType.INTEGER, RadioState.valueOf(RadioState.OUTPUT));
        meta.getPersistentDataContainer().set(radioFreqKey, PersistentDataType.INTEGER, 1);
        meta.lore(List.of(
                Component.text("Частота: 1, Статус: Отдача", TextColor.fromHexString("#6e341d")),
                Component.text("Передаёт звуки на большие расстояния", TextColor.fromHexString("#6e341d"))
        ));
        stack.setItemMeta(meta);
        changeNameOfItem("Радио", stack);
        return stack;
    }

    public ItemStack getItem(RadioData data) {
        ItemStack stack = getItemInstance();
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(radioStateKey, PersistentDataType.INTEGER, RadioState.valueOf(data.state()));
        meta.getPersistentDataContainer().set(radioFreqKey, PersistentDataType.INTEGER, data.frequency());
        meta.lore(List.of(
                Component.text("Частота: " + data.frequency() + ", Статус: " + (data.state() == RadioState.INPUT ? "Принятие" : "Отдача"), TextColor.fromHexString("#6e341d")),
                Component.text("Передаёт звуки на большие расстояния", TextColor.fromHexString("#6e341d"))
        ));
        stack.setItemMeta(meta);
        changeNameOfItem(data.name(), stack);
        return stack;
    }

    public ItemStack changeNameOfItem(String name, ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(radioCustomName, PersistentDataType.STRING, name);
        meta.itemName(Component.text(name, TextColor.fromHexString("#f57542")));
        stack.setItemMeta(meta);
        return stack;
    }

    public RadioData getDataFromItem(Location location, ItemStack stack) {
        if (stack == null || !isItemRadio(stack)) return null;
        var pdc = stack.getItemMeta().getPersistentDataContainer();

        Integer stateI = null;
        try {
            stateI = pdc.get(radioStateKey, PersistentDataType.INTEGER);
        } catch (Exception ignored) { }

        RadioState state = RadioState.valueOf(stateI == null ? 0 : stateI);

        Integer frequency = null;
        try {
            frequency = pdc.get(radioFreqKey, PersistentDataType.INTEGER);
        } catch (Exception ignored) { }

        String name = "Радио";
        try {
             name = pdc.get(radioCustomName, PersistentDataType.STRING);
        } catch (Exception ignored) { }

        return new RadioData(name == null ? "Радио" : name, location, state, frequency == null ? 1 : frequency);
    }

    private ItemStack getItemInstance() {
        if (manager == null) return new ItemStack(Material.AIR);

        Skull skull = manager.getSkullByName(keyS);
        if (skull == null) {
            manager.addSkull(skullOfItem());
            skull = manager.getSkullByName(keyS);
        }

        if (skull == null) return new ItemStack(Material.AIR);

        ItemStack item = manager.getItemBySkull(skull, 0);
        if (item == null || item.getType() == Material.AIR) {
            return new ItemStack(Material.AIR);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            item.setItemMeta(meta = Bukkit.getItemFactory().getItemMeta(item.getType()));
            if (meta == null) {
                return new ItemStack(Material.AIR);
            }
        }

        meta.itemName(LegacyTextHandler.parseText("Радио").color(TextColor.fromHexString("#f57542")));
        meta.getPersistentDataContainer().set(radioMarkerKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isItemRadio(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }
        return Objects.equals(meta.getPersistentDataContainer().get(radioMarkerKey, PersistentDataType.BOOLEAN), true);
    }

    public boolean isSkullRadio(org.bukkit.block.Skull skull) {
        return Objects.equals(skull.getPersistentDataContainer().get(radioMarkerKey, PersistentDataType.BOOLEAN), true);
    }

    public void setRadioToSkull(org.bukkit.block.Skull skull) {
        if (isSkullRadio(skull)) return;
        skull.getPersistentDataContainer().set(radioMarkerKey, PersistentDataType.BOOLEAN, true);
        skull.update();
    }

    private Skull skullOfItem() {
        return new Skull(keyS, List.of("ewogICJ0aW1lc3RhbXAiIDogMTc1OTMwNjQ4NjQ2MiwKICAicHJvZmlsZUlkIiA6ICI4MTFkNjM0NzUwY2Y0ZDI0OTJmZDcxYTViZjZhZjI3MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGlmZnRvcGlhbW1wMSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84Mjc1NjA2YTA3MmYzODRjZWIyMTNjMWVlMmIzMTAxNWQ1ZGY0ZjY4MDFmNjRlMGFjMGM5ZGJkOGM1NTEwMWZjIgogICAgfQogIH0KfQ=="));
    }

    private void registerCraft() {

        ItemStack item = getItem();
        if (item == null) return;

        ShapedRecipe recipe = new ShapedRecipe(radioMarkerKey, item);
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
