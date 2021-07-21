/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Client Config
 * - not synced, only loaded client side
 * - only use for PURELY AESTHETIC options
 */
public class ClientConfig
{
    // General
    public final ForgeConfigSpec.BooleanValue ignoreExperimentalWorldGenWarning;
    public final ForgeConfigSpec.BooleanValue assumeTFCWorld;
    public final ForgeConfigSpec.BooleanValue enableDebug;
    // Display
    public final ForgeConfigSpec.BooleanValue enableHungerBar;
    public final ForgeConfigSpec.BooleanValue enableHealthBar;
    public final ForgeConfigSpec.BooleanValue enableThirstBar;
    public final ForgeConfigSpec.ConfigValue<HealthDisplayStyle> healthDisplayStyle;
    public final ForgeConfigSpec.ConfigValue<FoodExpiryTooltipStyle> foodExpiryTooltipStyle;
    public final ForgeConfigSpec.BooleanValue enableTFCF3Overlays;

    ClientConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("general");

        ignoreExperimentalWorldGenWarning = builder.apply("ignoreExperimentalWorldGenWarning").comment("Should TFC forcefully skip the 'Experimental World Generation' warning screen when creating or loading a world?").define("ignoreExperimentalWorldGenWarning", true);

        assumeTFCWorld = builder.apply("assumeTFCWorld").comment(
            "This will assume in several places, that the world is a TFC world, and modify rendering appropriately. This affects the following changes:",
            "1. (Requires a world restart) Cloud height is moved from 160 -> 210",
            "2. The 'horizon height' (where the fog changes from sky to black) is moved from 63 -> 96"
        ).define("assumeTFCWorld", true);

        enableDebug = builder.apply("enableDebugNBTTooltip").comment("Enables a series of additional debugging tooltips, displayed information, and logging.").define("enableDebugNBTTooltip", () -> !FMLEnvironment.production);

        innerBuilder.pop().push("display");

        enableHungerBar = builder.apply("enableHungerBar").comment("Replace the vanilla hunger bar with a TFC one.").define("enableHungerBar", true);
        enableHealthBar = builder.apply("enableHealthBar").comment("Replaces the vanilla health bar with a TFC one.").define("enableHealthBar", true);
        enableThirstBar = builder.apply("enableThirstBar").comment("Adds a TFC thirst bar over the hotbar.").define("enableThirstBar", true);
        healthDisplayStyle = builder.apply("healthDisplayStyle").comment(
            "Health display format. This affects what number is displayed on top of the tfc-style health bar",
            "TFC - e.g. 750 / 1000",
            "VANILLA - e.g. 15.0 / 20.0",
            "TFC_CURRENT - e.g. 750",
            "VANILLA_CURRENT - e.g. 15.0"
        ).defineEnum("healthDisplayStyle", HealthDisplayStyle.TFC);
        foodExpiryTooltipStyle = builder.apply("foodExpiryTooltipStyle").comment(
            "Food expiry tooltip display style. This affects what information is shown on the food item stack tooltips.",
            "NONE - Shows nothing. Maximum mystery!",
            "EXPIRY - e.g. 'Expires on June 3, 05:00",
            "TIME_LEFT - e.g. 'Expires in about 3 day(s)",
            "BOTH - Shows both of the above, e.g. Expires on June 3, 05:00 (in about 3 day(s))."
        ).defineEnum("foodExpiryTooltipStyle", FoodExpiryTooltipStyle.BOTH);
        enableTFCF3Overlays = builder.apply("enableTFCF3Overlays").comment("Enable TFC additions to the F3 menu, showing time, date, and climate information.").define("enableTFCF3Overlays", true);

        innerBuilder.pop();
    }
}