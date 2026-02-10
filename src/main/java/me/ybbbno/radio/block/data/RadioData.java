package me.ybbbno.radio.data;

import org.bukkit.Location;

public class RadioData {
    private int frequency;
    private RadioState state;
    private Location location;

    public RadioData(Location location, RadioState state, int frequency) {
        this.location = location;
        this.state = state;
        this.frequency = frequency;
    }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }
    public RadioState getState() { return state; }
    public void setState(RadioState state) { this.state = state; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
}
