package net.dries007.tfc.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ThatchBlock extends Block
{
    public ThatchBlock()
    {
        super(Properties.of(Material.REPLACEABLE_PLANT).strength(0.6F, 0.4F).noOcclusion());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        entityIn.makeStuckInBlock(state, new Vector3d(0.3D, (double)0.24F, 0.3D));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }
}
