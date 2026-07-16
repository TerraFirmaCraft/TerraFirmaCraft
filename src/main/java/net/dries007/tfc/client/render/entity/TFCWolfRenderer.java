package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.model.entity.TFCWolfModel;
import net.dries007.tfc.common.entities.predator.TFCWolf;
import net.dries007.tfc.util.Helpers;

public class TFCWolfRenderer extends SimpleMobRenderer<TFCWolf, TFCWolfModel>
{
    private static final ResourceLocation DEFAULT_WOLF_LOCATION = Helpers.identifierMC("textures/entity/wolf/wolf.png");

    public TFCWolfRenderer(EntityRendererProvider.Context ctx, TFCWolfModel model)
    {
        super(ctx, model, "wolf", 0.5f, false, 1.1F, false, false, wolf -> DEFAULT_WOLF_LOCATION);
    }

    @Override
    public ResourceLocation getTextureLocation(TFCWolf entity)
    {
        return entity.getTexture();
    }
}
