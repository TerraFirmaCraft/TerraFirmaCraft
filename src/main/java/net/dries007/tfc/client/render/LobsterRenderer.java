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
import net.dries007.tfc.client.model.LobsterModel;
import net.dries007.tfc.common.entities.aquatic.AquaticCritter;

public class LobsterRenderer extends MobRenderer<AquaticCritter, LobsterModel>
{
    private static final ResourceLocation LOCATION = ClientHelpers.animalTexture("lobster");

    public LobsterRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new LobsterModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("lobster"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(AquaticCritter bluegill)
    {
        return LOCATION;
    }

}
