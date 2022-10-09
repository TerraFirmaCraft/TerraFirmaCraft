/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.mojang.math.Vector3f;
import net.dries007.tfc.util.Helpers;

public record AnimationChannel(AnimationChannel.Target target, Keyframe... keyframes)
{

    public interface Target
    {
        void apply(ModelPart part, Vector3f pose);
    }

    public static class Targets
    {
        public static final AnimationChannel.Target POSITION = (part, pose) -> {
            part.x += pose.x();
            part.y += pose.y();
            part.z += pose.z();
        };
        public static final AnimationChannel.Target ROTATION = (part, pose) -> {
            part.xRot += pose.x();
            part.yRot += pose.y();
            part.zRot += pose.z();
        };
        // SCALE not implementable in 1.18
    }

    public interface Interpolation
    {
        Vector3f apply(Vector3f pos, float lerpAmount, Keyframe[] frames, int start, int end, float scale);
    }

    public static class Interpolations
    {
        public static final AnimationChannel.Interpolation LINEAR = (pos, lerp, frames, startIndex, endIndex, scale) -> {
            final Vector3f start = frames[startIndex].target();
            final Vector3f end = frames[endIndex].target();
            pos.set(
                Mth.lerp(lerp, start.x(), end.x()) * scale,
                Mth.lerp(lerp, start.y(), end.y()) * scale,
                Mth.lerp(lerp, start.z(), end.z()) * scale
            );
            return pos;
        };
        public static final AnimationChannel.Interpolation CATMULLROM = (pos, lerp, frames, startIndex, endIndex, scale) -> {
            final Vector3f lowAnchor = frames[Math.max(0, startIndex - 1)].target();
            final Vector3f start = frames[startIndex].target();
            final Vector3f end = frames[endIndex].target();
            final Vector3f highAnchor = frames[Math.min(frames.length - 1, endIndex + 1)].target();
            pos.set(
                Helpers.catMullRomSpline(lerp, lowAnchor.x(), start.x(), end.x(), highAnchor.x()) * scale,
                Helpers.catMullRomSpline(lerp, lowAnchor.y(), start.y(), end.y(), highAnchor.y()) * scale,
                Helpers.catMullRomSpline(lerp, lowAnchor.z(), start.z(), end.z(), highAnchor.z()) * scale
            );
            return pos;
        };
    }
}
