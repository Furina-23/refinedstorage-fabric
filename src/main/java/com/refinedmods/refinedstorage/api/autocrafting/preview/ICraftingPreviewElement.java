package com.refinedmods.refinedstorage.api.autocrafting.preview;

import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a crafting preview element.
 */
public interface ICraftingPreviewElement {
    /**
     * @param graphics the graphics
     * @param x        position on the x axis to render
     * @param y        position on the y axis to render
     * @param drawers  the drawers this element can use
     */
    void draw(GuiGraphics graphics, int x, int y, IElementDrawers drawers);

    /**
     * @return true when this crafting preview elements signifies an error that disables starting a task
     */
    boolean doesDisableTaskStarting();

    /**
     * @param buf buffer to write to
     */
    void write(FriendlyByteBuf buf);

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    ResourceLocation getId();
}


