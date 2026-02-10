package me.ybbbno.radio.data;

import me.deadybbb.ybmj.BasicConfigHandler;
import me.deadybbb.ybmj.LegacyTextHandler;
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

            Location location = Location.deserialize(dataSection.getValues(false));
            RadioState state = RadioState.valueOf(dataSection.getString("state", "INPUT"));
            int frequency = dataSection.getInt("frequency");

            radioDataList.add(new RadioData(location, state, frequency));
        }

        return radioDataList;
    }

    public void setRadioData(List<RadioData> radioDataList) {
        List<ConfigurationSection> sections = new ArrayList<>();
        for (int i = 0; i < radioDataList.size(); i++) {
            ConfigurationSection section = config.createSection(String.valueOf(i));
            section.set("location", radioDataList.get(i).getLocation().serialize());
            section.set("state", radioDataList.get(i).getState().name());
            section.set("frequency", radioDataList.get(i).getFrequency());
            sections.add(section);
        }
        config.set("radio_data", sections);
        saveConfig();
    }
}
