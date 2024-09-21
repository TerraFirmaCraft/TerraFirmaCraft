/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Client Config
 * - not synced, only loaded client side
 * - only use for PURELY AESTHETIC options
 */
public class ClientConfig extends BaseConfig
{
    // General
    public final Supplier<Boolean> ignoreExperimentalWorldGenWarning;
    public final Supplier<Boolean> enableDebug;
    // Display
    public final Supplier<Boolean> enableHungerBar;
    public final Supplier<Boolean> enableHealthBar;
    public final Supplier<Boolean> enableThirstBar;
    public final Supplier<Boolean> enableExperienceBar;
    public final Supplier<Boolean> enableInkSplatter;
    public final Supplier<Boolean> enableScreenParticles;
    public final Supplier<Boolean> enableVanillaTutorialToasts;
    public final Supplier<Integer> effectHorizontalAdjustment;
    public final Supplier<HealthDisplayStyle> healthDisplayStyle;
    public final Supplier<FoodExpiryTooltipStyle> foodExpiryTooltipStyle;
    public final Supplier<Integer> foodExpiryOverlayColor;
    public final Supplier<TemperatureDisplayStyle> heatTooltipStyle;
    public final Supplier<TemperatureDisplayStyle> climateTooltipStyle;
    public final Supplier<TimeDeltaTooltipStyle> timeDeltaTooltipStyle;
    public final Supplier<DisabledExperienceBarStyle> disabledExperienceBarStyle;
    public final Supplier<Boolean> sendProspectResultsToActionbar;
    public final Supplier<Boolean> showHoeOverlaysOnlyWhenShifting;
    public final Supplier<Boolean> showHoeOverlaysInInfoMods;
    public final Supplier<Boolean> displayFamiliarityAsPercent;
    public final Supplier<Boolean> showGuideBookLinksAlways;
    public final Supplier<Boolean> showGuideBookTabInInventory;
    public final Supplier<Boolean> displayItemContentsAsImages;
    public final Supplier<Boolean> displayItemHeatBars;
    public final Supplier<Boolean> enableWindParticles;

    // Compatibility
    public final Supplier<List<String>> additionalSpecialModels;

