/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.util.Cache;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Common Config
 * - not synced, saved per instance
 * - use for things that are only important server side (i.e. world gen), or make less sense to have per-world.
 */
public class CommonConfig extends CachingConfig
{
    // General
    public final Cache.Int defaultMonthLength;
    public final Cache.Boolean enableDevTweaks;
    public final Cache.Boolean setTFCWorldTypeAsDefault;

    CommonConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.general." + name);

        innerBuilder.push("general");

        defaultMonthLength = wrap(builder.apply("defaultMonthLength").defineInRange("defaultMonthLength", 8, 1, Integer.MAX_VALUE));
        enableDevTweaks = wrap(builder.apply("enableDevTweaks").comment(
            "This enables a series of quality of life logging improvements aimed at mod or pack development. It has no end user or gameplay effect.",
            "This currently enables the following tweaks:",
            " - Enables a [Possible DFU FU] log message, which attempts to catch errors due to incorrect world generation data packs. This does produce false errors!",
            " - Improves and shortens the error message for invalid loot tables.",
            " - Improves and shortens the error message for invalid recipes.",
            " - Fixes MC-190122 (Makes the 'Loaded Recipes' log message accurate)"
        ).define("enableDevTweaks", true));

        setTFCWorldTypeAsDefault = wrap(builder.apply("setTFCWorldTypeAsDefault").comment(
            "If the TFC world type (tfc:tng) should be set as the default world generation.",
            "1. This ONLY sets the corresponding config option in Forge's config.",
            "2. This ONLY will set the default if it was set to 'default' (or vanilla generation)",
            "3. This DOES NOT guarantee that the world generation will be TFC, if another mod sets the default another way"
        ).define("setTFCWorldTypeAsDefault", true));

        innerBuilder.pop();
    }
}