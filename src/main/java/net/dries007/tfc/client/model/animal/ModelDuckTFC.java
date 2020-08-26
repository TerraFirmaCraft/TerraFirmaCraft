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

import net.dries007.tfc.objects.entity.animal.EntityDuckTFC;

/**
 * ModelDuckTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelDuckTFC extends ModelBase
{
    public ModelRenderer bill;
    public ModelRenderer head;
    public ModelRenderer neck;
    public ModelRenderer body;
    public ModelRenderer bodyFront1;
    public ModelRenderer bodyFront2;
    public ModelRenderer bodyBack2;
    public ModelRenderer bodyBack1;
    public ModelRenderer bodyBack3;
    public ModelRenderer bodyBack4;
    public ModelRenderer rightWing1;
    public ModelRenderer rightWing2;
    public ModelRenderer rightWing3;
    public ModelRenderer leftWing1;
    public ModelRenderer leftWing2;
    public ModelRenderer leftWing3;
    public ModelRenderer legR1;
    public ModelRenderer legR2;
    public ModelRenderer legL1;
    public ModelRenderer legL2;
    public ModelRenderer feetR;
    public ModelRenderer feetL;

    public ModelDuckTFC()
    {
        textureWidth = 64;
        textureHeight = 32;

        bill = new ModelRenderer(this, 17, 21);
        bill.addBox(-1.5F, -1.0F, -1.5F, 3, 2, 3, 0F);
        bill.setRotationPoint(0F, -5.1F, -6F);

        head = new ModelRenderer(this, 17, 26);
        head.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0F);
        head.setRotationPoint(0F, -5.6F, -3F);

        neck = new ModelRenderer(this, 48, 0);
        neck.addBox(-2.0F, -8F, -2F, 4, 10, 4, 0F);
        neck.setRotationPoint(0.5F, 12.5F, -2.2F);

        body = new ModelRenderer(this, 30, 14);
        body.addBox(-3.0F, -3.0F, -4.0F, 7, 8, 10, 0F);
        body.setRotationPoint(0F, 15.5F, 0F);
        setRotateAngle(body, -0.091106F, 0F, 0F);

        bodyFront1 = new ModelRenderer(this, 28, 17);
        bodyFront1.addBox(-2.5F, -2.5F, -0.5F, 5, 6, 1, 0F);
        bodyFront1.setRotationPoint(0.5F, 15.4F, -4.5F);
        setRotateAngle(bodyFront1, -0.091106F, 0F, 0F);

        bodyFront2 = new ModelRenderer(this, 54, 19);
        bodyFront2.addBox(-2.0F, -2.0F, 0F, 4, 4, 1, 0F);
        bodyFront2.setRotationPoint(0.5F, 15.3F, -5.7F);
        setRotateAngle(bodyFront2, -0.091106F, 0F, 0F);

        bodyBack1 = new ModelRenderer(this, 0, 24);
        bodyBack1.addBox(-2.5F, -2.5F, -1.0F, 6, 6, 2, 0F);
        bodyBack1.setRotationPoint(0F, 15.27F, 6.0F);
        setRotateAngle(bodyBack1, 0.136659F, 0F, 0F);

        bodyBack2 = new ModelRenderer(this, 0, 17);
        bodyBack2.addBox(-2.0F, -2.0F, -1.0F, 5, 5, 2, 0F);
        bodyBack2.setRotationPoint(0F, 14.6F, 7.0F);
        setRotateAngle(bodyBack2, 0.182037F, 0F, 0F);

        bodyBack3 = new ModelRenderer(this, 0, 12);
        bodyBack3.addBox(-1.5F, -1.5F, -1.0F, 3, 3, 2, 0F);
        bodyBack3.setRotationPoint(0.5F, 13.9F, 8.0F);
        setRotateAngle(bodyBack3, 0.227590F, 0F, 0F);

        bodyBack4 = new ModelRenderer(this, 0, 8);
        bodyBack4.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0F);
        bodyBack4.setRotationPoint(0.5F, 13.1F, 8.9F);
        setRotateAngle(bodyBack4, 0.318697F, 0F, 0F);

        rightWing1 = new ModelRenderer(this, 17, 1);
        rightWing1.addBox(-1F, 0F, -3.0F, 1, 2, 9, 0F);
        rightWing1.setRotationPoint(-3F, 13F, 0F);
        setRotateAngle(rightWing1, -0.045553F, 0F, 0F);

        rightWing2 = new ModelRenderer(this, 8, 0);
        rightWing2.addBox(-1F, 0F, -3.0F, 1, 2, 8, 0F);
        rightWing2.setRotationPoint(0F, 2.0F, 0F);

        rightWing3 = new ModelRenderer(this, 0, 0);
        rightWing3.addBox(-1F, 0F, -3.0F, 1, 1, 7, 0F);
        rightWing3.setRotationPoint(0F, 4.0F, 0F);

        leftWing1 = new ModelRenderer(this, 17, 1);
        leftWing1.addBox(0F, 0F, -3.0F, 1, 2, 9, 0F);
        leftWing1.setRotationPoint(4.0F, 13F, 0F);
        setRotateAngle(leftWing1, -0.045553F, 0F, 0F);
        leftWing1.mirror = true;

        leftWing2 = new ModelRenderer(this, 8, 0);
        leftWing2.addBox(0F, 0F, -3.0F, 1, 2, 8, 0F);
        leftWing2.setRotationPoint(0F, 2.0F, 0F);
        leftWing2.mirror = true;

        leftWing3 = new ModelRenderer(this, 0, 0);
        leftWing3.addBox(0F, 0F, -3.0F, 1, 1, 7, 0F);
        leftWing3.setRotationPoint(0F, 4.0F, 0F);
        leftWing3.mirror = true;

        legR1 = new ModelRenderer(this, 39, 8);
        legR1.setRotationPoint(-1.5F, 20.5F, 0.0F);
        legR1.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 1, 0.4F);
        setRotateAngle(legR1, 0.08726646259971647F, 0.0F, 0.0F);

        legR2 = new ModelRenderer(this, 39, 4);
        legR2.setRotationPoint(0.0F, 0.2F, 0.0F);
        legR2.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotateAngle(legR2, -0.17453292519943295F, 0.0F, 0.0F);

        feetR = new ModelRenderer(this, 36, 0);
        feetR.setRotationPoint(1.0F, 2.6F, 1.8F);
        feetR.addBox(-2.5F, 0.0F, -3.0F, 3, 1, 3, 0.0F);
        setRotateAngle(feetR, 0.04363323129985824F, 0.0F, 0.0F);

        legL1 = new ModelRenderer(this, 39, 8);
        legL1.mirror = true;
        legL1.setRotationPoint(1.5F, 20.5F, 0.0F);
        legL1.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 1, 0.4F);
        setRotateAngle(legL1, 0.08726646259971647F, 0.0F, 0.0F);

        legL2 = new ModelRenderer(this, 39, 4);
        legL2.mirror = true;
        legL2.setRotationPoint(0.0F, 0.2F, 0.0F);
        legL2.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotateAngle(legL2, -0.17453292519943295F, 0.0F, 0.0F);

        feetL = new ModelRenderer(this, 36, 0);
        feetL.mirror = true;
        feetL.setRotationPoint(1.0F, 2.6F, 1.8F);
        feetL.addBox(-2.5F, 0.0F, -3.0F, 3, 1, 3, 0.0F);
        setRotateAngle(feetL, 0.04363323129985824F, 0.0F, 0.0F);

        neck.addChild(bill);
        neck.addChild(head);
        rightWing1.addChild(rightWing2);
        rightWing1.addChild(rightWing3);
        leftWing1.addChild(leftWing2);
        leftWing1.addChild(leftWing3);
        legR1.addChild(legR2);
        legR2.addChild(feetR);
        legL1.addChild(legL2);
        legL2.addChild(feetL);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

        EntityDuckTFC duck = ((EntityDuckTFC) entity);

        float percent = (float) duck.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        neck.render(par7);
        body.render(par7);
        bodyFront1.render(par7);
        bodyFront2.render(par7);
        bodyBack1.render(par7);
        bodyBack2.render(par7);
        bodyBack3.render(par7);
        bodyBack4.render(par7);
        legR1.render(par7);
        legL1.render(par7);
        rightWing1.render(par7);
        leftWing1.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.neck.rotateAngleX = -(par5 / (180F / (float) Math.PI));
        this.neck.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.legR1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legL1.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.rightWing1.rotateAngleZ = par3;
        this.leftWing1.rotateAngleZ = -par3;
        this.rightWing1.rotateAngleX = 0;
        this.leftWing1.rotateAngleX = 0;
        this.rightWing1.setRotationPoint(-3.0F, 13, 0.0F);
        this.leftWing1.setRotationPoint(4.0F, 13, 0.0F);


        //setRotateAngle(bodyBack2, 0.182037F, 0.0F, 0.0F);
        //setRotateAngle(bodyBack3, 0.227590F, 0.0F, 0.0F);
        //setRotateAngle(bodyBack4, 0.318697F, 0.0F, 0.0F);

    }

    private void setRotateAngle(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}