/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import net.dries007.tfc.client.animation.ParrotAnimation;
import net.dries007.tfc.common.entities.prey.TFCParrot;
import net.dries007.tfc.mixin.client.accessor.ParrotModelAccessor;

public class TFCParrotModel extends ParrotModel
{
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
    private static final int SHOULDER_IDLE_WINDOW = 200; // one idle per window, starting in its first half
    private static final Map<TFCParrot.IdleAnimation, AnimationDefinition> MIRRORED = new EnumMap<>(TFCParrot.IdleAnimation.class);

    /**
     * Shoulder parrots are not entities, so idles are scheduled from game time and UUID, which every client agrees on.
     * Animations lean left, so they're mirrored on the right shoulder to stay clear of the player's head.
     */
    public static void renderOnShoulder(ParrotModel model, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, Player player, CompoundTag tag, boolean leftShoulder)
    {
        final ParrotModelAccessor accessor = (ParrotModelAccessor) model;
        model.root().getAllParts().forEach(ModelPart::resetPose);
        accessor.invoke$prepare(ParrotModel.State.ON_SHOULDER);
        accessor.invoke$setupAnim(ParrotModel.State.ON_SHOULDER, player.tickCount, limbSwing, limbSwingAmount, 0f, netHeadYaw, headPitch);

        final Minecraft mc = Minecraft.getInstance();
        if (player != mc.player || !mc.options.getCameraType().isFirstPerson()) // a wing could swing into the holder's screen
        {
            final int seed = tag.hasUUID("UUID") ? tag.getUUID("UUID").hashCode() : player.getUUID().hashCode() + (leftShoulder ? 1 : 0);
            final float time = player.level().getGameTime() + mc.getTimer().getGameTimeDeltaPartialTick(false);
            final int window = Mth.floor(time / SHOULDER_IDLE_WINDOW);
            final int hash = Mth.murmurHash3Mixer(seed + window * 0x9E3779B9);
            final float elapsed = time - (window * SHOULDER_IDLE_WINDOW + Math.floorMod(hash, SHOULDER_IDLE_WINDOW / 2));
            if (elapsed >= 0 && elapsed < TFCParrot.MAX_IDLE_ANIMATION_TICKS)
            {
                final TFCParrot.IdleAnimation animation = TFCParrot.IdleAnimation.VALUES[Math.floorMod(hash >>> 8, TFCParrot.IdleAnimation.VALUES.length)];
                KeyframeAnimations.animate(model, leftShoulder ? ParrotAnimation.get(animation) : mirrored(animation), (long) (elapsed * 50), 1f, ANIMATION_VECTOR_CACHE);
            }
        }
        narrowTail(model);
        model.root().render(poseStack, buffer, packedLight, packedOverlay);
        model.root().getAllParts().forEach(ModelPart::resetPose); // The layer shares this model with vanilla parrots, which don't reset it
    }

    /**
     * The tail is exactly as wide as the body, so their sides are coplanar and z-fight
     */
    private static void narrowTail(ParrotModel model)
    {
        model.root().getChild("tail").xScale *= 0.99f;
    }

    private static AnimationDefinition mirrored(TFCParrot.IdleAnimation animation)
    {
        return MIRRORED.computeIfAbsent(animation, key -> {
            final AnimationDefinition definition = ParrotAnimation.get(key);
            final Map<String, List<AnimationChannel>> bones = new HashMap<>();
            definition.boneAnimations().forEach((bone, channels) -> bones.put(mirrorBone(bone), channels.stream().map(TFCParrotModel::mirror).toList()));
            return new AnimationDefinition(definition.lengthInSeconds(), definition.looping(), bones);
        });
    }

    private static String mirrorBone(String bone)
    {
        if (bone.startsWith("left_")) return "right_" + bone.substring(5);
        if (bone.startsWith("right_")) return "left_" + bone.substring(6);
        return bone;
    }

    private static AnimationChannel mirror(AnimationChannel channel)
    {
        final Vector3f flip = channel.target() == AnimationChannel.Targets.POSITION ? new Vector3f(-1, 1, 1)
            : channel.target() == AnimationChannel.Targets.ROTATION ? new Vector3f(1, -1, -1)
            : new Vector3f(1, 1, 1);
        return new AnimationChannel(channel.target(), Arrays.stream(channel.keyframes())
            .map(key -> new Keyframe(key.timestamp(), key.target().mul(flip, new Vector3f()), key.interpolation()))
            .toArray(Keyframe[]::new));
    }

    private float partialTick;

    public TFCParrotModel(ModelPart root)
    {
        super(root);
    }

    @Override
    public void prepareMobModel(Parrot entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        // Vanilla only sets the parts it animates
        root().getAllParts().forEach(ModelPart::resetPose);
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.partialTick = partialTick;
    }

    @Override
    public void setupAnim(Parrot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity instanceof TFCParrot parrot)
        {
            // The renderer passes the wing flap as ageInTicks
            parrot.idleAnimationState.updateTime(parrot.tickCount + partialTick, 1f);
            final float weight = parrot.getIdleAnimationWeight(partialTick);
            if (weight > 0f)
            {
                parrot.idleAnimationState.ifStarted(state -> KeyframeAnimations.animate(this, ParrotAnimation.get(parrot.getIdleAnimation()), state.getAccumulatedTime(), weight, ANIMATION_VECTOR_CACHE));
            }
        }
        narrowTail(this);
    }
}