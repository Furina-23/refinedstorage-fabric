package com.refinedmods.refinedstorage.network.fabric;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;

public final class NetworkHooks {
    private NetworkHooks() {}
    public static void openScreen(ServerPlayer player, MenuProvider provider) { }
    public static void openScreen(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> writer) {
        if (writer != null) writer.accept(new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer()));
    }
    public static void openScreen(ServerPlayer player, MenuProvider provider, BlockPos pos) {
        openScreen(player, provider, buffer -> buffer.writeBlockPos(pos));
    }
}



