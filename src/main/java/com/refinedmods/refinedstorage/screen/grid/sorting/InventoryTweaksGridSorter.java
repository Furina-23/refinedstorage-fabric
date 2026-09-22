package com.refinedmods.refinedstorage.screen.grid.sorting;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

public class InventoryTweaksGridSorter implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return false;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, SortingDirection direction) {
        return 0;
    }
}

