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

import net.dries007.tfc.world.classic.CalenderTFC;

import static net.dries007.tfc.Constants.MOD_ID;

/**
 * Top level items must be static, the subclasses' fields must not be static.
 */
@Config(modid = MOD_ID, category = "")
@Mod.EventBusSubscriber(modid = MOD_ID)
@Config.LangKey("config." + MOD_ID)
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

            CalenderTFC.reload();

            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
        }
    }

    public static class GeneralCFG
    {
        @Config.Comment("Various debug options. Activates some extra wand features.")
        @Config.LangKey("config." + MOD_ID + ".general.debug")
        public boolean debug = Launch.blackboard.get("fml.deobfuscatedEnvironment") != null;

        @Config.Comment("Lengths of a month in in game days. Year length is this x12")
        @Config.LangKey("config." + MOD_ID + ".general.monthLength")
        @Config.RangeInt(min = 1, max = 1000)
        public int monthLength = 96;

        @Config.Comment("Normal decay leaf drop chance for sticks")
        @Config.RangeDouble(min = 0, max = 1)
        public double leafStickDropChance = 0.1; // todo: lang key

        @Config.Comment("Bonus decay leaf drop chance for sticks")
        @Config.RangeDouble(min = 0, max = 1)
        public double leafStickDropChanceBonus = 0.25; // todo: lang key

        @Config.Comment("Bonus decay leaf drop chance for sticks tool classes")
        public String[] leafStickDropChanceBonusClasses = new String[] {"knife", "scythe"}; // todo: lang key
    }

    public static class ClientCFG
    {
        @Config.Comment({"Only works client side!", "Servers require the world type to be set to 'tfc_classic'"})
        @Config.LangKey("config." + MOD_ID + ".client.makeWorldTypeClassicDefault")
        @Config.RequiresMcRestart
        public boolean makeWorldTypeClassicDefault = true;
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
