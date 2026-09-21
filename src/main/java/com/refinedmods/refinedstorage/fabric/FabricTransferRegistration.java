package com.refinedmods.refinedstorage.fabric;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.blockentity.CrafterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.DiskDriveBlockEntity;
import com.refinedmods.refinedstorage.blockentity.DiskManipulatorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.FluidInterfaceBlockEntity;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkTransmitterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.transfer.fluid.FabricFluidStorage;
import com.refinedmods.refinedstorage.transfer.item.FabricItemStorage;
import com.refinedmods.refinedstorage.transfer.item.IItemHandler;
import com.refinedmods.refinedstorage.transfer.item.IItemHandlerModifiable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class FabricTransferRegistration {
    private FabricTransferRegistration() {
    }

    public static void register() {
        ItemStorage.SIDED.registerForBlockEntities(
            FabricTransferRegistration::findItemStorage,
            RSBlockEntities.DISK_DRIVE.get(),
            RSBlockEntities.PATTERN_GRID.get(),
            RSBlockEntities.NETWORK_TRANSMITTER.get(),
            RSBlockEntities.INTERFACE.get(),
            RSBlockEntities.FLUID_INTERFACE.get(),
            RSBlockEntities.DISK_MANIPULATOR.get(),
            RSBlockEntities.CRAFTER.get()
        );
        FluidStorage.SIDED.registerForBlockEntity(
            FabricTransferRegistration::findFluidStorage,
            RSBlockEntities.FLUID_INTERFACE.get()
        );
    }

    private static Storage<ItemVariant> findItemStorage(BlockEntity blockEntity, Direction direction) {
        IItemHandler handler;
        if (blockEntity instanceof DiskDriveBlockEntity diskDrive) {
            handler = diskDrive.getNode().getDisks();
        } else if (blockEntity instanceof GridBlockEntity grid) {
            handler = grid.getNode().getPatterns();
        } else if (blockEntity instanceof NetworkTransmitterBlockEntity transmitter) {
            handler = transmitter.getNode().getNetworkCard();
        } else if (blockEntity instanceof InterfaceBlockEntity interfaceBlockEntity) {
            handler = interfaceBlockEntity.getNode().getItems();
        } else if (blockEntity instanceof FluidInterfaceBlockEntity fluidInterface) {
            handler = fluidInterface.getNode().getIn();
        } else if (blockEntity instanceof DiskManipulatorBlockEntity diskManipulator) {
            handler = diskManipulator.getNode().getDisks();
        } else if (blockEntity instanceof CrafterBlockEntity crafter
            && direction != null
            && direction != crafter.getNode().getDirection()) {
            handler = crafter.getNode().getPatternInventory();
        } else {
            return null;
        }
        return handler instanceof IItemHandlerModifiable modifiable ? new FabricItemStorage(modifiable) : null;
    }

    private static Storage<FluidVariant> findFluidStorage(FluidInterfaceBlockEntity blockEntity, Direction direction) {
        return new FabricFluidStorage(blockEntity.getNode().getTank());
    }
}
