/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TopPlantBlock extends GrowingPlantHeadBlock implements IForgeBlockExtension
{
    private final Supplier<? extends Block> bodyBlock;
    private final ExtendedProperties properties;

    public TopPlantBlock(ExtendedProperties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape)
    {
        super(properties.properties(), direction, shape, false, 0);
        this.bodyBlock = bodyBlock;
        this.properties = properties;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(AGE) < 25 && CommonHooks.canCropGrow(level, pos.relative(growthDirection), level.getBlockState(pos.relative(growthDirection)), random.nextDouble() < TFCConfig.SERVER.plantLongGrowthChance.get()))
        {
            BlockPos blockpos = pos.relative(growthDirection);
            if (canGrowInto(level.getBlockState(blockpos)))
            {
                level.setBlockAndUpdate(blockpos, state.cycle(AGE));
                CommonHooks.fireCropGrowPost(level, blockpos, level.getBlockState(blockpos));
            }
        }
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource source)
    {
        return 0;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(AGE, Mth.nextInt(context.getLevel().getRandom(), 10, 18));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected boolean canGrowInto(BlockState state)
    {
        return NetherVines.isValidGrowthState(state);
    }

    @Override // lifted from AbstractPlantBlock to add leaves to it
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos relPos = pos.relative(growthDirection.getOpposite());
        final BlockState relState = level.getBlockState(relPos);
        final Block block = relState.getBlock();
        if (!canAttachTo(relState))
        {
            return false;
        }
        else
        {
            return block == getHeadBlock() || block == getBodyBlock() || Helpers.isBlock(relState, BlockTags.LEAVES) || relState.isFaceSturdy(level, relPos, growthDirection);
        }
    }

    @Override
    protected Block getBodyBlock()
    {
        return bodyBlock.get();
    }

    @Override
    protected MapCodec<? extends GrowingPlantHeadBlock> codec()
    {
        return fakeBlockCodec();
    }
}
