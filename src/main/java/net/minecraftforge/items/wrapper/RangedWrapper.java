package net.minecraftforge.items.wrapper;

import com.refinedmods.refinedstorage.transfer.item.IItemHandler;
import net.minecraft.world.item.ItemStack;

public class RangedWrapper implements IItemHandler {
    private final IItemHandler delegate; private final int start; private final int end;
    public RangedWrapper(IItemHandler delegate, int start, int end) { this.delegate = delegate; this.start = start; this.end = end; }
    @Override public int getSlots() { return end - start; }
    @Override public ItemStack getStackInSlot(int slot) { return delegate.getStackInSlot(start + slot); }
    @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return delegate.insertItem(start + slot, stack, simulate); }
    @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return delegate.extractItem(start + slot, amount, simulate); }
    @Override public int getSlotLimit(int slot) { return delegate.getSlotLimit(start + slot); }
    @Override public boolean isItemValid(int slot, ItemStack stack) { return delegate.isItemValid(start + slot, stack); }
}
