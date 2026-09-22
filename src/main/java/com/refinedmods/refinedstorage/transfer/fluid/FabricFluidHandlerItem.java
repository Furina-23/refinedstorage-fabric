package com.refinedmods.refinedstorage.transfer.fluid;

import com.refinedmods.refinedstorage.transfer.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

public final class FabricFluidHandlerItem implements IFluidHandlerItem {
    private static final long SCALE = FluidConstants.BUCKET / 1000;
    private final ContainerItemContext context;
    private final Storage<FluidVariant> storage;

    private FabricFluidHandlerItem(ItemStack stack) {
        context = ContainerItemContext.withInitial(stack.copy());
        storage = context.find(FluidStorage.ITEM);
    }

    public static FabricFluidHandlerItem find(ItemStack stack) {
        FabricFluidHandlerItem result = new FabricFluidHandlerItem(stack);
        return result.storage == null ? null : result;
    }

    public ItemStack getContainer() {
        ItemVariant variant = context.getItemVariant();
        return variant.isBlank() ? ItemStack.EMPTY : variant.toStack((int) context.getAmount());
    }

    public int getTanks() { return 1; }
    public FluidStack getFluidInTank(int tank) {
        for (StorageView<FluidVariant> view : storage) {
            if (!view.isResourceBlank()) return new FluidStack(view.getResource(), (int) (view.getAmount() / SCALE));
        }
        return FluidStack.EMPTY;
    }
    public int getTankCapacity(int tank) {
        for (StorageView<FluidVariant> view : storage) return (int) (view.getCapacity() / SCALE);
        return 1000;
    }
    public boolean isFluidValid(int tank, FluidStack stack) { return true; }
    public int fill(FluidStack resource, FluidAction action) {
        try (Transaction tx = Transaction.openOuter()) {
            long amount = storage.insert(FluidVariant.of(resource.getFluid(), resource.getTag()), (long) resource.getAmount() * SCALE, tx);
            if (action == FluidAction.EXECUTE) tx.commit();
            return (int) (amount / SCALE);
        }
    }
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return drainVariant(FluidVariant.of(resource.getFluid(), resource.getTag()), resource.getAmount(), action);
    }
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack current = getFluidInTank(0);
        return current.isEmpty() ? FluidStack.EMPTY : drainVariant(FluidVariant.of(current.getFluid(), current.getTag()), maxDrain, action);
    }
    private FluidStack drainVariant(FluidVariant variant, int amount, FluidAction action) {
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = storage.extract(variant, (long) amount * SCALE, tx);
            if (action == FluidAction.EXECUTE) tx.commit();
            return new FluidStack(variant, (int) (extracted / SCALE));
        }
    }
}


