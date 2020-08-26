/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

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

/**
 * ModelPantherTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelPantherTFC extends ModelBase
{
    public ModelRenderer backBody;
    public ModelRenderer backLeftLegTop;
    public ModelRenderer head;
    public ModelRenderer tailBody;
    public ModelRenderer frontRightLegTop;
    public ModelRenderer neckBase;
    public ModelRenderer backRightLegTop;
    public ModelRenderer frontLeftLegTop;
    public ModelRenderer neck;
    public ModelRenderer frontBody;
    public ModelRenderer backLeftLegMiddle;
    public ModelRenderer backLeftLegBottom;
    public ModelRenderer backLeftLegPaw;
    public ModelRenderer nose;
    public ModelRenderer earRight;
    public ModelRenderer earLeft;
    public ModelRenderer mouthBottom;
    public ModelRenderer mouthTop;
    public ModelRenderer tailMiddle;
    public ModelRenderer tailTip;
    public ModelRenderer frontRightLegMiddle;
    public ModelRenderer frontRightLegBottom;
    public ModelRenderer frontRightLegPaw;
    public ModelRenderer backRightLegMiddle;
    public ModelRenderer backRightLegBottom;
    public ModelRenderer backRightLegPaw;
    public ModelRenderer frontLeftLegMiddle;
    public ModelRenderer frontLeftLegBottom;
    public ModelRenderer frontLeftLegPaw;

    public ModelPantherTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        frontLeftLegPaw = new ModelRenderer(this, 1, 33);
        frontLeftLegPaw.mirror = true;
        frontLeftLegPaw.setRotationPoint(0.0F, 2.4F, 0.5F);
        frontLeftLegPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotateAngle(frontLeftLegPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backBody = new ModelRenderer(this, 33, 31);
        backBody.setRotationPoint(0.0F, 13.2F, 4.0F);
        backBody.addBox(-3.0F, -4.0F, -4.0F, 6, 7, 9, 0.0F);
        backLeftLegMiddle = new ModelRenderer(this, 16, 43);
        backLeftLegMiddle.mirror = true;
        backLeftLegMiddle.setRotationPoint(-0.7F, 4.8F, 0.9F);
        backLeftLegMiddle.addBox(-1.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotateAngle(backLeftLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backLeftLegBottom = new ModelRenderer(this, 17, 38);
        backLeftLegBottom.mirror = true;
        backLeftLegBottom.setRotationPoint(0.0F, 5.8F, 0.0F);
        backLeftLegBottom.addBox(-1.01F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotateAngle(backLeftLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        mouthBottom = new ModelRenderer(this, 24, 5);
        mouthBottom.setRotationPoint(-2.0F, 1.0F, -7.8F);
        mouthBottom.addBox(0.0F, 0.0F, 1.0F, 3, 1, 3, 0.0F);
        earRight = new ModelRenderer(this, 17, 4);
        earRight.setRotationPoint(-3.9F, -4.6F, 0.4F);
        earRight.addBox(0.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotateAngle(earRight, -0.7285004297824331F, 0.0F, -0.40980330836826856F);
        tailMiddle = new ModelRenderer(this, 13, 23);
        tailMiddle.setRotationPoint(-0.5F, 1.2F, 0.0F);
        tailMiddle.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        setRotateAngle(tailMiddle, -0.3490658503988659F, 0.0F, 0.0F);
        frontLeftLegTop = new ModelRenderer(this, 0, 53);
        frontLeftLegTop.mirror = true;
        frontLeftLegTop.setRotationPoint(4.0F, 9.5F, -6.0F);
        frontLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 2, 7, 4, 0.0F);
        head = new ModelRenderer(this, 0, 6);
        head.setRotationPoint(0.5F, 11.3F, -11.4F);
        head.addBox(-3.0F, -3.0F, -4.0F, 5, 5, 5, 0.1F);
        mouthTop = new ModelRenderer(this, 23, 9);
        mouthTop.setRotationPoint(-2.5F, -0.6F, -8.0F);
        mouthTop.addBox(0.0F, 0.0F, 1.0F, 4, 2, 3, 0.0F);
        frontRightLegTop = new ModelRenderer(this, 0, 53);
        frontRightLegTop.setRotationPoint(-3.0F, 9.5F, -6.0F);
        frontRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 2, 7, 4, 0.0F);
        frontLeftLegBottom = new ModelRenderer(this, 2, 38);
        frontLeftLegBottom.mirror = true;
        frontLeftLegBottom.setRotationPoint(-1.0F, 5.9F, -0.2F);
        frontLeftLegBottom.addBox(-1.01F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotateAngle(frontLeftLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 4, 0);
        nose.setRotationPoint(-1.5F, -1.2F, -8.3F);
        nose.addBox(0.0F, 0.0F, 1.0F, 2, 2, 4, 0.0F);
        setRotateAngle(nose, 0.18203784098300857F, 0.0F, 0.0F);
        backRightLegPaw = new ModelRenderer(this, 16, 33);
        backRightLegPaw.setRotationPoint(0.0F, 2.5F, 0.5F);
        backRightLegPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotateAngle(backRightLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegMiddle = new ModelRenderer(this, 1, 43);
        frontLeftLegMiddle.mirror = true;
        frontLeftLegMiddle.setRotationPoint(0.2F, 5.2F, 1.0F);
        frontLeftLegMiddle.addBox(-2.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotateAngle(frontLeftLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        neck = new ModelRenderer(this, 40, 12);
        neck.setRotationPoint(0.0F, 11.3F, -8.2F);
        neck.addBox(-2.0F, -2.5F, -3.5F, 4, 5, 4, -0.2F);
        setRotateAngle(neck, -0.18203784098300857F, 0.0F, 0.0F);
        earLeft = new ModelRenderer(this, 17, 4);
        earLeft.mirror = true;
        earLeft.setRotationPoint(2.0F, -5.0F, 0.4F);
        earLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotateAngle(earLeft, -0.7285004297824331F, 0.0F, 0.40980330836826856F);
        tailTip = new ModelRenderer(this, 13, 17);
        tailTip.setRotationPoint(0.0F, 2.3F, -0.75F);
        tailTip.addBox(-0.5F, 1.0F, -0.5F, 1, 5, 1, 0.0F);
        setRotateAngle(tailTip, 0.2617993877991494F, 0.0F, 0.0F);
        backLeftLegTop = new ModelRenderer(this, 15, 53);
        backLeftLegTop.mirror = true;
        backLeftLegTop.setRotationPoint(3.8F, 10.0F, 5.5F);
        backLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 2, 7, 4, 0.0F);
        neckBase = new ModelRenderer(this, 38, 21);
        neckBase.setRotationPoint(0.0F, 13.2F, -6.5F);
        neckBase.addBox(-3.0F, -4.0F, -3.0F, 6, 6, 4, -0.1F);
        setRotateAngle(neckBase, -0.27314402793711257F, 0.0F, 0.0F);
        backRightLegBottom = new ModelRenderer(this, 17, 38);
        backRightLegBottom.setRotationPoint(0.0F, 5.8F, 0.0F);
        backRightLegBottom.addBox(-0.99F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotateAngle(backRightLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        frontRightLegPaw = new ModelRenderer(this, 1, 33);
        frontRightLegPaw.setRotationPoint(0.0F, 2.4F, 0.5F);
        frontRightLegPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotateAngle(frontRightLegPaw, 0.17453292519943295F, 0.0F, 0.0F);
        tailBody = new ModelRenderer(this, 11, 28);
        tailBody.setRotationPoint(0.5F, 11.5F, 9.5F);
        tailBody.addBox(-1.5F, -1.5F, -1.5F, 2, 3, 2, -0.2F);
        setRotateAngle(tailBody, 0.6981317007977318F, 0.0F, 0.0F);
        frontRightLegBottom = new ModelRenderer(this, 2, 38);
        frontRightLegBottom.setRotationPoint(-1.0F, 5.9F, -0.2F);
        frontRightLegBottom.addBox(-0.99F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotateAngle(frontRightLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontBody = new ModelRenderer(this, 32, 47);
        frontBody.setRotationPoint(0.5F, 13.5F, -7.8F);
        frontBody.addBox(-4.0F, -5.0F, 0.0F, 7, 8, 9, 0.1F);
        setRotateAngle(frontBody, -0.045553093477052F, 0.0F, 0.0F);
        backLeftLegPaw = new ModelRenderer(this, 16, 33);
        backLeftLegPaw.mirror = true;
        backLeftLegPaw.setRotationPoint(0.0F, 2.5F, 0.5F);
        backLeftLegPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotateAngle(backLeftLegPaw, 0.08726646259971647F, 0.0F, 0.0F);
        backRightLegTop = new ModelRenderer(this, 15, 53);
        backRightLegTop.setRotationPoint(-2.8F, 10.0F, 5.5F);
        backRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 2, 7, 4, 0.0F);
        backRightLegMiddle = new ModelRenderer(this, 16, 43);
        backRightLegMiddle.setRotationPoint(-0.2F, 4.8F, 0.9F);
        backRightLegMiddle.addBox(-1.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotateAngle(backRightLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        frontRightLegMiddle = new ModelRenderer(this, 1, 43);
        frontRightLegMiddle.setRotationPoint(0.8F, 5.2F, 1.0F);
        frontRightLegMiddle.addBox(-2.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotateAngle(frontRightLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);

        frontLeftLegBottom.addChild(frontLeftLegPaw);
        backLeftLegTop.addChild(backLeftLegMiddle);
        backLeftLegMiddle.addChild(backLeftLegBottom);
        head.addChild(mouthBottom);
        head.addChild(earRight);
        tailBody.addChild(tailMiddle);
        head.addChild(mouthTop);
        frontLeftLegMiddle.addChild(frontLeftLegBottom);
        head.addChild(nose);
        backRightLegBottom.addChild(backRightLegPaw);
        frontLeftLegTop.addChild(frontLeftLegMiddle);
        head.addChild(earLeft);
        tailMiddle.addChild(tailTip);
        backRightLegMiddle.addChild(backRightLegBottom);
        frontRightLegBottom.addChild(frontRightLegPaw);
        frontRightLegMiddle.addChild(frontRightLegBottom);
        backLeftLegBottom.addChild(backLeftLegPaw);
        backRightLegTop.addChild(backRightLegMiddle);
        frontRightLegTop.addChild(frontRightLegMiddle);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);

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

        head.render(f5);
        neck.render(f5);
        neckBase.render(f5);
        frontBody.render(f5);
        backBody.render(f5);
        tailBody.render(f5);
        frontRightLegTop.render(f5);
        frontLeftLegTop.render(f5);
        backRightLegTop.render(f5);
        backLeftLegTop.render(f5);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        //super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        setRotateAngle(head, f4 / (180F / (float) Math.PI), f3 / (180F / (float) Math.PI), 0F);
        setRotateAngle(neck, f4 / (1.5F * (180F / (float) Math.PI)) + -0.18203784098300857F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);

        frontRightLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
        frontLeftLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
        backRightLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
        backLeftLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
    }

    private void setRotateAngle(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}