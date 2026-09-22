package com.refinedmods.refinedstorage.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import com.refinedmods.refinedstorage.network.fabric.NetworkEvent;
import com.refinedmods.refinedstorage.network.fabric.PacketDistributor;
import com.refinedmods.refinedstorage.network.fabric.simple.SimpleChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** Keeps packet registration stable while the Fabric networking backend is migrated. */
public final class PacketSplitter {
    private final SimpleChannel channel;
    private final Set<Class<?>> splitMessages = new HashSet<>();

    public PacketSplitter(int maxNumberOfMessages, SimpleChannel channel, ResourceLocation channelId) {
        this.channel = channel;
    }

    public boolean shouldMessageBeSplit(Class<?> type) { return splitMessages.contains(type); }

    public void sendToPlayer(ServerPlayer player, Object message) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public void sendToServer(Object message) {
        channel.send(PacketDistributor.SERVER.noArg(), message);
    }

    public <MSG> void registerMessage(int index, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                      Function<FriendlyByteBuf, MSG> decoder,
                                      BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        splitMessages.add(type);
        channel.registerMessage(index, type, encoder, decoder, consumer);
    }

    public void addPackagePart(int communicationId, int packetIndex, byte[] payload) { }
}


