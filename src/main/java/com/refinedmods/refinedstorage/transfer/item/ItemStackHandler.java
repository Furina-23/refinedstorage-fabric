package com.refinedmods.refinedstorage.transfer.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public class ItemStackHandler implements IItemHandlerModifiable {
    protected final NonNullList<ItemStack> stacks;

    public ItemStackHandler(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return stacks.get(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        stacks.set(slot, stack.copy());
        onContentsChanged(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        validateSlotIndex(slot);
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }

        ItemStack existing = stacks.get(slot);
        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        if (!existing.isEmpty() && !ItemStack.isSameItemSameTags(existing, stack)) {
            return stack;
        }

        int space = limit - existing.getCount();
        if (space <= 0) {
            return stack;
        }

        int inserted = Math.min(space, stack.getCount());
        if (!simulate) {
            ItemStack result = existing.isEmpty() ? stack.copy() : existing.copy();
            result.setCount(existing.getCount() + inserted);
            stacks.set(slot, result);
            onContentsChanged(slot);
        }

        if (inserted == stack.getCount()) {
            return ItemStack.EMPTY;
        }

        ItemStack remainder = stack.copy();
        remainder.shrink(inserted);
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);
        if (amount <= 0 || stacks.get(slot).isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = stacks.get(slot);
        int extracted = Math.min(amount, existing.getCount());
        ItemStack result = existing.copyWithCount(extracted);
        if (!simulate) {
            ItemStack remaining = existing.copy();
            remaining.shrink(extracted);
            stacks.set(slot, remaining);
            onContentsChanged(slot);
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        validateSlotIndex(slot);
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        return true;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, stacks);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, stacks);
        for (int i = 0; i < stacks.size(); ++i) {
            onContentsChanged(i);
        }
    }

    protected void onContentsChanged(int slot) {
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            throw new IndexOutOfBoundsException("Invalid slot " + slot);
        }
    }
}


