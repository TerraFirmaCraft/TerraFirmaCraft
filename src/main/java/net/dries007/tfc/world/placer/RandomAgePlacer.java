/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class RandomAgePlacer extends BlockPlacer
{
    public static final Codec<RandomAgePlacer> CODEC = Codec.unit(new RandomAgePlacer());

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState state, Random random)
    {
        // todo: make this respect other properties and values? via config somehow. possibly just use the tfc 'age' property and provide a max int for how many stages?
        if (state.hasProperty(TFCBlockStateProperties.AGE_3))
        {
            state = state.setValue(TFCBlockStateProperties.AGE_3, random.nextInt(4));
        }
        worldIn.setBlock(pos, state, 2);
    }

    @Override
    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.RANDOM_AGE.get();
    }
}
