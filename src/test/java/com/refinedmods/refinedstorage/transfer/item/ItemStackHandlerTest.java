package com.refinedmods.refinedstorage.transfer.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.refinedmods.refinedstorage.MinecraftTestBootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemStackHandlerTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        MinecraftTestBootstrap.boot();
    }

    @Test
    void insertsWithStackLimitAndExtracts() {
        ItemStackHandler handler = new ItemStackHandler(1);

        ItemStack remainder = handler.insertItem(0, new ItemStack(Items.STONE, 70), false);

        assertEquals(6, remainder.getCount());
        assertEquals(64, handler.getStackInSlot(0).getCount());
        assertEquals(10, handler.extractItem(0, 10, false).getCount());
        assertEquals(54, handler.getStackInSlot(0).getCount());
    }

    @Test
    void simulationDoesNotChangeContents() {
        ItemStackHandler handler = new ItemStackHandler(1);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 8));

        ItemStack insertRemainder = handler.insertItem(0, new ItemStack(Items.STONE, 4), true);
        ItemStack extracted = handler.extractItem(0, 3, true);

        assertTrue(insertRemainder.isEmpty());
        assertEquals(3, extracted.getCount());
        assertEquals(8, handler.getStackInSlot(0).getCount());
    }

    @Test
    void serializesAndRestoresStacks() {
        ItemStackHandler original = new ItemStackHandler(2);
        original.setStackInSlot(0, new ItemStack(Items.DIAMOND, 3));
        original.setStackInSlot(1, new ItemStack(Items.APPLE, 7));
        CompoundTag tag = original.serializeNBT();

        ItemStackHandler restored = new ItemStackHandler(2);
        restored.deserializeNBT(tag);

        assertEquals(Items.DIAMOND, restored.getStackInSlot(0).getItem());
        assertEquals(3, restored.getStackInSlot(0).getCount());
        assertEquals(Items.APPLE, restored.getStackInSlot(1).getItem());
        assertEquals(7, restored.getStackInSlot(1).getCount());
    }

    @Test
    void rejectsInvalidSlots() {
        ItemStackHandler handler = new ItemStackHandler(1);

        assertThrows(IndexOutOfBoundsException.class, () -> handler.getStackInSlot(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> handler.extractItem(1, 1, false));
    }
}
