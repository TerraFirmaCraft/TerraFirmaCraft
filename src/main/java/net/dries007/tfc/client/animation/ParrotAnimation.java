/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

import net.dries007.tfc.common.entities.prey.TFCParrot;

public class ParrotAnimation
{
    public static final AnimationDefinition PREEN = AnimationDefinition.Builder.withLength(2.4F)
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.0F, -2.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(0.433F, -0.087F, -0.186F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(0.44F, -0.145F, -0.327F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.433F, -0.087F, -0.186F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(0.44F, -0.145F, -0.327F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.433F, -0.087F, -0.186F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.44F, -0.145F, -0.327F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.214F, -0.039F, -0.094F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(27.0F, -104.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(38.5F, -109.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(27.0F, -104.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(38.5F, -109.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(27.0F, -104.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(38.5F, -109.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(21.0F, -97.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(0.461F, -0.079F, -0.214F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(0.47F, -0.132F, -0.377F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.461F, -0.079F, -0.214F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(0.47F, -0.132F, -0.377F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.461F, -0.079F, -0.214F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.47F, -0.132F, -0.377F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.228F, -0.035F, -0.109F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "beak2",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.0F, -2.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(-0.099F, 0.083F, -0.039F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(-0.094F, 0.14F, -0.066F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(-0.099F, 0.083F, -0.039F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(-0.094F, 0.14F, -0.066F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(-0.099F, 0.083F, -0.039F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(-0.094F, 0.14F, -0.066F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(-0.051F, 0.04F, -0.018F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(-2.0F, -4.0F, -9.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.5F, -4.0F, -9.735F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(-2.0F, -4.0F, -10.224F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(-3.5F, -4.0F, -10.469F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-2.0F, -4.0F, -10.469F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-3.5F, -4.0F, -10.224F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(-1.0F, -2.0F, -10.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(0.387F, -0.155F, -0.066F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(0.394F, -0.206F, -0.196F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.387F, -0.155F, -0.066F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(0.394F, -0.206F, -0.196F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.387F, -0.155F, -0.066F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.394F, -0.206F, -0.196F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.193F, -0.073F, -0.035F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(-2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(-2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(-3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-2.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-3.5F, -4.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(-1.0F, -2.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(0.399F, 0.002F, -0.276F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(0.405F, -0.05F, -0.405F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.399F, 0.002F, -0.276F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(0.405F, -0.05F, -0.405F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.399F, 0.002F, -0.276F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.405F, -0.05F, -0.405F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.196F, 0.005F, -0.139F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .build();

    public static final AnimationDefinition WING_PREEN = AnimationDefinition.Builder.withLength(2.6F)
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.degreeVec(3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.degreeVec(1.5F, -3.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.posVec(-0.098F, -0.069F, -0.183F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.posVec(-0.082F, -0.126F, -0.323F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.posVec(-0.098F, -0.069F, -0.183F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(-0.082F, -0.126F, -0.323F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(-0.098F, -0.069F, -0.183F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.posVec(-0.082F, -0.126F, -0.323F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.posVec(-0.081F, -0.052F, -0.14F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(32.0F, -80.0F, 7.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.degreeVec(40.5F, -85.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(32.0F, -80.0F, 7.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(40.5F, -85.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(32.0F, -80.0F, 7.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(40.5F, -85.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.degreeVec(29.5F, -73.0F, 6.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.posVec(0.841F, 0.537F, -0.512F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.posVec(0.925F, 0.525F, -0.694F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.posVec(0.95F, 0.602F, -0.544F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(0.983F, 0.56F, -0.712F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(0.956F, 0.606F, -0.546F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.posVec(0.938F, 0.533F, -0.698F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.posVec(0.879F, 0.553F, -0.462F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "beak2",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.degreeVec(3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.degreeVec(1.5F, -3.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.posVec(-0.242F, 0.065F, -0.042F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.posVec(-0.243F, 0.122F, -0.069F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.posVec(-0.242F, 0.065F, -0.042F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(-0.243F, 0.122F, -0.069F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(-0.242F, 0.065F, -0.042F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.posVec(-0.243F, 0.122F, -0.069F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.posVec(-0.149F, 0.053F, -0.028F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.35F, KeyframeAnimations.degreeVec(-16.374F, -3.71F, -57.226F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(-17.62F, -5.0F, -60.273F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.degreeVec(-19.753F, -5.0F, -62.596F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(-18.667F, -5.0F, -64.111F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(-20.36F, -5.0F, -64.818F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-18.832F, -5.0F, -64.717F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(-20.084F, -5.0F, -63.808F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.degreeVec(-17.464F, -3.0F, -60.535F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(-15.536F, -1.182F, -55.807F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, -27.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.094F, 0.188F, 0.125F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.posVec(-0.105F, 0.017F, -0.038F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.posVec(-0.091F, -0.033F, -0.167F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.posVec(-0.105F, 0.017F, -0.038F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(-0.091F, -0.033F, -0.167F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(-0.105F, 0.017F, -0.038F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.posVec(-0.091F, -0.033F, -0.167F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.posVec(-0.082F, 0.006F, -0.051F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(-2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.degreeVec(-3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(-2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(-3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-2.0F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(-3.5F, -5.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.degreeVec(-1.5F, -3.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5F, KeyframeAnimations.posVec(-0.09F, -0.139F, -0.299F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.7F, KeyframeAnimations.posVec(-0.076F, -0.19F, -0.429F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.posVec(-0.09F, -0.139F, -0.299F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(-0.076F, -0.19F, -0.429F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(-0.09F, -0.139F, -0.299F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.posVec(-0.076F, -0.19F, -0.429F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.75F, KeyframeAnimations.posVec(-0.076F, -0.098F, -0.208F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .build();

    public static final AnimationDefinition WING_STRETCH = AnimationDefinition.Builder.withLength(2.2F)
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -4.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(-0.373F, -0.162F, -0.379F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(-0.417F, -0.207F, -0.472F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(-0.373F, -0.162F, -0.379F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -4.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(-0.431F, -0.15F, -0.436F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(-0.481F, -0.192F, -0.543F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(-0.431F, -0.15F, -0.436F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -4.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(-0.075F, 0.149F, -0.07F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(-0.088F, 0.186F, -0.089F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(-0.075F, 0.149F, -0.07F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(-29.0F, 0.0F, -94.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(-33.125F, 0.0F, -105.75F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(-29.0F, 0.0F, -94.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(-0.347F, -0.039F, -0.349F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(-0.388F, -0.066F, -0.435F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(-0.347F, -0.039F, -0.349F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -4.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(-0.34F, -0.248F, -0.349F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.0F, KeyframeAnimations.posVec(-0.379F, -0.301F, -0.435F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(-0.34F, -0.248F, -0.349F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_leg",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .build();

    public static final AnimationDefinition TAIL_FLICK = AnimationDefinition.Builder.withLength(0.8F)
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.degreeVec(1.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.2F, KeyframeAnimations.degreeVec(1.406F, 0.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 3.75F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.588F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.posVec(0.0F, -0.053F, -0.143F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -0.035F, -0.096F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.degreeVec(1.5F, 0.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.2F, KeyframeAnimations.degreeVec(1.406F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 2.25F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.588F, 0.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.posVec(0.0F, -0.047F, -0.165F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -0.031F, -0.11F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.degreeVec(-13.5F, 20.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.2F, KeyframeAnimations.degreeVec(-13.594F, -20.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(-10.25F, 15.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(-4.412F, -10.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.posVec(-0.5F, 0.058F, -0.025F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.2F, KeyframeAnimations.posVec(0.5F, 0.054F, -0.023F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(-0.375F, 0.038F, -0.017F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.posVec(0.25F, 0.023F, -0.01F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.2F, KeyframeAnimations.degreeVec(-1.406F, 0.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 3.75F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(-0.588F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.posVec(0.0F, -0.046F, -0.132F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -0.031F, -0.088F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.2F, KeyframeAnimations.degreeVec(-1.406F, 0.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 3.75F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.4F, KeyframeAnimations.degreeVec(-0.588F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.1F, KeyframeAnimations.posVec(0.0F, -0.046F, -0.132F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -0.031F, -0.088F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.55F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .build();

    public static final AnimationDefinition FLUFF = AnimationDefinition.Builder.withLength(2.0F)
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-2.391F, 0.0F, 8.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(-0.609F, 0.0F, 6.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 0.095F, 0.291F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.095F, 0.291F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.SCALE,
                new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.scaleVec(1.2F, 1.05F, 1.2F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.scaleVec(1.2F, 1.05F, 1.2F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-2.391F, 0.0F, 4.8F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, -4.8F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(-0.609F, 0.0F, 3.6F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 0.081F, 0.333F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.081F, 0.333F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.SCALE,
                new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.scaleVec(1.04F, 1.04F, 1.04F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.scaleVec(1.04F, 1.04F, 1.04F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(-9.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-9.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-12.391F, 22.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-11.5F, -22.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(-9.609F, 16.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.014F, -0.117F, 0.046F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, -0.117F, 0.046F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(-0.6F, -0.093F, 0.037F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.6F, -0.058F, 0.024F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.posVec(-0.45F, -0.024F, 0.01F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(3.0F, 0.0F, -0.188F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(2.391F, 0.0F, 8.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.5F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.609F, 0.0F, 6.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.3F, 0.083F, 0.267F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(0.3F, 0.083F, 0.267F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(3.0F, 0.0F, -0.188F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(2.391F, 0.0F, 8.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.5F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.609F, 0.0F, 6.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(-0.3F, 0.083F, 0.267F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(-0.3F, 0.083F, 0.267F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .build();

    public static final AnimationDefinition DOUBLE_WING_STRETCH = AnimationDefinition.Builder.withLength(2.0F)
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(7.5F, 0.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.posVec(0.0F, -0.279F, -0.656F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(0.136F, -0.303F, -0.701F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(-0.136F, -0.303F, -0.701F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(0.091F, -0.279F, -0.656F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, -0.279F, -0.656F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(16.364F, 0.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(16.165F, 0.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(15.377F, 0.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "head",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.posVec(0.0F, -0.255F, -0.756F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(0.158F, -0.279F, -0.809F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(-0.158F, -0.279F, -0.809F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(0.106F, -0.256F, -0.756F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, -0.255F, -0.756F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(-3.58F, 0.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-3.331F, 0.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-3.471F, 0.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "tail",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.posVec(0.0F, 0.262F, -0.13F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(0.032F, 0.28F, -0.14F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(-0.032F, 0.28F, -0.14F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(0.021F, 0.262F, -0.13F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, 0.262F, -0.13F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.degreeVec(-27.0F, 0.0F, -150.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(-29.661F, 0.0F, -164.705F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-29.162F, 0.0F, -163.965F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-27.942F, 0.0F, -156.064F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-27.0F, 0.0F, -150.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.posVec(0.0F, -0.246F, -0.604F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(0.125F, -0.307F, -0.646F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(-0.126F, -0.229F, -0.646F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(0.084F, -0.273F, -0.604F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, -0.246F, -0.604F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.degreeVec(-27.0F, 0.0F, 150.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.degreeVec(-29.661F, 0.0F, 167.705F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.degreeVec(-29.162F, 0.0F, 160.965F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.degreeVec(-27.942F, 0.0F, 158.064F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-27.0F, 0.0F, 150.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_wing",
            new AnimationChannel(
                AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.45F, KeyframeAnimations.posVec(0.0F, -0.246F, -0.604F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.1F, KeyframeAnimations.posVec(0.126F, -0.229F, -0.646F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.2F, KeyframeAnimations.posVec(-0.125F, -0.307F, -0.646F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.3F, KeyframeAnimations.posVec(0.084F, -0.221F, -0.604F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, -0.246F, -0.604F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .build();

    public static AnimationDefinition get(TFCParrot.IdleAnimation animation)
    {
        return switch (animation)
        {
            case PREEN -> PREEN;
            case WING_PREEN -> WING_PREEN;
            case WING_STRETCH -> WING_STRETCH;
            case TAIL_FLICK -> TAIL_FLICK;
            case FLUFF -> FLUFF;
            case DOUBLE_WING_STRETCH -> DOUBLE_WING_STRETCH;
        };
    }
}