/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.common.blocks.crop.DeadCropBlock;
import net.dries007.tfc.common.blocks.crop.DeadDoubleCropBlock;
import net.dries007.tfc.common.blocks.plant.fruit.*;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.compat.jade.provider.FruitTreeSaplingProvider;
import net.dries007.tfc.compat.jade.provider.HoeOverlayProvider;
import net.dries007.tfc.compat.jade.provider.SaplingProvider;

@WailaPlugin
@SuppressWarnings("UnstableApiUsage")
public class JadeIntegration implements IWailaPlugin
{

    @Override
    public void register(IWailaCommonRegistration registry)
    {

    }

    @Override
    public void registerClient(IWailaClientRegistration registry)
    {
        registry.registerComponentProvider(SaplingProvider.INSTANCE, TooltipPosition.BODY, TFCSaplingBlock.class);
        registry.registerComponentProvider(FruitTreeSaplingProvider.INSTANCE, TooltipPosition.BODY, FruitTreeSaplingBlock.class);

        // todo: composter, crop, double crop, animal, blast furnace, bloomery, forge, crucible, climate??, IPile, lamp, LPile, ore?, kiln, placed, quern
        registerHoeOverlay(registry, FarmlandBlock.class);
        registerHoeOverlay(registry, DeadCropBlock.class);
        registerHoeOverlay(registry, DeadDoubleCropBlock.class);
        registerHoeOverlay(registry, BananaPlantBlock.class);
        registerHoeOverlay(registry, FruitTreeBranchBlock.class);
        registerHoeOverlay(registry, FruitTreeLeavesBlock.class);
        registerHoeOverlay(registry, StationaryBerryBushBlock.class);
        registerHoeOverlay(registry, SpreadingBushBlock.class);

    }

    private void registerHoeOverlay(IWailaClientRegistration registry, Class<? extends Block> blockClass)
    {
        registry.registerComponentProvider(new HoeOverlayProvider(), TooltipPosition.BODY, blockClass);
    }

    public static void loadHoeOverlay(HoeOverlayBlock block, ITooltip tooltip, BlockAccessor access)
    {
        final List<Component> text = new ArrayList<>();
        final BlockPos pos = access.getPosition();
        final Level level = access.getLevel();
        block.addHoeOverlayInfo(level, pos, level.getBlockState(pos), text, false);
        tooltip.addAll(text);
    }

}
