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

import net.dries007.tfc.common.entities.aquatic.BluegillEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class BluegillModel extends SegmentedModel<BluegillEntity>
{
	private final ModelRenderer backFins;
	private final ModelRenderer backFin2;
	private final ModelRenderer backFin1;
	private final ModelRenderer body;
	private final ModelRenderer front;

	public BluegillModel()
    {
		texHeight = 16;
		texWidth = 16;

		backFins = new ModelRenderer(this);
		backFins.setPos(0.0F, 22.0F, 0.0F);
		backFins.texOffs(4, 7).addBox(-0.5F, -1.5F, -3.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

		backFin2 = new ModelRenderer(this);
		backFin2.setPos(0.0F, 1.0F, -5.0F);
		backFins.addChild(backFin2);
		setRotationAngle(backFin2, -0.7854F, 0.0F, 0.0F);
		backFin2.texOffs(8, 0).addBox(0.0F, -3.658F, 0.9397F, 0.0F, 3.0F, 1.0F, 0.0F, false);

		backFin1 = new ModelRenderer(this);
		backFin1.setPos(0.0F, 1.0F, -2.0F);
		backFins.addChild(backFin1);
		setRotationAngle(backFin1, 0.7854F, 0.0F, 0.0F);
		backFin1.texOffs(0, 9).addBox(0.0F, -3.658F, 0.9397F, 0.0F, 3.0F, 1.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setPos(0.0F, 21.0F, 0.0F);
		body.texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 4.0F, 0.0F, false);
		body.texOffs(0, 4).addBox(0.0F, -2.0F, -1.0F, 0.0F, 1.0F, 3.0F, 0.0F, false);
		body.texOffs(0, 2).addBox(0.0F, 2.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);
		body.texOffs(2, 2).addBox(0.0F, 2.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);

		front = new ModelRenderer(this);
		front.setPos(0.0F, 22.0F, 2.0F);
		front.texOffs(0, 0).addBox(-0.5F, -1.5F, 1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		front.texOffs(0, 8).addBox(-0.5F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
	}

    @Override
    public void setupAnim(BluegillEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float speed = entityIn.isInWater() ? 1.0F : 1.5F;

        backFins.yRot = -speed * 0.45F * MathHelper.sin(0.6F * ageInTicks);
    }

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(body, front, backFins);
    }
}