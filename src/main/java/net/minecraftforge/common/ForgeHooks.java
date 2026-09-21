package net.minecraftforge.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;

public final class ForgeHooks {
    private static Player craftingPlayer;
    private ForgeHooks() {}
    public static void setCraftingPlayer(Player player) { craftingPlayer = player; }
    public static Player getCraftingPlayer() { return craftingPlayer; }
    public static InteractionResult onPlaceItemIntoWorld(BlockPlaceContext context) { return InteractionResult.PASS; }
}
