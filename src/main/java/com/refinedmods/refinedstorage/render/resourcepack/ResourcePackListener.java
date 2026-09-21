package com.refinedmods.refinedstorage.render.resourcepack;

import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.RS;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;

public class ResourcePackListener extends SimplePreparableReloadListener<RSResourcePackSection> implements IdentifiableResourceReloadListener {
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    public net.minecraft.resources.ResourceLocation getFabricId() {
        return new net.minecraft.resources.ResourceLocation(RS.ID, "render_settings");
    }

    @Override
    protected RSResourcePackSection prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Pack info : Minecraft.getInstance().getResourcePackRepository().getSelectedPacks()) {
            try {
                RSResourcePackSection section = info.open().getMetadataSection(RSResourcePackSection.DESERIALIZER);

                if (section != null) {
                    return section;
                }
            } catch (IOException e) {
                logger.error("Could not read Refined Storage resource pack section", e);
            }
        }

        return null;
    }

    @Override
    protected void apply(@Nullable RSResourcePackSection section, ResourceManager resourceManager, ProfilerFiller profiler) {
        if (section != null) {
            RenderSettings.INSTANCE.setColors(section.getPrimaryColor(), section.getSecondaryColor());
        } else {
            RenderSettings.INSTANCE.setColors(-1, -1);
        }
    }
}
