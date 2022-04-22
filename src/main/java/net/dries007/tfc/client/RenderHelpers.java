/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.model.Animation;
import net.dries007.tfc.client.model.Easing;
import net.dries007.tfc.common.entities.land.TFCAnimal;
import net.dries007.tfc.common.entities.land.TFCAnimalProperties;
import net.dries007.tfc.util.Helpers;

public class RenderHelpers
{
    public static void setShaderColor(int color)
    {
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);
    }

    // Use this to get vertices for a box from Min - Max point in 3D
    // Pass the string of the axies you want the box to render on ('xz') for no top / bottom, etc.
    // Pass 'xyz' for all vertices
    public static float[][] getVerticesBySide(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, String axes)
    {
        float[][] ret = new float[][] {};
        if (axes.contains("x"))
        {
            ret = append(ret, getXVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        if (axes.contains("y"))
        {
            ret = append(ret, getYVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        if (axes.contains("z"))
        {
            ret = append(ret, getZVertices(minX, minY, minZ, maxX, maxY, maxZ));
        }
        return ret;

    }

    public static float[][] getXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, minY, minZ, 0, 1}, // Main +X Side
            {minX, minY, maxZ, 1, 1},
            {minX, maxY, maxZ, 1, 0},
            {minX, maxY, minZ, 0, 0},

            {maxX, minY, maxZ, 1, 0}, // Main -X Side
            {maxX, minY, minZ, 0, 0},
            {maxX, maxY, minZ, 0, 1},
            {maxX, maxY, maxZ, 1, 1}
        };
    }

    public static float[][] getYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, maxY, minZ, 0, 1}, // Top
            {minX, maxY, maxZ, 1, 1},
            {maxX, maxY, maxZ, 1, 0},
            {maxX, maxY, minZ, 0, 0},

            {minX, minY, maxZ, 1, 0}, // Bottom
            {minX, minY, minZ, 0, 0},
            {maxX, minY, minZ, 0, 1},
            {maxX, minY, maxZ, 1, 1}
        };
    }

    public static float[][] getZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {maxX, minY, minZ, 0, 1}, // Main +Z Side
            {minX, minY, minZ, 1, 1},
            {minX, maxY, minZ, 1, 0},
            {maxX, maxY, minZ, 0, 0},

            {minX, minY, maxZ, 1, 0}, // Main -Z Side
            {maxX, minY, maxZ, 0, 0},
            {maxX, maxY, maxZ, 0, 1},
            {minX, maxY, maxZ, 1, 1}
        };
    }

    public static float[][] append(float[][] a, float[][] b)
    {
        float[][] result = new float[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * This is the map code in {@link net.minecraft.client.renderer.ItemInHandRenderer}
     */
    public static void renderTwoHandedItem(PoseStack poseStack, MultiBufferSource source, int combinedLight, float pitch, float equipProgress, float swingProgress, ItemStack stack)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        final float swingSqrt = Mth.sqrt(swingProgress);
        final float y = -0.2F * Mth.sin(swingProgress * (float) Math.PI);
        final float z = -0.4F * Mth.sin(swingSqrt * (float) Math.PI);
        poseStack.translate(0.0D, -y / 2.0F, z);

        final float tilt = calculateTilt(pitch);
        poseStack.translate(0.0D, 0.04F + equipProgress * -1.2F + tilt * -0.5F, -0.72F);
        // tfc: clamp the tilt amount, and reverse the tilt direction based on the player looking around
        poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.clamp(tilt, 0.3F, 0.51F) * 85.0F));
        if (!mc.player.isInvisible())
        {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            renderMapHand(mc, poseStack, source, combinedLight, HumanoidArm.RIGHT);
            renderMapHand(mc, poseStack, source, combinedLight, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        addSiftingMovement(mc.player, poseStack); // tfc: rotate the pan due to sifting
        poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(swingSqrt * (float) Math.PI) * 20.0F));
        poseStack.scale(2.0F, 2.0F, 2.0F);

        final boolean right = mc.player.getMainArm() == HumanoidArm.RIGHT;
        mc.getItemInHandRenderer().renderItem(mc.player, stack,
            right ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
            !right, poseStack, source, combinedLight);

    }

    /**
     * Creates {@link ModelLayerLocation} in the default manner
     */
    public static ModelLayerLocation modelIdentifier(String name, String part)
    {
        return new ModelLayerLocation(Helpers.identifier(name), part);
    }

    public static ModelLayerLocation modelIdentifier(String name)
    {
        return modelIdentifier(name, "main");
    }

    public static ResourceLocation animalTexture(String name)
    {
        return Helpers.identifier("textures/entity/animal/" + name + ".png");
    }

    public static ModelPart bakeSimple(EntityRendererProvider.Context ctx, String layerName)
    {
        return ctx.bakeLayer(modelIdentifier(layerName));
    }

    public static Animation.Bone.Builder newBone()
    {
        return new Animation.Bone.Builder(Easing.LINEAR);
    }

    public static float itemTimeRotation()
    {
        return (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
    }

    public static int getFluidColor(FluidStack fluid)
    {
        return fluid.getFluid().getAttributes().getColor();
    }

    public static void renderFluidFace(PoseStack poseStack, FluidStack fluidStack, MultiBufferSource buffer, float minX, float minZ, float maxX, float maxZ, float y, int combinedOverlay, int combinedLight)
    {
        renderFluidFace(poseStack, fluidStack, buffer, getFluidColor(fluidStack), minX, minZ, maxX, maxZ, y, combinedOverlay, combinedLight);
    }

    @SuppressWarnings("deprecation")
    public static void renderFluidFace(PoseStack poseStack, FluidStack fluidStack, MultiBufferSource buffer, int color, float minX, float minZ, float maxX, float maxZ, float y, int combinedOverlay, int combinedLight)
    {
        Fluid fluid = fluidStack.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation texture = attributes.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
        Matrix4f matrix4f = poseStack.last().pose();

        builder.vertex(matrix4f, minX, y, minZ).color(color).uv(sprite.getU(minX * 16), sprite.getV(minZ * 16)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix4f, minX, y, maxZ).color(color).uv(sprite.getU(minX * 16), sprite.getV(maxZ * 16)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix4f, maxX, y, maxZ).color(color).uv(sprite.getU(maxX * 16), sprite.getV(maxZ * 16)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix4f, maxX, y, minZ).color(color).uv(sprite.getU(maxX * 16), sprite.getV(minX * 16)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
    }

    public static ResourceLocation getTextureForAge(TFCAnimal animal, ResourceLocation young, ResourceLocation old)
    {
        return animal.getAgeType() == TFCAnimalProperties.Age.OLD ? old : young;
    }

    private static float calculateTilt(float pitch)
    {
        final float deg = Mth.clamp(1.0F - pitch / 45.0F + 0.1F, 0.0F, 1.0F) * (float) Math.PI;
        return -Mth.cos(deg) * 0.5F + 0.5F;
    }

    private static void renderMapHand(Minecraft mc, PoseStack poseStack, MultiBufferSource source, int combinedLight, HumanoidArm arm)
    {
        RenderSystem.setShaderTexture(0, mc.player.getSkinTextureLocation());
        PlayerRenderer playerrenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().<AbstractClientPlayer>getRenderer(mc.player);
        poseStack.pushPose();
        final float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(92.0F));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(side * -41.0F + calculateArmMovement(mc.player))); // tfc: jiggle the arms
        poseStack.translate(side * 0.3D, -1.1D, 0.45D);
        if (arm == HumanoidArm.RIGHT)
        {
            playerrenderer.renderRightHand(poseStack, source, combinedLight, mc.player);
        }
        else
        {
            playerrenderer.renderLeftHand(poseStack, source, combinedLight, mc.player);
        }
        poseStack.popPose();
    }

    private static void addSiftingMovement(LocalPlayer player, PoseStack stack)
    {
        final float degrees = player.getUseItemRemainingTicks() * (float) Math.PI / 10F;
        if (degrees > 0f)
        {
            final float scale = 0.1f;
            stack.translate(scale * Mth.cos(degrees), 0f, scale * Mth.sin(degrees));
        }
    }

    private static float calculateArmMovement(LocalPlayer player)
    {
        final float degrees = player.getUseItemRemainingTicks() * (float) Math.PI / 10F;
        if (degrees > 0f)
        {
            return 10f * Mth.cos(degrees);
        }
        return 0f;
    }
}
