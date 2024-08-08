package net.dries007.tfc.common.blocks;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.climate.Climate;

public interface IClimateWeatheringBlock
{
    float[] AGE_MODIFIERS = {0.5f, 1.0f, 1.0f, 1.0f};

    Block getNext();

    BlockState getNext(BlockState state);

    BlockState getPrevious(BlockState state);

    Block getPrevious();

    default void applyChangeOverTime(BlockState blockState, ServerLevel serverLevel, BlockPos pos, RandomSource random)
    {
        if (blockState.getBlock() instanceof IClimateWeatheringBlock climateWeatheringBlock && climateWeatheringBlock.getAge() == TFCWeatherState.FULLY_WEATHERED)
        {
            return;
        }

        // designed to slow weathering after most blocks are exposed
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

        // do climate-affected weathering when blocks are exposed to > 5 blocks of air
        boolean topExposed = true;
        for (int y = 1; y < 5; y++)
        {
            if (!serverLevel.getBlockState(pos.above(y)).getCollisionShape(serverLevel, pos.above(y)).isEmpty())
            {
                topExposed = false;
                break;
            }
        }

        if (topExposed)
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
        int adjacentCount = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList())
        {
            if (serverLevel.getBlockState(pos.relative(direction)).getBlock() instanceof IClimateWeatheringBlock)
            {
                adjacentCount++;
            }
        }
        if (adjacentCount <= 2)
        {
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
        }

        // do slow random weathering
        float overallChance = getAgeAffectedModifier(this.getAge()) * 0.006f;
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

        public boolean hasNext()
        {
            return this.ordinal() < 3;
        }

        public boolean hasPrevious()
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
