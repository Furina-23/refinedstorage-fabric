package net.minecraftforge.common.util;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TransformationHelper {
    private TransformationHelper() { }
    public static Quaternionf quatFromXYZ(Vector3f rotation, boolean degrees) {
        float factor = degrees ? (float) (Math.PI / 180.0) : 1.0F;
        return new Quaternionf().rotationXYZ(rotation.x() * factor, rotation.y() * factor, rotation.z() * factor);
    }
}
