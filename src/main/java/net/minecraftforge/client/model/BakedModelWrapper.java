package net.minecraftforge.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.ChunkRenderTypeSet;
import java.util.List;

public class BakedModelWrapper<T extends BakedModel> implements BakedModel {
    protected final T originalModel;
    public BakedModelWrapper(T originalModel) { this.originalModel = originalModel; }
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return originalModel.getQuads(state, side, random); }
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType type) { return getQuads(state, side, random); }
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.none(); }
    public boolean useAmbientOcclusion() { return originalModel.useAmbientOcclusion(); }
    public boolean isGui3d() { return originalModel.isGui3d(); }
    public boolean usesBlockLight() { return originalModel.usesBlockLight(); }
    public boolean isCustomRenderer() { return originalModel.isCustomRenderer(); }
    public TextureAtlasSprite getParticleIcon() { return originalModel.getParticleIcon(); }
    public ItemOverrides getOverrides() { return originalModel.getOverrides(); }
    public ItemTransforms getTransforms() { return originalModel.getTransforms(); }
}
