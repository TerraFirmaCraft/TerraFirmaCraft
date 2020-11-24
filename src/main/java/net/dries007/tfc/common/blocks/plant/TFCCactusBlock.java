/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class TFCCactusBlock extends TFCTallGrassBlock
{
    public static TFCCactusBlock create(IPlant plant, Properties properties)
    {
        return new TFCCactusBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected TFCCactusBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        if (state.getValue(PART) == Part.LOWER)
        {
            return blockstate.is(BlockTags.SAND);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return blockstate.is(BlockTags.SAND); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return blockstate.getBlock() == this && blockstate.getValue(PART) == Part.LOWER;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        entityIn.hurt(DamageSource.CACTUS, 1.0F);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.block();
    }

    @Override
    public OffsetType getOffsetType()
    {
        return OffsetType.NONE;
    }
}
