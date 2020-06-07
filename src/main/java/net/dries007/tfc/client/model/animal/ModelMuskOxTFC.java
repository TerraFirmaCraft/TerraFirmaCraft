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
public class ModelMuskOxTFC extends ModelBase {
    public ModelRenderer bodyMain;
    public ModelRenderer legBackLeft;
    public ModelRenderer legBackRight;
    public ModelRenderer legFrontLeft;
    public ModelRenderer bodyShoulderQiviut;
    public ModelRenderer legFrontRight;
    public ModelRenderer headBase;
    public ModelRenderer bodyShoulder;
    public ModelRenderer neck;
    public ModelRenderer bodyHairQiviut;
    public ModelRenderer bodyHair;
    public ModelRenderer bodyMainQiviut;
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

    public ModelMuskOxTFC() {
        textureWidth = 128;
        textureHeight = 128;
        hornRight3 = new ModelRenderer(this, 94, 15);
        hornRight3.setRotationPoint(3.0F, 0.0F, 0.0F);
        hornRight3.addBox(-0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornRight3, 0.0F, 1.1676252695842066F, 0.32812189937493397F);
        headBase = new ModelRenderer(this, 0, 0);
        headBase.setRotationPoint(0.0F, 1.0F, -13.0F);
        headBase.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        hornLeft3 = new ModelRenderer(this, 94, 14);
        hornLeft3.setRotationPoint(3.0F, 0.0F, 0.0F);
        hornLeft3.addBox(-0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornLeft3, 0.0F, -1.1676252695842066F, 0.32812189937493397F);
        legBackRight = new ModelRenderer(this, 75, 0);
        legBackRight.setRotationPoint(-4.0F, 14.5F, 8.0F);
        legBackRight.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        hornRightBase = new ModelRenderer(this, 94, 0);
        hornRightBase.setRotationPoint(-1.5F, -5.2F, 0.0F);
        hornRightBase.addBox(-2.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        setRotateAngle(hornRightBase, 0.0F, 0.0F, -0.2617993877991494F);
        bodyMainQiviut = new ModelRenderer(this, 61, 46);
        bodyMainQiviut.setRotationPoint(0.0F, 10.0F, 0.0F);
        bodyMainQiviut.addBox(-4.5F, -8.5F, -8.5F, 9, 16, 23, 0.0F);
        head = new ModelRenderer(this, 102, 51);
        head.setRotationPoint(0.0F, 5.5F, -1.5F);
        head.addBox(-3.0F, -4.5F, -1.0F, 6, 7, 6, 0.0F);
        setRotateAngle(head, 0.8726646259971648F, 0.0F, 0.0F);
        bodyMain = new ModelRenderer(this, 0, 68);
        bodyMain.setRotationPoint(0.0F, 10.0F, 0.0F);
        bodyMain.addBox(-4.0F, -8.0F, -8.0F, 8, 15, 22, 0.0F);
        legFrontRight = new ModelRenderer(this, 75, 18);
        legFrontRight.setRotationPoint(-4.0F, 14.5F, -5.5F);
        legFrontRight.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        bodyHairQiviut = new ModelRenderer(this, 55, 88);
        bodyHairQiviut.setRotationPoint(0.0F, 11.0F, -1.5F);
        bodyHairQiviut.addBox(-7.0F, -8.0F, -8.0F, 14, 16, 22, 0.0F);
        hornRight1 = new ModelRenderer(this, 94, 5);
        hornRight1.setRotationPoint(-1.0F, 1.0F, 1.5F);
        hornRight1.addBox(0.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F);
        setRotateAngle(hornRight1, 0.0F, 0.0F, 2.6179938779914944F);
        beard = new ModelRenderer(this, 106, 23);
        beard.setRotationPoint(0.0F, 0.0F, -1.0F);
        beard.addBox(0.0F, -2.5F, -1.0F, 0, 9, 6, 0.0F);
        setRotateAngle(beard, -1.2217304763960306F, 0.0F, 0.0F);
        hornLeft2 = new ModelRenderer(this, 94, 10);
        hornLeft2.setRotationPoint(2.5F, 0.7F, 0.0F);
        hornLeft2.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(hornLeft2, 0.0F, -1.1676252695842066F, 0.3490658503988659F);
        legBackLeft = new ModelRenderer(this, 75, 0);
        legBackLeft.mirror = true;
        legBackLeft.setRotationPoint(4.0F, 14.5F, 8.0F);
        legBackLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        hornLeftBase = new ModelRenderer(this, 94, 0);
        hornLeftBase.setRotationPoint(1.5F, -5.2F, 0.0F);
        hornLeftBase.addBox(-1.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        setRotateAngle(hornLeftBase, 0.0F, 0.0F, 0.2617993877991494F);
        hornLeft1 = new ModelRenderer(this, 94, 5);
        hornLeft1.setRotationPoint(1.0F, 1.0F, 1.5F);
        hornLeft1.addBox(0.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F);
        setRotateAngle(hornLeft1, 0.0F, 3.141592653589793F, -2.6179938779914944F);
        bodyHair = new ModelRenderer(this, 0, 0);
        bodyHair.setRotationPoint(0.0F, 12.0F, -1.0F);
        bodyHair.addBox(-6.5F, -8.0F, -8.0F, 13, 15, 21, 0.0F);
        bodyShoulder = new ModelRenderer(this, 0, 40);
        bodyShoulder.setRotationPoint(0.0F, 9.0F, -7.0F);
        bodyShoulder.addBox(-5.0F, -8.0F, -5.0F, 10, 14, 10, 0.0F);
        hornRight2 = new ModelRenderer(this, 94, 10);
        hornRight2.setRotationPoint(2.5F, 0.7F, 0.0F);
        hornRight2.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(hornRight2, 0.0F, 1.1676252695842066F, 0.3490658503988659F);
        legFrontLeft = new ModelRenderer(this, 75, 18);
        legFrontLeft.mirror = true;
        legFrontLeft.setRotationPoint(4.0F, 14.5F, -5.5F);
        legFrontLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        bodyShoulderQiviut = new ModelRenderer(this, 41, 38);
        bodyShoulderQiviut.setRotationPoint(0.0F, 9.0F, -7.0F);
        bodyShoulderQiviut.addBox(-5.5F, -8.5F, -5.5F, 11, 15, 11, 0.0F);
        neck = new ModelRenderer(this, 0, 110);
        neck.setRotationPoint(0.0F, 0.0F, -13.0F);
        neck.addBox(-2.0F, -1.2F, -2.2F, 4, 9, 9, 0.0F);
        snout = new ModelRenderer(this, 102, 40);
        snout.setRotationPoint(0.0F, 2.0F, -2.5F);
        snout.addBox(-2.0F, -4.5F, -1.0F, 4, 5, 6, 0.0F);
        setRotateAngle(snout, 0.3490658503988659F, 0.0F, 0.0F);
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
            //udders.isHidden = true;
        }
        else
        {
            //horn.isHidden = true;
            //horn2b.isHidden = true;
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
        bodyHairQiviut.render(par7);
        bodyMainQiviut.render(par7);
        neck.render(par7);
        headBase.render(par7);
        legBackRight.render(par7);
        bodyShoulderQiviut.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        /*this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.body.rotateAngleX = (float) Math.PI / 2F;
        this.udders.rotateAngleX = (float) Math.PI / 2F;
        this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        horn1.rotateAngleX = 0F;
        horn2.rotateAngleX = 0F;
        horn1.isHidden = false;
        horn1b.isHidden = false;
        horn2.isHidden = false;
        horn2b.isHidden = false;
        udders.isHidden = false;

         */
    }

    private void setRotateAngle(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}