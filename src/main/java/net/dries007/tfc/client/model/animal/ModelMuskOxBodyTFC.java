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

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityMuskOxTFC;

/**
 * ModelMuskOxTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelMuskOxBodyTFC extends ModelBase
{
    public ModelRenderer bodyMain;
    public ModelRenderer legBackLeft;
    public ModelRenderer legBackRight;
    public ModelRenderer legFrontLeft;
    public ModelRenderer legFrontRight;
    public ModelRenderer headBase;
    public ModelRenderer bodyShoulder;
    public ModelRenderer hump;
    public ModelRenderer bodyHair;
    public ModelRenderer head;
    public ModelRenderer hornRightBase;
    public ModelRenderer beard;
    public ModelRenderer snout;
    public ModelRenderer hornLeftBase;
    public ModelRenderer hornRight1;
    public ModelRenderer hornRight2;
    public ModelRenderer hornRight3;
    public ModelRenderer hornLeft1;
    public ModelRenderer hornLeft2;
    public ModelRenderer hornLeft3;
    public ModelRenderer hornCenter;
    public ModelRenderer hornRightF1;
    public ModelRenderer hornRightF2;
    public ModelRenderer hornLeftF1;
    public ModelRenderer hornLeftF2;
    public ModelRenderer nose;
    public ModelRenderer mouthBottomA;
    public ModelRenderer mouthBottomB;

    public ModelMuskOxBodyTFC()
    {
        textureWidth = 128;
        textureHeight = 128;

        hornRight3 = new ModelRenderer(this, 94, 14);
        hornRight3.setRotationPoint(3.0F, 0.0F, 0.0F);
        hornRight3.addBox(-0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornRight3, 0.0F, 1.1676252695842066F, 0.32812189937493397F);
        headBase = new ModelRenderer(this, 0, 0);
        headBase.setRotationPoint(0.0F, 5.0F, -12.5F);
        headBase.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        hornLeft3 = new ModelRenderer(this, 94, 14);
        hornLeft3.setRotationPoint(3.0F, 0.0F, 0.0F);
        hornLeft3.addBox(-0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornLeft3, 0.0F, -1.1676252695842066F, 0.32812189937493397F);
        legBackRight = new ModelRenderer(this, 78, 0);
        legBackRight.setRotationPoint(-4.5F, 14.5F, 8.0F);
        legBackRight.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        hornRightBase = new ModelRenderer(this, 94, 0);
        hornRightBase.setRotationPoint(-1.5F, -5.2F, 0.0F);
        hornRightBase.addBox(-2.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        setRotateAngle(hornRightBase, 0.0F, 0.0F, -0.2617993877991494F);
        head = new ModelRenderer(this, 102, 40);
        head.setRotationPoint(0.0F, 1.5F, -3.0F);
        head.addBox(-3.0F, -4.5F, -1.0F, 6, 7, 6, 0.0F);
        setRotateAngle(head, 0.10471975511965977F, 0.0F, 0.0F);
        bodyMain = new ModelRenderer(this, 2, 70);
        bodyMain.setRotationPoint(0.0F, 10.0F, 0.0F);
        bodyMain.addBox(-4.0F, -8.0F, -8.0F, 8, 15, 22, 0.0F);
        legFrontRight = new ModelRenderer(this, 78, 18);
        legFrontRight.setRotationPoint(-4.5F, 14.5F, -5.5F);
        legFrontRight.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        hornRight1 = new ModelRenderer(this, 94, 5);
        hornRight1.setRotationPoint(-1.0F, 1.0F, 1.5F);
        hornRight1.addBox(0.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F);
        setRotateAngle(hornRight1, 0.0F, 0.0F, 2.6179938779914944F);
        beard = new ModelRenderer(this, 106, 23);
        beard.setRotationPoint(0.0F, 0.0F, -1.0F);
        beard.addBox(0.0F, -2.5F, -1.0F, 0, 9, 6, 0.0F);
        setRotateAngle(beard, -0.3490658503988659F, 0.0F, 0.0F);
        hornLeft2 = new ModelRenderer(this, 94, 10);
        hornLeft2.setRotationPoint(2.5F, 0.7F, 0.0F);
        hornLeft2.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(hornLeft2, 0.0F, -1.1676252695842066F, 0.3490658503988659F);
        legBackLeft = new ModelRenderer(this, 78, 0);
        legBackLeft.mirror = true;
        legBackLeft.setRotationPoint(3.5F, 14.5F, 8.0F);
        legBackLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        hornLeftBase = new ModelRenderer(this, 94, 0);
        hornLeftBase.setRotationPoint(1.5F, -5.2F, 0.0F);
        hornLeftBase.addBox(-1.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        setRotateAngle(hornLeftBase, 0.0F, 0.0F, 0.2617993877991494F);
        hornLeft1 = new ModelRenderer(this, 94, 5);
        hornLeft1.setRotationPoint(1.0F, 1.0F, 1.5F);
        hornLeft1.addBox(0.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F);
        setRotateAngle(hornLeft1, 0.0F, 3.141592653589793F, -2.6179938779914944F);
        bodyHair = new ModelRenderer(this, 1, 1);
        bodyHair.setRotationPoint(0.0F, 12.0F, -1.0F);
        bodyHair.addBox(-6.5F, -8.0F, -8.0F, 13, 15, 21, 0.0F);
        bodyShoulder = new ModelRenderer(this, 0, 40);
        bodyShoulder.setRotationPoint(0.0F, 9.0F, -7.0F);
        bodyShoulder.addBox(-5.0F, -8.0F, -5.0F, 10, 14, 10, 0.0F);
        hornRight2 = new ModelRenderer(this, 94, 10);
        hornRight2.setRotationPoint(2.5F, 0.7F, 0.0F);
        hornRight2.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(hornRight2, 0.0F, 1.1676252695842066F, 0.3490658503988659F);
        legFrontLeft = new ModelRenderer(this, 78, 18);
        legFrontLeft.mirror = true;
        legFrontLeft.setRotationPoint(3.5F, 14.5F, -5.5F);
        legFrontLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        hump = new ModelRenderer(this, 0, 110);
        hump.setRotationPoint(0.0F, 0.4F, -9.0F);
        hump.addBox(-2.0F, -1.2F, -2.2F, 4, 4, 9, 0.0F);
        snout = new ModelRenderer(this, 106, 15);
        snout.setRotationPoint(0.0F, 2.0F, -2.5F);
        snout.addBox(-2.0F, -4.5F, -1.0F, 4, 3, 6, 0.0F);
        setRotateAngle(snout, 0.3490658503988659F, 0.0F, 0.0F);
        hornCenter = new ModelRenderer(this, 94, 0);
        hornCenter.setRotationPoint(0.0F, -4.7F, 0.0F);
        hornCenter.addBox(-1.0F, -0.5F, 0.1F, 2, 1, 3, 0.0F);
        hornRightF1 = new ModelRenderer(this, 94, 10);
        hornRightF1.setRotationPoint(3.0F, 0.4F, 0.0F);
        hornRightF1.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornRightF1, -0.3141592653589793F, 0.2617993877991494F, -0.3141592653589793F);
        hornRightF2 = new ModelRenderer(this, 94, 14);
        hornRightF2.setRotationPoint(1.0F, 0.0F, 0.0F);
        hornRightF2.addBox(-0.2F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornRightF2, -0.5235987755982988F, 0.40980330836826856F, -0.9075712110370513F);
        hornLeftF1 = new ModelRenderer(this, 94, 10);
        hornLeftF1.setRotationPoint(3.0F, 0.4F, 0.0F);
        hornLeftF1.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornLeftF1, -0.3141592653589793F, -0.2617993877991494F, -0.3141592653589793F);
        hornLeftF2 = new ModelRenderer(this, 94, 14);
        hornLeftF2.setRotationPoint(1.0F, 0.0F, 0.0F);
        hornLeftF2.addBox(-0.2F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornLeftF2, -0.5235987755982988F, -0.40980330836826856F, -0.9075712110370513F);
        nose = new ModelRenderer(this, 111, 62);
        nose.setRotationPoint(0.0F, -3.7F, -0.2F);
        nose.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F);
        mouthBottomA = new ModelRenderer(this, 99, 55);
        mouthBottomA.setRotationPoint(-0.3F, -0.8F, 1.21F);
        mouthBottomA.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 4, 0.0F);
        mouthBottomB = new ModelRenderer(this, 113, 55);
        mouthBottomB.mirror = true;
        mouthBottomB.setRotationPoint(0.3F, -0.8F, 1.2F);
        mouthBottomB.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 4, 0.0F);

        hornRight2.addChild(hornRight3);
        hornLeft2.addChild(hornLeft3);
        head.addChild(hornRightBase);
        headBase.addChild(head);
        hornRightBase.addChild(hornRight1);
        head.addChild(beard);
        hornLeft1.addChild(hornLeft2);
        head.addChild(hornLeftBase);
        hornLeftBase.addChild(hornLeft1);
        hornRight1.addChild(hornRight2);
        head.addChild(snout);
        head.addChild(hornCenter);
        hornRightF1.addChild(hornRightF2);
        hornLeftF1.addChild(hornLeftF2);
        hornRight1.addChild(hornRightF1);
        hornLeft1.addChild(hornLeftF1);
        snout.addChild(mouthBottomB);
        snout.addChild(mouthBottomA);
        snout.addChild(nose);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityMuskOxTFC muskox = ((EntityMuskOxTFC) entity);

        float percent = (float) muskox.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (muskox.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            if (percent < 0.5)
            {
                hornCenter.isHidden = true;
                hornRightBase.isHidden = true;
                hornLeftBase.isHidden = true;
                hornRightF1.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1.isHidden = true;
                hornLeftF2.isHidden = true;
                hornRight1.isHidden = true;
                hornRight2.isHidden = true;
                hornRight3.isHidden = true;
                hornLeft1.isHidden = true;
                hornLeft2.isHidden = true;
                hornLeft3.isHidden = true;
            }
            else if (percent < 0.75)
            {
                hornRightF1.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1.isHidden = true;
                hornLeftF2.isHidden = true;
                hornRight1.isHidden = true;
                hornRight2.isHidden = true;
                hornRight3.isHidden = true;
                hornLeft1.isHidden = true;
                hornLeft2.isHidden = true;
                hornLeft3.isHidden = true;
            }

            else
            {
                hornRightF1.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1.isHidden = true;
                hornLeftF2.isHidden = true;
            }
        }

        else
        {
            if (percent < 0.5)
            {
                hornCenter.isHidden = true;
                hornRightBase.isHidden = true;
                hornLeftBase.isHidden = true;
                hornRightF1.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1.isHidden = true;
                hornLeftF2.isHidden = true;
                hornRight1.isHidden = true;
                hornRight2.isHidden = true;
                hornRight3.isHidden = true;
                hornLeft1.isHidden = true;
                hornLeft2.isHidden = true;
                hornLeft3.isHidden = true;
            }
            else if (percent < 0.75)
            {
                hornRightF1.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1.isHidden = true;
                hornLeftF2.isHidden = true;
                hornRight1.isHidden = true;
                hornRight2.isHidden = true;
                hornRight3.isHidden = true;
                hornLeft1.isHidden = true;
                hornLeft2.isHidden = true;
                hornLeft3.isHidden = true;
            }
            else
            {
                hornRight2.isHidden = true;
                hornRight3.isHidden = true;
                hornLeft2.isHidden = true;
                hornLeft3.isHidden = true;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        legFrontRight.render(par7);
        bodyShoulder.render(par7);
        bodyMain.render(par7);
        legFrontLeft.render(par7);
        bodyHair.render(par7);
        legBackLeft.render(par7);
        hump.render(par7);
        headBase.render(par7);
        legBackRight.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.headBase.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.headBase.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.legFrontRight.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legFrontLeft.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legBackRight.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legBackLeft.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;

        hornCenter.isHidden = false;
        hornRightBase.isHidden = false;
        hornLeftBase.isHidden = false;
        hornRightF1.isHidden = false;
        hornRightF2.isHidden = false;
        hornLeftF1.isHidden = false;
        hornLeftF2.isHidden = false;
        hornRight1.isHidden = false;
        hornRight2.isHidden = false;
        hornRight3.isHidden = false;
        hornLeft1.isHidden = false;
        hornLeft2.isHidden = false;
        hornLeft3.isHidden = false;
    }

    private void setRotateAngle(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}