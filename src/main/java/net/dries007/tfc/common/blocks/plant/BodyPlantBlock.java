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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class BodyPlantBlock extends GrowingPlantBodyBlock implements IForgeBlockExtension
{

    public static final VoxelShape BODY_SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    public static final VoxelShape THIN_BODY_SHAPE = box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    public static final VoxelShape WEEPING_SHAPE = box(4.0D, 9.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    public static final VoxelShape TWISTING_SHAPE = box(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);
    public static final VoxelShape TWISTING_THIN_SHAPE = box(5.0D, 0.0D, 5.0D, 11.0D, 12.0D, 11.0D);

    private final Supplier<? extends Block> headBlock;
    private final ExtendedProperties properties;

    public BodyPlantBlock(ExtendedProperties properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties.properties().dynamicShape().offsetType(OffsetType.XZ), direction, shape, true);
        this.headBlock = headBlock;
        this.properties = properties;
    }

    @Override // lifted from AbstractPlantBlock to add leaves to it
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos blockpos = pos.relative(growthDirection.getOpposite());
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!canAttachTo(blockstate))
        {
            return false;
        }
        else
        {
            return block == getHeadBlock() || block == getBodyBlock() || Helpers.isBlock(blockstate, BlockTags.LEAVES) || blockstate.isFaceSturdy(level, blockpos, growthDirection);
        }
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock()
    {
        return (GrowingPlantHeadBlock) headBlock.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return new ItemStack(getHeadBlock());
    }

    @Override
    protected MapCodec<? extends GrowingPlantBodyBlock> codec()
    {
        return fakeBlockCodec();
    }
}
