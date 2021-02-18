package net.dries007.tfc.common.blocks.fruit_tree;

public class FruitTree
{
    public FruitTree()
    {

    }

    public enum Default
    {
        BANANA(new FruitTree()),
        CHERRY(new FruitTree()),
        GREEN_APPLE(new FruitTree()),
        LEMON(new FruitTree()),
        OLIVE(new FruitTree()),
        ORANGE(new FruitTree()),
        PEACH(new FruitTree()),
        PLUM(new FruitTree()),
        RED_APPLE(new FruitTree());

        private final FruitTree fruitTree;

        Default(FruitTree fruitTree)
        {
            this.fruitTree = fruitTree;
        }

        public FruitTree getFruitTree()
        {
            return fruitTree;
        }
    }
}
