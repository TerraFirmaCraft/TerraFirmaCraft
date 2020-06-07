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

import net.dries007.tfc.objects.entity.animal.EntityCoyoteTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
/**
 * ModelCoyoteTFC - Either Mojang or a mod author
 * Created using Tabula 7.1.0
 */
public class ModelCoyoteTFC extends ModelBase
{
    public ModelRenderer backBody;
    public ModelRenderer head;
    public ModelRenderer frontRLegTop;
    public ModelRenderer tailMain;
    public ModelRenderer neck;
    public ModelRenderer backRLegTop;
    public ModelRenderer backLLegTop;
    public ModelRenderer frontLLegTop;
    public ModelRenderer frontBody;
    public ModelRenderer mouthBottom;
    public ModelRenderer earR;
    public ModelRenderer mouthTop;
    public ModelRenderer nose;
    public ModelRenderer earL;
    public ModelRenderer frontRLegMiddle;
    public ModelRenderer frontRLegBottom;
    public ModelRenderer frontRLegPaw;
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

    public ModelCoyoteTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        backLLegPaw = new ModelRenderer(this, 14, 25);
        backLLegPaw.mirror = true;
        backLLegPaw.setRotationPoint(-0.01F, 2.6F, 1.0F);
        backLLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(backLLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        tailTip = new ModelRenderer(this, 25, 0);
        tailTip.setRotationPoint(0.0F, 0.0F, 6.0F);
        tailTip.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(tailTip, -0.08726646259971647F, 0.0F, 0.0F);
        backBody = new ModelRenderer(this, 33, 21);
        backBody.setRotationPoint(0.0F, 22.5F, -1.5F);
        backBody.addBox(-3.0F, -14.9F, 3.5F, 6, 7, 7, -0.2F);
        tailMain = new ModelRenderer(this, 24, 14);
        tailMain.setRotationPoint(0.0F, 9.6F, 8.5F);
        tailMain.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tailMain, -1.0471975511965976F, 0.0F, 0.0F);
        backRLegBottom = new ModelRenderer(this, 15, 30);
        backRLegBottom.setRotationPoint(-0.95F, 5.5F, 0.1F);
        backRLegBottom.addBox(-1.0F, -1.0F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(backRLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backLLegBottom = new ModelRenderer(this, 15, 30);
        backLLegBottom.mirror = true;
        backLLegBottom.setRotationPoint(-1.05F, 5.5F, -0.1F);
        backLLegBottom.addBox(-1.0F, -1.0F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(backLLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        frontLLegBottom = new ModelRenderer(this, 2, 30);
        frontLLegBottom.mirror = true;
        frontLLegBottom.setRotationPoint(-1.05F, 5.5F, -0.1F);
        frontLLegBottom.addBox(-1.0F, -1.0F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(frontLLegBottom, -0.17453292519943295F, 0.0F, 0.0F);
        frontLLegTop = new ModelRenderer(this, 0, 44);
        frontLLegTop.mirror = true;
        frontLLegTop.setRotationPoint(3.0F, 8.8F, -6.0F);
        frontLLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 4, 0.0F);
        head = new ModelRenderer(this, 0, 12);
        head.setRotationPoint(0.0F, 6.900000095367432F, -8.5F);
        head.addBox(-2.5F, -2.5F, -5.0F, 5, 5, 5, -0.20000004768371582F);
        backLLegMiddle = new ModelRenderer(this, 14, 35);
        backLLegMiddle.mirror = true;
        backLLegMiddle.setRotationPoint(0.8F, 6.0F, 1.6F);
        backLLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 2, 6, 3, 0.0F);
        setRotateAngle(backLLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        earL = new ModelRenderer(this, 16, 1);
        earL.mirror = true;
        earL.setRotationPoint(2.8F, -0.8F, -0.5F);
        earL.addBox(-2.0F, -3.0F, -2.0F, 2, 3, 1, 0.0F);
        setRotateAngle(earL, 0.0F, 0.17453292519943295F, 0.3490658503988659F);
        neck = new ModelRenderer(this, 36, 10);
        neck.setRotationPoint(0.0F, 9.0F, -8.2F);
        neck.addBox(-2.0F, -2.5F, -3.5F, 4, 5, 6, -0.2F);
        setRotateAngle(neck, -0.7853981633974483F, 0.0F, 0.0F);
        backLLegTop = new ModelRenderer(this, 13, 44);
        backLLegTop.mirror = true;
        backLLegTop.setRotationPoint(3.0F, 9.0F, 5.0F);
        backLLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 4, 0.0F);
        frontRLegBottom = new ModelRenderer(this, 2, 30);
        frontRLegBottom.setRotationPoint(-0.95F, 5.5F, -0.1F);
        frontRLegBottom.addBox(-1.0F, -0.8F, -1.5F, 2, 3, 2, 0.0F);
        setRotateAngle(frontRLegBottom, -0.17453292519943295F, 0.0F, 0.0F);
        backRLegMiddle = new ModelRenderer(this, 14, 35);
        backRLegMiddle.setRotationPoint(1.2F, 6.0F, 1.6F);
        backRLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 2, 6, 3, 0.0F);
        setRotateAngle(backRLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        frontLLegPaw = new ModelRenderer(this, 1, 25);
        frontLLegPaw.mirror = true;
        frontLLegPaw.setRotationPoint(-0.01F, 2.6F, 1.1F);
        frontLLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(frontLLegPaw, 0.08726646259971647F, 0.0F, -0.008901179185171082F);
        mouthTop = new ModelRenderer(this, 5, 7);
        mouthTop.setRotationPoint(0.0F, 0.2F, -6.0F);
        mouthTop.addBox(-1.0F, -1.0F, -1.5F, 2, 2, 3, 0.0F);
        backRLegPaw = new ModelRenderer(this, 14, 25);
        backRLegPaw.setRotationPoint(-0.01F, 2.6F, 1.0F);
        backRLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(backRLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        frontRLegPaw = new ModelRenderer(this, 1, 25);
        frontRLegPaw.setRotationPoint(-0.01F, 2.6F, 1.1F);
        frontRLegPaw.addBox(-1.0F, -1.0F, -3.5F, 2, 2, 3, 0.0F);
        setRotateAngle(frontRLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        tailBody = new ModelRenderer(this, 19, 4);
        tailBody.setRotationPoint(0.0F, 0.0F, 1.5F);
        tailBody.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 7, 0.0F);
        setRotateAngle(tailBody, -0.2617993877991494F, 0.0F, 0.0F);
        frontBody = new ModelRenderer(this, 28, 35);
        frontBody.setRotationPoint(0.0F, 22.5F, 0.0F);
        frontBody.addBox(-3.5F, -15.0F, -8.5F, 7, 8, 11, -0.2F);
        earR = new ModelRenderer(this, 16, 1);
        earR.setRotationPoint(-0.8F, -1.5F, -0.5F);
        earR.addBox(-2.0F, -3.0F, -2.0F, 2, 3, 1, 0.0F);
        setRotateAngle(earR, 0.0F, -0.17453292519943295F, -0.3490658503988659F);
        mouthBottom = new ModelRenderer(this, 4, 2);
        mouthBottom.setRotationPoint(0.0F, 1.5F, -5.4F);
        mouthBottom.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F);
        backRLegTop = new ModelRenderer(this, 13, 44);
        backRLegTop.setRotationPoint(-3.0F, 9.0F, 5.0F);
        backRLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 4, 0.0F);
        frontLLegMiddle = new ModelRenderer(this, 1, 35);
        frontLLegMiddle.mirror = true;
        frontLLegMiddle.setRotationPoint(0.8F, 6.0F, 1.6F);
        frontLLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 2, 6, 3, 0.0F);
        setRotateAngle(frontLLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 8, 0);
        nose.setRotationPoint(0.0F, 0.0F, -7.5F);
        nose.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        frontRLegMiddle = new ModelRenderer(this, 1, 35);
        frontRLegMiddle.setRotationPoint(1.2F, 6.0F, 1.6F);
        frontRLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 2, 6, 3, 0.0F);
        frontRLegTop = new ModelRenderer(this, 0, 44);
        frontRLegTop.setRotationPoint(-3.0F, 8.8F, -6.0F);
        frontRLegTop.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 4, 0.0F);

        backLLegBottom.addChild(backLLegPaw);
        tailBody.addChild(tailTip);
        backRLegMiddle.addChild(backRLegBottom);
        backLLegMiddle.addChild(backLLegBottom);
        frontLLegMiddle.addChild(frontLLegBottom);
        backLLegTop.addChild(backLLegMiddle);
        head.addChild(earL);
        frontRLegMiddle.addChild(frontRLegBottom);
        backRLegTop.addChild(backRLegMiddle);
        frontLLegBottom.addChild(frontLLegPaw);
        head.addChild(mouthTop);
        backRLegBottom.addChild(backRLegPaw);
        frontRLegBottom.addChild(frontRLegPaw);
        tailMain.addChild(tailBody);
        head.addChild(earR);
        head.addChild(mouthBottom);
        frontLLegTop.addChild(frontLLegMiddle);
        head.addChild(nose);
        frontRLegTop.addChild(frontRLegMiddle);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

        EntityCoyoteTFC coyote = ((EntityCoyoteTFC) entity);

        float percent = (float) coyote.getPercentToAdulthood();
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
        head.rotateAngleX = f4 / (180F / (float) Math.PI);
        head.rotateAngleY = f3 / (180F / (float) Math.PI);
        //neckMane.rotateAngleX = f4 / (90F / (float) Math.PI);
        //neckMane.rotateAngleY = f3 / (90F / (float) Math.PI);
        //setRotateAngle(neckMane, -0.4379031093253773F, 0.0F, 0.0F);

        frontRLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
        frontLLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
        backRLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
        backLLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}

