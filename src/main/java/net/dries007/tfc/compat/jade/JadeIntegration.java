/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.crop.CropBlock;
import net.dries007.tfc.common.blocks.crop.DeadCropBlock;
import net.dries007.tfc.common.blocks.crop.DeadDoubleCropBlock;
import net.dries007.tfc.common.blocks.devices.*;
import net.dries007.tfc.common.blocks.plant.fruit.*;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltips;
import net.dries007.tfc.compat.jade.common.EntityTooltip;
import net.dries007.tfc.compat.jade.common.EntityTooltips;

@WailaPlugin
@SuppressWarnings("UnstableApiUsage")
public class JadeIntegration implements IWailaPlugin
{
    @Override
    public void registerClient(IWailaClientRegistration registry)
    {
        register(registry, BlockEntityTooltips.BARREL, BarrelBlock.class);
        register(registry, BlockEntityTooltips.BELLOWS, BellowsBlock.class);
        register(registry, BlockEntityTooltips.SAPLING, TFCSaplingBlock.class);
        register(registry, BlockEntityTooltips.BLAST_FURNACE, BlastFurnaceBlock.class);
        register(registry, BlockEntityTooltips.BLOOMERY, BloomeryBlock.class);
        register(registry, BlockEntityTooltips.BLOOM, BloomBlock.class);
        register(registry, BlockEntityTooltips.CHARCOAL_FORGE, CharcoalForgeBlock.class);
        register(registry, BlockEntityTooltips.COMPOSTER, TFCComposterBlock.class);
        register(registry, BlockEntityTooltips.CROP, CropBlock.class);
        register(registry, BlockEntityTooltips.CRUCIBLE, CrucibleBlock.class);
        register(registry, BlockEntityTooltips.FIREPIT, FirepitBlock.class);
        register(registry, BlockEntityTooltips.FRUIT_TREE_SAPLING, FruitTreeSaplingBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, FarmlandBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, DeadCropBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, DeadDoubleCropBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, BananaPlantBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, FruitTreeBranchBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, FruitTreeLeavesBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, StationaryBerryBushBlock.class);
        register(registry, BlockEntityTooltips.HOE_OVERLAY, SpreadingBushBlock.class);
        register(registry, BlockEntityTooltips.LAMP, LampBlock.class);
        register(registry, BlockEntityTooltips.NEST_BOX, NestBoxBlock.class);
        register(registry, BlockEntityTooltips.PIT_KILN_INTERNAL, PitKilnBlock.class);
        register(registry, BlockEntityTooltips.PIT_KILN_ABOVE, FireBlock.class);
        register(registry, BlockEntityTooltips.POWDER_KEG, PowderkegBlock.class);

        register(registry, EntityTooltips.ANIMAL, TFCAnimal.class);
        register(registry, EntityTooltips.ANIMAL, TFCHorse.class);
        register(registry, EntityTooltips.ANIMAL, TFCChestedHorse.class);
    }

    private void register(IWailaClientRegistration registry, BlockEntityTooltip blockEntityTooltip, Class<? extends Block> blockClass)
    {
        registry.registerComponentProvider((tooltip, access, config) -> blockEntityTooltip.display(access.getLevel(), access.getBlockState(), access.getBlockEntity(), tooltip::add), TooltipPosition.BODY, blockClass);
    }

    private void register(IWailaClientRegistration registry, EntityTooltip entityTooltip, Class<? extends Entity> entityClass)
    {
        registry.registerComponentProvider((tooltip, access, config) -> entityTooltip.display(access.getLevel(), access.getEntity(), tooltip::add), TooltipPosition.BODY, entityClass);
    }
}
