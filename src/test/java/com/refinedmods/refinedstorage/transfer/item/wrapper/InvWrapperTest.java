package com.refinedmods.refinedstorage.transfer.item.wrapper;

import com.refinedmods.refinedstorage.MinecraftTestBootstrap;
import com.refinedmods.refinedstorage.transfer.item.ItemHandlerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvWrapperTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        MinecraftTestBootstrap.boot();
    }

    @Test
    void insertsOneStackIntoEmptySlotWithoutDuplicatingIt() {
        SimpleContainer inventory = new SimpleContainer(1);
        InvWrapper wrapper = new InvWrapper(inventory);

        ItemStack remainder = wrapper.insertItem(0, new ItemStack(Items.STONE, 64), false);

        assertTrue(remainder.isEmpty());
        assertEquals(64, inventory.getItem(0).getCount());
    }

    @Test
    void stackedInsertionSplitsBetweenExistingAndEmptySlotsExactlyOnce() {
        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, new ItemStack(Items.STONE, 60));
        InvWrapper wrapper = new InvWrapper(inventory);

        ItemStack remainder = ItemHandlerHelper.insertItemStacked(
            wrapper, new ItemStack(Items.STONE, 10), false);

        assertTrue(remainder.isEmpty());
        assertEquals(64, inventory.getItem(0).getCount());
        assertEquals(6, inventory.getItem(1).getCount());
    }

    @Test
    void simulatedInsertionDoesNotChangeEmptySlot() {
        SimpleContainer inventory = new SimpleContainer(1);
        InvWrapper wrapper = new InvWrapper(inventory);

        ItemStack remainder = wrapper.insertItem(0, new ItemStack(Items.DIRT, 32), true);

        assertTrue(remainder.isEmpty());
        assertTrue(inventory.getItem(0).isEmpty());
    }
}
