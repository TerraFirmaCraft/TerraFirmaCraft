package net.dries007.tfc.common.blocks.wood;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class TwigBlock extends GroundcoverBlock
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    private static final VoxelShape TWIG = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);

    public TwigBlock()
    {
        super(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(FACING, Direction.EAST));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return TWIG;
    }
}
