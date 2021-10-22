package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.BluegillModel;
import net.dries007.tfc.common.entities.aquatic.Bluegill;

public class BluegillRenderer extends MobRenderer<Bluegill, BluegillModel>
{
    private static final ResourceLocation LOCATION = ClientHelpers.animalTexture("bluegill");

    public BluegillRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new BluegillModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("bluegill"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(Bluegill bluegill)
    {
        return LOCATION;
    }
}
