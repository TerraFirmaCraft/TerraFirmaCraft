package net.dries007.tfc.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.common.entities.aquatic.JellyfishEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class JellyfishModel extends SegmentedModel<JellyfishEntity>
{
    private final ModelRenderer head;
    private final ModelRenderer tail1;
    private final ModelRenderer tail2;
    private final ModelRenderer tail3;
    private final ModelRenderer tail4;

    public JellyfishModel()
    {
        texWidth = 32;
        texHeight = 32;

        head = new ModelRenderer(this);
        head.setPos(0.0F, 15.0F, 0.0F);
        head.texOffs(12, 7).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        head.texOffs(0, 0).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        head.texOffs(0, 7).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);

        tail1 = new ModelRenderer(this);
        tail1.setPos(-1.0F, 15.0F, -1.0F);
        setRotationAngle(tail1, -0.0873F, 0.0F, 0.0873F);
        tail1.texOffs(0, 12).addBox(-0.909F, -0.8336F, -0.9018F, 1.0F, 7.0F, 1.0F, 0.0F, false);

        tail2 = new ModelRenderer(this);
        tail2.setPos(1.0F, 16.0F, -2.0F);
        setRotationAngle(tail2, -0.0873F, 0.0F, -0.0873F);
        tail2.texOffs(4, 12).addBox(0.0F, -2.0F, 0.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);

        tail3 = new ModelRenderer(this);
        tail3.setPos(1.0F, 16.0F, 1.0F);
        setRotationAngle(tail3, 0.0873F, 0.0F, -0.0873F);
        tail3.texOffs(8, 12).addBox(0.0F, -2.0F, 0.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);

        tail4 = new ModelRenderer(this);
        tail4.setPos(-2.0F, 16.0F, 1.0F);
        setRotationAngle(tail4, 0.0873F, 0.0F, 0.0873F);
        tail4.texOffs(12, 12).addBox(0.0F, -2.0F, 0.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(JellyfishEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float oscillation = -0.2F * MathHelper.sin(0.06F * ageInTicks);
        tail1.xRot = -1 * oscillation;
        tail1.zRot = oscillation;
        tail2.xRot = -1 * oscillation;
        tail2.zRot = -1 * oscillation;
        tail3.xRot = oscillation;
        tail3.zRot = -1 * oscillation;
        tail4.xRot = oscillation;
        tail4.zRot = oscillation;
    }

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(head, tail1, tail2, tail3, tail4);
    }
}
