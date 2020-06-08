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
import net.dries007.tfc.objects.entity.animal.EntityPolarBearTFC;

/**
 * ModelPolarBearTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelPolarBearTFC extends ModelBase
{
    private final ModelRenderer head;
    private final ModelRenderer rearbody;
    private final ModelRenderer frontbody;
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


    public ModelPolarBearTFC()
    {
        textureWidth = 80;
        textureHeight = 64;

        ear1 = new ModelRenderer(this, 26, 12);
        ear1.mirror = true;
        ear1.setRotationPoint(0.2F, -0.5F, 0.0F);
        ear1.addBox(2.5F, -4.0F, 0.0F, 2, 2, 1, 0.0F);
        ear2 = new ModelRenderer(this, 26, 12);
        ear2.setRotationPoint(-0.1F, -0.5F, 0.0F);
        ear2.addBox(-4.5F, -4.0F, 0.0F, 2, 2, 1, 0.0F);
        head = new ModelRenderer(this, 0, 7);
        head.setRotationPoint(0.0F, 9.0F, -16.0F);
        head.addBox(-4.0F, -3.0F, -3.5F, 8, 7, 8, 0.0F);
        nose = new ModelRenderer(this, 7, 0);
        nose.setRotationPoint(0.0F, 0.0F, -0.2F);
        nose.addBox(-3.0F, 0.0F, -6.0F, 6, 4, 3, 0.0F);
        frontbody = new ModelRenderer(this, 28, 40);
        frontbody.setRotationPoint(-2.0F, 8.6F, 12.0F);
        frontbody.addBox(-5.0F, -25.0F, -6.0F, 14, 12, 12, 0.0F);
        setRotation(frontbody, 1.5707963267948966F, 0.0F, 0.0F);
        rearbody = new ModelRenderer(this, 22, 13);
        rearbody.setRotationPoint(-2.0F, 9.0F, 12.0F);
        rearbody.addBox(-6.0F, -13.0F, -6.0F, 16, 14, 13, 0.0F);
        setRotation(rearbody, 1.5707963705062866F, 0.0F, 0.0F);
        tail = new ModelRenderer(this, 45, 7);
        tail.setRotationPoint(1.5F, 0.7F, 0.0F);
        tail.addBox(-1.0F, -1.0F, -1.0F, 3, 3, 3, 0.0F);
        setRotation(tail, -0.36425021489121656F, 0.0F, 0.0F);
        leg1 = new ModelRenderer(this, 1, 47);
        leg1.setRotationPoint(-4.6F, 14.0F, 6.0F);
        leg1.addBox(-2.0F, 0.0F, -2.0F, 5, 10, 7, 0.0F);
        leg2 = new ModelRenderer(this, 1, 47);
        leg2.setRotationPoint(3.6F, 14.0F, 6.0F);
        leg2.addBox(-2.0F, 0.0F, -2.0F, 5, 10, 7, 0.0F);
        leg3 = new ModelRenderer(this, 0, 28);
        leg3.setRotationPoint(-4.6F, 14.0F, -8.0F);
        leg3.addBox(-2.0F, 0.0F, -2.0F, 5, 10, 6, 0.0F);
        leg4 = new ModelRenderer(this, 0, 28);
        leg4.setRotationPoint(3.6F, 14.0F, -8.0F);
        leg4.addBox(-2.0F, 0.0F, -2.0F, 5, 10, 6, 0.0F);
        paw1 = new ModelRenderer(this, 0, 22);
        paw1.setRotationPoint(-2.0F, 7.0F, -3.0F);
        paw1.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
        paw2 = new ModelRenderer(this, 0, 22);
        paw2.setRotationPoint(-2.0F, 7.0F, -3.0F);
        paw2.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
        paw3 = new ModelRenderer(this, 12, 22);
        paw3.setRotationPoint(-2.0F, 7.0F, -3.0F);
        paw3.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);
        paw4 = new ModelRenderer(this, 12, 22);
        paw4.setRotationPoint(-2.0F, 7.0F, -3.0F);
        paw4.addBox(0.0F, 0.0F, 0.0F, 5, 3, 1, 0.0F);

        head.addChild(ear1);
        head.addChild(ear2);
        head.addChild(nose);
        rearbody.addChild(tail);
        leg1.addChild(paw1);
        leg2.addChild(paw2);
        leg3.addChild(paw3);
        leg4.addChild(paw4);

        --this.leg1.rotationPointX;
        ++this.leg2.rotationPointX;
        ModelRenderer var10000 = this.leg1;
        var10000.rotationPointZ += 0.0F;
        var10000 = this.leg2;
        var10000.rotationPointZ += 0.0F;
        --this.leg3.rotationPointX;
        ++this.leg4.rotationPointX;
        --this.leg3.rotationPointZ;
        --this.leg4.rotationPointZ;
        //this.childZOffset += 2.0F;;
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

        head.render(scale);
        rearbody.render(scale);
        frontbody.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        head.rotateAngleX = headPitch / (180F / (float) Math.PI);
        head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);

        leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        float f1 = ((EntityPolarBearTFC) entityIn).getStandingAnimationScale(ageInTicks);
        f1 *= f1;
        float f2 = 1.0F - f1;
        frontbody.rotateAngleX = 1.5707964F - f1 * 3.1415927F * 0.35F;
        frontbody.rotationPointY = 9.0F * f2 + 11.0F * f1;
        leg3.rotationPointY = 14.0F * f2 + -6.0F * f1;
        leg3.rotationPointZ = -8.0F * f2 + -4.0F * f1;
        leg3.rotateAngleX -= f1 * 3.1415927F * 0.45F;
        leg4.rotationPointY = leg3.rotationPointY;
        leg4.rotationPointZ = leg3.rotationPointZ;
        leg4.rotateAngleX -= f1 * 3.1415927F * 0.45F;
        head.rotationPointY = 10.0F * f2 + -12.0F * f1;
        head.rotationPointZ = -16.0F * f2 + -3.0F * f1;
        head.rotateAngleX += f1 * 3.1415927F * 0.15F;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

