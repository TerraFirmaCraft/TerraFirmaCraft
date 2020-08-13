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

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityZebuTFC;

/**
 * ModelZebuTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelZebuTFC extends ModelBase
{
    public ModelRenderer maleNeck;
    public ModelRenderer headNode;
    public ModelRenderer legRFrontTop;
    public ModelRenderer humpMain;
    public ModelRenderer legLBackTop;
    public ModelRenderer legLFrontTop;
    public ModelRenderer legRBackTop;
    public ModelRenderer chest;
    public ModelRenderer body;
    public ModelRenderer rump;
    public ModelRenderer chestHump;
    public ModelRenderer legRFrontMiddle;
    public ModelRenderer lefRFrontBottom;
    public ModelRenderer legRFrontHoof;
    public ModelRenderer humpBack;
    public ModelRenderer legLBackMiddle;
    public ModelRenderer legLBackBottom;
    public ModelRenderer legLBackHoof;
    public ModelRenderer legLFrontMiddle;
    public ModelRenderer legLFrontBottom;
    public ModelRenderer legLFrontHoof;
    public ModelRenderer legRBackMiddle;
    public ModelRenderer legRBackBottom;
    public ModelRenderer legRBackHoof;
    public ModelRenderer udders;
    public ModelRenderer tailBase;
    public ModelRenderer teat1;
    public ModelRenderer teat3;
    public ModelRenderer teat2;
    public ModelRenderer teat4;
    public ModelRenderer tailBody;
    public ModelRenderer tailTip;
    public ModelRenderer neck;
    public ModelRenderer neckBase;
    public ModelRenderer head;
    public ModelRenderer nose;
    public ModelRenderer mouthTop;
    public ModelRenderer earL;
    public ModelRenderer earR;
    public ModelRenderer mouthBottom;
    public ModelRenderer hornML1;
    public ModelRenderer hornMR1;
    public ModelRenderer hornML2;
    public ModelRenderer hornML3a;
    public ModelRenderer hornML3b;
    public ModelRenderer hornML3c;
    public ModelRenderer hornML3d;
    public ModelRenderer hornML4;
    public ModelRenderer hornMR2;
    public ModelRenderer hornMR3a;
    public ModelRenderer hornMR3b;
    public ModelRenderer hornMR3c;
    public ModelRenderer hornMR3d;
    public ModelRenderer hornMR4;
    public ModelRenderer hornRightF1A;
    public ModelRenderer hornRightF1B;
    public ModelRenderer hornRightF1C;
    public ModelRenderer hornRightF1D;
    public ModelRenderer hornRightF2;
    public ModelRenderer hornLeftF1A;
    public ModelRenderer hornLeftF1B;
    public ModelRenderer hornLeftF1C;
    public ModelRenderer hornLeftF1D;
    public ModelRenderer hornLeftF2;

    public ModelZebuTFC()
    {
        textureWidth = 128;
        textureHeight = 128;

        headNode = new ModelRenderer(this, 0, 0);
        headNode.setRotationPoint(0.0F, -0.5F, -10.5F);
        headNode.addBox(-0.5F, -0.5F, -1.0F, 1, 1, 1, 0.0F);
        maleNeck = new ModelRenderer(this, 100, 5);
        maleNeck.setRotationPoint(0.0F, 2.2F, -0.8F);
        maleNeck.addBox(-1.0F, -3.0F, -1.5F, 2, 10, 3, 0.0F);
        setRotateAngle(maleNeck, 1.186823891356144F, 0.0F, 0.0F);
        legLFrontTop = new ModelRenderer(this, 10, 114);
        legLFrontTop.setRotationPoint(4.8F, 4.0F, -4.0F);
        legLFrontTop.addBox(-0.5F, -1.0F, -3.0F, 3, 8, 5, 0.0F);
        setRotateAngle(legLFrontTop, 0.017453292519943295F, 0.0F, -0.08726646259971647F);
        udders = new ModelRenderer(this, 12, 70);
        udders.setRotationPoint(0.0F, 6.2F, -7.5F);
        udders.addBox(-3.5F, -1.5F, -4.0F, 7, 3, 8, 0.0F);
        setRotateAngle(udders, 0.08726646259971647F, 0.0F, 0.0F);
        hornMR1 = new ModelRenderer(this, 0, 60);
        hornMR1.setRotationPoint(-5.0F, -2.5F, -4.5F);
        hornMR1.addBox(-0.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
        setRotateAngle(hornMR1, 0.0F, -0.5235987755982988F, 0.0F);
        legLBackTop = new ModelRenderer(this, 30, 114);
        legLBackTop.setRotationPoint(5.0F, 3.5F, 8.6F);
        legLBackTop.addBox(-0.7F, -1.5F, -3.0F, 3, 9, 5, 0.0F);
        setRotateAngle(legLBackTop, 0.0F, 0.0F, -0.08726646259971647F);
        legRFrontMiddle = new ModelRenderer(this, 11, 105);
        legRFrontMiddle.mirror = true;
        legRFrontMiddle.setRotationPoint(-1.1F, 6.7F, 0.1F);
        legRFrontMiddle.addBox(-1.1F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legRFrontMiddle, 0.08726646259971647F, 0.0F, -0.03490658503988659F);
        tailBase = new ModelRenderer(this, 60, 88);
        tailBase.setRotationPoint(0.0F, -4.8F, -4.0F);
        tailBase.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        setRotateAngle(tailBase, 0.4553564018453205F, 0.0F, 0.0F);
        mouthBottom = new ModelRenderer(this, 8, 4);
        mouthBottom.setRotationPoint(0.0F, 0.9F, 1.8F);
        mouthBottom.addBox(-2.0F, 0.0F, -1.4F, 4, 5, 1, 0.0F);
        setRotateAngle(mouthBottom, -0.091106186954104F, 0.0F, 0.0F);
        nose = new ModelRenderer(this, 6, 17);
        nose.setRotationPoint(0.0F, 3.6F, -2.8F);
        nose.addBox(-2.0F, -2.5F, -0.7F, 4, 5, 3, 0.0F);
        setRotateAngle(nose, 0.36425021489121656F, 0.0F, 0.0F);
        hornML3a = new ModelRenderer(this, 2, 52);
        hornML3a.mirror = true;
        hornML3a.setRotationPoint(1.76F, -0.2F, -0.24F);
        hornML3a.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3a, 0.0F, 0.3490658503988659F, 0.0F);
        hornMR3d = new ModelRenderer(this, 2, 52);
        hornMR3d.setRotationPoint(-1.32F, 0.2F, -0.1F);
        hornMR3d.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3d, 0.0F, -0.3490658503988659F, 0.0F);
        hornML3d = new ModelRenderer(this, 2, 52);
        hornML3d.mirror = true;
        hornML3d.setRotationPoint(1.91F, 0.2F, 0.2F);
        hornML3d.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3d, 0.0F, 0.3490658503988659F, 0.0F);
        hornMR4 = new ModelRenderer(this, 2, 48);
        hornMR4.setRotationPoint(-1.4F, -0.2F, -0.4F);
        hornMR4.addBox(-0.4F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR4, 0.0F, -0.2617993877991494F, 0.0F);
        legLFrontBottom = new ModelRenderer(this, 12, 95);
        legLFrontBottom.setRotationPoint(-0.6F, 4.5F, -0.4F);
        legLFrontBottom.addBox(-1.5F, 0.0F, -1.5F, 3, 7, 3, 0.0F);
        setRotateAngle(legLFrontBottom, -0.10471975511965977F, 0.0F, 0.05235987755982988F);
        earL = new ModelRenderer(this, 46, 34);
        earL.setRotationPoint(3.0F, -2.0F, -2.2F);
        earL.addBox(0.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F);
        setRotateAngle(earL, 0.4363323129985824F, -0.08726646259971647F, 0.08726646259971647F);
        legLBackMiddle = new ModelRenderer(this, 31, 105);
        legLBackMiddle.setRotationPoint(1.1F, 6.5F, -0.3F);
        legLBackMiddle.addBox(-2.0F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legLBackMiddle, 0.3141592653589793F, 0.0F, 0.08726646259971647F);
        legRFrontHoof = new ModelRenderer(this, 10, 88);
        legRFrontHoof.mirror = true;
        legRFrontHoof.setRotationPoint(0.0F, 5.9F, -0.1F);
        legRFrontHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        neckBase = new ModelRenderer(this, 95, 30);
        neckBase.setRotationPoint(0.0F, -2.5F, -2.8F);
        neckBase.addBox(-3.5F, -3.0F, -1.5F, 7, 7, 3, 0.0F);
        setRotateAngle(neckBase, -0.3490658503988659F, 0.0F, 0.0F);
        hornML1 = new ModelRenderer(this, 0, 60);
        hornML1.mirror = true;
        hornML1.setRotationPoint(3.0F, -2.5F, -3.5F);
        hornML1.addBox(-0.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
        setRotateAngle(hornML1, 0.0F, 0.5235987755982988F, 0.0F);
        hornMR3c = new ModelRenderer(this, 2, 52);
        hornMR3c.setRotationPoint(-1.21F, 0.2F, -0.4F);
        hornMR3c.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3c, 0.0F, -0.3490658503988659F, 0.0F);
        body = new ModelRenderer(this, 82, 86);
        body.setRotationPoint(0.0F, 9.0F, 1.5F);
        body.addBox(-6.0F, -8.1F, -4.0F, 12, 12, 11, 0.2F);
        setRotateAngle(body, 0.045553093477052F, 0.0F, 0.0F);
        hornMR2 = new ModelRenderer(this, 1, 55);
        hornMR2.setRotationPoint(-1.1F, 0.0F, -0.8F);
        hornMR2.addBox(-0.4F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotateAngle(hornMR2, 0.0F, -0.6981317007977318F, 0.0F);
        legLBackHoof = new ModelRenderer(this, 30, 87);
        legLBackHoof.setRotationPoint(0.0F, 6.4F, -0.1F);
        legLBackHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        setRotateAngle(legLBackHoof, -0.13962634015954636F, 0.0F, 0.0F);
        teat3 = new ModelRenderer(this, 25, 66);
        teat3.setRotationPoint(2.0F, 2.0F, 1.4F);
        teat3.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        legRBackTop = new ModelRenderer(this, 30, 114);
        legRBackTop.mirror = true;
        legRBackTop.setRotationPoint(-3.9F, 3.5F, 8.0F);
        legRBackTop.addBox(-3.3F, -1.5F, -3.0F, 3, 9, 5, 0.0F);
        setRotateAngle(legRBackTop, 0.0F, 0.0F, 0.08726646259971647F);
        teat4 = new ModelRenderer(this, 25, 66);
        teat4.mirror = true;
        teat4.setRotationPoint(-2.0F, 2.0F, 1.4F);
        teat4.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        tailBody = new ModelRenderer(this, 62, 78);
        tailBody.setRotationPoint(0.0F, 3.7F, 0.0F);
        tailBody.addBox(-0.5F, 0.0F, -0.5F, 1, 8, 1, 0.0F);
        setRotateAngle(tailBody, -0.27314402793711257F, 0.0F, 0.0F);
        teat1 = new ModelRenderer(this, 25, 66);
        teat1.setRotationPoint(2.0F, 2.0F, -1.7F);
        teat1.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        neck = new ModelRenderer(this, 93, 18);
        neck.setRotationPoint(0.0F, -1.5F, -3.0F);
        neck.addBox(-3.0F, -3.5F, -5.0F, 6, 6, 6, 0.0F);
        setRotateAngle(neck, -0.7475245186291712F, 0.0F, 0.0F);
        mouthTop = new ModelRenderer(this, 6, 10);
        mouthTop.mirror = true;
        mouthTop.setRotationPoint(0.0F, 2.3F, -0.1F);
        mouthTop.addBox(-2.5F, -1.3F, -1.5F, 5, 5, 2, 0.0F);
        chestHump = new ModelRenderer(this, 91, 45);
        chestHump.setRotationPoint(0.0F, 6.9F, -6.5F);
        chestHump.addBox(-4.0F, -5.9F, -3.0F, 8, 14, 6, 0.0F);
        setRotateAngle(chestHump, -0.2617993877991494F, 0.0F, 0.0F);
        lefRFrontBottom = new ModelRenderer(this, 12, 95);
        lefRFrontBottom.mirror = true;
        lefRFrontBottom.setRotationPoint(0.6F, 4.5F, -0.4F);
        lefRFrontBottom.addBox(-1.5F, 0.0F, -1.5F, 3, 7, 3, 0.0F);
        setRotateAngle(lefRFrontBottom, -0.10471975511965977F, 0.0F, -0.05235987755982988F);
        hornML2 = new ModelRenderer(this, 1, 55);
        hornML2.mirror = true;
        hornML2.setRotationPoint(2.2F, 0.0F, 0.0F);
        hornML2.addBox(-0.4F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotateAngle(hornML2, 0.0F, 0.6981317007977318F, 0.0F);
        legRFrontTop = new ModelRenderer(this, 10, 114);
        legRFrontTop.mirror = true;
        legRFrontTop.setRotationPoint(-4.8F, 4.0F, -4.0F);
        legRFrontTop.addBox(-2.5F, -1.0F, -3.0F, 3, 8, 5, 0.0F);
        setRotateAngle(legRFrontTop, 0.017453292519943295F, 0.0F, 0.08726646259971647F);
        hornML4 = new ModelRenderer(this, 2, 48);
        hornML4.mirror = true;
        hornML4.setRotationPoint(1.2F, -0.15F, -0.15F);
        hornML4.addBox(-0.4F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML4, 0.0F, 0.2617993877991494F, 0.0F);
        tailTip = new ModelRenderer(this, 60, 71);
        tailTip.setRotationPoint(0.0F, 8.0F, 0.0F);
        tailTip.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F);
        setRotateAngle(tailTip, -0.045553093477052F, 0.0F, 0.0F);
        humpMain = new ModelRenderer(this, 40, 20);
        humpMain.setRotationPoint(0.0F, 1.2F, -0.6F);
        humpMain.addBox(-4.0F, -2.5F, -3.0F, 8, 3, 5, 0.2F);
        setRotateAngle(humpMain, 0.17453292519943295F, 0.0F, 0.0F);
        teat2 = new ModelRenderer(this, 25, 66);
        teat2.mirror = true;
        teat2.setRotationPoint(-2.0F, 2.0F, -1.7F);
        teat2.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1, 0.0F);
        chest = new ModelRenderer(this, 84, 65);
        chest.setRotationPoint(0.0F, 5.0F, -1.0F);
        chest.addBox(-6.5F, -4.5F, -7.5F, 13, 14, 7, -0.2F);
        setRotateAngle(chest, -0.045553093477052F, 0.0F, 0.0F);
        hornML3b = new ModelRenderer(this, 2, 52);
        hornML3b.mirror = true;
        hornML3b.setRotationPoint(1.92F, -0.2F, 0.2F);
        hornML3b.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3b, 0.0F, 0.3490658503988659F, 0.0F);
        hornMR3b = new ModelRenderer(this, 2, 52);
        hornMR3b.setRotationPoint(-1.32F, -0.2F, -0.1F);
        hornMR3b.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3b, 0.0F, -0.3490658503988659F, 0.0F);
        legRBackMiddle = new ModelRenderer(this, 31, 105);
        legRBackMiddle.mirror = true;
        legRBackMiddle.setRotationPoint(-1.1F, 6.5F, -0.3F);
        legRBackMiddle.addBox(-2.0F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legRBackMiddle, 0.3141592653589793F, 0.0F, -0.08726646259971647F);
        rump = new ModelRenderer(this, 86, 109);
        rump.setRotationPoint(0.0F, 7.0F, 15.0F);
        rump.addBox(-6.5F, -6.0F, -9.0F, 13, 13, 6, 0.0F);
        setRotateAngle(rump, -0.08726646259971647F, 0.0F, 0.0F);
        earR = new ModelRenderer(this, 46, 34);
        earR.mirror = true;
        earR.setRotationPoint(-2.0F, -2.0F, -2.2F);
        earR.addBox(-5.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F);
        setRotateAngle(earR, 0.4363323129985824F, 0.08726646259971647F, -0.08726646259971647F);
        humpBack = new ModelRenderer(this, 44, 13);
        humpBack.setRotationPoint(0.0F, -0.2F, -2.8F);
        humpBack.addBox(-3.0F, -2.0F, -2.5F, 6, 2, 4, 0.0F);
        setRotateAngle(humpBack, 0.17453292519943295F, 0.0F, 0.0F);
        legLFrontHoof = new ModelRenderer(this, 10, 88);
        legLFrontHoof.setRotationPoint(0.0F, 5.9F, -0.1F);
        legLFrontHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        head = new ModelRenderer(this, 0, 25);
        head.setRotationPoint(0.0F, 1.0F, -2.0F);
        head.addBox(-3.5F, -4.5F, -4.5F, 7, 7, 6, 0.0F);
        setRotateAngle(head, -1.1344640137963142F, 0.0F, 0.0F);
        legLFrontMiddle = new ModelRenderer(this, 11, 105);
        legLFrontMiddle.setRotationPoint(1.3F, 6.7F, 0.1F);
        legLFrontMiddle.addBox(-2.1F, 0.0F, -2.5F, 3, 5, 4, 0.0F);
        setRotateAngle(legLFrontMiddle, 0.08726646259971647F, 0.0F, 0.03490658503988659F);
        legRBackHoof = new ModelRenderer(this, 30, 87);
        legRBackHoof.mirror = true;
        legRBackHoof.setRotationPoint(0.0F, 6.4F, -0.1F);
        legRBackHoof.addBox(-2.0F, 0.0F, -2.5F, 4, 3, 4, 0.0F);
        setRotateAngle(legRBackHoof, -0.13962634015954636F, 0.0F, 0.0F);
        legRBackBottom = new ModelRenderer(this, 32, 94);
        legRBackBottom.mirror = true;
        legRBackBottom.setRotationPoint(-0.4F, 4.9F, -0.4F);
        legRBackBottom.addBox(-1.5F, -0.4F, -1.5F, 3, 8, 3, 0.0F);
        setRotateAngle(legRBackBottom, -0.17453292519943295F, 0.0F, 0.0F);
        hornML3c = new ModelRenderer(this, 2, 52);
        hornML3c.mirror = true;
        hornML3c.setRotationPoint(1.76F, 0.2F, -0.23F);
        hornML3c.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornML3c, 0.0F, 0.3490658503988659F, 0.0F);
        legLBackBottom = new ModelRenderer(this, 32, 94);
        legLBackBottom.setRotationPoint(-0.6F, 4.9F, -0.4F);
        legLBackBottom.addBox(-1.5F, -0.4F, -1.5F, 3, 8, 3, 0.0F);
        setRotateAngle(legLBackBottom, -0.17453292519943295F, 0.0F, 0.0F);
        hornMR3a = new ModelRenderer(this, 2, 52);
        hornMR3a.setRotationPoint(-1.21F, -0.2F, -0.4F);
        hornMR3a.addBox(-0.5F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornMR3a, 0.0F, -0.3490658503988659F, 0.0F);

        hornRightF1A = new ModelRenderer(this, 1, 45);
        hornRightF1A.setRotationPoint(-3.5F, -2.2F, -2.8F);
        hornRightF1A.addBox(-1.5F, -0.8F, -0.8F, 3, 1, 1, 0.0F);
        setRotateAngle(hornRightF1A, 1.5707963267948966F, -0.3490658503988659F, 0.17453292519943295F);
        hornRightF1B = new ModelRenderer(this, 1, 45);
        hornRightF1B.setRotationPoint(0.0F, -0.3F, 0.3F);
        hornRightF1B.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornRightF1C = new ModelRenderer(this, 1, 45);
        hornRightF1C.setRotationPoint(0.0F, 0.3F, -0.3F);
        hornRightF1C.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornRightF1D = new ModelRenderer(this, 1, 45);
        hornRightF1D.setRotationPoint(0.0F, 0.3F, 0.3F);
        hornRightF1D.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornRightF2 = new ModelRenderer(this, 1, 42);
        hornRightF2.setRotationPoint(-2.0F, -0.4F, 0.0F);
        hornRightF2.addBox(-2.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornRightF2, 0.2617993877991494F, 0.0F, 0.6108652381980153F);

        hornLeftF1A = new ModelRenderer(this, 1, 45);
        hornLeftF1A.mirror = true;
        hornLeftF1A.setRotationPoint(3.5F, -2.2F, -2.8F);
        hornLeftF1A.addBox(-1.5F, -0.8F, -0.8F, 3, 1, 1, 0.0F);
        setRotateAngle(hornLeftF1A, 1.5707963267948966F, 0.3490658503988659F, -0.17453292519943295F);
        hornLeftF1B = new ModelRenderer(this, 1, 45);
        hornLeftF1B.mirror = true;
        hornLeftF1B.setRotationPoint(-1.5F, -0.3F, 0.3F);
        hornLeftF1B.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornLeftF1C = new ModelRenderer(this, 1, 45);
        hornLeftF1C.mirror = true;
        hornLeftF1C.setRotationPoint(-1.5F, 0.3F, -0.3F);
        hornLeftF1C.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornLeftF1D = new ModelRenderer(this, 1, 45);
        hornLeftF1D.mirror = true;
        hornLeftF1D.setRotationPoint(-1.5F, 0.3F, 0.3F);
        hornLeftF1D.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornLeftF2 = new ModelRenderer(this, 1, 42);
        hornLeftF2.mirror = true;
        hornLeftF2.setRotationPoint(2.0F, -0.4F, 0.0F);
        hornLeftF2.addBox(-1.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornLeftF2, 0.2617993877991494F, 0.0F, -0.6108652381980153F);

        headNode.addChild(head);
        neck.addChild(maleNeck);
        rump.addChild(udders);
        head.addChild(hornMR1);
        legRFrontTop.addChild(legRFrontMiddle);
        rump.addChild(tailBase);
        head.addChild(mouthBottom);
        head.addChild(nose);
        hornML2.addChild(hornML3a);
        hornMR2.addChild(hornMR3d);
        hornML2.addChild(hornML3d);
        hornMR3d.addChild(hornMR4);
        legLFrontMiddle.addChild(legLFrontBottom);
        head.addChild(earL);
        legLBackTop.addChild(legLBackMiddle);
        lefRFrontBottom.addChild(legRFrontHoof);
        chestHump.addChild(neckBase);
        head.addChild(hornML1);
        hornMR2.addChild(hornMR3c);
        hornMR1.addChild(hornMR2);
        legLBackBottom.addChild(legLBackHoof);
        udders.addChild(teat3);
        udders.addChild(teat4);
        tailBase.addChild(tailBody);
        udders.addChild(teat1);
        chestHump.addChild(neck);
        head.addChild(mouthTop);
        legRFrontMiddle.addChild(lefRFrontBottom);
        hornML1.addChild(hornML2);
        hornML3d.addChild(hornML4);
        tailBody.addChild(tailTip);
        udders.addChild(teat2);
        hornML2.addChild(hornML3b);
        hornMR2.addChild(hornMR3b);
        legRBackTop.addChild(legRBackMiddle);
        head.addChild(earR);
        humpMain.addChild(humpBack);
        legLFrontBottom.addChild(legLFrontHoof);
        //neck.addChild(head);
        legLFrontTop.addChild(legLFrontMiddle);
        legRBackBottom.addChild(legRBackHoof);
        legRBackMiddle.addChild(legRBackBottom);
        hornML2.addChild(hornML3c);
        legLBackMiddle.addChild(legLBackBottom);
        hornMR2.addChild(hornMR3a);

        head.addChild(hornRightF1A);
        hornRightF1A.addChild(hornRightF1B);
        hornRightF1A.addChild(hornRightF1C);
        hornRightF1A.addChild(hornRightF1D);
        hornRightF1A.addChild(hornRightF2);
        head.addChild(hornLeftF1A);
        hornLeftF1A.addChild(hornLeftF1B);
        hornLeftF1A.addChild(hornLeftF1C);
        hornLeftF1A.addChild(hornLeftF1D);
        hornLeftF1A.addChild(hornLeftF2);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityZebuTFC zebu = ((EntityZebuTFC) entity);

        float percent = (float) zebu.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (zebu.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            if (percent < 0.5)
            {
                hornML1.isHidden = true;
                hornML2.isHidden = true;
                hornML3a.isHidden = true;
                hornML3b.isHidden = true;
                hornML3c.isHidden = true;
                hornML3d.isHidden = true;
                hornML4.isHidden = true;
                hornMR1.isHidden = true;
                hornMR2.isHidden = true;
                hornMR3a.isHidden = true;
                hornMR3b.isHidden = true;
                hornMR3c.isHidden = true;
                hornMR3d.isHidden = true;
                hornMR4.isHidden = true;
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
                udders.isHidden = true;
                maleNeck.isHidden = true;
            }
            else if (percent < 0.75)
            {
                hornML3a.isHidden = true;
                hornML3b.isHidden = true;
                hornML3c.isHidden = true;
                hornML3d.isHidden = true;
                hornML4.isHidden = true;
                hornMR3a.isHidden = true;
                hornMR3b.isHidden = true;
                hornMR3c.isHidden = true;
                hornMR3d.isHidden = true;
                hornMR4.isHidden = true;
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
                udders.isHidden = true;
            }
            else
            {
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
                udders.isHidden = true;
            }
        }

        else
        {
            if (percent < 0.5)
            {
                hornML1.isHidden = true;
                hornML2.isHidden = true;
                hornML3a.isHidden = true;
                hornML3b.isHidden = true;
                hornML3c.isHidden = true;
                hornML3d.isHidden = true;
                hornML4.isHidden = true;
                hornMR1.isHidden = true;
                hornMR2.isHidden = true;
                hornMR3a.isHidden = true;
                hornMR3b.isHidden = true;
                hornMR3c.isHidden = true;
                hornMR3d.isHidden = true;
                hornMR4.isHidden = true;
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
                udders.isHidden = true;
                maleNeck.isHidden = true;
            }
            else if (percent < 0.75)
            {
                hornML1.isHidden = true;
                hornML2.isHidden = true;
                hornML3a.isHidden = true;
                hornML3b.isHidden = true;
                hornML3c.isHidden = true;
                hornML3d.isHidden = true;
                hornML4.isHidden = true;
                hornMR1.isHidden = true;
                hornMR2.isHidden = true;
                hornMR3a.isHidden = true;
                hornMR3b.isHidden = true;
                hornMR3c.isHidden = true;
                hornMR3d.isHidden = true;
                hornMR4.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF2.isHidden = true;
                maleNeck.isHidden = true;
            }
            else
            {
                hornMR1.isHidden = true;
                hornML1.isHidden = true;
                maleNeck.isHidden = true;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        headNode.render(par7);
        maleNeck.render(par7);
        legLFrontTop.render(par7);
        legLBackTop.render(par7);
        body.render(par7);
        legRBackTop.render(par7);
        chestHump.render(par7);
        legRFrontTop.render(par7);
        humpMain.render(par7);
        chest.render(par7);
        rump.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        setRotateAngle(headNode, f4 / (180F / (float) Math.PI), f3 / (180F / (float) Math.PI), 0F);
        setRotateAngle(neck, f4 / (1.5F * (180F / (float) Math.PI)) + -0.7475245186291712F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);


        this.legRFrontTop.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
        this.legLFrontTop.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
        this.legRBackTop.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
        this.legLBackTop.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;

        hornML1.isHidden = false;
        hornML2.isHidden = false;
        hornML3a.isHidden = false;
        hornML3b.isHidden = false;
        hornML3c.isHidden = false;
        hornML3d.isHidden = false;
        hornML4.isHidden = false;
        hornMR1.isHidden = false;
        hornMR2.isHidden = false;
        hornMR3a.isHidden = false;
        hornMR3b.isHidden = false;
        hornMR3c.isHidden = false;
        hornMR3d.isHidden = false;
        hornMR4.isHidden = false;
        hornRightF1A.isHidden = false;
        hornRightF1B.isHidden = false;
        hornRightF1C.isHidden = false;
        hornRightF1D.isHidden = false;
        hornRightF2.isHidden = false;
        hornLeftF1A.isHidden = false;
        hornLeftF1B.isHidden = false;
        hornLeftF1C.isHidden = false;
        hornLeftF1D.isHidden = false;
        hornLeftF2.isHidden = false;
        udders.isHidden = false;
        maleNeck.isHidden = false;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}