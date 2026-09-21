package net.minecraftforge.client;

import net.minecraft.client.renderer.RenderType;

public final class RenderTypeGroup {
    private final RenderType block;
    private final RenderType entity;
    public RenderTypeGroup(RenderType block, RenderType entity) { this.block = block; this.entity = entity; }
    public RenderType block() { return block; }
    public RenderType entity() { return entity; }
}
