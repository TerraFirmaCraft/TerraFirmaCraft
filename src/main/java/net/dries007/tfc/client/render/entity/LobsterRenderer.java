/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.entity.LobsterModel;
import net.dries007.tfc.common.entities.aquatic.AquaticCritterEntity;

public class LobsterRenderer extends MobRenderer<AquaticCritterEntity, LobsterModel>
{
    private static final ResourceLocation LOCATION = ClientHelpers.animalTexture("lobster");

    public LobsterRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new LobsterModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("lobster"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(AquaticCritterEntity bluegill)
    {
        return LOCATION;
    }

}
