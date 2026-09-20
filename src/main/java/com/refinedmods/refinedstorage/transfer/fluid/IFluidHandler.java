package com.refinedmods.refinedstorage.transfer.fluid;

import com.refinedmods.refinedstorage.transfer.FluidStack;

public interface IFluidHandler {
    enum FluidAction {
        SIMULATE,
        EXECUTE
    }

    int getTanks();

    FluidStack getFluidInTank(int tank);

    int getTankCapacity(int tank);

    boolean isFluidValid(int tank, FluidStack stack);

    int fill(FluidStack resource, FluidAction action);

    FluidStack drain(FluidStack resource, FluidAction action);

    FluidStack drain(int maxDrain, FluidAction action);
}
