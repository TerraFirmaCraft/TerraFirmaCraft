package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.OrcaModel;
import net.dries007.tfc.common.entities.aquatic.TFCDolphin;

public class OrcaRenderer extends MobRenderer<TFCDolphin, OrcaModel>
{
    private static final ResourceLocation LOCATION = ClientHelpers.animalTexture("orca");

    public OrcaRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new OrcaModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("orca"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(TFCDolphin entity)
    {
        return LOCATION;
    }
}
