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

import net.dries007.tfc.common.entities.aquatic.ManateeEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class ManateeModel extends SegmentedModel<ManateeEntity>
{
    private final ModelRenderer head;
    private final ModelRenderer finright;
    private final ModelRenderer finleft;
    private final ModelRenderer body;
    private final ModelRenderer back;

    public ManateeModel()
    {
        texWidth = 128;
        texHeight = 128;

        head = new ModelRenderer(this);
        head.setPos(0.0F, 11.0F, 14.0F);
        setRotationAngle(head, -0.0873F, 0.0F, 0.0F);


        ModelRenderer head1_r1 = new ModelRenderer(this);
        head1_r1.setPos(0.0F, 11.0F, -14.0F);
        head.addChild(head1_r1);
        setRotationAngle(head1_r1, 2.967F, 0.0F, 3.1416F);
        head1_r1.texOffs(0, 69).addBox(-7.0F, -16.2658F, 8.9927F, 14.0F, 14.0F, 15.0F, 0.0F, false);

        ModelRenderer noseFront = new ModelRenderer(this);
        noseFront.setPos(0.0F, 0.7532F, 18.5568F);
        head.addChild(noseFront);
        setRotationAngle(noseFront, 0.0873F, 0.0F, 0.0F);


        ModelRenderer nosefront1_r1 = new ModelRenderer(this);
        nosefront1_r1.setPos(0.0F, 12.2468F, -32.5568F);
        noseFront.addChild(nosefront1_r1);
        setRotationAngle(nosefront1_r1, -3.1416F, 0.0F, 3.1416F);
        nosefront1_r1.texOffs(0, 98).addBox(-4.0F, -12.6943F, 30.2309F, 8.0F, 8.0F, 4.0F, 0.0F, false);

        ModelRenderer noseback = new ModelRenderer(this);
        noseback.setPos(0.0F, 8.0F, 5.0F);
        noseFront.addChild(noseback);
        setRotationAngle(noseback, -0.1745F, 0.0F, 0.0F);


        ModelRenderer noseback1_r1 = new ModelRenderer(this);
        noseback1_r1.setPos(0.0F, 4.2468F, -37.5568F);
        noseback.addChild(noseback1_r1);
        setRotationAngle(noseback1_r1, 2.7926F, 0.0F, 3.1416F);
        noseback1_r1.texOffs(64, 0).addBox(-5.0F, -12.3379F, 22.5985F, 10.0F, 10.0F, 9.0F, 0.0F, false);

        finright = new ModelRenderer(this);
        finright.setPos(-10.0F, 18.0F, 2.0F);
        setRotationAngle(finright, 0.0F, 0.0F, 0.3491F);


        ModelRenderer finright2_r1 = new ModelRenderer(this);
        finright2_r1.setPos(7.8278F, -0.318F, -2.0F);
        finright.addChild(finright2_r1);
        setRotationAngle(finright2_r1, 3.1416F, 0.0F, 2.4434F);
        finright2_r1.texOffs(96, 13).addBox(12.5464F, -4.2172F, -6.0F, 8.0F, 2.0F, 6.0F, 0.0F, false);
        finright2_r1.texOffs(0, 6).addBox(7.5464F, -4.2172F, -5.0F, 5.0F, 2.0F, 4.0F, 0.0F, false);

        finleft = new ModelRenderer(this);
        finleft.setPos(9.0F, 22.0F, 2.0F);
        setRotationAngle(finleft, 0.0F, 0.0F, -0.3491F);


        ModelRenderer finleft2_r1 = new ModelRenderer(this);
        finleft2_r1.setPos(-5.8619F, -3.795F, -2.0F);
        finleft.addChild(finleft2_r1);
        setRotationAngle(finleft2_r1, -3.1416F, 0.0F, -2.4434F);
        finleft2_r1.texOffs(0, 0).addBox(-11.8757F, -4.6785F, -5.0F, 5.0F, 2.0F, 4.0F, 0.0F, false);
        finleft2_r1.texOffs(93, 0).addBox(-19.8757F, -4.6785F, -6.0F, 8.0F, 2.0F, 6.0F, 0.0F, false);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 10.0F, 8.0F);


        ModelRenderer middleBody_r1 = new ModelRenderer(this);
        middleBody_r1.setPos(0.0F, 19.0F, -8.0F);
        body.addChild(middleBody_r1);
        setRotationAngle(middleBody_r1, -3.1416F, 0.0F, 3.1416F);
        middleBody_r1.texOffs(0, 0).addBox(-11.0F, -26.0F, -16.0F, 22.0F, 21.0F, 20.0F, 0.0F, false);
        middleBody_r1.texOffs(58, 71).addBox(-9.0F, -23.0F, 4.0F, 18.0F, 17.0F, 10.0F, 0.0F, false);

        back = new ModelRenderer(this);
        back.setPos(0.0F, 15.0F, 9.0F);

        ModelRenderer back1_r1 = new ModelRenderer(this);
        back1_r1.setPos(0.0F, 14.0F, -9.0F);
        back.addChild(back1_r1);
        setRotationAngle(back1_r1, -3.1416F, 0.0F, 3.1416F);
        back1_r1.texOffs(0, 41).addBox(-9.0F, -21.993F, -24.7425F, 18.0F, 13.0F, 15.0F, 0.0F, false);

        ModelRenderer butt = new ModelRenderer(this);
        butt.setPos(0.0F, -5.0266F, -33.6101F);
        back.addChild(butt);
        setRotationAngle(butt, 0.0873F, 0.0F, 0.0F);


        ModelRenderer butt1_r1 = new ModelRenderer(this);
        butt1_r1.setPos(0.0F, 14.0266F, 24.6101F);
        butt.addChild(butt1_r1);
        setRotationAngle(butt1_r1, -2.967F, 0.0F, -3.1416F);
        butt1_r1.texOffs(69, 26).addBox(-6.0F, -14.8234F, -34.0654F, 12.0F, 9.0F, 15.0F, 0.0F, false);

        ModelRenderer tail = new ModelRenderer(this);
        tail.setPos(0.0F, -4.0F, -6.0F);
        butt.addChild(tail);
        setRotationAngle(tail, 0.1745F, 0.0F, 0.0F);


        ModelRenderer tail1_r1 = new ModelRenderer(this);
        tail1_r1.setPos(0.0F, 18.0266F, 30.6101F);
        tail.addChild(tail1_r1);
        setRotationAngle(tail1_r1, -2.618F, 0.0F, -3.1416F);
        tail1_r1.texOffs(49, 52).addBox(-9.0F, -9.7884F, -47.0594F, 18.0F, 2.0F, 17.0F, 0.0F, false);
    }


    @Override
    public void setupAnim(ManateeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float swingRate = entity.isInWater() ? 1.0F : 1.5F;
        float oscillation = -1 * swingRate * 0.2F * MathHelper.sin(0.06F * ageInTicks);
        finright.zRot = oscillation * -2F;
        finleft.zRot = oscillation * 2F;
        head.xRot = oscillation * 0.5F;
        back.xRot = oscillation * 0.5F;
    }

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(head, finright, finleft, body, back);
    }
}