    ClientConfig(ConfigBuilder builder)
    {
        builder.push("general");

        ignoreExperimentalWorldGenWarning = builder.comment(
            "Should TFC forcefully skip the 'Experimental World Generation' warning screen when creating or loading a world?",
            "Note: this also speeds up loading a world by about 2x."
        ).define("ignoreExperimentalWorldGenWarning", true);

        enableDebug = builder.comment("Enables a series of additional debugging tooltips, displayed information, and logging.").define("enableDebug", !FMLEnvironment.production);

        builder.swap("display");

        enableHungerBar = builder.comment("Replace the vanilla hunger bar with a TFC one.").define("enableHungerBar", true);
        enableHealthBar = builder.comment("Replaces the vanilla health bar with a TFC one.").define("enableHealthBar", true);
        enableThirstBar = builder.comment("Adds a TFC thirst bar over the hotbar.").define("enableThirstBar", true);
        enableExperienceBar = builder.comment("Allows the vanilla XP bar to render.").define("enableExperienceBar", true);
        enableInkSplatter = builder.comment("Enables squids inking your screen.").define("enableInkSplatter", true);
        enableScreenParticles = builder.comment("Enables 'screen particles' that spawn on the screen when knapping rocks.").define("enableScreenParticles", true);
        enableVanillaTutorialToasts = builder.comment("Enables the vanilla tutorial toasts that appear during gameplay. These can be difficult to make disappear in modded environments, so they are disabled by default.").define("enableVanillaTutorialToasts", false);

        effectHorizontalAdjustment = builder.comment("Adjusts the position where potion effects render in the X direction. By default, this moves them to the right of the inventory tabs. Negative values shift them to the left.").define("effectHorizontalAdjustment", 20, -128, 128);

        healthDisplayStyle = builder.comment(
            "Health display format. This affects what number is displayed on top of the tfc-style health bar",
            "  TFC - e.g. 750 / 1000",
            "  VANILLA - e.g. 15.0 / 20.0",
            "  TFC_CURRENT - e.g. 750",
            "  VANILLA_CURRENT - e.g. 15.0"
        ).define("healthDisplayStyle", HealthDisplayStyle.TFC);

        foodExpiryTooltipStyle = builder.comment(
            "Food expiry tooltip display style. This affects what information is shown on the food item unsealedStack tooltips.",
            "  NONE - Shows nothing. Maximum mystery!",
            "  EXPIRY - e.g. 'Expires on June 3, 05:00",
            "  TIME_LEFT - e.g. 'Expires in about 3 day(s)",
            "  BOTH - Shows both of the above, e.g. Expires on June 3, 05:00 (in about 3 day(s))."
        ).define("foodExpiryTooltipStyle", FoodExpiryTooltipStyle.BOTH);
        foodExpiryOverlayColor = builder
            .comment("The overlay color to indicate rotten foods, in ARGB. Default = 0xFF88CC33")
            .define("foodExpiryOverlayColor", 0xFF88CC33);

        final var temperatureDisplayStyle = new String[] {
            "  COLOR = Approximate, color based tooltips (like Very Hot**, Brilliant White)",
            "  CELSIUS = Exact degrees Celsius",
            "  FAHRENHEIT = Exact degrees Fahrenheit",
            "  KELVIN = Exact Kelvin",
            "  RANKINE = Exact degrees Rankine"
        };

        heatTooltipStyle = builder.comment("The style to display all heat tooltips in.").comment(temperatureDisplayStyle).define("heatTooltipStyle", TemperatureDisplayStyle.COLOR);
        climateTooltipStyle = builder.comment("The style to display all external (i.e. climate) temperature in.").comment(temperatureDisplayStyle).define("climateTooltipStyle", TemperatureDisplayStyle.CELSIUS);

        timeDeltaTooltipStyle = builder.comment(
            "The style to display all time delta / duration tooltips in.",
            "  DAYS = Display values larger than a month as a number of days, i.e. '105 day(s)'",
            "  DAYS_MONTHS = Display values larger than a year as a number of months and days, i.e. '13 month(s), 1 day(s)'",
            "  DAYS_MONTHS_YEARS = Display values as normal, i.e. '1 year(s), 1 month(s), 1 day(s)'"
        ).define("timeDeltaTooltipStyle", TimeDeltaTooltipStyle.DAYS_MONTHS_YEARS);

        disabledExperienceBarStyle = builder.comment(
            "The style to display HUD elements when the XP bar is disabled.",
            "  HOVER = Display all elements in their default positions",
            "  BUMP = Move elements closer to the hotbar; when fishing or riding a jumping entity, other elements move to their default positions",
            "  LEFT_HOTBAR = Move elements closer to the hotbar; when fishing or riding a jumping entity, those elements will appear as a vertical bar between the hotbar and offhand slot"
        ).define("disabledExperienceBarStyle", DisabledExperienceBarStyle.HOVER);

        sendProspectResultsToActionbar = builder.comment("If prospect information should appear in the space above the hotbar (the actionbar). False will put them in the chat window.").define("sendProspectResultsToActionbar", true);

        showHoeOverlaysOnlyWhenShifting = builder.comment("If hoe overlays (for hydration, nutrition, or temperature, shown when hovering over a plant or farmland while holding a hoe), should only be shown when the shift key is held down.").define("showHoeOverlaysOnlyWhenShifting", false);
        showHoeOverlaysInInfoMods = builder.comment("If true, mods like Jade that add info when hovering on a block will add the hoe's overlay info to the tooltip, even when not holding a hoe.").define("showHoeOverlaysInInfoMods", true);

        displayFamiliarityAsPercent = builder.comment("If familiarity is displayed as a percent rather than a heart").define("displayFamiliarityAsPercent", false);

        showGuideBookLinksAlways = builder.comment("If, when hovering over an item in the inventory, or looking at a block in the world that has a linked page in the guide book, should it display a tooltip along with allowing you to hold Ctrl/Cmd to quickly navigate to that page in the book.").define("showGuideBookLinksAlways", true);
        showGuideBookTabInInventory = builder.comment("If a button linking to the TFC Field Guide should be added to the inventory, climate, nutrition, and calendar screens?").define("showGuideBookTabInInventory", true);

        displayItemContentsAsImages = builder.comment("For items like bundles, their contents inside will be rendered using Bundle Technology to show their items.").define("displayItemContentsAsImages", true);
        displayItemHeatBars = builder.comment("If true, for items that are hot, they will show a bar on the item like a durability bar").define("displayItemHeatBars", true);
        enableWindParticles = builder.comment("If true, particles specifically for wind will appear.").define("enableWindParticles", true);

        builder.swap("compatibility");

        additionalSpecialModels = builder.comment(
            "Registers additional models into forge's special model registry.",
            "For Pack Makers: this is needed if you want your custom item models to render when used for panning (if they are not already used somewhere else and added automatically in that case)"
        ).define("additionalSpecialModels", List.of(), name -> ResourceLocation.tryParse(name) != null);

        builder.pop();
    }
}