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

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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
        @Config.Comment("Various debug options. Activates some extra wand features. Enables extra item tooltips.")
        @Config.LangKey("config." + MOD_ID + ".general.debug")
        public boolean debug = Launch.blackboard.get("fml.deobfuscatedEnvironment") != null;

        @Config.Comment("If true, TFC will try and force the `level-type` setting to `tfc_classic` during DedicatedServer startup.")
        @Config.LangKey("config." + MOD_ID + ".general.forceTFCWorldTypeOnServer")
        public boolean forceTFCWorldTypeOnServer = true;

        @Config.Comment("Enable/Disable the vanilla recipe removal spam. False = Those recipes are left in place.")
        @Config.LangKey("config." + MOD_ID + ".general.removeVanillaRecipes")
        public boolean removeVanillaRecipes = true;

        @Config.Comment("Enable/Disable the vanilla loot entries that conflict with TFC (ie: potatoes). False = Those loot entries are left in place.")
        @Config.LangKey("config." + MOD_ID + ".general.removeVanillaLoots")
        public boolean removeVanillaLoots = true;

        @Config.Comment("Normal leaf drop chance for sticks")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.leafStickDropChance")
        public double leafStickDropChance = 0.1;

        @Config.Comment("Bonus leaf drop chance for sticks")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.leafStickDropChanceBonus")
        public double leafStickDropChanceBonus = 0.25;

        @Config.Comment("Drop chance for gem from raw stone")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.stoneGemDropChance")
        public double stoneGemDropChance = 31.0 / 8000.0; // 0.003875

        @Config.Comment("Bonus leaf drop stick chance for sticks tool classes")
        @Config.LangKey("config." + MOD_ID + ".general.leafStickDropChanceBonusClasses")
        public String[] leafStickDropChanceBonusClasses = new String[] {"knife", "scythe"};

        @Config.Comment("List of fluids allowed to be picked up by wooden bucket")
        @Config.LangKey("config." + MOD_ID + ".general.woodenBucketWhitelist")
        public String[] woodenBucketWhitelist = new String[] {"fresh_water", "hot_water", "salt_water", "water", "limewater", "tannin", "olive_oil", "vinegar", "rum", "beer", "whiskey", "rye_whiskey", "corn_whiskey", "sake", "vodka", "cider", "brine", "milk", "milk_curdled", "milk_vinegar"};

        @Config.Comment("List of fluids allowed to be picked up by blue steel bucket")
        @Config.LangKey("config." + MOD_ID + ".general.blueSteelBucketWhitelist")
        public String[] blueSteelBucketWhitelist = new String[] {"lava"};

        @Config.Comment("List of fluids allowed to be picked up by red steel bucket")
        @Config.LangKey("config." + MOD_ID + ".general.redSteelBucketWhitelist")
        public String[] redSteelBucketWhitelist = new String[] {"fresh_water", "hot_water", "salt_water", "water"};

        @Config.Comment("Modifier for how quickly items gains and loses heat. Smaller number = slower temperature changes.")
        @Config.RangeDouble(min = 0, max = 10)
        @Config.LangKey("config." + MOD_ID + ".general.temperatureModifierGlobal")
        public double temperatureModifierGlobal = 0.5; // todo: items cool too fast at 0.5, needs tweaking

        @Config.Comment("Modifier for how quickly devices (i.e. charcoal forge, firepit) gain and lose heat. Smaller number = slower temperature changes.")
        @Config.RangeDouble(min = 0, max = 10)
        @Config.LangKey("config." + MOD_ID + ".general.temperatureModifierHeating")
        public double temperatureModifierHeating = 1;

        @Config.Comment("Modifier for how quickly food will decay. Larger values = faster decay. Set to 0 for infinite expiration time")
        @Config.RangeDouble(min = 0, max = 10)
        @Config.LangKey("config." + MOD_ID + ".general.foodDecayModifier")
        public double foodDecayModifier = 1.0;

        @Config.Comment("Number of ticks required for a pit kiln to burn out. (1000 = 1 in game hour = 50 seconds), default is 8 hours.")
        @Config.RangeInt(min = 20)
        @Config.LangKey("config." + MOD_ID + ".general.pitKilnTime")
        public int pitKilnTime = 8000;

        @Config.Comment("Number of ticks required for a torch to burn out (72000 = 1 in game hour = 50 seconds), default is 72 hours. Set to -1 to disable torch burnout.")
        @Config.RangeInt(min = -1)
        @Config.LangKey("config." + MOD_ID + ".general.torchTime")
        public int torchTime = 72000;

        @Config.Comment("Number of ticks required for a bloomery to complete. (1000 = 1 in game hour = 50 seconds), default is 15 hours.")
        @Config.RangeInt(min = 20)
        @Config.LangKey("config." + MOD_ID + ".general.bloomeryTime")
        public int bloomeryTime = 15000;

        @Config.Comment("How fast the blast furnace consume fuels (compared to the charcoal forge).")
        @Config.RangeDouble(min = 0.1D)
        @Config.LangKey("config." + MOD_ID + ".general.blastFurnaceConsumption")
        public double blastFurnaceConsumption = 4;

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
        public double playerNutritionDecayModifier = 0.8;

        @Config.Comment("Minimum health modifier player can obtain with low nutrition.")
        @Config.RangeDouble(min = 0.1d, max = 1d)
        @Config.LangKey("config." + MOD_ID + ".general.playerMinHealthModifier")
        public double playerMinHealthModifier = 0.2d;

        @Config.Comment("Maximum health modifier player can obtain with high nutrition.")
        @Config.RangeDouble(min = 1d, max = 5d)
        @Config.LangKey("config." + MOD_ID + ".general.playerMaxHealthModifier")
        public double playerMaxHealthModifier = 3d;

        @Config.Comment("Modifier for how quickly the players becomes thirsty.")
        @Config.RangeDouble(min = 0, max = 100)
        @Config.LangKey("config." + MOD_ID + ".general.playerThirstModifier")
        public double playerThirstModifier = 8.0;

        @Config.Comment("Modifier for how quickly the player will naturally regenerate health.")
        @Config.LangKey("config." + MOD_ID + ".general.playerNaturalRegenerationModifier")
        @Config.RangeDouble(min = 0, max = 100)
        public double playerNaturalRegenerationModifier = 1.0;

        @Config.Comment("Chance that mining a raw rock triggers a collapse.")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.collapseChance")
        public double collapseChance = 0.1;

        @Config.Comment("Chance that collapsing blocks propagate the collapse. Influenced by distance from epicenter of collapse.")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.propagateCollapseChance")
        public double propagateCollapseChance = 0.55;

        @Config.Comment("Damage Source Types that will default to Slashing damage.")
        @Config.LangKey("config." + MOD_ID + ".general.slashingDamageSources")
        public String[] slashingDamageSources = new String[] {};

        @Config.Comment("Damage Source Types that will default to Piercing damage.")
        @Config.LangKey("config." + MOD_ID + ".general.piercingDamageSources")
        public String[] piercingDamageSources = new String[] {"arrow", "cactus", "thorns"};

        @Config.Comment("Damage Source Types that will default to Crushing damage.")
        @Config.LangKey("config." + MOD_ID + ".general.crushingDamageSources")
        public String[] crushingDamageSources = new String[] {"anvil", "falling_block"};

        @Config.Comment("Damage Source Entities that will default to Slashing damage.")
        @Config.LangKey("config." + MOD_ID + ".general.slashingDamageEntities")
        public String[] slashingDamageEntities = new String[] {"minecraft:wither_skeleton", "minecraft:vex", "minecraft:vindication_illager", "minecraft:zombie_pigman", "minecraft:wolf", "minecraft:polar_bear"};

        @Config.Comment("Damage Source Entities that will default to Piercing damage.")
        @Config.LangKey("config." + MOD_ID + ".general.piercingDamageEntities")
        public String[] piercingDamageEntities = new String[] {"minecraft:stray", "minecraft:skeleton"};

        @Config.Comment("Damage Source Entities that will default to Crushing damage.")
        @Config.LangKey("config." + MOD_ID + ".general.crushingDamageEntities")
        public String[] crushingDamageEntities = new String[] {"minecraft:husk", "minecraft:skeleton_horse", "minecraft:zombie_horse", "minecraft:spider", "minecraft:giant", "minecraft:zombie", "minecraft:slime", "minecraft:cave_spider", "minecraft:silverfish", "minecraft:villager_golem", "minecraft:zombie_villager"};

        @Config.Comment("The amount of metal contained in a small ore / nugget.")
        @Config.LangKey("config." + MOD_ID + ".general.smallOreMetalAmount")
        @Config.RangeInt(min = 1, max = 10_000)
        public int smallOreMetalAmount = 10;

        @Config.Comment("The amount of metal contained in a poor ore.")
        @Config.LangKey("config." + MOD_ID + ".general.poorOreMetalAmount")
        @Config.RangeInt(min = 1, max = 10_000)
        public int poorOreMetalAmount = 15;

        @Config.Comment("The amount of metal contained in a normal ore.")
        @Config.LangKey("config." + MOD_ID + ".general.normalOreMetalAmount")
        @Config.RangeInt(min = 1, max = 10_000)
        public int normalOreMetalAmount = 25;

        @Config.Comment("The amount of metal contained in a rich ore.")
        @Config.LangKey("config." + MOD_ID + ".general.richOreMetalAmount")
        @Config.RangeInt(min = 1, max = 10_000)
        public int richOreMetalAmount = 35;

        @Config.Comment("The amount of times a chunk can be worked. Note: While sluices increase work by 1, Goldpan increase by 6.")
        @Config.LangKey("config." + MOD_ID + ".general.maxWorkChunk")
        @Config.RangeInt(min = 1, max = 10_000)
        public int maxWorkChunk = 300;

        @Config.Comment("The radius sluice works on chunks.")
        @Config.LangKey("config." + MOD_ID + ".general.sluiceRadius")
        @Config.RangeInt(min = 0, max = 10)
        public int sluiceRadius = 1;

        @Config.Comment("The amount of ticks a sluice uses to work.")
        @Config.LangKey("config." + MOD_ID + ".general.sluiceTicks")
        @Config.RangeInt(min = 20)
        public int sluiceTicks = 100;

        @Config.Comment("Chance that a sluice operation produce small ore.")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.sluiceOreChance")
        public double sluiceOreChance = 0.05;

        @Config.Comment("Chance that a sluice operation produce gems.")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.sluiceGemChance")
        public double sluiceGemChance = 0.05;

        @Config.Comment("Chance that a diamond is dropped when sluice produce gems.")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.sluiceDiamondGemChance")
        public double sluiceDiamondGemChance = 0.01;

        @Config.Comment("If true, limits for gold pan and sluice are ignored.")
        @Config.LangKey("config." + MOD_ID + ".general.overworkChunk")
        public boolean overworkChunk = false;

        @Config.Comment({"If true, this will force the gamerule naturalRegeneration to be false. ", "Note: this DOES NOT AFFECT TFC's natural regeneration. If you set naturalRegeneration to true, then you will have both TFC regeneration and normal vanilla regeneration (which is much faster)"})
        @Config.LangKey("config." + MOD_ID + ".general.forceNoVanillaNaturalRegeneration")
        public boolean forceNoVanillaNaturalRegeneration = true;

        @Config.Comment("Should the player receive passive regeneration of health, food, and thirst, while in peaceful mode similar to vanilla?")
        @Config.LangKey("config." + MOD_ID + ".general.peacefulDifficultyPassiveRegeneration")
        public boolean peacefulDifficultyPassiveRegeneration = false;

        @Config.Comment("A multiplier for passive exhaustion (how fast your hunger decays. 0 = hunger doesn't decay passively, higher values = faster decay.")
        @Config.LangKey("config." + MOD_ID + ".general.foodPassiveExhaustionMultiplier")
        @Config.RangeDouble(min = 0, max = 100)
        public double foodPassiveExhaustionMultiplier = 1;

        @Config.Comment("The minimum time for a chunk to be unoccupied for it's resources to begin to naturally regenerate. (In days). After this amount, regeneration will scale up based on how long since this duration, up to a maximum of 4x")
        @Config.RangeInt(min = 12, max = 1000)
        @Config.LangKey("config." + MOD_ID + ".general.worldRegenerationMinimumTime")
        public int worldRegenerationMinimumTime = 24;

        @Config.Comment("The weight for loose rocks and sticks regeneration in the world.")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.worldRegenerationSticksRocksModifier")
        public double worldRegenerationSticksRocksModifier = 0.5;

        @Config.Comment("The number of hours to which initial food decay will be synced. When a food item is dropped, it's initial expiration date will be rounded to the closest multiple of this (in hours).")
        @Config.RangeInt(min = 1, max = 48)
        @Config.LangKey("config." + MOD_ID + ".general.foodDecayStackTime")
        public int foodDecayStackTime = 6;

        @Config.Comment("If true, hammer must be in offhand for chisel use. If false, hammer can be in offhand or toolbar.")
        @Config.LangKey("config." + MOD_ID + ".general.requireHammerInOffHand")
        public boolean requireHammerInOffHand = true;

        @Config.Comment("Does the chisel have a delay on use?")
        @Config.LangKey("config." + MOD_ID + ".general.chiselDelay")
        public boolean chiselDelay = false;

        @Config.Comment("Add iron ore dictionary to wrought iron items?")
        @Config.LangKey("config." + MOD_ID + ".general.oreDictIron")
        public boolean oreDictIron = false;

        @Config.Comment("Add plate ore dictionary to sheet items?")
        @Config.LangKey("config." + MOD_ID + ".general.oreDictPlate")
        public boolean oreDictPlate = false;

        @Config.Comment("Should living in a chunk block hostile mob spawning over time?")
        @Config.LangKey("config." + MOD_ID + ".general.spawnProtectionEnable")
        public boolean spawnProtectionEnable = true;

        @Config.Comment("The min Y value a spawn has to be for spawn protection to be considered. (spawns under this level won't be stopped by spawn protection.")
        @Config.LangKey("config." + MOD_ID + ".general.spawnProtectionMinY")
        public int spawnProtectionMinY = 100;

        @Config.Comment("The time required for a charcoal pit to complete")
        @Config.LangKey("config." + MOD_ID + ".general.charcoalPitTime")
        public int charcoalPitTime = 18_000;

        @Config.Comment("The default length of a month (in days) when a new world is started. This can be changed in existing worlds via the /timetfc command.")
        @Config.LangKey("config." + MOD_ID + ".general.defaultMonthLength")
        public int defaultMonthLength = 8;

        @Config.Comment("Should animals became old and die?")
        @Config.LangKey("config." + MOD_ID + ".general.enableAnimalAging")
        public boolean enableAnimalAging = true;

        @Config.Comment("How long until animals became old (in factor to adulthood)?")
        @Config.RangeDouble(min = 1, max = 50)
        @Config.LangKey("config." + MOD_ID + ".general.factorAnimalAging")
        public double factorAnimalAging = 3;

        @Config.Comment("Chance of animal dying (checked every in-game day) after it became old")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.chanceAnimalDeath")
        public double chanceAnimalDeath = 0.0;

        @Config.Comment("Log return rate of stone axes (eg: How efficiently it is)")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.LangKey("config." + MOD_ID + ".general.stoneAxeLogReturnRate")
        public double stoneAxeLogReturnRate = 0.6;
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

        @Config.Comment("The color to render on top of rotten food. Express as a 265 bit color value: 0xFFFFFF = white, 0x000000 = black")
        @Config.LangKey("config." + MOD_ID + ".client.rottenFoodOverlayColor")
        public int rottenFoodOverlayColor = 0x88CC33;
    }

    public static class WorldCFG
    {
        @Config.Comment({"This controls how the temperature gradient appears near the equator.", "1: south of equator is hot, north of equator is cold", "-1: south of equator is cold, north of equator is hot"})
        @Config.LangKey("config." + MOD_ID + ".world.hemisphereType")
        @Config.RangeInt(min = -1, max = 1)
        @Config.RequiresMcRestart
        public int hemisphereType = 1;

        @Config.Comment("This controls the appearance of cyclic temperature regions. If you want an endless north / south with a temperate equator, set this to false.")
        @Config.LangKey("config." + MOD_ID + ".world.cyclicTemperatureRegions")
        @Config.RequiresMcRestart
        public boolean cyclicTemperatureRegions = false;

        @Config.Comment("Debug worldgen [DANGER] Your world will be affected! Do not use on your proper world files!")
        @Config.LangKey("config." + MOD_ID + ".world.debugWorldGen")
        @Config.RequiresWorldRestart
        public boolean debugWorldGen = false;

        @Config.Comment({"This controls the size of the temperature regions. The size of each temperature zone is determined by a sin wave. This represents the period of the wave."})
        @Config.RangeInt(min = 1_000, max = 1_000_000)
        @Config.LangKey("config." + MOD_ID + ".world.latitudeTemperatureModifier")
        public int latitudeTemperatureModifier = 40_000;

        @Config.Comment("The rarity for clay pits to occur. On average 1 / N chunks will have a clay deposit, if the chunk in question is valid for clay to spawn.")
        @Config.RangeInt(min = 1)
        @Config.LangKey("config." + MOD_ID + ".world.clayRarity")
        public int clayRarity = 30;

        @Config.Comment("The minimum rainfall in an area required for clay to spawn. By default this is the same threshold as dry grass.")
        @Config.RangeInt(min = 1, max = 500)
        @Config.LangKey("config." + MOD_ID + ".world.clayRainfallThreshold")
        public int clayRainfallThreshold = 150;

        @Config.Comment("The number of attempts per chunk to spawn loose rocks. Includes surface ore samples.")
        @Config.RangeInt(min = 1)
        @Config.LangKey("config." + MOD_ID + ".world.looseRocksFrequency")
        public int looseRocksFrequency = 18;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.05, max = 0.4)
        @Config.Comment("This controls how spread the rainfall distribution is. Higher values mean the world will be distributed towards the extremes more. WARNING: This is can cause very weird world generation conditions.")
        @Config.LangKey("config." + MOD_ID + ".world.rainfallSpreadFactor")
        public double rainfallSpreadFactor = 0.13;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.05, max = 0.4)
        @Config.Comment("This controls how spread the flora diversity distribution is. Higher values mean the world will be distributed towards the extremes more. WARNING: This is can cause very weird world generation conditions.")
        @Config.LangKey("config." + MOD_ID + ".world.floraDiversitySpreadFactor")
        public double floraDiversitySpreadFactor = 0.16;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0.05, max = 0.4)
        @Config.Comment("This controls how spread the flora density distribution is. Higher values mean the world will be distributed towards the extremes more. WARNING: This is can cause very weird world generation conditions.")
        @Config.LangKey("config." + MOD_ID + ".world.floraDensitySpreadFactor")
        public double floraDensitySpreadFactor = 0.16;
    }
}
