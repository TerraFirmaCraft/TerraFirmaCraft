package net.dries007.tfc.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.common.entities.aquatic.SeafloorCritterEntity;

public class LobsterModel extends SegmentedModel<SeafloorCritterEntity>
{
    private final ModelRenderer body;
    private final ModelRenderer tail1;
    private final ModelRenderer leftAntenna;
    private final ModelRenderer rightAntenna;
    private final ModelRenderer armLeft;
    private final ModelRenderer clawLeft;
    private final ModelRenderer clawBottomLeft;
    private final ModelRenderer clawTopLeft;
    private final ModelRenderer armRight;
    private final ModelRenderer clawRight;
    private final ModelRenderer clawBottomRight;
    private final ModelRenderer clawTopRight;
    private final ModelRenderer legsLeft;
    private final ModelRenderer legsRight;

    public LobsterModel()
    {
        texWidth = 32;
        texHeight = 32;

        body = new ModelRenderer(this);
        body.setPos(0.0F, 24.0F, 0.0F);
        body.texOffs(0, 0).addBox(-2.0F, -3.0F, -3.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

        tail1 = new ModelRenderer(this);
        tail1.setPos(-0.5F, -3.0F, 1.0F);
        body.addChild(tail1);
        setRotationAngle(tail1, 0.2618F, 0.0F, 0.0F);
        tail1.texOffs(0, 13).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        ModelRenderer tail2 = new ModelRenderer(this);
        tail2.setPos(0.0F, 0.0F, 2.0F);
        tail1.addChild(tail2);
        setRotationAngle(tail2, 0.3054F, 0.0F, 0.0F);
        tail2.texOffs(13, 5).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer tail3 = new ModelRenderer(this);
        tail3.setPos(0.0F, 0.0F, 2.0F);
        tail2.addChild(tail3);
        setRotationAngle(tail3, 0.3054F, 0.0F, 0.0F);
        tail3.texOffs(10, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 2.0F, 0.0F, false);

        leftAntenna = new ModelRenderer(this);
        leftAntenna.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(leftAntenna);


        ModelRenderer skinny_r1 = new ModelRenderer(this);
        skinny_r1.setPos(1.0F, -3.0F, -2.5F);
        leftAntenna.addChild(skinny_r1);
        setRotationAngle(skinny_r1, -0.6981F, 0.0F, 0.6109F);
        skinny_r1.texOffs(8, 14).addBox(0.0F, -4.75F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, false);

        rightAntenna = new ModelRenderer(this);
        rightAntenna.setPos(-1.0F, 0.0F, 0.0F);
        body.addChild(rightAntenna);


        ModelRenderer skinny_r2 = new ModelRenderer(this);
        skinny_r2.setPos(-1.0F, -3.0F, -2.5F);
        rightAntenna.addChild(skinny_r2);
        setRotationAngle(skinny_r2, -0.6981F, 0.0F, -0.6109F);
        skinny_r2.texOffs(0, 6).addBox(0.0F, -4.75F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, false);

        armLeft = new ModelRenderer(this);
        armLeft.setPos(1.0F, -2.0F, -3.0F);
        body.addChild(armLeft);
        setRotationAngle(armLeft, 0.0F, -0.5236F, 0.0F);
        armLeft.texOffs(10, 9).addBox(0.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);

        clawLeft = new ModelRenderer(this);
        clawLeft.setPos(0.0F, 0.0F, -3.0F);
        armLeft.addChild(clawLeft);
        setRotationAngle(clawLeft, 0.0F, 0.3054F, 0.0F);


        clawBottomLeft = new ModelRenderer(this);
        clawBottomLeft.setPos(0.0F, 0.0F, 0.0F);
        clawLeft.addChild(clawBottomLeft);
        setRotationAngle(clawBottomLeft, 0.2182F, 0.0F, 0.0F);
        clawBottomLeft.texOffs(14, 2).addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        clawTopLeft = new ModelRenderer(this);
        clawTopLeft.setPos(0.0F, 0.0F, 0.0F);
        clawLeft.addChild(clawTopLeft);
        setRotationAngle(clawTopLeft, -0.2618F, 0.0F, 0.0F);
        clawTopLeft.texOffs(14, 14).addBox(0.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        armRight = new ModelRenderer(this);
        armRight.setPos(-2.0F, -2.0F, -3.0F);
        body.addChild(armRight);
        setRotationAngle(armRight, 0.0F, 0.5236F, 0.0F);
        armRight.texOffs(0, 9).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);

        clawRight = new ModelRenderer(this);
        clawRight.setPos(0.0F, 0.0F, -3.0F);
        armRight.addChild(clawRight);
        setRotationAngle(clawRight, 0.0F, -0.3054F, 0.0F);


        clawBottomRight = new ModelRenderer(this);
        clawBottomRight.setPos(0.0F, 0.0F, 0.0F);
        clawRight.addChild(clawBottomRight);
        setRotationAngle(clawBottomRight, 0.2182F, 0.0F, 0.0F);
        clawBottomRight.texOffs(6, 12).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        clawTopRight = new ModelRenderer(this);
        clawTopRight.setPos(0.0F, 0.0F, 0.0F);
        clawRight.addChild(clawTopRight);
        setRotationAngle(clawTopRight, -0.2618F, 0.0F, 0.0F);
        clawTopRight.texOffs(10, 13).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        legsLeft = new ModelRenderer(this);
        legsLeft.setPos(1.0F, -1.0F, -1.0F);
        body.addChild(legsLeft);


        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        legsLeft.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 0.6109F);
        cube_r1.texOffs(4, 7).addBox(0.0F, 0.0F, -2.5F, 2.0F, 0.0F, 5.0F, 0.0F, false);

        legsRight = new ModelRenderer(this);
        legsRight.setPos(-2.0F, -1.0F, -1.0F);
        body.addChild(legsRight);


        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 0.0F, 0.0F);
        legsRight.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, -0.6109F);
        cube_r2.texOffs(0, 7).addBox(-2.0F, 0.0F, -2.5F, 2.0F, 0.0F, 5.0F, 0.0F, false);
    }


    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(SeafloorCritterEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float oscillation = 0.2F * MathHelper.cos(0.2F * ageInTicks);
        armLeft.xRot = oscillation;
        armRight.xRot = -1 * oscillation;
        tail1.xRot = oscillation * 0.5F;
        rightAntenna.zRot = oscillation * 0.1F;
        leftAntenna.zRot = oscillation * -0.1F;
        clawTopLeft.xRot = oscillation * 0.1F;
        clawTopRight.xRot = oscillation * -0.1F;
    }
}
