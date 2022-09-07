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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.ui.IElement;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.crop.DeadCropBlock;
import net.dries007.tfc.common.blocks.crop.DeadDoubleCropBlock;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.devices.NestBoxBlock;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.blocks.plant.fruit.*;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.compat.jade.provider.*;

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
        registry.registerComponentProvider(ComposterProvider.INSTANCE, TooltipPosition.BODY, TFCComposterBlock.class);
        registry.registerComponentProvider(BarrelProvider.INSTANCE, TooltipPosition.BODY, BarrelBlock.class);
        registry.registerComponentProvider(BloomeryProvider.INSTANCE, TooltipPosition.BODY, BloomeryBlock.class);
        registry.registerComponentProvider(BloomProvider.INSTANCE, TooltipPosition.BODY, BloomBlock.class);
        registry.registerComponentProvider(NestBoxProvider.INSTANCE, TooltipPosition.BODY, NestBoxBlock.class);

        // todo: crop, double crop, animal, blast furnace, forge, crucible, climate??, IPile, lamp, kiln
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

    public static IElement getItem(ITooltip tooltip, ItemStack item)
    {
        return tooltip.getElementHelper().item(item, 0.5f).size(new Vec2(10, 10)).translate(new Vec2(0, -1));
    }

}
