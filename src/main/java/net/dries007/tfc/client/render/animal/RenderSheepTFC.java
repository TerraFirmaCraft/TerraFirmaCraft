package net.dries007.tfc.client.render.animal;

import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.objects.entity.animal.EntitySheepTFC;

public class RenderSheepTFC extends RenderLiving<EntitySheepTFC>
{
    private static final ResourceLocation SHEARED_SHEEP_TEXTURES = new ResourceLocation("textures/entity/sheep/sheep.png");

    public RenderSheepTFC(RenderManager p_i47195_1_)
    {
        super(p_i47195_1_, new ModelSheep2(), 0.7F);
        //this.addLayer(new LayerSheepWool(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySheepTFC entity)
    {
        return SHEARED_SHEEP_TEXTURES;
    }
}
