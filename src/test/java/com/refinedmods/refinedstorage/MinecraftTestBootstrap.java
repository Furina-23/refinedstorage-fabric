package com.refinedmods.refinedstorage;

import net.minecraft.server.Bootstrap;
import net.minecraft.SharedConstants;

public final class MinecraftTestBootstrap {
    private static boolean bootstrapped;

    private MinecraftTestBootstrap() {
    }

    public static synchronized void boot() {
        if (!bootstrapped) {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            bootstrapped = true;
        }
    }
}
