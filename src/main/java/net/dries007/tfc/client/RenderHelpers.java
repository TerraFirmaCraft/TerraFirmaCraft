/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.function.Consumer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.math.Quaternion;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.Helpers;

public final class RenderHelpers
{
    @SuppressWarnings("deprecation") public static final ResourceLocation BLOCKS_ATLAS = TextureAtlas.LOCATION_BLOCKS;

    /**
     * Renders a fully textured, solid cuboid described by the provided {@link AABB}, usually obtained from {@link VoxelShape#bounds()}.
     * Texture widths (in pixels) are inferred to be 16 x the width of the quad, which matches normal block pixel texture sizes.
     */
    public static void renderTexturedCuboid(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, AABB bounds)
    {
        renderTexturedCuboid(poseStack, buffer, sprite, packedLight, packedOverlay, (float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ);
    }

    /**
     * Renders a fully textured, solid cuboid described by the shape (minX, minY, minZ) x (maxX, maxY, maxZ).
     * Texture widths (in pixels) are inferred to be 16 x the width of the quad, which matches normal block pixel texture sizes.
     */
    public static void renderTexturedCuboid(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        renderTexturedCuboid(poseStack, buffer, sprite, packedLight, packedOverlay, minX, minY, minZ, maxX, maxY, maxZ, 16f * (maxX - minX), 16f * (maxY - minY), 16f * (maxZ - minZ));
    }

    /**
     * Renders a fully textured, solid cuboid described by the shape (minX, minY, minZ) x (maxX, maxY, maxZ).
     * (xPixels, yPixels, zPixels) represent pixel widths for each side, which are used for texture (u, v) purposes.
     */
    public static void renderTexturedCuboid(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float xPixels, float yPixels, float zPixels)
    {
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getXVertices(minX, minY, minZ, maxX, maxY, maxZ), zPixels, yPixels, 1, 0, 0);
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getYVertices(minX, minY, minZ, maxX, maxY, maxZ), zPixels, xPixels, 0, 1, 0);
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getZVertices(minX, minY, minZ, maxX, maxY, maxZ), xPixels, yPixels, 0, 0, 1);
    }

    /**
     * <pre>
     *  Q------Q.  ^ y
     *  |`.    | `.|
     *  |  `Q--+---Q--> x = maxY
     *  |   |  |   |
     *  P---+--P.  |
     *   `. |    `.|
     *     `P------P = minY
     * </pre>
     *
     * Renders a fully textured, solid trapezoidal cuboid described by the plane P, the plane Q, minY, and maxY.
     * (xPixels, yPixels, zPixels) represent pixel widths for each side, which are used for texture (u, v) purposes.
     */
    public static void renderTexturedTrapezoidalCuboid(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, float pMinX, float pMaxX, float pMinZ, float pMaxZ, float qMinX, float qMaxX, float qMinZ, float qMaxZ, float minY, float maxY, float xPixels, float yPixels, float zPixels)
    {
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getTrapezoidalCuboidXVertices(pMinX, pMaxX, pMinZ, pMaxZ, qMinX, qMaxX, qMinZ, qMaxZ, minY, maxY), zPixels, yPixels, 1, 0, 0);
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getTrapezoidalCuboidYVertices(pMinX, pMaxX, pMinZ, pMaxZ, qMinX, qMaxX, qMinZ, qMaxZ, minY, maxY), zPixels, xPixels, 0, 1, 0);
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getTrapezoidalCuboidZVertices(pMinX, pMaxX, pMinZ, pMaxZ, qMinX, qMaxX, qMinZ, qMaxZ, minY, maxY), xPixels, yPixels, 0, 0, 1);
    }

    /**
     * Renders a single textured quad, either by itself or as part of a larger cuboid construction.
     * {@code vertices} must be a set of vertices, usually obtained through {@link #getXVertices(float, float, float, float, float, float)}, {@link #getYVertices(float, float, float, float, float, float)}, or {@link #getZVertices(float, float, float, float, float, float)}. Parameters are (x, y, z, u, v, normalSign) for each vertex.
     * (normalX, normalY, normalZ) are the normal vectors (positive), for the quad. For example, for an X quad, this will be (1, 0, 0).
     *
     * @param vertices The vertices.
     * @param uSize    The horizontal (u) texture size of the quad, in pixels.
     * @param vSize    The vertical (v) texture size of the quad, in pixels.
     */
    public static void renderTexturedQuads(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, float[][] vertices, float uSize, float vSize, float normalX, float normalY, float normalZ)
    {
        for (float[] v : vertices)
        {
            renderTexturedVertex(poseStack, buffer, packedLight, packedOverlay, v[0], v[1], v[2], sprite.getU(v[3] * uSize), sprite.getV(v[4] * vSize), v[5] * normalX, v[5] * normalY, v[5] * normalZ);
        }
    }

    /**
     * Renders a single vertex as part of a quad.
     * <ul>
     *     <li>(x, y, z) describe the position of the vertex.</li>
     *     <li>(u, v) describe the texture coordinates, typically will be a number of pixels (i.e. 16x something)</li>
     *     <li>(normalX, normalY, normalZ) describe the normal vector to the quad.</li>
     * </ul>
     */
    public static void renderTexturedVertex(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ)
    {
        final float shade = getShade(normalX, normalY, normalZ);
        buffer.vertex(poseStack.last().pose(), x, y, z)
            .color(shade, shade, shade, 1f)
            .uv(u, v)
            .uv2(packedLight)
            .overlayCoords(packedOverlay)
            .normal(poseStack.last().normal(), normalX, normalY, normalZ)
            .endVertex();
    }

    /**
     * Converts a potentially angled normal into the 'nearest' directional step. Could potentially be reimplemented as an inverse lerp.
     */
    public static float getShade(float normalX, float normalY, float normalZ)
    {
        return getShadeForStep(Math.round(normalX), Math.round(normalY), Math.round(normalZ));
    }

    /**
     * Returns the static diffuse shade by MC for each directional face. The color value of a vertex should be multiplied by this.
     * Reimplements {@link net.minecraft.client.multiplayer.ClientLevel#getShade(Direction, boolean)}
     */
    public static float getShadeForStep(int normalX, int normalY, int normalZ)
    {
        if (normalY == 1) return 1f;
        if (normalY == -1) return 0.5f;
        if (normalZ != 0) return 0.8f;
        if (normalX != 0) return 0.6f;
        return 1f;
    }

    /**
     * <pre>
     *  O------P.  ^ y
     *  |`.    | `.|
     *  |  `O--+---P--> x
     *  |   |  |   |
     *  O---+--P.  |
     *   `. |    `.|
     *     `O------P
     * </pre>
     *
     * @return A collection of vertices for two parallel faces of a cube, facing outwards, defined by (minX, minY, minZ) x (maxX, maxY, maxZ). Or the faces O and P in the above art
     */
    public static float[][] getXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, minY, minZ, 0, 1, 1}, // +X
            {minX, minY, maxZ, 1, 1, 1},
            {minX, maxY, maxZ, 1, 0, 1},
            {minX, maxY, minZ, 0, 0, 1},

            {maxX, minY, maxZ, 1, 0, -1}, // -X
            {maxX, minY, minZ, 0, 0, -1},
            {maxX, maxY, minZ, 0, 1, -1},
            {maxX, maxY, maxZ, 1, 1, -1}
        };
    }

    /**
     * <pre>
     *  O------O.  ^ y
     *  |`.    | `.|
     *  |  `O--+---O--> x
     *  |   |  |   |
     *  P---+--P.  |
     *   `. |    `.|
     *     `P------P
     * </pre>
     *
     * @return A collection of vertices for two parallel faces of a cube, facing outwards, defined by (minX, minY, minZ) x (maxX, maxY, maxZ). Or the faces O and P in the above art
     */
    public static float[][] getYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, maxY, minZ, 0, 1, 1}, // +Y
            {minX, maxY, maxZ, 1, 1, 1},
            {maxX, maxY, maxZ, 1, 0, 1},
            {maxX, maxY, minZ, 0, 0, 1},

            {minX, minY, maxZ, 1, 0, -1}, // -Y
            {minX, minY, minZ, 0, 0, -1},
            {maxX, minY, minZ, 0, 1, -1},
            {maxX, minY, maxZ, 1, 1, -1}
        };
    }

    /**
     * <pre>
     *  O------O.  ^ y
     *  |`.    | `.|
     *  |  `P--+---P--> x
     *  |   |  |   |
     *  O---+--O.  |
     *   `. |    `.|
     *     `P------P
     * </pre>
     *
     * @return A collection of vertices for two parallel faces of a cube, facing outwards, defined by (minX, minY, minZ) x (maxX, maxY, maxZ). Or the faces O and P in the above art
     */
    public static float[][] getZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {maxX, minY, minZ, 0, 1, 1}, // +Z
            {minX, minY, minZ, 1, 1, 1},
            {minX, maxY, minZ, 1, 0, 1},
            {maxX, maxY, minZ, 0, 0, 1},

            {minX, minY, maxZ, 1, 0, -1}, // -Z
            {maxX, minY, maxZ, 0, 0, -1},
            {maxX, maxY, maxZ, 0, 1, -1},
            {minX, maxY, maxZ, 1, 1, -1}
        };
    }

    /**
     * <pre>
     *  P------P.  ^ y
     *  |`.    | `.|
     *  |  `+--+---+--> x
     *  |   |  |   |
     *  +---+--+.  |
     *   `. |    `.|
     *     `P------P
     * </pre>
     *
     * @return A collection of vertices for both sides of one of the diagonal faces of a cube defined by (minX, minY, minZ) x (maxX, maxY, maxZ). Or both sides of the face defined by vertices P in the above art.
     */
    public static float[][] getDiagonalPlaneVertices(float x1, float y1, float z1, float x2, float y2, float z2, float u1, float v1, float u2, float v2)
    {
        return new float[][] {
            {x1, y1, z1, u1, v1},
            {x2, y1, z1, u2, v1},
            {x2, y2, z2, u2, v2},
            {x1, y2, z2, u1, v2},

            {x2, y1, z1, u2, v1},
            {x1, y1, z1, u1, v1},
            {x1, y2, z2, u1, v2},
            {x2, y2, z2, u2, v2}
        };
    }

    /**
     * <pre>
     *  Q------Q.  ^ y
     *  |`.    | `.|
     *  |  `Q--+---Q--> x = maxY
     *  |   |  |   |
     *  P---+--P.  |
     *   `. |    `.|
     *     `P------P = minY
     * </pre>
     *
     * @return A collection of vertices for the positive and negative X outward faces of the above trapezoidal cuboid, defined by the plane P, and the plane Q, minY, and maxY.
     */
    public static float[][] getTrapezoidalCuboidXVertices(float pMinX, float pMaxX, float pMinZ, float pMaxZ, float qMinX, float qMaxX, float qMinZ, float qMaxZ, float minY, float maxY)
    {
        return new float[][] {
            {pMinX, minY, pMinZ, 0, 1, 1}, // +X
            {pMinX, minY, pMaxZ, 1, 1, 1},
            {qMinX, maxY, qMaxZ, 1, 0, 1},
            {qMinX, maxY, qMinZ, 0, 0, 1},

            {pMaxX, minY, pMaxZ, 1, 0, -1}, // -X
            {pMaxX, minY, pMinZ, 0, 0, -1},
            {qMaxX, maxY, qMinZ, 0, 1, -1},
            {qMaxX, maxY, qMaxZ, 1, 1, -1},
        };
    }

    /**
     * <pre>
     *  Q------Q.  ^ y
     *  |`.    | `.|
     *  |  `Q--+---Q--> x = maxY
     *  |   |  |   |
     *  P---+--P.  |
     *   `. |    `.|
     *     `P------P = minY
     * </pre>
     *
     * @return A collection of vertices for the positive and negative Y outward faces of the above trapezoidal cuboid, defined by the plane P, and the plane Q, minY, and maxY.
     */
    public static float[][] getTrapezoidalCuboidYVertices(float pMinX, float pMaxX, float pMinZ, float pMaxZ, float qMinX, float qMaxX, float qMinZ, float qMaxZ, float minY, float maxY)
    {
        return new float[][] {
            {qMinX, maxY, qMinZ, 0, 1, 1}, // +Y
            {qMinX, maxY, qMaxZ, 1, 1, 1},
            {qMaxX, maxY, qMaxZ, 1, 0, 1},
            {qMaxX, maxY, qMinZ, 0, 0, 1},

            {pMinX, minY, pMaxZ, 1, 0, -1}, // -Y
            {pMinX, minY, pMinZ, 0, 0, -1},
            {pMaxX, minY, pMinZ, 0, 1, -1},
            {pMaxX, minY, pMaxZ, 1, 1, -1},
        };
    }

    /**
     * <pre>
     *  Q------Q.  ^ y
     *  |`.    | `.|
     *  |  `Q--+---Q--> x = maxY
     *  |   |  |   |
     *  P---+--P.  |
     *   `. |    `.|
     *     `P------P = minY
     * </pre>
     *
     * @return A collection of vertices for the positive and negative X outward faces of the above trapezoidal cuboid, defined by the plane P, and the plane Q, minY, and maxY.
     */
    public static float[][] getTrapezoidalCuboidZVertices(float pMinX, float pMaxX, float pMinZ, float pMaxZ, float qMinX, float qMaxX, float qMinZ, float qMaxZ, float minY, float maxY)
    {
        return new float[][] {
            {pMaxX, minY, pMinZ, 0, 1, 1}, // +Z
            {pMinX, minY, pMinZ, 1, 1, 1},
            {qMinX, maxY, qMinZ, 1, 0, 1},
            {qMaxX, maxY, qMinZ, 0, 0, 1},

            {pMinX, minY, pMaxZ, 1, 0, -1}, // -Z
            {pMaxX, minY, pMaxZ, 0, 0, -1},
            {qMaxX, maxY, qMaxZ, 0, 1, -1},
            {qMinX, maxY, qMaxZ, 1, 1, -1}
        };
    }

    public static void setShaderColor(int color)
    {
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);
    }

    // todo 1.19.3: inline and remove
    public static Quaternion rotateDegreesX(float degrees)
    {
        return Vector3f.XP.rotationDegrees(degrees);
    }

    public static Quaternion rotateDegreesY(float degrees)
    {
        return Vector3f.YP.rotationDegrees(degrees);
    }

    public static Quaternion rotateDegreesZ(float degrees)
    {
        return Vector3f.ZP.rotationDegrees(degrees);
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
        poseStack.mulPose(rotateDegreesX(Mth.clamp(tilt, 0.3F, 0.51F) * 85.0F));
        if (!mc.player.isInvisible())
        {
            poseStack.pushPose();
            poseStack.mulPose(rotateDegreesY(90.0F));
            renderMapHand(mc, poseStack, source, combinedLight, HumanoidArm.RIGHT);
            renderMapHand(mc, poseStack, source, combinedLight, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        addSiftingMovement(mc.player, poseStack); // tfc: rotate the pan due to sifting
        poseStack.mulPose(rotateDegreesX(Mth.sin(swingSqrt * (float) Math.PI) * 20.0F));
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

    public static ModelPart bakeSimple(EntityRendererProvider.Context ctx, String layerName)
    {
        return ctx.bakeLayer(modelIdentifier(layerName));
    }

    public static float itemTimeRotation()
    {
        return (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
    }

    public static int getFluidColor(FluidStack fluid)
    {
        return getFluidColor(fluid.getFluid());
    }

    public static int getFluidColor(Fluid fluid)
    {
        return fluid.getAttributes().getColor();
    }

    public static void renderFluidFace(PoseStack poseStack, FluidStack fluidStack, MultiBufferSource buffer, float minX, float minZ, float maxX, float maxZ, float y, int combinedOverlay, int combinedLight)
    {
        renderFluidFace(poseStack, fluidStack, buffer, getFluidColor(fluidStack), minX, minZ, maxX, maxZ, y, combinedOverlay, combinedLight);
    }

    public static void renderFluidFace(PoseStack poseStack, FluidStack fluidStack, MultiBufferSource buffer, int color, float minX, float minZ, float maxX, float maxZ, float y, int combinedOverlay, int combinedLight)
    {
        Fluid fluid = fluidStack.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation texture = attributes.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(texture);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentCull(RenderHelpers.BLOCKS_ATLAS));
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

    public static TextureAtlasSprite getAndBindFluidSprite(FluidStack fluid)
    {
        setShaderColor(getFluidColor(fluid));
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluid.getFluid().getAttributes().getStillTexture(fluid));
    }

    /** @deprecated Use the updated version below. */
    @Deprecated(forRemoval = true)
    public static void fillAreaWithSprite(int left, int top, TextureAtlasSprite sprite, PoseStack poseStack, int startX, int endX, int endY, int fillHeight)
    {
        fillAreaWithSprite(poseStack, sprite, left + startX, top + endY - fillHeight, endX - startX, fillHeight, 16, 16);
    }

    /**
     * Renders a solid rectangle over the region {@code [x, y] x [x + width, y + height]}, composed of the given sprite. The sprite is assumed to have a regular width and height of {@code spriteWidth x spriteHeight}. This will tile the given texture as many times as necessary to cover the region.
     */
    public static void fillAreaWithSprite(PoseStack stack, TextureAtlasSprite sprite, int x, int y, int regionWidth, int regionHeight, int spriteWidth, int spriteHeight)
    {
        final int tileWidth = Helpers.ceilDiv(regionWidth, spriteWidth);
        final int tileHeight = Helpers.ceilDiv(regionHeight, spriteHeight);

        for (int tileX = 0; tileX < tileWidth; tileX++)
        {
            for (int tileY = 0; tileY < tileHeight; tileY++)
            {
                // Top left (x, y) coordinate of this tile to be drawn
                final int offsetX = tileX * spriteWidth;
                final int offsetY = tileY * spriteHeight;

                // The actual (width, height) pair of this tile, cut off by the region bounds, which are not an exact multiple of tile (width, height)
                final int actualWidth = Math.min(spriteWidth, regionWidth - offsetX);
                final int actualHeight = Math.min(spriteHeight, regionHeight - offsetY);

                // The fraction in [0, 1] x [0, 1] of this tile that needs to be drawn
                final float widthRatio = (float) actualWidth / spriteWidth;
                final float heightRatio = (float) actualHeight / spriteHeight;

                blit(stack, x + offsetX, y + offsetY, actualWidth, actualHeight, sprite.getU0(), sprite.getU(16 * widthRatio), sprite.getV0(), sprite.getV(16 * heightRatio));
            }
        }
    }

    /**
     * Copied from {@link GuiComponent#blit(PoseStack, int, int, int, int, int, TextureAtlasSprite)} but with explicit arguments for {@code minU, maxU, minV, maxV}.
     */
    public static void blit(PoseStack stack, int x, int y, int width, int height, float minU, float maxU, float minV, float maxV)
    {
        blit(stack.last().pose(), x, x + width, y, y + height, 0, minU, maxU, minV, maxV);
    }


    /**
     * Copied from {@link GuiComponent#innerBlit(Matrix4f, int, int, int, int, int, float, float, float, float)} because it's private.
     */
    public static void blit(Matrix4f pose, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        final BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(pose, x1, y2, blitOffset).uv(minU, maxV).endVertex();
        builder.vertex(pose, x2, y2, blitOffset).uv(maxU, maxV).endVertex();
        builder.vertex(pose, x2, y1, blitOffset).uv(maxU, minV).endVertex();
        builder.vertex(pose, x1, y1, blitOffset).uv(minU, minV).endVertex();
        builder.end();
        BufferUploader.end(builder);
    }

    public static Button.OnTooltip makeButtonTooltip(Screen screen, Component component)
    {
        return new Button.OnTooltip()
        {
            @Override
            public void onTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY)
            {
                screen.renderTooltip(poseStack, component, mouseX, mouseY);
            }

            @Override
            public void narrateTooltip(Consumer<Component> consumer)
            {
                consumer.accept(component);
            }
        };
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
        poseStack.mulPose(rotateDegreesY(92.0F));
        poseStack.mulPose(rotateDegreesX(45.0F));
        poseStack.mulPose(rotateDegreesZ(side * -41.0F + calculateArmMovement(mc.player))); // tfc: jiggle the arms
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
