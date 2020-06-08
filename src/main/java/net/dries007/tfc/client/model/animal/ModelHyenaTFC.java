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

/**
 * ModelHyenaTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelHyenaTFC extends ModelBase
{
    public ModelRenderer backBody;
    public ModelRenderer head;
    public ModelRenderer neck;
    public ModelRenderer frontBody;
    public ModelRenderer tailTop;
    public ModelRenderer tailBottom;
    public ModelRenderer earLeft;
    public ModelRenderer mouthTop;
    public ModelRenderer nose;
    public ModelRenderer earRight;
    public ModelRenderer mouthBottom;
    public ModelRenderer headManeCenter;
    public ModelRenderer headManeLeft;
    public ModelRenderer headManeRight;
    public ModelRenderer neckManeCenter;
    public ModelRenderer neckManeLeft;
    public ModelRenderer neckManeRight;
    public ModelRenderer bodyManeCenter;
    public ModelRenderer bodyManeLeft;
    public ModelRenderer bodyManeRight;
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

    public ModelHyenaTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        tailTop = new ModelRenderer(this, 48, 11);
        tailTop.setRotationPoint(-1.0F, -2.0F, 2.0F);
        tailTop.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotation(tailTop, 0.36756634047000575F, 0.0F, 0.0F);
        tailBottom = new ModelRenderer(this, 46, 16);
        tailBottom.setRotationPoint(-0.5F, 1.5F, -0.5F);
        tailBottom.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
        setRotation(tailBottom, -0.22759093446006054F, 0.0F, 0.0F);
        earRight = new ModelRenderer(this, 20, 2);
        earRight.setRotationPoint(-1.6F, -3.0F, 0.0F);
        earRight.addBox(-1.0F, -1.0F, -0.5F, 2, 3, 1, 0.0F);
        setRotation(earRight, 0.0F, 0.0F, -0.4363323129985824F);
        bodyManeLeft = new ModelRenderer(this, 0, 32);
        bodyManeLeft.setRotationPoint(0.5F, -5.5F, 3.4F);
        bodyManeLeft.addBox(0.0F, -1.0F, -2.5F, 0, 2, 4, 0.0F);
        neckManeRight = new ModelRenderer(this, 0, 25);
        neckManeRight.setRotationPoint(-1.0F, -2.7F, 0.0F);
        neckManeRight.addBox(0.0F, -1.5F, -2.5F, 0, 3, 4, 0.0F);
        headManeLeft = new ModelRenderer(this, 0, 20);
        headManeLeft.setRotationPoint(0.5F, -4.0F, 0.5F);
        headManeLeft.addBox(0.0F, -1.0F, -1.5F, 0, 2, 2, 0.0F);
        headManeRight = new ModelRenderer(this, 0, 20);
        headManeRight.setRotationPoint(-0.5F, -4.0F, 0.5F);
        headManeRight.addBox(0.0F, -1.0F, -1.5F, 0, 2, 2, 0.0F);
        head = new ModelRenderer(this, 0, 8);
        head.setRotationPoint(-0.5F, 12.0F, -11.2F);
        head.addBox(-2.0F, -3.2F, -3.0F, 4, 5, 4, 0.0F);
        frontBody = new ModelRenderer(this, 36, 49);
        frontBody.setRotationPoint(-0.5F, 14.2F, -7.5F);
        frontBody.addBox(-3.0F, -5.0F, 0.0F, 6, 7, 8, 0.0F);
        setRotation(frontBody, -0.045553093477052F, 0.0F, 0.0F);
        neckManeLeft = new ModelRenderer(this, 0, 25);
        neckManeLeft.setRotationPoint(0.0F, -2.7F, 0.0F);
        neckManeLeft.addBox(0.0F, -1.5F, -2.5F, 0, 3, 4, 0.0F);
        neckManeCenter = new ModelRenderer(this, 0, 21);
        neckManeCenter.setRotationPoint(-0.5F, -2.7F, 0.0F);
        neckManeCenter.addBox(0.0F, -1.5F, -2.5F, 0, 3, 4, 0.0F);
        neck = new ModelRenderer(this, 42, 25);
        neck.setRotationPoint(0.0F, 11.4F, -8.0F);
        neck.addBox(-2.0F, -2.0F, -3.5F, 3, 4, 5, 0.0F);
        setRotation(neck, -0.05235987755982988F, 0.0F, 0.0F);
        mouthBottom = new ModelRenderer(this, 18, 7);
        mouthBottom.setRotationPoint(0.0F, 1.0F, -4.4F);
        mouthBottom.addBox(-1.0F, -0.2F, -1.0F, 2, 1, 3, 0.0F);
        bodyManeCenter = new ModelRenderer(this, 0, 26);
        bodyManeCenter.setRotationPoint(0.0F, -5.5F, 3.4F);
        bodyManeCenter.addBox(0.0F, -1.0F, -2.5F, 0, 2, 7, 0.0F);
        earLeft = new ModelRenderer(this, 20, 2);
        earLeft.mirror = true;
        earLeft.setRotationPoint(1.6F, -3.0F, 0.0F);
        earLeft.addBox(-1.0F, -1.0F, -0.5F, 2, 3, 1, 0.0F);
        setRotation(earLeft, 0.0F, 0.0F, 0.4363323129985824F);
        headManeCenter = new ModelRenderer(this, 0, 16);
        headManeCenter.setRotationPoint(0.0F, -4.0F, -0.5F);
        headManeCenter.addBox(0.0F, -1.0F, -1.5F, 0, 2, 3, 0.0F);
        nose = new ModelRenderer(this, 2, 1);
        nose.setRotationPoint(0.0F, -1.3F, -4.7F);
        nose.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 4, 0.0F);
        setRotation(nose, 0.18203784098300857F, 0.0F, 0.0F);
        backBody = new ModelRenderer(this, 38, 35);
        backBody.setRotationPoint(0.0F, 13.3F, 4.0F);
        backBody.addBox(-3.0F, -3.5F, -4.0F, 5, 6, 7, 0.0F);
        bodyManeRight = new ModelRenderer(this, 0, 32);
        bodyManeRight.setRotationPoint(-0.5F, -5.5F, 3.4F);
        bodyManeRight.addBox(0.0F, -1.0F, -2.5F, 0, 2, 4, 0.0F);
        mouthTop = new ModelRenderer(this, 17, 12);
        mouthTop.setRotationPoint(0.0F, -1.0F, -4.5F);
        mouthTop.addBox(-1.5F, 0.0F, -1.0F, 3, 2, 3, 0.0F);

        frontRightLegTop = new ModelRenderer(this, 22, 54);
        frontRightLegTop.setRotationPoint(-3.2F, 11.0F, -6.0F);
        frontRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 3, 6, 4, 0.0F);
        setRotation(frontRightLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegMiddle = new ModelRenderer(this, 23, 44);
        frontRightLegMiddle.setRotationPoint(1.0F, 3.7F, 1.0F);
        frontRightLegMiddle.addBox(-2.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(frontRightLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegBottom = new ModelRenderer(this, 24, 39);
        frontRightLegBottom.setRotationPoint(-1.0F, 5.9F, -0.5F);
        frontRightLegBottom.addBox(-0.99F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(frontRightLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontRightPaw = new ModelRenderer(this, 23, 34);
        frontRightPaw.setRotationPoint(0.0F, 2.4F, 0.5F);
        frontRightPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(frontRightPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backRightLegTop = new ModelRenderer(this, 9, 55);
        backRightLegTop.setRotationPoint(-3.0F, 11.5F, 3.0F);
        backRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 2, 5, 4, 0.0F);
        setRotation(backRightLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backRightLegMiddle = new ModelRenderer(this, 10, 45);
        backRightLegMiddle.setRotationPoint(-0.3F, 3.4F, 0.9F);
        backRightLegMiddle.addBox(-1.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(backRightLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backRightLegBottom = new ModelRenderer(this, 11, 40);
        backRightLegBottom.setRotationPoint(0.0F, 5.8F, 0.0F);
        backRightLegBottom.addBox(-0.99F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(backRightLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backRightPaw = new ModelRenderer(this, 10, 35);
        backRightPaw.setRotationPoint(0.0F, 2.5F, 0.5F);
        backRightPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(backRightPaw, 0.08726646259971647F, 0.0F, 0.0F);

        frontLeftLegTop = new ModelRenderer(this, 22, 54);
        frontLeftLegTop.mirror = true;
        frontLeftLegTop.setRotationPoint(2.2F, 11.0F, -6.0F);
        frontLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 3, 6, 4, 0.0F);
        setRotation(frontLeftLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegMiddle = new ModelRenderer(this, 23, 44);
        frontLeftLegMiddle.mirror = true;
        frontLeftLegMiddle.setRotationPoint(1.0F, 3.7F, 1.0F);
        frontLeftLegMiddle.addBox(-2.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(frontLeftLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegBottom = new ModelRenderer(this, 24, 39);
        frontLeftLegBottom.mirror = true;
        frontLeftLegBottom.setRotationPoint(-1.0F, 5.9F, 0.0F);
        frontLeftLegBottom.addBox(-1.01F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(frontLeftLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontLeftPaw = new ModelRenderer(this, 23, 34);
        frontLeftPaw.mirror = true;
        frontLeftPaw.setRotationPoint(0.0F, 2.4F, 0.5F);
        frontLeftPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(frontLeftPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backLeftLegTop = new ModelRenderer(this, 9, 55);
        backLeftLegTop.mirror = true;
        backLeftLegTop.setRotationPoint(3.0F, 11.5F, 3.0F);
        backLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 2, 5, 4, 0.0F);
        setRotation(backLeftLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backLeftLegMiddle = new ModelRenderer(this, 10, 45);
        backLeftLegMiddle.mirror = true;
        backLeftLegMiddle.setRotationPoint(-0.7F, 3.4F, 0.9F);
        backLeftLegMiddle.addBox(-1.0F, -1.0F, -1.5F, 2, 7, 3, 0.0F);
        setRotation(backLeftLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backLeftLegBottom = new ModelRenderer(this, 11, 40);
        backLeftLegBottom.mirror = true;
        backLeftLegBottom.setRotationPoint(0.0F, 5.8F, 0.0F);
        backLeftLegBottom.addBox(-1.01F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotation(backLeftLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backLeftPaw = new ModelRenderer(this, 10, 35);
        backLeftPaw.mirror = true;
        backLeftPaw.setRotationPoint(0.0F, 2.5F, 0.5F);
        backLeftPaw.addBox(-1.0F, -1.0F, -2.5F, 2, 2, 3, 0.0F);
        setRotation(backLeftPaw, 0.08726646259971647F, 0.0F, 0.0F);

        tailTop.addChild(tailBottom);
        head.addChild(earRight);
        frontBody.addChild(bodyManeLeft);
        neck.addChild(neckManeRight);
        head.addChild(headManeLeft);
        head.addChild(headManeRight);
        neck.addChild(neckManeLeft);
        neck.addChild(neckManeCenter);
        head.addChild(mouthBottom);
        frontBody.addChild(bodyManeCenter);
        head.addChild(earLeft);
        head.addChild(headManeCenter);
        head.addChild(nose);
        backBody.addChild(tailTop);
        frontBody.addChild(bodyManeRight);
        head.addChild(mouthTop);

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
        frontBody.render(scale);
        neck.render(scale);
        backBody.render(scale);
        frontRightLegTop.render(scale);
        frontLeftLegTop.render(scale);
        backRightLegTop.render(scale);
        backLeftLegTop.render(scale);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
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
