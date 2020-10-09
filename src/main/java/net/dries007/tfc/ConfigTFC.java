/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.client.GrassColorHandler;
import net.dries007.tfc.util.Alloy;
import net.dries007.tfc.util.config.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Top level items must be static, the subclasses' fields must not be static.
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class ConfigTFC
{
    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID))
        {
            TerraFirmaCraft.getLog().warn("Config changed");
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
            GrassColorHandler.resetColors();
        }
    }

    @Config(modid = MOD_ID, category = "general", name = "TerraFirmaCraft - General")
    @Config.LangKey("config." + MOD_ID + ".general")
    public static final class General
    {
        @Config.Comment("Debug settings")
        @Config.LangKey("config." + MOD_ID + ".general.debug")
        public static final DebugCFG DEBUG = new DebugCFG();

        @Config.Comment("Override settings")
        @Config.LangKey("config." + MOD_ID + ".general.overrides")
        public static final OverridesCFG OVERRIDES = new OverridesCFG();

        @Config.Comment("Fallable settings")
        @Config.LangKey("config." + MOD_ID + ".general.fallable")
        public static final FallableCFG FALLABLE = new FallableCFG();

        @Config.Comment("Difficulty settings")
        @Config.LangKey("config." + MOD_ID + ".general.difficulty")
        public static final DifficultyCFG DIFFICULTY = new DifficultyCFG();

        @Config.Comment("Tree settings")
        @Config.LangKey("config." + MOD_ID + ".general.tree")
        public static final TreeCFG TREE = new TreeCFG();

        @Config.Comment("Spawn protection settings")
        @Config.LangKey("config." + MOD_ID + ".general.spawn_protection")
        public static final SpawnProtectionCFG SPAWN_PROTECTION = new SpawnProtectionCFG();

        @Config.Comment("Damage settings")
        @Config.LangKey("config." + MOD_ID + ".general.damage")
        public static final DamageCFG DAMAGE = new DamageCFG();

        @Config.Comment("Player settings")
        @Config.LangKey("config." + MOD_ID + ".general.player")
        public static final PlayerCFG PLAYER = new PlayerCFG();

        @Config.Comment("World generation settings")
        @Config.LangKey("config." + MOD_ID + ".general.world")
        public static final WorldCFG WORLD = new WorldCFG();

        @Config.Comment("World regeneration settings")
        @Config.LangKey("config." + MOD_ID + ".general.world_regen")
        public static final WorldRegenCFG WORLD_REGEN = new WorldRegenCFG();

        @Config.Comment("Food settings")
        @Config.LangKey("config." + MOD_ID + ".general.food")
        public static final FoodCFG FOOD = new FoodCFG();

        @Config.Comment("Miscelaneous")
        @Config.LangKey("config." + MOD_ID + ".general.misc")
        public static final MiscCFG MISC = new MiscCFG();

        public static final class DebugCFG
        {
            @Config.Comment("Various debug options. Activates some extra wand features. Enables extra item tooltips.")
            @Config.LangKey("config." + MOD_ID + ".general.debug.enable")
            public boolean enable = false;

            @Config.Comment("Debug worldgen (the danger part) This will glass maps at max world height to help debug world gen. THIS WILL MESS UP YOUR WORLD!")
            @Config.LangKey("config." + MOD_ID + ".general.debug.debugWorldGenDanger")
            @Config.RequiresWorldRestart
            public boolean debugWorldGenDanger = false;

            @Config.Comment("Debug worldgen (safe part) This will output map images of world gen steps and print some debug info. This is safe to use.")
            @Config.LangKey("config." + MOD_ID + ".general.debug.debugWorldGenSafe")
            @Config.RequiresWorldRestart
            public boolean debugWorldGenSafe = false;
        }

        public static final class OverridesCFG
        {
            @Config.Comment("Enable ingot pile placement in world.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableIngotPiles")
            public boolean enableIngotPiles = true;

            @Config.Comment("Enable log pile placement in world.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableLogPiles")
            public boolean enableLogPiles = true;

            @Config.Comment("Enable the creation of thatch beds.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableThatchBed")
            public boolean enableThatchBed = true;

            @Config.Comment("Enable the creation of grass paths using TFC's shovels.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableGrassPath")
            public boolean enableGrassPath = true;

            @Config.Comment("Enable the creation of farmland on TFC's soil.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableHoeing")
            public boolean enableHoeing = true;

            @Config.Comment("Enable the creation of stone anvils.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableStoneAnvil")
            public boolean enableStoneAnvil = true;

            @Config.Comment("Turn this off to disable TFC's registry replacement of Torches. This will disable them extihguishing over time.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableTorchOverride")
            public boolean enableTorchOverride = true;

            @Config.Comment("Turn this off to disable TFC's registry replacement of Ice and Snow blocks. This will remove their temperature based behavior.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableFrozenOverrides")
            public boolean enableFrozenOverrides = true;

            @Config.Comment("Number of ticks required for a torch to burn out (72000 = 1 in game hour = 50 seconds), default is 72 hours. Set to -1 to disable torch burnout.")
            @Config.RangeInt(min = -1)
            @Config.LangKey("config." + MOD_ID + ".general.overrides.torchTime")
            public int torchTime = 72000;

            @Config.Comment("Overrides lava and water making vanilla stones to TFC variants.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.enableLavaWaterPlacesTFCBlocks")
            public boolean enableLavaWaterPlacesTFCBlocks = true;

            @Config.Comment("If true, TFC will try and force the `level-type` setting to `tfc_classic` during DedicatedServer startup or define it as default world type for clients.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.forceTFCWorldType")
            public boolean forceTFCWorldType = true;

            @Config.Comment("If true, TFC will override default ore gen file. Pack Makers: Disable this to keep your ore gen file from being reset to default.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.forceDefaultOreGenFile")
            public boolean forceDefaultOreGenFile = true;

            @Config.Comment("Enable/Disable the vanilla recipe removal spam. False = Those recipes are left in place.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.removeVanillaRecipes")
            public boolean removeVanillaRecipes = true;

            @Config.Comment("Enable/Disable the vanilla loot entries that conflict with TFC (ie: potatoes). False = Those loot entries are left in place.")
            @Config.LangKey("config." + MOD_ID + ".general.overrides.removeVanillaLoots")
            public boolean removeVanillaLoots = true;

            @Config.Comment({"If true, this will force the gamerule naturalRegeneration to be false. ",
                "Note: this DOES NOT AFFECT TFC's natural regeneration.",
                "If you set naturalRegeneration to true, then you will have both TFC regeneration and normal vanilla regeneration (which is much faster)"})
            @Config.LangKey("config." + MOD_ID + ".general.overrides.forceNoVanillaNaturalRegeneration")
            public boolean forceNoVanillaNaturalRegeneration = true;

            @Config.Comment({"If true, this will replace vanilla animals with the TFC counterpart under any spawning circumstances (ie: mob spawner, etc)."})
            @Config.LangKey("config." + MOD_ID + ".general.overrides.forceReplaceVanillaAnimals")
            public boolean forceReplaceVanillaAnimals = true;
        }

        public static final class FallableCFG
        {
            @Config.Comment("If false, fallable blocks (ie: dirt, stone) will never fall.")
            @Config.LangKey("config." + MOD_ID + ".general.fallable.enable")
            public boolean enable = true;

            @Config.Comment("If false, fallable blocks (ie: dirt, stone) will never destroy ore blocks.")
            @Config.LangKey("config." + MOD_ID + ".general.fallable.destroyOres")
            public boolean destroyOres = true;

            @Config.Comment("If false, fallable blocks (ie: dirt, stone) will never destroy loose items.")
            @Config.LangKey("config." + MOD_ID + ".general.fallable.destroyItems")
            public boolean destroyItems = true;

            @Config.Comment("If false, fallable blocks (ie: dirt, stone) will never hurt entities.")
            @Config.LangKey("config." + MOD_ID + ".general.fallable.hurtEntities")
            public boolean hurtEntities = true;

            @Config.Comment("Chance that mining raw rocks triggers a collapse.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.fallable.collapseChance")
            public double collapseChance = 0.1;

            @Config.Comment("Chance that collapsing blocks propagate the collapse. Influenced by distance from epicenter of collapse.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.fallable.propagateCollapseChance")
            public double propagateCollapseChance = 0.55;

            @Config.Comment("Horizontal radius of the support range of support beams.")
            @Config.RangeInt(min = 0, max = 8)
            @Config.LangKey("config." + MOD_ID + ".general.fallable.supportBeamRangeHor")
            public int supportBeamRangeHor = 4;

            @Config.Comment("Upwards support range of support beams.")
            @Config.RangeInt(min = 0, max = 3)
            @Config.LangKey("config." + MOD_ID + ".general.fallable.supportBeamRangeUp")
            public int supportBeamRangeUp = 1;

            @Config.Comment("Downwards support range of support beams.")
            @Config.RangeInt(min = 0, max = 3)
            @Config.LangKey("config." + MOD_ID + ".general.fallable.supportBeamRangeDown")
            public int supportBeamRangeDown = 1;

            @Config.Comment("Should chiseling raw stone blocks cause collapses?")
            @Config.LangKey("config." + MOD_ID + ".general.fallable.chiselCausesCollapse")
            public boolean chiselCausesCollapse = true;

            @Config.Comment("Should exploding raw stone blocks cause collapses?")
            @Config.LangKey("config." + MOD_ID + ".general.fallable.explosionCausesCollapse")
            public boolean explosionCausesCollapse = true;
        }

        public static final class DifficultyCFG
        {
            @Config.Comment("This controls how many animals (any kind) are loaded (spawned) / player. Higher values means more animals near a player, which in turn raises difficulty and meat abundance (caution, a value too high can also negatively impact performance).")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.difficulty.animalSpawnCount")
            public int animalSpawnCount = 75;

            @Config.Comment("This controls how many mobs are loaded (spawned) / player. Higher values means more mobs near a player, which in turn raises difficulty (caution, a value too high can also negatively impact performance)")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.difficulty.mobSpawnCount")
            public int mobSpawnCount = 140;

            @Config.Comment("Prevent mob spawning on surface?")
            @Config.LangKey("config." + MOD_ID + ".general.difficulty.preventMobsOnSurface")
            public boolean preventMobsOnSurface = true;

            @Config.Comment("Give wrought iron weapons to vanilla mobs?")
            @Config.LangKey("config." + MOD_ID + ".general.difficulty.giveVanillaMobsEquipment")
            public boolean giveVanillaMobsEquipment = true;

            @Config.Comment("Range of pixels on either side of the working target that can be accepted to complete a smithing recipe")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.difficulty.acceptableAnvilRange")
            public int acceptableAnvilRange = 0;
        }

        public static final class TreeCFG
        {
            @Config.Comment({"Enable trees being fully cut by axes.",
                "Disable it if you want other mods to handle tree felling."})
            @Config.LangKey("config." + MOD_ID + ".general.tree.enableFelling")
            public boolean enableFelling = true;

            @Config.Comment("Enable smacking logs with a hammer giving sticks.")
            @Config.LangKey("config." + MOD_ID + ".general.tree.enableHammerSticks")
            public boolean enableHammerSticks = true;

            @Config.Comment("Should logs require tools (axes and saws, or hammers for sticks) to be cut? If false, breaking logs with an empty hand will not drop logs.")
            @Config.LangKey("config." + MOD_ID + ".general.tree.requiresAxe")
            public boolean requiresAxe = true;

            @Config.Comment("If false, leaves will not drop saplings.")
            @Config.LangKey("config." + MOD_ID + ".general.tree.enableSaplings")
            public boolean enableSaplings = true;

            @Config.Comment("Chance per log for an item to drop when using a stone axe.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.tree.stoneAxeReturnRate")
            public double stoneAxeReturnRate = 0.6;

            @Config.Comment("Normal leaf drop chance for sticks")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.tree.leafStickDropChance")
            public double leafStickDropChance = 0.1;

            @Config.Comment("Chance that leaves will drop more sticks when harvested with configured tool classes.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.tree.leafStickDropChanceBonus")
            public double leafStickDropChanceBonus = 0.25;

            @Config.Comment("Tool classes that have the configured bonus to drop more sticks when harvesting leaves.")
            @Config.LangKey("config." + MOD_ID + ".general.tree.leafStickDropChanceBonusClasses")
            public String[] leafStickDropChanceBonusClasses = new String[] {"knife", "scythe"};
        }

        public static final class SpawnProtectionCFG
        {
            @Config.Comment("Should living in a chunk prevent hostile mob spawning over time?")
            @Config.LangKey("config." + MOD_ID + ".general.spawn_protection.preventMobs")
            public boolean preventMobs = true;

            @Config.Comment("Should living in a chunk prevent predators spawning over time?")
            @Config.LangKey("config." + MOD_ID + ".general.spawn_protection.preventPredators")
            public boolean preventPredators = true;

            @Config.Comment("The min Y value a spawn has to be for spawn protection to prevent mobs. Anything below it will not be prevented.")
            @Config.RangeInt(min = 1, max = 255)
            @Config.LangKey("config." + MOD_ID + ".general.spawn_protection.minYMobs")
            public int minYMobs = 100;

            @Config.Comment("The max Y value a spawn has to be for spawn protection to prevent mobs. Anything above it will not be prevented.")
            @Config.RangeInt(min = 1, max = 255)
            @Config.LangKey("config." + MOD_ID + ".general.spawn_protection.maxYMobs")
            public int maxYMobs = 255;

            @Config.Comment("The min Y value a spawn has to be for spawn protection to prevent predators. Anything below it will not be prevented.")
            @Config.RangeInt(min = 1, max = 255)
            @Config.LangKey("config." + MOD_ID + ".general.spawn_protection.minYPredators")
            public int minYPredators = 100;

            @Config.Comment("The max Y value a spawn has to be for spawn protection to prevent predators. Anything above it will not be prevented.")
            @Config.RangeInt(min = 1, max = 255)
            @Config.LangKey("config." + MOD_ID + ".general.spawn_protection.maxYPredators")
            public int maxYPredators = 255;
        }

        public static final class DamageCFG
        {
            @Config.Comment("Damage Source Types that will default to Slashing damage.")
            @Config.LangKey("config." + MOD_ID + ".general.damage.slashingSources")
            public String[] slashingSources = new String[] {};

            @Config.Comment("Damage Source Types that will default to Piercing damage.")
            @Config.LangKey("config." + MOD_ID + ".general.damage.piercingSources")
            public String[] piercingSources = new String[] {"arrow", "cactus", "thorns"};

            @Config.Comment("Damage Source Types that will default to Crushing damage.")
            @Config.LangKey("config." + MOD_ID + ".general.damage.crushingSources")
            public String[] crushingSources = new String[] {"anvil", "falling_block"};

            @Config.Comment("Damage Source Entities that will default to Slashing damage.")
            @Config.LangKey("config." + MOD_ID + ".general.damage.slashingEntities")
            public String[] slashingEntities = new String[] {"minecraft:wither_skeleton", "minecraft:vex", "minecraft:vindication_illager", "minecraft:zombie_pigman", "minecraft:wolf", "minecraft:polar_bear"};

            @Config.Comment("Damage Source Entities that will default to Piercing damage.")
            @Config.LangKey("config." + MOD_ID + ".general.damage.piercingEntities")
            public String[] piercingEntities = new String[] {"minecraft:stray", "minecraft:skeleton"};

            @Config.Comment("Damage Source Entities that will default to Crushing damage.")
            @Config.LangKey("config." + MOD_ID + ".general.damage.crushingEntities")
            public String[] crushingEntities = new String[] {"minecraft:husk", "minecraft:skeleton_horse", "minecraft:zombie_horse", "minecraft:spider", "minecraft:giant", "minecraft:zombie", "minecraft:slime", "minecraft:cave_spider", "minecraft:silverfish", "minecraft:villager_golem", "minecraft:zombie_villager"};
        }

        public static final class PlayerCFG
        {
            @Config.Comment("Enable a 3x3 crafting inventory via key binding.")
            @Config.LangKey("config." + MOD_ID + ".general.player.inventoryCraftingMode")
            public InventoryCraftingMode inventoryCraftingMode = InventoryCraftingMode.ENABLED;

            @Config.Comment({"Minimum health modifier player can obtain with low nutrition.",
                "Example 1(Vanilla): 20 * 0.2(mod) = 4 (or 2 hearts).",
                "Example 2(TFC): 1000 * 0.2(mod) = 200."})
            @Config.RangeDouble(min = 0.1, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.player.minHealthModifier")
            public double minHealthModifier = 0.2;

            @Config.Comment({"Maximum health modifier player can obtain with high nutrition.",
                "Example 1(Vanilla): 20 * 3(mod) = 60 (or 30 hearts).",
                "Example 2(TFC): 1000 * 3(mod) = 3000."})
            @Config.RangeDouble(min = 1, max = 5)
            @Config.LangKey("config." + MOD_ID + ".general.player.maxHealthModifier")
            public double maxHealthModifier = 3;

            @Config.Comment("How quickly the players becomes thirsty when hunger is drained by actions/sprinting? 100 = full thirst bar.")
            @Config.RangeDouble(min = 0, max = 100)
            @Config.LangKey("config." + MOD_ID + ".general.player.thirstModifier")
            public double thirstModifier = 8.0;

            @Config.Comment({"Modifier for how quickly the player will naturally regenerate health.",
                "When on full hunger and thirst bars, 1.0 = 3HP / 5 secs."})
            @Config.LangKey("config." + MOD_ID + ".general.player.naturalRegenerationModifier")
            @Config.RangeDouble(min = 0, max = 100)
            public double naturalRegenerationModifier = 1.0;

            @Config.Comment("Should the player receive passive regeneration of health, food, and thirst, while in peaceful mode similar to vanilla?")
            @Config.LangKey("config." + MOD_ID + ".general.player.peacefulDifficultyPassiveRegeneration")
            public boolean peacefulDifficultyPassiveRegeneration = false;

            @Config.Comment("Modifier for passive exhaustion (exhaustion that naturally occurs just by living). 1.0 = full hunger bar once 2.5 minecraft days.")
            @Config.LangKey("config." + MOD_ID + ".general.player.passiveExhaustionMultiplier")
            @Config.RangeDouble(min = 0, max = 100)
            public double passiveExhaustionMultiplier = 1;

            @Config.Comment({"The amount of replenished hunger before the player's nutrition will lose the first food consumed. Most TFC foods have 4 hunger.",
                "Example: Multiply Vanilla hunger(20) by 4 to get one food bar worth of food before the first food is lost from the cycle."})
            @Config.LangKey("config." + MOD_ID + ".general.player.nutritionRotationHungerWindow")
            @Config.RangeInt(min = 4)
            public int nutritionRotationHungerWindow = 4 * 20;

            @Config.Comment("Delay (in ticks) for drinking water by hand")
            @Config.LangKey("config." + MOD_ID + ".general.player.drinkDelay")
            @Config.RangeInt(min = 1)
            public int drinkDelay = 12;

            @Config.Comment("Chance that a salty drink apply a thirst effect")
            @Config.LangKey("config." + MOD_ID + ".general.player.chanceThirstOnSaltyDrink")
            @Config.RangeDouble(min = 0, max = 1)
            public double chanceThirstOnSaltyDrink = 0.25;

            @Config.Comment("Which inventory slots will ammo refill/pickup search for quivers?")
            @Config.LangKey("config." + MOD_ID + ".general.player.quiverSearch")
            public QuiverSearch quiverSearch = QuiverSearch.HOTBAR;
        }

        public static final class WorldCFG
        {
            @Config.RequiresMcRestart
            @Config.Comment("Sets temperature in relation to the equator change. North = Cold, South = Hot or North = Hot, South = Cold.")
            @Config.LangKey("config." + MOD_ID + ".general.world.hemisphereType")
            public HemisphereType hemisphereType = HemisphereType.COLD_NORTH_HOT_SOUTH;

            @Config.RequiresMcRestart
            @Config.Comment("This controls how temperature is affected by how far from equator you are.")
            @Config.LangKey("config." + MOD_ID + ".general.world.temperatureMode")
            public TemperatureMode temperatureMode = TemperatureMode.CYCLIC;

            @Config.Comment({"If Cyclic, this controls the length (in blocks) of the temperature regions. The temperature values change in a wave-like pattern (sine wave).",
                "Wandering straight in a direction increases or decreases temperature. When you travel this many blocks, the temperature begins changing in the other direction."})
            @Config.RangeInt(min = 1_000, max = 1_000_000)
            @Config.LangKey("config." + MOD_ID + ".general.world.latitudeTemperatureModifier")
            public int latitudeTemperatureModifier = 40_000;

            @Config.Comment("The rarity for clay pits to occur. On average 1 / N chunks will have a clay deposit, if the chunk in question is valid for clay to spawn.")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".general.world.clayRarity")
            public int clayRarity = 30;

            @Config.Comment("The minimum rainfall in an area required for clay to spawn. By default this is the same threshold as dry grass.")
            @Config.RangeInt(min = 1, max = 500)
            @Config.LangKey("config." + MOD_ID + ".general.world.clayRainfallThreshold")
            public int clayRainfallThreshold = 150;

            @Config.Comment("The number of attempts per chunk to spawn loose rocks. Includes surface ore samples.")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".general.world.looseRocksFrequency")
            public int looseRocksFrequency = 18;

            @Config.Comment("Enable generation of loose rocks (just the rocks)?")
            @Config.LangKey("config." + MOD_ID + ".general.world.enableLooseRocks")
            public boolean enableLooseRocks = true;

            @Config.Comment("Enable generation of loose ores?")
            @Config.LangKey("config." + MOD_ID + ".general.world.enableLooseOres")
            public boolean enableLooseOres = true;

            @Config.Comment("Enable generation of loose rocks (just the rocks)?")
            @Config.LangKey("config." + MOD_ID + ".general.world.enableLooseSticks")
            public boolean enableLooseSticks = true;

            @Config.Comment({"This controls the number of sticks generated on chunk generation.",
                "The number of trees in the area and flora density is also a factor in this."})
            @Config.RangeDouble(min = 0, max = 10)
            @Config.LangKey("config." + MOD_ID + ".general.world.sticksDensityModifier")
            public double sticksDensityModifier = 1;

            @Config.Comment("This is how deep (in blocks) from the surface a loose rock will scan for a vein when generating, Higher values = More veins spawn samples thus adding more samples.")
            @Config.RangeInt(min = 1, max = 255)
            @Config.LangKey("config." + MOD_ID + ".general.world.looseRockScan")
            public int looseRockScan = 35;

            @Config.RequiresMcRestart
            @Config.RangeDouble(min = 0.05, max = 0.4)
            @Config.Comment({"This controls how spread the rainfall distribution is. Higher values means the world will be distributed towards the extremes more, making more deserts and rain forests.",
                "WARNING: This can cause very weird world generation conditions."})
            @Config.LangKey("config." + MOD_ID + ".general.world.rainfallSpreadFactor")
            public double rainfallSpreadFactor = 0.13;

            @Config.RequiresMcRestart
            @Config.RangeDouble(min = 0.05, max = 0.4)
            @Config.Comment({"This controls how spread the flora diversity distribution is. Higher values means the world will be distributed towards the extremes more, making forests have much more different kinds of trees.",
                "WARNING: This can cause very weird world generation conditions."})
            @Config.LangKey("config." + MOD_ID + ".general.world.floraDiversitySpreadFactor")
            public double floraDiversitySpreadFactor = 0.16;

            @Config.RequiresMcRestart
            @Config.RangeDouble(min = 0.05, max = 0.4)
            @Config.Comment({"This controls how spread the flora density distribution is. Higher values means the world will be distributed towards the extremes more, making more dense forest pockets.",
                "WARNING: This can cause very weird world generation conditions."})
            @Config.LangKey("config." + MOD_ID + ".general.world.floraDensitySpreadFactor")
            public double floraDensitySpreadFactor = 0.16;

            @Config.RequiresMcRestart
            @Config.Comment({"This controls which registered entities can respawn in TFC biomes.",
                "You must specify by following the pattern 'modid:entity <rarity> <minGroupSpawn> <maxGroupSpawn>'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.world.respawnableCreatures")
            public String[] respawnableCreatures = {"tfc:beartfc 30 1 2", "tfc:polarbeartfc 30 1 2", "tfc:panthertfc 30 1 2", "tfc:sabertoothtfc 30 1 2", "tfc:liontfc 30 1 2", "tfc:hyenatfc 30 3 6", "tfc:pheasanttfc 70 2 3", "tfc:deertfc 70 2 4", "tfc:wolftfc 70 2 4", "tfc:parrottfc 70 2 3", "tfc:ocelottfc 70 2 4"};
        }

        public static final class WorldRegenCFG
        {
            @Config.Comment("The minimum time for a chunk to be unoccupied for it's resources (berry bushes, debris and crops) to naturally regenerate. (In days). After this amount, regeneration will scale up based on how long since this duration, up to a maximum of 4x.")
            @Config.RangeInt(min = 12, max = 1000)
            @Config.LangKey("config." + MOD_ID + ".general.world_regen.minimumTime")
            public int minimumTime = 24;

            @Config.Comment("The weight for loose rocks and sticks regeneration in the world.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.world_regen.sticksRocksModifier")
            public double sticksRocksModifier = 0.5;
        }

        public static final class FoodCFG
        {
            @Config.Comment("Modifier for how quickly food will decay. Higher values = faster decay. Set to 0 for infinite expiration time")
            @Config.RangeDouble(min = 0, max = 10)
            @Config.LangKey("config." + MOD_ID + ".general.food.decayModifier")
            public double decayModifier = 1.0;

            @Config.Comment("The number of hours to which initial food decay will be synced. When a food item is dropped, it's initial expiration date will be rounded to the closest multiple of this (in hours).")
            @Config.RangeInt(min = 1, max = 48)
            @Config.LangKey("config." + MOD_ID + ".general.food.decayStackTime")
            public int decayStackTime = 6;

            @Config.Comment("If false, crops will never die under any circumstances. THIS DOES NOT MEAN THEY WILL ALWAYS GROW!")
            @Config.LangKey("config." + MOD_ID + ".general.food.enableCropDeath")
            public boolean enableCropDeath = true;

            @Config.Comment("Defines wild crops rarity to generate, in 1 / N chunks. 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.food.cropRarity")
            public int cropRarity = 30;

            @Config.Comment("Defines berry bush rarity to generate, in 1 / N chunks. 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.food.berryBushRarity")
            public int berryBushRarity = 80;

            @Config.Comment("Defines fruit tree rarity to generate, in 1 / N chunks. 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.food.fruitTreeRarity")
            public int fruitTreeRarity = 80;

            @Config.Comment("Modifier for how long crops take to grow.")
            @Config.RangeDouble(min = 0.01, max = 100)
            @Config.LangKey("config." + MOD_ID + ".general.food.cropGrowthTimeModifier")
            public double cropGrowthTimeModifier = 1.0;

            @Config.Comment("Modifier for how long berry bushes take to grow fruits.")
            @Config.RangeDouble(min = 0.01, max = 100)
            @Config.LangKey("config." + MOD_ID + ".general.food.berryBushGrowthTimeModifier")
            public double berryBushGrowthTimeModifier = 1.0;

            @Config.Comment("Modifier for how long fruit trees take to grow trunks / leaves / fruits.")
            @Config.RangeDouble(min = 0.01, max = 100)
            @Config.LangKey("config." + MOD_ID + ".general.food.fruitTreeGrowthTimeModifier")
            public double fruitTreeGrowthTimeModifier = 1.0;
        }

        public static final class MiscCFG
        {

            @Config.Comment("If true, the player will spawn with the TFC guidebook")
            @Config.LangKey("config." + MOD_ID + ".general.misc.guidebook")
            public boolean giveBook = true;

            @Config.Comment("The default length of a month (in days) when a new world is started. This can be changed in existing worlds via the /timetfc command.")
            @Config.LangKey("config." + MOD_ID + ".general.misc.defaultMonthLength")
            @Config.RangeInt(min = 1)
            public int defaultMonthLength = 8;

            @Config.Comment("Chance for a plant to grow each random tick, does not include crops. Lower = slower growth.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.misc.plantGrowthRate")
            public double plantGrowthRate = 0.01;

            @Config.Comment("Leaf block movement modifier. Lower = Slower, Higher = Faster. 1 = No slow down. (Speed * this = slow).")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.misc.leafMovementModifier")
            public double leafMovementModifier = 0.1;

            @Config.Comment("Berry bush movement modifier. Lower = Slower, Higher = Faster. 1 = No slow down. (Speed * this = slow).")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.misc.berryBushMovementModifier")
            public double berryBushMovementModifier = 0.1;

            @Config.Comment("Generic snow movement modifier. Lower = Slower, Higher = Faster. 1 = No slow down. (Speed * this = slow).")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.misc.snowMovementModifier")
            public double snowMovementModifier = 0.85;

            @Config.Comment("Generic movement modifier. Lower = Slower, Higher = Faster. 1 = No slow down. Note: this is a little different than other densities (leaf / berry bush), because this actually functions as a maximum slow down. Actual value is dependent on the plant and it's age.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.misc.minimumPlantMovementModifier")
            public double minimumPlantMovementModifier = 0;

            @Config.Comment({"Chance that mining a raw stone will drop a gem.",
                "Gem grade is random from: 16/31 Chipped, 8/31 Flawed, 4/31 Normal, 2/31 Flawless and 1/31 Exquisite."})
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".general.misc.stoneGemDropChance")
            public double stoneGemDropChance = 31.0 / 8000.0; // 0.003875

            @Config.Comment("Chance for the fire starter to be successful")
            @Config.RangeDouble(min = 0d, max = 1d)
            @Config.LangKey("config." + MOD_ID + ".general.misc.fireStarterChance")
            public double fireStarterChance = 0.5;

            @Config.Comment("The amount of metal contained in a small ore / nugget.")
            @Config.LangKey("config." + MOD_ID + ".general.misc.smallOreMetalAmount")
            @Config.RangeInt(min = 1, max = 10_000)
            public int smallOreMetalAmount = 10;

            @Config.Comment("The amount of metal contained in a poor ore.")
            @Config.LangKey("config." + MOD_ID + ".general.misc.poorOreMetalAmount")
            @Config.RangeInt(min = 1, max = 10_000)
            public int poorOreMetalAmount = 15;

            @Config.Comment("The amount of metal contained in a normal ore.")
            @Config.LangKey("config." + MOD_ID + ".general.misc.normalOreMetalAmount")
            @Config.RangeInt(min = 1, max = 10_000)
            public int normalOreMetalAmount = 25;

            @Config.Comment("The amount of metal contained in a rich ore.")
            @Config.LangKey("config." + MOD_ID + ".general.misc.richOreMetalAmount")
            @Config.RangeInt(min = 1, max = 10_000)
            public int richOreMetalAmount = 35;

            @Config.Comment("Add iron ore dictionary (ie: ingotIron, oreIron) to wrought iron items?")
            @Config.LangKey("config." + MOD_ID + ".general.misc.dictionaryIron")
            public boolean dictionaryIron = false;

            @Config.Comment("Add plate ore dictionary (plateIron, plateBronze) to sheets?")
            @Config.LangKey("config." + MOD_ID + ".general.misc.dictionaryPlates")
            public boolean dictionaryPlates = false;

            @Config.Comment("List of fluids allowed to be picked up by wooden bucket")
            @Config.LangKey("config." + MOD_ID + ".general.misc.woodenBucketWhitelist")
            public String[] woodenBucketWhitelist = new String[] {"fresh_water", "hot_water", "salt_water", "water", "limewater", "tannin", "olive_oil", "olive_oil_water", "vinegar", "rum", "beer", "whiskey", "rye_whiskey", "corn_whiskey", "sake", "vodka", "cider", "brine", "milk", "milk_curdled", "milk_vinegar", "white_dye", "orange_dye", "magenta_dye", "light_blue_dye", "yellow_dye", "lime_dye", "pink_dye", "gray_dye", "light_gray_dye", "cyan_dye", "purple_dye", "blue_dye", "brown_dye", "green_dye", "red_dye", "black_dye"};

            @Config.Comment("List of fluids allowed to be picked up by blue steel bucket")
            @Config.LangKey("config." + MOD_ID + ".general.misc.blueSteelBucketWhitelist")
            public String[] blueSteelBucketWhitelist = new String[] {"lava"};

            @Config.Comment("List of fluids allowed to be picked up by red steel bucket")
            @Config.LangKey("config." + MOD_ID + ".general.misc.redSteelBucketWhitelist")
            public String[] redSteelBucketWhitelist = new String[] {"fresh_water", "hot_water", "salt_water", "water"};

            @Config.Comment("Entities that can be plucked for feathers.")
            @Config.LangKey("config." + MOD_ID + ".general.misc.pluckableEntities")
            public String[] pluckableEntities = new String[] {"tfc:chickentfc", "tfc:pheasanttfc", "tfc:parrottfc", "tfc:ducktfc"};

            @Config.Comment("Damage dealt to an entity when a feather is harvested.")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".general.misc.damagePerFeather")
            public double damagePerFeather = 0.6;

            @Config.Comment("This controls the time it takes to mine rock blocks. 1.0 = Like vanilla, 10.0 = Classic TFC")
            @Config.RangeDouble(min = 1, max = 1000)
            @Config.LangKey("config." + MOD_ID + ".general.misc.rockMiningTimeModifier")
            public double rockMiningTimeModifier = 10.0;

            @Config.Comment("This controls the time it takes to mine log blocks. 1.0 = Like vanilla, 7.5 = Classic TFC")
            @Config.RangeDouble(min = 1, max = 1000)
            @Config.LangKey("config." + MOD_ID + ".general.misc.logMiningTimeModifier")
            public double logMiningTimeModifier = 7.5;
        }
    }


    @Config(modid = MOD_ID, category = "devices", name = "TerraFirmaCraft - Devices")
    @Config.LangKey("config." + MOD_ID + ".devices")
    public static final class Devices
    {
        @Config.Comment("Temperature Settings")
        @Config.LangKey("config." + MOD_ID + ".devices.temperature")
        public static final TemperatureCFG TEMPERATURE = new TemperatureCFG();

        @Config.Comment("Barrel")
        @Config.LangKey("config." + MOD_ID + ".devices.barrel")
        public static final BarrelCFG BARREL = new BarrelCFG();

        @Config.Comment("Blast Furnace")
        @Config.LangKey("config." + MOD_ID + ".devices.blast_furnace")
        public static final BlastFurnaceCFG BLAST_FURNACE = new BlastFurnaceCFG();

        @Config.Comment("Bloomery")
        @Config.LangKey("config." + MOD_ID + ".devices.bloomery")
        public static final BloomeryCFG BLOOMERY = new BloomeryCFG();

        @Config.Comment("Crucible")
        @Config.LangKey("config." + MOD_ID + ".devices.crucible")
        public static final CrucibleCFG CRUCIBLE = new CrucibleCFG();

        @Config.Comment("Charcoal Pit")
        @Config.LangKey("config." + MOD_ID + ".devices.charcoal_pit")
        public static final CharcoalPitCFG CHARCOAL_PIT = new CharcoalPitCFG();

        @Config.Comment("Charcoal Forge")
        @Config.LangKey("config." + MOD_ID + ".devices.charcoal_forge")
        public static final CharcoalForgeCFG CHARCOAL_FORGE = new CharcoalForgeCFG();

        @Config.Comment("Fire Pit")
        @Config.LangKey("config." + MOD_ID + ".devices.fire_pit")
        public static final FirePitCFG FIRE_PIT = new FirePitCFG();

        @Config.Comment("Pit Kiln")
        @Config.LangKey("config." + MOD_ID + ".devices.pit_kiln")
        public static final PitKilnCFG PIT_KILN = new PitKilnCFG();

        @Config.Comment("Lamp")
        @Config.LangKey("config." + MOD_ID + ".devices.lamp")
        public static final LampCFG LAMP = new LampCFG();

        @Config.Comment("Chisel")
        @Config.LangKey("config." + MOD_ID + ".devices.chisel")
        public static final ChiselCFG CHISEL = new ChiselCFG();

        @Config.Comment("Small Vessel")
        @Config.LangKey("config." + MOD_ID + ".devices.small_vessel")
        public static final SmallVesselCFG SMALL_VESSEL = new SmallVesselCFG();

        @Config.Comment("Sluice")
        @Config.LangKey("config." + MOD_ID + ".devices.sluice")
        public static final SluiceCFG SLUICE = new SluiceCFG();


        public static final class TemperatureCFG
        {
            @Config.Comment("Modifier for how quickly items will gain or lose heat. Smaller number = slower temperature changes.")
            @Config.RangeDouble(min = 0, max = 10)
            @Config.LangKey("config." + MOD_ID + ".devices.temperature.globalModifier")
            public double globalModifier = 0.5;

            @Config.Comment("Modifier for how quickly devices (i.e. charcoal forge, fire pit) will gain or lose heat. Smaller number = slower temperature changes.")
            @Config.RangeDouble(min = 0, max = 10)
            @Config.LangKey("config." + MOD_ID + ".devices.temperature.heatingModifier")
            public double heatingModifier = 1;
        }

        public static final class BarrelCFG
        {
            @Config.Comment("How much fluid (mB) can a barrel hold?")
            @Config.RangeInt(min = 100)
            @Config.LangKey("config." + MOD_ID + ".devices.barrel.tank")
            public int tank = 10_000;

            @Config.Comment("List of fluids allowed to be inserted into a barrel.")
            @Config.LangKey("config." + MOD_ID + ".devices.barrel.fluidWhitelist")
            public String[] fluidWhitelist = new String[] {"fresh_water", "hot_water", "salt_water", "water", "limewater", "tannin", "olive_oil", "olive_oil_water", "vinegar", "rum", "beer", "whiskey", "rye_whiskey", "corn_whiskey", "sake", "vodka", "cider", "brine", "milk", "milk_curdled", "milk_vinegar", "white_dye", "orange_dye", "magenta_dye", "light_blue_dye", "yellow_dye", "lime_dye", "pink_dye", "gray_dye", "light_gray_dye", "cyan_dye", "purple_dye", "blue_dye", "brown_dye", "green_dye", "red_dye", "black_dye"};
        }

        public static final class BlastFurnaceCFG
        {
            @Config.Comment({"How fast the blast furnace consume fuels (compared to the charcoal forge).",
                "Example: Charcoal (without bellows) lasts for 1800 ticks in forge while 1800 / 4 = 450 ticks in blast furnace."})
            @Config.RangeDouble(min = 0.1D)
            @Config.LangKey("config." + MOD_ID + ".devices.blast_furnace.consumption")
            public double consumption = 4;
        }

        public static final class BloomeryCFG
        {
            @Config.Comment("Number of ticks required for a bloomery to complete. (1000 = 1 in game hour = 50 seconds), default is 15 hours.")
            @Config.RangeInt(min = 20)
            @Config.LangKey("config." + MOD_ID + ".devices.bloomery.ticks")
            public int ticks = 15_000;
        }

        public static final class CrucibleCFG
        {
            @Config.Comment("How much metal (units / mB) can a crucible hold?")
            @Config.RangeInt(min = 100, max = Alloy.SAFE_MAX_ALLOY)
            @Config.LangKey("config." + MOD_ID + ".devices.crucible.tank")
            public int tank = 3_000;

            @Config.Comment("Let crucibles accept pouring metal (from small vessels / molds) from all 9 input slots at the same time.")
            @Config.LangKey("config." + MOD_ID + ".devices.crucible.enableAllSlots")
            public boolean enableAllSlots = false;

            @Config.Comment("How fast should crucibles accept fluids from molds / small vessel?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".devices.crucible.pouringSpeed")
            public int pouringSpeed = 1;
        }

        public static final class CharcoalPitCFG
        {
            @Config.Comment("Number of ticks required for charcoal pit to complete. (1000 = 1 in game hour = 50 seconds), default is 18 hours.")
            @Config.LangKey("config." + MOD_ID + ".devices.charcoal_pit.ticks")
            public int ticks = 18_000;

            @Config.Comment("Can charcoal pits take glass (or stained glass) as a valid cover block?")
            @Config.LangKey("config." + MOD_ID + ".devices.charcoal_pit.glass")
            public boolean canAcceptGlass = true;
        }

        public static final class CharcoalForgeCFG
        {
            @Config.Comment({"Number of burning ticks that is removed when the charcoal forge is on rain (random ticks).",
                "This effectively makes the charcoal forge consumes more fuel when it is raining above it."})
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".devices.charcoal_forge.rainTicks")
            public int rainTicks = 600;
        }

        public static final class FirePitCFG
        {
            @Config.Comment("Number of ticks required for a cooking pot on a fire pit to boil before turning into soup, per serving. (1000 = 1 in game hour = 50 seconds). Default is 1 hour.")
            @Config.RangeInt(min = 20)
            @Config.LangKey("config." + MOD_ID + ".devices.fire_pit.ticks")
            public int ticks = 1_000;

            @Config.Comment({"Number of burning ticks that is removed when the fire pit is on rain (random ticks).",
                "This effectively makes the fire pit consumes more fuel when it is raining above it."})
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".devices.fire_pit.rainTicks")
            public int rainTicks = 1000;
        }

        public static final class PitKilnCFG
        {
            @Config.Comment("Number of ticks required for a pit kiln to burn out. (1000 = 1 in game hour = 50 seconds), default is 8 hours.")
            @Config.RangeInt(min = 20)
            @Config.LangKey("config." + MOD_ID + ".devices.pit_kiln.ticks")
            public int ticks = 8_000;
        }

        public static final class LampCFG
        {
            @Config.Comment("How much fuel (mB) can a metal lamps hold?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".devices.lamp.tank")
            public int tank = 250;

            @Config.Comment("How fast lamps consume fuel (mb/hour)? 1 = lamp life of 1 hour per mB, 0.125 = lamp life of 2000 hours by default, 0 = infinite fuel")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".devices.lamp.burnRate")
            public double burnRate = 0.125;

            @Config.Comment("Which fluids are valid fuels for lamps?")
            @Config.LangKey("config." + MOD_ID + ".devices.lamp.fuels")
            public String[] fuels = {"olive_oil"};
        }

        public static final class ChiselCFG
        {
            @Config.Comment("Does the chisel have a delay on use?")
            @Config.LangKey("config." + MOD_ID + ".devices.chisel.hasDelay")
            public boolean hasDelay = false;

            @Config.Comment("If true, hammer must be in offhand for chisel use. If false, hammer can be in offhand or toolbar.")
            @Config.LangKey("config." + MOD_ID + ".devices.chisel.requireHammerInOffHand")
            public boolean requireHammerInOffHand = true;
        }

        public static final class SmallVesselCFG
        {
            @Config.Comment("How much metal (units / mB) can a small vessel hold?")
            @Config.RangeInt(min = 100, max = Alloy.SAFE_MAX_ALLOY)
            @Config.LangKey("config." + MOD_ID + ".devices.small_vessel.tank")
            public int tank = 4_000;
        }

        public static final class SluiceCFG
        {
            @Config.Comment({"The amount of times a chunk can be worked (300 = default, 0 = disable).",
                "Note: While sluices increase work by 1, Goldpan increase by 6."})
            @Config.LangKey("config." + MOD_ID + ".devices.sluice.maxWorkChunk")
            @Config.RangeInt(min = 0, max = 10_000)
            public int maxWorkChunk = 300;

            @Config.Comment("The radius sluice works, in chunks.")
            @Config.LangKey("config." + MOD_ID + ".devices.sluice.radius")
            @Config.RangeInt(min = 0, max = 10)
            public int radius = 1;

            @Config.Comment("The amount of ticks a sluice uses to work.")
            @Config.LangKey("config." + MOD_ID + ".devices.sluice.ticks")
            @Config.RangeInt(min = 20)
            public int ticks = 100;

            @Config.Comment("Chance that a sluice operation produce small ore.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".devices.sluice.oreChance")
            public double oreChance = 0.05;

            @Config.Comment("Chance that a sluice operation produce gems.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".devices.sluice.gemChance")
            public double gemChance = 0.05;

            @Config.Comment("Chance that a diamond is dropped when sluice produce gems.")
            @Config.RangeDouble(min = 0, max = 1)
            @Config.LangKey("config." + MOD_ID + ".devices.sluice.diamondGemChance")
            public double diamondGemChance = 0.01;
        }


    }

    @Config(modid = MOD_ID, category = "client", name = "TerraFirmaCraft - Client")
    @Config.LangKey("config." + MOD_ID + ".client")
    public static class Client
    {
        @Config.Comment("Tooltip settings")
        @Config.LangKey("config." + MOD_ID + ".client.tooltip")
        public static final TooltipCFG TOOLTIP = new TooltipCFG();

        @Config.Comment("Display settings")
        @Config.LangKey("config." + MOD_ID + ".client.display")
        public static final DisplayCFG DISPLAY = new DisplayCFG();

        @Config.Comment("Grass coloring settings")
        @Config.LangKey("config." + MOD_ID + ".client.grassColor")
        public static final GrassColorCFG GRASS_COLOR = new GrassColorCFG();

        public static final class TooltipCFG
        {
            @Config.Comment({"Show ItemStack tool classes when advanced tooltips are enabled. (F3+H)"})
            @Config.LangKey("config." + MOD_ID + ".client.tooltip.showToolClassTooltip")
            public boolean showToolClassTooltip = true;

            @Config.Comment({"Show ItemStack OreDictionary matches when advanced tooltips are enabled. (F3+H)"})
            @Config.LangKey("config." + MOD_ID + ".client.tooltip.showOreDictionaryTooltip")
            public boolean showOreDictionaryTooltip = true;

            @Config.Comment({"Show ItemStack NBT on the tooltip when advanced tooltips are enabled. (F3+H)"})
            @Config.LangKey("config." + MOD_ID + ".client.tooltip.showNBTTooltip")
            public boolean showNBTTooltip = false;

            @Config.Comment("Should the prospectors pick output to the actionbar? (the space just above the hotbar)")
            @Config.LangKey("config." + MOD_ID + ".client.tooltip.propickOutputToActionBar")
            public boolean propickOutputToActionBar = true;

            @Config.Comment("Ore tooltip info mode.")
            @Config.LangKey("config." + MOD_ID + ".client.tooltip.oreTooltipMode")
            public OreTooltipMode oreTooltipMode = OreTooltipMode.ALL_INFO;

            @Config.Comment("Food decay tooltip mode.")
            @Config.LangKey("config." + MOD_ID + ".client.tooltip.decayTooltipMode")
            public DecayTooltipMode decayTooltipMode = DecayTooltipMode.ALL_INFO;

            @Config.Comment({"Time tooltip info mode."})
            @Config.LangKey("config." + MOD_ID + ".client.tooltip.timeTooltipMode")
            public TimeTooltipMode timeTooltipMode = TimeTooltipMode.MINECRAFT_HOURS;
        }

        public static final class DisplayCFG
        {
            @Config.Comment("If TFC health bar is enabled, this changes display health format. (Default: TFC = 1000 / 1000).")
            @Config.LangKey("config." + MOD_ID + ".client.display.healthDisplayFormat")
            public HealthDisplayFormat healthDisplayFormat = HealthDisplayFormat.TFC;

            @Config.Comment({"Disable TFC health bar and use vanilla instead?"})
            @Config.LangKey("config." + MOD_ID + ".client.display.useVanillaHealth")
            public boolean useVanillaHealth = false;

            @Config.Comment({"Disable TFC hunger bar and use vanilla instead?"})
            @Config.LangKey("config." + MOD_ID + ".client.display.useVanillaHunger")
            public boolean useVanillaHunger = false;

            @Config.Comment({"Hide the thirst bar?"})
            @Config.LangKey("config." + MOD_ID + ".client.display.hideThirstBar")
            public boolean hideThirstBar = false;

            @Config.Comment("The color to render on top of rotten food. Express as a 256 bit color value: 0xFFFFFF = white, 0x000000 = black")
            @Config.LangKey("config." + MOD_ID + ".client.display.rottenFoodOverlayColor")
            public int rottenFoodOverlayColor = 0x88CC33;
        }

        public static final class GrassColorCFG
        {
            @Config.Comment("If true, grass and foliage will be slightly varied in color.")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.noiseEnable")
            public boolean noiseEnable = true;

            @Config.Comment("If true, grass and foliage will be colored seasonally.")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.seasonColorEnable")
            public boolean seasonColorEnable = true;

            @Config.Comment("The noise scale. Default = 10")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.noiseScale")
            public float noiseScale = 10f;

            @Config.Comment("How many darkness levels should the noise have? Default = 5")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.noiseLevels")
            public int noiseLevels = 5;

            @Config.Comment("How potent should the darkness be? Default = 0.15")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.noiseDarkness")
            public float noiseDarkness = 0.15f;

            @Config.Comment("ARGB code for summer coloring in hexadecimal. Default: 1155FF44")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.seasonColorSummer")
            public String seasonColorSummer = "1155FF44";

            @Config.Comment("ARGB code for summer coloring in hexadecimal. Default: 55FFDD44")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.seasonColorAutumn")
            public String seasonColorAutumn = "55FFDD44";

            @Config.Comment("ARGB code for winter coloring in hexadecimal. Default: 335566FF")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.seasonColorWinter")
            public String seasonColorWinter = "335566FF";

            @Config.Comment("ARGB code for spring coloring in hexadecimal. Default: 3355FFBB")
            @Config.LangKey("config." + MOD_ID + ".client.grassColor.seasonColorSpring")
            public String seasonColorSpring = "3355FFBB";
        }
    }

    @Config(modid = MOD_ID, category = "animals", name = "TerraFirmaCraft - Animals")
    @Config.LangKey("config." + MOD_ID + ".animals")
    public static final class Animals
    {
        @Config.Comment("Alpaca")
        @Config.LangKey("config." + MOD_ID + ".animals.alpaca")
        public static final AlpacaCFG ALPACA = new AlpacaCFG();

        @Config.Comment("Sheep")
        @Config.LangKey("config." + MOD_ID + ".animals.sheep")
        public static final SheepCFG SHEEP = new SheepCFG();

        @Config.Comment("Cow")
        @Config.LangKey("config." + MOD_ID + ".animals.cow")
        public static final CowCFG COW = new CowCFG();

        @Config.Comment("Goat")
        @Config.LangKey("config." + MOD_ID + ".animals.goat")
        public static final GoatCFG GOAT = new GoatCFG();

        @Config.Comment("Chicken")
        @Config.LangKey("config." + MOD_ID + ".animals.chicken")
        public static final ChickenCFG CHICKEN = new ChickenCFG();

        @Config.Comment("Duck")
        @Config.LangKey("config." + MOD_ID + ".animals.duck")
        public static final DuckCFG DUCK = new DuckCFG();

        @Config.Comment("Pig")
        @Config.LangKey("config." + MOD_ID + ".animals.pig")
        public static final PigCFG PIG = new PigCFG();

        @Config.Comment("Camel")
        @Config.LangKey("config." + MOD_ID + ".animals.camel")
        public static final CamelCFG CAMEL = new CamelCFG();

        @Config.Comment("Llama")
        @Config.LangKey("config." + MOD_ID + ".animals.llama")
        public static final LlamaCFG LLAMA = new LlamaCFG();

        @Config.Comment("Horse")
        @Config.LangKey("config." + MOD_ID + ".animals.horse")
        public static final HorseCFG HORSE = new HorseCFG();

        @Config.Comment("Donkey")
        @Config.LangKey("config." + MOD_ID + ".animals.donkey")
        public static final DonkeyCFG DONKEY = new DonkeyCFG();

        @Config.Comment("Mule")
        @Config.LangKey("config." + MOD_ID + ".animals.mule")
        public static final MuleCFG MULE = new MuleCFG();

        @Config.Comment("Ocelot")
        @Config.LangKey("config." + MOD_ID + ".animals.ocelot")
        public static final OcelotCFG OCELOT = new OcelotCFG();

        @Config.Comment("Wolf")
        @Config.LangKey("config." + MOD_ID + ".animals.wolf")
        public static final WolfCFG WOLF = new WolfCFG();

        @Config.Comment("GrizzlyBear")
        @Config.LangKey("config." + MOD_ID + ".animals.grizzly_bear")
        public static final GrizzlyBearCFG GRIZZLY_BEAR = new GrizzlyBearCFG();

        @Config.Comment("Polar Bear")
        @Config.LangKey("config." + MOD_ID + ".animals.polar_bear")
        public static final PolarBearCFG POLAR_BEAR = new PolarBearCFG();

        @Config.Comment("Lion")
        @Config.LangKey("config." + MOD_ID + ".animals.lion")
        public static final LionCFG LION = new LionCFG();

        @Config.Comment("Panther")
        @Config.LangKey("config." + MOD_ID + ".animals.panther")
        public static final PantherCFG PANTHER = new PantherCFG();

        @Config.Comment("Saber Tooth")
        @Config.LangKey("config." + MOD_ID + ".animals.saber_tooth")
        public static final SaberToothCFG SABER_TOOTH = new SaberToothCFG();

        @Config.Comment("Hyena")
        @Config.LangKey("config." + MOD_ID + ".animals.hyena")
        public static final HyenaCFG HYENA = new HyenaCFG();

        @Config.Comment("Deer")
        @Config.LangKey("config." + MOD_ID + ".animals.deer")
        public static final DeerCFG DEER = new DeerCFG();

        @Config.Comment("Parrot")
        @Config.LangKey("config." + MOD_ID + ".animals.parrot")
        public static final ParrotCFG PARROT = new ParrotCFG();

        @Config.Comment("Pheasant")
        @Config.LangKey("config." + MOD_ID + ".animals.pheasant")
        public static final PheasantCFG PHEASANT = new PheasantCFG();

        @Config.Comment("Rabbit")
        @Config.LangKey("config." + MOD_ID + ".animals.rabbit")
        public static final RabbitCFG RABBIT = new RabbitCFG();

        @Config.Comment("DireWolf")
        @Config.LangKey("config." + MOD_ID + ".animals.direwolf")
        public static final DireWolfCFG DIREWOLF = new DireWolfCFG();

        @Config.Comment("Hare")
        @Config.LangKey("config." + MOD_ID + ".animals.hare")
        public static final HareCFG HARE = new HareCFG();

        @Config.Comment("Boar")
        @Config.LangKey("config." + MOD_ID + ".animals.boar")
        public static final BoarCFG BOAR = new BoarCFG();

        @Config.Comment("Zebu")
        @Config.LangKey("config." + MOD_ID + ".animals.zebu")
        public static final ZebuCFG ZEBU = new ZebuCFG();

        @Config.Comment("Gazelle")
        @Config.LangKey("config." + MOD_ID + ".animals.gazelle")
        public static final GazelleCFG GAZELLE = new GazelleCFG();

        @Config.Comment("Wildebeest")
        @Config.LangKey("config." + MOD_ID + ".animals.wildebeest")
        public static final WildebeestCFG WILDEBEEST = new WildebeestCFG();

        @Config.Comment("Quail")
        @Config.LangKey("config." + MOD_ID + ".animals.quail")
        public static final QuailCFG QUAIL = new QuailCFG();

        @Config.Comment("Grouse")
        @Config.LangKey("config." + MOD_ID + ".animals.grouse")
        public static final GrouseCFG GROUSE = new GrouseCFG();

        @Config.Comment("Mongoose")
        @Config.LangKey("config." + MOD_ID + ".animals.mongoose")
        public static final MongooseCFG MONGOOSE = new MongooseCFG();

        @Config.Comment("Turkey")
        @Config.LangKey("config." + MOD_ID + ".animals.turkey")
        public static final TurkeyCFG TURKEY = new TurkeyCFG();

        @Config.Comment("Jackal")
        @Config.LangKey("config." + MOD_ID + ".animals.jackal")
        public static final JackalCFG JACKAL = new JackalCFG();

        @Config.Comment("MuskOx")
        @Config.LangKey("config." + MOD_ID + ".animals.muskox")
        public static final MuskOxCFG MUSKOX = new MuskOxCFG();

        @Config.Comment("Yak")
        @Config.LangKey("config." + MOD_ID + ".animals.yak")
        public static final YakCFG YAK = new YakCFG();

        @Config.Comment("Black Bear")
        @Config.LangKey("config." + MOD_ID + ".animals.black_bear")
        public static final BlackBearCFG BLACK_BEAR = new BlackBearCFG();

        @Config.Comment("Cougar")
        @Config.LangKey("config." + MOD_ID + ".animals.cougar")
        public static final CougarCFG COUGAR = new CougarCFG();

        @Config.Comment("Coyote")
        @Config.LangKey("config." + MOD_ID + ".animals.coyote")
        public static final CoyoteCFG COYOTE = new CoyoteCFG();

        public static final class AlpacaCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 98;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 392;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 36;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("How many ticks are needed for this animal grow back wool?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.woolTicks")
            public int woolTicks = 120_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class SheepCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 64;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 256;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 28;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 2;

            @Config.Comment("How many ticks are needed for this animal grow back wool?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.woolTicks")
            public int woolTicks = 168_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 240;
        }

        public static final class MuskOxCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 192;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 768;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 59;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("How many ticks are needed for this animal grow back wool?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.woolTicks")
            public int woolTicks = 96_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class CowCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 192;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 768;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 58;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("How many ticks it is needed for this animal give milk?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.milkTicks")
            public int milkTicks = 24_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 240;
        }

        public static final class YakCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 180;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 720;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 62;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("How many ticks it is needed for this animal give milk?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.milkTicks")
            public int milkTicks = 24_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class ZebuCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 108;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 686;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 32;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("How many ticks it is needed for this animal give milk?.")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.milkTicks")
            public int milkTicks = 48_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class GoatCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 96;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 420;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 28;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 2;

            @Config.Comment("How many ticks it is needed for this animal give milk?.")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.milkTicks")
            public int milkTicks = 72_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 240;
        }

        public static final class ChickenCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 24;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 92;

            @Config.Comment("How many days it is needed for this animal finish hatching?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.hatch")
            public int hatch = 8;

            @Config.Comment("How many ticks it is needed for this animal to lay eggs?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.eggTicks")
            public int eggTicks = 30_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 240;
        }

        public static final class GrouseCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 26;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 98;

            @Config.Comment("How many days it is needed for this animal finish hatching?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.hatch")
            public int hatch = 10;

            @Config.Comment("How many ticks it is needed for this animal to lay eggs?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.eggTicks")
            public int eggTicks = 30_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class QuailCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 22;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 88;

            @Config.Comment("How many days it is needed for this animal finish hatching?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.hatch")
            public int hatch = 8;

            @Config.Comment("How many ticks it is needed for this animal to lay eggs?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.eggTicks")
            public int eggTicks = 28_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 240;
        }

        public static final class DuckCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 32;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 140;

            @Config.Comment("How many days it is needed for this animal finish hatching?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.hatch")
            public int hatch = 12;

            @Config.Comment("How many ticks it is needed for this animal to lay eggs?")
            @Config.RangeInt(min = 1_000)
            @Config.LangKey("config." + MOD_ID + ".animals.eggTicks")
            public int eggTicks = 32_000;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 240;
        }

        public static final class PigCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 80;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 320;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 19;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 10;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 240;
        }

        public static final class CamelCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 192;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 768;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 59;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class LlamaCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 160;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 640;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 55;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class HorseCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 200;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 800;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 43;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class DonkeyCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 200;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 800;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 43;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 1;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class MuleCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 200;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 800;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 0;
        }

        public static final class ParrotCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;
        }

        public static final class OcelotCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 59;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 236;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 8;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 2;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:pheasanttfc", "tfc:chickentfc", "tfc:ducktfc", "tfc:rabbittfc"};
        }

        public static final class WolfCFG
        {
            @Config.Comment("How many days until this animal is a full grown adult?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.adulthood")
            public int adulthood = 70;

            @Config.Comment("How many days after becoming an adult until this animal is old? 0 = Disable")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.elder")
            public int elder = 280;

            @Config.Comment("How many days until this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.gestation")
            public int gestation = 10;

            @Config.Comment("How many babies are born when this animal gives birth?")
            @Config.RangeInt(min = 1)
            @Config.LangKey("config." + MOD_ID + ".animals.babies")
            public int babies = 2;

            @Config.Comment("Chance that old animals will die at the start of a new day. 0 = Disable")
            @Config.RangeDouble(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.oldDeathChance")
            public double oldDeathChance = 0;

            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 250;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:sheeptfc", "tfc:rabbittfc", "tfc:haretfc"};
        }

        public static final class GrizzlyBearCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:deertfc", "tfc:haretfc", "tfc:rabbittfc"};
        }

        public static final class BlackBearCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:deertfc", "tfc:haretfc", "tfc:rabbittfc"};
        }

        public static final class PolarBearCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 120;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:deertfc", "tfc:haretfc", "tfc:rabbittfc"};
        }

        public static final class LionCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:gazelletfc", "tfc:wildebeesttfc"};
        }

        public static final class SaberToothCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:deertfc", "tfc:boartfc"};
        }

        public static final class DireWolfCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:horsetfc", "tfc:donkeytfc", "tfc:muletfc", "tfc:turkeytfc"};
        }

        public static final class CougarCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 100;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:boartfc", "tfc:haretfc"};
        }

        public static final class CoyoteCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 100;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:mongoosetfc", "tfc:haretfc"};
        }

        public static final class PantherCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 100;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:boartfc", "tfc:haretfc"};
        }

        public static final class JackalCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 120;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:pheasanttfc", "tfc:rabbittfc", "tfc:haretfc"};
        }

        public static final class HyenaCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 100;

            @Config.Comment({"This controls which registered entities will be hunted by this animal (unless tamed), in priority order.",
                "You must specify by 'modid:entity'",
                "Invalid entries will be ignored."})
            @Config.LangKey("config." + MOD_ID + ".general.animals.huntCreatures")
            public String[] huntCreatures = {"tfc:gazalletfc", "tfc:rabbittfc", "tfc:haretfc"};
        }

        public static final class MongooseCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 120;
        }

        public static final class DeerCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;
        }

        public static final class TurkeyCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 140;
        }

        public static final class PheasantCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 120;
        }

        public static final class RabbitCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 100;
        }

        public static final class HareCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 100;
        }

        public static final class BoarCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 160;
        }

        public static final class GazelleCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;
        }

        public static final class WildebeestCFG
        {
            @Config.Comment("How rare this animal should be, in 1 / N chunks, on valid biomes (this is used on chunk generation only)? 0 = Disable.")
            @Config.RangeInt(min = 0)
            @Config.LangKey("config." + MOD_ID + ".animals.rarity")
            public int rarity = 150;
        }
    }
}
