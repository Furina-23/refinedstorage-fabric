package com.refinedmods.refinedstorage.transfer.fluid;

import com.refinedmods.refinedstorage.transfer.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public final class FabricFluidHandler implements IFluidHandler {
    private static final long SCALE = FluidConstants.BUCKET / 1000;
    private final Storage<FluidVariant> storage;

    public FabricFluidHandler(Storage<FluidVariant> storage) {
        this.storage = storage;
    }

    @Override
    public int getTanks() {
        int tanks = 0;
        for (StorageView<FluidVariant> ignored : storage) {
            tanks++;
        }
        return tanks;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        int index = 0;
        for (StorageView<FluidVariant> view : storage) {
            if (index++ == tank) {
                return view.isResourceBlank() ? FluidStack.EMPTY : new FluidStack(view.getResource(), toDroplets(view.getAmount()));
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        int index = 0;
        for (StorageView<FluidVariant> view : storage) {
            if (index++ == tank) {
                return toDroplets(view.getCapacity());
            }
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return !stack.isEmpty();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        FluidVariant variant = resource.hasTag() ? FluidVariant.of(resource.getFluid(), resource.getTag()) : FluidVariant.of(resource.getFluid());
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(variant, toFabricUnits(resource.getAmount()), transaction);
            if (action == FluidAction.EXECUTE) {
                transaction.commit();
            }
            return toDroplets(inserted);
        }
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidVariant variant = resource.hasTag() ? FluidVariant.of(resource.getFluid(), resource.getTag()) : FluidVariant.of(resource.getFluid());
        return drain(variant, resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack current = getFluidInTank(0);
        if (current.isEmpty()) {
            return FluidStack.EMPTY;
        }
        FluidVariant variant = current.hasTag() ? FluidVariant.of(current.getFluid(), current.getTag()) : FluidVariant.of(current.getFluid());
        return drain(variant, maxDrain, action);
    }

    private FluidStack drain(FluidVariant variant, int amount, FluidAction action) {
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(variant, toFabricUnits(amount), transaction);
            if (action == FluidAction.EXECUTE) {
                transaction.commit();
            }
            return new FluidStack(variant, toDroplets(extracted));
        }
    }

    private static long toFabricUnits(int amount) {
        return (long) amount * SCALE;
    }

    private static int toDroplets(long amount) {
        return (int) Math.min(Integer.MAX_VALUE, amount / SCALE);
    }
}


