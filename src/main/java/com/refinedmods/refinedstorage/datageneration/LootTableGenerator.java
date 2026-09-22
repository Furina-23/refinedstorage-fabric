package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public final class LootTableGenerator extends FabricBlockLootTableProvider {
    public LootTableGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        RSBlocks.CONTROLLER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), ControllerLootFunction.builder()));
        RSBlocks.CREATIVE_CONTROLLER.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.CRAFTER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), CrafterLootFunction.builder()));
        RSBlocks.GRID.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.CRAFTING_GRID.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.FLUID_GRID.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.PATTERN_GRID.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.SECURITY_MANAGER.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.WIRELESS_TRANSMITTER.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.RELAY.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.NETWORK_TRANSMITTER.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.NETWORK_RECEIVER.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.DISK_MANIPULATOR.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.CRAFTING_MONITOR.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.CRAFTER_MANAGER.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.DETECTOR.values().forEach(block -> dropSelf(block.get()));
    }

    private void genBlockItemLootTableWithFunction(Block block, LootItemFunction.Builder builder) {
        add(block, LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block)
                    .apply(builder))
                .when(ExplosionCondition.survivesExplosion())));
    }
}


