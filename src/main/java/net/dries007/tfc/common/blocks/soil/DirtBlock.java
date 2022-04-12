/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

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

public class DirtBlock extends Block implements IDirtBlock
{
    private final Supplier<? extends Block> grass;
    @Nullable private final Supplier<? extends Block> path;

    public DirtBlock(Properties properties, SoilBlockType grassType, SoilBlockType.Variant variant)
    {
        this(properties, TFCBlocks.SOIL.get(grassType).get(variant), TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH).get(variant));
    }

    public DirtBlock(Properties properties, Supplier<? extends Block> grass, @Nullable Supplier<? extends Block> path)
    {
        super(properties);

        this.grass = grass;
        this.path = path;
    }

    public BlockState getGrass()
    {
        return grass.get().defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction action)
    {
        if (stack.canPerformAction(action) && action == ToolActions.SHOVEL_FLATTEN && path != null && TFCConfig.SERVER.enableGrassPathCreation.get())
        {
            return path.get().defaultBlockState();
        }
        return null;
    }
}