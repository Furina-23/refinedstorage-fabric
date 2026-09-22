package com.refinedmods.refinedstorage.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.command.disk.CreateDiskCommand;
import com.refinedmods.refinedstorage.command.disk.ListDiskCommand;
import com.refinedmods.refinedstorage.command.network.GetNetworkCommand;
import com.refinedmods.refinedstorage.command.network.ListNetworkCommand;
import com.refinedmods.refinedstorage.command.pattern.PatternDumpCommand;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.PlayerUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;

/** Registers server-side lifecycle and gameplay callbacks. */
public final class FabricEventRegistration {
    private FabricEventRegistration() {
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(FabricEventRegistration::tickWorld);
        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> chunk.getBlockEntities().values().forEach(blockEntity -> {
            if (blockEntity instanceof BaseBlockEntity baseBlockEntity) {
                baseBlockEntity.onChunkUnloaded();
            }
        }));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
        UseBlockCallback.EVENT.register(FabricEventRegistration::beforeBlockUse);
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> canBreak(level, player, pos));
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

    private static InteractionResult beforeBlockUse(net.minecraft.world.entity.player.Player player,
                                                     net.minecraft.world.level.Level level,
                                                     net.minecraft.world.InteractionHand hand,
                                                     net.minecraft.world.phys.BlockHitResult hitResult) {
        if (level.isClientSide || !(player.getItemInHand(hand).getItem() instanceof BlockItem blockItem)) {
            return InteractionResult.PASS;
        }
        if (!(blockItem.getBlock() instanceof NetworkNodeBlock) && !(blockItem.getBlock() instanceof ControllerBlock)) {
            return InteractionResult.PASS;
        }

        BlockPos placementPos = new BlockPlaceContext(player, hand, player.getItemInHand(hand), hitResult).getClickedPos();
        for (Direction direction : Direction.values()) {
            INetworkNode neighbor = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(placementPos.relative(direction)));
            if (neighbor != null && neighbor.getNetwork() != null
                && !neighbor.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
                LevelUtils.sendNoPermissionMessage(player);
                PlayerUtils.updateHeldItems((ServerPlayer) player);
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    private static boolean canBreak(net.minecraft.world.level.Level level,
                                    net.minecraft.world.entity.player.Player player,
                                    BlockPos pos) {
        if (level.isClientSide) {
            return true;
        }
        INetworkNode node = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(pos));
        if (node != null && node.getNetwork() != null
            && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
            LevelUtils.sendNoPermissionMessage(player);
            return false;
        }
        return true;
    }
}


