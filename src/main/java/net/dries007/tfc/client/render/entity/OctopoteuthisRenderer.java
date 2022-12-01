/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.entities.aquatic.Octopoteuthis;

public class OctopoteuthisRenderer extends TFCSquidRenderer<Octopoteuthis>
{
    private static final ResourceLocation LOCATION = new ResourceLocation("textures/entity/squid/glow_squid.png");

    public OctopoteuthisRenderer(EntityRendererProvider.Context ctx, SquidModel<Octopoteuthis> model)
    {
        super(ctx, model);
    }

    @Override
    public ResourceLocation getTextureLocation(Octopoteuthis squid)
    {
        return LOCATION;
    }

    @Override
    protected int getBlockLightLevel(Octopoteuthis squid, BlockPos pos)
    {
        if (squid.getDarkTicksRemaining() > 0)
        {
            return super.getBlockLightLevel(squid, pos);
        }
        else
        {
            return 15;
        }
    }
}
