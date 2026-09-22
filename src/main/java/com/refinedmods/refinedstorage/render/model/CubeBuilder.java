package com.refinedmods.refinedstorage.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CubeBuilder {
    private Vector3f from;
    private Vector3f to;
    private final Map<Direction, Face> faces = new HashMap<>();
    private int color = 0xFFFFFFFF;

    public CubeBuilder from(float x, float y, float z) {
        this.from = new Vector3f(x / 16, y / 16, z / 16);

        return this;
    }

    public CubeBuilder to(float x, float y, float z) {
        this.to = new Vector3f(x / 16, y / 16, z / 16);

        return this;
    }

    public CubeBuilder color(int color) {
        this.color = color;

        return this;
    }

    public CubeBuilder addFaces(Function<Direction, Face> faceSupplier) {
        for (Direction facing : Direction.values()) {
            addFace(faceSupplier.apply(facing));
        }

        return this;
    }

    public CubeBuilder addFace(Face face) {
        faces.put(face.face, face);

        return this;
    }

    public List<BakedQuad> bake() {
        List<BakedQuad> quads = new ArrayList<>();

        for (Map.Entry<Direction, Face> entry : faces.entrySet()) {
            quads.add(bakeFace(entry.getKey(), entry.getValue()));
        }

        return quads;
    }

    private BakedQuad bakeFace(Direction facing, Face cubeFace) {
        int[] vertices = new int[32];
        int[] vertexIndex = {0};

        Uv uv = getDefaultUv(facing, cubeFace.sprite, from.x(), from.y(), from.z(), to.x(), to.y(), to.z());

        switch (facing) {
            case DOWN:
                addVertexTopRight(vertices, vertexIndex, cubeFace, to.x(), from.y(), from.z(), uv);
                addVertexBottomRight(vertices, vertexIndex, cubeFace, to.x(), from.y(), to.z(), uv);
                addVertexBottomLeft(vertices, vertexIndex, cubeFace, from.x(), from.y(), to.z(), uv);
                addVertexTopLeft(vertices, vertexIndex, cubeFace, from.x(), from.y(), from.z(), uv);
                break;
            case UP:
                addVertexTopLeft(vertices, vertexIndex, cubeFace, from.x(), to.y(), from.z(), uv);
                addVertexBottomLeft(vertices, vertexIndex, cubeFace, from.x(), to.y(), to.z(), uv);
                addVertexBottomRight(vertices, vertexIndex, cubeFace, to.x(), to.y(), to.z(), uv);
                addVertexTopRight(vertices, vertexIndex, cubeFace, to.x(), to.y(), from.z(), uv);
                break;
            case NORTH:
                addVertexBottomRight(vertices, vertexIndex, cubeFace, to.x(), to.y(), from.z(), uv);
                addVertexTopRight(vertices, vertexIndex, cubeFace, to.x(), from.y(), from.z(), uv);
                addVertexTopLeft(vertices, vertexIndex, cubeFace, from.x(), from.y(), from.z(), uv);
                addVertexBottomLeft(vertices, vertexIndex, cubeFace, from.x(), to.y(), from.z(), uv);
                break;
            case SOUTH:
                addVertexBottomLeft(vertices, vertexIndex, cubeFace, from.x(), to.y(), to.z(), uv);
                addVertexTopLeft(vertices, vertexIndex, cubeFace, from.x(), from.y(), to.z(), uv);
                addVertexTopRight(vertices, vertexIndex, cubeFace, to.x(), from.y(), to.z(), uv);
                addVertexBottomRight(vertices, vertexIndex, cubeFace, to.x(), to.y(), to.z(), uv);
                break;
            case WEST:
                addVertexTopLeft(vertices, vertexIndex, cubeFace, from.x(), from.y(), from.z(), uv);
                addVertexTopRight(vertices, vertexIndex, cubeFace, from.x(), from.y(), to.z(), uv);
                addVertexBottomRight(vertices, vertexIndex, cubeFace, from.x(), to.y(), to.z(), uv);
                addVertexBottomLeft(vertices, vertexIndex, cubeFace, from.x(), to.y(), from.z(), uv);
                break;
            case EAST:
                addVertexBottomRight(vertices, vertexIndex, cubeFace, to.x(), to.y(), from.z(), uv);
                addVertexBottomLeft(vertices, vertexIndex, cubeFace, to.x(), to.y(), to.z(), uv);
                addVertexTopLeft(vertices, vertexIndex, cubeFace, to.x(), from.y(), to.z(), uv);
                addVertexTopRight(vertices, vertexIndex, cubeFace, to.x(), from.y(), from.z(), uv);
                break;
        }

        return new BakedQuad(vertices, -1, facing, cubeFace.sprite, true);
    }

    private Uv getDefaultUv(Direction face, TextureAtlasSprite texture, float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        Uv uv = new Uv();

        switch (face) {
            case DOWN:
                uv.xFrom = texture.getU(fromX * 16);
                uv.yFrom = texture.getV(16 - fromZ * 16);
                uv.xTo = texture.getU(toX * 16);
                uv.yTo = texture.getV(16 - toZ * 16);
                break;
            case UP:
                uv.xFrom = texture.getU(fromX * 16);
                uv.yFrom = texture.getV(fromZ * 16);
                uv.xTo = texture.getU(toX * 16);
                uv.yTo = texture.getV(toZ * 16);
                break;
            case NORTH:
                uv.xFrom = texture.getU(16 - fromX * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(16 - toX * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
            case SOUTH:
                uv.xFrom = texture.getU(fromX * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(toX * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
            case WEST:
                uv.xFrom = texture.getU(fromZ * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(toZ * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
            case EAST:
                uv.xFrom = texture.getU(16 - toZ * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(16 - fromZ * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
        }

        return uv;
    }

    private void addVertexTopLeft(int[] vertices, int[] index, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
            case CLOCKWISE_90:
                u = uv.xFrom;
                v = uv.yTo;
                break;
            case CLOCKWISE_180:
                u = uv.xTo;
                v = uv.yTo;
                break;
            case CLOCKWISE_270:
                u = uv.xTo;
                v = uv.yFrom;
                break;
        }

        addVertex(vertices, index, face, x, y, z, u, v);
    }

    private void addVertexTopRight(int[] vertices, int[] index, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xTo;
                v = uv.yFrom;
                break;
            case CLOCKWISE_90:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
            case CLOCKWISE_180:
                u = uv.xFrom;
                v = uv.yTo;
                break;
            case CLOCKWISE_270:
                u = uv.xTo;
                v = uv.yTo;
                break;
        }

        addVertex(vertices, index, face, x, y, z, u, v);
    }

    private void addVertexBottomRight(int[] vertices, int[] index, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xTo;
                v = uv.yTo;
                break;
            case CLOCKWISE_90:
                u = uv.xTo;
                v = uv.yFrom;
                break;
            case CLOCKWISE_180:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
            case CLOCKWISE_270:
                u = uv.xFrom;
                v = uv.yTo;
                break;
        }

        addVertex(vertices, index, face, x, y, z, u, v);
    }

    private void addVertexBottomLeft(int[] vertices, int[] index, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xFrom;
                v = uv.yTo;
                break;
            case CLOCKWISE_90:
                u = uv.xTo;
                v = uv.yTo;
                break;
            case CLOCKWISE_180:
                u = uv.xTo;
                v = uv.yFrom;
                break;
            case CLOCKWISE_270:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
        }

        addVertex(vertices, index, face, x, y, z, u, v);
    }

    private void addVertex(int[] vertices, int[] index, Face face, float x, float y, float z, float u, float v) {
        int offset = index[0]++ * 8;
        vertices[offset] = Float.floatToRawIntBits(x);
        vertices[offset + 1] = Float.floatToRawIntBits(y);
        vertices[offset + 2] = Float.floatToRawIntBits(z);
        vertices[offset + 3] = color;
        vertices[offset + 4] = Float.floatToRawIntBits(u);
        vertices[offset + 5] = Float.floatToRawIntBits(v);
        vertices[offset + 6] = 0;
        vertices[offset + 7] = packNormal(face.face);
    }

    private int packNormal(Direction direction) {
        return (direction.getStepX() * 127 & 0xFF)
            | ((direction.getStepY() * 127 & 0xFF) << 8)
            | ((direction.getStepZ() * 127 & 0xFF) << 16);
    }

    public enum UvRotation {
        CLOCKWISE_0,
        CLOCKWISE_90,
        CLOCKWISE_180,
        CLOCKWISE_270
    }

    private static class Uv {
        private float xFrom;
        private float xTo;
        private float yFrom;
        private float yTo;
    }

    public static class Face {
        private final Direction face;
        private final TextureAtlasSprite sprite;
        private final UvRotation uvRotation = UvRotation.CLOCKWISE_0;

        public Face(Direction face, TextureAtlasSprite sprite) {
            this.face = face;
            this.sprite = sprite;
        }
    }
}


