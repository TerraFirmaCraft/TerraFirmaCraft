/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class BananaPlantBlock extends SeasonalPlantBlock implements EntityBlockExtension
{
    public static final VoxelShape PLANT = box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape TRUNK_0 = box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape TRUNK_1 = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);

    public BananaPlantBlock(ForgeBlockProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages)
    {
        super(properties, productItem, stages);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(STAGE))
            {
                case 0 -> TRUNK_0;
                case 1 -> TRUNK_1;
                default -> PLANT;
            };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.empty() : getShape(state, worldIn, pos, context);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
        // no op the superclass
    }

    public void cycle(BerryBushBlockEntity te, Level world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;
            if (stage < 2)
            {
                BlockPos downPos = pos.below(3);
                if (random.nextInt(3) == 0 || world.getBlockState(downPos).is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
                {
                    stage++;
                }
                BlockPos abovePos = pos.above();
                if (world.isEmptyBlock(abovePos))
                {
                    world.setBlockAndUpdate(abovePos, state.setValue(STAGE, stage));
                    BerryBushBlockEntity newTe = Helpers.getBlockEntity(world, abovePos, BerryBushBlockEntity.class);
                    if (newTe != null)
                    {
                        newTe.reduceCounter(-1 * (te.getTicksSinceUpdate() - ICalendar.TICKS_IN_DAY));
                    }
                }
                else
                {
                    te.setGrowing(false);
                }
            }
            else if (!world.canSeeSky(pos.above()))
            {
                te.setGrowing(false);
            }
        }
        else if (lifecycle == Lifecycle.DORMANT)
        {
            te.setGrowing(false);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || belowState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        return new ItemStack(TFCBlocks.BANANA_SAPLING.get());
    }
}
