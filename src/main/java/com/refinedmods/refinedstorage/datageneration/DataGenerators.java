package com.refinedmods.refinedstorage.datageneration;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        BlockTagGenerator blockTags = pack.addProvider(BlockTagGenerator::new);
        pack.addProvider((output, registries) -> new ItemTagGenerator(output, registries, blockTags));
        pack.addProvider(BlockEntityTagGenerator::new);
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(LootTableGenerator::new);
    }
}
