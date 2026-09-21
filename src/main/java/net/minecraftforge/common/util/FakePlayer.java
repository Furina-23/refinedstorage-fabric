package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FakePlayer extends ServerPlayer {
    public FakePlayer(MinecraftServer server, ServerLevel level, GameProfile profile) {
        super(server, level, profile);
    }
}
