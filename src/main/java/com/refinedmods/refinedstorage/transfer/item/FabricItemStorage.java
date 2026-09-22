package com.refinedmods.refinedstorage.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Exposes an internal item handler through Fabric Transfer transactions. */
public final class FabricItemStorage extends SnapshotParticipant<List<ItemStack>> implements Storage<ItemVariant> {
    private final IItemHandlerModifiable handler;

    public FabricItemStorage(IItemHandlerModifiable handler) {
        this.handler = handler;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (maxAmount == 0) {
            return 0;
        }

        updateSnapshots(transaction);
        long remaining = maxAmount;
        for (int slot = 0; slot < handler.getSlots() && remaining > 0; ++slot) {
            int offered = (int) Math.min(Integer.MAX_VALUE, remaining);
            ItemStack remainder = handler.insertItem(slot, resource.toStack(offered), false);
            remaining -= offered - remainder.getCount();
        }
        return maxAmount - remaining;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (maxAmount == 0) {
            return 0;
        }

        updateSnapshots(transaction);
        long extracted = 0;
        for (int slot = 0; slot < handler.getSlots() && extracted < maxAmount; ++slot) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (resource.matches(stack)) {
                int requested = (int) Math.min(Integer.MAX_VALUE, maxAmount - extracted);
                extracted += handler.extractItem(slot, requested, false).getCount();
            }
        }
        return extracted;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        List<StorageView<ItemVariant>> views = new ArrayList<>(handler.getSlots());
        for (int slot = 0; slot < handler.getSlots(); ++slot) {
            views.add(new SlotView(slot));
        }
        return views.iterator();
    }

    @Override
    protected List<ItemStack> createSnapshot() {
        List<ItemStack> snapshot = new ArrayList<>(handler.getSlots());
        for (int slot = 0; slot < handler.getSlots(); ++slot) {
            snapshot.add(handler.getStackInSlot(slot).copy());
        }
        return snapshot;
    }

    @Override
    protected void readSnapshot(List<ItemStack> snapshot) {
        for (int slot = 0; slot < snapshot.size(); ++slot) {
            handler.setStackInSlot(slot, snapshot.get(slot));
        }
    }

    private final class SlotView implements StorageView<ItemVariant> {
        private final int slot;

        private SlotView(int slot) {
            this.slot = slot;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            ItemStack stack = handler.getStackInSlot(slot);
            if (maxAmount == 0 || !resource.matches(stack)) {
                return 0;
            }

            updateSnapshots(transaction);
            int requested = (int) Math.min(Integer.MAX_VALUE, maxAmount);
            return handler.extractItem(slot, requested, false).getCount();
        }

        @Override
        public boolean isResourceBlank() {
            return handler.getStackInSlot(slot).isEmpty();
        }

        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(handler.getStackInSlot(slot));
        }

        @Override
        public long getAmount() {
            return handler.getStackInSlot(slot).getCount();
        }

        @Override
        public long getCapacity() {
            return handler.getSlotLimit(slot);
        }
    }
}


