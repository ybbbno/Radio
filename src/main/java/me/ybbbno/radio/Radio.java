package me.ybbbno.radio;

import me.deadybbb.ybmj.PluginProvider;
import me.ybbbno.radio.block.RadioBlockListener;
import me.ybbbno.radio.block.RadioBlockManager;
import me.ybbbno.radio.item.RadioItemListener;
import me.ybbbno.radio.item.RadioItemManager;

public final class Radio extends PluginProvider {
    private RadioBlockManager blockM;
    private RadioItemManager itemM;

    @Override
    public void onEnable() {
        blockM = new RadioBlockManager(this);
        itemM = new RadioItemManager(this);

        blockM.init();

        getServer().getPluginManager().registerEvents(new RadioBlockListener(this, itemM, blockM), this);
        getServer().getPluginManager().registerEvents(new RadioItemListener(itemM), this);
        registerCommand("radio", new RadioCommand(itemM));
    }

    @Override
    public void onDisable() {
        blockM.deinit();
    }
}
