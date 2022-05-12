/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import org.jetbrains.annotations.Nullable;

public class TFCLightBlock extends Block implements IFluidLoggable
{
    public static final int MAX_LEVEL = 15;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    public TFCLightBlock(BlockBehaviour.Properties properties)
    {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(LEVEL, MAX_LEVEL).setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        CompoundTag tag = context.getItemInHand().getTag();
        if (tag != null && tag.contains("level", Tag.TAG_INT))
        {
            state = state.setValue(LEVEL, tag.getInt("level"));
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        if (rand.nextInt(20) == 0)
        {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL, FLUID);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (!player.isCreative())
        {
            if (!level.isClientSide)
            {
                level.setBlock(pos, state.cycle(LEVEL), 2);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return ctx.isHoldingItem(asItem()) ? Shapes.block() : Shapes.empty();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 1.0F;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState faceState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return super.updateShape(state, facing, faceState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state)
    {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("level", state.getValue(LEVEL));
        return stack;
    }

}
