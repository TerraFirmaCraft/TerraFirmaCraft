/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.compat.jade.common.*;

@WailaPlugin
@SuppressWarnings("UnstableApiUsage")
public class JadeIntegration implements IWailaPlugin
{
    @Override
    public void registerClient(IWailaClientRegistration registry)
    {
        BlockEntityTooltips.register((tooltip, aClass) -> register(registry, tooltip, aClass));
        EntityTooltips.register((tooltip, aClass) -> register(registry, tooltip, aClass));
    }

    private void register(IWailaClientRegistration registry, BlockEntityTooltip blockEntityTooltip, Class<? extends Block> blockClass)
    {
        registry.registerComponentProvider((tooltip, access, config) -> blockEntityTooltip.display(access.getLevel(), access.getBlockState(), access.getPosition(), access.getBlockEntity(), tooltip::add), TooltipPosition.BODY, blockClass);
    }

    private void register(IWailaClientRegistration registry, EntityTooltip entityTooltip, Class<? extends Entity> entityClass)
    {
        registry.registerComponentProvider((tooltip, access, config) -> entityTooltip.display(access.getLevel(), access.getEntity(), tooltip::add), TooltipPosition.BODY, entityClass);
    }
}
