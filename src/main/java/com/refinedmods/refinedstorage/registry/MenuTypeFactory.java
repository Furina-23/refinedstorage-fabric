package com.refinedmods.refinedstorage.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import com.refinedmods.refinedstorage.container.factory.ContainerFactory;

/** Creates menu types while retaining the menu factory's network buffer contract. */
public final class MenuTypeFactory {
    private MenuTypeFactory() {
    }

    public static <T extends AbstractContainerMenu> MenuType<T> create(ContainerFactory<T> factory) {
        return new ExtendedScreenHandlerType<>(factory::create);
    }
}
