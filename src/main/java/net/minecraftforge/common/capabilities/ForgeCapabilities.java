package net.minecraftforge.common.capabilities;

import com.refinedmods.refinedstorage.transfer.energy.IEnergyStorage;
import com.refinedmods.refinedstorage.transfer.fluid.IFluidHandler;
import com.refinedmods.refinedstorage.transfer.fluid.IFluidHandlerItem;
import com.refinedmods.refinedstorage.transfer.item.IItemHandler;

public final class ForgeCapabilities {
    public static final Capability<IEnergyStorage> ENERGY = new Capability<>();
    public static final Capability<IItemHandler> ITEM_HANDLER = new Capability<>();
    public static final Capability<IFluidHandler> FLUID_HANDLER = new Capability<>();
    public static final Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM = new Capability<>();

    private ForgeCapabilities() {
    }
}
