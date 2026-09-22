package com.refinedmods.refinedstorage.transfer.item.wrapper;

import com.refinedmods.refinedstorage.transfer.item.IItemHandler;
import net.minecraft.world.item.ItemStack;

public class CombinedInvWrapper implements IItemHandler {
    private final IItemHandler[] handlers;
    public CombinedInvWrapper(IItemHandler... handlers) { this.handlers = handlers; }
    private int[] locate(int slot) { for (int i = 0; i < handlers.length; i++) { int size = handlers[i].getSlots(); if (slot < size) return new int[]{i, slot}; slot -= size; } throw new IndexOutOfBoundsException(); }
    @Override public int getSlots() { int result = 0; for (IItemHandler handler : handlers) result += handler.getSlots(); return result; }
    @Override public ItemStack getStackInSlot(int slot) { int[] p = locate(slot); return handlers[p[0]].getStackInSlot(p[1]); }
    @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { int[] p = locate(slot); return handlers[p[0]].insertItem(p[1], stack, simulate); }
    @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { int[] p = locate(slot); return handlers[p[0]].extractItem(p[1], amount, simulate); }
    @Override public int getSlotLimit(int slot) { int[] p = locate(slot); return handlers[p[0]].getSlotLimit(p[1]); }
    @Override public boolean isItemValid(int slot, ItemStack stack) { int[] p = locate(slot); return handlers[p[0]].isItemValid(p[1], stack); }
}


