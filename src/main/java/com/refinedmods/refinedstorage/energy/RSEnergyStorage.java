package com.refinedmods.refinedstorage.energy;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import com.refinedmods.refinedstorage.transfer.energy.IEnergyStorage;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public final class RSEnergyStorage {
    public static final BlockApiLookup<IEnergyStorage, Direction> BLOCK = BlockApiLookup.get(
        new ResourceLocation(RS.ID, "energy_storage"), IEnergyStorage.class, Direction.class
    );
    public static final ItemApiLookup<IEnergyStorage, Void> ITEM = ItemApiLookup.get(
        new ResourceLocation(RS.ID, "item_energy_storage"), IEnergyStorage.class, Void.class
    );

    private RSEnergyStorage() {
    }

    public static void register() {
        BLOCK.registerForBlockEntity(
            (ControllerBlockEntity blockEntity, Direction direction) -> blockEntity.getNetwork().getEnergyStorage(),
            RSBlockEntities.CONTROLLER.get()
        );
        BLOCK.registerForBlockEntity(
            (ControllerBlockEntity blockEntity, Direction direction) -> blockEntity.getNetwork().getEnergyStorage(),
            RSBlockEntities.CREATIVE_CONTROLLER.get()
        );
        BLOCK.registerForBlockEntity(
            (PortableGridBlockEntity blockEntity, Direction direction) -> blockEntity.getEnergyStorage(),
            RSBlockEntities.PORTABLE_GRID.get()
        );
        BLOCK.registerForBlockEntity(
            (PortableGridBlockEntity blockEntity, Direction direction) -> blockEntity.getEnergyStorage(),
            RSBlockEntities.CREATIVE_PORTABLE_GRID.get()
        );
        ITEM.registerFallback((stack, context) -> ItemEnergyStorageFactory.get(stack));
    }
}


