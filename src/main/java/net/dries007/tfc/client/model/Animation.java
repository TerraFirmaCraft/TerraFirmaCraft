/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

import com.mojang.math.Vector3f;

public record Animation(int lengthInTicks, float lengthScale, Map<String, Bone> bones)
{
    public static ImmutableMap<ModelPart, PartPose> initDefaults(Map<String, ModelPart> parts)
    {
        var rotationsMap = new ImmutableMap.Builder<ModelPart, PartPose>();
        parts.forEach((s, part) -> rotationsMap.put(part, part.storePose()));
        return rotationsMap.build();
    }

    @Nullable
    private static Vector3f getTransformForTime(Map<Float, Vector3f> times, float totalProgress, Easing easing)
    {
        if (!times.isEmpty())
        {
            Map.Entry<Float, Vector3f> current = null;
            Map.Entry<Float, Vector3f> next = null;
            for (Map.Entry<Float, Vector3f> entry : times.entrySet())
            {
                if (totalProgress >= entry.getKey())
                {
                    current = entry;
                }
                else
                {
                    next = entry;
                    break;
                }
            }
            if (current != null && next != null)
            {
                return lerp(current, next, totalProgress, easing);
            }
        }
        return null;
    }

    private static Vector3f lerp(Map.Entry<Float, Vector3f> a, Map.Entry<Float, Vector3f> b, float totalProgress, Easing easing)
    {
        final float time = easing.apply(Mth.map(totalProgress, a.getKey(), b.getKey(), 0F, 1F));
        final Vector3f start = a.getValue();
        final Vector3f end = b.getValue();
        return new Vector3f(Mth.lerp(time, start.x(), end.x()), Mth.lerp(time, start.y(), end.y()), Mth.lerp(time, start.z(), end.z()));
    }

    public void tick(Map<String, ModelPart> map, float ageInTicks)
    {
        final float progress = (ageInTicks % lengthInTicks) / lengthInTicks * lengthScale;

        bones.forEach((name, bone) -> {
            final ModelPart part = map.get(name);

            final Vector3f rotations = getTransformForTime(bone.rotations, progress, bone.easing);
            final Vector3f translations = getTransformForTime(bone.translations, progress, bone.easing);
            //final Vector3f scales = getTransformForTime(part, bone.scales, time, easing);

            if (rotations != null)
            {
                part.setRotation(rotations.x(), rotations.y(), rotations.z());
            }
            if (translations != null)
            {
                part.x = translations.x();
                part.y = translations.y();
                part.z = translations.z();
            }
            // scales: unimplemented?
        });
    }

    public static class Builder
    {
        private final float lengthScale;
        private final int lengthInTicks;
        private final ImmutableMap.Builder<String, Bone> boneBuilder;

        public Builder(float length)
        {
            lengthScale = length;
            lengthInTicks = Mth.floor(length * 20F);
            boneBuilder = new ImmutableMap.Builder<>();
        }

        public Builder bone(String name, Bone bone)
        {
            boneBuilder.put(name, bone);
            return this;
        }

        public Animation build()
        {
            return new Animation(lengthInTicks, lengthScale, boneBuilder.build());
        }
    }

    public record Bone(Map<Float, Vector3f> rotations, Map<Float, Vector3f> translations, Map<Float, Vector3f> scales, Easing easing)
    {
        public static class Builder
        {
            private final Easing easing;
            private final ImmutableMap.Builder<Float, Vector3f> rotationBuilder;
            private final ImmutableMap.Builder<Float, Vector3f> translationBuilder;
            private final ImmutableMap.Builder<Float, Vector3f> scaleBuilder;

            public Builder(Easing easing)
            {
                rotationBuilder = new ImmutableMap.Builder<>();
                translationBuilder = new ImmutableMap.Builder<>();
                scaleBuilder = new ImmutableMap.Builder<>();
                this.easing = easing;
            }

            public Builder rotation(float time, float x, float y, float z)
            {
                rotationBuilder.put(time, new Vector3f(x / 180F * Mth.PI, y / 180F * Mth.PI, z / 180F * Mth.PI));
                return this;
            }

            public Builder translation(float time, float x, float y, float z)
            {
                translationBuilder.put(time, new Vector3f(x / 180F * Mth.PI, y / 180F * Mth.PI, z / 180F * Mth.PI));
                return this;
            }

            public Builder scale(float time, float x, float y, float z)
            {
                scaleBuilder.put(time, new Vector3f(x / 180F * Mth.PI, y / 180F * Mth.PI, z / 180F * Mth.PI));
                return this;
            }

            public Builder noRotation(float time)
            {
                rotationBuilder.put(time, Vector3f.ZERO);
                return this;
            }

            public Bone build()
            {
                return new Bone(rotationBuilder.build(), translationBuilder.build(), scaleBuilder.build(), easing);
            }
        }
    }
}
