/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelAlpacaWoolTFC extends ModelBase
{
    private ModelRenderer ear1f;
    private ModelRenderer ear2f;
    private ModelRenderer ear1m;
    private ModelRenderer ear2m;
    private ModelRenderer nose;
    private ModelRenderer head;
    private ModelRenderer neck1;
    private ModelRenderer neck2;
    private ModelRenderer body;
    private ModelRenderer tailf;
    private ModelRenderer tailm;
    private ModelRenderer leg1;
    private ModelRenderer leg2;
    private ModelRenderer leg3;
    private ModelRenderer leg4;

    public ModelAlpacaWoolTFC()
    {
        //super(12, 0.0F);
        textureWidth = 128;
        textureHeight = 64;

        ear1f = new ModelRenderer(this, 16, 14);
        ear1f.addBox(-1F, -1F, -1F, 2, 2, 2, 0.3F);
        ear1f.setRotationPoint(-1.5F, -5F, -1F);
        setRotation(ear1f, 0F, 0F, 0F);

        ear2f = new ModelRenderer(this, 16, 14);
        ear2f.addBox(-1F, -1F, -1F, 2, 2, 2, 0.3F);
        ear2f.setRotationPoint(1.5F, -5F, -1F);
        setRotation(ear2f, 0F, 0F, 0F);

        ear1m = new ModelRenderer(this, 16, 14);
        ear1m.addBox(-1F, -1F, -1F, 2, 3, 2, 0.3F);
        ear1m.setRotationPoint(-1.5F, -5.5F, -1F);
        setRotation(ear1m, 0F, 0F, 0F);

        ear2m = new ModelRenderer(this, 16, 14);
        ear2m.addBox(-1F, -1F, -1F, 2, 3, 2, 0.3F);
        ear2m.setRotationPoint(1.5F, -5.5F, -1F);
        setRotation(ear2m, 0F, 0F, 0F);

        nose = new ModelRenderer(this, 16, 14);
        nose.addBox(-2F, -1.5F, -1.5F, 4, 3, 2, 0.3F);
        nose.setRotationPoint(0F, -0.2F, -6.5F);

        head = new ModelRenderer(this, 2, 34);
        head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 7, 0.4F);
        head.setRotationPoint(0.0F, -2F, -6.5F);

        neck1 = new ModelRenderer(this, 75, 25);
        neck1.addBox(-2.001F, -3.501F, -2.501F, 4, 7, 5, 0.3F);
        neck1.setRotationPoint(0F, 8.5F, -6.9F);
        setRotation(neck1, 0.591841F, 0F, 0F);

        neck2 = new ModelRenderer(this, 58, 30);
        neck2.addBox(-2F, -4F, -2.5F, 4, 8, 5, 0.3F);
        neck2.setRotationPoint(0F, 2.6F, -8.55F);
        setRotation(neck2, 0F, 0F, 0F);

        body = new ModelRenderer(this, 58, 30);
        body.addBox(-4.5F, -3.0F, -8.5F, 9, 6, 17, 0.75F);
        body.setRotationPoint(0.5F, 10.0F, 1.0F);

        tailf = new ModelRenderer(this, 16, 14);
        tailf.addBox(-1F, -1F, -1.5F, 2, 2, 3, 0.5F);
        tailf.setRotationPoint(0F, 9.5F, 9.8F);

        tailm = new ModelRenderer(this, 16, 14);
        tailm.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.5F);
        tailm.setRotationPoint(0F, 9.5F, 9.8F);

        leg1 = new ModelRenderer(this, 0, 13);
        leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
        leg1.setRotationPoint(-2.5F, 12.0F, 7.0F);

        leg2 = new ModelRenderer(this, 0, 13);
        leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
        leg2.setRotationPoint(3.5F, 12.0F, 7.0F);

        leg3 = new ModelRenderer(this, 0, 13);
        leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
        leg3.setRotationPoint(-2.5F, 12.0F, -3.5F);

        leg4 = new ModelRenderer(this, 0, 13);
        leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
        leg4.setRotationPoint(3.5F, 12.0F, -3.5F);

        head.addChild(ear1f);
        head.addChild(ear2f);
        head.addChild(ear1m);
        head.addChild(ear2m);
        head.addChild(nose);
    }

    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityAlpacaTFC alpaca = ((EntityAlpacaTFC) entity);

        float percent = alpaca.getPercentToAdulthood();
        float ageScale = 2.0F - percent;
        float ageHeadScale = (float) Math.pow(1 / ageScale, 0.66);

        GlStateManager.pushMatrix();

        GlStateManager.translate(0.0F, 0.75f - (0.75f * percent), 0f);
        GlStateManager.scale(ageHeadScale, ageHeadScale, ageHeadScale);
        GlStateManager.translate(0.0F, (ageScale - 1) * -0.125f, 0.1875f - (0.1875f * percent));

        if (alpaca.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            ear1f.isHidden = true;
            ear2f.isHidden = true;
            ear1m.isHidden = false;
            ear2m.isHidden = false;
            tailf.isHidden = true;
            tailm.isHidden = false;
        }

        else
        {
            ear1f.isHidden = false;
            ear2f.isHidden = false;
            ear1m.isHidden = true;
            ear2m.isHidden = true;
            tailf.isHidden = false;
            tailm.isHidden = true;
        }


        head.render(par7);
        neck1.render(par7);
        neck2.render(par7);

        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.75f - (0.75f * percent), 0f);
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);

        body.render(par7);
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