/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;

/**
 * Dirt that doesn't let grass spread to it but can still be interacted on
 */
public class TFCRootedDirtBlock extends Block
{
    private final Supplier<? extends Block> dirt;

    public TFCRootedDirtBlock(Properties properties, SoilBlockType dirtType, SoilBlockType.Variant variant)
    {
        super(properties);
        this.dirt = TFCBlocks.SOIL.get(dirtType).get(variant);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction action)
    {
        //todo: should idiomatically be hoe till action, which forge didn't implement yet... https://github.com/MinecraftForge/MinecraftForge/pull/8025
        if (stack.canPerformAction(action) && action == ToolActions.SHOVEL_FLATTEN && TFCConfig.SERVER.enableGrassPathCreation.get())
        {
            return dirt.get().defaultBlockState();
        }
        return state;
    }
}
