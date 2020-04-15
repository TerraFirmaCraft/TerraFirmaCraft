/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

//Made with Blockbench
//Paste this code into your mod.

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityLionTFC;

public class ModelLionTFC extends ModelBase
{
    private ModelRenderer body;
    private ModelRenderer tail;
    private ModelRenderer tail1;
    private ModelRenderer head;
    private ModelRenderer nose;
    private ModelRenderer jaw;
    private ModelRenderer earML;
    private ModelRenderer earMR;
    private ModelRenderer earFL;
    private ModelRenderer earFR;
    private ModelRenderer legFR;
    private ModelRenderer legFL;
    private ModelRenderer legBL;
    private ModelRenderer legBR;

    public ModelLionTFC()
    {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 21.0F, 0.0F);
        body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, -14.9F, -9.0F, 8, 11, 13, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 36, 24, -3.0F, -14.9F, 4.0F, 6, 9, 9, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 42, 0, -7.0F, -17.9F, -10.0F, 14, 9, 7, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 32, 42, -5.0F, -16.9F, -3.0F, 10, 6, 3, 0.0F, false));

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, -14.0F, 12.0F);
        setRotationAngle(tail, -0.7854F, 0.0F, 0.0F);
        body.addChild(tail);
        tail.cubeList.add(new ModelBox(tail, 72, 76, -0.5F, -1.0F, 0.0F, 1, 1, 7, 0.0F, false));

        tail1 = new ModelRenderer(this);
        tail1.setRotationPoint(0.0F, -1.0F, 7.0F);
        setRotationAngle(tail1, 1.0472F, 0.0F, 0.0F);
        tail.addChild(tail1);
        tail1.cubeList.add(new ModelBox(tail1, 44, 84, -0.5F, -0.366F, -0.366F, 1, 1, 5, 0.0F, false));
        tail1.cubeList.add(new ModelBox(tail1, 0, 91, 0.0F, -0.866F, 3.634F, 0, 2, 2, 0.0F, false));
        tail1.cubeList.add(new ModelBox(tail1, 64, 88, -1.0F, 0.134F, 3.634F, 2, 0, 2, 0.0F, false));

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -15.0F, -9.0F);
        body.addChild(head);
        head.cubeList.add(new ModelBox(head, 66, 24, -4.0F, -4.0F, -7.0F, 8, 8, 7, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 30, 84, -2.0F, -1.0F, -10.0F, 4, 3, 3, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 0, 24, -6.0F, -5.0F, -5.0F, 12, 12, 6, 0.0F, false));

        nose = new ModelRenderer(this);
        nose.setRotationPoint(0.0F, -1.0F, -5.5F);
        setRotationAngle(nose, 0.3491F, 0.0F, 0.0F);
        head.addChild(nose);
        nose.cubeList.add(new ModelBox(nose, 16, 84, -1.0F, -2.0F, -4.7F, 2, 2, 5, 0.0F, false));

        jaw = new ModelRenderer(this);
        jaw.setRotationPoint(0.0F, 2.0F, -6.0F);
        setRotationAngle(jaw, 0.0873F, 0.0F, 0.0F);
        head.addChild(jaw);
        jaw.cubeList.add(new ModelBox(jaw, 0, 84, -2.0F, 0.0001F, -4.0F, 4, 2, 4, 0.0F, false));

        earML = new ModelRenderer(this);
        earML.setRotationPoint(-2.0F, -3.0F, -1.0F);
        head.addChild(earML);
        earML.cubeList.add(new ModelBox(earML, 56, 84, -2.0F, -4.0F, -2.0F, 3, 3, 1, 0.0F, false));

        earMR = new ModelRenderer(this);
        earMR.setRotationPoint(2.0F, -3.0F, -1.0F);
        head.addChild(earMR);
        earMR.cubeList.add(new ModelBox(earMR, 64, 84, -1.0F, -4.0F, -2.0F, 3, 3, 1, 0.0F, false));

        earFL = new ModelRenderer(this);
        earFL.setRotationPoint(-2.0F, -3.0F, -1.0F);
        setRotationAngle(earFL, 0.0F, -0.1745F, -0.1745F);
        head.addChild(earFL);
        earFL.cubeList.add(new ModelBox(earFL, 56, 84, -2.0F, -3.0F, -2.0F, 3, 3, 1, 0.0F, false));

        earFR = new ModelRenderer(this);
        earFR.setRotationPoint(2.0F, -3.0F, -1.0F);
        setRotationAngle(earFR, 0.0F, 0.1745F, 0.1745F);
        head.addChild(earFR);
        earFR.cubeList.add(new ModelBox(earFR, 64, 84, -1.0F, -3.0F, -2.0F, 3, 3, 1, 0.0F, false));

        legFR = new ModelRenderer(this);
        legFR.setRotationPoint(-3.0F, -14.0F, -7.0F);
        body.addChild(legFR);
        legFR.cubeList.add(new ModelBox(legFR, 0, 42, -2.0F, -1.0F, -1.0F, 4, 16, 4, 0.0F, false));
        legFR.cubeList.add(new ModelBox(legFR, 54, 76, -2.0F, 15.0F, -2.0F, 4, 2, 5, 0.0F, false));

        legFL = new ModelRenderer(this);
        legFL.setRotationPoint(3.0F, -14.0F, -7.0F);
        body.addChild(legFL);
        legFL.cubeList.add(new ModelBox(legFL, 16, 42, -2.0F, -1.0F, -1.0F, 4, 16, 4, 0.0F, false));
        legFL.cubeList.add(new ModelBox(legFL, 18, 76, -2.0F, 15.0F, -2.0F, 4, 2, 5, 0.0F, false));

        legBL = new ModelRenderer(this);
        legBL.setRotationPoint(2.0F, -12.0F, 6.0F);
        body.addChild(legBL);
        legBL.cubeList.add(new ModelBox(legBL, 40, 62, -1.0F, 6.0F, 1.0F, 4, 7, 4, 0.0F, false));
        legBL.cubeList.add(new ModelBox(legBL, 0, 62, -1.0F, -2.0F, -1.0F, 4, 8, 6, 0.0F, false));
        legBL.cubeList.add(new ModelBox(legBL, 0, 76, -1.0F, 13.0F, 0.0F, 4, 2, 5, 0.0F, false));

        legBR = new ModelRenderer(this);
        legBR.setRotationPoint(2.0F, -12.0F, 6.0F);
        body.addChild(legBR);
        legBR.cubeList.add(new ModelBox(legBR, 56, 62, -7.0F, 6.0F, 1.0F, 4, 7, 4, 0.0F, false));
        legBR.cubeList.add(new ModelBox(legBR, 20, 62, -7.0F, -2.0F, -1.0F, 4, 8, 6, 0.0F, false));
        legBR.cubeList.add(new ModelBox(legBR, 36, 76, -7.0F, 13.0F, 0.0F, 4, 2, 5, 0.0F, false));
    }


    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        EntityLionTFC lion = ((EntityLionTFC) entity);

        float percent = (float) lion.getPercentToAdulthood();
        float ageScale = 2.0F - percent;
        float ageHeadScale = (float) Math.pow(1 / ageScale, 0.66);

        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        float age = 1;

        if (lion.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            earFL.isHidden = true;
            earFR.isHidden = true;
            earML.isHidden = false;
            earMR.isHidden = false;
        }
        else
        {
            earFL.isHidden = false;
            earFR.isHidden = false;
            earML.isHidden = true;
            earMR.isHidden = true;
        }


        if (isChild)
        {
            float aa = 2F - (1.0F - age);
            GlStateManager.pushMatrix();
            float ab = (float) Math.sqrt(1.0F / aa);
            GlStateManager.scale(ab, ab, ab);
            GlStateManager.translate(0.0F, 24F * f5 * age / aa, 2F * f5 * age / ab);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F / aa, 1.0F / aa, 1.0F / aa);
            GlStateManager.translate(0.0F, 24F * f5 * age, 0.0F);
            body.render(f5);
            GlStateManager.popMatrix();
        }
        else
        {
            body.render(f5);
        }

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
        this.legBR.rotateAngleX = MathHelper.cos(par1 * 0.5662F) * 0.8F * par2;
        this.legBL.rotateAngleX = MathHelper.cos(par1 * 0.5662F + (float) Math.PI) * 0.8F * par2;
        this.legFR.rotateAngleX = MathHelper.cos(par1 * 0.5662F + (float) Math.PI) * 0.8F * par2;
        this.legFL.rotateAngleX = MathHelper.cos(par1 * 0.5662F) * 0.8F * par2;
        this.jaw.rotateAngleX = 0.0873F + mouthAngle;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}