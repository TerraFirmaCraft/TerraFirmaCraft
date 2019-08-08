/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.Random;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.collections.WeightedCollection;

public enum Gem
{
    AGATE,
    AMETHYST,
    BERYL,
    DIAMOND,
    EMERALD,
    GARNET,
    JADE,
    JASPER,
    OPAL,
    RUBY,
    SAPPHIRE,
    TOPAZ,
    TOURMALINE;

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

        /**
         * Calculates the chances of a gem dropping from stone with dug
         *
         * @param random Random generator for rolling the odds
         * @return null if no gem, otherwise the grade of the gem that should be dropped
         */
        public static Grade randomGrade(Random random)
        {
            double roll = random.nextDouble();
            double dropChance = ConfigTFC.GENERAL.stoneGemDropChance;

            // roll must first pass the drop chance odds
            if(roll < dropChance)
            {
                // Create a weighted collection to handle odds of gem drops for us
                WeightedCollection<Grade> gradeOdds = new WeightedCollection<>();

                // add each grade with its associated drop weight
                for (Grade grade : VALUES)
                    gradeOdds.add(grade.dropWeight, grade);

                // pick out a gem grade
                return gradeOdds.getRandomEntry(random);
            }

            // if roll did not pass the first check, return null, because no gem should drop.
            return null;
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
