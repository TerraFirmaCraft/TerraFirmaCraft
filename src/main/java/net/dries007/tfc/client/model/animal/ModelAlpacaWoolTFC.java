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

import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;

/**
 * ModelAlpacaWoolTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelAlpacaWoolTFC extends ModelBase
{
    private final ModelRenderer head;
    private final ModelRenderer neck1;
    private final ModelRenderer neck2;
    private final ModelRenderer body;
    private final ModelRenderer tailf;
    private final ModelRenderer tailm;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;

    public ModelAlpacaWoolTFC()
    {
        textureWidth = 128;
        textureHeight = 64;

        head = new ModelRenderer(this, 4, 51);
        head.addBox(-3.0F, -2.5F, -2.8F, 6, 6, 7, 0.5F);
        head.setRotationPoint(0.0F, -4F, -9.4F);

        neck1 = new ModelRenderer(this, 50, 52);
        neck1.addBox(-2F, -3.5F, -2.5F, 4, 7, 5, 0.3F);
        neck1.setRotationPoint(0.001F, 8.6F, -6.7F);
        setRotation(neck1, 0.591841F, 0F, 0F);

        neck2 = new ModelRenderer(this, 51, 39);
        neck2.addBox(-1.5F, -4F, -2.5F, 4, 9, 4, 0.3F);
        neck2.setRotationPoint(-0.5F, 1.71F, -8.34F);
        setRotation(neck2, 0F, 0F, 0F);

        body = new ModelRenderer(this, 98, 34);
        body.addBox(-4.5F, -8.5F, -3F, 9, 17, 6, 1F);
        body.setRotationPoint(0F, 10.0F, 1.0F);

        tailf = new ModelRenderer(this, 99, 57);
        tailf.addBox(-1F, -1F, -1.5F, 2, 2, 3, 0.5F);
        tailf.setRotationPoint(0F, 9.5F, 9.8F);

        tailm = new ModelRenderer(this, 113, 57);
        tailm.addBox(-1.5F, -1.5F, -1.5F, 3, 4, 3, 0.5F);
        tailm.setRotationPoint(0F, 9.5F, 9.8F);

        leg1 = new ModelRenderer(this, 82, 44);
        leg1.addBox(-1F, 0.5F, -2F, 4, 9, 4, 0.4F);
        leg1.setRotationPoint(-4F, 13F, 6.99F);

        leg2 = new ModelRenderer(this, 82, 44);
        leg2.addBox(0F, 0.5F, -2F, 4, 9, 4, 0.4F);
        leg2.setRotationPoint(1F, 13F, 6.99F);

        leg3 = new ModelRenderer(this, 82, 44);
        leg3.addBox(-1F, 0.5F, -2F, 4, 9, 4, 0.4F);
        leg3.setRotationPoint(-4F, 13F, -4F);

        leg4 = new ModelRenderer(this, 82, 44);
        leg4.addBox(0F, 0.5F, -2F, 4, 9, 4, 0.4F);
        leg4.setRotationPoint(1F, 13F, -4F);
    }

    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityAlpacaTFC alpaca = ((EntityAlpacaTFC) entity);

        float percent = (float) alpaca.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head.render(par7);
        neck1.render(par7);
        neck2.render(par7);
        body.render(par7);
        tailf.render(par7);
        tailm.render(par7);
        leg1.render(par7);
        leg2.render(par7);
        leg3.render(par7);
        leg4.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.body.rotateAngleX = (float) Math.PI / 2F;
        this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.2F * par2;
        this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.2F * par2;
        this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.2F * par2;
        this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.2F * par2;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}