package com.refinedmods.refinedstorage.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

/** Adapts Fabric Transfer item storage to the internal handler contract. */
public final class FabricItemHandler implements IItemHandler {
    private final Storage<ItemVariant> storage;

    public FabricItemHandler(Storage<ItemVariant> storage) {
        this.storage = storage;
    }

    @Override
    public int getSlots() {
        int slots = 0;
        for (StorageView<ItemVariant> ignored : storage) {
            slots++;
        }
        return slots;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        StorageView<ItemVariant> view = viewAt(slot);
        return view == null || view.isResourceBlank() ? ItemStack.EMPTY : view.getResource().toStack((int) Math.min(Integer.MAX_VALUE, view.getAmount()));
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }
        StorageView<ItemVariant> view = viewAt(slot);
        if (view == null) {
            return stack;
        }
        ItemVariant variant = ItemVariant.of(stack);
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(variant, stack.getCount(), transaction);
            if (!simulate) {
                transaction.commit();
            }
            if (inserted >= stack.getCount()) {
                return ItemStack.EMPTY;
            }
            ItemStack remainder = stack.copy();
            remainder.shrink((int) inserted);
            return remainder;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        StorageView<ItemVariant> view = viewAt(slot);
        if (view == null || view.isResourceBlank()) {
            return ItemStack.EMPTY;
        }
        ItemVariant variant = view.getResource();
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = view.extract(variant, amount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return variant.toStack((int) Math.min(Integer.MAX_VALUE, extracted));
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        StorageView<ItemVariant> view = viewAt(slot);
        return view == null ? 0 : (int) Math.min(Integer.MAX_VALUE, view.getCapacity());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty() && viewAt(slot) != null;
    }

    private StorageView<ItemVariant> viewAt(int slot) {
        if (slot < 0) {
            return null;
        }
        int index = 0;
        for (StorageView<ItemVariant> view : storage) {
            if (index++ == slot) {
                return view;
            }
        }
        return null;
    }
}
