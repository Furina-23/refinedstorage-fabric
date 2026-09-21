package net.minecraftforge.client;

import net.minecraft.client.renderer.RenderType;
import java.util.Collection;

public final class ChunkRenderTypeSet {
    private final Collection<RenderType> types;
    private ChunkRenderTypeSet(Collection<RenderType> types) { this.types = types; }
    public static ChunkRenderTypeSet of(RenderType type) { return new ChunkRenderTypeSet(java.util.List.of(type)); }
    public static ChunkRenderTypeSet none() { return new ChunkRenderTypeSet(java.util.List.of()); }
}
