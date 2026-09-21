package com.refinedmods.refinedstorage.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.command.disk.CreateDiskCommand;
import com.refinedmods.refinedstorage.command.disk.ListDiskCommand;
import com.refinedmods.refinedstorage.command.network.GetNetworkCommand;
import com.refinedmods.refinedstorage.command.network.ListNetworkCommand;
import com.refinedmods.refinedstorage.command.pattern.PatternDumpCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

/** Registers the Fabric equivalents for the server-side Forge event hooks. */
public final class FabricEventRegistration {
    private FabricEventRegistration() {
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(FabricEventRegistration::tickWorld);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private static void tickWorld(ServerLevel level) {
        if (level.isClientSide()) {
            return;
        }

        level.getProfiler().push("network ticking");
        for (INetwork network : API.instance().getNetworkManager(level).all()) {
            network.update();
        }
        level.getProfiler().pop();

        level.getProfiler().push("network node ticking");
        for (INetworkNode node : API.instance().getNetworkNodeManager(level).all()) {
            node.update();
        }
        level.getProfiler().pop();
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(RS.ID)
            .then(Commands.literal("pattern").then(PatternDumpCommand.register()))
            .then(Commands.literal("disk").then(CreateDiskCommand.register()).then(ListDiskCommand.register()))
            .then(Commands.literal("network").then(GetNetworkCommand.register()).then(ListNetworkCommand.register())));
    }
}
