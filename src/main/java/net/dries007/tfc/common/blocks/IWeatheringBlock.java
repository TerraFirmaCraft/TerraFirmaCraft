/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.DataMapHooks;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;

public interface IWeatheringBlock
{
    /**
     * @return The current age of this block.
     */
    Age getAge();

    /**
     * @return A value between {@code [0, 1]} which indicates how susceptible this block is to weathering.
     */
    default float ageModifier()
    {
        return getAge() == Age.NONE ? 0.5f : 1.0f;
    }

    /**
     * @return A value between {@code [0, 1]} which indicates how resistant this block is to weathering.
     * This is independent of other modifiers.
     */
    float weatheringResistance();

    /**
     * Called from random tick, for a block that experiences time-related weathering.
     */
    default void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (getAge() == Age.OXIDIZED || random.nextFloat() < 0.015f)
        {
            return;
        }

        // Designed to slow weathering after most blocks are exposed
        float count = 0;
        float overall = 0;
        for (BlockPos next : BlockPos.withinManhattan(pos, 4, 4, 4))
        {
            if (level.getBlockState(next).getBlock() instanceof IWeatheringBlock weatheringBlock)
            {
                if (weatheringBlock.getAge().ordinal() > getAge().ordinal())
                {
                    count++;
                }
                overall++;
            }
        }

        float neighborModifier = 1;
        if ((count) / (overall) > 0.4f)
        {
            neighborModifier = 0.005f;
        }

        final float chance = random.nextFloat();

        // do climate-affected weathering when blocks are exposed to > 5 blocks of air
        boolean topExposed = true;
        for (int y = 1; y < 5; y++)
        {
            if (!level.getBlockState(pos.above(y)).getCollisionShape(level, pos.above(y)).isEmpty())
            {
                topExposed = false;
                break;
            }
        }

        if (topExposed)
        {
            float overallChance = ageModifier() * climateModifier(level, pos);
            if (getAge() != Age.NONE)
            {
                overallChance *= neighborModifier;
            }
            if (chance < overallChance && chance > weatheringResistance())
            {
                changeToNextState(level, pos, state);
                return;
            }
        }

        // do "drip" weathering
        int adjacentCount = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList())
        {
            if (level.getBlockState(pos.relative(direction)).getBlock() instanceof IWeatheringBlock)
            {
                adjacentCount++;
            }
        }
        if (adjacentCount <= 2)
        {
            float drip = 0;
            for (int y = 1; y < 5; y++)
            {
                if (level.getBlockState(pos.above(y)).getBlock() instanceof IWeatheringBlock weatheringBlock && weatheringBlock.getAge().ordinal() > getAge().ordinal())
                {
                    drip += 0.2f;
                }
                else
                {
                    break;
                }
            }
            float overallChance = ageModifier() * drip;
            if (getAge() != Age.NONE)
            {
                overallChance *= neighborModifier;
            }
            if (chance < overallChance && chance > weatheringResistance())
            {
                changeToNextState(level, pos, state);
                return;
            }
        }

        // do slow random weathering
        float overallChance = ageModifier() * 0.006f;
        if (chance < overallChance && chance > weatheringResistance())
        {
            changeToNextState(level, pos, state);
        }
    }

    private float climateModifier(Level level, BlockPos pos)
    {
        final ClimateModel model = Climate.get(level);

        float temp = Mth.abs(-0.5f + (Mth.clamp(model.getTemperature(level, pos), -40, 40) + 40) / 80f) * 2;
        float rainfall = Mth.clamp(model.getAverageRainfall(level, pos), 0, 500) / 500f;
        float wind = Mth.clamp(model.getWind(level, pos).length(), 0, 1);
        float raining = level.isRainingAt(pos) ? 1 : 0.5f;

        float climate = temp * rainfall; // range 0 - 1
        float weather = raining * wind; // range 0 - 1

        return (Mth.clamp(climate, 0, 1) + 0.2f) * 0.6f + (Mth.clamp(weather, 0, 1) + 0.2f) * 0.4f; // weight immediate weather and overall climate
    }

    private void changeToNextState(ServerLevel level, BlockPos pos, BlockState state)
    {
        final Block block = DataMapHooks.getNextOxidizedStage(state.getBlock());
        if (block != null)
        {
            final BlockState next = Helpers.copyProperties(block.defaultBlockState(), state);
            level.setBlockAndUpdate(pos, next);
        }
    }

    enum Age
    {
        NONE, EXPOSED, WEATHERED, OXIDIZED;
    }
}
