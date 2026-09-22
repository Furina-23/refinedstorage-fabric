package com.refinedmods.refinedstorage.util;

import com.mojang.authlib.GameProfile;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.refinedmods.refinedstorage.transfer.fluid.IFluidHandler;
import com.refinedmods.refinedstorage.transfer.fluid.FabricFluidHandler;
import com.refinedmods.refinedstorage.transfer.item.IItemHandler;
import com.refinedmods.refinedstorage.transfer.item.FabricItemHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import com.refinedmods.refinedstorage.transfer.item.wrapper.InvWrapper;
import com.refinedmods.refinedstorage.transfer.item.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public final class LevelUtils {
    private static final GameProfile AUTOMATION_PROFILE = new GameProfile(
        UUID.fromString("41c82c87-7afb-4024-ba57-13d2c99cae77"), "[RefinedStorage]"
    );

    private LevelUtils() {
    }

    public static void updateBlock(@Nullable Level level, BlockPos pos) {
        if (level != null && level.isLoaded(pos)) {
            BlockState state = level.getBlockState(pos);

            level.sendBlockUpdated(pos, state, state, 1 | 2);
        }
    }

    public static IItemHandler getItemHandler(@Nullable BlockEntity blockEntity, Direction side) {
        if (blockEntity == null) {
            return null;
        }

        if (blockEntity.getLevel() != null) {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), side);
            if (storage != null) {
                return new FabricItemHandler(storage);
            }
        }

        IItemHandler handler = null;
        if (handler == null) {
            if (side != null && blockEntity instanceof WorldlyContainer) {
                handler = new SidedInvWrapper((WorldlyContainer) blockEntity, side);
            } else if (blockEntity instanceof Container) {
                handler = new InvWrapper((Container) blockEntity);
            }
        }

        return handler;
    }

    public static IFluidHandler getFluidHandler(@Nullable BlockEntity blockEntity, Direction side) {
        if (blockEntity != null && blockEntity.getLevel() != null) {
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), side);
            return storage == null ? null : new FabricFluidHandler(storage);
        }

        return null;
    }

    public static RSFakePlayer getFakePlayer(ServerLevel level, @Nullable UUID owner) {
        if (owner != null) {
            GameProfileCache profileCache = level.getServer().getProfileCache();

            Optional<GameProfile> profile = profileCache.get(owner);

            if (profile.isPresent()) {
                return new RSFakePlayer(level, profile.get());
            }
        }

        return new RSFakePlayer(level, AUTOMATION_PROFILE);
    }

    public static void sendNoPermissionMessage(Player player) {
        player.sendSystemMessage(Component.translatable("misc.refinedstorage.security.no_permission").setStyle(Styles.RED));
    }

    public static HitResult rayTracePlayer(Level level, Player player) {
        double reachDistance = 5.0D;

        Vec3 base = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        Vec3 target = base.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance);

        return level.clip(new ClipContext(base, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }
}


