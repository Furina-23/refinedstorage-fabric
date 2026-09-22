package com.refinedmods.refinedstorage.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class RSFakePlayer extends ServerPlayer {
    public RSFakePlayer(ServerLevel level, GameProfile profile) {
        super(level.getServer(), level, profile);
    }
}


