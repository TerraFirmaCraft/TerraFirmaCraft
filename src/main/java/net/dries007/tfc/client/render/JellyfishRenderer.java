package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.JellyfishModel;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;

public class JellyfishRenderer extends MobRenderer<Jellyfish, JellyfishModel>
{
    public JellyfishRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new JellyfishModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("jellyfish"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(Jellyfish jellyfish)
    {
        return jellyfish.getTextureLocation();
    }
}