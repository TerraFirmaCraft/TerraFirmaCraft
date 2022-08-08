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
    public final ForgeConfigSpec.BooleanValue enableInkSplatter;
    public final ForgeConfigSpec.BooleanValue enableVanillaTutorialToasts;
    public final ForgeConfigSpec.EnumValue<HealthDisplayStyle> healthDisplayStyle;
    public final ForgeConfigSpec.EnumValue<FoodExpiryTooltipStyle> foodExpiryTooltipStyle;
    public final ForgeConfigSpec.IntValue foodExpiryOverlayColor;
    public final ForgeConfigSpec.EnumValue<HeatTooltipStyle> heatTooltipStyle;
    public final ForgeConfigSpec.BooleanValue enableTFCF3Overlays;
    public final ForgeConfigSpec.BooleanValue sendProspectResultsToActionbar;
    public final ForgeConfigSpec.BooleanValue showHoeOverlaysOnlyWhenShifting;
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
        enableInkSplatter = builder.apply("enableInkSplatter").comment("Enables squids inking your screen.").define("enableInkSplatter", true);
        enableVanillaTutorialToasts = builder.apply("enableVanillaTutorialToasts").comment("Enables the vanilla tutorial toasts that appear during gameplay. These can be difficult to make disappear in modded environments, so they are disabled by default.").define("enableVanillaTutorialToasts", false);

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

        enableTFCF3Overlays = builder.apply("enableTFCF3Overlays").comment("Enable TFC additions to the F3 menu, showing time, date, and climate information.").define("enableTFCF3Overlays", true);

        sendProspectResultsToActionbar = builder.apply("sendProspectResultsToActionbar").comment("If prospect information should appear in the space above the hotbar (the actionbar). False will put them in the chat window.").define("sendProspectResultsToActionbar", true);

        showHoeOverlaysOnlyWhenShifting = builder.apply("showHoeOverlaysOnlyWhenShifting").comment("If hoe overlays (for hydration, nutrition, or temperature, shown when hovering over a plant or farmland while holding a hoe), should only be shown when the shift key is held down.").define("showHoeOverlaysOnlyWhenShifting", false);

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