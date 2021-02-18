package net.dries007.tfc.common.blocks.fruit_tree;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;

public class FruitTreeLeavesBlock extends Block implements IForgeBlockProperties, ILeavesBlock
{
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    private final ForgeBlockProperties properties;

    public FruitTreeLeavesBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        registerDefaultState(getStateDefinition().any().setValue(PERSISTENT, false));
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {

    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return !state.getValue(PERSISTENT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!isValid(worldIn, pos, state))
            worldIn.destroyBlock(pos, true);
    }

    private boolean isValid(IWorld worldIn, BlockPos pos, BlockState state)
    {
        if (state.getValue(PERSISTENT)) return true;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction direction : Direction.values())
        {
            mutablePos.set(pos).move(direction);
            if (worldIn.getBlockState(mutablePos).is(TFCTags.Blocks.FRUIT_TREE_BRANCH)) return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return isValid(worldIn, currentPos, stateIn) ? stateIn : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PERSISTENT);
    }
}
