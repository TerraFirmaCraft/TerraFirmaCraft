/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.rock;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemTier;

import net.dries007.tfc.api.types.Rock;

public enum RockCategory implements Predicate<Rock>
{
    IGNEOUS_EXTRUSIVE(ItemTier.STONE, true, true, false, true),
    IGNEOUS_INTRUSIVE(ItemTier.STONE, false, true, true, true),
    METAMORPHIC(ItemTier.STONE, true, true, true, false),
    SEDIMENTARY(ItemTier.STONE, true, true, false, false);

    private final IItemTier itemTier;
    private final boolean layer1;
    private final boolean layer2;
    private final boolean layer3;
    private final boolean hasAnvil;

    /**
     * A rock category.
     *
     * @param itemTier The tool material used for stone tools made of this rock
     * @param hasAnvil if this rock should be able to create a stone anvil
     */
    RockCategory(@Nonnull IItemTier itemTier, boolean layer1, boolean layer2, boolean layer3, boolean hasAnvil)
    {
        this.itemTier = itemTier;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.hasAnvil = hasAnvil;
    }

    @Nonnull
    public IItemTier getItemTier()
    {
        return itemTier;
    }

    @Override
    public boolean test(Rock rock)
    {
        return rock.getCategory() == this;
    }

    public boolean hasAnvil()
    {
        return hasAnvil;
    }

    @Override
    public String toString()
    {
        return name().toLowerCase();
    }

    public enum Layer implements Predicate<Rock>
    {
        BOTTOM(3, x -> x.layer3),
        MIDDLE(2, x -> x.layer2),
        TOP(1, x -> x.layer1);

        public final int layer;
        private final Predicate<RockCategory> filter;

        Layer(int layer, Predicate<RockCategory> filter)
        {
            this.layer = layer;
            this.filter = filter;
        }

        @Override
        public boolean test(Rock rock)
        {
            return filter.test(rock.getCategory());
        }
    }
}
