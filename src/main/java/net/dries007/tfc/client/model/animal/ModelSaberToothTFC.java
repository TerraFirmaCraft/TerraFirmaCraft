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
public class ModelSaberToothTFC extends ModelBase
{
    private ModelRenderer frontBody;
    private ModelRenderer neckBase;
    private ModelRenderer neck;
    private ModelRenderer backBody;
    private ModelRenderer tailBody;
    private ModelRenderer tailTop;
    private ModelRenderer tailBottom;
    private ModelRenderer head;
    private ModelRenderer ear2;
    private ModelRenderer ear1;
    private ModelRenderer nose;
    private ModelRenderer tooth1;
    private ModelRenderer tooth2;
    private ModelRenderer mouthBottom;
    private ModelRenderer mouthTop;
    private ModelRenderer upperLeg1;
    private ModelRenderer upperLeg2;
    private ModelRenderer upperLeg3;
    private ModelRenderer upperLeg4;
    private ModelRenderer leg1;
    private ModelRenderer leg2;
    private ModelRenderer leg3;
    private ModelRenderer leg4;
    private ModelRenderer paw1;
    private ModelRenderer paw2;
    private ModelRenderer paw3;
    private ModelRenderer paw4;



    public ModelSaberToothTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        paw1 = new ModelRenderer(this, 21, 37);
        paw1.setRotationPoint(0.0F, 10.0F, -2.0F);
        paw1.addBox(-2.0F, -1.0F, -1.0F, 3, 2, 1, 0.0F);
        paw4 = new ModelRenderer(this, 21, 37);
        paw4.setRotationPoint(0.5F, 10.0F, -2.0F);
        paw4.addBox(-2.0F, -1.0F, -1.0F, 3, 2, 1, 0.0F);
        tailBottom = new ModelRenderer(this, 44, 7);
        tailBottom.setRotationPoint(-1.0F, 13.3F, 14.0F);
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
        upperLeg4 = new ModelRenderer(this, 1, 38);
        upperLeg4.setRotationPoint(-3.8F, 10.4F, 8.5F);
        upperLeg4.addBox(-2.0F, -4.0F, -2.0F, 4, 8, 4, 0.0F);
        head = new ModelRenderer(this, 1, 4);
        head.setRotationPoint(0.5F, 10.0F, -10.5F);
        head.addBox(-3.0F, -4.0F, -4.5F, 5, 6, 5, 0.1F);
        leg1 = new ModelRenderer(this, 19, 40);
        leg1.mirror = true;
        leg1.setRotationPoint(0.4F, 2.8F, 0.5F);
        leg1.addBox(-2.0F, 0.0F, -2.0F, 3, 11, 3, 0.0F);
        paw3 = new ModelRenderer(this, 21, 37);
        paw3.setRotationPoint(0.0F, 10.0F, -2.0F);
        paw3.addBox(-2.0F, -1.0F, -1.0F, 3, 2, 1, 0.0F);
        upperLeg2 = new ModelRenderer(this, 0, 50);
        upperLeg2.setRotationPoint(-4.0F, 10.2F, -4.6F);
        upperLeg2.addBox(-2.0F, -4.5F, -2.5F, 4, 9, 5, 0.0F);
        tailBody = new ModelRenderer(this, 42, 19);
        tailBody.setRotationPoint(0.0F, 9.2F, 11.5F);
        tailBody.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, -0.2F);
        setRotation(tailBody, 0.5989969992844539F, 0.0F, 0.0F);
        neck = new ModelRenderer(this, 2, 16);
        neck.setRotationPoint(0.0F, 8.6F, -5.0F);
        neck.addBox(-2.0F, -2.5F, -6.5F, 4, 5, 5, 0.0F);
        setRotation(neck, 0.091106186954104F, 0.0F, 0.0F);
        upperLeg3 = new ModelRenderer(this, 1, 38);
        upperLeg3.setRotationPoint(3.8F, 10.4F, 8.5F);
        upperLeg3.addBox(-2.0F, -4.0F, -2.0F, 4, 8, 4, 0.0F);
        upperLeg1 = new ModelRenderer(this, 0, 50);
        upperLeg1.setRotationPoint(4.0F, 10.2F, -4.6F);
        upperLeg1.addBox(-2.0F, -4.5F, -2.5F, 4, 9, 5, 0.0F);
        leg4 = new ModelRenderer(this, 19, 40);
        leg4.setRotationPoint(0.0F, 2.6F, 0.5F);
        leg4.addBox(-1.5F, 0.0F, -2.0F, 3, 11, 3, 0.0F);
        paw2 = new ModelRenderer(this, 21, 37);
        paw2.setRotationPoint(0.0F, 10.0F, -1.899999976158142F);
        paw2.addBox(-2.0F, -1.0F, -1.0F, 3, 2, 1, 0.0F);
        frontBody = new ModelRenderer(this, 28, 43);
        frontBody.setRotationPoint(0.5F, 10.0F, -7.8F);
        frontBody.addBox(-4.0F, -5.0F, 0.0F, 7, 10, 11, 0.0F);
        setRotation(frontBody, -0.07592182246175333F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 16, 3);
        nose.setRotationPoint(-1.5F, -2.299999952316284F, -8.600000381469727F);
        nose.addBox(0.0F, 0.0F, 0.4000000059604645F, 2, 2, 4, 0.0F);
        setRotation(nose, 0.1820378452539444F, 0.0F, 0.0F);

        backBody = new ModelRenderer(this, 32, 25);
        backBody.setRotationPoint(0.0F, 10.4F, 5.8F);
        backBody.addBox(-3.0F, -4.0F, -4.0F, 6, 8, 10, 0.0F);
        setRotation(backBody, -0.028623399732707F, 0.0F, 0.0F);
        leg2 = new ModelRenderer(this, 19, 40);
        leg2.setRotationPoint(0.6F, 2.8F, 0.5F);
        leg2.addBox(-2.0F, 0.0F, -2.0F, 3, 11, 3, 0.0F);
        leg3 = new ModelRenderer(this, 19, 40);
        leg3.mirror = true;
        leg3.setRotationPoint(0.5F, 2.6F, 0.5F);
        leg3.addBox(-2.0F, 0.0F, -2.0F, 3, 11, 3, 0.0F);
        neckBase = new ModelRenderer(this, 0, 26);
        neckBase.setRotationPoint(0.5F, 9.4F, -5.8F);
        neckBase.addBox(-4.0F, -4.0F, -3.0F, 7, 7, 4, -0.1F);
        setRotation(neckBase, 0.136659280431156F, 0.0F, 0.0F);
        mouthBottom = new ModelRenderer(this, 23, 14);
        mouthBottom.setRotationPoint(-2.0F, -0.10000000149011612F, -8.0F);
        mouthBottom.addBox(0.0F, 0.0F, 0.5F, 3, 2, 3, 0.0F);
        tailTop = new ModelRenderer(this, 44, 13);
        tailTop.setRotationPoint(-1.0F, 10.8F, 11.3F);
        tailTop.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
        setRotation(tailTop, 0.8337679215359916F, 0.0F, 0.0F);
        mouthTop = new ModelRenderer(this, 21, 19);
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


        //tooth1a = new ModelRenderer(this, 16, 0);
        //tooth1a.setRotationPoint(0.8F, 10.1F, -10.2F);
        //tooth1a.addBox(0.2F, -0.2F, -7.2F, 1, 1, 1, -0.05F);
        //setRotation(tooth1a, 0.136659280431156F, 0.0F, 0.0F);
        //tooth1b = new ModelRenderer(this, 16, 1);
        //tooth1b.setRotationPoint(0.8F, 10.9F, -10.1F);
        //tooth1b.addBox(0.2F, -0.2F, -7.2F, 1, 1, 1, -0.1F);
        //setRotation(tooth1b, 0.136659280431156F, 0.0F, 0.0F);
        //tooth1c = new ModelRenderer(this, 16, 2);
        //tooth1c.setRotationPoint(0.8F, 11.2F, -10.1F);
        //tooth1c.addBox(0.2F, -0.2F, -7.2F, 1, 1, 1, -0.16F);
        //setRotation(tooth1c, 0.18203784098300857F, 0.0F, 0.0F);
        //tooth1d = new ModelRenderer(this, 16, 3);
        //tooth1d.setRotationPoint(0.8F, 11.7F, -10.0F);
        //tooth1d.addBox(0.2F, -0.2F, -7.2F, 1, 1, 1, -0.25F);
        //setRotation(tooth1d, 0.2F, 0.0F, 0.0F);

        head.addChild(nose);
        head.addChild(ear1);
        head.addChild(ear2);
        head.addChild(mouthBottom);
        head.addChild(mouthTop);
        mouthTop.addChild(tooth1);
        mouthTop.addChild(tooth2);
        upperLeg1.addChild(leg1);
        upperLeg2.addChild(leg2);
        upperLeg3.addChild(leg3);
        upperLeg4.addChild(leg4);
        leg1.addChild(paw1);
        leg2.addChild(paw2);
        leg3.addChild(paw3);
        leg4.addChild(paw4);

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

        head.render(f5);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0D, 1.0D, 1.0D);

        neck.render(f5);
        neckBase.render(f5);
        frontBody.render(f5);
        backBody.render(f5);
        tailBody.render(f5);
        tailTop.render(f5);
        tailBottom.render(f5);
        upperLeg1.render(f5);
        upperLeg2.render(f5);
        upperLeg3.render(f5);
        upperLeg4.render(f5);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        //super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        head.rotateAngleX = f4 / (180F / (float) Math.PI);
        head.rotateAngleY = f3 / (180F / (float) Math.PI);
        //tooth2.rotateAngleX = f4 / (180F / (float) Math.PI);
        //tooth2.rotateAngleY = f3 / (180F / (float) Math.PI);

        upperLeg3.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
        upperLeg4.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
        upperLeg1.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
        upperLeg2.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}