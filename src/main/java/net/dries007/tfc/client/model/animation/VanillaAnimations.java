/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.animation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

import com.mojang.math.Vector3f;

import net.dries007.tfc.mixin.client.accessor.ModelPartAccessor;

public final class VanillaAnimations
{

    public static void animate(HierarchicalModel<?> model, AnimationDefinition definition, long ticks, float scale, Vector3f pos)
    {
        float seconds = getElapsedSeconds(definition, ticks);

        for (Map.Entry<String, List<AnimationChannel>> entry : definition.boneAnimations().entrySet())
        {
            final Optional<ModelPart> optionalPart = findNamedPart(model, entry.getKey());
            final List<AnimationChannel> list = entry.getValue();
            optionalPart.ifPresent(part -> {
                list.forEach(channel -> {
                    final Keyframe[] frames = channel.keyframes();
                    final int startIdx = Math.max(0, Mth.binarySearch(0, frames.length, idx -> seconds <= frames[idx].timestamp()) - 1);
                    final int endIdx = Math.min(frames.length - 1, startIdx + 1);
                    final Keyframe start = frames[startIdx];
                    final Keyframe end = frames[endIdx];
                    final float now = seconds - start.timestamp();
                    final float lerp = Mth.clamp(now / (end.timestamp() - start.timestamp()), 0f, 1f);
                    end.interpolation().apply(pos, lerp, frames, startIdx, endIdx, scale);
                    channel.target().apply(part, pos);
                });
            });
        }

    }

    public static Optional<ModelPart> findNamedPart(HierarchicalModel<?> model, String name)
    {
        return model.root().getAllParts()
            .filter((p) -> ((ModelPartAccessor) (Object) p).accessor$getChildren().containsKey(name)).findFirst()
            .map((p) -> p.getChild(name));
    }

    private static float getElapsedSeconds(AnimationDefinition anim, long ticks)
    {
        float f = (float) ticks / 1000.0F;
        return anim.loop() ? f % anim.lengthInSeconds() : f;
    }

    public static ImmutableMap<ModelPart, PartPose> save(Stream<ModelPart> parts)
    {
        final ImmutableMap.Builder<ModelPart, PartPose> map = new ImmutableMap.Builder<>();
        parts.forEach(part -> map.put(part, part.storePose()));
        return map.build();
    }

    public static Keyframe translation(float time, float x, float y, float z)
    {
        return translation(time, x, y, z, true);
    }

    public static Keyframe translation(float time, float x, float y, float z, boolean cmr)
    {
        return new Keyframe(time, posVec(x, y, z), cmr ? AnimationChannel.Interpolations.CATMULLROM : AnimationChannel.Interpolations.LINEAR);
    }

    public static Keyframe noRotation(float time)
    {
        return rotation(time, 0f, 0f, 0f);
    }

    public static Keyframe noRotation(float time, boolean cmr)
    {
        return rotation(time, 0f, 0f, 0f, cmr);
    }

    public static Keyframe rotation(float time, float x, float y, float z)
    {
        return rotation(time, x, y, z, true);
    }

    public static Keyframe rotation(float time, float x, float y, float z, boolean cmr)
    {
        return new Keyframe(time, degreeVec(x, y, z), cmr ? AnimationChannel.Interpolations.CATMULLROM : AnimationChannel.Interpolations.LINEAR);
    }

    public static Vector3f posVec(float x, float y, float z)
    {
        return new Vector3f(x, -y, z);
    }

    public static Vector3f degreeVec(float x, float y, float z)
    {
        return new Vector3f(x * (Mth.PI / 180F), y * (Mth.PI / 180F), z * (Mth.PI / 180F));
    }

}
