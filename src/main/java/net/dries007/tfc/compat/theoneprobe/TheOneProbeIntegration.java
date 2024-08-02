/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.theoneprobe;

import java.util.function.Function;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.tooltip.BlockEntityTooltips;
import net.dries007.tfc.util.tooltip.EntityTooltip;
import net.dries007.tfc.util.tooltip.EntityTooltips;

public class TheOneProbeIntegration implements Function<ITheOneProbe, Void>
{
    public static void init(IEventBus bus)
    {
        bus.addListener((InterModEnqueueEvent event) -> InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeIntegration::new));
    }

    @Override
    public Void apply(ITheOneProbe registry)
    {
        BlockEntityTooltips.register((name, tooltip, block) -> register(registry, name, tooltip, block));
        EntityTooltips.register((name, tooltip, entity) -> register(registry, name, tooltip, entity));
        return null;
    }

    private void register(ITheOneProbe top, ResourceLocation name, BlockEntityTooltip tooltip, Class<? extends Block> blockClass)
    {
        top.registerProvider(new IProbeInfoProvider() {
            @Override
            public ResourceLocation getID()
            {
                return name;
            }

            @Override
            public void addProbeInfo(ProbeMode probeMode, IProbeInfo info, Player player, Level level, BlockState blockState, IProbeHitData data)
            {
                if (data.getPos() != null && blockClass.isInstance(blockState.getBlock()))
                {
                    tooltip.display(level, blockState, data.getPos(), level.getBlockEntity(data.getPos()), info::text);
                }
            }
        });
    }

    private void register(ITheOneProbe top, ResourceLocation name, EntityTooltip tooltip, Class<? extends Entity> entityClass)
    {
        top.registerEntityProvider(new IProbeInfoEntityProvider() {
            @Override
            public String getID()
            {
                return name.toString();
            }

            @Override
            public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo info, Player player, Level level, Entity entity, IProbeHitEntityData data)
            {
                if (entityClass.isInstance(entity))
                {
                    tooltip.display(level, entity, info::text);
                }
            }
        });
    }
}
