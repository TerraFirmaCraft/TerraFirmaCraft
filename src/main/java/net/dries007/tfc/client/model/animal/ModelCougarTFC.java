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

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelCougarTFC extends ModelBase
{
    public ModelRenderer backBody;
    public ModelRenderer head;
    public ModelRenderer tailBody;
    public ModelRenderer neckBase;
    public ModelRenderer neck;
    public ModelRenderer frontBody;
    public ModelRenderer earLeft;
    public ModelRenderer mouthTop;
    public ModelRenderer nose;
    public ModelRenderer earRight;
    public ModelRenderer mouthBottom;
    public ModelRenderer tailTop;
    public ModelRenderer tailBottom;
    public ModelRenderer frontRightLegTop;
    public ModelRenderer frontRightLegMiddle;
    public ModelRenderer frontRightLegBottom;
    public ModelRenderer frontRightPaw;
    public ModelRenderer frontLeftLegTop;
    public ModelRenderer frontLeftLegMiddle;
    public ModelRenderer frontLeftLegBottom;
    public ModelRenderer frontLeftPaw;
    public ModelRenderer backRightLegTop;
    public ModelRenderer backRightLegMiddle;
    public ModelRenderer backRightLegBottom;
    public ModelRenderer backRightPaw;
    public ModelRenderer backLeftLegTop;
    public ModelRenderer backLeftLegMiddle;
    public ModelRenderer backLeftLegBottom;
    public ModelRenderer backLeftPaw;

    public ModelCougarTFC()
    {

        textureWidth = 64;
        textureHeight = 64;

        frontBody = new ModelRenderer(this, 32, 47);
        frontBody.setRotationPoint(0.5F, 13.5F, -7.8F);
        frontBody.addBox(-4.0F, -5.0F, 0.0F, 7, 8, 9, 0.0F);
        setRotation(frontBody, -0.045553093477052F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 4, 0);
        nose.setRotationPoint(-1.5F, -1.5F, -8.2F);
        nose.addBox(0.0F, 0.0F, 1.0F, 2, 2, 4, 0.0F);
        setRotation(nose, 0.18203784098300857F, 0.0F, 0.0F);
        neckBase = new ModelRenderer(this, 38, 22);
        neckBase.setRotationPoint(0.0F, 13.2F, -6.5F);
        neckBase.addBox(-3.0F, -4.0F, -3.0F, 6, 6, 4, -0.1F);
        setRotation(neckBase, -0.27314402793711257F, 0.0F, 0.0F);
        earRight = new ModelRenderer(this, 17, 4);
        earRight.setRotationPoint(-3.9F, -4.6F, 0.4F);
        earRight.addBox(0.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotation(earRight, -0.7285004297824331F, 0.0F, -0.40980330836826856F);
        neck = new ModelRenderer(this, 39, 12);
        neck.setRotationPoint(0.0F, 11.5F, -8.5F);
        neck.addBox(-2.0F, -2.5F, -3.5F, 4, 5, 5, -0.2F);
        setRotation(neck, -0.18203784098300857F, 0.0F, 0.0F);
        backBody = new ModelRenderer(this, 34, 32);
        backBody.setRotationPoint(0.0F, 13.3F, 4.2F);
        backBody.addBox(-3.0F, -4.0F, -4.0F, 6, 7, 8, 0.0F);
        head = new ModelRenderer(this, 0, 6);
        head.setRotationPoint(0.5F, 11.0F, -11.6F);
        head.addBox(-3.0F, -3.0F, -4.0F, 5, 5, 5, 0.2F);
        earLeft = new ModelRenderer(this, 17, 4);
        earLeft.setRotationPoint(2.0F, -5.0F, 0.4F);
        earLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotation(earLeft, -0.7285004297824331F, 0.0F, 0.40980330836826856F);
        mouthBottom = new ModelRenderer(this, 24, 5);
        mouthBottom.setRotationPoint(-2.0F, 1.0F, -7.8F);
        mouthBottom.addBox(0.0F, 0.0F, 1.0F, 3, 1, 3, 0.0F);
        mouthTop = new ModelRenderer(this, 23, 9);
        mouthTop.setRotationPoint(-2.5F, -1.0F, -8.0F);
        mouthTop.addBox(0.0F, 0.0F, 1.0F, 4, 2, 3, 0.0F);
        tailBody = new ModelRenderer(this, 11, 28);
        tailBody.setRotationPoint(0.5F, 11.5F, 8.5F);
        tailBody.addBox(-1.5F, -1.5F, -1.5F, 2, 3, 2, -0.2F);
        setRotation(tailBody, 0.6981317007977318F, 0.0F, 0.0F);
        tailBottom = new ModelRenderer(this, 13, 19);
        tailBottom.setRotationPoint(0.0F, 2.3F, -1.15F);
        tailBottom.addBox(-0.5F, 1.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(tailBottom, 0.6108652381980153F, 0.0F, 0.0F);
        tailTop = new ModelRenderer(this, 13, 23);
        tailTop.setRotationPoint(-0.5F, 1.2F, 0.0F);
        tailTop.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        setRotation(tailTop, 0.3490658503988659F, 0.0F, 0.0F);

        frontRightLegTop = new ModelRenderer(this, 15, 54);
        frontRightLegTop.setRotationPoint(-3.2F, 10.5F, -6.0F);
        frontRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 3, 6, 4, 0.0F);
        setRotation(frontRightLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegMiddle = new ModelRenderer(this, 17, 44);
        frontRightLegMiddle.setRotationPoint(1.0F, 4.2F, 1.0F);
        frontRightLegMiddle.addBox(-2.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(frontRightLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegBottom = new ModelRenderer(this, 18, 39);
        frontRightLegBottom.setRotationPoint(-1.0F, 5.9F, -0.5F);
        frontRightLegBottom.addBox(-0.99F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(frontRightLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontRightPaw = new ModelRenderer(this, 17, 34);
        frontRightPaw.setRotationPoint(0.0F, 2.4F, 0.5F);
        frontRightPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(frontRightPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backRightLegTop = new ModelRenderer(this, 0, 55);
        backRightLegTop.setRotationPoint(-3.0F, 11.0F, 4.8F);
        backRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 3, 5, 4, 0.0F);
        setRotation(backRightLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backRightLegMiddle = new ModelRenderer(this, 2, 45);
        backRightLegMiddle.setRotationPoint(0.0F, 3.8F, 0.9F);
        backRightLegMiddle.addBox(-1.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(backRightLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backRightLegBottom = new ModelRenderer(this, 3, 40);
        backRightLegBottom.setRotationPoint(0.0F, 5.8F, 0.0F);
        backRightLegBottom.addBox(-0.99F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(backRightLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backRightPaw = new ModelRenderer(this, 2, 35);
        backRightPaw.setRotationPoint(0.0F, 2.5F, 0.5F);
        backRightPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(backRightPaw, 0.08726646259971647F, 0.0F, 0.0F);

        frontLeftLegTop = new ModelRenderer(this, 15, 54);
        frontLeftLegTop.setRotationPoint(3.2F, 10.5F, -6.0F);
        frontLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 3, 6, 4, 0.0F);
        setRotation(frontLeftLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegMiddle = new ModelRenderer(this, 17, 44);
        frontLeftLegMiddle.setRotationPoint(1.0F, 4.2F, 1.0F);
        frontLeftLegMiddle.addBox(-2.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(frontLeftLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegBottom = new ModelRenderer(this, 18, 39);
        frontLeftLegBottom.setRotationPoint(-1.0F, 5.9F, 0.0F);
        frontLeftLegBottom.addBox(-1.01F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(frontLeftLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontLeftPaw = new ModelRenderer(this, 17, 34);
        frontLeftPaw.setRotationPoint(0.0F, 2.4F, 0.5F);
        frontLeftPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(frontLeftPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backLeftLegTop = new ModelRenderer(this, 0, 55);
        backLeftLegTop.setRotationPoint(3.0F, 11.0F, 4.8F);
        backLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 3, 5, 4, 0.0F);
        setRotation(backLeftLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backLeftLegMiddle = new ModelRenderer(this, 2, 45);
        backLeftLegMiddle.setRotationPoint(0.0F, 3.8F, 0.9F);
        backLeftLegMiddle.addBox(-1.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(backLeftLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backLeftLegBottom = new ModelRenderer(this, 3, 40);
        backLeftLegBottom.setRotationPoint(0.0F, 5.8F, 0.0F);
        backLeftLegBottom.addBox(-1.01F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(backLeftLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backLeftPaw = new ModelRenderer(this, 2, 35);
        backLeftPaw.setRotationPoint(0.0F, 2.5F, 0.5F);
        backLeftPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(backLeftPaw, 0.08726646259971647F, 0.0F, 0.0F);

        frontRightLegTop.addChild(frontRightLegMiddle);
        frontRightLegMiddle.addChild(frontRightLegBottom);
        frontRightLegBottom.addChild(frontRightPaw);
        backRightLegTop.addChild(backRightLegMiddle);
        backRightLegMiddle.addChild(backRightLegBottom);
        backRightLegBottom.addChild(backRightPaw);
        frontLeftLegTop.addChild(frontLeftLegMiddle);
        frontLeftLegMiddle.addChild(frontLeftLegBottom);
        frontLeftLegBottom.addChild(frontLeftPaw);
        backLeftLegTop.addChild(backLeftLegMiddle);
        backLeftLegMiddle.addChild(backLeftLegBottom);
        backLeftLegBottom.addChild(backLeftPaw);
        head.addChild(nose);
        head.addChild(earRight);
        head.addChild(earLeft);
        head.addChild(mouthBottom);
        head.addChild(mouthTop);
        tailTop.addChild(tailBottom);
        tailBody.addChild(tailTop);
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

        head.render(f5);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0D, 1.0D, 1.0D);

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
        head.rotateAngleX = f4 / (180F / (float) Math.PI);
        head.rotateAngleY = f3 / (180F / (float) Math.PI);

        frontRightLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
        frontLeftLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
        backRightLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F + (float) Math.PI) * 0.8F * f1;
        backLeftLegTop.rotateAngleX = MathHelper.cos(f * 0.4862F) * 0.8F * f1;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}