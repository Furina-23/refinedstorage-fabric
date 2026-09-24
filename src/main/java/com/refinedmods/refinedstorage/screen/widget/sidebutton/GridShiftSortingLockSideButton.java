package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class GridShiftSortingLockSideButton extends SideButton {
    private final GridScreen gridScreen;

    public GridShiftSortingLockSideButton(GridScreen screen) {
        super(screen);
        this.gridScreen = screen;
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        boolean enabled = RS.CLIENT_CONFIG.getGrid().getPreventSortingWhileShiftIsDown();
        int color = enabled ? 0xFFE0E0E0 : 0xFF777777;

        graphics.fill(x + 4, y + 7, x + 12, y + 14, color);
        graphics.fill(x + 5, y + 3, x + 7, y + 8, color);
        graphics.fill(x + 6, y + 2, x + 11, y + 4, color);
        graphics.fill(x + 10, y + (enabled ? 3 : 1), x + 12, y + 8, color);

        int indicator = enabled ? 0xFF55FF55 : 0xFFFF5555;
        graphics.fill(x + 10, y + 12, x + 13, y + 15, indicator);
    }

    @Override
    protected String getSideButtonTooltip() {
        boolean enabled = RS.CLIENT_CONFIG.getGrid().getPreventSortingWhileShiftIsDown();
        return I18n.get("sidebutton.refinedstorage.grid.shift_sorting_lock") + "\n" + ChatFormatting.GRAY
            + I18n.get("sidebutton.refinedstorage.grid.shift_sorting_lock." + (enabled ? "on" : "off"));
    }

    @Override
    public void onPress() {
        boolean enabled = !RS.CLIENT_CONFIG.getGrid().getPreventSortingWhileShiftIsDown();
        gridScreen.setShiftSortingLockEnabled(enabled);
    }
}
