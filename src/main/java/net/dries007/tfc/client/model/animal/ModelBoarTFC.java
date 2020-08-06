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

import net.dries007.tfc.objects.entity.animal.EntityBoarTFC;


/**
 * ModelBoarTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelBoarTFC extends ModelBase
{
    public ModelRenderer legLBackTop;
    public ModelRenderer legLFrontTop;
    public ModelRenderer body;
    public ModelRenderer legRFrontTop;
    public ModelRenderer chest;
    public ModelRenderer neck;
    public ModelRenderer legRBackTop;
    public ModelRenderer legLBackLower;
    public ModelRenderer legLBackBottom;
    public ModelRenderer legLBackHoof;
    public ModelRenderer legLFrontMiddle;
    public ModelRenderer legLFrontBottom;
    public ModelRenderer legLFrontHoof;
    public ModelRenderer hairTop3;
    public ModelRenderer hairBottomFront;
    public ModelRenderer hairBottomLeft;
    public ModelRenderer bodyMid;
    public ModelRenderer hairTop2;
    public ModelRenderer hairBottomRight;
    public ModelRenderer hairBottomBack;
    public ModelRenderer bodyBack;
    public ModelRenderer tailBasea;
    public ModelRenderer tailBody;
    public ModelRenderer tailBaseb;
    public ModelRenderer tailBasec;
    public ModelRenderer tailBased;
    public ModelRenderer tailFluff;
    public ModelRenderer legRFrontMiddle;
    public ModelRenderer legRFrontBottom;
    public ModelRenderer legRFrontHoof;
    public ModelRenderer hairTop1;
    public ModelRenderer head;
    public ModelRenderer mouthMiddle;
    public ModelRenderer earR;
    public ModelRenderer mouthBottom;
    public ModelRenderer earL;
    public ModelRenderer mouthTop;
    public ModelRenderer tuskL1;
    public ModelRenderer tuskR1;
    public ModelRenderer tuskL2;
    public ModelRenderer tuskL3;
    public ModelRenderer tuskR2;
    public ModelRenderer tuskR3;
    public ModelRenderer nose;
    public ModelRenderer legRBackMiddle;
    public ModelRenderer legRBackBottom;
    public ModelRenderer legRBackHoof;

    public ModelBoarTFC()
    {
        textureWidth = 86;
        textureHeight = 64;

        hairBottomLeft = new ModelRenderer(this, 20, 0);
        hairBottomLeft.setRotationPoint(3.4F, 4.5F, 3.0F);
        hairBottomLeft.addBox(0.0F, 0.0F, -7.0F, 0, 4, 14, 0.0F);
        setRotateAngle(hairBottomLeft, 0.13962634015954636F, 0.0F, 0.0F);
        chest = new ModelRenderer(this, 64, 4);
        chest.setRotationPoint(0.0F, 13.6F, -7.8F);
        chest.addBox(-2.5F, -5.4F, -1.0F, 5, 9, 3, 0.0F);
        setRotateAngle(chest, 0.13962634015954636F, 0.0F, 0.0F);
        tailBased = new ModelRenderer(this, 37, 31);
        tailBased.setRotationPoint(0.0F, 0.0F, 0.0F);
        tailBased.addBox(-0.8F, 0.0F, -0.8F, 1, 2, 1, 0.0F);
        tuskL1 = new ModelRenderer(this, 1, 31);
        tuskL1.setRotationPoint(1.3F, -0.8F, -2.8F);
        tuskL1.addBox(-0.4F, -0.8F, -0.5F, 1, 1, 1, 0.0F);
        setRotateAngle(tuskL1, 0.24434609527920614F, 0.0F, 0.6981317007977318F);
        nose = new ModelRenderer(this, 7, 26);
        nose.setRotationPoint(0.0F, -0.4F, -4.3F);
        nose.addBox(-1.5F, -1.5F, -0.8F, 3, 3, 1, 0.0F);
        setRotateAngle(nose, -0.13962634015954636F, 0.0F, 0.0F);
        tuskR3 = new ModelRenderer(this, 1, 27);
        tuskR3.mirror = true;
        tuskR3.setRotationPoint(-0.23F, 0.28F, -0.06F);
        tuskR3.addBox(-0.6F, -1.7F, -0.5F, 1, 1, 1, 0.0F);
        setRotateAngle(tuskR3, -0.03490658503988659F, 0.0F, 0.4363323129985824F);
        neck = new ModelRenderer(this, 3, 54);
        neck.setRotationPoint(0.0F, 11.7F, -8.5F);
        neck.addBox(-2.0F, -3.2F, -3.0F, 4, 6, 4, 0.0F);
        setRotateAngle(neck, -0.136659280431156F, 0.0F, 0.0F);
        hairBottomFront = new ModelRenderer(this, 20, 7);
        hairBottomFront.setRotationPoint(0.0F, 6.5F, -5.5F);
        hairBottomFront.addBox(-3.5F, -1.0F, 0.0F, 7, 3, 0, 0.0F);
        setRotateAngle(hairBottomFront, 0.2181661564992912F, 0.0F, 0.0F);
        legLFrontBottom = new ModelRenderer(this, 25, 38);
        legLFrontBottom.mirror = true;
        legLFrontBottom.setRotationPoint(0.2F, 1.5F, 0.0F);
        legLFrontBottom.addBox(-1.0F, 0.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotateAngle(legLFrontBottom, -0.08726646259971647F, 0.0F, 0.03490658503988659F);
        bodyMid = new ModelRenderer(this, 58, 31);
        bodyMid.setRotationPoint(0.0F, 0.2F, 3.8F);
        bodyMid.addBox(-4.0F, -4.6F, -3.0F, 8, 10, 6, 0.0F);
        setRotateAngle(bodyMid, 0.04363323129985824F, 0.0F, 0.0F);
        tailBasea = new ModelRenderer(this, 37, 31);
        tailBasea.setRotationPoint(0.0F, -2.0F, 5.5F);
        tailBasea.addBox(-0.2F, 0.0F, -0.2F, 1, 2, 1, 0.0F);
        setRotateAngle(tailBasea, 0.8651597102135892F, 0.0F, 0.0F);
        tailBody = new ModelRenderer(this, 37, 26);
        tailBody.setRotationPoint(0.0F, 1.5F, 0.3F);
        tailBody.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(tailBody, -0.6373942428283291F, 0.0F, 0.0F);
        legRBackHoof = new ModelRenderer(this, 42, 33);
        legRBackHoof.setRotationPoint(0.0F, 4.1F, 0.0F);
        legRBackHoof.addBox(-1.5F, 0.0F, -1.8F, 3, 2, 3, 0.0F);
        setRotateAngle(legRBackHoof, -0.13962634015954636F, 0.0F, 0.0F);
        legLFrontMiddle = new ModelRenderer(this, 23, 46);
        legLFrontMiddle.mirror = true;
        legLFrontMiddle.setRotationPoint(1.2F, 4.8F, 0.0F);
        legLFrontMiddle.addBox(-1.5F, -0.5F, -2.0F, 3, 3, 4, 0.0F);
        setRotateAngle(legLFrontMiddle, 0.08726646259971647F, 0.0F, 0.05235987755982988F);
        legRBackBottom = new ModelRenderer(this, 43, 38);
        legRBackBottom.setRotationPoint(-0.2F, 2.4F, 0.0F);
        legRBackBottom.addBox(-1.0F, 0.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotateAngle(legRBackBottom, -0.17453292519943295F, 0.0F, 0.0F);
        mouthBottom = new ModelRenderer(this, 3, 20);
        mouthBottom.setRotationPoint(0.0F, 1.9F, -4.1F);
        mouthBottom.addBox(-1.5F, -0.5F, -4.7F, 3, 1, 5, 0.0F);
        setRotateAngle(mouthBottom, -0.08726646259971647F, 0.0F, 0.0F);
        legLBackHoof = new ModelRenderer(this, 42, 33);
        legLBackHoof.mirror = true;
        legLBackHoof.setRotationPoint(0.0F, 4.1F, 0.0F);
        legLBackHoof.addBox(-1.5F, 0.0F, -1.8F, 3, 2, 3, 0.0F);
        setRotateAngle(legLBackHoof, -0.13962634015954636F, 0.0F, 0.0F);
        tuskR1 = new ModelRenderer(this, 1, 31);
        tuskR1.mirror = true;
        tuskR1.setRotationPoint(-1.3F, -0.8F, -2.8F);
        tuskR1.addBox(-0.5F, -0.8F, -0.5F, 1, 1, 1, 0.0F);
        setRotateAngle(tuskR1, 0.24434609527920614F, 0.0F, -0.6981317007977318F);
        tuskL3 = new ModelRenderer(this, 1, 27);
        tuskL3.setRotationPoint(0.5F, 0.18F, -0.02F);
        tuskL3.addBox(-0.6F, -1.7F, -0.5F, 1, 1, 1, 0.0F);
        setRotateAngle(tuskL3, -0.03490658503988659F, 0.0F, -0.4363323129985824F);
        hairTop1 = new ModelRenderer(this, 3, 0);
        hairTop1.setRotationPoint(0.0F, -5.4F, -0.7F);
        hairTop1.addBox(-2.5F, 0.0F, 0.0F, 5, 0, 2, 0.0F);
        setRotateAngle(hairTop1, 0.13962634015954636F, 0.0F, 0.0F);
        legLBackBottom = new ModelRenderer(this, 43, 38);
        legLBackBottom.mirror = true;
        legLBackBottom.setRotationPoint(0.2F, 2.4F, 0.0F);
        legLBackBottom.addBox(-1.0F, 0.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotateAngle(legLBackBottom, -0.17453292519943295F, 0.0F, 0.0F);
        legRBackTop = new ModelRenderer(this, 39, 53);
        legRBackTop.setRotationPoint(-1.0F, 12.0F, 7.0F);
        legRBackTop.addBox(-3.5F, -1.0F, -2.5F, 4, 6, 5, 0.0F);
        setRotateAngle(legRBackTop, 0.0F, 0.0F, 0.08726646259971647F);
        hairBottomRight = new ModelRenderer(this, 20, 0);
        hairBottomRight.setRotationPoint(-3.4F, 4.5F, 3.0F);
        hairBottomRight.addBox(0.0F, 0.0F, -7.0F, 0, 4, 14, 0.0F);
        setRotateAngle(hairBottomRight, 0.13962634015954636F, 0.0F, 0.0F);
        hairTop3 = new ModelRenderer(this, 0, 5);
        hairTop3.setRotationPoint(0.0F, -4.25F, -4.0F);
        hairTop3.addBox(-3.5F, 0.0F, 0.0F, 7, 0, 3, 0.0F);
        setRotateAngle(hairTop3, 0.08726646259971647F, 0.0F, 0.0F);
        legLFrontTop = new ModelRenderer(this, 22, 53);
        legLFrontTop.mirror = true;
        legLFrontTop.setRotationPoint(1.8F, 11.5F, -4.5F);
        legLFrontTop.addBox(0.0F, -1.0F, -2.5F, 3, 6, 5, 0.0F);
        setRotateAngle(legLFrontTop, 0.0F, 0.0F, -0.08726646259971647F);
        legLFrontHoof = new ModelRenderer(this, 24, 33);
        legLFrontHoof.mirror = true;
        legLFrontHoof.setRotationPoint(-0.5F, 4.4F, -0.1F);
        legLFrontHoof.addBox(-1.0F, 0.0F, -1.8F, 3, 2, 3, 0.0F);
        setRotateAngle(legLFrontHoof, 0.017453292519943295F, 0.0F, 0.0F);
        earR = new ModelRenderer(this, 17, 29);
        earR.mirror = true;
        earR.setRotationPoint(-1.2F, -2.7F, -2.0F);
        earR.addBox(-2.2F, -3.0F, -0.5F, 3, 3, 1, 0.0F);
        setRotateAngle(earR, -0.22689280275926282F, 0.7853981633974483F, -0.7853981633974483F);
        legRFrontHoof = new ModelRenderer(this, 24, 33);
        legRFrontHoof.setRotationPoint(0.0F, 4.6F, -0.1F);
        legRFrontHoof.addBox(-1.5F, 0.0F, -1.8F, 3, 2, 3, 0.0F);
        legLBackTop = new ModelRenderer(this, 39, 53);
        legLBackTop.mirror = true;
        legLBackTop.setRotationPoint(1.0F, 12.0F, 7.0F);
        legLBackTop.addBox(-0.5F, -1.0F, -2.5F, 4, 6, 5, 0.0F);
        setRotateAngle(legLBackTop, 0.0F, 0.0F, -0.08726646259971647F);
        tuskR2 = new ModelRenderer(this, 1, 29);
        tuskR2.mirror = true;
        tuskR2.setRotationPoint(0.0F, -1.0F, 0.03F);
        tuskR2.addBox(-0.45F, -0.65F, -0.5F, 1, 1, 1, 0.0F);
        setRotateAngle(tuskR2, -0.08726646259971647F, 0.0F, 0.2617993877991494F);
        mouthMiddle = new ModelRenderer(this, 4, 30);
        mouthMiddle.setRotationPoint(0.0F, 0.7F, -4.4F);
        mouthMiddle.addBox(-1.5F, -1.0F, -4.0F, 3, 2, 4, 0.0F);
        legRBackMiddle = new ModelRenderer(this, 41, 46);
        legRBackMiddle.setRotationPoint(-1.7F, 3.9F, -0.1F);
        legRBackMiddle.addBox(-1.5F, 0.0F, -2.0F, 3, 3, 4, 0.0F);
        setRotateAngle(legRBackMiddle, 0.3141592653589793F, 0.0F, -0.08726646259971647F);
        tailFluff = new ModelRenderer(this, 35, 20);
        tailFluff.setRotationPoint(0.0F, 3.8F, 0.0F);
        tailFluff.addBox(-1.1F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        hairTop2 = new ModelRenderer(this, 0, 2);
        hairTop2.setRotationPoint(0.0F, -4.3F, -5.9F);
        hairTop2.addBox(-3.5F, 0.0F, 0.0F, 7, 0, 3, 0.0F);
        setRotateAngle(hairTop2, 0.13962634015954636F, 0.0F, 0.0F);
        tailBasec = new ModelRenderer(this, 37, 31);
        tailBasec.setRotationPoint(0.0F, 0.0F, 0.0F);
        tailBasec.addBox(-0.8F, 0.0F, -0.2F, 1, 2, 1, 0.0F);
        tuskL2 = new ModelRenderer(this, 1, 29);
        tuskL2.setRotationPoint(0.0F, -1.0F, 0.03F);
        tuskL2.addBox(-0.45F, -0.65F, -0.5F, 1, 1, 1, 0.0F);
        setRotateAngle(tuskL2, -0.08726646259971647F, 0.0F, -0.2617993877991494F);
        hairBottomBack = new ModelRenderer(this, 20, 10);
        hairBottomBack.setRotationPoint(0.0F, 4.0F, 11.5F);
        hairBottomBack.addBox(-3.5F, 0.0F, 0.0F, 7, 4, 0, 0.0F);
        setRotateAngle(hairBottomBack, 0.13962634015954636F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 1, 43);
        head.setRotationPoint(0.0F, -0.3F, -1.2F);
        head.addBox(-2.5F, -3.5F, -5.0F, 5, 6, 5, 0.0F);
        setRotateAngle(head, 0.3141592653589793F, 0.0F, 0.0F);
        body = new ModelRenderer(this, 58, 47);
        body.setRotationPoint(0.0F, 12.5F, -1.5F);
        body.addBox(-3.5F, -4.3F, -6.0F, 7, 10, 7, 0.0F);
        setRotateAngle(body, -0.06981317007977318F, 0.0F, 0.0F);
        bodyBack = new ModelRenderer(this, 59, 16);
        bodyBack.setRotationPoint(0.0F, -0.9F, 2.5F);
        bodyBack.addBox(-3.5F, -3.5F, 0.0F, 7, 9, 6, 0.0F);
        setRotateAngle(bodyBack, -0.12217304763960307F, 0.0F, 0.0F);
        legRFrontTop = new ModelRenderer(this, 22, 53);
        legRFrontTop.setRotationPoint(-1.8F, 11.5F, -4.5F);
        legRFrontTop.addBox(-3.0F, -1.0F, -2.5F, 3, 6, 5, 0.0F);
        setRotateAngle(legRFrontTop, 0.0F, 0.0F, 0.08726646259971647F);
        legLBackLower = new ModelRenderer(this, 41, 46);
        legLBackLower.mirror = true;
        legLBackLower.setRotationPoint(1.7F, 3.9F, -0.1F);
        legLBackLower.addBox(-1.5F, 0.0F, -2.0F, 3, 3, 4, 0.0F);
        setRotateAngle(legLBackLower, 0.3141592653589793F, 0.0F, 0.08726646259971647F);
        legRFrontBottom = new ModelRenderer(this, 25, 38);
        legRFrontBottom.setRotationPoint(-0.2F, 1.5F, 0.0F);
        legRFrontBottom.addBox(-1.0F, 0.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotateAngle(legRFrontBottom, -0.08726646259971647F, 0.0F, -0.03490658503988659F);
        tailBaseb = new ModelRenderer(this, 37, 31);
        tailBaseb.setRotationPoint(0.0F, 0.0F, 0.0F);
        tailBaseb.addBox(-0.2F, 0.0F, -0.8F, 1, 2, 1, 0.0F);
        legRFrontMiddle = new ModelRenderer(this, 23, 46);
        legRFrontMiddle.setRotationPoint(-1.2F, 4.6F, 0.0F);
        legRFrontMiddle.addBox(-1.5F, 0.0F, -2.0F, 3, 3, 4, 0.0F);
        setRotateAngle(legRFrontMiddle, 0.08726646259971647F, 0.0F, -0.05235987755982988F);
        earL = new ModelRenderer(this, 17, 29);
        earL.setRotationPoint(1.2F, -2.7F, -2.0F);
        earL.addBox(-0.8F, -3.0F, -0.5F, 3, 3, 1, 0.0F);
        setRotateAngle(earL, -0.22689280275926282F, -0.7853981633974483F, 0.7853981633974483F);
        mouthTop = new ModelRenderer(this, 2, 36);
        mouthTop.setRotationPoint(0.0F, -0.9F, -4.4F);
        mouthTop.addBox(-2.0F, -1.5F, -4.7F, 4, 2, 5, 0.0F);
        setRotateAngle(mouthTop, 0.2617993877991494F, 0.0F, 0.0F);

        body.addChild(hairBottomLeft);
        tailBasea.addChild(tailBased);
        mouthBottom.addChild(tuskL1);
        mouthTop.addChild(nose);
        tuskR2.addChild(tuskR3);
        body.addChild(hairBottomFront);
        legLFrontMiddle.addChild(legLFrontBottom);
        body.addChild(bodyMid);
        bodyBack.addChild(tailBasea);
        tailBasea.addChild(tailBody);
        legRBackBottom.addChild(legRBackHoof);
        legLFrontTop.addChild(legLFrontMiddle);
        legRBackMiddle.addChild(legRBackBottom);
        head.addChild(mouthBottom);
        legLBackBottom.addChild(legLBackHoof);
        mouthBottom.addChild(tuskR1);
        tuskL2.addChild(tuskL3);
        chest.addChild(hairTop1);
        legLBackLower.addChild(legLBackBottom);
        body.addChild(hairBottomRight);
        body.addChild(hairTop3);
        legLFrontBottom.addChild(legLFrontHoof);
        head.addChild(earR);
        legRFrontBottom.addChild(legRFrontHoof);
        tuskR1.addChild(tuskR2);
        head.addChild(mouthMiddle);
        legRBackTop.addChild(legRBackMiddle);
        tailBody.addChild(tailFluff);
        body.addChild(hairTop2);
        tailBasea.addChild(tailBasec);
        tuskL1.addChild(tuskL2);
        body.addChild(hairBottomBack);
        neck.addChild(head);
        bodyMid.addChild(bodyBack);
        legLBackTop.addChild(legLBackLower);
        legRFrontMiddle.addChild(legRFrontBottom);
        tailBasea.addChild(tailBaseb);
        legRFrontTop.addChild(legRFrontMiddle);
        head.addChild(earL);
        head.addChild(mouthTop);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        EntityBoarTFC hog = ((EntityBoarTFC) entity);

        float percent = (float) hog.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        if (!hog.isChild())
        {
            tuskL1.isHidden = true;
            tuskR1.isHidden = true;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        chest.render(par7);
        neck.render(par7);
        legRBackTop.render(par7);
        legLFrontTop.render(par7);
        legLBackTop.render(par7);
        body.render(par7);
        legRFrontTop.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        tuskR1.isHidden = true;
        tuskL1.isHidden = true;

        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.legRFrontTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legLFrontTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legRBackTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legLBackTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}