package com.refinedmods.refinedstorage.fabric;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.RSCreativeModeTabItems;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.RSRecipeSerializers;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.energy.RSEnergyStorage;
import com.refinedmods.refinedstorage.setup.CommonSetup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class RefinedStorageFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RS.SERVER_CONFIG.getSpec().load(FabricLoader.getInstance().getConfigDir().resolve("refinedstorage-server.json"));
        RSBlocks.register();
        RSItems.register();
        RSBlockEntities.register();
        FabricTransferRegistration.register();
        RSEnergyStorage.register();
        RSContainerMenus.register();
        RSRecipeSerializers.register();
        RSLootFunctions.register();
        RSCreativeModeTabItems.registerFabric();

        CommonSetup.initialize();
        FabricEventRegistration.register();
        API.deliver();
    }
}
