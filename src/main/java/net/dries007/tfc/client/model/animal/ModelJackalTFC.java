/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityJackalTFC;

/**
 * ModelJackalTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelJackalTFC extends ModelBase
{
    public ModelRenderer frontRLegTop;
    public ModelRenderer backBody;
    public ModelRenderer head;
    public ModelRenderer tailMain;
    public ModelRenderer neck;
    public ModelRenderer backRLegTop;
    public ModelRenderer backLLegTop;
    public ModelRenderer frontLLegTop;
    public ModelRenderer frontBody;
    public ModelRenderer frontRLegMiddle;
    public ModelRenderer frontRLegBottom;
    public ModelRenderer frontRLegPaw;
    public ModelRenderer leftEar;
    public ModelRenderer mouthTop1;
    public ModelRenderer mouthTop2;
    public ModelRenderer rightEar;
    public ModelRenderer mouthBottom;
    public ModelRenderer nose;
    public ModelRenderer leftEarTip;
    public ModelRenderer rightEarTip;
    public ModelRenderer tailBody;
    public ModelRenderer tailTip;
    public ModelRenderer backRLegMiddle;
    public ModelRenderer backRLegBottom;
    public ModelRenderer backRLegPaw;
    public ModelRenderer backLLegMiddle;
    public ModelRenderer backLLegBottom;
    public ModelRenderer backLLegPaw;
    public ModelRenderer frontLLegMiddle;
    public ModelRenderer frontLLegBottom;
    public ModelRenderer frontLLegPaw;

    public ModelJackalTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        neck = new ModelRenderer(this, 39, 24);
        neck.setRotationPoint(0.0F, 10.0F, -6.6F);
        neck.addBox(-2.0F, -2.5F, -3.5F, 4, 5, 5, -0.2F);
        setRotateAngle(neck, -0.7853981633974483F, 0.0F, 0.0F);
        rightEar = new ModelRenderer(this, 0, 2);
        rightEar.setRotationPoint(-0.4F, -1.8F, -0.5F);
        rightEar.addBox(-2.0F, -3.0F, -2.0F, 2, 3, 1, 0.0F);
        setRotateAngle(rightEar, 0.0F, -0.17453292519943295F, -0.3490658503988659F);
        frontRLegTop = new ModelRenderer(this, 1, 57);
        frontRLegTop.setRotationPoint(-2.8F, 11.0F, -5.0F);
        frontRLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.3F);
        setRotateAngle(frontRLegTop, 0.13962634015954636F, 0.0F, 0.04363323129985824F);
        frontLLegMiddle = new ModelRenderer(this, 1, 50);
        frontLLegMiddle.mirror = true;
        frontLLegMiddle.setRotationPoint(1.0F, 4.5F, 1.0F);
        frontLLegMiddle.addBox(-2.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
        setRotateAngle(frontLLegMiddle, -0.13962634015954636F, 0.0F, 0.04363323129985824F);
        frontRLegPaw = new ModelRenderer(this, 0, 40);
        frontRLegPaw.setRotationPoint(-0.01F, 1.8F, 1.1F);
        frontRLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(frontRLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        leftEar = new ModelRenderer(this, 0, 2);
        leftEar.mirror = true;
        leftEar.setRotationPoint(2.2F, -1.2F, -0.5F);
        leftEar.addBox(-2.0F, -3.0F, -2.0F, 2, 3, 1, 0.0F);
        setRotateAngle(leftEar, 0.0F, 0.17453292519943295F, 0.3490658503988659F);
        backLLegPaw = new ModelRenderer(this, 13, 37);
        backLLegPaw.mirror = true;
        backLLegPaw.setRotationPoint(0.01F, 2.6F, 1.0F);
        backLLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(backLLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        rightEarTip = new ModelRenderer(this, 1, 0);
        rightEarTip.setRotationPoint(-1.0F, -3.5F, -1.5F);
        rightEarTip.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        nose = new ModelRenderer(this, 8, 0);
        nose.setRotationPoint(0.0F, 0.0F, -7.5F);
        nose.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        backLLegMiddle = new ModelRenderer(this, 13, 47);
        backLLegMiddle.mirror = true;
        backLLegMiddle.setRotationPoint(1.0F, 4.8F, 1.0F);
        backLLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 2, 6, 3, 0.0F);
        setRotateAngle(backLLegMiddle, 0.2617993877991494F, 0.0F, 0.04363323129985824F);
        leftEarTip = new ModelRenderer(this, 1, 0);
        leftEarTip.setRotationPoint(-1.0F, -3.5F, -1.5F);
        leftEarTip.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        backLLegTop = new ModelRenderer(this, 13, 56);
        backLLegTop.mirror = true;
        backLLegTop.setRotationPoint(2.6F, 10.5F, 4.5F);
        backLLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 3, 0.3F);
        setRotateAngle(backLLegTop, 0.0F, 0.0F, -0.04363323129985824F);
        frontLLegPaw = new ModelRenderer(this, 0, 40);
        frontLLegPaw.mirror = true;
        frontLLegPaw.setRotationPoint(0.01F, 1.8F, 1.1F);
        frontLLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(frontLLegPaw, 0.08726646259971647F, 0.0F, -0.0F);
        frontRLegBottom = new ModelRenderer(this, 1, 45);
        frontRLegBottom.setRotationPoint(-0.99F, 5.8F, -0.6F);
        frontRLegBottom.addBox(-1.0F, -1.0F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(frontRLegBottom, -0.08726646259971647F, 0.0F, 0.0F);
        tailBody = new ModelRenderer(this, 21, 4);
        tailBody.setRotationPoint(0.0F, 0.0F, 1.5F);
        tailBody.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 7, 0.0F);
        setRotateAngle(tailBody, -0.2617993877991494F, 0.0F, 0.0F);
        tailTip = new ModelRenderer(this, 27, 0);
        tailTip.setRotationPoint(0.0F, 0.0F, 6.0F);
        tailTip.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(tailTip, -0.08726646259971647F, 0.0F, 0.0F);
        frontLLegBottom = new ModelRenderer(this, 1, 45);
        frontLLegBottom.mirror = true;
        frontLLegBottom.setRotationPoint(-1.01F, 5.8F, -0.6F);
        frontLLegBottom.addBox(-1.0F, -1.0F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(frontLLegBottom, -0.08726646259971647F, 0.0F, 0.0F);
        backBody = new ModelRenderer(this, 34, 34);
        backBody.setRotationPoint(0.0F, 23.6F, -2.5F);
        backBody.addBox(-3.5F, -14.9F, 3.5F, 7, 7, 7, -0.2F);
        frontLLegTop = new ModelRenderer(this, 1, 57);
        frontLLegTop.mirror = true;
        frontLLegTop.setRotationPoint(2.8F, 11.0F, -5.0F);
        frontLLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.3F);
        setRotateAngle(frontLLegTop, 0.13962634015954636F, 0.0F, -0.04363323129985824F);
        backRLegTop = new ModelRenderer(this, 13, 56);
        backRLegTop.setRotationPoint(-2.6F, 10.5F, 4.5F);
        backRLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 3, 0.3F);
        setRotateAngle(backRLegTop, -0.0F, 0.0F, 0.04363323129985824F);
        backLLegBottom = new ModelRenderer(this, 14, 42);
        backLLegBottom.mirror = true;
        backLLegBottom.setRotationPoint(-1.01F, 5.2F, -0.2F);
        backLLegBottom.addBox(-1.0F, -1.0F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(backLLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        frontBody = new ModelRenderer(this, 32, 48);
        frontBody.setRotationPoint(0.0F, 24.0F, 1.0F);
        frontBody.addBox(-3.5F, -15.0F, -8.5F, 7, 7, 9, 0.2F);
        mouthBottom = new ModelRenderer(this, 4, 2);
        mouthBottom.setRotationPoint(0.0F, 1.5F, -5.4F);
        mouthBottom.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F);
        backRLegMiddle = new ModelRenderer(this, 13, 47);
        backRLegMiddle.setRotationPoint(1.0F, 4.8F, 1.0F);
        backRLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 2, 6, 3, 0.0F);
        setRotateAngle(backRLegMiddle, 0.2617993877991494F, 0.0F, -0.04363323129985824F);
        frontRLegMiddle = new ModelRenderer(this, 1, 50);
        frontRLegMiddle.setRotationPoint(1.0F, 4.5F, 1.0F);
        frontRLegMiddle.addBox(-2.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
        setRotateAngle(frontRLegMiddle, -0.13962634015954636F, 0.0F, -0.04363323129985824F);
        backRLegBottom = new ModelRenderer(this, 14, 42);
        backRLegBottom.setRotationPoint(-0.99F, 5.5F, 0.1F);
        backRLegBottom.addBox(-1.0F, -1.0F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(backRLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 0, 12);
        head.setRotationPoint(0.0F, 7.3F, -7.0F);
        head.addBox(-2.5F, -2.5F, -5.0F, 5, 5, 5, -0.2F);
        tailMain = new ModelRenderer(this, 26, 14);
        tailMain.setRotationPoint(0.0F, 10.5F, 7.5F);
        tailMain.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tailMain, -1.0471975511965976F, 0.0F, 0.0F);
        backRLegPaw = new ModelRenderer(this, 13, 37);
        backRLegPaw.setRotationPoint(-0.01F, 2.6F, 1.0F);
        backRLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(backRLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        mouthTop1 = new ModelRenderer(this, 0, 7);
        mouthTop1.setRotationPoint(-0.2F, 0.2F, -6.0F);
        mouthTop1.addBox(-1.0F, -1.0F, -1.5F, 2, 2, 3, 0.0F);
        mouthTop2 = new ModelRenderer(this, 10, 7);
        mouthTop2.mirror = true;
        mouthTop2.setRotationPoint(0.2F, 0.2F, -6.0F);
        mouthTop2.addBox(-1.0F, -1.0F, -1.5F, 2, 2, 3, 0.0F);

        head.addChild(rightEar);
        head.addChild(mouthTop1);
        head.addChild(mouthTop2);
        frontLLegTop.addChild(frontLLegMiddle);
        frontRLegBottom.addChild(frontRLegPaw);
        head.addChild(leftEar);
        backLLegBottom.addChild(backLLegPaw);
        rightEar.addChild(rightEarTip);
        head.addChild(nose);
        backLLegTop.addChild(backLLegMiddle);
        leftEar.addChild(leftEarTip);
        frontLLegBottom.addChild(frontLLegPaw);
        frontRLegMiddle.addChild(frontRLegBottom);
        tailMain.addChild(tailBody);
        tailBody.addChild(tailTip);
        frontLLegMiddle.addChild(frontLLegBottom);
        backLLegMiddle.addChild(backLLegBottom);
        head.addChild(mouthBottom);
        backRLegTop.addChild(backRLegMiddle);
        frontRLegTop.addChild(frontRLegMiddle);
        backRLegMiddle.addChild(backRLegBottom);
        backRLegBottom.addChild(backRLegPaw);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

        EntityJackalTFC jackal = ((EntityJackalTFC) entity);

        float percent = (float) jackal.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        backBody.render(par7);
        tailMain.render(par7);
        frontLLegTop.render(par7);
        head.render(par7);
        neck.render(par7);
        backLLegTop.render(par7);
        frontBody.render(par7);
        backRLegTop.render(par7);
        frontRLegTop.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        setRotateAngle(head, f4 / (180F / (float) Math.PI), f3 / (180F / (float) Math.PI), 0F);
        setRotateAngle(neck, f4 / (1.5F * (180F / (float) Math.PI)) + -0.7853981633974483F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);

        frontRLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1 + 0.13962634015954636F;
        frontLLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1 + 0.13962634015954636F;
        backRLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
        backLLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}

