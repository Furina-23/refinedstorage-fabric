package net.minecraftforge.network;

import net.minecraft.server.level.ServerPlayer;
import java.util.function.Supplier;

public final class PacketDistributor {
    public static final Target PLAYER = new Target();
    public static final Target SERVER = new Target();
    private PacketDistributor() { }
    public static final class Target {
        public PacketTarget with(Supplier<ServerPlayer> player) { return new PacketTarget(player.get(), false); }
        public PacketTarget noArg() { return new PacketTarget(null, true); }
    }
    public static final class PacketTarget {
        private final ServerPlayer player;
        private final boolean server;
        private PacketTarget(ServerPlayer player, boolean server) {
            this.player = player;
            this.server = server;
        }
        public ServerPlayer player() { return player; }
        public boolean server() { return server; }
    }
}
