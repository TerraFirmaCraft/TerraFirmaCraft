/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

/**
 * Top level items must be static, the subclasses' fields must not be static.
 */
@Config(modid = MOD_ID, category = "")
@Mod.EventBusSubscriber(modid = MOD_ID)
@Config.LangKey("config." + MOD_ID)
@SuppressWarnings("WeakerAccess")
public class ConfigTFC
{
    @Config.Comment("General settings")
    @Config.LangKey("config." + MOD_ID + ".general")
    public static final GeneralCFG GENERAL = new GeneralCFG();

    @Config.Comment("Client side settings")
    @Config.LangKey("config." + MOD_ID + ".client")
    public static final ClientCFG CLIENT = new ClientCFG();

    @Config.Comment("World gen settings")
    @Config.LangKey("config." + MOD_ID + ".world")
    public static final WorldCFG WORLD = new WorldCFG();

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID))
        {
            TerraFirmaCraft.getLog().warn("Config changed");
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
        }
    }

    public static class GeneralCFG
    {
        @Config.Comment("Various debug options. Activates some extra wand features.")
        @Config.LangKey("config." + MOD_ID + ".general.debug")
        public boolean debug = Launch.blackboard.get("fml.deobfuscatedEnvironment") != null;

        @Config.Comment({"Enable/Disable the vanilla recipe removal spam. False = Those recipes are left in place."})
        @Config.LangKey("config." + MOD_ID + ".general.removeVanillaRecipes")
        public boolean removeVanillaRecipes = true;

        @Config.Comment("Normal decay leaf drop chance for sticks")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.leafStickDropChance")
        public double leafStickDropChance = 0.1;

        @Config.Comment("Bonus decay leaf drop chance for sticks")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.leafStickDropChanceBonus")
        public double leafStickDropChanceBonus = 0.25;

        @Config.Comment("Bonus decay leaf drop chance for sticks tool classes")
        @Config.LangKey("config." + MOD_ID + ".general.leafStickDropChanceBonusClasses")
        public String[] leafStickDropChanceBonusClasses = new String[] {"knife", "scythe"};

        @Config.Comment("Modifier for how quickly items gains and loses heat. Smaller number = slower temperature changes.")
        @Config.RangeDouble(min = 0, max = 10)
        @Config.LangKey("config." + MOD_ID + ".general.temperatureModifierGlobal")
        public double temperatureModifierGlobal = 0.5; // todo: items cool too fast at 0.5, needs tweaking

        @Config.Comment("Modifier for how quickly devices (i.e. charcoal forge, firepit) gain and lose heat. Smaller number = slower temperature changes.")
        @Config.RangeDouble(min = 0, max = 10)
        @Config.LangKey("config." + MOD_ID + ".general.temperatureModifierHeating")
        public double temperatureModifierHeating = 1;

        @Config.Comment("Number of ticks required for a pit kiln to burn out. (1000 = 1 in game hour = 50 seconds), default is 8 hours.")
        @Config.RangeInt(min = 20)
        @Config.LangKey("config." + MOD_ID + ".general.pitKilnTime")
        public int pitKilnTime = 8000;

        @Config.Comment("Number of ticks required for a torch to burn out (72000 = 1 in game hour), default is 72 hours. Set to -1 to disable torch burnout.")
        @Config.RangeInt(min = 20)
        @Config.LangKey("config." + MOD_ID + ".general.torchTime")
        public int torchTime = 72000;

        @Config.Comment("Number of ticks required for a bloomery to complete. (1000 = 1 in game hour = 50 seconds), default is 15 hours.")
        @Config.RangeInt(min = 20)
        @Config.LangKey("config." + MOD_ID + ".general.bloomeryTime")
        public int bloomeryTime = 15000;

        @Config.Comment("Percentage chance that plants will grow each update. Smaller number = slower.")
        @Config.RangeDouble(min = 0d, max = 1d)
        @Config.LangKey("config." + MOD_ID + ".general.plantGrowthRate")
        public double plantGrowthRate = 0.01d;

        @Config.Comment("Chance for the fire starter to be successful")
        @Config.RangeDouble(min = 0d, max = 1d)
        @Config.LangKey("config." + MOD_ID + ".general.fireStarterChance")
        public double fireStarterChance = 0.5d;

        @Config.Comment("Modifier for how quickly the players nutrition values will decay")
        @Config.RangeDouble(min = 0, max = 10)
        @Config.LangKey("config." + MOD_ID + ".general.playerNutritionDecayModifier")
        public double playerNutritionDecayModifier = 0.2;
    }

    public static class ClientCFG
    {
        @Config.Comment({"Only works client side!", "Servers require the world type to be set to 'tfc_classic'"})
        @Config.LangKey("config." + MOD_ID + ".client.makeWorldTypeClassicDefault")
        @Config.RequiresMcRestart
        public boolean makeWorldTypeClassicDefault = true;

        @Config.Comment({"Show ItemStack tool classes when advanced tooltips are enabled. (F3+H)"})
        @Config.LangKey("config." + MOD_ID + ".client.showToolClassTooltip")
        public boolean showToolClassTooltip = true;

        @Config.Comment({"Show ItemStack OreDictionary matches when advanced tooltips are enabled. (F3+H)"})
        @Config.LangKey("config." + MOD_ID + ".client.showOreDictionaryTooltip")
        public boolean showOreDictionaryTooltip = true;

        @Config.Comment({"Show ItemStack NBT on the tooltip when advanced tooltips are enabled. (F3+H)"})
        @Config.LangKey("config." + MOD_ID + ".client.showNBTTooltip")
        public boolean showNBTTooltip = false;

        @Config.Comment("Should the prospectors pick output to the actionbar? (the space just above the hotbar)")
        @Config.LangKey("config." + MOD_ID + ".client.propickOutputToActionBar")
        public boolean propickOutputToActionBar = true;
    }

    public static class WorldCFG
    {
        @Config.Comment({"This controls how the temperature gradient appears near the equator.", "1: south of equator is hot, north of equator is cold", "-1: south of equator is cold, north of equator is hot"})
        @Config.LangKey("config." + MOD_ID + ".world.hemisphereType")
        @Config.RangeInt(min = -1, max = 1)
        @Config.RequiresMcRestart
        public int hemisphereType = 1;

        @Config.Comment("This controls the appearance of cyclic temperature regions. If you want an endless north / south with a temperate equator, set this to false")
        @Config.LangKey("config." + MOD_ID + ".world.cyclicTemperatureRegions")
        @Config.RequiresMcRestart
        public boolean cyclicTemperatureRegions = true;

        @Config.Comment("Debug worldgen [DANGER] Your world will be affected! Do not use on your proper world files!")
        @Config.LangKey("config." + MOD_ID + ".world.debugWorldGen")
        @Config.RequiresWorldRestart
        public boolean debugMode = false;

        @Config.Comment({"This controls the size of the temperature regions. The size of each temperature zone is determined by a sin wave.",
            "The equation is roughly sin(pi * zCoord / (16 * zTemperatureModifier)). 2500 gives a total range of 40 km (peaks at +/- 20km)"})
        @Config.RangeDouble(min = 100, max = 10000)
        @Config.LangKey("config." + MOD_ID + ".world.zTemperatureModifier")
        public double zTemperatureModifier = 2500f;
    }
}
