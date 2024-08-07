package net.dries007.tfc.common.blocks;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.climate.Climate;

public interface IClimateWeatheringBlock
{
    static float[] AGE_MODIFIERS = {0.2f, 0.5f, 0.7f, 1.0f};

    Block getNext();

    BlockState getNext(BlockState var1);

    Block getPrevious();

    default void applyChangeOverTime(BlockState blockState, ServerLevel serverLevel, BlockPos pos, RandomSource random)
    {
        if (blockState.getBlock() instanceof IClimateWeatheringBlock climateWeatheringBlock && climateWeatheringBlock.getAge() == TFCWeatherState.FULLY_WEATHERED)
        {
            return;
        }

        boolean topExposed = true;

        for (int y = 1; y < 3; y++)
        {
            if (!serverLevel.getBlockState(pos.relative(Direction.UP, y)).isAir())
            {
                topExposed = false;
                break;
            }
        }

        Iterator<BlockPos> var8 = BlockPos.withinManhattan(pos, 4, 4, 4).iterator();
        float count = 0;
        float overall = 0;
        while (var8.hasNext())
        {
            if (serverLevel.getBlockState(var8.next()).getBlock() instanceof IClimateWeatheringBlock weatheringBlock)
            {
                if (weatheringBlock.getAge().ordinal() > this.getAge().ordinal())
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

        float randomFloat = random.nextFloat();

        if (!(serverLevel.getBlockState(pos.below()).getBlock() instanceof IClimateWeatheringBlock) || topExposed)
        {
            float overallChance = getAgeAffectedModifier(this.getAge()) * getClimateAffectedModifier(serverLevel, pos);
            if (this.getAge() != TFCWeatherState.UNAFFECTED)
            {
                overallChance *= neighborModifier;
            }
            if (randomFloat < overallChance && randomFloat < getMaterialModifier())
            {
                serverLevel.setBlockAndUpdate(pos, getNext(blockState));
                return;
            }
        }

        // do "drip" weathering
        float drip = 0;
        for (int y = 1; y < 5; y++)
        {
            if (serverLevel.getBlockState(pos.above(y)).getBlock() instanceof IClimateWeatheringBlock weatheringBlock && weatheringBlock.getAge().ordinal() > this.getAge().ordinal())
            {
                drip += 0.2f;
            }
            else
            {
                break;
            }
        }

        float overallChance = getAgeAffectedModifier(this.getAge()) * drip;
        if (this.getAge() != TFCWeatherState.UNAFFECTED)
        {
            overallChance *= neighborModifier;
        }
        if (randomFloat < overallChance && randomFloat < getMaterialModifier())
        {
            serverLevel.setBlockAndUpdate(pos, getNext(blockState));
            return;
        }
        overallChance = getAgeAffectedModifier(this.getAge()) * 0.006f;
        if (randomFloat < overallChance && randomFloat < getMaterialModifier())
        {
            serverLevel.setBlockAndUpdate(pos, getNext(blockState));
        }
    }

    float getMaterialModifier();

    static float getClimateAffectedModifier(Level level, BlockPos pos)
    {
        float temp = Mth.abs(-0.5f + (Mth.clamp(Climate.getTemperature(level, pos), -40, 40) + 40) / 80f) * 2;
        float rainfall = Mth.clamp(Climate.getRainfall(level, pos), 0, 500) / 500f;
        float windspeed = Mth.clamp(Climate.getWindVector(level, pos).length(), 0, 1);
        float raining = level.isRaining() ? 1 : 0.5f;

        float climate = temp * rainfall; // range 0 - 1
        float weather = raining * windspeed; // range 0 - 1

        return (Mth.clamp(climate, 0, 1) + 0.2f) * 0.6f + (Mth.clamp(weather, 0, 1) + 0.2f) * 0.4f; // weight immediate weather and overall climate
    }

    static float getAgeAffectedModifier(TFCWeatherState weatherState)
    {
        return AGE_MODIFIERS[weatherState.ordinal()];
    }

    TFCWeatherState getAge();

    enum TFCWeatherState
    {
        UNAFFECTED,
        EXPOSED,
        WEATHERED,
        FULLY_WEATHERED;

        static TFCWeatherState getByOrdinal(int ordinal)
        {
            ordinal = Mth.clamp(ordinal, 0, 3);
            return TFCWeatherState.values()[ordinal];
        }

        boolean hasNext()
        {
            return this.ordinal() < 3;
        }

        boolean hasPrevious()
        {
            return this.ordinal() > 0;
        }

        TFCWeatherState getNext()
        {
            return getByOrdinal(this.ordinal() + 1);
        }

        TFCWeatherState getPrevious()
        {
            return getByOrdinal(this.ordinal() - 1);
        }
    }


}
