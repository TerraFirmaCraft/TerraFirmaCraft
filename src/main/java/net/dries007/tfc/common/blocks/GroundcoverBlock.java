package net.dries007.tfc.common.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    protected static final VoxelShape FLAT = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    protected static final VoxelShape FLAT_TALL = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D);
    protected static final VoxelShape SMALL = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    protected static final VoxelShape MEDIUM = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);
    protected static final VoxelShape LARGE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D);
    protected static final VoxelShape PIXEL_HIGH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public VoxelShape shape;

    public GroundcoverBlock(MiscCoverTypes cover)
    {
        super(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_WART).noOcclusion());
        shape = cover.getShape();
        this.registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(FACING, Direction.EAST));
    }

    public GroundcoverBlock(RockCoverTypes cover)
    {
        super(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.STONE).noOcclusion());
        shape = cover.getShape();
        this.registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(FACING, Direction.EAST));
    }

    public GroundcoverBlock(WoodCoverTypes cover)
    {
        super(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.SCAFFOLDING).noOcclusion());
        shape = cover.getShape();
        this.registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(FACING, Direction.EAST));
    }

    public GroundcoverBlock() // used for nuggets
    {
        super(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noOcclusion());
        shape = SMALL;
        this.registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(FACING, Direction.EAST));
    }

    public GroundcoverBlock(Properties properties) { super(properties); } // used for leaf constructor

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.is(FluidTags.WATER) && fluidstate.isSource();
        return super.getStateForPlacement(context).setValue(WATERLOGGED, flag).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(worldIn, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else
        {
            if (stateIn.getValue(WATERLOGGED))
            {
                worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
            }
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.getMainHandItem() == ItemStack.EMPTY)
        {
            worldIn.destroyBlock(pos, (!player.isCreative()));
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED, FACING);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if(!world.isClientSide())
        {
            FluidState fluidstate = world.getFluidState(pos);
            world.setBlock(pos, state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER), 1);
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
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public enum MiscCoverTypes //todo: finish these loot tables as items get added
    {
        BONES(MEDIUM, false), // drops bones
        BRANCH(FLAT, false), // drops sticks
        CLAM(SMALL, true), // flux
        DEAD_GRASS(PIXEL_HIGH, false), // drops straw
        DRIFTWOOD(FLAT, false), // drops sticks
        FEATHER(FLAT, false),
        FLINT(SMALL, false),
        GUANO(SMALL, true), // guano is traditionally a fertilizer
        MOLLUSK(SMALL, true), // flux
        MUSSEL(SMALL, true), // flux
        PINECONE(SMALL, false), // drops something useful for compost?
        PODZOL(PIXEL_HIGH, false), // drops something useful for compost?
        ROTTEN_FLESH(FLAT, false),
        SALT_LICK(PIXEL_HIGH, false), // drops salt
        SEAWEED(FLAT, false), // drops seaweed (tbd)
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
        PEBBLE(SMALL),
        RUBBLE(FLAT),
        BOULDER(MEDIUM);

        private final VoxelShape shape;

        RockCoverTypes(VoxelShape shape) { this.shape = shape; }

        public VoxelShape getShape() { return shape; }
    }

    public enum WoodCoverTypes
    {
        FALLEN_LOG(FLAT_TALL),
        FALLEN_TWIG(FLAT),
        STUMP(LARGE);

        private final VoxelShape shape;

        WoodCoverTypes(VoxelShape shape)
        {
            this.shape = shape;
        }

        public VoxelShape getShape() { return shape; }
    }
}
