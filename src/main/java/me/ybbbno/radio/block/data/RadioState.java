package me.ybbbno.radio.block.data;

public enum RadioState {
    INPUT,
    OUTPUT;

    public static RadioState next(RadioState state) { return state == INPUT ? OUTPUT : INPUT; }
    public static RadioState valueOf(int i) { return i == 1 ? INPUT : OUTPUT; }
    public static int valueOf(RadioState state) { return state == INPUT ? 1 : 0; }
}
