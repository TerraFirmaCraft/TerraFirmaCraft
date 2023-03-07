/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blocks.crop.WildSpreadingCropBlock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.world.feature.BlockConfig;

public class SpreadingCropFeature extends Feature<BlockConfig<WildSpreadingCropBlock>>
{
    public static final Codec<BlockConfig<WildSpreadingCropBlock>> CODEC = BlockConfig.codec(b -> b instanceof WildSpreadingCropBlock t ? t : null, "Must be a " + WildSpreadingCropBlock.class.getSimpleName());

    public SpreadingCropFeature(Codec<BlockConfig<WildSpreadingCropBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<WildSpreadingCropBlock>> context)
    {
        final WildSpreadingCropBlock block = context.config().block();
        final WorldGenLevel level = context.level();
        final BlockPos origin = context.origin();
        final BlockPos offsetPos = origin.relative(Direction.Plane.HORIZONTAL.getRandomDirection(context.random()));

        setBlock(level, origin, block.defaultBlockState());
        final BlockPos below = offsetPos.below();
        if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP) && EnvironmentHelpers.isWorldgenReplaceable(level, offsetPos))
        {
            setBlock(level, offsetPos, block.getFruit().defaultBlockState());
            if (level.getBlockEntity(offsetPos) instanceof DecayingBlockEntity decaying)
            {
                final ItemStack food = new ItemStack(block.getFruit());
                FoodCapability.applyTrait(food, FoodTraits.WILD);
                decaying.setStack(food);
            }
        }
        return true;
    }
}
