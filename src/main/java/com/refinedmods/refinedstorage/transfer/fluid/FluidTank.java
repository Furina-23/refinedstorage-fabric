package com.refinedmods.refinedstorage.transfer.fluid;

import com.refinedmods.refinedstorage.transfer.FluidStack;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Predicate;

public class FluidTank implements IFluidHandler {
    private final int capacity;
    private final Predicate<FluidStack> validator;
    private FluidStack fluid = FluidStack.EMPTY;

    public FluidTank(int capacity) {
        this(capacity, stack -> true);
    }

    public FluidTank(int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        validateTank(tank);
        return fluid;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public void setFluid(FluidStack fluid) {
        this.fluid = fluid == null ? FluidStack.EMPTY : fluid.copy();
        onContentsChanged();
    }

    @Override
    public int getTankCapacity(int tank) {
        validateTank(tank);
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        validateTank(tank);
        return validator.test(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource == null || resource.isEmpty() || !isFluidValid(0, resource)) {
            return 0;
        }
        if (!fluid.isEmpty() && !fluid.isFluidEqual(resource)) {
            return 0;
        }

        int inserted = Math.min(capacity - fluid.getAmount(), resource.getAmount());
        if (inserted > 0 && action == FluidAction.EXECUTE) {
            if (fluid.isEmpty()) {
                setFluid(resource.copy());
                fluid.setAmount(inserted);
            } else {
                fluid.grow(inserted);
                onContentsChanged();
            }
        }
        return inserted;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource == null || resource.isEmpty() || fluid.isEmpty() || !fluid.isFluidEqual(resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0 || fluid.isEmpty()) {
            return FluidStack.EMPTY;
        }
        int drained = Math.min(maxDrain, fluid.getAmount());
        FluidStack result = fluid.copy();
        result.setAmount(drained);
        if (action == FluidAction.EXECUTE) {
            fluid.shrink(drained);
            if (fluid.isEmpty()) {
                fluid = FluidStack.EMPTY;
            }
            onContentsChanged();
        }
        return result;
    }

    public CompoundTag writeToNBT(CompoundTag tag) {
        if (!fluid.isEmpty()) {
            fluid.writeToNBT(tag);
        }
        return tag;
    }

    public void readFromNBT(CompoundTag tag) {
        setFluid(FluidStack.loadFluidStackFromNBT(tag));
    }

    protected void onContentsChanged() {
    }

    private void validateTank(int tank) {
        if (tank != 0) {
            throw new IndexOutOfBoundsException("Invalid tank " + tank);
        }
    }
}
