/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.FruitTreeLeavesBlockEntity;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class FruitTreeLeavesBlock extends SeasonalPlantBlock implements IForgeBlockExtension, ILeavesBlock
{
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    public FruitTreeLeavesBlock(ForgeBlockProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages)
    {
        super(properties, productItem, stages);

        registerDefaultState(getStateDefinition().any().setValue(PERSISTENT, false).setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return Shapes.block();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random)
    {
        FruitTreeLeavesBlockEntity te = Helpers.getBlockEntity(world, pos, FruitTreeLeavesBlockEntity.class);
        if (te == null) return;

        Lifecycle old = state.getValue(LIFECYCLE); // have to put this in random tick to capture the old state
        if (old == Lifecycle.FLOWERING || old == Lifecycle.FRUITING)
        {
            if (!te.isOnYear() && te.isGrowing() && old == Lifecycle.FLOWERING && super.updateLifecycle(te) == Lifecycle.FRUITING)
            {
                te.addDeath();
                int probability = Mth.clamp(te.getDeath(), 2, 10);
                if (random.nextInt(probability) == 0)
                {
                    te.setOnYear(true);
                }
            }
        }
        else
        {
            te.setOnYear(false); // reset when we're not in season
        }
        super.randomTick(state, world, pos, random);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
        if (TFCConfig.SERVER.enableLeavesSlowEntities.get())
        {
            Helpers.slowEntityInBlock(entityIn, 0.2f, 5);
        }
    }

    public void cycle(BerryBushBlockEntity te, Level world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (te.getDeath() > 10)
        {
            te.setGrowing(false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PERSISTENT);
    }

    protected Lifecycle updateLifecycle(BerryBushBlockEntity te)
    {
        Lifecycle lifecycle = super.updateLifecycle(te);

        FruitTreeLeavesBlockEntity fruityTE = (FruitTreeLeavesBlockEntity) te;
        if (lifecycle == Lifecycle.FRUITING && !fruityTE.isOnYear())
        {
            lifecycle = Lifecycle.HEALTHY;
        }
        return lifecycle;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return !state.getValue(PERSISTENT);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return isValid(worldIn, currentPos, stateIn) ? stateIn : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        return 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        if (!isValid(worldIn, pos, state))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    private boolean isValid(LevelAccessor worldIn, BlockPos pos, BlockState state)
    {
        if (state.getValue(PERSISTENT))
        {
            return true;
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Helpers.DIRECTIONS)
        {
            mutablePos.set(pos).move(direction);
            if (worldIn.getBlockState(mutablePos).is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
            {
                return true;
            }
        }
        return false;
    }
}
