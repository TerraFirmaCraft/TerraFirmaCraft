/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class TFCCactusBlock extends TFCTallGrassBlock
{
    public TFCCactusBlock(Properties properties)
    {
        super(properties.strength(0.25F).sound(SoundType.WOOL));
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
