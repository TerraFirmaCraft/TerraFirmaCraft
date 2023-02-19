/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
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
    public final ForgeConfigSpec.BooleanValue enableDebug;
    // Display
    public final ForgeConfigSpec.BooleanValue enableHungerBar;
    public final ForgeConfigSpec.BooleanValue enableHealthBar;
    public final ForgeConfigSpec.BooleanValue enableThirstBar;
    public final ForgeConfigSpec.BooleanValue enableExperienceBar;
    public final ForgeConfigSpec.BooleanValue enableInkSplatter;
    public final ForgeConfigSpec.BooleanValue enableVanillaTutorialToasts;
    public final ForgeConfigSpec.IntValue effectHorizontalAdjustment;
    public final ForgeConfigSpec.EnumValue<HealthDisplayStyle> healthDisplayStyle;
    public final ForgeConfigSpec.EnumValue<FoodExpiryTooltipStyle> foodExpiryTooltipStyle;
    public final ForgeConfigSpec.IntValue foodExpiryOverlayColor;
    public final ForgeConfigSpec.EnumValue<HeatTooltipStyle> heatTooltipStyle;
    public final ForgeConfigSpec.EnumValue<TimeDeltaTooltipStyle> timeDeltaTooltipStyle;
    public final ForgeConfigSpec.EnumValue<DisabledExperienceBarStyle> disabledExperienceBarStyle;
    public final ForgeConfigSpec.BooleanValue enableTFCF3Overlays;
    public final ForgeConfigSpec.BooleanValue sendProspectResultsToActionbar;
    public final ForgeConfigSpec.BooleanValue showHoeOverlaysOnlyWhenShifting;
    public final ForgeConfigSpec.BooleanValue showHoeOverlaysInInfoMods;
    public final ForgeConfigSpec.BooleanValue displayFamiliarityAsPercent;
    public final ForgeConfigSpec.BooleanValue showGuideBookLinksAlways;
    public final ForgeConfigSpec.BooleanValue showGuideBookTabInInventory;
    // Compatibility
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> additionalMetalSheetTextures;

    ClientConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("general");

        ignoreExperimentalWorldGenWarning = builder.apply("ignoreExperimentalWorldGenWarning").comment(
            "Should TFC forcefully skip the 'Experimental World Generation' warning screen when creating or loading a world?",
            "Note: this also speeds up loading a world by about 2x."
        ).define("ignoreExperimentalWorldGenWarning", true);

        enableDebug = builder.apply("enableDebug").comment("Enables a series of additional debugging tooltips, displayed information, and logging.").define("enableDebug", () -> !FMLEnvironment.production);

        innerBuilder.pop().push("display");

        enableHungerBar = builder.apply("enableHungerBar").comment("Replace the vanilla hunger bar with a TFC one.").define("enableHungerBar", true);
        enableHealthBar = builder.apply("enableHealthBar").comment("Replaces the vanilla health bar with a TFC one.").define("enableHealthBar", true);
        enableThirstBar = builder.apply("enableThirstBar").comment("Adds a TFC thirst bar over the hotbar.").define("enableThirstBar", true);
        enableExperienceBar = builder.apply("enableExperienceBar").comment("Allows the vanilla XP bar to render.").define("enableExperienceBar", true);
        enableInkSplatter = builder.apply("enableInkSplatter").comment("Enables squids inking your screen.").define("enableInkSplatter", true);
        enableVanillaTutorialToasts = builder.apply("enableVanillaTutorialToasts").comment("Enables the vanilla tutorial toasts that appear during gameplay. These can be difficult to make disappear in modded environments, so they are disabled by default.").define("enableVanillaTutorialToasts", false);

        effectHorizontalAdjustment = builder.apply("effectHorizontalAdjustment").comment("Adjusts the position where potion effects render in the X direction. By default, this moves them to the right of the inventory tabs. Negative values shift them to the left.").defineInRange("effectHorizontalAdjustment", 20, -128, 128);

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
        foodExpiryOverlayColor = builder.apply("foodExpiryOverlayColor").comment("The overlay color to indicate rotten foods. Default = 0x88CC33").defineInRange("foodExpiryOverlayColor", 0x88CC33, 0, 0xFFFFFF);

        heatTooltipStyle = builder.apply("heatTooltipStyle").comment(
            "The style to display all heat tooltips in.",
            "COLOR = Approximate, color based tooltips (like Very Hot**, Brilliant White)",
            "CELSIUS = Exact degrees celsius",
            "FAHRENHEIT = Exact degrees fahrenheit"
        ).defineEnum("heatTooltipStyle", HeatTooltipStyle.COLOR);

        timeDeltaTooltipStyle = builder.apply("timeDeltaTooltipStyle").comment(
            "The style to display all time delta / duration tooltips in.",
            "DAYS = Display values larger than a month as a number of days, i.e. '105 day(s)'",
            "DAYS_MONTHS = Display values larger than a year as a number of months and days, i.e. '13 month(s), 1 day(s)'",
            "DAYS_MONTHS_YEARS = Display values as normal, i.e. '1 year(s), 1 month(s), 1 day(s)'"
        ).defineEnum("timeDeltaTooltipStyle", TimeDeltaTooltipStyle.DAYS_MONTHS_YEARS);

        disabledExperienceBarStyle = builder.apply("disabledExperienceBarStyle").comment(
            "The style to display HUD elements when the XP bar is disabled.",
            "HOVER = Display all elements in their default positions",
            "BUMP = Move elements closer to the hotbar; when fishing or riding a jumping entity, other elements move to their default positions",
            "LEFT_HOTBAR = Move elements closer to the hotbar; when fishing or riding a jumping entity, those elements will appear as a vertical bar between the hotbar and offhand slot"
        ).defineEnum("disabledExperienceBarStyle", DisabledExperienceBarStyle.HOVER);

        enableTFCF3Overlays = builder.apply("enableTFCF3Overlays").comment("Enable TFC additions to the F3 menu, showing time, date, and climate information.").define("enableTFCF3Overlays", true);

        sendProspectResultsToActionbar = builder.apply("sendProspectResultsToActionbar").comment("If prospect information should appear in the space above the hotbar (the actionbar). False will put them in the chat window.").define("sendProspectResultsToActionbar", true);

        showHoeOverlaysOnlyWhenShifting = builder.apply("showHoeOverlaysOnlyWhenShifting").comment("If hoe overlays (for hydration, nutrition, or temperature, shown when hovering over a plant or farmland while holding a hoe), should only be shown when the shift key is held down.").define("showHoeOverlaysOnlyWhenShifting", false);
        showHoeOverlaysInInfoMods = builder.apply("showHoeOverlaysInInfoMods").comment("If true, mods like Jade that add info when hovering on a block will add the hoe's overlay info to the tooltip, even when not holding a hoe.").define("showHoeOverlaysInInfoMods", true);

        displayFamiliarityAsPercent = builder.apply("displayFamiliarityAsPercent").comment("If familiarity is displayed as a percent rather than a heart").define("displayFamiliarityAsPercent", false);

        showGuideBookLinksAlways = builder.apply("showGuideBookLinksAlways").comment("If, when hovering over an item in the inventory, or looking at a block in the world that has a linked page in the guide book, should it display a tooltip along with allowing you to hold Ctrl/Cmd to quickly navigate to that page in the book.").define("showGuideBookLinksAlways", true);
        showGuideBookTabInInventory = builder.apply("showGuideBookTabInInventory").comment("If a button linking to the TFC Field Guide should be added to the inventory, climate, nutrition, and calendar screens?").define("showGuideBookTabInInventory", true);

        innerBuilder.pop().push("compatibility");

        additionalMetalSheetTextures = builder.apply("additionalMetalSheetTextures").comment(
            "Defines additional metal sheet textures that should be added to the block atlas, as they would be otherwise unused, for use in ingot piles and metal sheet blocks.",
            "For Pack Makers: When adding a Metal via a datapack, with a custom texture \"domain:block/my_texture\", and you get missing textures in ingot piles and sheet blocks, that texture needs to be added here"
        ).defineList("additionalMetalSheetTextures", ArrayList::new, o -> o instanceof String s && ResourceLocation.isValidResourceLocation(s));

        innerBuilder.pop();
    }
}