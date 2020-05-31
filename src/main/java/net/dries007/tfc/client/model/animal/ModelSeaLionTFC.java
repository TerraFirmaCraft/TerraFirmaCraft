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

import net.dries007.tfc.objects.entity.animal.EntitySeaLionTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
/**
 * ModelSeaLionTFC - Either Mojang or a mod author
 * Created using Tabula 7.1.0
 */
public class ModelSeaLionTFC extends ModelBase {
    public ModelRenderer backLFin1;
    public ModelRenderer frontLArm;
    public ModelRenderer neckbase;
    public ModelRenderer frontRArm;
    public ModelRenderer backRFin1;
    public ModelRenderer mainBody;
    public ModelRenderer head;
    public ModelRenderer backLFin2;
    public ModelRenderer frontLFin1;
    public ModelRenderer frontLFin2;
    public ModelRenderer neck;
    public ModelRenderer frontRFin1;
    public ModelRenderer frontRFin2;
    public ModelRenderer backRFin2;
    public ModelRenderer body2;
    public ModelRenderer body3;
    public ModelRenderer body1;
    public ModelRenderer nose;

    public ModelSeaLionTFC()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.body3 = new ModelRenderer(this, 34, 19);
        this.body3.setRotationPoint(2.0F, 1.5F, 18.0F);
        this.body3.addBox(0.0F, 0.0F, 0.0F, 6, 4, 4, 0.0F);
        this.setRotateAngle(body3, 0.17453292519943295F, 0.0F, 0.0F);
        this.mainBody = new ModelRenderer(this, 24, 47);
        this.mainBody.setRotationPoint(-4.0F, 15.4F, -7.9F);
        this.mainBody.addBox(0.0F, 0.0F, 0.0F, 10, 7, 10, 0.0F);
        this.setRotateAngle(mainBody, -0.17453292519943295F, 0.0F, 0.0F);
        this.backLFin2 = new ModelRenderer(this, 5, 30);
        this.backLFin2.mirror = true;
        this.backLFin2.setRotationPoint(4.0F, 1.0F, 0.0F);
        this.backLFin2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.neck = new ModelRenderer(this, 2, 19);
        this.neck.setRotationPoint(1.0F, -0.5F, -5.2F);
        this.neck.addBox(-3.5F, -2.5F, -2.0F, 7, 6, 4, 0.0F);
        this.setRotateAngle(neck, -0.08726646259971647F, 0.0F, 0.0F);
        this.nose = new ModelRenderer(this, 5, 0);
        this.nose.setRotationPoint(0.0F, 1.5F, -7.0F);
        this.nose.addBox(-2.0F, -2.0F, -2.0F, 4, 3, 4, 0.0F);
        this.backLFin1 = new ModelRenderer(this, 2, 33);
        this.backLFin1.mirror = true;
        this.backLFin1.setRotationPoint(3.0F, 22.0F, 11.0F);
        this.backLFin1.addBox(0.0F, 0.0F, 0.0F, 4, 2, 3, 0.0F);
        this.setRotateAngle(backLFin1, 0.0F, -0.35028758087526196F, 0.0F);
        this.backRFin2 = new ModelRenderer(this, 5, 30);
        this.backRFin2.setRotationPoint(-2.0F, 1.0F, 0.0F);
        this.backRFin2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.frontRFin2 = new ModelRenderer(this, 1, 40);
        this.frontRFin2.setRotationPoint(-2.4F, 0.0F, -1.0F);
        this.frontRFin2.addBox(-2.0F, 0.0F, -1.5F, 4, 1, 4, 0.0F);
        this.setRotateAngle(frontRFin2, 0.0F, 0.0F, -0.03490658503988659F);
        this.frontRFin1 = new ModelRenderer(this, 0, 45);
        this.frontRFin1.setRotationPoint(-4.5F, 5.5F, 0.6F);
        this.frontRFin1.addBox(-0.5F, -1.0F, -2.5F, 4, 2, 5, 0.0F);
        this.setRotateAngle(frontRFin1, 0.0F, 0.0F, -0.15707963267948966F);
        this.frontLFin1 = new ModelRenderer(this, 0, 45);
        this.frontLFin1.mirror = true;
        this.frontLFin1.setRotationPoint(0.8F, 5.0F, 0.6F);
        this.frontLFin1.addBox(-0.5F, -1.0F, -2.5F, 4, 2, 5, 0.0F);
        this.setRotateAngle(frontLFin1, 0.0F, 0.0F, 0.15707963267948966F);
        this.frontRArm = new ModelRenderer(this, 2, 52);
        this.frontRArm.setRotationPoint(-3.5F, 18.0F, -6.0F);
        this.frontRArm.addBox(-1.5F, -1.0F, -2.0F, 2, 7, 5, 0.0F);
        this.setRotateAngle(frontRArm, 0.0F, 0.0F, 0.13962634015954636F);
        this.frontLFin2 = new ModelRenderer(this, 1, 40);
        this.frontLFin2.mirror = true;
        this.frontLFin2.setRotationPoint(1.4F, -0.1F, -1.0F);
        this.frontLFin2.addBox(2.0F, 0.0F, -1.5F, 4, 1, 4, 0.0F);
        this.setRotateAngle(frontLFin2, 0.0F, 0.0F, 0.03490658503988659F);
        this.head = new ModelRenderer(this, 0, 7);
        this.head.setRotationPoint(1.0F, 9.9F, -7.0F);
        this.head.addBox(-3.0F, -2.5F, -6.0F, 6, 5, 7, 0.0F);
        this.body2 = new ModelRenderer(this, 32, 27);
        this.body2.setRotationPoint(1.0F, 1.2F, 14.0F);
        this.body2.addBox(0.0F, 0.0F, 0.0F, 8, 5, 4, 0.0F);
        this.setRotateAngle(body2, 0.17453292519943295F, 0.0F, 0.0F);
        this.neckbase = new ModelRenderer(this, 28, 4);
        this.neckbase.setRotationPoint(0.0F, 18.5F, -6.5F);
        this.neckbase.addBox(-3.5F, -3.5F, -4.5F, 9, 7, 7, 0.0F);
        this.setRotateAngle(neckbase, -1.2217304763960306F, 0.0F, 0.0F);
        this.backRFin1 = new ModelRenderer(this, 2, 33);
        this.backRFin1.setRotationPoint(-5.0F, 22.0F, 12.0F);
        this.backRFin1.addBox(0.0F, 0.0F, 0.0F, 4, 2, 3, 0.0F);
        this.setRotateAngle(backRFin1, -0.0F, 0.3490658503988659F, 0.0F);
        this.body1 = new ModelRenderer(this, 30, 36);
        this.body1.setRotationPoint(0.5F, 0.7F, 9.5F);
        this.body1.addBox(0.0F, 0.0F, 0.0F, 9, 6, 5, 0.0F);
        this.setRotateAngle(body1, 0.08726646259971647F, 0.0F, 0.0F);
        this.frontLArm = new ModelRenderer(this, 2, 52);
        this.frontLArm.mirror = true;
        this.frontLArm.setRotationPoint(6.7F, 18.0F, -6.0F);
        this.frontLArm.addBox(-1.5F, -1.0F, -2.0F, 2, 7, 5, 0.0F);
        this.setRotateAngle(frontLArm, -0.0F, 0.0F, -0.13962634015954636F);

        this.mainBody.addChild(this.body3);
        this.backLFin1.addChild(this.backLFin2);
        this.neckbase.addChild(this.neck);
        this.head.addChild(this.nose);
        this.backRFin1.addChild(this.backRFin2);
        this.frontRFin1.addChild(this.frontRFin2);
        this.frontRArm.addChild(this.frontRFin1);
        this.frontLArm.addChild(this.frontLFin1);
        this.frontLFin1.addChild(this.frontLFin2);
        this.mainBody.addChild(this.body2);
        this.mainBody.addChild(this.body1);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        EntitySeaLionTFC sealion = ((EntitySeaLionTFC) entity);

        float percent = (float) sealion.getPercentToAdulthood();
        float ageScale = 2.0F - percent;


        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head.render(par7);
        mainBody.render(par7);
        neckbase.render(par7);
        frontRArm.render(par7);
        frontLArm.render(par7);
        backRFin1.render(par7);
        backLFin1.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.frontRArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.frontLArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.backRFin1.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.backLFin1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}