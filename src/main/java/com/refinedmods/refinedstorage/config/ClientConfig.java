package com.refinedmods.refinedstorage.config;

import com.refinedmods.refinedstorage.config.ConfigSpec;

public class ClientConfig {
    private final ConfigSpec.Builder builder = new ConfigSpec.Builder();
    private final ConfigSpec spec;

    private final Grid grid;
    private final CrafterManager crafterManager;
    private final Cover cover;

    public ClientConfig() {
        grid = new Grid();
        crafterManager = new CrafterManager();
        cover = new Cover();
        spec = builder.build();
    }

    public ConfigSpec getSpec() {
        return spec;
    }

    public Grid getGrid() {
        return grid;
    }

    public CrafterManager getCrafterManager() {
        return crafterManager;
    }

    public Cover getCover() {
        return cover;
    }

    public class Grid {
        private final ConfigSpec.IntValue maxRowsStretch;
        private final ConfigSpec.BooleanValue detailedTooltip;
        private final ConfigSpec.BooleanValue largeFont;
        private final ConfigSpec.BooleanValue preventSortingWhileShiftIsDown;
        private final ConfigSpec.BooleanValue rememberSearchQuery;

        public Grid() {
            builder.push("grid");

            maxRowsStretch = builder.comment("The maximum amount of rows that the Grid can show when stretched").defineInRange("maxRowsStretch", Integer.MAX_VALUE, 3, Integer.MAX_VALUE);
            detailedTooltip = builder.comment("Whether the Grid should display a detailed tooltip when hovering over an item or fluid").define("detailedTooltip", true);
            largeFont = builder.comment("Whether the Grid should use a large font for stack quantity display").define("largeFont", false);
            preventSortingWhileShiftIsDown = builder.comment("Whether the Grid should prevent sorting while the shift key is held down").define("preventSortingWhileShiftIsDown", true);
            rememberSearchQuery = builder.comment("Whether the Grid should remember the search query when closing and re-opening the Grid").define("rememberSearchQuery", false);

            builder.pop();
        }

        public int getMaxRowsStretch() {
            return maxRowsStretch.get();
        }

        public boolean getDetailedTooltip() {
            return detailedTooltip.get();
        }

        public boolean getLargeFont() {
            return largeFont.get();
        }

        public boolean getPreventSortingWhileShiftIsDown() {
            return preventSortingWhileShiftIsDown.get();
        }

        public boolean getRememberSearchQuery() {
            return rememberSearchQuery.get();
        }
    }

    public class CrafterManager {
        private final ConfigSpec.IntValue maxRowsStretch;

        public CrafterManager() {
            builder.push("crafterManager");

            maxRowsStretch = builder.comment("The maximum amount of rows that the Crafter Manager can show when stretched").defineInRange("maxRowsStretch", Integer.MAX_VALUE, 3, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getMaxRowsStretch() {
            return maxRowsStretch.get();
        }
    }

    public class Cover {

        private final ConfigSpec.BooleanValue showAllCoversInJEI;

        public Cover() {
            builder.push("cover");
            showAllCoversInJEI = builder.comment("When true all the possible covers will be added to JEI (Game restart required)").define("showAllCoversInJEI", false);
            builder.pop();
        }

        public boolean showAllRecipesInJEI() {
            return showAllCoversInJEI.get();
        }
    }
}
