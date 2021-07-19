package net.dries007.tfc.common.types;

public class KnappingType
{
    private final int amountToConsume;
    private final boolean consumeAfterComplete;

    public KnappingType(int amountToConsume, boolean consumeAfterComplete)
    {
        this.amountToConsume = amountToConsume;
        this.consumeAfterComplete = consumeAfterComplete;
    }

    /**
     * How many of the required item this will consume
     * The knapping source (whatever opens the GUI) should check this before starting the recipe
     */
    public int getAmountToConsume()
    {
        return amountToConsume;
    }

    /**
     * If true, the recipe will only consume it's contents after the player removes the item from the knapping GUI, or closes the GUI (dropping the item into their inventory)
     * If false, the recipe will consume one ingredient as soon as a single square is removed from the knapping grid
     */
    public boolean consumeAfterComplete()
    {
        return consumeAfterComplete;
    }
}
