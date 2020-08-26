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

import net.dries007.tfc.objects.entity.animal.EntityDireWolfTFC;

/**
 * ModelDireWolfTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelDireWolfTFC extends ModelBase
{
    public ModelRenderer frontRLegTop;
    public ModelRenderer backBody;
    public ModelRenderer head;
    public ModelRenderer backRLegTop;
    public ModelRenderer backLLegTop;
    public ModelRenderer frontLLegTop;
    public ModelRenderer frontBody;
    public ModelRenderer tailMain;
    public ModelRenderer frontRLegCalf;
    public ModelRenderer frontRLegAnkle;
    public ModelRenderer frontRLegPaw;
    public ModelRenderer nose;
    public ModelRenderer earR;
    public ModelRenderer mouthBottom1;
    public ModelRenderer mouthBottom2;
    public ModelRenderer mouthTop;
    public ModelRenderer headMane;
    public ModelRenderer earL;
    public ModelRenderer backRLegCalf;
    public ModelRenderer backRLegAnkle;
    public ModelRenderer backRLegPaw;
    public ModelRenderer backLLegCalf;
    public ModelRenderer backLLegAnkle;
    public ModelRenderer backLLegPaw;
    public ModelRenderer frontLLegCalf;
    public ModelRenderer frontLLegAnkle;
    public ModelRenderer frontLLegPaw;
    public ModelRenderer tailBody;
    public ModelRenderer tailTip;
    public ModelRenderer neckMane1;
    public ModelRenderer neckMane2;
    public ModelRenderer neckManeLower1;
    public ModelRenderer neckManeLower2;


    public ModelDireWolfTFC()
    {
        textureWidth = 88;
        textureHeight = 88;

        frontBody = new ModelRenderer(this, 45, 66);
        frontBody.setRotationPoint(0.0F, 21.0F, 0.0F);
        frontBody.addBox(-4.5F, -15.0F, -9.0F, 9, 9, 12, 0.0F);
        backLLegTop = new ModelRenderer(this, 19, 75);
        backLLegTop.setRotationPoint(3.0F, 8.5F, 6.0F);
        backLLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        backRLegPaw = new ModelRenderer(this, 20, 48);
        backRLegPaw.setRotationPoint(-0.01F, 2.9F, 1.0F);
        backRLegPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotateAngle(backRLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        frontRLegAnkle = new ModelRenderer(this, 3, 55);
        frontRLegAnkle.setRotationPoint(-0.99F, 6.0F, 0.2F);
        frontRLegAnkle.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotateAngle(frontRLegAnkle, -0.2617993877991494F, 0.0F, 0.0F);
        frontLLegAnkle = new ModelRenderer(this, 3, 55);
        frontLLegAnkle.setRotationPoint(-0.99F, 6.0F, 0.2F);
        frontLLegAnkle.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotateAngle(frontLLegAnkle, -0.2617993877991494F, 0.0F, 0.0F);
        earR = new ModelRenderer(this, 29, 40);
        earR.setRotationPoint(-1.3F, -3.2F, -1.5F);
        earR.addBox(-2.0F, -2.0F, -2.0F, 2, 3, 1, 0.0F);
        setRotateAngle(earR, 0.0F, -0.17453292519943295F, -0.17453292519943295F);
        frontRLegPaw = new ModelRenderer(this, 2, 48);
        frontRLegPaw.setRotationPoint(-0.01F, 3.0F, 1.0F);
        frontRLegPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotateAngle(frontRLegPaw, 0.17453292519943295F, 0.0F, 0.0F);
        tailTip = new ModelRenderer(this, 34, 0);
        tailTip.setRotationPoint(0.0F, 0.3F, 6.0F);
        tailTip.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        setRotateAngle(tailTip, -0.08726646259971647F, 0.0F, 0.0F);
        backLLegAnkle = new ModelRenderer(this, 21, 55);
        backLLegAnkle.setRotationPoint(-0.99F, 6.0F, 0.1F);
        backLLegAnkle.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotateAngle(backLLegAnkle, -0.3490658503988659F, 0.0F, 0.0F);
        tailMain = new ModelRenderer(this, 35, 18);
        tailMain.setRotationPoint(0.0F, 8.0F, 9.5F);
        tailMain.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tailMain, -1.0471975511965976F, 0.0F, 0.0F);
        mouthTop = new ModelRenderer(this, 11, 26);
        mouthTop.setRotationPoint(0.5F, -1.3F, 0.4F);
        mouthTop.addBox(-2.0F, 0.0F, -9.0F, 3, 2, 4, 0.0F);
        frontLLegTop = new ModelRenderer(this, 0, 75);
        frontLLegTop.setRotationPoint(3.0F, 8.0F, -6.5F);
        frontLLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        frontLLegPaw = new ModelRenderer(this, 2, 48);
        frontLLegPaw.setRotationPoint(-0.01F, 3.0F, 1.0F);
        frontLLegPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotateAngle(frontLLegPaw, 0.17453292519943295F, 0.0F, 0.0F);
        earL = new ModelRenderer(this, 29, 40);
        earL.setRotationPoint(2.2F, -3.0F, -1.5F);
        earL.addBox(-1.0F, -2.0F, -2.0F, 2, 3, 1, 0.0F);
        setRotateAngle(earL, 0.0F, 0.17453292519943295F, 0.17453292519943295F);
        frontLLegCalf = new ModelRenderer(this, 2, 63);
        frontLLegCalf.setRotationPoint(1.0F, 6.0F, 1.6F);
        frontLLegCalf.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotateAngle(frontLLegCalf, 0.08726646259971647F, 0.0F, 0.0F);
        backRLegTop = new ModelRenderer(this, 19, 75);
        backRLegTop.setRotationPoint(-4.0F, 8.5F, 5.8F);
        backRLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        backRLegCalf = new ModelRenderer(this, 21, 63);
        backRLegCalf.setRotationPoint(1.0F, 5.8F, 1.4F);
        backRLegCalf.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotateAngle(backRLegCalf, 0.2617993877991494F, 0.0F, 0.0F);
        frontRLegCalf = new ModelRenderer(this, 2, 63);
        frontRLegCalf.setRotationPoint(1.0F, 6.0F, 1.6F);
        frontRLegCalf.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotateAngle(frontRLegCalf, 0.08726646259971647F, 0.0F, 0.0F);
        headMane = new ModelRenderer(this, 56, 0);
        headMane.setRotationPoint(0.0F, -1.5F, -3.0F);
        headMane.addBox(-3.0F, -2.0F, -1.0F, 6, 6, 4, 0.0F);
        mouthBottom1 = new ModelRenderer(this, 6, 20);
        mouthBottom1.setRotationPoint(0.6F, -0.3F, -5.48F);
        mouthBottom1.addBox(-2.0F, 1.0F, -3.0F, 2, 1, 4, 0.0F);
        mouthBottom2 = new ModelRenderer(this, 18, 20);
        mouthBottom2.setRotationPoint(1.4F, -0.3F, -5.48F);
        mouthBottom2.addBox(-2.0F, 1.0F, -3.0F, 2, 1, 4, 0.0F);
        nose = new ModelRenderer(this, 15, 18);
        nose.setRotationPoint(0.0F, 0.0F, -5.3F);
        nose.addBox(-1.0F, -1.0F, -3.7F, 2, 1, 1, 0.0F);
        backRLegAnkle = new ModelRenderer(this, 21, 55);
        backRLegAnkle.setRotationPoint(-0.99F, 6.0F, 0.1F);
        backRLegAnkle.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotateAngle(backRLegAnkle, -0.3490658503988659F, 0.0F, 0.0F);
        backLLegPaw = new ModelRenderer(this, 20, 48);
        backLLegPaw.setRotationPoint(-0.01F, 2.9F, 1.0F);
        backLLegPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotateAngle(backLLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        backBody = new ModelRenderer(this, 50, 49);
        backBody.setRotationPoint(-0.5F, 21.0F, -1.5F);
        backBody.addBox(-3.5F, -14.9F, 4.0F, 8, 8, 8, 0.0F);
        backLLegCalf = new ModelRenderer(this, 21, 63);
        backLLegCalf.setRotationPoint(1.0F, 5.8F, 1.4F);
        backLLegCalf.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotateAngle(backLLegCalf, 0.2617993877991494F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 8, 34);
        head.setRotationPoint(0.0F, 6.6F, -9.5F);
        head.addBox(-2.5F, -3.0F, -6.0F, 5, 5, 5, 0.0F);
        tailBody = new ModelRenderer(this, 30, 7);
        tailBody.setRotationPoint(0.0F, 0.0F, 1.5F);
        tailBody.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 7, 0.0F);
        setRotateAngle(tailBody, -0.2617993877991494F, 0.0F, 0.0F);
        frontRLegTop = new ModelRenderer(this, 0, 75);
        frontRLegTop.setRotationPoint(-4.0F, 8.0F, -6.5F);
        frontRLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        neckMane1 = new ModelRenderer(this, 54, 37);
        neckMane1.setRotationPoint(0.0F, 7.3F, -8.5F);
        neckMane1.addBox(-4.0F, -3.5F, -3.0F, 8, 7, 4, 0.0F);
        setRotateAngle(neckMane1, -0.47123889803846897F, 0.0F, 0.0F);
        neckMane2 = new ModelRenderer(this, 54, 27);
        neckMane2.setRotationPoint(0.0F, 7.0F, -6.5F);
        neckMane2.addBox(-3.5F, -2.0F, -2.0F, 7, 4, 5, 0.2F);
        setRotateAngle(neckMane2, -0.2617993877991494F, 0.0F, 0.0F);
        neckManeLower1 = new ModelRenderer(this, 55, 20);
        neckManeLower1.setRotationPoint(0.0F, 10.5F, -10.5F);
        neckManeLower1.addBox(-3.5F, -2.0F, -1.5F, 7, 2, 4, 0.0F);
        setRotateAngle(neckManeLower1, 0.3141592653589793F, 0.0F, 0.0F);
        neckManeLower2 = new ModelRenderer(this, 57, 11);
        neckManeLower2.setRotationPoint(0.0F, 11.0F, -9.5F);
        neckManeLower2.addBox(-3.0F, -2.5F, -1.5F, 6, 5, 3, -0.1F);
        setRotateAngle(neckManeLower2, 0.4363323129985824F, 0.0F, 0.0F);


        backRLegAnkle.addChild(backRLegPaw);
        frontRLegCalf.addChild(frontRLegAnkle);
        frontLLegCalf.addChild(frontLLegAnkle);
        head.addChild(earR);
        frontRLegAnkle.addChild(frontRLegPaw);
        tailBody.addChild(tailTip);
        backLLegCalf.addChild(backLLegAnkle);
        head.addChild(mouthTop);
        frontLLegAnkle.addChild(frontLLegPaw);
        head.addChild(earL);
        frontLLegTop.addChild(frontLLegCalf);
        backRLegTop.addChild(backRLegCalf);
        frontRLegTop.addChild(frontRLegCalf);
        head.addChild(headMane);
        head.addChild(mouthBottom1);
        head.addChild(mouthBottom2);
        head.addChild(nose);
        backRLegCalf.addChild(backRLegAnkle);
        backLLegAnkle.addChild(backLLegPaw);
        backLLegTop.addChild(backLLegCalf);
        tailMain.addChild(tailBody);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

        EntityDireWolfTFC direwolf = ((EntityDireWolfTFC) entity);

        float percent = (float) direwolf.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head.render(par7);
        neckMane1.render(par7);
        neckMane2.render(par7);
        neckManeLower1.render(par7);
        neckManeLower2.render(par7);
        frontBody.render(par7);
        backBody.render(par7);
        tailMain.render(par7);
        frontRLegTop.render(par7);
        frontLLegTop.render(par7);
        backRLegTop.render(par7);
        backLLegTop.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        setRotateAngle(head, f4 / (180F / (float) Math.PI), f3 / (180F / (float) Math.PI), 0F);
        //setRotateAngle(neckMane1, f4 / (1.5F * (180F / (float) Math.PI)) + -0.47123889803846897F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);
        //setRotateAngle(neckManeLower1, f4 / (1.5F * (180F / (float) Math.PI)) + 0.3141592653589793F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);

        frontRLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
        frontLLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
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

