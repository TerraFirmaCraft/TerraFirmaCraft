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
import net.dries007.tfc.objects.entity.animal.EntityTurkeyTFC;

/**
 * ModelTurkeyTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelTurkeyTFC extends ModelBase
{
    public ModelRenderer wingRMain;
    public ModelRenderer body;
    public ModelRenderer head;
    public ModelRenderer wingLMain;
    public ModelRenderer neck;
    public ModelRenderer legRTop;
    public ModelRenderer legLTop;
    public ModelRenderer chest;
    public ModelRenderer neck2;
    public ModelRenderer neck3;
    public ModelRenderer wingRFront;
    public ModelRenderer wingRBack;
    public ModelRenderer wingRTip;
    public ModelRenderer nose;
    public ModelRenderer beak;
    public ModelRenderer wingLBack;
    public ModelRenderer wingLFront;
    public ModelRenderer wingLTip;
    public ModelRenderer legRBottom;
    public ModelRenderer legRFeet;
    public ModelRenderer legLBottom;
    public ModelRenderer legLFeet;
    public ModelRenderer thingy;
    public ModelRenderer tailBase;
    public ModelRenderer tailFMain;
    public ModelRenderer tails1;
    public ModelRenderer tails2;
    public ModelRenderer tails3;
    public ModelRenderer tails4;
    public ModelRenderer tails5;
    public ModelRenderer tails6;
    public ModelRenderer tails7;
    public ModelRenderer tails8;
    public ModelRenderer tails9;
    public ModelRenderer tails10;
    public ModelRenderer tails11;
    public ModelRenderer tailsf1;
    public ModelRenderer tailsf2;
    public ModelRenderer tailsf3;
    public ModelRenderer tailsf4;
    public ModelRenderer tailsf5;
    public ModelRenderer tailsf6;
    public ModelRenderer tailsf7;
    public ModelRenderer tailsf8;
    public ModelRenderer tailsf9;
    public ModelRenderer tailsf10;
    public ModelRenderer tailsf11;

    public ModelTurkeyTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        body = new ModelRenderer(this, 28, 45);
        body.setRotationPoint(0.0F, 14.5F, -0.5F);
        body.addBox(-4.0F, -4.0F, -5.0F, 8, 9, 10, 0.0F);
        setRotateAngle(body, -0.08726646259971647F, 0.0F, 0.0F);
        legLBottom = new ModelRenderer(this, 2, 44);
        legLBottom.setRotationPoint(0.0F, 3.0F, -1.0F);
        legLBottom.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
        neck = new ModelRenderer(this, 37, 22);
        neck.setRotationPoint(0.0F, 16.4F, -7.8F);
        neck.addBox(-2.0F, -5.0F, -2.0F, 4, 5, 4, -0.2F);
        setRotateAngle(neck, 0.08726646259971647F, 0.0F, 0.0F);
        legLTop = new ModelRenderer(this, 0, 47);
        legLTop.setRotationPoint(2.4F, 18.8F, 0.5F);
        legLTop.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.2F);
        head = new ModelRenderer(this, 0, 20);
        head.setRotationPoint(0.0F, 5.8F, -7.2F);
        head.addBox(-2.5F, -2.0F, -2.0F, 5, 4, 4, 0.1F);
        chest = new ModelRenderer(this, 31, 31);
        chest.setRotationPoint(0.0F, 14.0F, -5.8F);
        chest.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
        setRotateAngle(chest, -0.3490658503988659F, 0.0F, 0.0F);
        wingLBack = new ModelRenderer(this, 23, 16);
        wingLBack.mirror = true;
        wingLBack.setRotationPoint(-0.1F, 0.0F, 2.5F);
        wingLBack.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotateAngle(wingLBack, -0.17453292519943295F, 0.0F, 0.0F);
        wingRBack = new ModelRenderer(this, 23, 16);
        wingRBack.setRotationPoint(0.1F, 0.0F, 2.5F);
        wingRBack.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotateAngle(wingRBack, -0.17453292519943295F, 0.0F, 0.0F);
        wingLMain = new ModelRenderer(this, 20, 27);
        wingLMain.mirror = true;
        wingLMain.setRotationPoint(4.5F, 11.5F, -0.5F);
        wingLMain.addBox(-0.5F, 0.0F, -2.5F, 1, 5, 5, 0.0F);
        setRotateAngle(wingLMain, -0.08726646259971647F, 0.0F, 0.0F);
        wingLFront = new ModelRenderer(this, 23, 21);
        wingLFront.mirror = true;
        wingLFront.setRotationPoint(0.0F, 0.0F, -4.5F);
        wingLFront.addBox(-0.5F, 0.0F, 0.0F, 1, 4, 2, 0.0F);
        wingRMain = new ModelRenderer(this, 20, 27);
        wingRMain.setRotationPoint(-4.5F, 11.5F, -0.5F);
        wingRMain.addBox(-0.5F, 0.0F, -2.5F, 1, 5, 5, 0.0F);
        setRotateAngle(wingRMain, -0.08726646259971647F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 5, 17);
        nose.setRotationPoint(0.0F, 0.8F, -1.8F);
        nose.addBox(-1.5F, -1.0F, -1.0F, 3, 2, 1, 0.0F);
        beak = new ModelRenderer(this, 4, 12);
        beak.setRotationPoint(0.0F, 0.25F, -1.5F);
        beak.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 3, 0.0F);
        neck3 = new ModelRenderer(this, 37, 7);
        neck3.setRotationPoint(0.0F, 8.78F, -7.56F);
        neck3.addBox(-2.0F, -1.5F, -1.5F, 4, 3, 4, -0.22F);
        legRBottom = new ModelRenderer(this, 2, 44);
        legRBottom.setRotationPoint(0.0F, 3.0F, -1.0F);
        legRBottom.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
        wingLTip = new ModelRenderer(this, 24, 14);
        wingLTip.mirror = true;
        wingLTip.setRotationPoint(0.0F, 0.0F, 2.0F);
        wingLTip.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        neck2 = new ModelRenderer(this, 37, 14);
        neck2.setRotationPoint(0.0F, 10.72F, -8.1F);
        neck2.addBox(-2.0F, -2.0F, -1.5F, 4, 4, 4, -0.21F);
        setRotateAngle(neck2, -0.4363323129985824F, 0.0F, 0.0F);
        legRFeet = new ModelRenderer(this, -1, 39);
        legRFeet.setRotationPoint(0.0F, 2.0F, 0.5F);
        legRFeet.addBox(-1.5F, 0.0F, -2.5F, 3, 1, 4, 0.0F);
        setRotateAngle(legRFeet, -0.10471975511965977F, 0.0F, 0.0F);
        wingRFront = new ModelRenderer(this, 23, 21);
        wingRFront.setRotationPoint(0.0F, 0.0F, -4.5F);
        wingRFront.addBox(-0.5F, 0.0F, 0.0F, 1, 4, 2, 0.0F);
        legLFeet = new ModelRenderer(this, -1, 39);
        legLFeet.setRotationPoint(0.0F, 2.0F, 0.5F);
        legLFeet.addBox(-1.5F, 0.0F, -2.5F, 3, 1, 4, 0.0F);
        setRotateAngle(legLFeet, -0.10471975511965977F, 0.0F, 0.0F);
        legRTop = new ModelRenderer(this, 0, 47);
        legRTop.setRotationPoint(-2.4F, 18.8F, 0.5F);
        legRTop.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.2F);
        wingRTip = new ModelRenderer(this, 24, 14);
        wingRTip.setRotationPoint(0.0F, 0.0F, 2.0F);
        wingRTip.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        thingy = new ModelRenderer(this, 7, 8);
        thingy.setRotationPoint(-1.0F, -1.01F, 0.8F);
        thingy.addBox(0.0F, 0.0F, -1.0F, 0, 3, 1, 0.0F);
        setRotateAngle(thingy, 0.0F, 0.0F, 0.08726646259971647F);
        tailBase = new ModelRenderer(this, 7, 54);
        tailBase.setRotationPoint(0.0F, -0.8F, 5.5F);
        tailBase.addBox(-3.5F, -2.5F, -1.5F, 7, 7, 3, 0.0F);
        setRotateAngle(tailBase, -0.17453292519943295F, 0.0F, 0.0F);
        tailFMain = new ModelRenderer(this, 10, 46);
        tailFMain.setRotationPoint(0.0F, -0.8F, 7.5F);
        tailFMain.addBox(-2.0F, -1.5F, -1.5F, 4, 4, 3, 0.0F);
        setRotateAngle(tailFMain, -0.3490658503988659F, 0.0F, 0.0F);

        tails1 = new ModelRenderer(this, 0, 0);
        tails1.setRotationPoint(-1.0F, 13.0F, 5.5F);
        tails1.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails1, -0.3490658503988659F, -0.17453292519943295F, -1.3089969389957472F);
        tails2 = new ModelRenderer(this, 0, 0);
        tails2.setRotationPoint(-0.8F, 13.0F, 5.4F);
        tails2.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails2, -0.3490658503988659F, -0.13962634015954636F, -1.0471975511965976F);
        tails3 = new ModelRenderer(this, 0, 0);
        tails3.setRotationPoint(-0.6F, 13.0F, 5.3F);
        tails3.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails3, -0.3490658503988659F, -0.10471975511965977F, -0.7853981633974483F);
        tails4 = new ModelRenderer(this, 0, 0);
        tails4.setRotationPoint(-0.4F, 13.0F, 5.2F);
        tails4.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails4, -0.3490658503988659F, -0.06981317007977318F, -0.5235987755982988F);
        tails5 = new ModelRenderer(this, 0, 0);
        tails5.setRotationPoint(-0.2F, 13.0F, 5.1F);
        tails5.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails5, -0.3490658503988659F, -0.03490658503988659F, -0.2617993877991494F);
        tails6 = new ModelRenderer(this, 0, 0);
        tails6.setRotationPoint(0.0F, 13.0F, 5.0F);
        tails6.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails6, -0.3490658503988659F, 0.0F, 0.0F);
        tails7 = new ModelRenderer(this, 0, 0);
        tails7.setRotationPoint(0.2F, 13.0F, 5.1F);
        tails7.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails7, -0.3490658503988659F, 0.03490658503988659F, 0.2617993877991494F);
        tails8 = new ModelRenderer(this, 0, 0);
        tails8.setRotationPoint(0.4F, 13.0F, 5.2F);
        tails8.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails8, -0.3490658503988659F, 0.06981317007977318F, 0.5235987755982988F);
        tails9 = new ModelRenderer(this, 0, 0);
        tails9.setRotationPoint(0.6F, 13.0F, 5.3F);
        tails9.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails9, -0.3490658503988659F, 0.10471975511965977F, 0.7853981633974483F);
        tails10 = new ModelRenderer(this, 0, 0);
        tails10.setRotationPoint(0.8F, 13.0F, 5.4F);
        tails10.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails10, -0.3490658503988659F, 0.13962634015954636F, 1.0471975511965976F);
        tails11 = new ModelRenderer(this, 0, 0);
        tails11.setRotationPoint(1.0F, 13.0F, 5.5F);
        tails11.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails11, -0.3490658503988659F, 0.17453292519943295F, 1.3089969389957472F);

        tailsf1 = new ModelRenderer(this, 0, 0);
        tailsf1.setRotationPoint(-2.0F, 14.0F, 5.0F);
        tailsf1.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf1, -1.6390387005478748F, -0.17453292519943295F, -1.3089969389957472F);
        tailsf2 = new ModelRenderer(this, 0, 0);
        tailsf2.setRotationPoint(-2.0F, 14.0F, 5.0F);
        tailsf2.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf2, -1.6845917940249266F, -0.13962634015954636F, -1.0471975511965976F);
        tailsf3 = new ModelRenderer(this, 0, 0);
        tailsf3.setRotationPoint(-1.0F, 13.0F, 5.0F);
        tailsf3.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf3, -1.8212510744560826F, -0.10471975511965977F, -0.7853981633974483F);
        tailsf4 = new ModelRenderer(this, 0, 0);
        tailsf4.setRotationPoint(0.0F, 13.0F, 5.0F);
        tailsf4.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf4, -1.8212510744560826F, -0.06981317007977318F, -0.5235987755982988F);
        tailsf5 = new ModelRenderer(this, 0, 0);
        tailsf5.setRotationPoint(0.0F, 13.0F, 5.0F);
        tailsf5.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf5, -1.9577358219620393F, -0.03490658503988659F, -0.2617993877991494F);
        tailsf6 = new ModelRenderer(this, 0, 0);
        tailsf6.setRotationPoint(0.0F, 13.0F, 5.0F);
        tailsf6.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf6, -1.8668041679331349F, 0.0F, 0.0F);
        tailsf7 = new ModelRenderer(this, 0, 0);
        tailsf7.setRotationPoint(0.0F, 12.0F, 5.0F);
        tailsf7.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf7, -1.8212510744560826F, 0.03490658503988659F, 0.2617993877991494F);
        tailsf8 = new ModelRenderer(this, 0, 0);
        tailsf8.setRotationPoint(1.0F, 14.0F, 5.0F);
        tailsf8.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf8, -1.9123572614101867F, 0.06981317007977318F, 0.5235987755982988F);
        tailsf9 = new ModelRenderer(this, 0, 0);
        tailsf9.setRotationPoint(1.0F, 15.0F, 5.0F);
        tailsf9.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf9, -1.7756979809790308F, 0.10471975511965977F, 0.7853981633974483F);
        tailsf10 = new ModelRenderer(this, 0, 0);
        tailsf10.setRotationPoint(2.0F, 13.0F, 5.0F);
        tailsf10.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf10, -1.7756979809790308F, 0.13962634015954636F, 1.0471975511965976F);
        tailsf11 = new ModelRenderer(this, 0, 0);
        tailsf11.setRotationPoint(3.0F, 14.0F, 5.0F);
        tailsf11.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tailsf11, -1.730144887501979F, 0.17453292519943295F, 1.3089969389957472F);

        legLTop.addChild(legLBottom);
        body.addChild(tailFMain);
        wingLMain.addChild(wingLBack);
        wingRMain.addChild(wingRBack);
        wingLMain.addChild(wingLFront);
        body.addChild(tailBase);
        head.addChild(nose);
        nose.addChild(beak);
        beak.addChild(thingy);
        legRTop.addChild(legRBottom);
        wingLBack.addChild(wingLTip);
        legRBottom.addChild(legRFeet);
        wingRMain.addChild(wingRFront);
        legLBottom.addChild(legLFeet);
        wingRBack.addChild(wingRTip);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

        EntityTurkeyTFC turkey = ((EntityTurkeyTFC) entity);

        float percent = (float) turkey.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (turkey.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            if (percent < 0.5)
            {
                tails1.isHidden = true;
                tails2.isHidden = true;
                tails3.isHidden = true;
                tails4.isHidden = true;
                tails5.isHidden = true;
                tails6.isHidden = true;
                tails7.isHidden = true;
                tails8.isHidden = true;
                tails9.isHidden = true;
                tails10.isHidden = true;
                tails11.isHidden = true;
                tailsf1.isHidden = false;
                tailsf2.isHidden = false;
                tailsf3.isHidden = false;
                tailsf4.isHidden = false;
                tailsf5.isHidden = false;
                tailsf6.isHidden = false;
                tailsf7.isHidden = false;
                tailsf8.isHidden = false;
                tailsf9.isHidden = false;
                tailsf10.isHidden = false;
                tailsf11.isHidden = false;
            }

            else
            {
                tailFMain.isHidden = true;
                tailsf1.isHidden = true;
                tailsf2.isHidden = true;
                tailsf3.isHidden = true;
                tailsf4.isHidden = true;
                tailsf5.isHidden = true;
                tailsf6.isHidden = true;
                tailsf7.isHidden = true;
                tailsf8.isHidden = true;
                tailsf9.isHidden = true;
                tailsf10.isHidden = true;
                tailsf11.isHidden = true;
            }
        }
        else
        {
            tails1.isHidden = true;
            tails2.isHidden = true;
            tails3.isHidden = true;
            tails4.isHidden = true;
            tails5.isHidden = true;
            tails6.isHidden = true;
            tails7.isHidden = true;
            tails8.isHidden = true;
            tails9.isHidden = true;
            tails10.isHidden = true;
            tails11.isHidden = true;
            tailsf1.isHidden = false;
            tailsf2.isHidden = false;
            tailsf3.isHidden = false;
            tailsf4.isHidden = false;
            tailsf5.isHidden = false;
            tailsf6.isHidden = false;
            tailsf7.isHidden = false;
            tailsf8.isHidden = false;
            tailsf9.isHidden = false;
            tailsf10.isHidden = false;
            tailsf11.isHidden = false;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        body.render(par7);
        neck.render(par7);
        legLTop.render(par7);
        head.render(par7);
        chest.render(par7);
        wingLMain.render(par7);
        wingRMain.render(par7);
        neck3.render(par7);
        neck2.render(par7);
        legRTop.render(par7);
        tails1.render(par7);
        tails2.render(par7);
        tails3.render(par7);
        tails4.render(par7);
        tails5.render(par7);
        tails6.render(par7);
        tails7.render(par7);
        tails8.render(par7);
        tails9.render(par7);
        tails10.render(par7);
        tails11.render(par7);
        tailsf1.render(par7);
        tailsf2.render(par7);
        tailsf3.render(par7);
        tailsf4.render(par7);
        tailsf5.render(par7);
        tailsf6.render(par7);
        tailsf7.render(par7);
        tailsf8.render(par7);
        tailsf9.render(par7);
        tailsf10.render(par7);
        tailsf11.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head.rotateAngleX = -(par5 / (180F / (float) Math.PI));
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);

        legRTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        legLTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;

        tailFMain.isHidden = false;
        tailsf1.isHidden = false;
        tailsf2.isHidden = false;
        tailsf3.isHidden = false;
        tailsf4.isHidden = false;
        tailsf5.isHidden = false;
        tailsf6.isHidden = false;
        tailsf7.isHidden = false;
        tailsf8.isHidden = false;
        tailsf9.isHidden = false;
        tailsf10.isHidden = false;
        tailsf11.isHidden = false;
        tails1.isHidden = false;
        tails2.isHidden = false;
        tails3.isHidden = false;
        tails4.isHidden = false;
        tails5.isHidden = false;
        tails6.isHidden = false;
        tails7.isHidden = false;
        tails8.isHidden = false;
        tails9.isHidden = false;
        tails10.isHidden = false;
        tails11.isHidden = false;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}