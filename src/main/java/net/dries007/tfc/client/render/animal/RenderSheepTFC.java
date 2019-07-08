package net.dries007.tfc.client.render.animal;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.animal.ModelSheepBodyTFC;
import net.dries007.tfc.objects.entity.animal.EntitySheepTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class RenderSheepTFC extends RenderLiving<EntitySheepTFC>
{
    private static final ResourceLocation SHEEP_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/sheep.png");

    public RenderSheepTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelSheepBodyTFC(), 0.7F);
        this.addLayer(new LayerSheepWoolTFC(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntitySheepTFC entity)
    {
        return SHEEP_TEXTURES;
    }
}
