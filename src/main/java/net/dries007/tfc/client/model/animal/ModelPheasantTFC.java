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
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityPheasantTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelPheasantTFC extends ModelBase
{
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer tailFeather;
    private final ModelRenderer leftLowerLeg;
    private final ModelRenderer rightLowerLeg;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer beak;
    private final ModelRenderer chest;
    private final ModelRenderer leftWing;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftFoot;
    private final ModelRenderer rightFoot;

    public ModelPheasantTFC()
    {
        textureWidth = 64;
        textureHeight = 32;

        body = new ModelRenderer(this, 0, 7);
        body.addBox(-4F, 0F, -2.5F, 9, 6, 5);
        body.setRotationPoint(0F, 12F, 0F);
        body.mirror = true;

        tail = new ModelRenderer(this, 0, 0);
        tail.addBox(-7F, 2F, -2F, 6, 3, 4);
        tail.setRotationPoint(0F, 12F, 0F);
        tail.mirror = true;

        leftLeg = new ModelRenderer(this, 16, 24);
        leftLeg.addBox(1F, 4F, 1F, 3, 4, 2);
        leftLeg.setRotationPoint(0F, 12F, 0F);
        leftLeg.mirror = true;

        rightLeg = new ModelRenderer(this, 26, 24);
        rightLeg.addBox(1F, 4F, -3F, 3, 4, 2);
        rightLeg.setRotationPoint(0F, 12F, 0F);
        rightLeg.mirror = true;

        tailFeather = new ModelRenderer(this, 20, 0);
        tailFeather.addBox(-14.96F, 3F, -1F, 9, 1, 2);
        tailFeather.setRotationPoint(0F, 12F, 0F);
        tailFeather.mirror = true;

        leftLowerLeg = new ModelRenderer(this, 34, 19);
        leftLowerLeg.addBox(-1F, 8F, 1.5F, 1, 4, 1);
        leftLowerLeg.setRotationPoint(0F, 0F, 0F);
        leftLowerLeg.mirror = true;

        rightLowerLeg = new ModelRenderer(this, 38, 19);
        rightLowerLeg.addBox(-1F, 8F, -2.5F, 1, 4, 1);
        rightLowerLeg.setRotationPoint(0F, 0F, 0F);
        rightLowerLeg.mirror = true;

        neck = new ModelRenderer(this, 28, 13);
        neck.addBox(4F, 2F, -1.5F, 4, 3, 3, 0.05F);
        neck.setRotationPoint(-4F, 2F, 0F);
        neck.mirror = true;

        head = new ModelRenderer(this, 16, 18);
        head.addBox(2.5F, -3F, -1.5F, 3, 3, 3, 0.1F);
        head.setRotationPoint(4F, 10F, 0F);
        head.mirror = true;

        beak = new ModelRenderer(this, 28, 19);
        beak.addBox(5F, -2F, -0.5F, 2, 1, 1);
        beak.setRotationPoint(4F, 10F, 0F);
        beak.mirror = true;

        chest = new ModelRenderer(this, 28, 7);
        chest.addBox(-1F, 4.5F, -1.5F, 4, 3, 3);
        chest.setRotationPoint(-4F, 2F, 0F);
        chest.mirror = true;

        leftWing = new ModelRenderer(this, 0, 23);
        leftWing.addBox(-3F, 1F, 2.5F, 7, 4, 1);
        leftWing.setRotationPoint(0F, 12F, 0F);
        leftWing.mirror = true;

        rightWing = new ModelRenderer(this, 0, 18);
        rightWing.addBox(-3F, 1F, -3.5F, 7, 4, 1);
        rightWing.setRotationPoint(0F, 12F, 0F);
        rightWing.mirror = true;

        leftFoot = new ModelRenderer(this, 20, 3);
        leftFoot.addBox(-2F, 11.9F, 0.5F, 4, 0, 3, 0.001F);
        leftFoot.setRotationPoint(0F, 0F, 0F);
        leftFoot.mirror = true;

        rightFoot = new ModelRenderer(this, 20, 3);
        rightFoot.addBox(-2F, 11.9F, -3.5F, 4, 0, 3, 0.001F);
        rightFoot.setRotationPoint(0F, 0F, 0F);
        rightFoot.mirror = true;

        head.addChild(neck);
        head.addChild(chest);

        rightLeg.addChild(rightLowerLeg);
        rightLowerLeg.addChild(rightFoot);
        leftLeg.addChild(leftLowerLeg);
        leftLowerLeg.addChild(leftFoot);
    }

    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        float percent = (float) ((EntityPheasantTFC) entity).getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        GlStateManager.pushMatrix();

        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        this.head.render(par7);
        this.beak.render(par7);

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        this.body.render(par7);
        this.rightLeg.render(par7);
        this.leftLeg.render(par7);
        this.rightWing.render(par7);
        this.leftWing.render(par7);
        this.tail.render(par7);
        this.tailFeather.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity)
    {
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.beak.rotateAngleY = this.head.rotateAngleY;
        this.neck.rotateAngleZ = -(5 * (float) (Math.PI / 18F));
        this.chest.rotateAngleZ = -(8 * (float) (Math.PI / 18F));
        this.body.rotateAngleZ = -((float) (Math.PI / 6F));
        this.rightWing.rotateAngleZ = -((float) (Math.PI / 6F));
        this.leftWing.rotateAngleZ = -((float) (Math.PI / 6F));
        if (par3 != 0)
        {
            rightWing.setRotationPoint(4, 12, -2);
            leftWing.setRotationPoint(4, 12, 2);
            rightWing.rotateAngleZ = -(float) (Math.PI / 2F);
            leftWing.rotateAngleZ = -(float) (Math.PI / 2F);
            rightWing.offsetX = -3F / 16F;
            rightWing.offsetY = -3F / 16F;
            rightWing.offsetZ = -1.5F / 16F;
            leftWing.offsetX = -3F / 16F;
            leftWing.offsetY = -3F / 16F;
            leftWing.offsetZ = 1.5F / 16F;
        }
        else
        {
            rightWing.setRotationPoint(0, 12, 0);
            leftWing.setRotationPoint(0, 12, 0);
            rightWing.offsetX = 0;
            rightWing.offsetY = 0;
            rightWing.offsetZ = 0;
            leftWing.offsetX = 0;
            leftWing.offsetY = 0;
            leftWing.offsetZ = 0;
        }
        this.rightWing.rotateAngleY = par3;
        this.leftWing.rotateAngleY = -par3;


        this.tail.rotateAngleZ = -((float) (Math.PI / 18F));
        this.tailFeather.rotateAngleZ = -((float) (Math.PI / 36F));
        this.rightLeg.rotateAngleZ = ((float) (Math.PI / 9F)) + MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.leftLeg.rotateAngleZ = ((float) (Math.PI / 9F)) + MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.rightLowerLeg.rotateAngleZ = -((float) (Math.PI / 9F));
        this.leftLowerLeg.rotateAngleZ = -((float) (Math.PI / 9F));
        this.rightFoot.rotateAngleZ = 0;
        this.leftFoot.rotateAngleZ = 0;
    }

}