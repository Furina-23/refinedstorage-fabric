package com.refinedmods.refinedstorage.integration.inventorysorter;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.resources.ResourceLocation;

public class InventorySorterIntegration {
    private static final String ID = "inventorysorter";

    private InventorySorterIntegration() {
    }

    public static boolean isLoaded() {
        return false;
    }

    public static void register() {
        // Prevent items moving while scrolling through slots with Inventory Sorter in the Crafter Manager
    }
}
