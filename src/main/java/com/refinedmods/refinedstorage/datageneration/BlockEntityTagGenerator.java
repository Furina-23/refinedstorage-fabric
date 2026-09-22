package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlockEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.concurrent.CompletableFuture;

public final class BlockEntityTagGenerator extends FabricTagProvider<BlockEntityType<?>> {
    public BlockEntityTagGenerator(
        FabricDataOutput output,
        CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, Registries.BLOCK_ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        FabricTagBuilder problematic = getOrCreateTagBuilder(TagKey.create(
            Registries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("packingtape", "blacklist/problematic")
        ));
        problematic.add(
            RSBlockEntities.CONTROLLER.get(),
            RSBlockEntities.CREATIVE_CONTROLLER.get(),
            RSBlockEntities.DETECTOR.get(),
            RSBlockEntities.DISK_DRIVE.get(),
            RSBlockEntities.EXPORTER.get(),
            RSBlockEntities.EXTERNAL_STORAGE.get(),
            RSBlockEntities.GRID.get(),
            RSBlockEntities.CRAFTING_GRID.get(),
            RSBlockEntities.PATTERN_GRID.get(),
            RSBlockEntities.FLUID_GRID.get(),
            RSBlockEntities.IMPORTER.get(),
            RSBlockEntities.NETWORK_TRANSMITTER.get(),
            RSBlockEntities.NETWORK_RECEIVER.get(),
            RSBlockEntities.RELAY.get(),
            RSBlockEntities.CABLE.get(),
            RSBlockEntities.ONE_K_STORAGE_BLOCK.get(),
            RSBlockEntities.FOUR_K_STORAGE_BLOCK.get(),
            RSBlockEntities.SIXTEEN_K_STORAGE_BLOCK.get(),
            RSBlockEntities.SIXTY_FOUR_K_STORAGE_BLOCK.get(),
            RSBlockEntities.CREATIVE_STORAGE_BLOCK.get(),
            RSBlockEntities.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK.get(),
            RSBlockEntities.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK.get(),
            RSBlockEntities.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK.get(),
            RSBlockEntities.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK.get(),
            RSBlockEntities.CREATIVE_FLUID_STORAGE_BLOCK.get(),
            RSBlockEntities.SECURITY_MANAGER.get(),
            RSBlockEntities.INTERFACE.get(),
            RSBlockEntities.FLUID_INTERFACE.get(),
            RSBlockEntities.WIRELESS_TRANSMITTER.get(),
            RSBlockEntities.STORAGE_MONITOR.get(),
            RSBlockEntities.CONSTRUCTOR.get(),
            RSBlockEntities.DESTRUCTOR.get(),
            RSBlockEntities.DISK_MANIPULATOR.get(),
            RSBlockEntities.PORTABLE_GRID.get(),
            RSBlockEntities.CREATIVE_PORTABLE_GRID.get(),
            RSBlockEntities.CRAFTER.get(),
            RSBlockEntities.CRAFTER_MANAGER.get(),
            RSBlockEntities.CRAFTING_MONITOR.get()
        );
    }

    @Override
    public String getName() {
        return "Refined Storage block entity type tags";
    }
}


