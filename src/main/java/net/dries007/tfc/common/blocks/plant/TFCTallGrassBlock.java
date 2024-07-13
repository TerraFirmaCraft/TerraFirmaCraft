/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class TFCTallGrassBlock extends ShortGrassBlock implements ITallPlant
{
    protected static final EnumProperty<Part> PART = TFCBlockStateProperties.TALL_PLANT_PART;
    protected static final VoxelShape PLANT_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHORTER_PLANT_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    public static TFCTallGrassBlock create(RegistryPlant plant, ExtendedProperties properties)
    {
        return new TFCTallGrassBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected TFCTallGrassBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(stateDefinition.any().setValue(PART, Part.LOWER));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        super.randomTick(state, level, pos, random);
        if (PlantRegrowth.canSpread(level, random) && state.getValue(PART) == Part.LOWER)
        {
            final BlockPos newPos = PlantRegrowth.spreadSelf(state, level, pos, random, 2, 2, 4);
            if (newPos != null && PlantRegrowth.DEFAULT_PLACEMENT_TEST.test(level.getBlockState(newPos.above()), newPos.above()))
            {
                placeTwoHalves(level, newPos, 2, random);
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        Part part = state.getValue(PART);
        if (facing.getAxis() != Direction.Axis.Y || part == Part.LOWER != (facing == Direction.UP) || facingState.getBlock() == this && facingState.getValue(PART) != part)
        {
            return part == Part.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
        else
        {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        if (state.getValue(PART) == Part.LOWER)
        {
            return super.canSurvive(state, level, pos);
        }
        else
        {
            BlockState blockstate = level.getBlockState(pos.below());
            if (state.getBlock() != this)
            {
                return super.canSurvive(state, level, pos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return blockstate.getBlock() == this && blockstate.getValue(PART) == Part.LOWER;
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos pos = context.getClickedPos();
        return pos.getY() < context.getLevel().getMaxBuildHeight() - 1 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context) ? super.getStateForPlacement(context) : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(PART));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.setBlockAndUpdate(pos.above(), defaultBlockState().setValue(PART, Part.UPPER));
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if (!level.isClientSide)
        {
            if (player.isCreative())
            {
                if (state.getValue(PART) == Part.UPPER)
                {
                    BlockPos blockpos = pos.below();
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(PART) == Part.LOWER)
                    {
                        level.setBlock(blockpos, level.getFluidState(blockpos).createLegacyBlock(), 35);
                        level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                    }
                }
            }
            else
            {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }
        return state;
    }

    /**
     * See {@link net.minecraft.world.level.block.DoublePlantBlock}. We handle drops in playerWillDestroy so we must not drop things here.
     */
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity tile, ItemStack stack)
    {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), tile, stack);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        Part part = state.getValue(PART);
        if (part == Part.LOWER)
            return PLANT_SHAPE;
        return SHORTER_PLANT_SHAPE;
    }

    public void placeTwoHalves(LevelAccessor world, BlockPos pos, int flags, RandomSource random)
    {
        int age = random.nextInt(3) + 1;
        world.setBlock(pos, updateStateWithCurrentMonth(defaultBlockState().setValue(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER).setValue(TFCBlockStateProperties.AGE_3, age)), flags);
        world.setBlock(pos.above(), updateStateWithCurrentMonth(defaultBlockState().setValue(TFCBlockStateProperties.TALL_PLANT_PART, Part.UPPER).setValue(TFCBlockStateProperties.AGE_3, age)), flags);
    }
}
