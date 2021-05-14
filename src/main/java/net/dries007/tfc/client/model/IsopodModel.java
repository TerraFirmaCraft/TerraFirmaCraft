package net.dries007.tfc.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.common.entities.aquatic.SeafloorCritterEntity;

import static net.dries007.tfc.client.ClientHelpers.setRotationAngle;

public class IsopodModel extends SegmentedModel<SeafloorCritterEntity>
{
    private final ModelRenderer body;
    private final ModelRenderer leftfeet;
    private final ModelRenderer leftfeet2;

    public IsopodModel()
    {
        texWidth = 32;
        texHeight = 32;

        body = new ModelRenderer(this);
        body.setPos(0.0F, 23.0F, -2.0F);
        body.texOffs(12, 7).addBox(-1.0F, -1.5F, 4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(0, 7).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 1.0F, 6.0F, 0.0F, false);
        body.texOffs(0, 7).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        body.texOffs(0, 0).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);

        ModelRenderer rightantenna = new ModelRenderer(this);
        rightantenna.setPos(0.0F, 0.0F, 6.0F);
        body.addChild(rightantenna);
        setRotationAngle(rightantenna, 0.0F, 0.7854F, 0.0F);
        rightantenna.texOffs(0, 3).addBox(0.0F, 0.5F, -1.0F, 1.0F, 0.0F, 3.0F, 0.0F, false);

        ModelRenderer leftantenna = new ModelRenderer(this);
        leftantenna.setPos(-1.0F, 0.0F, 6.0F);
        body.addChild(leftantenna);
        setRotationAngle(leftantenna, 0.0F, -0.7854F, 0.0F);
        leftantenna.texOffs(0, 0).addBox(-0.2929F, 0.5F, -1.7071F, 1.0F, 0.0F, 3.0F, 0.0F, false);

        leftfeet = new ModelRenderer(this);
        leftfeet.setPos(-3.0F, 24.0F, -2.0F);
        leftfeet.texOffs(8, 14).addBox(0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        leftfeet.texOffs(4, 14).addBox(0.5F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        leftfeet.texOffs(0, 14).addBox(0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        leftfeet2 = new ModelRenderer(this);
        leftfeet2.setPos(1.0F, 24.0F, -2.0F);
        leftfeet2.texOffs(12, 11).addBox(0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        leftfeet2.texOffs(0, 11).addBox(0.5F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        leftfeet2.texOffs(0, 9).addBox(0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    @Override
    public Iterable<ModelRenderer> parts()
    {
        return ImmutableList.of(body, leftfeet, leftfeet2);
    }

    @Override
    public void setupAnim(SeafloorCritterEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        //double oscillation = 0.04 * MathHelper.cos(0.2F * ageInTicks);
    }
}
