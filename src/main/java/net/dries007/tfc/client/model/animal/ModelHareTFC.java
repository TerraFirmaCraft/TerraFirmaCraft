/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityHareTFC;

/**
 * ModelHareTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
public class ModelHareTFC extends ModelBase
{
    public ModelRenderer hareHead;
    public ModelRenderer hareNose;
    public ModelRenderer hareRightFoot;
    public ModelRenderer hareRightArm;
    public ModelRenderer hareRightThigh;
    public ModelRenderer hareTail;
    public ModelRenderer hareLeftFoot;
    public ModelRenderer hareLeftThigh;
    public ModelRenderer hareLeftEar;
    public ModelRenderer hareLeftArm;
    public ModelRenderer hareBody;
    public ModelRenderer hareRightEar;
    private float jumpRotation;

    public ModelHareTFC()
    {
        textureWidth = 64;
        textureHeight = 32;

        hareTail = new ModelRenderer(this, 0, 4);
        hareTail.setRotationPoint(0.0F, 19.0F, 7.0F);
        hareTail.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2, 0.0F);
        setRotateAngle(hareTail, -0.3490658503988659F, 0.0F, 0.0F);
        hareRightThigh = new ModelRenderer(this, 49, 22);
        hareRightThigh.setRotationPoint(-3.0F, 17.5F, 3.7F);
        hareRightThigh.addBox(-1.0F, -0.5F, -0.5F, 2, 5, 5, 0.0F);
        setRotateAngle(hareRightThigh, -0.3665191429188092F, 0.0F, 0.0F);
        hareLeftEar = new ModelRenderer(this, 27, 2);
        hareLeftEar.mirror = true;
        hareLeftEar.setRotationPoint(-0.5F, -0.5F, -0.5F);
        hareLeftEar.addBox(0.0F, -9.5F, -2.0F, 3, 6, 1, 0.0F);
        setRotateAngle(hareLeftEar, 0.0F, 0.2617993877991494F, 0.2617993877991494F);
        hareRightFoot = new ModelRenderer(this, 48, 13);
        hareRightFoot.setRotationPoint(-3.0F, 17.5F, 3.7F);
        hareRightFoot.addBox(-1.0F, 4.5F, -3.2F, 2, 2, 6, 0.0F);
        hareHead = new ModelRenderer(this, 7, 5);
        hareHead.setRotationPoint(0.0F, 15.5F, -1.0F);
        hareHead.addBox(-2.5F, -4.0F, -6.0F, 5, 4, 5, 0.2F);
        hareRightEar = new ModelRenderer(this, 27, 2);
        hareRightEar.setRotationPoint(0.5F, -0.5F, -0.5F);
        hareRightEar.addBox(-3.0F, -9.5F, -2.0F, 3, 6, 1, 0.0F);
        setRotateAngle(hareRightEar, 0.0F, -0.2617993877991494F, -0.2617993877991494F);
        hareNose = new ModelRenderer(this, 3, 1);
        hareNose.setRotationPoint(0.0F, 16.0F, -1.0F);
        hareNose.addBox(-0.5F, -2.5F, -7.0F, 1, 1, 1, 0.2F);
        hareLeftArm = new ModelRenderer(this, 39, 22);
        hareLeftArm.mirror = true;
        hareLeftArm.setRotationPoint(3.0F, 17.0F, -1.0F);
        hareLeftArm.addBox(-1.0F, -0.8F, -1.5F, 2, 8, 2, -0.0F);
        setRotateAngle(hareLeftArm, -0.19198621771937624F, 0.0F, 0.0F);
        hareBody = new ModelRenderer(this, 0, 15);
        hareBody.setRotationPoint(0.0F, 18.0F, 7.0F);
        hareBody.addBox(-3.0F, -2.0F, -10.0F, 6, 6, 11, 0.0F);
        setRotateAngle(hareBody, -0.3490658503988659F, 0.0F, 0.0F);
        hareLeftThigh = new ModelRenderer(this, 49, 22);
        hareLeftThigh.mirror = true;
        hareLeftThigh.setRotationPoint(3.0F, 17.5F, 3.7F);
        hareLeftThigh.addBox(-1.0F, -1.0F, -1.0F, 2, 5, 5, 0.0F);
        setRotateAngle(hareLeftThigh, -0.3665191429188092F, 0.0F, 0.0F);
        hareRightArm = new ModelRenderer(this, 39, 22);
        hareRightArm.setRotationPoint(-3.0F, 17.0F, -1.0F);
        hareRightArm.addBox(-1.0F, -0.8F, -1.5F, 2, 8, 2, -0.0F);
        setRotateAngle(hareRightArm, -0.19198621771937624F, 0.0F, 0.0F);
        hareLeftFoot = new ModelRenderer(this, 48, 13);
        hareLeftFoot.mirror = true;
        hareLeftFoot.setRotationPoint(3.0F, 17.5F, 3.7F);
        hareLeftFoot.addBox(-1.0F, 4.5F, -3.2F, 2, 2, 6, 0.0F);

        hareHead.addChild(this.hareLeftEar);
        hareHead.addChild(this.hareRightEar);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        if (this.isChild)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.56666666F, 0.56666666F, 0.56666666F);
            GlStateManager.translate(0.0F, 22.0F * scale, 2.0F * scale);
            hareHead.render(scale);
            hareNose.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
            GlStateManager.translate(0.0F, 36.0F * scale, 0.0F);
            hareLeftFoot.render(scale);
            hareRightFoot.render(scale);
            hareLeftThigh.render(scale);
            hareRightThigh.render(scale);
            hareBody.render(scale);
            hareLeftArm.render(scale);
            hareRightArm.render(scale);
            hareTail.render(scale);
            GlStateManager.popMatrix();
        }
        else
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.6F, 0.6F, 0.6F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            hareLeftFoot.render(scale);
            hareRightFoot.render(scale);
            hareLeftThigh.render(scale);
            hareRightThigh.render(scale);
            hareBody.render(scale);
            hareLeftArm.render(scale);
            hareRightArm.render(scale);
            hareHead.render(scale);
            hareTail.render(scale);
            hareNose.render(scale);
            GlStateManager.popMatrix();
        }
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        float f = ageInTicks - (float) entityIn.ticksExisted;
        EntityHareTFC EntityHareTFC = (EntityHareTFC) entityIn;
        this.hareNose.rotateAngleX = headPitch * 0.017453292F;
        this.hareHead.rotateAngleX = headPitch * 0.017453292F;
        this.hareNose.rotateAngleY = netHeadYaw * 0.017453292F;
        this.hareHead.rotateAngleY = netHeadYaw * 0.017453292F;
        this.jumpRotation = MathHelper.sin(EntityHareTFC.getJumpCompletion(f) * (float) Math.PI);
        this.hareLeftThigh.rotateAngleX = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
        this.hareRightThigh.rotateAngleX = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
        this.hareLeftFoot.rotateAngleX = this.jumpRotation * 50.0F * 0.017453292F;
        this.hareRightFoot.rotateAngleX = this.jumpRotation * 50.0F * 0.017453292F;
        this.hareLeftArm.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
        this.hareRightArm.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
    }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
        this.jumpRotation = MathHelper.sin(((EntityHareTFC) entitylivingbaseIn).getJumpCompletion(partialTickTime) * (float) Math.PI);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}