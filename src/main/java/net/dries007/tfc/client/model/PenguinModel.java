/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.dries007.tfc.common.entities.aquatic.PenguinEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class PenguinModel extends SegmentedModel<PenguinEntity>
{
    private final ModelRenderer core;
    private final ModelRenderer head;
    private final ModelRenderer leftfoot;
    private final ModelRenderer rightfoot;
    private final ModelRenderer leftwing;
    private final ModelRenderer rightwing;

	public PenguinModel()
    {
        texWidth = 32;
		texHeight = 32;

        core = new ModelRenderer(this);
        core.setPos(0.0F, 23.0F, 1.0F);
        core.texOffs(0, 0).addBox(-2.0F, -8.0F, -3.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -8.0F, 0.0F);
        core.addChild(head);
        head.texOffs(13, 9).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
        head.texOffs(16, 4).addBox(-1.0F, -1.0F, 0.5F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        leftfoot = new ModelRenderer(this);
        leftfoot.setPos(-1.0F, 0.0F, -1.0F);
        core.addChild(leftfoot);
        setRotationAngle(leftfoot, 0.0F, -0.1745F, 0.0F);
        leftfoot.texOffs(12, 0).addBox(-1.3264F, 0.0F, -0.0152F, 2.0F, 1.0F, 3.0F, 0.0F, false);

        rightfoot = new ModelRenderer(this);
        rightfoot.setPos(2.0F, 0.0F, -1.0F);
        core.addChild(rightfoot);
        setRotationAngle(rightfoot, 0.0F, 0.1745F, 0.0F);
        rightfoot.texOffs(16, 16).addBox(-1.6736F, 0.0F, -0.0152F, 2.0F, 1.0F, 3.0F, 0.0F, false);

        leftwing = new ModelRenderer(this);
        leftwing.setPos(-2.0F, -7.0F, -1.0F);
        core.addChild(leftwing);
        setRotationAngle(leftwing, -0.0873F, 0.0F, 0.1745F);
        leftwing.texOffs(0, 12).addBox(-0.7287F, 0.0972F, -0.904F, 1.0F, 5.0F, 3.0F, 0.0F, false);

        rightwing = new ModelRenderer(this);
        rightwing.setPos(2.0F, -7.0F, -1.0F);
        core.addChild(rightwing);
        setRotationAngle(rightwing, -0.0873F, 0.0F, -0.1745F);
        rightwing.texOffs(8, 12).addBox(-0.1888F, 0.154F, -0.899F, 1.0F, 5.0F, 3.0F, 0.0F, false);
	}

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(core);
    }

    @Override
	public void setupAnim(PenguinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float swing = MathHelper.cos((entity.isInWater() ? 1.2F : 1.0F) * limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        head.xRot = entity.isInWater() ? 1 : headPitch * ((float)Math.PI / 180F);
        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        rightwing.zRot = swing;
        leftwing.zRot = -swing;
        leftfoot.xRot = 0.5F * swing;
        rightfoot.xRot = -0.5F * swing;
        core.zRot = entity.isInWater() ? 0.0F : 0.3F * MathHelper.triangleWave(limbSwing * 0.2F, 2.0F);
	}

	@Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
    {
        if (young)
        {
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.translate(0.0F, 1.5F, 0.0F);
        }
        super.renderToBuffer(matrixStack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
}