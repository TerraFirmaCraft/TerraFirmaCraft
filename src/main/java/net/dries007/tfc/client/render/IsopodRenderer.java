/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.IsopodModel;
import net.dries007.tfc.common.entities.aquatic.AquaticCritter;

public class IsopodRenderer extends MobRenderer<AquaticCritter, IsopodModel>
{
    private static final ResourceLocation LOCATION = ClientHelpers.animalTexture("isopod");

    public IsopodRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new IsopodModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("isopod"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(AquaticCritter entity)
    {
        return LOCATION;
    }

}
