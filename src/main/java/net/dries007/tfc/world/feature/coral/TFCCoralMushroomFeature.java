/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;

public class TFCCoralMushroomFeature extends TFCCoralFeature
{
    public TFCCoralMushroomFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    protected boolean placeFeature(IWorld world, Random rand, BlockPos pos, BlockState state)
    {
        int i = rand.nextInt(3) + 3;
        int j = rand.nextInt(3) + 3;
        int k = rand.nextInt(3) + 3;
        int l = rand.nextInt(3) + 1;
        BlockPos.Mutable mutablePos = pos.toMutable();

        for (int x = 0; x <= j; ++x)
        {
            for (int y = 0; y <= i; ++y)
            {
                for (int z = 0; z <= k; ++z)
                {
                    mutablePos.setPos(x + pos.getX(), y + pos.getY(), z + pos.getZ());
                    mutablePos.move(Direction.DOWN, l);
                    if ((x != 0 && x != j || y != 0 && y != i) && (z != 0 && z != k || y != 0 && y != i) && (x != 0 && x != j || z != 0 && z != k) && (x == 0 || x == j || y == 0 || y == i || z == 0 || z == k) && !(rand.nextFloat() < 0.1F) && !placeCoralBlock(world, rand, mutablePos, state))
                    {
                        //mojang magic
                    }
                }
            }
        }

        return true;
    }
}
