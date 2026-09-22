package com.refinedmods.refinedstorage.container.transfer;

import com.refinedmods.refinedstorage.transfer.item.IItemHandler;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
    private final IItemHandler itemHandler;
    private final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int x, int y) {
        super(new HandlerContainer(itemHandler), index, x, y);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public int getSlotIndex() { return index; }

    @Override
    public ItemStack getItem() {
        return itemHandler.getStackInSlot(index);
    }

    @Override
    public void set(ItemStack stack) {
        if (itemHandler instanceof com.refinedmods.refinedstorage.transfer.item.IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(index, stack);
        }
        setChanged();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return itemHandler.isItemValid(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        return itemHandler.getSlotLimit(index);
    }

    private static final class HandlerContainer implements net.minecraft.world.Container {
        private final IItemHandler handler;

        private HandlerContainer(IItemHandler handler) {
            this.handler = handler;
        }

        @Override public int getContainerSize() { return handler.getSlots(); }
        @Override public boolean isEmpty() { for (int i = 0; i < getContainerSize(); i++) if (!getItem(i).isEmpty()) return false; return true; }
        @Override public ItemStack getItem(int slot) { return handler.getStackInSlot(slot); }
        @Override public ItemStack removeItem(int slot, int amount) { return handler.extractItem(slot, amount, false); }
        @Override public ItemStack removeItemNoUpdate(int slot) { return handler.extractItem(slot, getItem(slot).getCount(), false); }
        @Override public void setItem(int slot, ItemStack stack) { if (handler instanceof com.refinedmods.refinedstorage.transfer.item.IItemHandlerModifiable modifiable) modifiable.setStackInSlot(slot, stack); }
        @Override public void setChanged() { }
        @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return true; }
        @Override public void clearContent() { for (int i = 0; i < getContainerSize(); i++) removeItemNoUpdate(i); }
    }
}



