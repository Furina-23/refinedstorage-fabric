package com.refinedmods.refinedstorage.fabric;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.RSKeyBindings;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.CrafterContainerMenu;
import com.refinedmods.refinedstorage.container.CrafterManagerContainerMenu;
import com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot;
import com.refinedmods.refinedstorage.item.property.ControllerItemPropertyGetter;
import com.refinedmods.refinedstorage.item.property.NetworkItemPropertyGetter;
import com.refinedmods.refinedstorage.item.property.SecurityCardItemPropertyGetter;
import com.refinedmods.refinedstorage.render.blockentity.StorageMonitorBlockEntityRenderer;
import com.refinedmods.refinedstorage.render.color.PatternItemColor;
import com.refinedmods.refinedstorage.render.model.FabricModelLoadingPlugin;
import com.refinedmods.refinedstorage.render.resourcepack.ResourcePackListener;
import com.refinedmods.refinedstorage.screen.*;
import com.refinedmods.refinedstorage.screen.factory.CrafterManagerScreenFactory;
import com.refinedmods.refinedstorage.screen.factory.GridScreenFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public final class RefinedStorageFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricModelLoadingPlugin.register();
        RS.CLIENT_CONFIG.getSpec().load(FabricLoader.getInstance().getConfigDir().resolve("refinedstorage-client.json"));
        registerScreens();
        registerKeyBindings();
        registerItemProperties();
        registerPatternRenderHandlers();
        ColorProviderRegistry.ITEM.register(new PatternItemColor(), RSItems.PATTERN.get());
        ClientTickEvents.END_CLIENT_TICK.register(KeyInputListener::onClientTick);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourcePackListener());
        BlockEntityRenderers.register(RSBlockEntities.STORAGE_MONITOR.get(), context -> new StorageMonitorBlockEntityRenderer());
    }

    private static void registerScreens() {
        MenuScreens.register(RSContainerMenus.FILTER.get(), FilterScreen::new);
        MenuScreens.register(RSContainerMenus.CONTROLLER.get(), ControllerScreen::new);
        MenuScreens.register(RSContainerMenus.DISK_DRIVE.get(), DiskDriveScreen::new);
        MenuScreens.register(RSContainerMenus.GRID.get(), new GridScreenFactory());
        MenuScreens.register(RSContainerMenus.STORAGE_BLOCK.get(), StorageBlockScreen::new);
        MenuScreens.register(RSContainerMenus.FLUID_STORAGE_BLOCK.get(), FluidStorageBlockScreen::new);
        MenuScreens.register(RSContainerMenus.EXTERNAL_STORAGE.get(), ExternalStorageScreen::new);
        MenuScreens.register(RSContainerMenus.IMPORTER.get(), ImporterScreen::new);
        MenuScreens.register(RSContainerMenus.EXPORTER.get(), ExporterScreen::new);
        MenuScreens.register(RSContainerMenus.NETWORK_TRANSMITTER.get(), NetworkTransmitterScreen::new);
        MenuScreens.register(RSContainerMenus.RELAY.get(), RelayScreen::new);
        MenuScreens.register(RSContainerMenus.DETECTOR.get(), DetectorScreen::new);
        MenuScreens.register(RSContainerMenus.SECURITY_MANAGER.get(), SecurityManagerScreen::new);
        MenuScreens.register(RSContainerMenus.INTERFACE.get(), InterfaceScreen::new);
        MenuScreens.register(RSContainerMenus.FLUID_INTERFACE.get(), FluidInterfaceScreen::new);
        MenuScreens.register(RSContainerMenus.WIRELESS_TRANSMITTER.get(), WirelessTransmitterScreen::new);
        MenuScreens.register(RSContainerMenus.STORAGE_MONITOR.get(), StorageMonitorScreen::new);
        MenuScreens.register(RSContainerMenus.CONSTRUCTOR.get(), ConstructorScreen::new);
        MenuScreens.register(RSContainerMenus.DESTRUCTOR.get(), DestructorScreen::new);
        MenuScreens.register(RSContainerMenus.DISK_MANIPULATOR.get(), DiskManipulatorScreen::new);
        MenuScreens.register(RSContainerMenus.CRAFTER.get(), CrafterScreen::new);
        MenuScreens.register(RSContainerMenus.CRAFTER_MANAGER.get(), new CrafterManagerScreenFactory());
        MenuScreens.register(RSContainerMenus.CRAFTING_MONITOR.get(), CraftingMonitorScreen::new);
        MenuScreens.register(RSContainerMenus.WIRELESS_CRAFTING_MONITOR.get(), CraftingMonitorScreen::new);
    }

    private static void registerPatternRenderHandlers() {
        API.instance().addPatternRenderHandler(pattern -> Screen.hasShiftDown());
        API.instance().addPatternRenderHandler(pattern -> {
            AbstractContainerMenu menu = net.minecraft.client.Minecraft.getInstance().player.containerMenu;
            if (menu instanceof CrafterManagerContainerMenu) {
                for (Slot slot : menu.slots) {
                    if (slot instanceof CrafterManagerSlot && slot.getItem() == pattern) {
                        return true;
                    }
                }
            }
            return false;
        });
        API.instance().addPatternRenderHandler(pattern -> {
            AbstractContainerMenu menu = net.minecraft.client.Minecraft.getInstance().player.containerMenu;
            if (menu instanceof CrafterContainerMenu) {
                for (int slot = 0; slot < 9; ++slot) {
                    if (menu.getSlot(slot).getItem() == pattern) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private static void registerKeyBindings() {
        KeyBindingHelper.registerKeyBinding(RSKeyBindings.FOCUS_SEARCH_BAR);
        KeyBindingHelper.registerKeyBinding(RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX);
        KeyBindingHelper.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_GRID);
        KeyBindingHelper.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_FLUID_GRID);
        KeyBindingHelper.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR);
        KeyBindingHelper.registerKeyBinding(RSKeyBindings.OPEN_PORTABLE_GRID);
    }

    private static void registerItemProperties() {
        ResourceLocation connected = new ResourceLocation(RS.ID, "connected");
        SecurityCardItemPropertyGetter security = new SecurityCardItemPropertyGetter();
        ControllerItemPropertyGetter controllerEnergy = new ControllerItemPropertyGetter();
        NetworkItemPropertyGetter network = new NetworkItemPropertyGetter();
        ItemProperties.register(RSItems.SECURITY_CARD.get(), new ResourceLocation(RS.ID, "active"), (stack, level, entity, seed) -> security.call(stack, level, entity, seed));
        RSItems.CONTROLLER.values().forEach(controller -> ItemProperties.register(controller.get(), new ResourceLocation(RS.ID, "energy_type"), (stack, level, entity, seed) -> controllerEnergy.call(stack, level, entity, seed)));
        RSItems.CREATIVE_CONTROLLER.values().forEach(controller -> ItemProperties.register(controller.get(), new ResourceLocation(RS.ID, "energy_type"), (stack, level, entity, seed) -> controllerEnergy.call(stack, level, entity, seed)));
        ItemProperties.register(RSItems.WIRELESS_CRAFTING_MONITOR.get(), connected, (stack, level, entity, seed) -> network.call(stack, level, entity, seed));
        ItemProperties.register(RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR.get(), connected, (stack, level, entity, seed) -> network.call(stack, level, entity, seed));
        ItemProperties.register(RSItems.WIRELESS_GRID.get(), connected, (stack, level, entity, seed) -> network.call(stack, level, entity, seed));
        ItemProperties.register(RSItems.CREATIVE_WIRELESS_GRID.get(), connected, (stack, level, entity, seed) -> network.call(stack, level, entity, seed));
        ItemProperties.register(RSItems.WIRELESS_FLUID_GRID.get(), connected, (stack, level, entity, seed) -> network.call(stack, level, entity, seed));
        ItemProperties.register(RSItems.CREATIVE_WIRELESS_FLUID_GRID.get(), connected, (stack, level, entity, seed) -> network.call(stack, level, entity, seed));
    }
}


