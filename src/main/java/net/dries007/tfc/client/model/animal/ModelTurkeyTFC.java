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

import net.dries007.tfc.objects.entity.animal.EntityTurkeyTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
/**
 * ModelTurkeyTFC - Either Mojang or a mod author
 * Created using Tabula 7.1.0
 */
public class ModelTurkeyTFC extends ModelBase
{
    public ModelRenderer wingRMain;
    public ModelRenderer tails5;
    public ModelRenderer tails6;
    public ModelRenderer tails3;
    public ModelRenderer tails4;
    public ModelRenderer body;
    public ModelRenderer tails9;
    public ModelRenderer tails7;
    public ModelRenderer tails8;
    public ModelRenderer head;
    public ModelRenderer tails1;
    public ModelRenderer wingLMain;
    public ModelRenderer tails2;
    public ModelRenderer neck;
    public ModelRenderer tails11;
    public ModelRenderer tails10;
    public ModelRenderer legRTop;
    public ModelRenderer legLTop;
    public ModelRenderer chest;
    public ModelRenderer neck2;
    public ModelRenderer neck3;
    public ModelRenderer wingRFront;
    public ModelRenderer wingRBack;
    public ModelRenderer wingRTip;
    public ModelRenderer tailFMain;
    public ModelRenderer tailBase;
    public ModelRenderer tailFTip;
    public ModelRenderer beak;
    public ModelRenderer wingLBack;
    public ModelRenderer wingLFront;
    public ModelRenderer wingLTip;
    public ModelRenderer legRBottom;
    public ModelRenderer legRFeet;
    public ModelRenderer legLBottom;
    public ModelRenderer legLFeet;

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
        tails5 = new ModelRenderer(this, 0, 0);
        tails5.setRotationPoint(-0.2F, 13.0F, 5.1F);
        tails5.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails5, -0.3490658503988659F, -0.03490658503988659F, -0.2617993877991494F);
        neck = new ModelRenderer(this, 38, 22);
        neck.setRotationPoint(0.0F, 15.4F, -7.8F);
        neck.addBox(-2.0F, -5.0F, -2.0F, 4, 6, 3, -0.2F);
        setRotateAngle(neck, 0.08726646259971647F, 0.0F, 0.0F);
        tails4 = new ModelRenderer(this, 0, 0);
        tails4.setRotationPoint(-0.4F, 13.0F, 5.2F);
        tails4.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails4, -0.3490658503988659F, -0.06981317007977318F, -0.5235987755982988F);
        tails7 = new ModelRenderer(this, 0, 0);
        tails7.setRotationPoint(0.2F, 13.0F, 5.1F);
        tails7.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails7, -0.3490658503988659F, 0.03490658503988659F, 0.2617993877991494F);
        legLTop = new ModelRenderer(this, 0, 47);
        legLTop.setRotationPoint(2.4F, 19.0F, 0.5F);
        legLTop.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.2F);
        head = new ModelRenderer(this, 0, 20);
        head.setRotationPoint(-0.5F, 4.4F, -7.5F);
        head.addBox(-2.0F, -1.5F, -2.5F, 5, 4, 4, 0.2F);
        chest = new ModelRenderer(this, 31, 31);
        chest.setRotationPoint(0.0F, 14.0F, -5.8F);
        chest.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
        setRotateAngle(chest, -0.3490658503988659F, 0.0F, 0.0F);
        tailFMain = new ModelRenderer(this, 10, 46);
        tailFMain.setRotationPoint(0.0F, -0.8F, 7.5F);
        tailFMain.addBox(-2.0F, -1.5F, -1.5F, 4, 5, 3, 0.0F);
        setRotateAngle(tailFMain, -0.3490658503988659F, 0.0F, 0.0F);
        tails3 = new ModelRenderer(this, 0, 0);
        tails3.setRotationPoint(-0.6F, 13.0F, 5.3F);
        tails3.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails3, -0.3490658503988659F, -0.10471975511965977F, -0.7853981633974483F);
        wingLBack = new ModelRenderer(this, 23, 18);
        wingLBack.mirror = true;
        wingLBack.setRotationPoint(-0.1F, 0.0F, 2.5F);
        wingLBack.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotateAngle(wingLBack, -0.17453292519943295F, 0.0F, 0.0F);
        wingRBack = new ModelRenderer(this, 23, 18);
        wingRBack.setRotationPoint(0.1F, 0.0F, 2.5F);
        wingRBack.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotateAngle(wingRBack, -0.17453292519943295F, 0.0F, 0.0F);
        tailFTip = new ModelRenderer(this, 10, 39);
        tailFTip.setRotationPoint(0.0F, -1.0F, 1.0F);
        tailFTip.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
        wingLMain = new ModelRenderer(this, 20, 28);
        wingLMain.mirror = true;
        wingLMain.setRotationPoint(4.0F, 11.5F, -0.5F);
        wingLMain.addBox(-0.5F, 0.0F, -2.5F, 1, 4, 5, 0.0F);
        setRotateAngle(wingLMain, -0.08726646259971647F, 0.0F, -0.0F);
        wingLFront = new ModelRenderer(this, 23, 23);
        wingLFront.mirror = true;
        wingLFront.setRotationPoint(0.0F, 0.0F, -4.5F);
        wingLFront.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        wingRMain = new ModelRenderer(this, 20, 28);
        wingRMain.setRotationPoint(-4.0F, 11.5F, -0.5F);
        wingRMain.addBox(-0.5F, 0.0F, -2.5F, 1, 4, 5, 0.0F);
        setRotateAngle(wingRMain, -0.08726646259971647F, 0.0F, 0.0F);
        tails8 = new ModelRenderer(this, 0, 0);
        tails8.setRotationPoint(0.4F, 13.0F, 5.2F);
        tails8.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails8, -0.3490658503988659F, 0.06981317007977318F, 0.5235987755982988F);
        tails6 = new ModelRenderer(this, 0, 0);
        tails6.setRotationPoint(0.0F, 13.0F, 5.0F);
        tails6.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails6, -0.3490658503988659F, 0.0F, 0.0F);
        tails11 = new ModelRenderer(this, 0, 0);
        tails11.setRotationPoint(1.0F, 13.0F, 5.5F);
        tails11.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails11, -0.3490658503988659F, 0.17453292519943295F, 1.3089969389957472F);
        tailBase = new ModelRenderer(this, 7, 54);
        tailBase.setRotationPoint(0.0F, -0.8F, 5.5F);
        tailBase.addBox(-3.5F, -2.5F, -1.5F, 7, 7, 3, 0.0F);
        setRotateAngle(tailBase, -0.17453292519943295F, 0.0F, 0.0F);
        beak = new ModelRenderer(this, 3, 15);
        beak.setRotationPoint(0.0F, 1.0F, -3.5F);
        beak.addBox(-1.0F, -1.0F, -1.0F, 3, 2, 3, 0.0F);
        tails10 = new ModelRenderer(this, 0, 0);
        tails10.setRotationPoint(0.8F, 13.0F, 5.4F);
        tails10.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails10, -0.3490658503988659F, 0.13962634015954636F, 1.0471975511965976F);
        neck3 = new ModelRenderer(this, 38, 10);
        neck3.setRotationPoint(0.0F, 8.4F, -7.5F);
        neck3.addBox(-2.0F, -1.5F, -1.5F, 4, 2, 3, -0.2F);
        legRBottom = new ModelRenderer(this, 2, 44);
        legRBottom.setRotationPoint(0.0F, 3.0F, -1.0F);
        legRBottom.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
        tails2 = new ModelRenderer(this, 0, 0);
        tails2.setRotationPoint(-0.8F, 13.0F, 5.4F);
        tails2.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails2, -0.3490658503988659F, -0.13962634015954636F, -1.0471975511965976F);
        tails1 = new ModelRenderer(this, 0, 0);
        tails1.setRotationPoint(-1.0F, 13.0F, 5.5F);
        tails1.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails1, -0.3490658503988659F, -0.17453292519943295F, -1.3089969389957472F);
        wingLTip = new ModelRenderer(this, 24, 16);
        wingLTip.mirror = true;
        wingLTip.setRotationPoint(0.0F, 0.0F, 2.0F);
        wingLTip.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        neck2 = new ModelRenderer(this, 38, 15);
        neck2.setRotationPoint(0.0F, 9.7F, -8.1F);
        neck2.addBox(-2.0F, -2.0F, -1.5F, 4, 4, 3, -0.2F);
        setRotateAngle(neck2, -0.4363323129985824F, 0.0F, 0.0F);
        legRFeet = new ModelRenderer(this, -1, 40);
        legRFeet.setRotationPoint(0.0F, 2.0F, 0.0F);
        legRFeet.addBox(-1.5F, 0.0F, -2.5F, 3, 0, 4, 0.0F);
        tails9 = new ModelRenderer(this, 0, 0);
        tails9.setRotationPoint(0.6F, 13.0F, 5.3F);
        tails9.addBox(-1.5F, -13.0F, 0.0F, 3, 13, 0, 0.0F);
        setRotateAngle(tails9, -0.3490658503988659F, 0.10471975511965977F, 0.7853981633974483F);
        wingRFront = new ModelRenderer(this, 23, 23);
        wingRFront.setRotationPoint(0.0F, 0.0F, -4.5F);
        wingRFront.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        legLFeet = new ModelRenderer(this, -1, 40);
        legLFeet.setRotationPoint(0.0F, 2.0F, 0.0F);
        legLFeet.addBox(-1.5F, 0.0F, -2.5F, 3, 0, 4, 0.0F);
        legRTop = new ModelRenderer(this, 0, 47);
        legRTop.setRotationPoint(-2.4F, 19.0F, 0.5F);
        legRTop.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.2F);
        wingRTip = new ModelRenderer(this, 24, 16);
        wingRTip.setRotationPoint(0.0F, 0.0F, 2.0F);
        wingRTip.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);

        legLTop.addChild(legLBottom);
        body.addChild(tailFMain);
        wingLMain.addChild(wingLBack);
        wingRMain.addChild(wingRBack);
        tailFMain.addChild(tailFTip);
        wingLMain.addChild(wingLFront);
        body.addChild(tailBase);
        head.addChild(beak);
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

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        body.render(par7);
        tails5.render(par7);
        neck.render(par7);
        tails4.render(par7);
        tails7.render(par7);
        legLTop.render(par7);
        head.render(par7);
        chest.render(par7);
        tails3.render(par7);
        wingLMain.render(par7);
        wingRMain.render(par7);
        tails8.render(par7);
        tails6.render(par7);
        tails11.render(par7);
        tails10.render(par7);
        neck3.render(par7);
        tails2.render(par7);
        tails1.render(par7);
        neck2.render(par7);
        tails9.render(par7);
        legRTop.render(par7);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head.rotateAngleX = -(par5 / (180F / (float) Math.PI));
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);

        legRTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        legLTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        wingRMain.rotateAngleZ = par3;
        wingLMain.rotateAngleZ = -par3;
        wingRMain.rotateAngleX = 0;
        wingLMain.rotateAngleX = 0;
        //rightWing.setRotationPoint(-4.0F, 13, 0.0F);
        //leftWing.setRotationPoint(4.0F, 13, 0.0F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}