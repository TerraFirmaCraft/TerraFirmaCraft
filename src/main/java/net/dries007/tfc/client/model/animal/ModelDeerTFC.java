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
import net.dries007.tfc.objects.entity.animal.EntityDeerTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelDeerTFC extends ModelBase
{
    //fields
    private final ModelRenderer antler24;
    private final ModelRenderer antler23;
    private final ModelRenderer antler22;
    private final ModelRenderer antler21;
    private final ModelRenderer antler14;
    private final ModelRenderer antler13;
    private final ModelRenderer antler12;
    private final ModelRenderer antler11;
    private final ModelRenderer hoof2;
    private final ModelRenderer toes3;
    private final ModelRenderer thigh1;
    private final ModelRenderer ear2;
    private final ModelRenderer ear1;
    private final ModelRenderer calf2;
    private final ModelRenderer tail;
    private final ModelRenderer collar;
    private final ModelRenderer upperLeg4;
    private final ModelRenderer neck;
    private final ModelRenderer rump;
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;
    private final ModelRenderer snout;
    private final ModelRenderer torso;
    private final ModelRenderer upperLeg3;
    private final ModelRenderer calf1;
    private final ModelRenderer lowerleg3;
    private final ModelRenderer lowerleg4;
    private final ModelRenderer thigh2;
    private final ModelRenderer toes4;
    private final ModelRenderer toes2;
    private final ModelRenderer toes1;
    private final ModelRenderer hoof1;
    private final ModelRenderer hoof3;
    private final ModelRenderer hoof4;
    private boolean running;

    public ModelDeerTFC()
    {
        textureWidth = 128;
        textureHeight = 64;

        antler24 = new ModelRenderer(this, 44, 0);
        antler24.addBox(-6.8F, -15.4F, -1.8F, 1, 2, 1);
        antler24.setRotationPoint(0F, 0F, 0F);
        antler24.setTextureSize(128, 64);
        antler24.mirror = true;
        setRotation(antler24, 0F, 0F, 0.2792527F);
        antler23 = new ModelRenderer(this, 44, 0);
        antler23.addBox(-2.8F, -11F, 8F, 1, 2, 1);
        //antler23.addBox(0F,0F,0F,1,2,1);
        antler23.setRotationPoint(0F, 0F, 0F);
        antler23.setTextureSize(128, 64);
        antler23.mirror = true;
        setRotation(antler23, 0.8726646F, -0.4363323F, 0F);
        antler22 = new ModelRenderer(this, 44, 0);
        antler22.addBox(2.3F, -14.3F, -5.1F, 1, 3, 1);
        antler22.setRotationPoint(0F, 0F, 0F);
        antler22.setTextureSize(128, 64);
        antler22.mirror = true;
        setRotation(antler22, -0.2268928F, 0F, -0.3490659F);
        antler21 = new ModelRenderer(this, 44, 0);
        antler21.addBox(-2F, -13F, -2.5F, 1, 3, 1);
        antler21.setRotationPoint(0F, -1F, -8F);
        antler21.setTextureSize(128, 64);
        antler21.mirror = true;
        setRotation(antler21, 0F, 0F, 0F);
        antler14 = new ModelRenderer(this, 44, 0);
        antler14.addBox(5.8F, -15.4F, -1.8F, 1, 2, 1);
        antler14.setRotationPoint(0F, 0F, 0F);
        antler14.setTextureSize(128, 64);
        antler14.mirror = true;
        setRotation(antler14, 0F, 0F, -0.2792527F);
        antler13 = new ModelRenderer(this, 44, 0);
        antler13.addBox(1.8F, -11F, 8F, 1, 2, 1);
        antler13.setRotationPoint(0F, 0F, 0F);
        antler13.setTextureSize(128, 64);
        antler13.mirror = true;
        setRotation(antler13, 0.8726646F, 0.4363323F, 0F);
        antler12 = new ModelRenderer(this, 44, 0);
        antler12.addBox(-3.3F, -14.3F, -5.1F, 1, 3, 1);
        antler12.setRotationPoint(0F, 0F, 0F);
        antler12.setTextureSize(128, 64);
        antler12.mirror = true;
        setRotation(antler12, -0.2268928F, 0F, 0.3490659F);
        antler11 = new ModelRenderer(this, 44, 0);
        antler11.addBox(1F, -13F, -2.5F, 1, 3, 1);
        antler11.setRotationPoint(0F, -1F, -8F);
        antler11.setTextureSize(128, 64);
        antler11.mirror = true;
        setRotation(antler11, 0F, 0F, 0F);
        toes3 = new ModelRenderer(this, 18, 22);
        toes3.addBox(-0.5F, 0.4F, -3F, 2, 1, 4);
        toes3.setRotationPoint(0F, 5F, 0F);
        toes3.setTextureSize(128, 64);
        setRotation(toes3, 1.134464F, 0F, 0F);
        thigh1 = new ModelRenderer(this, 40, 22);
        thigh1.addBox(-1F, -2.3F, -2F, 2, 9, 5);
        thigh1.setRotationPoint(-3F, 2F, 7F);
        thigh1.setTextureSize(128, 64);
        setRotation(thigh1, -0.1745329F, 0F, 0.1745329F);
        ear2 = new ModelRenderer(this, 54, 16);
        ear2.mirror = true;
        ear2.addBox(-9F, -10F, -2F, 5, 2, 1);
        ear2.setRotationPoint(0F, 0F, 0F);
        ear2.setTextureSize(128, 64);
        setRotation(ear2, 0F, 0.3490659F, 0.34906585F);
        ear1 = new ModelRenderer(this, 54, 16);
        ear1.addBox(4F, -10F, -2F, 5, 2, 1);
        ear1.setRotationPoint(0F, 0F, 0F);
        ear1.setTextureSize(128, 64);
        setRotation(ear1, 0F, -0.3490659F, -0.34906585F);
        calf2 = new ModelRenderer(this, 54, 7);
        calf2.mirror = true;
        calf2.addBox(-1F, -1F, 0F, 2, 6, 3);
        calf2.setRotationPoint(0F, 6F, 0F);
        calf2.setTextureSize(128, 64);
        setRotation(calf2, 0.5585054F, 0F, 0F);
        tail = new ModelRenderer(this, 24, 52);
        tail.addBox(-1.5F, -0.5F, 0F, 3, 2, 9);
        tail.setRotationPoint(0F, -1.5F, 10F);
        tail.setTextureSize(128, 64);
        tail.mirror = true;
        setRotation(tail, -1.308997F, 0F, 0F);
        collar = new ModelRenderer(this, 30, 38);
        collar.addBox(0F, -2F, -4F, 5, 6, 7);
        collar.setRotationPoint(-2.5F, -1F, -8F);
        collar.setTextureSize(128, 64);
        collar.mirror = true;
        setRotation(collar, 1.151917F, 0F, 0F);
        upperLeg4 = new ModelRenderer(this, 30, 22);
        upperLeg4.mirror = true;
        upperLeg4.addBox(-1.25F, -1F, -1.5F, 2, 5, 3);
        upperLeg4.setRotationPoint(4F, 5F, -7F);
        upperLeg4.setTextureSize(128, 64);

        setRotation(upperLeg4, 0.3490659F, 0F, 0.0349066F);
        neck = new ModelRenderer(this, 57, 22);
        neck.addBox(-2F, -4F, -2F, 4, 5, 8);
        neck.setRotationPoint(0F, -1F, -8F);
        neck.setTextureSize(128, 64);
        neck.mirror = true;
        setRotation(neck, 1.815142F, 0F, 0F);
        rump = new ModelRenderer(this, 0, 47);
        rump.addBox(-3F, -4F, 3F, 6, 10, 6);
        rump.setRotationPoint(0F, 1.5F, 1F);
        rump.setTextureSize(128, 64);
        rump.mirror = true;
        setRotation(rump, -0.0872665F, 0F, 0F);
        head = new ModelRenderer(this, 54, 35);
        head.addBox(-2.5F, -11F, -5F, 5, 6, 6);
        head.setRotationPoint(0F, -1F, -8F);
        head.setTextureSize(128, 64);
        head.mirror = true;
        setRotation(head, 0.1570796F, 0F, 0F);
        head.addChild(ear1);
        head.addChild(ear2);
        body = new ModelRenderer(this, 18, 4);
        body.addBox(-4F, -11F, -8F, 8, 8, 10);
        body.setRotationPoint(0F, 1F, 2F);
        body.setTextureSize(128, 64);
        body.mirror = true;
        setRotation(body, 1.43117F, 0F, 0F);
        leg1 = new ModelRenderer(this, 0, 16);
        leg1.addBox(-0F, -1F, 0F, 2, 9, 2);
        leg1.setRotationPoint(0F, 5F, 0F);
        leg1.setTextureSize(128, 64);
        setRotation(leg1, 0F, 0F, 0F);
        leg2 = new ModelRenderer(this, 0, 16);
        leg2.mirror = true;
        leg2.addBox(-1F, -1F, 0F, 2, 9, 2);
        leg2.setRotationPoint(0F, 5F, 0F);
        leg2.setTextureSize(128, 64);

        setRotation(leg2, 0F, 0F, 0F);
        leg3 = new ModelRenderer(this, 8, 16);
        leg3.addBox(-0.5F, 0F, 0F,/*-0.5F, 3F, 0F,*/ 2, 7, 2);
        leg3.setRotationPoint(0F, 3F, -1F);
        leg3.setTextureSize(128, 64);
        setRotation(leg3, 0F, 0F, 0F);
        leg4 = new ModelRenderer(this, 8, 16);
        leg4.mirror = true;
        leg4.addBox(-1.5F, 0F, 0F, 2, 7, 2);
        leg4.setRotationPoint(0F, 3F, -1F);
        leg4.setTextureSize(128, 64);

        setRotation(leg4, -0.3490659F, 0F, -0.0349066F);
        snout = new ModelRenderer(this, 54, 0);
        snout.addBox(-1.5F, -9.3F, -9F, 3, 3, 4);
        snout.setRotationPoint(0F, 0F, 0F);
        snout.setTextureSize(128, 64);
        snout.mirror = true;
        head.addChild(snout);
        torso = new ModelRenderer(this, 0, 29);
        torso.addBox(-3.5F, -3F, -5F, 7, 10, 8);
        torso.setRotationPoint(0F, 1F, 2F);
        torso.setTextureSize(128, 64);
        torso.mirror = true;
        setRotation(torso, 0.122173F, 0F, 0F);
        upperLeg3 = new ModelRenderer(this, 30, 22);
        upperLeg3.addBox(-0.7F, -1F, -1.5F, 2, 5, 3);
        upperLeg3.setRotationPoint(-4F, 5F, -7F);
        upperLeg3.setTextureSize(128, 64);
        setRotation(upperLeg3, 0.3490659F, 0F, -0.0349066F);
        calf1 = new ModelRenderer(this, 54, 7);
        calf1.addBox(0F, -1F, 0F, 2, 6, 3);
        calf1.setRotationPoint(0F, 7F, 0F);
        calf1.setTextureSize(128, 64);
        setRotation(calf1, 0.5585054F, 0F, 0F);
        lowerleg3 = new ModelRenderer(this, 30, 30);
        lowerleg3.addBox(-0.5F, 0F, 0F, 2, 6, 2);
        lowerleg3.setRotationPoint(0F, 7F, 0F);
        lowerleg3.setTextureSize(128, 64);
        setRotation(lowerleg3, 0F, 0F, 0F);
        lowerleg4 = new ModelRenderer(this, 30, 30);
        lowerleg4.mirror = true;
        lowerleg4.addBox(-1.5F, 0F, 0F, 2, 6, 2);
        lowerleg4.setRotationPoint(0F, 7F, 0F);
        lowerleg4.setTextureSize(128, 64);

        setRotation(lowerleg4, 0F, 0F, 0F);
        thigh2 = new ModelRenderer(this, 40, 22);
        thigh2.mirror = true;
        thigh2.addBox(-1F, -2.3F, -2F, 2, 9, 5);
        thigh2.setRotationPoint(3F, 2F, 7F);
        thigh2.setTextureSize(128, 64);

        setRotation(thigh2, -0.1745329F, 0F, -0.1745329F);
        toes4 = new ModelRenderer(this, 18, 22);
        toes4.mirror = true;
        toes4.addBox(-1.5F, 0.4F, -3F, 2, 1, 4);
        toes4.setRotationPoint(0F, 5F, 0F);
        toes4.setTextureSize(128, 64);

        setRotation(toes4, 1.134464F, 0F, 0F);
        toes2 = new ModelRenderer(this, 18, 22);
        toes2.mirror = true;
        toes2.addBox(-1.0F, 0.4F, -3F, 2, 1, 4);
        toes2.setRotationPoint(0F, 8F, 0F);
        toes2.setTextureSize(128, 64);

        setRotation(toes2, 1.134464F, 0F, 0F);
        toes1 = new ModelRenderer(this, 18, 22);
        toes1.addBox(-0F, 0.4F, -3F, 2, 1, 4);
        toes1.setRotationPoint(0F, 8F, 0F);
        toes1.setTextureSize(128, 64);
        setRotation(toes1, 1.134464F, 0F, 0F);
        hoof1 = new ModelRenderer(this, 30, 0);
        hoof1.addBox(-0F, 0F, -6.3F, 2, 1, 2);
        hoof1.setRotationPoint(0F, 5F, 0.5F);
        hoof1.setTextureSize(128, 64);
        setRotation(hoof1, 0F, 0F, 0F);
        hoof2 = new ModelRenderer(this, 30, 0);
        hoof2.mirror = true;
        hoof2.addBox(-0.5F, 0F, -6.3F, 2, 1, 2);
        hoof2.setRotationPoint(0F, 5F, -0.5F);
        hoof2.setTextureSize(128, 64);

        setRotation(hoof2, 0F, 0F, 0F);
        hoof3 = new ModelRenderer(this, 30, 0);
        hoof3.addBox(-0.5F, 0F, -6.3F, 2, 1, 2);
        hoof3.setRotationPoint(0F, 5F, -0.5F);
        hoof3.setTextureSize(128, 64);
        setRotation(hoof3, 0F, 0F, 0F);
        hoof4 = new ModelRenderer(this, 30, 0);
        hoof4.mirror = true;
        hoof4.addBox(-1.5F, 0F, -6.3F, 2, 1, 2);
        hoof4.setRotationPoint(0F, 5F, -0.5F);
        hoof4.setTextureSize(128, 64);

        setRotation(hoof4, 0F, 0F, 0F);

        upperLeg4.addChild(leg4);
        leg4.addChild(lowerleg4);
        lowerleg4.addChild(toes4);
        toes4.addChild(hoof4);

        upperLeg3.addChild(leg3);
        leg3.addChild(lowerleg3);
        lowerleg3.addChild(toes3);
        toes3.addChild(hoof3);

        thigh1.addChild(calf1);
        calf1.addChild(leg1);
        leg1.addChild(toes1);
        toes1.addChild(hoof1);

        thigh2.addChild(calf2);
        calf2.addChild(leg2);
        leg2.addChild(toes2);
        toes2.addChild(hoof2);

        antler11.addChild(antler12);
        antler21.addChild(antler22);
        antler11.addChild(antler13);
        antler21.addChild(antler23);
        antler11.addChild(antler14);
        antler21.addChild(antler24);
    }

    @Override
    public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        EntityDeerTFC deer = ((EntityDeerTFC) entity);

        running = false;
        //running = ((EntityDeer)entity).getRunning();
        float age = (float) (1f - deer.getPercentToAdulthood());

        float aa = 2F - (1.0F - age);
        GlStateManager.translate(0.0F, -6F * f5 * age / (float) Math.pow(aa, 0.4), 0);
        GlStateManager.pushMatrix();
        float ab = (float) Math.sqrt(1.0F / aa);
        GlStateManager.scale(ab, ab, ab);
        GlStateManager.translate(0.0F, 22F * f5 * age / (float) Math.pow(aa, 0.4), 2F * f5 * age / ab);
        if (deer.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            if (aa <= 1.75)
            {
                antler11.isHidden = false;
                antler21.isHidden = false;
                if (aa <= 1.5)
                {
                    antler12.isHidden = false;
                    antler22.isHidden = false;
                    if (aa <= 1.3)
                    {
                        antler13.isHidden = false;
                        antler23.isHidden = false;
                        if (aa <= 1.1)
                        {
                            antler14.isHidden = false;
                            antler24.isHidden = false;
                        }
                    }
                }
            }
        }
        antler11.render(f5);
        antler21.render(f5);
        head.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F / aa, ab, 1.0F / aa);
        GlStateManager.translate(0.0F, 22F * f5 * age / (float) Math.pow(aa, 0.4), 0.0F);
        thigh1.render(f5);
        upperLeg4.render(f5);
        upperLeg3.render(f5);
        thigh2.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F / aa, 1.0F / aa, 1.0F / aa);
        GlStateManager.translate(0.0F, 22F * f5 * age, 0.0F);
        tail.render(f5);
        collar.render(f5);
        neck.render(f5);
        rump.render(f5);
        body.render(f5);
        torso.render(f5);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        f1 = Math.min(f1 * 7.5f, 0.75f);
        f *= 0.95f;

        antler11.isHidden = true;
        antler12.isHidden = true;
        antler13.isHidden = true;
        antler14.isHidden = true;
        antler21.isHidden = true;
        antler22.isHidden = true;
        antler23.isHidden = true;
        antler24.isHidden = true;
        setRotation(antler21, f4 / (180F / (float) Math.PI), f3 / (180F / (float) Math.PI), 0F);
        setRotation(head, f4 / (180F / (float) Math.PI) + 0.1570796F, f3 / (180F / (float) Math.PI), 0F);
        setRotation(antler11, f4 / (180F / (float) Math.PI), f3 / (180F / (float) Math.PI), 0F);
        setRotation(torso, 0.122173F, 0F, 0F);
        setRotation(collar, f4 / (3 * (180F / (float) Math.PI)) + 1.151917F, f3 / (3 * (180F / (float) Math.PI)), 0F);
        setRotation(neck, f4 / (1.5F * (180F / (float) Math.PI)) + 1.815142F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);
        setRotation(rump, -0.0872665F, 0F, 0F);
        setRotation(body, 1.43117F, 0F, 0F);
        setRotation(calf1, 0.5585054F, 0F, -0.1745329F);
        setRotation(calf2, 0.5585054F, 0F, 0.1745329F);
        setRotation(toes3, 1.134464F, 0, 0);
        setRotation(hoof1, -1.134464F, 0F, 0F);
        setRotation(hoof2, -1.134464F, 0F, 0F);
        setRotation(hoof3, -1.134464F, 0F, 0F);
        setRotation(hoof4, -1.134464F, 0F, 0F);

        setRotation(tail, -1.308997F, 0F, 0F);

        setRotation(leg1, -22F / 180F * (float) Math.PI, 0F, 0F);
        setRotation(leg2, -22F / 180F * (float) Math.PI, 0F, 0F);
        setRotation(leg3, -0.3490659F, 0F, 0.0349066F);
        setRotation(leg4, -0.3490659F, 0F, -0.0349066F);

        setRotation(upperLeg4, 0.3490659F, 0F, 0.0349066F);
        setRotation(upperLeg3, 0.3490659F, 0F, -0.0349066F);
        setRotation(thigh1, -0.1745329F, 0F, 0.1745329F);
        setRotation(thigh2, -0.1745329F, 0F, -0.1745329F);

        if (!running)
        {
            setRotation(upperLeg4, MathHelper.cos(f / 1.5F + 3F * (float) Math.PI / 2F) * 0.7F * f1 + 0.3490659F, 0F, 0.0349066F);
            setRotation(upperLeg3, MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) * 0.7F * f1 + 0.3490659F, 0F, -0.0349066F);
            setRotation(thigh1, MathHelper.cos(f / 1.5F + (float) Math.PI * 7F / 4F) * 0.7F * f1 - 0.1745329F, 0F, 0.1745329F);
            setRotation(thigh2, MathHelper.cos(f / 1.5F + 3f * (float) Math.PI / 4F) * 0.7F * f1 - 0.1745329F, 0F, -0.1745329F);
            if (MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 0.7F * f1 > 0)
            {
                setRotation(lowerleg3, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 1.4F * f1, 0F, 0F);
                setRotation(leg3, -MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 0.7F * f1 - 0.3490659F, 0F, 0.0349066F);
                setRotation(toes3, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 2.1F * f1 + 1.134464F, 0, 0);
            }
            if (MathHelper.sin(f / 1.5F + 1F * (float) Math.PI / 2F) * 0.7F * f1 < 0)
            {
                setRotation(lowerleg4, MathHelper.sin(f / 1.5F + 3F * (float) Math.PI / 2F) * 1.4F * f1, 0F, 0F);
                setRotation(leg4, -MathHelper.sin(f / 1.5F + 3F * (float) Math.PI / 2F) * 0.7F * f1 - 0.3490659F, 0F, -0.0349066F);
                setRotation(toes4, MathHelper.sin(f / 1.5F + 3F * (float) Math.PI / 2F) * 2.1F * f1 + 1.134464F, 0, 0);
            }
            if (MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 0.7F * f1 > 0)
            {
                setRotation(calf1, MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 1.4F * f1 + 0.5585054F, 0F, -0.1745329F);
                setRotation(leg1, -MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 1.4F * f1 - 22F / 180F * (float) Math.PI, 0F, 0F);
                setRotation(toes1, MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 2.1F * f1 + 1.134464F, 0F, 0F);
            }
            if (MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 0.7F * f1 > 0)
            {
                setRotation(calf2, MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 1.4F * f1 + 0.5585054F, 0F, 0.1745329F);
                setRotation(leg2, -MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 1.4F * f1 - 22F / 180F * (float) Math.PI, 0F, 0F);
                setRotation(toes2, MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 2.1F * f1 + 1.134464F, 0F, 0F);
            }

        }
        else
        {
            if (MathHelper.cos(f / 1.5F + 5 * (float) Math.PI / 4F) > -Math.sqrt(0.5) && MathHelper.cos(f / 1.5F + 5 * (float) Math.PI / 4F) < Math.sqrt(0.5))
            {
                setRotation(upperLeg4, MathHelper.cos(f / 1.5F + 5F * (float) Math.PI / 4F) * 2.8F * f1 + 0.3490659F, 0F, 0.0349066F);
            }
            if (MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) > 0)
            {
                setRotation(lowerleg4, MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) * 3.5F * f1, 0F, 0F);
                setRotation(leg4, -MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) * 3.5F * f1 - 0.3490659F, 0F, -0.0349066F);
                setRotation(toes4, MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) * 2.1F * f1 + 1.134464F, 0, 0);
            }


            if (MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) > -Math.sqrt(0.5) && MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) < Math.sqrt(0.5))
            {
                setRotation(upperLeg3, MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) * 2.8F * f1 + 0.3490659F, 0F, -0.0349066F);
            }
            if (MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) > 0)
            {
                setRotation(lowerleg3, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) * 3.5F * f1, 0F, 0F);
                setRotation(leg3, -MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) * 3.5F * f1 - 0.3490659F, 0F, 0.0349066F);
                setRotation(toes3, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) * 2.1F * f1 + 1.134464F, 0, 0);
            }

            setRotation(thigh1, MathHelper.cos(f / 1.5F + (float) Math.PI * 7F / 4F) * 2.8F * f1 - 0.1745329F, 0F, 0.1745329F);
            setRotation(thigh2, MathHelper.cos(f / 1.5F + 3f * (float) Math.PI / 4F) * 2.8F * f1 - 0.1745329F, 0F, -0.1745329F);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}