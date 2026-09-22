package com.refinedmods.refinedstorage.network.fabric;

import net.minecraft.resources.ResourceLocation;
import com.refinedmods.refinedstorage.network.fabric.simple.SimpleChannel;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class NetworkRegistry {
    private NetworkRegistry() { }
    public static final class ChannelBuilder {
        private ResourceLocation id;
        public static ChannelBuilder named(ResourceLocation id) {
            ChannelBuilder builder = new ChannelBuilder();
            builder.id = id;
            return builder;
        }
        public ChannelBuilder clientAcceptedVersions(Predicate<String> versions) { return this; }
        public ChannelBuilder serverAcceptedVersions(Predicate<String> versions) { return this; }
        public ChannelBuilder networkProtocolVersion(Supplier<String> version) { return this; }
        public SimpleChannel simpleChannel() { return new SimpleChannel(id); }
    }
}



