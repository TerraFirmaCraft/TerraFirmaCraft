package net.dries007.tfc.common.blocks.fruittree;

import net.dries007.tfc.common.blocks.berrybush.AbstractBerryBushBlock;
import net.dries007.tfc.common.blocks.berrybush.BerryBush;
import net.dries007.tfc.common.items.TFCItems;

import static net.dries007.tfc.common.blocks.berrybush.AbstractBerryBushBlock.Lifecycle.*;

public class FruitTree
{
    private final BerryBush base;
    private final int saplingDays;

    public FruitTree(BerryBush base, int saplingDays)
    {
        this.base = base;
        this.saplingDays = saplingDays;
    }

    public BerryBush getBase()
    {
        return base;
    }

    public int getSaplingDays()
    {
        return saplingDays;
    }

    public enum Default
    {
        CHERRY(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.CHERRY), 5f, 21f, 100f, 350f, new AbstractBerryBushBlock.Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY}, 8, 8), 8)),
        GREEN_APPLE(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.GREEN_APPLE), 8f, 25f, 110f, 280f, new AbstractBerryBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}, 8, 8), 8)),
        LEMON(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.LEMON), 10f, 30f, 180f, 470f, new AbstractBerryBushBlock.Lifecycle[] {DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT}, 8, 8), 8)),
        OLIVE(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.OLIVE), 13f, 30f, 150f, 380f, new AbstractBerryBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}, 8, 8), 8)),
        ORANGE(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.ORANGE), 23f, 36f, 250f, 480f, new AbstractBerryBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 8, 8), 8)),
        PEACH(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.PEACH), 9f, 27f, 60f, 230f, new AbstractBerryBushBlock.Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY}, 8, 8), 8)),
        PLUM(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.PLUM), 18f, 31f, 250f, 400f, new AbstractBerryBushBlock.Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT}, 8, 8), 8)),
        RED_APPLE(new FruitTree(new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.RED_APPLE), 9f, 25f, 100f, 280f, new AbstractBerryBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}, 8, 8), 8));

        private final FruitTree fruitTree;

        Default(FruitTree fruitTree)
        {
            this.fruitTree = fruitTree;
        }

        public FruitTree getFruitTree()
        {
            return fruitTree;
        }

        public BerryBush baseInfo()
        {
            return fruitTree.getBase();
        }
    }
}
