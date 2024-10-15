/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class GroundcoverBlock extends ExtendedBlock implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    public static final VoxelShape FLAT = box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    public static final VoxelShape SMALL = box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    public static final VoxelShape MEDIUM = box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);
    public static final VoxelShape PIXEL_HIGH = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public static final VoxelShape TWIG = box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);

    public static GroundcoverBlock twig(ExtendedProperties properties)
    {
        return new GroundcoverBlock(properties.flammable(60, 30), TWIG);
    }

    public static GroundcoverBlock looseOre(BlockBehaviour.Properties properties)
    {
        return new GroundcoverBlock(ExtendedProperties.of(properties), SMALL);
    }

    private final VoxelShape shape;

    public GroundcoverBlock(GroundcoverBlockType cover)
    {
        this(ExtendedProperties.of(MapColor.PLANT).strength(0.05F, 0.0F).sound(SoundType.NETHER_WART).noCollission().pushReaction(PushReaction.DESTROY).cloneItem(cover.getVanillaItem()), cover.getShape());
    }

    public GroundcoverBlock(ExtendedProperties properties, VoxelShape shape)
    {
        super(properties);

        this.shape = shape;

        registerDefaultState(getStateDefinition().any().setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
    {
        if (fluid instanceof FlowingFluid && !getFluidProperty().canContain(fluid))
        {
            return true;
        }
        return IFluidLoggable.super.canPlaceLiquid(player, level, pos, state, fluid);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        if (fluidStateIn.getType() instanceof FlowingFluid && !getFluidProperty().canContain(fluidStateIn.getType()))
        {
            level.destroyBlock(pos, true);
            level.setBlock(pos, fluidStateIn.createLegacyBlock(), 2);
            return true;
        }
        return IFluidLoggable.super.placeLiquid(level, pos, state, fluidStateIn);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = defaultBlockState();
        if (getFluidProperty().canContain(fluidState.getType()))
        {
            return state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(getFluidProperty());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return state.canSurvive(level, currentPos) ? super.updateShape(state, facing, facingState, level, currentPos, facingPos) : state.getFluidState().createLegacyBlock();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!player.isCreative() && level instanceof ServerLevel serverLevel)
        {
            final BlockEntity entity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            getDrops(state, serverLevel, pos, entity, null, ItemStack.EMPTY).forEach(stackToSpawn -> ItemHandlerHelper.giveItemToPlayer(player, stackToSpawn));
        }
        level.removeBlock(pos, false);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        return level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shape;
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }
}
