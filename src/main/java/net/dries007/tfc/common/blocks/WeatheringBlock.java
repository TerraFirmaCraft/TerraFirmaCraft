/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringBlock extends Block implements IWeatheringBlock
{
    private final Age age;
    private final float weatheringResistance;

    public WeatheringBlock(Properties properties, Age age, float weatheringResistance)
    {
        super(age == Age.OXIDIZED ? properties : properties.randomTicks());
        this.age = age;
        this.weatheringResistance = weatheringResistance;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        onRandomTick(state, level, pos, random);
    }

    @Override
    public float weatheringResistance()
    {
        return weatheringResistance;
    }

    @Override
    public Age getAge()
    {
        return age;
    }
}
