package net.dries007.tfc.common.blocks.berry_bush;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import net.dries007.tfc.common.items.TFCItems;

import static net.dries007.tfc.common.blocks.berry_bush.AbstractBerryBushBlock.Lifecycle.*;

public class BerryBush
{
    public static final BerryBush NOOP = new BerryBush(Type.SPREADING, () -> Items.AIR, 0, 0, 0, 0, new SpreadingBushBlock.Lifecycle[] {}, 0, 0);

    private final Type type;
    private final Supplier<? extends Item> berry;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final SpreadingBushBlock.Lifecycle[] stages;
    private final int maxHeight;
    private final int deathFactor;

    public BerryBush(Type type, Supplier<? extends Item> berry, float minTemp, float maxTemp, float minRain, float maxRain, SpreadingBushBlock.Lifecycle[] stages, int maxHeight, int deathFactor)
    {
        this.type = type;
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

    private Type getType()
    {
        return type;
    }

    public enum Default
    {
        BLACKBERRY(new BerryBush(Type.SPREADING, TFCItems.BERRIES.get(Berry.RASPBERRY), 7f, 20f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 4, 7)),
        RASPBERRY(new BerryBush(Type.SPREADING, TFCItems.BERRIES.get(Berry.RASPBERRY), 5f, 20f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 3, 9)),
        BLUEBERRY(new BerryBush(Type.SPREADING, TFCItems.BERRIES.get(Berry.BLUEBERRY), 7f, 25f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 2, 13)),
        ELDERBERRY(new BerryBush(Type.SPREADING, TFCItems.BERRIES.get(Berry.ELDERBERRY), 10f, 29f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 3, 9)),
        GOOSEBERRY(new BerryBush(Type.STATIONARY, TFCItems.BERRIES.get(Berry.GOOSEBERRY), 5f, 27f, 100f, 400f, new SpreadingBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 0, 9));

        private final BerryBush bush;

        Default(BerryBush bush)
        {
            this.bush = bush;
        }

        public BerryBush getBush()
        {
            return bush;
        }

        public boolean isSpreading()
        {
            return getBush().getType() == Type.SPREADING;
        }

        public boolean isStationary()
        {
            return getBush().getType() == Type.STATIONARY;
        }
    }

    private enum Type
    {
        SPREADING, STATIONARY
    }
}
