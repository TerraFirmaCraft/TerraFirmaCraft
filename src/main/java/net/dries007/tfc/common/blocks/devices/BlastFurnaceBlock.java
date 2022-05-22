/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.SheetPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.MultiBlock;

public class BlastFurnaceBlock extends DeviceBlock
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final MultiBlock BLAST_FURNACE_CHIMNEY;
    private static final int MAX_CHIMNEY_LEVELS = 4;

    static
    {
        BLAST_FURNACE_CHIMNEY = new MultiBlock()
            .match(new BlockPos(0, 0, 0), state -> state.isAir() || Helpers.isBlock(state, TFCBlocks.MOLTEN.get()))
            .match(new BlockPos(0, 0, 1), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(0, 0, -1), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(1, 0, 0), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(-1, 0, 0), TFCTags.Blocks.BLAST_FURNACE_INSULATION)
            .match(new BlockPos(0, 0, -2), matchSheet(Direction.SOUTH))
            .match(new BlockPos(0, 0, 2), matchSheet(Direction.NORTH))
            .match(new BlockPos(2, 0, 0), matchSheet(Direction.WEST))
            .match(new BlockPos(-2, 0, 0), matchSheet(Direction.EAST))
            .match(new BlockPos(-1, 0, -1), matchSheet(Direction.SOUTH, Direction.EAST))
            .match(new BlockPos(1, 0, -1), matchSheet(Direction.SOUTH, Direction.WEST))
            .match(new BlockPos(-1, 0, 1), matchSheet(Direction.NORTH, Direction.EAST))
            .match(new BlockPos(1, 0, 1), matchSheet(Direction.NORTH, Direction.WEST));
    }

    /**
     * @return The number of layers of chimney present in the blast furnace, in the range [0, 4].
     */
    public static int getChimneyLevels(Level level, BlockPos centerPos)
    {
        for (int i = 0; i < MAX_CHIMNEY_LEVELS; i++)
        {
            final BlockPos center = centerPos.above(i + 1);
            if (!BLAST_FURNACE_CHIMNEY.test(level, center))
            {
                return i;
            }
        }
        return MAX_CHIMNEY_LEVELS;
    }

    private static BiPredicate<LevelAccessor, BlockPos> matchSheet(Direction face)
    {
        return (level, pos) -> {
            final BlockState state = level.getBlockState(pos);
            final SheetPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).orElse(null);
            return Helpers.isBlock(state, TFCBlocks.SHEET_PILE.get())
                && pile != null
                && isTier3SheetOrHigherInDirection(state, pile, face);
        };
    }

    private static BiPredicate<LevelAccessor, BlockPos> matchSheet(Direction face, Direction secondFace)
    {
        return (level, pos) -> {
            final BlockState state = level.getBlockState(pos);
            final SheetPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.SHEET_PILE.get()).orElse(null);
            return Helpers.isBlock(state, TFCBlocks.SHEET_PILE.get())
                && pile != null
                && isTier3SheetOrHigherInDirection(state, pile, face)
                && isTier3SheetOrHigherInDirection(state, pile, secondFace);
        };
    }

    private static boolean isTier3SheetOrHigherInDirection(BlockState state, SheetPileBlockEntity pile, Direction face)
    {
        return state.getValue(DirectionPropertyBlock.getProperty(face))
            && pile.getOrCacheMetal(face).getTier() >= Metal.Tier.TIER_III.ordinal();
    }

    public BlastFurnaceBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIT));
    }
}
