package com.refinedmods.refinedstorage;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class RSKeyBindings {
    public static final KeyMapping FOCUS_SEARCH_BAR = new KeyMapping(
        "key.refinedstorage.focusSearchBar",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_TAB,
        RS.NAME
    );

    public static final KeyMapping CLEAR_GRID_CRAFTING_MATRIX = new KeyMapping(
        "key.refinedstorage.clearGridCraftingMatrix",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        RS.NAME
    );

    public static final KeyMapping OPEN_WIRELESS_GRID = new KeyMapping(
        "key.refinedstorage.openWirelessGrid",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        RS.NAME
    );

    public static final KeyMapping OPEN_WIRELESS_FLUID_GRID = new KeyMapping(
        "key.refinedstorage.openWirelessFluidGrid",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        RS.NAME
    );

    public static final KeyMapping OPEN_WIRELESS_CRAFTING_MONITOR = new KeyMapping(
        "key.refinedstorage.openWirelessCraftingMonitor",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        RS.NAME
    );

    public static final KeyMapping OPEN_PORTABLE_GRID = new KeyMapping(
        "key.refinedstorage.openPortableGrid",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        RS.NAME
    );

    private RSKeyBindings() {
    }
}


