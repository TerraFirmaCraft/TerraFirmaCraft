/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

//Made with Blockbench
//Paste this code into your mod.

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityLionTFC;

/**
 * ModelLionTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelLionTFC extends ModelBase
{
    public ModelRenderer frontBodyM;
    public ModelRenderer backBodyM;
    public ModelRenderer mane2;
    public ModelRenderer mane3;
    public ModelRenderer tail;
    public ModelRenderer head;
    public ModelRenderer mouthTop;
    public ModelRenderer mane1;
    public ModelRenderer tail1;
    public ModelRenderer tailTip1;
    public ModelRenderer tailTip2;
    public ModelRenderer nose;
    public ModelRenderer mouthBottom;
    public ModelRenderer earFR;
    public ModelRenderer earFL;
    public ModelRenderer earMR;
    public ModelRenderer earML;
    public ModelRenderer frontRightLegTop;
    public ModelRenderer frontRightLegMiddle;
    public ModelRenderer frontRightLegBottom;
    public ModelRenderer frontRightPaw;
    public ModelRenderer frontLeftLegTop;
    public ModelRenderer frontLeftLegMiddle;
    public ModelRenderer frontLeftLegBottom;
    public ModelRenderer frontLeftPaw;
    public ModelRenderer backRightLegTop;
    public ModelRenderer backRightLegMiddle;
    public ModelRenderer backRightLegBottom;
    public ModelRenderer backRightPaw;
    public ModelRenderer backLeftLegTop;
    public ModelRenderer backLeftLegMiddle;
    public ModelRenderer backLeftLegBottom;
    public ModelRenderer backLeftPaw;
    public ModelRenderer frontBodyF;
    public ModelRenderer backBodyF;
    public ModelRenderer neck;


    public ModelLionTFC()
    {
        textureWidth = 88;
        textureHeight = 88;

        earMR = new ModelRenderer(this, 46, 70);
        earMR.setRotationPoint(-2.0F, -2.0F, -1.0F);
        earMR.addBox(-2.0F, -4.0F, -2.0F, 3, 3, 1, 0.0F);
        mane1 = new ModelRenderer(this, 3, 0);
        mane1.setRotationPoint(0.0F, 0.0F, 0.0F);
        mane1.addBox(-6.0F, -5.0F, -5.0F, 12, 12, 6, 0.0F);
        mouthBottom = new ModelRenderer(this, 56, 46);
        mouthBottom.setRotationPoint(0.5F, 0.5F, -6.3F);
        mouthBottom.addBox(-2.0F, 0.0F, -4.0F, 3, 2, 4, 0.0F);
        tailTip2 = new ModelRenderer(this, 42, 50);
        tailTip2.setRotationPoint(0.0F, -0.9F, 6.2F);
        tailTip2.addBox(-1.0F, 0.13F, 3.63F, 2, 0, 2, 0.0F);
        setRotation(tailTip2, 0.6981317007977318F, 0.0F, 0.0F);
        earFR = new ModelRenderer(this, 71, 70);
        earFR.setRotationPoint(-1.5F, -3.0F, -1.0F);
        earFR.addBox(-2.0F, -3.0F, -2.0F, 3, 3, 1, 0.0F);
        setRotation(earFR, 0.0F, -0.17453292519943295F, -0.17453292519943295F);
        mouthTop = new ModelRenderer(this, 55, 53);
        mouthTop.setRotationPoint(0.0F, 0.4F, -0.5F);
        mouthTop.addBox(-2.0F, -1.0F, -10.0F, 4, 2, 4, 0.0F);
        mane2 = new ModelRenderer(this, 0, 20);
        mane2.setRotationPoint(0.0F, 21.0F, 0.0F);
        mane2.addBox(-7.0F, -17.9F, -10.0F, 14, 9, 7, 0.0F);
        tailTip1 = new ModelRenderer(this, 44, 50);
        tailTip1.setRotationPoint(0.0F, -0.9F, 6.2F);
        tailTip1.addBox(0.0F, -0.87F, 3.63F, 0, 2, 2, 0.0F);
        setRotation(tailTip1, 0.6981317007977318F, 0.0F, 0.0F);
        tail = new ModelRenderer(this, 38, 60);
        tail.setRotationPoint(0.0F, 8.0F, 10.0F);
        tail.addBox(-0.5F, -1.0F, 0.0F, 1, 1, 6, 0.0F);
        setRotation(tail, -0.9599310885968813F, 0.0F, 0.0F);
        mane3 = new ModelRenderer(this, 8, 37);
        mane3.setRotationPoint(0.0F, 21.0F, 0.0F);
        mane3.addBox(-5.0F, -16.9F, -3.0F, 10, 6, 3, 0.0F);
        tail1 = new ModelRenderer(this, 39, 54);
        tail1.setRotationPoint(0.0F, -0.7F, 5.9F);
        tail1.addBox(-0.5F, -0.37F, -0.37F, 1, 1, 5, 0.0F);
        setRotation(tail1, 0.6981317007977318F, 0.0F, 0.0F);
        earML = new ModelRenderer(this, 46, 70);
        earML.setRotationPoint(2.0F, -2.0F, -1.0F);
        earML.addBox(-1.0F, -4.0F, -2.0F, 3, 3, 1, 0.0F);
        head = new ModelRenderer(this, 50, 68);
        head.setRotationPoint(0.0F, 6.0F, -9.0F);
        head.addBox(-3.0F, -4.0F, -7.0F, 6, 7, 7, 0.0F);
        earFL = new ModelRenderer(this, 71, 70);
        earFL.setRotationPoint(1.5F, -3.0F, -1.0F);
        earFL.addBox(-1.0F, -3.0F, -2.0F, 3, 3, 1, 0.0F);
        setRotation(earFL, 0.0F, 0.17453292519943295F, 0.17453292519943295F);
        frontBodyM = new ModelRenderer(this, 41, 0);
        frontBodyM.setRotationPoint(0.0F, 21.0F, 0.0F);
        frontBodyM.addBox(-4.0F, -14.9F, -9.0F, 8, 10, 13, 0.0F);
        nose = new ModelRenderer(this, 56, 60);
        nose.setRotationPoint(0.0F, -0.8F, -5.8F);
        nose.addBox(-1.0F, -2.0F, -4.7F, 2, 2, 5, 0.0F);
        setRotation(nose, 0.3490658503988659F, 0.0F, 0.0F);
        backBodyM = new ModelRenderer(this, 49, 25);
        backBodyM.setRotationPoint(0.0F, 21.0F, 0.0F);
        backBodyM.addBox(-3.0F, -14.9F, 4.0F, 6, 9, 7, 0.0F);

        frontBodyF = new ModelRenderer(this, 41, 2);
        frontBodyF.setRotationPoint(0.0F, 21.0F, 0.0F);
        frontBodyF.addBox(-4.0F, -14.9F, -9.0F, 8, 9, 13, 0.0F);
        backBodyF = new ModelRenderer(this, 49, 27);
        backBodyF.setRotationPoint(0.0F, 21.0F, 0.0F);
        backBodyF.addBox(-3.0F, -14.9F, 4.0F, 6, 7, 7, 0.0F);
        neck = new ModelRenderer(this, 52, 27);
        neck.setRotationPoint(0.0F, -12.0F, -6.4F);
        neck.addBox(-2.5F, -2.5F, -5.0F, 5, 6, 5, 0.0F);
        setRotation(neck, -0.6108652381980153F, 0.0F, 0.0F);

        frontRightLegTop = new ModelRenderer(this, 0, 75);
        frontRightLegTop.setRotationPoint(-4.0F, 8.0F, -6.5F);
        frontRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(frontRightLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegMiddle = new ModelRenderer(this, 2, 63);
        frontRightLegMiddle.setRotationPoint(1.0F, 6.0F, 1.6F);
        frontRightLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(frontRightLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegBottom = new ModelRenderer(this, 3, 55);
        frontRightLegBottom.setRotationPoint(-0.99F, 6.0F, 0.2F);
        frontRightLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(frontRightLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontRightPaw = new ModelRenderer(this, 2, 48);
        frontRightPaw.setRotationPoint(-0.009F, 3.0F, 1.0F);
        frontRightPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(frontRightPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backRightLegTop = new ModelRenderer(this, 19, 75);
        backRightLegTop.setRotationPoint(-3.5F, 8.5F, 6.0F);
        backRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(backRightLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backRightLegMiddle = new ModelRenderer(this, 21, 63);
        backRightLegMiddle.setRotationPoint(1.0F, 5.8F, 1.4F);
        backRightLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(backRightLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backRightLegBottom = new ModelRenderer(this, 21, 55);
        backRightLegBottom.setRotationPoint(-0.99F, 6.0F, 0.1F);
        backRightLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(backRightLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backRightPaw = new ModelRenderer(this, 20, 48);
        backRightPaw.setRotationPoint(-0.009F, 2.9F, 1.0F);
        backRightPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(backRightPaw, 0.08726646259971647F, 0.0F, 0.0F);

        frontLeftLegTop = new ModelRenderer(this, 0, 75);
        frontLeftLegTop.setRotationPoint(3.0F, 8.0F, -6.5F);
        frontLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(frontLeftLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegMiddle = new ModelRenderer(this, 2, 63);
        frontLeftLegMiddle.setRotationPoint(1.0F, 6.0F, 1.6F);
        frontLeftLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(frontLeftLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegBottom = new ModelRenderer(this, 3, 55);
        frontLeftLegBottom.setRotationPoint(-1.009F, 6.0F, 0.2F);
        frontLeftLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(frontLeftLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontLeftPaw = new ModelRenderer(this, 2, 48);
        frontLeftPaw.setRotationPoint(0.009F, 3.0F, 1.0F);
        frontLeftPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(frontLeftPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backLeftLegTop = new ModelRenderer(this, 19, 75);
        backLeftLegTop.setRotationPoint(2.5F, 8.5F, 6.0F);
        backLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(backLeftLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backLeftLegMiddle = new ModelRenderer(this, 21, 63);
        backLeftLegMiddle.setRotationPoint(1.0F, 5.8F, 1.4F);
        backLeftLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(backLeftLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backLeftLegBottom = new ModelRenderer(this, 21, 55);
        backLeftLegBottom.setRotationPoint(-1.009F, 6.0F, 0.1F);
        backLeftLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(backLeftLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backLeftPaw = new ModelRenderer(this, 20, 48);
        backLeftPaw.setRotationPoint(0.009F, 2.9F, 1.0F);
        backLeftPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(backLeftPaw, 0.08726646259971647F, 0.0F, 0.0F);

        head.addChild(earFR);
        head.addChild(mane1);
        head.addChild(mouthTop);
        head.addChild(mouthBottom);
        tail.addChild(tailTip2);
        head.addChild(earMR);
        tail.addChild(tailTip1);
        tail.addChild(tail1);
        head.addChild(earFL);
        head.addChild(earML);
        head.addChild(nose);
        frontBodyF.addChild(neck);
        frontBodyM.addChild(neck);

        frontRightLegTop.addChild(frontRightLegMiddle);
        frontRightLegMiddle.addChild(frontRightLegBottom);
        frontRightLegBottom.addChild(frontRightPaw);
        backRightLegTop.addChild(backRightLegMiddle);
        backRightLegMiddle.addChild(backRightLegBottom);
        backRightLegBottom.addChild(backRightPaw);
        frontLeftLegTop.addChild(frontLeftLegMiddle);
        frontLeftLegMiddle.addChild(frontLeftLegBottom);
        frontLeftLegBottom.addChild(frontLeftPaw);
        backLeftLegTop.addChild(backLeftLegMiddle);
        backLeftLegMiddle.addChild(backLeftLegBottom);
        backLeftLegBottom.addChild(backLeftPaw);
    }


    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

        EntityLionTFC lion = ((EntityLionTFC) entity);

        float percent = (float) lion.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (lion.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            earFL.isHidden = true;
            earFR.isHidden = true;
            earML.isHidden = false;
            earMR.isHidden = false;
            frontBodyM.isHidden = false;
            backBodyM.isHidden = false;
            frontBodyF.isHidden = true;
            backBodyF.isHidden = true;
            mane2.isHidden = false;
            mane3.isHidden = false;

            if (percent < 0.6)
            {
                mane1.isHidden = true;
                mane2.isHidden = true;
                mane3.isHidden = true;
            }
            else if (percent < 0.8)
            {
                mane1.isHidden = false;
                mane2.isHidden = true;
                mane3.isHidden = true;
            }
        }
        else
        {
            earFL.isHidden = false;
            earFR.isHidden = false;
            earML.isHidden = true;
            earMR.isHidden = true;
            frontBodyM.isHidden = true;
            backBodyM.isHidden = true;
            frontBodyF.isHidden = false;
            backBodyF.isHidden = false;
            mane1.isHidden = true;
            mane2.isHidden = true;
            mane3.isHidden = true;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.75f - (0.75f * percent), 0f);
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);

        head.render(par7);
        frontBodyM.render(par7);
        frontBodyF.render(par7);
        backBodyM.render(par7);
        backBodyF.render(par7);
        tail.render(par7);
        mane2.render(par7);
        mane3.render(par7);
        frontLeftLegTop.render(par7);
        frontRightLegTop.render(par7);
        backLeftLegTop.render(par7);
        backRightLegTop.render(par7);
        GlStateManager.popMatrix();
    }


    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        EntityLionTFC lion = ((EntityLionTFC) ent);
        int mouthTicks = lion.getMouthTicks();
        float mouthAngle;

        if (mouthTicks >= 1 && mouthTicks < 15)
        {
            mouthAngle = ((float) Math.PI / 3f) * ((float) mouthTicks / 20f);
        }
        else if (mouthTicks >= 15 && mouthTicks < 30)
        {
            mouthAngle = ((float) Math.PI / 3f) * (1f - ((float) mouthTicks / 20f));
        }
        else
        {
            mouthAngle = 0;
        }

        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.frontRightLegTop.rotateAngleX = MathHelper.cos(par1 * 0.4662F) * 0.8F * par2;
        this.frontLeftLegTop.rotateAngleX = MathHelper.cos(par1 * 0.4662F + (float) Math.PI) * 0.8F * par2;
        this.backRightLegTop.rotateAngleX = MathHelper.cos(par1 * 0.4662F + (float) Math.PI) * 0.8F * par2;
        this.backLeftLegTop.rotateAngleX = MathHelper.cos(par1 * 0.4662F) * 0.8F * par2;
        this.mouthBottom.rotateAngleX = 0.0873F + mouthAngle;

        mane1.isHidden = false;
        mane2.isHidden = false;
        mane3.isHidden = false;
    }

    public void setRotation(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}