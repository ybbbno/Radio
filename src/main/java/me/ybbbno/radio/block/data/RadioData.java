package me.ybbbno.radio.block.data;

import org.bukkit.Location;

public class RadioData {
    private String name;
    private int frequency;
    private RadioState state;
    private Location location;

    public RadioData(String name, Location location, RadioState state, int frequency) {
        this.name = name;
        this.location = location;
        this.state = state;
        this.frequency = frequency;
    }

    public String name() { return name; }
    public void name(String name) { this.name = name; }
    public int frequency() { return frequency; }
    public void frequency(int frequency) { this.frequency = frequency; }
    public RadioState state() { return state; }
    public void state(RadioState state) { this.state = state; }
    public Location location() { return location; }
    public void location(Location location) { this.location = location; }
}
