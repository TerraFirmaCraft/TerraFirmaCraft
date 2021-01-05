/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.util.calendar.Calendar;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Animal Config, handles configuration for all animals
 */
public class AnimalConfig
{
    public final OviparousConfig CHICKEN;

    AnimalConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        innerBuilder.push("chicken");
        CHICKEN = new OviparousConfig(innerBuilder, "chicken", 24 * Calendar.TICKS_IN_DAY, 92 * Calendar.TICKS_IN_DAY, 8 * Calendar.TICKS_IN_DAY, 30_000);
        innerBuilder.pop().pop();
    }

    /**
     * Common for all animals
     */
    public static class CommonAnimalConfig
    {
        public final ForgeConfigSpec.LongValue adulthoodTicks; // Number of ticks to reach adulthood
        public final ForgeConfigSpec.LongValue elderlyTicks; // Number of ticks to reach elderly (after adulthood)

        CommonAnimalConfig(ForgeConfigSpec.Builder innerBuilder, String animalName, long adulthood, long elderly)
        {
            // Standardization for translation keys
            Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.animals." + animalName + "." + name);

            adulthoodTicks = builder.apply("adulthoodTicks").defineInRange("adulthoodTicks", adulthood, 1, Long.MAX_VALUE);
            elderlyTicks = builder.apply("elderlyTicks").defineInRange("elderlyTicks", elderly, 0, Long.MAX_VALUE);
        }
    }

    /**
     * Common config for oviparous livestock (ie: chicken, duck)
     */
    public static class OviparousConfig extends CommonAnimalConfig
    {
        public final ForgeConfigSpec.LongValue hatchTicks; // Number of ticks for eggs
        public final ForgeConfigSpec.LongValue eggTicks; // Number of ticks for egg laying

        OviparousConfig(ForgeConfigSpec.Builder innerBuilder, String animalName, long adulthood, long elderly, long hatch, long egg)
        {
            super(innerBuilder, animalName, adulthood, elderly);
            // Standardization for translation keys
            Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.animals." + animalName + "." + name);

            hatchTicks = builder.apply("hatchTicks").defineInRange("hatchTicks", hatch, 1, Long.MAX_VALUE);
            eggTicks = builder.apply("eggTicks").defineInRange("eggTicks", egg, 1, Long.MAX_VALUE);

        }
    }

    /**
     * Common config for mammal livestock (ie: pig, horse)
     */
    public static class MammalsConfig extends CommonAnimalConfig
    {
        public final ForgeConfigSpec.LongValue gestationTicks; // Number of ticks for gestation
        public final ForgeConfigSpec.IntValue babies; // Number of babies that is born per gestation

        MammalsConfig(ForgeConfigSpec.Builder innerBuilder, String animalName, long adulthood, long elderly, long gestation, int defaultBabies)
        {
            super(innerBuilder, animalName, adulthood, elderly);
            // Standardization for translation keys
            Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.animals." + animalName + "." + name);

            gestationTicks = builder.apply("gestationTicks").defineInRange("gestationTicks", gestation, 1, Long.MAX_VALUE);
            babies = builder.apply("babies").defineInRange("babies", defaultBabies, 1, Integer.MAX_VALUE);
        }
    }

    /**
     * Common config for produce livestock (ie: cow, sheep)
     */
    public static class ProduceConfig extends MammalsConfig
    {
        public final ForgeConfigSpec.LongValue produceTicks; // Number of ticks for produce cooldown (wool growth, milk)

        ProduceConfig(ForgeConfigSpec.Builder innerBuilder, String animalName, long adulthood, long elderly, long gestation, int defaultBabies, long produce)
        {
            super(innerBuilder, animalName, adulthood, elderly, gestation, defaultBabies);
            // Standardization for translation keys
            Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.animals." + animalName + "." + name);

            produceTicks = builder.apply("produceTicks").defineInRange("produceTicks", produce, 1, Long.MAX_VALUE);
        }
    }
}
