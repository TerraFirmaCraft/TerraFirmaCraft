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

import net.dries007.tfc.objects.entity.animal.EntityMongooseTFC;

/**
 * ModelMongooseTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelMongooseTFC extends ModelBase
{
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer neck;
    public ModelRenderer legLBack;
    public ModelRenderer tail1;
    public ModelRenderer legRBack;
    public ModelRenderer legRFront;
    public ModelRenderer tail2;
    public ModelRenderer legLFront;
    public ModelRenderer rump;
    public ModelRenderer nose;
    public ModelRenderer earR;
    public ModelRenderer earL;
    public ModelRenderer legLBackLower;
    public ModelRenderer legLBackPaw;
    public ModelRenderer legRBackLower;
    public ModelRenderer legRBackPaw;
    public ModelRenderer legRFrontLower;
    public ModelRenderer legRFrontPaw;
    public ModelRenderer legLFrontLower;
    public ModelRenderer legLFrontPaw;

    public ModelMongooseTFC()
    {
        textureWidth = 64;
        textureHeight = 32;

        legRFrontLower = new ModelRenderer(this, 15, 19);
        legRFrontLower.setRotationPoint(-0.99F, 3.5F, 0.5F);
        legRFrontLower.addBox(0.0F, 0.0F, -0.5F, 2, 4, 2, 0.0F);
        legLFront = new ModelRenderer(this, 14, 25);
        legLFront.mirror = true;
        legLFront.setRotationPoint(2.5F, 16.5F, -5.0F);
        legLFront.addBox(-1.0F, 0.0F, -0.5F, 2, 4, 3, 0.0F);
        legLBackPaw = new ModelRenderer(this, 27, 17);
        legLBackPaw.mirror = true;
        legLBackPaw.setRotationPoint(0.01F, 2.0F, -0.5F);
        legLBackPaw.addBox(0.0F, 0.0F, -1.5F, 2, 1, 1, 0.0F);
        tail2 = new ModelRenderer(this, 5, 23);
        tail2.setRotationPoint(0.0F, 20.6F, 13.2F);
        tail2.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, 0.0F);
        setRotateAngle(tail2, 1.7278759594743864F, 0.0F, 0.0F);
        earR = new ModelRenderer(this, 0, 0);
        earR.setRotationPoint(-1.2F, 2.0F, 0.0F);
        earR.addBox(-2.0F, -3.0F, 0.0F, 1, 2, 1, 0.0F);
        legRFront = new ModelRenderer(this, 14, 25);
        legRFront.setRotationPoint(-2.5F, 16.5F, -5.0F);
        legRFront.addBox(-1.0F, 0.0F, -0.5F, 2, 4, 3, 0.0F);
        legRBackPaw = new ModelRenderer(this, 27, 17);
        legRBackPaw.setRotationPoint(-0.01F, 2.0F, -0.5F);
        legRBackPaw.addBox(0.0F, 0.0F, -1.5F, 2, 1, 1, 0.0F);
        head = new ModelRenderer(this, 0, 7);
        head.setRotationPoint(0.0F, 11.3F, -7.5F);
        head.addBox(-2.5F, -2.0F, -3.0F, 5, 4, 5, 0.0F);
        tail1 = new ModelRenderer(this, 0, 23);
        tail1.setRotationPoint(0.0F, 15.4F, 7.0F);
        tail1.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, 0.2F);
        setRotateAngle(tail1, 0.9000662952534757F, 0.0F, 0.0F);
        legRBackLower = new ModelRenderer(this, 26, 19);
        legRBackLower.setRotationPoint(-0.99F, 5.0F, 1.5F);
        legRBackLower.addBox(0.0F, 0.0F, -1.5F, 2, 3, 2, 0.0F);
        legLFrontPaw = new ModelRenderer(this, 16, 17);
        legLFrontPaw.mirror = true;
        legLFrontPaw.setRotationPoint(0.01F, 3.0F, -0.5F);
        legLFrontPaw.addBox(0.0F, 0.0F, -0.5F, 2, 1, 1, 0.0F);
        body = new ModelRenderer(this, 36, 18);
        body.setRotationPoint(0.0F, 16.65F, -7.2F);
        body.addBox(-3.0F, -3.0F, 0.0F, 6, 6, 8, 0.0F);
        setRotateAngle(body, -0.04363323129985824F, 0.0F, 0.0F);
        legLFrontLower = new ModelRenderer(this, 15, 19);
        legLFrontLower.mirror = true;
        legLFrontLower.setRotationPoint(-1.01F, 3.5F, 0.5F);
        legLFrontLower.addBox(0.0F, 0.0F, -0.5F, 2, 4, 2, 0.0F);
        earL = new ModelRenderer(this, 0, 0);
        earL.mirror = true;
        earL.setRotationPoint(1.2F, 2.0F, 0.0F);
        earL.addBox(1.0F, -3.0F, 0.0F, 1, 2, 1, 0.0F);
        neck = new ModelRenderer(this, 21, 0);
        neck.setRotationPoint(0.0F, 15.0F, -6.5F);
        neck.addBox(-2.0F, -4.0F, -2.0F, 4, 8, 4, 0.0F);
        setRotateAngle(neck, 0.3490658503988659F, 0.0F, 0.0F);
        legLBack = new ModelRenderer(this, 25, 24);
        legLBack.mirror = true;
        legLBack.setRotationPoint(2.7F, 16.0F, 4.0F);
        legLBack.addBox(-1.0F, 0.0F, -0.5F, 2, 5, 3, 0.0F);
        legRBack = new ModelRenderer(this, 25, 24);
        legRBack.setRotationPoint(-2.7F, 16.0F, 4.0F);
        legRBack.addBox(-1.0F, 0.0F, -0.5F, 2, 5, 3, 0.0F);
        legRFrontPaw = new ModelRenderer(this, 16, 17);
        legRFrontPaw.setRotationPoint(-0.01F, 3.0F, -0.5F);
        legRFrontPaw.addBox(0.0F, 0.0F, -0.5F, 2, 1, 1, 0.0F);
        nose = new ModelRenderer(this, 5, 3);
        nose.setRotationPoint(0.0F, -0.3F, -1.0F);
        nose.addBox(-1.5F, 0.0F, -4.0F, 3, 2, 2, 0.0F);
        legLBackLower = new ModelRenderer(this, 26, 19);
        legLBackLower.mirror = true;
        legLBackLower.setRotationPoint(-1.01F, 5.0F, 1.5F);
        legLBackLower.addBox(0.0F, 0.0F, -1.5F, 2, 3, 2, 0.0F);
        rump = new ModelRenderer(this, 37, 5);
        rump.setRotationPoint(0.0F, 17.0F, 4.2F);
        rump.addBox(-3.0F, -3.0F, -3.5F, 6, 6, 7, 0.1F);

        legRFront.addChild(legRFrontLower);
        legLBackLower.addChild(legLBackPaw);
        head.addChild(earR);
        legRBackLower.addChild(legRBackPaw);
        legRBack.addChild(legRBackLower);
        legLFrontLower.addChild(legLFrontPaw);
        legLFront.addChild(legLFrontLower);
        head.addChild(earL);
        legRFrontLower.addChild(legRFrontPaw);
        head.addChild(nose);
        legLBack.addChild(legLBackLower);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        EntityMongooseTFC mongoose = ((EntityMongooseTFC) entity);

        float percent = (float) mongoose.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        legLFront.render(par7);
        tail2.render(par7);
        legRFront.render(par7);
        head.render(par7);
        tail1.render(par7);
        body.render(par7);
        neck.render(par7);
        legLBack.render(par7);
        legRBack.render(par7);
        rump.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.legRFront.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legLFront.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legRBack.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legLBack.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
    }

    private void setRotateAngle(ModelRenderer renderer, float x, float y, float z)
    {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }
}