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
 * ModelSabertoothTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelSaberToothTFC extends ModelBase
{
    private final ModelRenderer frontBody;
    private final ModelRenderer neckBase;
    private final ModelRenderer neck;
    private final ModelRenderer backBody;
    private final ModelRenderer tailBody;
    private final ModelRenderer tailTop;
    private final ModelRenderer tailBottom;
    private final ModelRenderer head;
    private final ModelRenderer ear2;
    private final ModelRenderer ear1;
    private final ModelRenderer nose;
    private final ModelRenderer tooth1;
    private final ModelRenderer tooth2;
    private final ModelRenderer mouthBottom;
    private final ModelRenderer mouthTop;
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


    public ModelSaberToothTFC()
    {
        textureWidth = 80;
        textureHeight = 64;

        tailBottom = new ModelRenderer(this, 34, 3);
        tailBottom.setRotationPoint(-1.0F, 13.3F, 13.0F);
        tailBottom.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, -0.1F);
        setRotation(tailBottom, 0.9560913642424937F, 0.0F, 0.0F);
        ear1 = new ModelRenderer(this, 0, 4);
        ear1.mirror = true;
        ear1.setRotationPoint(2.1F, -5.8F, -0.9F);
        ear1.addBox(0.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotation(ear1, -0.728F, 0.0F, 0.409F);
        ear2 = new ModelRenderer(this, 0, 4);
        ear2.setRotationPoint(-4.0F, -5.4F, -0.9F);
        ear2.addBox(0.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F);
        setRotation(ear2, -0.728F, 0.0F, -0.409F);
        head = new ModelRenderer(this, 1, 4);
        head.setRotationPoint(0.5F, 10.0F, -11.5F);
        head.addBox(-3.0F, -4.0F, -4.5F, 5, 6, 5, 0.1F);
        tailBody = new ModelRenderer(this, 32, 15);
        tailBody.setRotationPoint(0.0F, 9.2F, 10.5F);
        tailBody.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, -0.2F);
        setRotation(tailBody, 0.5989969992844539F, 0.0F, 0.0F);
        neck = new ModelRenderer(this, 53, 3);
        neck.setRotationPoint(0.0F, 8.6F, -6.0F);
        neck.addBox(-2.0F, -2.5F, -6.5F, 4, 5, 5, 0.0F);
        setRotation(neck, 0.091106186954104F, 0.0F, 0.0F);
        frontBody = new ModelRenderer(this, 42, 43);
        frontBody.setRotationPoint(0.0F, 10.0F, -8.8F);
        frontBody.addBox(-4.0F, -5.0F, 0.0F, 8, 10, 11, 0.0F);
        setRotation(frontBody, -0.07592182246175333F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 16, 3);
        nose.setRotationPoint(-1.5F, -2.299999952316284F, -8.600000381469727F);
        nose.addBox(0.0F, 0.0F, 0.4000000059604645F, 2, 2, 4, 0.0F);
        setRotation(nose, 0.1820378452539444F, 0.0F, 0.0F);
        backBody = new ModelRenderer(this, 46, 24);
        backBody.setRotationPoint(0.0F, 10.2F, 4.8F);
        backBody.addBox(-3.5F, -4.0F, -4.0F, 7, 9, 10, 0.0F);
        setRotation(backBody, -0.028623399732707F, 0.0F, 0.0F);
        neckBase = new ModelRenderer(this, 51, 13);
        neckBase.setRotationPoint(0.5F, 9.4F, -6.8F);
        neckBase.addBox(-4.0F, -4.0F, -3.0F, 7, 7, 4, -0.1F);
        setRotation(neckBase, 0.136659280431156F, 0.0F, 0.0F);
        mouthBottom = new ModelRenderer(this, 5, 15);
        mouthBottom.setRotationPoint(-2.0F, -0.10000000149011612F, -8.0F);
        mouthBottom.addBox(0.0F, 0.0F, 0.5F, 3, 2, 3, 0.0F);
        tailTop = new ModelRenderer(this, 34, 9);
        tailTop.setRotationPoint(-1.0F, 10.8F, 10.3F);
        tailTop.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
        setRotation(tailTop, 0.8337679215359916F, 0.0F, 0.0F);
        mouthTop = new ModelRenderer(this, 3, 20);
        mouthTop.setRotationPoint(-2.5F, -2.0F, -8.300000190734863F);
        mouthTop.addBox(0.0F, 0.0F, 0.5F, 4, 3, 4, 0.0F);
        tooth1 = new ModelRenderer(this, 16, 0);
        tooth1.setRotationPoint(2.7F, 1.2F, 8.2F);
        tooth1.addBox(0.2F, -0.2F, -7.2F, 1, 4, 1, -0.1F);
        setRotation(tooth1, 0.2F, 0.0F, 0.0F);
        tooth2 = new ModelRenderer(this, 16, 0);
        tooth2.setRotationPoint(2.9F, 1.2F, 8.2F);
        tooth2.addBox(-2.8F, -0.2F, -7.2F, 1, 4, 1, -0.1F);
        setRotation(tooth2, 0.2F, 0.0F, 0.0F);

        frontRightLegTop = new ModelRenderer(this, 23, 52);
        frontRightLegTop.setRotationPoint(-3.5F, 8.0F, -6.5F);
        frontRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(frontRightLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegMiddle = new ModelRenderer(this, 25, 41);
        frontRightLegMiddle.setRotationPoint(1.0F, 6.0F, 1.6F);
        frontRightLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(frontRightLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontRightLegBottom = new ModelRenderer(this, 26, 34);
        frontRightLegBottom.setRotationPoint(-0.99F, 6.0F, 0.2F);
        frontRightLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(frontRightLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontRightPaw = new ModelRenderer(this, 25, 28);
        frontRightPaw.setRotationPoint(-0.009F, 3.0F, 1.0F);
        frontRightPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(frontRightPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backRightLegTop = new ModelRenderer(this, 0, 52);
        backRightLegTop.setRotationPoint(-3.5F, 8.5F, 5.0F);
        backRightLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(backRightLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backRightLegMiddle = new ModelRenderer(this, 2, 41);
        backRightLegMiddle.setRotationPoint(1.0F, 5.8F, 1.4F);
        backRightLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(backRightLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backRightLegBottom = new ModelRenderer(this, 3, 34);
        backRightLegBottom.setRotationPoint(-0.99F, 6.0F, 0.1F);
        backRightLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(backRightLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backRightPaw = new ModelRenderer(this, 2, 28);
        backRightPaw.setRotationPoint(-0.009F, 2.9F, 1.0F);
        backRightPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(backRightPaw, 0.08726646259971647F, 0.0F, 0.0F);

        frontLeftLegTop = new ModelRenderer(this, 23, 52);
        frontLeftLegTop.setRotationPoint(2.5F, 8.0F, -6.5F);
        frontLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(frontLeftLegTop, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegMiddle = new ModelRenderer(this, 25, 41);
        frontLeftLegMiddle.setRotationPoint(1.0F, 6.0F, 1.6F);
        frontLeftLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(frontLeftLegMiddle, 0.08726646259971647F, 0.0F, 0.0F);
        frontLeftLegBottom = new ModelRenderer(this, 26, 34);
        frontLeftLegBottom.setRotationPoint(-1.009F, 6.0F, 0.2F);
        frontLeftLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(frontLeftLegBottom, -0.2617993877991494F, 0.0F, 0.0F);
        frontLeftPaw = new ModelRenderer(this, 25, 28);
        frontLeftPaw.setRotationPoint(0.009F, 3.0F, 1.0F);
        frontLeftPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(frontLeftPaw, 0.17453292519943295F, 0.0F, 0.0F);
        backLeftLegTop = new ModelRenderer(this, 0, 52);
        backLeftLegTop.setRotationPoint(2.5F, 8.5F, 5.0F);
        backLeftLegTop.addBox(-1.5F, -1.0F, -1.0F, 4, 7, 5, 0.0F);
        setRotation(backLeftLegTop, -0.08726646259971647F, 0.0F, 0.0F);
        backLeftLegMiddle = new ModelRenderer(this, 2, 41);
        backLeftLegMiddle.setRotationPoint(1.0F, 5.8F, 1.4F);
        backLeftLegMiddle.addBox(-2.0F, -1.0F, -2.0F, 3, 7, 4, 0.0F);
        setRotation(backLeftLegMiddle, 0.2617993877991494F, 0.0F, 0.0F);
        backLeftLegBottom = new ModelRenderer(this, 3, 34);
        backLeftLegBottom.setRotationPoint(-1.009F, 6.0F, 0.1F);
        backLeftLegBottom.addBox(-1.0F, -1.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(backLeftLegBottom, -0.3490658503988659F, 0.0F, 0.0F);
        backLeftPaw = new ModelRenderer(this, 2, 28);
        backLeftPaw.setRotationPoint(0.009F, 2.9F, 1.0F);
        backLeftPaw.addBox(-1.0F, -1.0F, -3.5F, 3, 2, 4, 0.0F);
        setRotation(backLeftPaw, 0.08726646259971647F, 0.0F, 0.0F);


        head.addChild(nose);
        head.addChild(ear1);
        head.addChild(ear2);
        head.addChild(mouthBottom);
        head.addChild(mouthTop);
        mouthTop.addChild(tooth1);
        mouthTop.addChild(tooth2);

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
            GlStateManager.translate(0.0F, 1.25f - (1.25f * percent), 0f);
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0D, 1.0D, 1.0D);

        head.render(f5);
        neck.render(f5);
        neckBase.render(f5);
        frontBody.render(f5);
        backBody.render(f5);
        tailBody.render(f5);
        tailTop.render(f5);
        tailBottom.render(f5);
        frontRightLegTop.render(f5);
        frontLeftLegTop.render(f5);
        backRightLegTop.render(f5);
        backLeftLegTop.render(f5);
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