package net.dries007.tfc.common.blocks.berry_bush;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import net.dries007.tfc.common.items.TFCItems;

import static net.dries007.tfc.common.blocks.berry_bush.SpreadingBushBlock.Lifecycle.*;

public class SpreadingBush
{
    public static final SpreadingBush NOOP = new SpreadingBush(() -> Items.AIR, 0, 0, 0, 0, new SpreadingBushBlock.Lifecycle[] {}, 0, 0);

    private final Supplier<? extends Item> berry;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final SpreadingBushBlock.Lifecycle[] stages;
    private int maxHeight;
    private int deathFactor;

    public SpreadingBush(Supplier<? extends Item> berry, float minTemp, float maxTemp, float minRain, float maxRain, SpreadingBushBlock.Lifecycle[] stages, int maxHeight, int deathFactor)
    {
        this.berry = berry;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.stages = stages;
        this.maxHeight = maxHeight;
        this.deathFactor = deathFactor;
    }

    public Item getBerry()
    {
        return berry.get();
    }

    public boolean isValidConditions(float tempIn, float rainIn)
    {
        return minTemp <= tempIn && maxTemp >= tempIn && minRain <= rainIn && maxRain >= rainIn;
    }

    public int getMaxHeight()
    {
        return maxHeight;
    }

    public int getDeathFactor()
    {
        return deathFactor;
    }


    public SpreadingBushBlock.Lifecycle[] getStages()
    {
        return stages;
    }

    public enum Default
    {
        BLACKBERRY(new SpreadingBush(TFCItems.BERRIES.get(Berry.BLACKBERRY), 7f, 20f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 4, 7)),
        RASPBERRY(new SpreadingBush(TFCItems.BERRIES.get(Berry.RASPBERRY), 5f, 20f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 3, 9)),
        BLUEBERRY(new SpreadingBush(TFCItems.BERRIES.get(Berry.BLUEBERRY), 7f, 25f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 2, 13)),
        ELDERBERRY(new SpreadingBush(TFCItems.BERRIES.get(Berry.ELDERBERRY), 10f, 29f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 3, 9));

        private final SpreadingBush bush;

        Default(SpreadingBush bush)
        {
            this.bush = bush;
        }

        public SpreadingBush getBush()
        {
            return bush;
        }
    }
}
