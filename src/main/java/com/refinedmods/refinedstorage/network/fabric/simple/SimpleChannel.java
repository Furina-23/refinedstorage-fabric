package com.refinedmods.refinedstorage.network.fabric.simple;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.refinedmods.refinedstorage.network.fabric.NetworkEvent;
import com.refinedmods.refinedstorage.network.fabric.PacketDistributor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SimpleChannel {
    private final ResourceLocation id;
    private final Map<Integer, Registration<?>> registrationsById = new HashMap<>();
    private final Map<Class<?>, Registration<?>> registrationsByType = new HashMap<>();
    private boolean receiversRegistered;

    public SimpleChannel(ResourceLocation id) {
        this.id = id;
    }

    public <MSG> void registerMessage(int id, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                      Function<FriendlyByteBuf, MSG> decoder,
                                      BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        Registration<MSG> registration = new Registration<>(id, encoder, decoder, consumer);
        registrationsById.put(id, registration);
        registrationsByType.put(type, registration);
        registerReceivers();
    }

    public void send(Object target, Object message) {
        Registration<Object> registration = getRegistration(message.getClass());
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeVarInt(registration.id());
        registration.encoder().accept(message, buffer);

        PacketDistributor.PacketTarget packetTarget = (PacketDistributor.PacketTarget) target;
        if (packetTarget.server()) {
            FabricClientNetworking.send(id, buffer);
        } else {
            ServerPlayNetworking.send(packetTarget.player(), id, buffer);
        }
    }

    private void registerReceivers() {
        if (receiversRegistered) {
            return;
        }
        receiversRegistered = true;
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buffer, responseSender) -> {
            Registration<Object> registration = getRegistration(buffer.readVarInt());
            Object message = registration.decoder().apply(buffer);
            NetworkEvent.Context context = new NetworkEvent.Context(player, server::execute);
            registration.consumer().accept(message, () -> context);
        });
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricClientNetworking.register(id, this);
        }
    }

    void receiveClient(FriendlyByteBuf buffer, java.util.function.Consumer<Runnable> scheduler) {
        Registration<Object> registration = getRegistration(buffer.readVarInt());
        Object message = registration.decoder().apply(buffer);
        NetworkEvent.Context context = new NetworkEvent.Context(null, scheduler);
        registration.consumer().accept(message, () -> context);
    }

    @SuppressWarnings("unchecked")
    private Registration<Object> getRegistration(Class<?> type) {
        Registration<?> registration = registrationsByType.get(type);
        if (registration == null) {
            throw new IllegalArgumentException("Unregistered packet type " + type.getName());
        }
        return (Registration<Object>) registration;
    }

    @SuppressWarnings("unchecked")
    private Registration<Object> getRegistration(int packetId) {
        Registration<?> registration = registrationsById.get(packetId);
        if (registration == null) {
            throw new IllegalArgumentException("Unregistered packet id " + packetId);
        }
        return (Registration<Object>) registration;
    }

    private record Registration<MSG>(int id, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                     Function<FriendlyByteBuf, MSG> decoder,
                                     BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
    }
}



