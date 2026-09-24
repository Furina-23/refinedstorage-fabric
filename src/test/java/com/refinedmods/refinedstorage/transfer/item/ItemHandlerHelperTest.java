package com.refinedmods.refinedstorage.transfer.item;

import com.refinedmods.refinedstorage.transfer.item.wrapper.CombinedInvWrapper;
import com.refinedmods.refinedstorage.transfer.item.wrapper.RangedWrapper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.refinedmods.refinedstorage.MinecraftTestBootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemHandlerHelperTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        MinecraftTestBootstrap.boot();
    }

    @Test
    void stackedInsertionFillsMatchingSlotsBeforeEmptySlots() {
        ItemStackHandler first = new ItemStackHandler(2);
        first.setStackInSlot(0, new ItemStack(Items.STONE, 60));
        ItemStackHandler second = new ItemStackHandler(1);
        CombinedInvWrapper combined = new CombinedInvWrapper(first, second);

        ItemStack remainder = ItemHandlerHelper.insertItemStacked(combined, new ItemStack(Items.STONE, 10), false);

        assertEquals(0, remainder.getCount());
        assertEquals(64, first.getStackInSlot(0).getCount());
        assertEquals(6, first.getStackInSlot(1).getCount());
        assertEquals(0, second.getStackInSlot(0).getCount());
    }

    @Test
    void rangedWrapperMapsSlotsToDelegate() {
        ItemStackHandler delegate = new ItemStackHandler(3);
        RangedWrapper range = new RangedWrapper(delegate, 1, 3);

        range.insertItem(0, new ItemStack(Items.APPLE, 2), false);

        assertEquals(2, delegate.getStackInSlot(1).getCount());
        assertEquals(2, range.getSlots());
        assertThrows(IndexOutOfBoundsException.class, () -> range.getStackInSlot(2));
    }
}
