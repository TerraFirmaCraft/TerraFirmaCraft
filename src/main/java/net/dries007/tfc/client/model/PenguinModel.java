package net.dries007.tfc.client.model;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TurtleEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class PenguinModel extends SegmentedModel<TurtleEntity>
{
	private final ModelRenderer rightwing;
	private final ModelRenderer leftwing;
	private final ModelRenderer rightfoot;
	private final ModelRenderer leftfoot;
	private final ModelRenderer head;
	private final ModelRenderer bb_main;

	public PenguinModel()
    {
        texWidth = 32;
		texHeight = 32;

		rightwing = new ModelRenderer(this);
		rightwing.setPos(2.0F, 16.0F, 0.0F);
		setRotationAngle(rightwing, -0.0873F, 0.0F, -0.1745F);
		rightwing.texOffs(8, 12).addBox(-0.1888F, 0.154F, -0.899F, 1.0F, 5.0F, 3.0F, 0.0F, false);

		leftwing = new ModelRenderer(this);
		leftwing.setPos(-2.0F, 16.0F, 0.0F);
		setRotationAngle(leftwing, -0.0873F, 0.0F, 0.1745F);
		leftwing.texOffs(0, 12).addBox(-0.7287F, 0.0972F, -0.904F, 1.0F, 5.0F, 3.0F, 0.0F, false);

		rightfoot = new ModelRenderer(this);
		rightfoot.setPos(2.0F, 23.0F, 0.0F);
		setRotationAngle(rightfoot, 0.0F, 0.1745F, 0.0F);
		rightfoot.texOffs(16, 16).addBox(-1.6736F, 0.0F, -0.0152F, 2.0F, 1.0F, 3.0F, 0.0F, false);

		leftfoot = new ModelRenderer(this);
		leftfoot.setPos(-1.0F, 23.0F, 0.0F);
		setRotationAngle(leftfoot, 0.0F, -0.1745F, 0.0F);
		leftfoot.texOffs(12, 0).addBox(-1.3264F, 0.0F, -0.0152F, 2.0F, 1.0F, 3.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setPos(0.0F, 15.0F, 1.0F);
		head.texOffs(13, 9).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
		head.texOffs(16, 4).addBox(-1.0F, -1.0F, 0.5F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		bb_main = new ModelRenderer(this);
		bb_main.setPos(0.0F, 24.0F, 0.0F);
		bb_main.texOffs(0, 0).addBox(-2.0F, -9.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
	}

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(rightwing, leftwing, rightfoot, leftfoot, head, bb_main);
    }

    @Override
	public void setupAnim(TurtleEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (entity.isVisuallySwimming())
        {
            //something
        }
		//previously the render function, render code was moved to a method below
	}
}