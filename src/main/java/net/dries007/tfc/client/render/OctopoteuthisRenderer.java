/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.aquatic.Octopoteuthis;

public class OctopoteuthisRenderer extends TFCSquidRenderer<Octopoteuthis>
{
    private static final ResourceLocation LOCATION = new ResourceLocation("textures/entity/squid/glow_squid.png");

    public OctopoteuthisRenderer(EntityRendererProvider.Context ctx, SquidModel<Octopoteuthis> model)
    {
        super(ctx, model);
    }

    @Override
    public ResourceLocation getTextureLocation(Octopoteuthis squid) {
        return LOCATION;
    }

    @Override
    protected int getBlockLightLevel(Octopoteuthis squid, BlockPos pos)
    {
        int darkness = (int) Mth.clampedLerp(0.0F, 15.0F, 1.0F - (float)squid.getDarkTicksRemaining() / 10.0F);
        return darkness == 15 ? 15 : Math.max(darkness, super.getBlockLightLevel(squid, pos));
    }
}
