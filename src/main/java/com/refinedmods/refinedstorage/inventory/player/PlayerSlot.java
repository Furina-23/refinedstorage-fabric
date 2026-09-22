package com.refinedmods.refinedstorage.inventory.player;

import com.refinedmods.refinedstorage.util.PacketBufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerSlot {
    int slot;
    String curioSlot;

    public PlayerSlot(int slot, String curioSlot) {
        this.slot = slot;
        this.curioSlot = curioSlot;
    }

    public PlayerSlot(int slot) {
        this.slot = slot;
    }

    public PlayerSlot(FriendlyByteBuf buffer) {
        slot = buffer.readInt();

        if (buffer.readBoolean()) {
            curioSlot = PacketBufferUtils.readString(buffer);
        }
    }

    public static PlayerSlot getSlotForHand(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return new PlayerSlot(player.getInventory().selected);
        }
        return new PlayerSlot(Inventory.SLOT_OFFHAND);
    }

    public ItemStack getStackFromSlot(Player player) {
        // Accessory slots remain serialized for protocol compatibility, but no
        // accessory API is part of the current Fabric baseline.
        return curioSlot == null ? player.getInventory().getItem(slot) : ItemStack.EMPTY;
    }

    public void writePlayerSlot(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeBoolean(curioSlot != null);
        if (curioSlot != null) {
            buffer.writeUtf(curioSlot);
        }
    }

    public int getSlotIdInPlayerInventory() {
        if (curioSlot != null) {
            return -1;
        }
        return slot;
    }
}


