package com.refinedmods.refinedstorage.screen.grid.view;

import com.refinedmods.refinedstorage.MinecraftTestBootstrap;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GridViewImplTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        MinecraftTestBootstrap.boot();
    }

    @Test
    void findsZeroedEquivalentAtItsOriginalVisibleIndex() {
        ItemGridStack first = stack(Items.STONE, 32);
        ItemGridStack zeroed = stack(Items.DIRT, 64);
        zeroed.setQuantity(0);
        ItemGridStack third = stack(Items.DIAMOND, 4);
        List<IGridStack> visible = new ArrayList<>(List.of(first, zeroed, third));

        ItemGridStack returned = stack(Items.DIRT, 18);
        int index = GridViewImpl.findZeroedStackIndex(visible, returned);
        visible.set(index, returned);

        assertEquals(1, index);
        assertEquals(List.of(first, returned, third), visible);
        assertEquals(-1, GridViewImpl.findZeroedStackIndex(visible, stack(Items.APPLE, 1)));
    }

    private static ItemGridStack stack(net.minecraft.world.item.Item item, int count) {
        return new ItemGridStack(UUID.randomUUID(), null, new ItemStack(item, count), false, null);
    }
}
