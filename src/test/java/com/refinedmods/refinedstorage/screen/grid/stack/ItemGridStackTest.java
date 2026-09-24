package com.refinedmods.refinedstorage.screen.grid.stack;

import com.refinedmods.refinedstorage.MinecraftTestBootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemGridStackTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        MinecraftTestBootstrap.boot();
    }

    @Test
    void zeroedStackReturnsToNormalWhenQuantityBecomesPositive() {
        ItemGridStack stack = stack(Items.DIRT, 64);

        stack.setQuantity(0);
        assertTrue(stack.isZeroed());
        assertEquals(0, stack.getQuantity());
        assertEquals("0", stack.getFormattedFullQuantity());

        stack.setQuantity(27);
        assertFalse(stack.isZeroed());
        assertEquals(27, stack.getQuantity());
    }

    @Test
    void sameTypeIncludesItemAndNbtButIgnoresQuantityAndId() {
        ItemGridStack original = stack(Items.DIRT, 64);
        ItemGridStack replacement = stack(Items.DIRT, 1);

        assertTrue(original.isSameType(replacement));
        assertFalse(original.isSameType(stack(Items.STONE, 64)));
    }

    private static ItemGridStack stack(net.minecraft.world.item.Item item, int count) {
        return new ItemGridStack(UUID.randomUUID(), null, new ItemStack(item, count), false, null);
    }
}
