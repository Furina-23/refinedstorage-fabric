package net.minecraftforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.locale.Language;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public final class ForgeHooksClient {
    private ForgeHooksClient() { }
    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack,
            List<? extends FormattedText> text, int mouseX, int width, int height, Font font) {
        java.util.ArrayList<ClientTooltipComponent> result = new java.util.ArrayList<>();
        for (FormattedText line : text) {
            result.add(ClientTooltipComponent.create(Language.getInstance().getVisualOrder(line)));
        }
        return result;
    }
}
