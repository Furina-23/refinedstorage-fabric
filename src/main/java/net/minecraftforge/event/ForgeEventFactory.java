package net.minecraftforge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public final class ForgeEventFactory {
    private ForgeEventFactory() {}
    public static void firePlayerCraftingEvent(Player player, ItemStack stack, CraftingContainer container) { }
}
