package net.dries007.tfc.common.blocks;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.stream.Stream;

public class CalciteBlock extends Block
{
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final VoxelShape PILLAR = VoxelShapes.or(
        Block.box(9.5, 0, 12.5, 11.5, 16, 14.5),
        Block.box(8, 0, 1, 11, 16, 4),
        Block.box(3.5, 0, 1.5, 5.5, 16, 3.5),
        Block.box(4, 0, 11, 7, 16, 14),
        Block.box(2.5, 0, 8.5, 4.5, 16, 10.5),
        Block.box(9.5, 0, 4.5, 11.5, 16, 6.5),
        Block.box(11, 0, 8, 14, 16, 11),
        Block.box(4, 0, 4, 8, 16, 8));

    public static final VoxelShape TIP = VoxelShapes.or(
        Block.box(5, 4, 12, 6, 8, 13),
        Block.box(4, 12, 11, 7, 16, 14),
        Block.box(4.5, 8, 11.5, 6.5, 12, 13.5),
        Block.box(9, 4, 2, 10, 8, 3),
        Block.box(8, 12, 1, 11, 16, 4),
        Block.box(8.5, 8, 1.5, 10.5, 12, 3.5),
        Block.box(5, 2, 5, 7, 7, 7),
        Block.box(4, 11, 4, 8, 16, 8),
        Block.box(4.5, 6, 4.5, 7.5, 11, 7.5),
        Block.box(12, 5, 9, 13, 9, 10),
        Block.box(11, 13, 8, 14, 16, 11),
        Block.box(11.5, 9, 8.5, 13.5, 13, 10.5),
        Block.box(10, 6, 5, 11, 12, 6),
        Block.box(9.5, 12, 4.5, 11.5, 16, 6.5),
        Block.box(3, 10, 9, 4, 14, 10),
        Block.box(2.5, 14, 8.5, 4.5, 16, 10.5),
        Block.box(4, 10, 2, 5, 13, 3),
        Block.box(3.5, 13, 1.5, 5.5, 16, 3.5),
        Block.box(10, 9, 13, 11, 14, 14),
        Block.box(9.5, 14, 12.5, 11.5, 16, 14.5));

    public CalciteBlock()
    {
        super(Block.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(1.0F,3.0F).harvestLevel(0).harvestTool(ToolType.PICKAXE));
        this.registerDefaultState(this.getStateDefinition().any().setValue(DOWN, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (TFCTags.Blocks.CAN_COLLAPSE.contains(this))
        {
            BlockPos downPos = pos.below();
            if (TFCFallingBlockEntity.canFallThrough(world, downPos))
            {
                // Potential to collapse from the top
                if (!world.isClientSide() && !isSupported(world, pos))
                {
                    // Spike is unsupported
                    boolean collapsed = false;
                    BlockState stateAt = state;
                    // Mark all blocks below for also collapsing
                    while (stateAt.getBlock() == this)
                    {
                        collapsed |= CollapseRecipe.collapseBlock(world, pos, stateAt);
                        pos = pos.below();
                        stateAt = world.getBlockState(pos);
                    }
                    if (collapsed)
                    {
                        world.playSound(null, pos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundCategory.BLOCKS, 0.8f, 1.0f);
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return stateIn.setValue(DOWN, worldIn.getBlockState(currentPos.below()).getBlock() instanceof CalciteBlock);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) { builder.add(DOWN); }

    private boolean isSupported(World world, BlockPos pos) // lifted from RockSpikeBlock
    {
        BlockState state = world.getBlockState(pos);
        BlockState stateDown = world.getBlockState(pos.below());
        BlockState stateUp = world.getBlockState(pos.above());
        // It can be directly supported below, by either a flat surface, *or* another calcite block
        if (stateDown.isFaceSturdy(world, pos.below(), Direction.UP) || stateUp.getBlock() == this)
        {
            return true;
        }
        // Otherwise, we need to walk upwards and find the roof
        while (state.getBlock() == this)
        {
            pos = pos.below();
            state = world.getBlockState(pos);
        }
        return state.isFaceSturdy(world, pos.above(), Direction.DOWN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return state.getValue(DOWN) ? PILLAR : TIP;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) { return true; }
}
