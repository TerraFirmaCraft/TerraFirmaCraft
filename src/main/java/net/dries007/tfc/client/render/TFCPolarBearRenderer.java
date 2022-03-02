package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.TFCPolarBearModel;
import net.dries007.tfc.common.entities.predator.Predator;

public class TFCPolarBearRenderer extends MobRenderer<Predator, TFCPolarBearModel>
{
    private static final ResourceLocation POLAR_BEAR_LOCATION = ClientHelpers.animalTexture("polar_bear");

    public TFCPolarBearRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new TFCPolarBearModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("polar_bear"))), 0.9F);
    }

    @Override
    protected void scale(Predator predator, PoseStack poseStack, float ticks)
    {
        poseStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(predator, poseStack, ticks);
    }

    @Override
    public ResourceLocation getTextureLocation(Predator predator)
    {
        return POLAR_BEAR_LOCATION;
    }
}
