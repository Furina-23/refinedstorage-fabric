package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public final class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public BlockTagGenerator(
        FabricDataOutput output,
        CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        RSBlocks.COLORED_BLOCK_TAGS.forEach((tag, map) ->
            map.values().forEach(block -> getOrCreateTagBuilder(tag).add(block.get()))
        );
    }
}
