package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.network.ClientProxy;
import com.refinedmods.refinedstorage.network.fabric.NetworkEvent;

import java.util.function.Supplier;

public class GridCraftingStartResponseMessage {
    public static void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientProxy.onReceivedCraftingStartResponseMessage());
        ctx.get().setPacketHandled(true);
    }
}


