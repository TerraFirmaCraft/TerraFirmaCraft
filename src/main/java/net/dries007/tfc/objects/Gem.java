/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.Arrays;
import java.util.Random;

import net.dries007.tfc.util.collections.WeightedCollection;

public enum Gem
{
    AGATE(true),
    AMETHYST(true),
    BERYL(true),
    DIAMOND(false),
    EMERALD(true),
    GARNET(true),
    JADE(true),
    JASPER(true),
    OPAL(true),
    RUBY(true),
    SAPPHIRE(true),
    TOPAZ(true),
    TOURMALINE(true);

    Gem(boolean canDrop)
    {
        this.canDrop = canDrop;
    }

    // whether this gem can be found as a drop from raw stone
    private final boolean canDrop;

    // list of gems that can drop
    private static final Gem[] RANDOM_DROP_GEMS = Arrays.stream(values()).filter(x -> x.canDrop).toArray(Gem[]::new);


    public static Gem getRandomDropGem(Random random)
    {
        return RANDOM_DROP_GEMS[random.nextInt(RANDOM_DROP_GEMS.length)];
    }

    public enum Grade
    {
        CHIPPED(16),
        FLAWED(8),
        NORMAL(4),
        FLAWLESS(2),
        EXQUISITE(1);

        Grade(int dropWeight)
        {
            this.dropWeight = dropWeight;
        }

        // the probability of this gem grade dropping compared to the other grades. higher is more likely.
        private int dropWeight;

        // cache grades statically for in house use
        private static final Grade[] VALUES = values();

        // cache grade weight odds
        private static final WeightedCollection<Grade> GRADE_ODDS = new WeightedCollection<>();

        static
        {
            Arrays.stream(VALUES).forEach((grade) -> GRADE_ODDS.add(grade.dropWeight, grade));
        }

        /**
         * Calculates the chances of a gem dropping from stone with dug
         *
         * @param random Random generator for rolling the odds
         * @return null if no gem, otherwise the grade of the gem that should be dropped
         */
        public static Grade randomGrade(Random random)
        {
            // pick out a gem grade
            return GRADE_ODDS.getRandomEntry(random);
        }

        public static Grade fromMeta(int meta)
        {
            return VALUES[meta];
        }

        public int getMeta()
        {
            return this.ordinal();
        }
    }
}
