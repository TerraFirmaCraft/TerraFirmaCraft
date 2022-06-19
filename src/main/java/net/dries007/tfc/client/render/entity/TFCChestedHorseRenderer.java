package net.dries007.tfc.client.render.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;

public class TFCChestedHorseRenderer<T extends TFCChestedHorse> extends ChestedHorseRenderer<T>
{
    private final ResourceLocation texture;

    public TFCChestedHorseRenderer(EntityRendererProvider.Context ctx, float shadow, ModelLayerLocation layer, String name)
    {
        this(ctx, shadow, layer, new ResourceLocation("textures/entity/horse/" + name + ".png"));
    }

    public TFCChestedHorseRenderer(EntityRendererProvider.Context ctx, float shadow, ModelLayerLocation layer, ResourceLocation texture)
    {
        super(ctx, shadow, layer);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(T horse)
    {
        return texture;
    }
}
