/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blocks.wood.AbstractSignBlock;
import net.dries007.tfc.common.blocks.wood.TFCStandingSignBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;

import static net.minecraft.client.renderer.Sheets.SIGN_SHEET;

// todo: custom SignEditScreen
public class TFCSignBlockEntityRenderer extends SignRenderer
{
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);

    private static Material createSignMaterial(String name)
    {
        return new Material(SIGN_SHEET, Helpers.identifier("entity/signs/" + name.toLowerCase(Locale.ROOT)));
    }

    private static int getDarkColor(SignBlockEntity sign)
    {
        int i = sign.getColor().getTextColor();
        int j = (int) ((double) NativeImage.getR(i) * 0.4D);
        int k = (int) ((double) NativeImage.getG(i) * 0.4D);
        int l = (int) ((double) NativeImage.getB(i) * 0.4D);
        return i == DyeColor.BLACK.getTextColor() && sign.hasGlowingText() ? -988212 : NativeImage.combine(0, l, k, j);
    }

    private static boolean isOutlineVisible(SignBlockEntity sing, int dyeIndex)
    {
        if (dyeIndex == DyeColor.BLACK.getTextColor())
        {
            return true;
        }
        else
        {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localplayer = minecraft.player;
            if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping())
            {
                return true;
            }
            else
            {
                Entity entity = minecraft.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(sing.getBlockPos())) < (double) OUTLINE_RENDER_DISTANCE;
            }
        }
    }
    private final Font font;
    private final Map<Wood, Material> materials;
    private final Map<Wood, SignModel> models;

    public TFCSignBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.font = context.getFont();
        this.materials = Arrays.stream(Wood.VALUES).collect(ImmutableMap.toImmutableMap(Functions.identity(), wood -> createSignMaterial(wood.name().toLowerCase(Locale.ROOT))));
        this.models = Arrays.stream(Wood.VALUES).collect(ImmutableMap.toImmutableMap(Functions.identity(), wood -> new SignModel(context.bakeLayer(RenderHelpers.modelIdentifier("sign/" + wood.name().toLowerCase(Locale.ROOT))))));
    }

    public void render(SignBlockEntity sign, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight, int overlay)
    {
        BlockState state = sign.getBlockState();
        poseStack.pushPose();
        float scale = 0.6666667F;
        SignModel model = models.get(((AbstractSignBlock) state.getBlock()).getWood());
        if (state.getBlock() instanceof TFCStandingSignBlock)
        {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            float yRot = -((float) (state.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
            model.stick.visible = true;
        }
        else
        {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            float yRot = -state.getValue(WallSignBlock.FACING).toYRot();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
            poseStack.translate(0.0D, -0.3125D, -0.4375D);
            model.stick.visible = false;
        }

        poseStack.pushPose();
        poseStack.scale(scale, -scale, -scale);
        Material material = materials.get(((AbstractSignBlock) state.getBlock()).getWood());
        VertexConsumer vertexconsumer = material.buffer(source, model::renderType);
        model.root.render(poseStack, vertexconsumer, packedLight, overlay);
        poseStack.popPose();

        float rescale = 0.010416667F;

        poseStack.translate(0.0D, 0.33333334F, 0.046666667F);
        poseStack.scale(rescale, -rescale, rescale);

        int darkColor = getDarkColor(sign);
        FormattedCharSequence[] lines = sign.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), component -> {
            List<FormattedCharSequence> list = this.font.split(component, 90);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });
        int textColor;
        boolean outline;
        int totalLight;
        if (sign.hasGlowingText())
        {
            textColor = sign.getColor().getTextColor();
            outline = isOutlineVisible(sign, textColor);
            totalLight = 15728880;
        }
        else
        {
            textColor = darkColor;
            outline = false;
            totalLight = packedLight;
        }

        for (int i1 = 0; i1 < 4; ++i1)
        {
            FormattedCharSequence formattedcharsequence = lines[i1];
            float f3 = (float) (-this.font.width(formattedcharsequence) / 2);
            if (outline)
            {
                this.font.drawInBatch8xOutline(formattedcharsequence, f3, (float) (i1 * 10 - 20), textColor, darkColor, poseStack.last().pose(), source, totalLight);
            }
            else
            {
                this.font.drawInBatch(formattedcharsequence, f3, (float) (i1 * 10 - 20), textColor, false, poseStack.last().pose(), source, false, 0, totalLight);
            }
        }

        poseStack.popPose();
    }
}
