package net.dries007.tfc.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.dries007.tfc.util.Helpers;

public class ThatchBlock extends Block implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;

    public ThatchBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());

        this.properties = properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        Helpers.slowEntityInBlock(entityIn, 0.3f, 5);
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
