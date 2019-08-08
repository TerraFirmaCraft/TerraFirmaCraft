/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.Random;

import net.dries007.tfc.ConfigTFC;

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
        private static final int TOTAL_WEIGHT = Arrays.stream(Grade.values()).mapToInt(g -> g.dropWeight).sum();

        // chance of any gem dropping is determined by the general configs.
        private static double dropChance = ConfigTFC.GENERAL.stoneGemDropChance;

        /**
         * Calculates the chances of a gem dropping from stone with dug
         *
         * @param random Random generator for rolling the odds
         * @return null if no gem, otherwise the grade of the gem that should be dropped
         */
        public static Grade randomGrade(Random random)
        {
            double roll = random.nextDouble();

            // roll must first pass the drop chance odds
            if(roll < dropChance)
            {
                // divide by limiting config to bring into range of 0-1
                roll /= dropChance;
                // multiply by total drop weight since we're comparing ints now
                roll *= totalWeight;

                // subtract by each grade's weight until the roll passes 0.
                for (Grade grade : Grade.values())
                {
                    roll -= grade.dropWeight;
                    if (roll < 0)
                        return grade;
                }
            }

            // if roll did not pass the first check, return null, because no gem should drop.
            return null;
        }

        public static Grade fromMeta(int meta)
        {
            return values()[meta];
        }

        public int getMeta()
        {
            return this.ordinal();
        }
    }
}
