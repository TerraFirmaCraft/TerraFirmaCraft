/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import org.jetbrains.annotations.Nullable;

public class DirtBlock extends Block implements IDirtBlock
{
    private final Supplier<? extends Block> grass;
    @Nullable private final Supplier<? extends Block> path;
    @Nullable private final Supplier<? extends Block> farmland;

    public DirtBlock(Properties properties, SoilBlockType grassType, SoilBlockType.Variant variant)
    {
        this(properties, TFCBlocks.SOIL.get(grassType).get(variant), TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH).get(variant), TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(variant));
    }

    public DirtBlock(Properties properties, Supplier<? extends Block> grass, @Nullable Supplier<? extends Block> path, @Nullable Supplier<? extends Block> farmland)
    {
        super(properties);

        this.grass = grass;
        this.path = path;
        this.farmland = farmland;
    }

    public BlockState getGrass()
    {
        return grass.get().defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate)
    {
        if (context.getItemInHand().canPerformAction(action))
        {
            if (action == ToolActions.SHOVEL_FLATTEN && path != null && TFCConfig.SERVER.enableGrassPathCreation.get())
            {
                return path.get().defaultBlockState();
            }
            if (action == ToolActions.HOE_TILL && farmland != null && TFCConfig.SERVER.enableFarmlandCreation.get() && HoeItem.onlyIfAirAbove(context))
            {
                final BlockState farmlandState = farmland.get().defaultBlockState();
                HoeItem.changeIntoState(farmlandState).accept(context);
                return farmlandState;
            }
        }
        return null;
    }
}