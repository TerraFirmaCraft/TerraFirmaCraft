package net.dries007.tfc.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.common.entities.VultureEntity;

public class VultureModel extends SegmentedModel<VultureEntity>
{
    private final ModelRenderer main;
    private final ModelRenderer legs;
    private final ModelRenderer tail;
    private final ModelRenderer wingRight1;
    private final ModelRenderer wingLeft1;
    private final ModelRenderer head;

    public VultureModel()
    {
        texHeight = 64;
        texWidth = 64;

        main = new ModelRenderer(this);
        main.setPos(0.0F, 24.0F, 0.0F);
        main.texOffs(0, 0).addBox(-3.0F, -5.0F, -5.0F, 5.0F, 4.0F, 11.0F, 0.0F, false);

        ModelRenderer neck_r1 = new ModelRenderer(this);
        neck_r1.setPos(-0.5F, -3.75F, -5.0F);
        main.addChild(neck_r1);
        setRotationAngle(neck_r1, 0.1745F, 0.0F, 0.0F);
        neck_r1.texOffs(20, 31).addBox(-2.0F, -1.75F, -3.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        legs = new ModelRenderer(this);
        legs.setPos(0.0F, -2.0F, 4.0F);
        main.addChild(legs);


        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 1.0F, 0.0F);
        legs.addChild(cube_r1);
        setRotationAngle(cube_r1, 1.0036F, 0.0F, 0.0F);
        cube_r1.texOffs(0, 36).addBox(-4.0F, 0.0F, 0.0F, 7.0F, 5.0F, 0.0F, 0.0F, false);

        tail = new ModelRenderer(this);
        tail.setPos(-0.5F, -5.0F, 6.0F);
        main.addChild(tail);
        tail.texOffs(33, 40).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 3.0F, 0.0F, false);
        tail.texOffs(23, 15).addBox(-1.5F, 0.0F, 3.0F, 3.0F, 0.0F, 7.0F, 0.0F, false);
        tail.texOffs(17, 15).addBox(-3.5F, 0.0F, 0.0F, 2.0F, 0.0F, 9.0F, 0.0F, false);
        tail.texOffs(13, 15).addBox(1.5F, 0.0F, 0.0F, 2.0F, 0.0F, 9.0F, 0.0F, false);

        wingRight1 = new ModelRenderer(this);
        wingRight1.setPos(-3.0F, -5.0F, 0.0F);
        main.addChild(wingRight1);
        wingRight1.texOffs(22, 24).addBox(-6.0F, 0.0F, -5.0F, 6.0F, 2.0F, 5.0F, 0.0F, false);
        wingRight1.texOffs(39, 23).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 1.0F, 2.0F, 0.0F, false);
        wingRight1.texOffs(32, 19).addBox(-6.0F, 0.0F, 2.0F, 6.0F, 0.0F, 4.0F, 0.0F, false);

        ModelRenderer wingRight2 = new ModelRenderer(this);
        wingRight2.setPos(-6.0F, 0.0F, 0.0F);
        wingRight1.addChild(wingRight2);
        wingRight2.texOffs(42, 9).addBox(-3.0F, 0.0F, -5.0F, 3.0F, 2.0F, 3.0F, 0.0F, false);
        wingRight2.texOffs(10, 37).addBox(-3.0F, 0.0F, -2.0F, 3.0F, 1.0F, 4.0F, 0.0F, false);
        wingRight2.texOffs(30, 3).addBox(-8.0F, 0.0F, -5.0F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        wingRight2.texOffs(0, 4).addBox(-3.0F, 0.0F, 2.0F, 3.0F, 0.0F, 4.0F, 0.0F, false);
        wingRight2.texOffs(31, 14).addBox(-8.0F, 0.0F, 0.0F, 5.0F, 0.0F, 5.0F, 0.0F, false);
        wingRight2.texOffs(0, 15).addBox(-15.0F, 0.0F, -5.0F, 7.0F, 0.0F, 8.0F, 0.0F, false);

        wingLeft1 = new ModelRenderer(this);
        wingLeft1.setPos(2.0F, -5.0F, 0.0F);
        main.addChild(wingLeft1);
        wingLeft1.texOffs(0, 23).addBox(0.0F, 0.0F, -5.0F, 6.0F, 2.0F, 5.0F, 0.0F, false);
        wingLeft1.texOffs(35, 0).addBox(0.0F, 0.0F, 0.0F, 6.0F, 1.0F, 2.0F, 0.0F, false);
        wingLeft1.texOffs(28, 31).addBox(0.0F, 0.0F, 2.0F, 6.0F, 0.0F, 4.0F, 0.0F, false);

        ModelRenderer wingLeft2 = new ModelRenderer(this);
        wingLeft2.setPos(6.0F, 0.0F, 0.0F);
        wingLeft1.addChild(wingLeft2);
        wingLeft2.texOffs(0, 41).addBox(0.0F, 0.0F, -5.0F, 3.0F, 2.0F, 3.0F, 0.0F, false);
        wingLeft2.texOffs(32, 35).addBox(0.0F, 0.0F, -2.0F, 3.0F, 1.0F, 4.0F, 0.0F, false);
        wingLeft2.texOffs(0, 30).addBox(3.0F, 0.0F, -5.0F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        wingLeft2.texOffs(0, 0).addBox(0.0F, 0.0F, 2.0F, 3.0F, 0.0F, 4.0F, 0.0F, false);
        wingLeft2.texOffs(27, 9).addBox(3.0F, 0.0F, 0.0F, 5.0F, 0.0F, 5.0F, 0.0F, false);
        wingLeft2.texOffs(13, 0).addBox(8.0F, 0.0F, -5.0F, 7.0F, 0.0F, 8.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -4.0F, -4.0F);
        main.addChild(head);
        head.texOffs(21, 39).addBox(-2.0F, -0.75F, -6.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        head.texOffs(0, 6).addBox(-1.0F, 0.25F, -8.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        head.texOffs(0, 0).addBox(-1.0F, 0.75F, -9.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
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
        return ImmutableList.of(main);
    }

    @Override
    public void setupAnim(VultureEntity vulture, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float oscillation = -0.9F * MathHelper.sin(0.09F * ageInTicks);
        boolean dive = headPitch < -30 && headPitch > -60;
        wingLeft1.zRot = dive ? 1.0F : oscillation;
        wingRight1.zRot = dive ? -1.0F : -1 * oscillation;
        tail.xRot = 0.5F * oscillation;
    }
}
