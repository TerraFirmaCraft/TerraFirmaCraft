package net.dries007.tfc.common.blocks;

import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class GroundcoverBlock extends Block implements IWaterLoggable
{
    //todo: random rotations through the blockstate
    //todo: fallen leaves/logs

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    protected static final VoxelShape FLAT = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    protected static final VoxelShape SMALL = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    protected static final VoxelShape MEDIUM = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);
    protected static final VoxelShape LARGE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D);
    protected static final VoxelShape LONG = Block.makeCuboidShape(1.0D, 0.0D, 5.0D, 15.0D, 4.0D, 11.0D);

    public VoxelShape shape;

    public GroundcoverBlock(MiscCoverTypes cover)
    {
        super(Properties.create(Material.ORGANIC).hardnessAndResistance(0.05F, 0.0F).notSolid());
        shape = cover.getShape();
        this.setDefaultState(getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.EAST));
    }

    public GroundcoverBlock(RockCoverTypes cover)
    {
        super(Properties.create(Material.EARTH).hardnessAndResistance(0.05F, 0.0F).notSolid());
        shape = cover.getShape();
        this.setDefaultState(getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.EAST));
    }

    public GroundcoverBlock()
    {
        super(Properties.create(Material.EARTH).hardnessAndResistance(0.05F, 0.0F).notSolid());
        shape = SMALL;
        this.setDefaultState(getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.EAST));
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        boolean flag = ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8;
        return super.getStateForPlacement(context).with(WATERLOGGED, flag).with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.isValidPosition(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            if (stateIn.get(WATERLOGGED))
            {
                worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        worldIn.destroyBlock(pos, (!player.isCreative() && player.getHeldItem(handIn) == ItemStack.EMPTY));
        return ActionResultType.PASS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED, FACING);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if(!world.isRemote)
        {
            IFluidState ifluidstate = world.getFluidState(pos);
            world.setBlockState(pos, state.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isSolid();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public enum MiscCoverTypes
    {
        BONES(MEDIUM, false), // drops bones
        BRANCH(FLAT, false), // drops sticks
        CLAM(SMALL, true), // flux
        DEAD_GRASS(FLAT, false), // drops straw
        DRIFTWOOD(LONG, false), // drops sticks
        FEATHER(FLAT, false),
        FLINT(SMALL, false),
        GUANO(SMALL, true), // guano is traditionally a fertilizer
        MOLLUSK(SMALL, true), // flux
        MUSSEL(SMALL, true), // flux
        PINECONE(SMALL, false), // drops something useful for compost?
        PODZOL(FLAT, false), // drops something useful for compost?
        ROTTEN_FLESH(FLAT, false),
        SALT_LICK(FLAT, false), // drops salt
        SEAWEED(LONG, false), //todo: this should refer to the Food seaweed
        STICK(FLAT, false);

        private final VoxelShape shape;
        private final boolean hasItem;

        MiscCoverTypes(VoxelShape shape, boolean hasItem)
        {
            this.shape = shape;
            this.hasItem = hasItem;
        }

        public VoxelShape getShape() { return shape; }
        public boolean isHasItem() { return hasItem; }
    }

    public enum RockCoverTypes
    {
        PEBBLE(SMALL, false),
        RUBBLE(FLAT, false),
        BOULDER(MEDIUM, false);

        private final VoxelShape shape;
        private final boolean hasItem;

        RockCoverTypes(VoxelShape shape, boolean hasItem)
        {
            this.shape = shape;
            this.hasItem = hasItem;
        }

        public VoxelShape getShape() { return shape; }
        public boolean isHasItem() { return hasItem; }
    }
}
