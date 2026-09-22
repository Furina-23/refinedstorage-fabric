package com.refinedmods.refinedstorage.network.fabric;

import net.minecraft.server.level.ServerPlayer;
import java.util.function.Consumer;

public final class NetworkEvent {
    private NetworkEvent() { }

    public static class Context {
        private final ServerPlayer sender;
        private final Consumer<Runnable> scheduler;
        public Context(ServerPlayer sender, Consumer<Runnable> scheduler) {
            this.sender = sender;
            this.scheduler = scheduler;
        }
        public ServerPlayer getSender() { return sender; }
        public void enqueueWork(Runnable work) { scheduler.accept(work); }
        public void setPacketHandled(boolean handled) { }
    }
}



