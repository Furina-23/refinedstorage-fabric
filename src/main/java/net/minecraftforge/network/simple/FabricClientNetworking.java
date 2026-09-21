package net.minecraftforge.network.simple;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

final class FabricClientNetworking {
    private FabricClientNetworking() {
    }

    static void register(ResourceLocation id, SimpleChannel channel) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buffer, responseSender) -> channel.receiveClient(buffer, client::execute));
    }

    static void send(ResourceLocation id, FriendlyByteBuf buffer) {
        ClientPlayNetworking.send(id, buffer);
    }
}
