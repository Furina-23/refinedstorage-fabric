package com.refinedmods.refinedstorage.network.fabric;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class NetworkHooks {
    private NetworkHooks() {
    }

    public static void openScreen(ServerPlayer player, MenuProvider provider) {
        openScreen(player, provider, buffer -> {
        });
    }

    public static void openScreen(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> writer) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer serverPlayer, FriendlyByteBuf buffer) {
                writer.accept(buffer);
            }

            @Override
            public Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player menuPlayer) {
                return provider.createMenu(windowId, inventory, menuPlayer);
            }
        });
    }

    public static void openScreen(ServerPlayer player, MenuProvider provider, BlockPos pos) {
        openScreen(player, provider, buffer -> buffer.writeBlockPos(pos));
    }
}
