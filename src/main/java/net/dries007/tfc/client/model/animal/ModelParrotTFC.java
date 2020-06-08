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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.IAnimalTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelParrotTFC extends ModelBase
{
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer wingLeft;
    private final ModelRenderer wingRight;
    private final ModelRenderer head;
    private final ModelRenderer feather;
    private final ModelRenderer legLeft;
    private final ModelRenderer legRight;
    private State state;

    public ModelParrotTFC()
    {
        this.state = State.STANDING;
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.body = new ModelRenderer(this, 2, 8);
        this.body.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3);
        this.body.setRotationPoint(0.0F, 16.5F, -3.0F);
        this.tail = new ModelRenderer(this, 22, 1);
        this.tail.addBox(-1.5F, -1.0F, -1.0F, 3, 4, 1);
        this.tail.setRotationPoint(0.0F, 21.07F, 1.16F);
        this.wingLeft = new ModelRenderer(this, 19, 8);
        this.wingLeft.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
        this.wingLeft.setRotationPoint(1.5F, 16.94F, -2.76F);
        this.wingRight = new ModelRenderer(this, 19, 8);
        this.wingRight.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
        this.wingRight.setRotationPoint(-1.5F, 16.94F, -2.76F);
        this.head = new ModelRenderer(this, 2, 2);
        this.head.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 2);
        this.head.setRotationPoint(0.0F, 15.69F, -2.76F);
        ModelRenderer head2 = new ModelRenderer(this, 10, 0);
        head2.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4);
        head2.setRotationPoint(0.0F, -2.0F, -1.0F);
        this.head.addChild(head2);
        ModelRenderer beak1 = new ModelRenderer(this, 11, 7);
        beak1.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1);
        beak1.setRotationPoint(0.0F, -0.5F, -1.5F);
        this.head.addChild(beak1);
        ModelRenderer beak2 = new ModelRenderer(this, 16, 7);
        beak2.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
        beak2.setRotationPoint(0.0F, -1.75F, -2.45F);
        this.head.addChild(beak2);
        this.feather = new ModelRenderer(this, 2, 18);
        this.feather.addBox(0.0F, -4.0F, -2.0F, 0, 5, 4);
        this.feather.setRotationPoint(0.0F, -2.15F, 0.15F);
        this.head.addChild(this.feather);
        this.legLeft = new ModelRenderer(this, 14, 18);
        this.legLeft.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
        this.legLeft.setRotationPoint(1.0F, 22.0F, -1.05F);
        this.legRight = new ModelRenderer(this, 14, 18);
        this.legRight.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
        this.legRight.setRotationPoint(-1.0F, 22.0F, -1.05F);
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

        this.body.render(scale);
        this.wingLeft.render(scale);
        this.wingRight.render(scale);
        this.tail.render(scale);
        this.head.render(scale);
        this.legLeft.render(scale);
        this.legRight.render(scale);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        float f = ageInTicks * 0.3F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleZ = 0.0F;
        this.head.rotationPointX = 0.0F;
        this.body.rotationPointX = 0.0F;
        this.tail.rotationPointX = 0.0F;
        this.wingRight.rotationPointX = -1.5F;
        this.wingLeft.rotationPointX = 1.5F;
        if (this.state != State.FLYING)
        {
            if (this.state == State.SITTING)
            {
                return;
            }

            if (this.state == State.PARTY)
            {
                float f1 = MathHelper.cos((float) entityIn.ticksExisted);
                float f2 = MathHelper.sin((float) entityIn.ticksExisted);
                this.head.rotationPointX = f1;
                this.head.rotationPointY = 15.69F + f2;
                this.head.rotateAngleX = 0.0F;
                this.head.rotateAngleY = 0.0F;
                this.head.rotateAngleZ = MathHelper.sin((float) entityIn.ticksExisted) * 0.4F;
                this.body.rotationPointX = f1;
                this.body.rotationPointY = 16.5F + f2;
                this.wingLeft.rotateAngleZ = -0.0873F - ageInTicks;
                this.wingLeft.rotationPointX = 1.5F + f1;
                this.wingLeft.rotationPointY = 16.94F + f2;
                this.wingRight.rotateAngleZ = 0.0873F + ageInTicks;
                this.wingRight.rotationPointX = -1.5F + f1;
                this.wingRight.rotationPointY = 16.94F + f2;
                this.tail.rotationPointX = f1;
                this.tail.rotationPointY = 21.07F + f2;
                return;
            }

            ModelRenderer var10000 = this.legLeft;
            var10000.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            var10000 = this.legRight;
            var10000.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
        }

        this.head.rotationPointY = 15.69F + f;
        this.tail.rotateAngleX = 1.015F + MathHelper.cos(limbSwing * 0.6662F) * 0.3F * limbSwingAmount;
        this.tail.rotationPointY = 21.07F + f;
        this.body.rotationPointY = 16.5F + f;
        this.wingLeft.rotateAngleZ = -0.0873F - ageInTicks;
        this.wingLeft.rotationPointY = 16.94F + f;
        this.wingRight.rotateAngleZ = 0.0873F + ageInTicks;
        this.wingRight.rotationPointY = 16.94F + f;
        this.legLeft.rotationPointY = 22.0F + f;
        this.legRight.rotationPointY = 22.0F + f;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        this.feather.rotateAngleX = -0.2214F;
        this.body.rotateAngleX = 0.4937F;
        this.wingLeft.rotateAngleX = -0.69813174F;
        this.wingLeft.rotateAngleY = -3.1415927F;
        this.wingRight.rotateAngleX = -0.69813174F;
        this.wingRight.rotateAngleY = -3.1415927F;
        this.legLeft.rotateAngleX = -0.0299F;
        this.legRight.rotateAngleX = -0.0299F;
        this.legLeft.rotationPointY = 22.0F;
        this.legRight.rotationPointY = 22.0F;
        if (entitylivingbaseIn instanceof EntityParrot)
        {
            EntityParrot entityparrot = (EntityParrot) entitylivingbaseIn;
            if (entityparrot.isPartying())
            {
                this.legLeft.rotateAngleZ = -0.34906584F;
                this.legRight.rotateAngleZ = 0.34906584F;
                this.state = State.PARTY;
                return;
            }

            if (entityparrot.isSitting())
            {
                this.head.rotationPointY = 17.59F;
                this.tail.rotateAngleX = 1.5388988F;
                this.tail.rotationPointY = 22.97F;
                this.body.rotationPointY = 18.4F;
                this.wingLeft.rotateAngleZ = -0.0873F;
                this.wingLeft.rotationPointY = 18.84F;
                this.wingRight.rotateAngleZ = 0.0873F;
                this.wingRight.rotationPointY = 18.84F;
                ++this.legLeft.rotationPointY;
                ++this.legRight.rotationPointY;
                ++this.legLeft.rotateAngleX;
                ++this.legRight.rotateAngleX;
                this.state = State.SITTING;
            }
            else if (entityparrot.isFlying())
            {
                ModelRenderer var10000 = this.legLeft;
                var10000.rotateAngleX += 0.69813174F;
                var10000 = this.legRight;
                var10000.rotateAngleX += 0.69813174F;
                this.state = State.FLYING;
            }
            else
            {
                this.state = State.STANDING;
            }

            this.legLeft.rotateAngleZ = 0.0F;
            this.legRight.rotateAngleZ = 0.0F;
        }

    }

    @SideOnly(Side.CLIENT)
    enum State
    {
        FLYING,
        STANDING,
        SITTING,
        PARTY
    }
}
