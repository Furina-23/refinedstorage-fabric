package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import java.util.UUID;

public final class FakePlayerFactory {
    private static final GameProfile MINECRAFT = new GameProfile(UUID.fromString("41c82c87-7afb-4024-ba57-13d2c99cae77"), "[Minecraft]");
    private FakePlayerFactory() { }
    public static FakePlayer get(ServerLevel level, GameProfile profile) { return new FakePlayer(level.getServer(), level, profile); }
    public static FakePlayer getMinecraft(ServerLevel level) { return get(level, MINECRAFT); }
}
