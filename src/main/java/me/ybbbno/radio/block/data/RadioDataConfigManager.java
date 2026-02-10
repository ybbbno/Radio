package me.ybbbno.radio.block.data;

import me.deadybbb.ybmj.BasicConfigHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class RadioDataConfigManager extends BasicConfigHandler {

    public RadioDataConfigManager(PluginProvider plugin) {
        super(plugin, "radio_data.yml");
    }

    public List<RadioData> getRadioData() {
        reloadConfig();
        List<RadioData> radioDataList = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection("radio_data");
        if (section == null) return radioDataList;

        for (String key : section.getKeys(false)) {
            ConfigurationSection dataSection = section.getConfigurationSection(key);
            if (dataSection == null) continue;

            ConfigurationSection locMap = dataSection.getConfigurationSection("location");
            if(locMap == null) continue;

            Location location = Location.deserialize(locMap.getValues(false));
            RadioState state = RadioState.valueOf(dataSection.getString("state", "INPUT"));
            int frequency = dataSection.getInt("frequency");

            String name = dataSection.getString("name", "Радио");

            radioDataList.add(new RadioData(name, location, state, frequency));
        }

        return radioDataList;
    }

    public void setRadioData(List<RadioData> radioDataList) {

        config.set("radio_data", null);

        for (int i = 0; i < radioDataList.size(); i++) {
            String key = String.valueOf(i);
            RadioData data = radioDataList.get(i);

            config.set("radio_data." + key + ".name", data.name());
            config.set("radio_data." + key + ".location", data.location().serialize());
            config.set("radio_data." + key + ".state", data.state().name());
            config.set("radio_data." + key + ".frequency", data.frequency());
        }

        saveConfig();
    }
}
