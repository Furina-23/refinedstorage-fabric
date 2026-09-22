package com.refinedmods.refinedstorage.registry;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Inventory;

import com.refinedmods.refinedstorage.container.factory.ContainerFactory;

/** Creates menu types while retaining the menu factory's network buffer contract. */
public final class MenuTypeFactory {
    private MenuTypeFactory() {
    }

    public static <T extends AbstractContainerMenu> MenuType<T> create(ContainerFactory<T> factory) {
        return new MenuType<>((windowId, inventory) -> factory.create(windowId, inventory, new FriendlyByteBuf(Unpooled.buffer())), FeatureFlags.VANILLA_SET);
    }
}


