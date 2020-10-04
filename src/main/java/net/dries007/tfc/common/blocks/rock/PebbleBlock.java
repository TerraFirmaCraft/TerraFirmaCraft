package net.dries007.tfc.common.blocks.rock;

import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.types.Rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PebbleBlock extends GroundcoverBlock
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final IntegerProperty ROCKS = IntegerProperty.create("rocks", 1, 3);

    private static final VoxelShape ONE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    private static final VoxelShape TWO = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    private static final VoxelShape THREE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);

    private final Rock.Default rock;

    public PebbleBlock(Rock.Default rock)
    {
        super(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.STONE).noOcclusion());
        this.registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(FACING, Direction.EAST).setValue(ROCKS, 1));
        this.rock = rock;
    }

    public Rock.Default getRock() { return rock; } // this is used by ItemRock to check for matching types

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED, ROCKS);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.getValue(ROCKS))
        {
            case 1:
            default:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
        }
    }
}
