/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Common Config
 * - not synced, saved per instance
 * - use for things that are only important server side (i.e. world gen), or make less sense to have per-world.
 */
public class CommonConfig
{
    private static final Logger LOGGER = LogUtils.getLogger();

    // General
    public final ForgeConfigSpec.BooleanValue setTFCWorldTypeAsDefault;

    // Calendar
    public final ForgeConfigSpec.IntValue defaultMonthLength;
    public final ForgeConfigSpec.IntValue defaultCalendarStartDay;

    // Debug
    private final ForgeConfigSpec.BooleanValue enableNetworkDebugging;
    private boolean hasLoggedNetworkDebugInfoMessage = false;
    public final ForgeConfigSpec.BooleanValue enableDatapackTests;

    CommonConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.general." + name);

        innerBuilder.push("general");

        setTFCWorldTypeAsDefault = builder.apply("setTFCWorldTypeAsDefault").comment(
            " If the TFC world type (tfc:tng) should be set as the default world generation.",
            " 1. This ONLY sets the corresponding config option in Forge's config.",
            " 2. This ONLY will set the default if it was set to 'default' (or vanilla generation)",
            " 3. This DOES NOT guarantee that the world generation will be TFC, if another mod sets the default another way"
        ).define("setTFCWorldTypeAsDefault", true);

        innerBuilder.pop().push("calendar");

        defaultMonthLength = builder.apply("defaultMonthLength").comment(
            " The number of days in a month, for newly created worlds.",
            " This can be modified in existing worlds using the /time command"
        ).defineInRange("defaultMonthLength", 8, 1, Integer.MAX_VALUE);
        defaultCalendarStartDay = builder.apply("defaultCalendarStartDay").comment(
            " The start date for newly created worlds, in a number of ticks, for newly created worlds",
            " This represents a number of days offset from January 1, 1000",
            " The default is (5 * daysInMonth) = 40, which starts at June 1, 1000 (with the default daysInMonth = 8)"
        ).defineInRange("defaultCalendarStartDay", (5 * 8), -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("debug");

        enableNetworkDebugging = builder.apply("enableNetworkDebugging").comment(
            " Enables a series of network fail-safes that are used to debug network connections between client and servers.",
            " Important: this MUST BE THE SAME as what the server has set, otherwise you are liable to see even stranger errors."
        ).define("enableNetworkDebugging", () -> !FMLEnvironment.production);

        enableDatapackTests = builder.apply("enableDatapackTests").comment("If enabled, TFC will validate that certain pieces of reloadable data fit the conditions we expect, for example heating recipes having heatable items. It will error or warn in the log if these conditions are not met.").define("enableDatapackTests", () -> !FMLEnvironment.production);
    }

    public boolean enableNetworkDebugging()
    {
        if (!hasLoggedNetworkDebugInfoMessage)
        {
            hasLoggedNetworkDebugInfoMessage = true;
            LOGGER.info("TFC Network Debugging = {}", enableNetworkDebugging.get());
        }
        return enableNetworkDebugging.get();
    }
}