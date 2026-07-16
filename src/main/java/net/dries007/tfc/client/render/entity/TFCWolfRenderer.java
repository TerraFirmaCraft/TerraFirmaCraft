package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.model.entity.TFCWolfModel;
import net.dries007.tfc.common.entities.predator.TFCWolf;

public class TFCWolfRenderer extends MobRenderer<TFCWolf, TFCWolfModel>
{
    public TFCWolfRenderer(EntityRendererProvider.Context ctx, TFCWolfModel model)
    {
        super(ctx, model, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(TFCWolf entity)
    {
        return entity.getTexture();
    }
}
