package net.dries007.tfc.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.common.entities.aquatic.OrcaEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class OrcaModel extends SegmentedModel<OrcaEntity>
{
    private final ModelRenderer front;
    private final ModelRenderer right;
    private final ModelRenderer left;
    private final ModelRenderer top;
    private final ModelRenderer body;
    private final ModelRenderer rump;
    private final ModelRenderer tail;
    private final ModelRenderer fin;
    private final ModelRenderer tip;
    private final ModelRenderer knob;
    private final ModelRenderer backleft;
    private final ModelRenderer backright;


    public OrcaModel()
    {
        texWidth = 128;
        texHeight = 128;

        body = new ModelRenderer(this);
        body.setPos(0.0F, 19.0F, -3.0F);
        body.texOffs(0, 0).addBox(-8.0F, -9.0F, -11.0F, 16.0F, 14.0F, 27.0F, 0.0F, false);

        right = new ModelRenderer(this);
        right.setPos(7.0F, 0.0F, 6.0F);
        body.addChild(right);
        setRotationAngle(right, 0.0F, 0.0F, 0.6109F);
        right.texOffs(72, 49).addBox(0.2066F, -2.0017F, -4.0F, 12.0F, 2.0F, 7.0F, 0.0F, false);

        top = new ModelRenderer(this);
        top.setPos(0.0F, -8.3075F, -2.0851F);
        body.addChild(top);
        top.texOffs(0, 11).addBox(-1.5F, -5.8264F, -2.0152F, 3.0F, 6.0F, 6.0F, 0.0F, false);

        fin = new ModelRenderer(this);
        fin.setPos(1.0F, -8.6925F, 0.0851F);
        top.addChild(fin);
        setRotationAngle(fin, 0.1745F, 0.0F, 0.0F);
        fin.texOffs(0, 41).addBox(-2.0F, -1.2098F, -1.9685F, 2.0F, 6.0F, 5.0F, 0.0F, false);

        tip = new ModelRenderer(this);
        tip.setPos(-1.0F, -0.3075F, -8.0851F);
        fin.addChild(tip);
        setRotationAngle(tip, 0.0873F, 0.0F, 0.0F);
        tip.texOffs(18, 11).addBox(-0.5F, -4.9963F, 6.2183F, 1.0F, 5.0F, 3.0F, 0.0F, false);

        knob = new ModelRenderer(this);
        knob.setPos(0.0F, 0.587F, 6.0544F);
        tip.addChild(knob);
        setRotationAngle(knob, 0.0873F, 0.0F, 0.0F);
        knob.texOffs(0, 0).addBox(-0.5F, -7.0657F, 0.5453F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        left = new ModelRenderer(this);
        left.setPos(-6.0F, 1.0F, 6.0F);
        body.addChild(left);
        setRotationAngle(left, 0.0F, 0.0F, -0.6109F);
        left.texOffs(72, 58).addBox(-12.8192F, -2.5736F, -4.0F, 12.0F, 2.0F, 7.0F, 0.0F, false);

        rump = new ModelRenderer(this);
        rump.setPos(0.0F, -1.0F, -9.0F);
        body.addChild(rump);
        setRotationAngle(rump, 0.0873F, 0.0F, 0.0F);
        rump.texOffs(0, 41).addBox(-6.0F, -5.7459F, -14.2773F, 12.0F, 11.0F, 16.0F, 0.0F, false);

        tail = new ModelRenderer(this);
        tail.setPos(0.0F, -7.636F, -13.7581F);
        rump.addChild(tail);
        setRotationAngle(tail, 0.0873F, 0.0F, 0.0F);
        tail.texOffs(41, 53).addBox(-4.0F, 5.5232F, -13.6395F, 8.0F, 6.0F, 15.0F, 0.0F, false);

        backleft = new ModelRenderer(this);
        backleft.setPos(-2.0F, 2.3127F, -9.0463F);
        tail.addChild(backleft);
        setRotationAngle(backleft, 0.0F, -0.1745F, 0.0F);
        backleft.texOffs(0, 68).addBox(-14.8221F, 7.8451F, -5.8613F, 14.0F, 2.0F, 6.0F, 0.0F, false);

        backright = new ModelRenderer(this);
        backright.setPos(3.0F, 1.66F, -10.0767F);
        tail.addChild(backright);
        setRotationAngle(backright, 0.0F, 0.1745F, 0.0F);
        backright.texOffs(40, 41).addBox(-0.5833F, 8.0648F, -5.3118F, 14.0F, 2.0F, 6.0F, 0.0F, false);

        front = new ModelRenderer(this);
        front.setPos(0.0F, -2.0F, 15.0F);
        body.addChild(front);
        setRotationAngle(front, -0.0873F, 0.0F, 0.0F);
        front.texOffs(59, 0).addBox(-7.0F, -5.3256F, -0.4466F, 14.0F, 11.0F, 9.0F, 0.0F, false);
        front.texOffs(35, 74).addBox(-5.3258F, -4.0042F, 7.0998F, 10.0F, 9.0F, 5.0F, 0.0F, false);
        front.texOffs(0, 0).addBox(-4.3258F, -1.2694F, 10.1755F, 8.0F, 6.0F, 5.0F, 0.0F, false);
    }

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(OrcaEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float pitch = headPitch * (float) Math.PI / -180F;
        float yaw = netHeadYaw * (float) Math.PI / 180F;
        body.xRot = pitch;
        body.yRot = yaw;

        if (Entity.getHorizontalDistanceSqr(entityIn.getDeltaMovement()) > 1.0E-7D)
        {
            float oscillation = 0.1F - 0.2F * MathHelper.cos(ageInTicks * 0.3F);
            rump.zRot = oscillation;
            front.zRot = oscillation;
            top.zRot = oscillation;
            left.zRot = oscillation;
            left.zRot = -1 * oscillation;
            right.zRot = oscillation;
            right.zRot = -1 * oscillation;
        }
    }
}
