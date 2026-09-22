package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public final class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ItemTagGenerator(
        FabricDataOutput output,
        CompletableFuture<HolderLookup.Provider> registriesFuture,
        BlockTagGenerator blockTags
    ) {
        super(output, registriesFuture, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        RSItems.COLORED_ITEM_TAGS.forEach((tag, map) ->
            map.values().forEach(item -> getOrCreateTagBuilder(tag).add(item.get()))
        );
    }
}


