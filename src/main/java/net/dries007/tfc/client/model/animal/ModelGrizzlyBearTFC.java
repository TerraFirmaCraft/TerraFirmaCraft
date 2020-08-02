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
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityGrizzlyBearTFC;

/**
 * ModelGrizzlyBearTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelGrizzlyBearTFC extends ModelBase
{
    private final ModelRenderer bearHead;
    private final ModelRenderer rearBody;
    private final ModelRenderer frontBody;
    private final ModelRenderer ear2;
    private final ModelRenderer ear1;
    private final ModelRenderer nose;
    private final ModelRenderer tail;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;
    private final ModelRenderer paw1;
    private final ModelRenderer paw2;
    private final ModelRenderer paw3;
    private final ModelRenderer paw4;


    public ModelGrizzlyBearTFC()
    {
        textureWidth = 80;
        textureHeight = 64;

        ear1 = new ModelRenderer(this, 23, 11);
        ear1.mirror = true;
        ear1.setRotationPoint(-1.8F, -1.5F, -1.5F);
        ear1.addBox(3.0F, -3.5F, 0.5F, 3, 2, 1, 0.0F);
        ear2 = new ModelRenderer(this, 23, 11);
        ear2.setRotationPoint(-0.2F, -1.0F, -1.5F);
        ear2.addBox(-4.0F, -3.5F, 0.5F, 3, 2, 1, 0.0F);
        nose = new ModelRenderer(this, 5, 0);
        nose.setRotationPoint(0.0F, 1.4F, -1.0F);
        nose.addBox(-2.0F, -2.0F, -5.0F, 4, 4, 4, 0.0F);
        bearHead = new ModelRenderer(this, 0, 8);
        bearHead.setRotationPoint(0.0F, 8.0F, -14.8F);
        bearHead.addBox(-3.5F, -3.5F, -3.5F, 7, 7, 7, 0.0F);

        frontBody = new ModelRenderer(this, 29, 39);
        frontBody.setRotationPoint(-1.0F, 9F, 7.5F);
        frontBody.addBox(-5.0F, -19.0F, -6.5F, 13, 13, 12, 0.0F);
        setRotation(frontBody, 1.5707963267948966F, 0.0F, 0.0F);

        rearBody = new ModelRenderer(this, 32, 20);
        rearBody.setRotationPoint(0.5F, 10.5F, 14.5F);
        rearBody.addBox(-6.0F, -14.0F, -6.2F, 12, 9, 10, 0.0F);
        setRotation(rearBody, 1.4114477660878142F, 0.0F, 0.0F);
        tail = new ModelRenderer(this, 48, 3);
        tail.setRotationPoint(-1.0F, -3.8F, -0.5F);
        tail.addBox(-1.0F, -2.0F, -1.5F, 3, 3, 3, 0.0F);
        setRotation(tail, -0.36425021489121656F, 0.0F, 0.0F);

        leg1 = new ModelRenderer(this, 1, 30);
        leg1.setRotationPoint(5.5F, 0.0F, -8.0F);
        leg1.addBox(-2.0F, -2.0F, -1.0F, 5, 12, 5, 0.0F);
        leg2 = new ModelRenderer(this, 1, 30);
        leg2.setRotationPoint(-6.5F, 0.0F, -8.0F);
        leg2.addBox(-1.0F, -2.0F, -1.0F, 5, 12, 5, 0.0F);
        leg3 = new ModelRenderer(this, 0, 47);
        leg3.setRotationPoint(4.5F, 12.0F, 5.0F);
        leg3.addBox(-1.0F, 0.0F, -1.0F, 5, 12, 5, 0.0F);
        leg4 = new ModelRenderer(this, 0, 47);
        leg4.setRotationPoint(-6.5F, 12.0F, 5.0F);
        leg4.addBox(-1.0F, 0.0F, -1.0F, 5, 12, 5, 0.0F);
        paw1 = new ModelRenderer(this, 0, 26);
        paw1.setRotationPoint(-2.0F, 7.0F, -2.0F);
        paw1.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
        paw2 = new ModelRenderer(this, 0, 26);
        paw2.setRotationPoint(-1.0F, 7.0F, -2.0F);
        paw2.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
        paw3 = new ModelRenderer(this, 12, 26);
        paw3.setRotationPoint(-1.0F, 9.0F, -2.0F);
        paw3.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
        paw4 = new ModelRenderer(this, 12, 26);
        paw4.setRotationPoint(-1.0F, 9.0F, -2.0F);
        paw4.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);

        bearHead.addChild(nose);
        bearHead.addChild(ear1);
        bearHead.addChild(ear2);
        rearBody.addChild(tail);
        leg1.addChild(this.paw1);
        leg2.addChild(this.paw2);
        leg3.addChild(this.paw3);
        leg4.addChild(this.paw4);

        --this.leg3.rotationPointX;
        ++this.leg4.rotationPointX;
        ModelRenderer var10000 = this.leg3;
        var10000.rotationPointZ += 0.0F;
        var10000 = this.leg4;
        var10000.rotationPointZ += 0.0F;
        --this.leg1.rotationPointX;
        ++this.leg2.rotationPointX;
        --this.leg1.rotationPointZ;
        --this.leg2.rotationPointZ;
        //this.childZOffset += 2.0F;
    }

    @Override
    public void render(@Nonnull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        if (((EntityAnimal) entity).isChild())
        {
            double ageScale = 1;
            double percent = 1;
            if (entity instanceof IAnimalTFC)
            {
                percent = ((IAnimalTFC) entity).getPercentToAdulthood();
                ageScale = 1 / (2.0D - percent);
            }
            GlStateManager.scale(ageScale, ageScale, ageScale);
            GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0D, 1.0D, 1.0D);

        bearHead.render(scale);
        frontBody.render(scale);
        rearBody.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
        GlStateManager.popMatrix();
    }


    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        bearHead.rotateAngleX = headPitch / (180F / (float) Math.PI);
        bearHead.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);

        leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        float f1 = ((EntityGrizzlyBearTFC) entityIn).getStandingAnimationScale(ageInTicks);
        f1 *= f1;
        float f2 = 1.0F - f1;
        this.frontBody.rotateAngleX = 1.5707964F - f1 * 3.1415927F * 0.35F;
        this.frontBody.rotationPointY = 9.0F * f2 + 11.0F * f1;
        this.leg1.rotationPointY = 14.0F * f2 + -6.0F * f1;
        this.leg1.rotationPointZ = -8.0F * f2 + -4.0F * f1;
        this.leg1.rotateAngleX -= f1 * 3.1415927F * 0.45F;
        this.leg2.rotationPointY = this.leg1.rotationPointY;
        this.leg2.rotationPointZ = this.leg1.rotationPointZ;
        this.leg1.rotateAngleX -= f1 * 3.1415927F * 0.45F;
        this.bearHead.rotationPointY = 8.0F * f2 + -12.0F * f1;
        this.bearHead.rotationPointZ = -14.8F * f2 + -3.0F * f1;
        this.leg1.rotateAngleX += f1 * 3.1415927F * 0.15F;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}