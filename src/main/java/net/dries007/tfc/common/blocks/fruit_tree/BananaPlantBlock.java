package net.dries007.tfc.common.blocks.fruit_tree;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.berry_bush.AbstractBerryBushBlock;
import net.dries007.tfc.common.blocks.berry_bush.BerryBush;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.util.Helpers;

public class BananaPlantBlock extends AbstractBerryBushBlock
{
    private static final VoxelShape TRUNK_0 = box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape TRUNK_1 = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    public static final VoxelShape PLANT = box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);

    //todo: find out why the TE is randomly null
    public BananaPlantBlock(ForgeBlockProperties properties, BerryBush bush)
    {
        super(properties, bush);
    }

    public void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;

            if (distanceToGround(world, pos, bush.getMaxHeight()) >= bush.getMaxHeight())
            {
                te.setGrowing(false);
            }
            else if (stage < 2)
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
                    BerryBushTileEntity newTe = Helpers.getTileEntity(world, abovePos, BerryBushTileEntity.class);
                    if (newTe != null)
                    {
                        newTe.reduceCounter(te.getTicksSinceUpdate());
                    }
                }
                else
                {
                    te.setGrowing(false);
                }
            }
        }
        else if (lifecycle == Lifecycle.DORMANT)
        {
            te.setGrowing(false); // basically once it's dead it's dead
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || belowState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH);
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {

    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.getValue(STAGE))
        {
            case 0:
                return TRUNK_0;
            case 1:
                return TRUNK_1;
        }
        return PLANT;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return state.getValue(STAGE) == 2 ? VoxelShapes.empty() : getShape(state, worldIn, pos, context);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(TFCBlocks.BANANA_SAPLING.get());
    }
}
