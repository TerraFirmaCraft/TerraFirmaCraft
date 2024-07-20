/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.EnumMap;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.config.animals.OviparousAnimalConfig;
import net.dries007.tfc.config.animals.ProducingMammalConfig;
import net.dries007.tfc.util.FluidAlloy;

/**
 * Server Config
 * - synced, stored per world, can be shipped per instance with default configs
 * - use for the majority of config options, or any that need to be present on both sides
 */
public class ServerConfig extends BaseConfig
{
    // General
    public final BooleanValue enableNetherPortals;
    public final BooleanValue enableForcedTFCGameRules;
    public final BooleanValue enableTimeStopWhenServerEmpty;
    public final BooleanValue enableFireArrowSpreading;
    public final DoubleValue fireStarterChance;
    public final BooleanValue enableInfestations;
    public final BooleanValue enableCalendarSensitiveMoonPhases;
    public final BooleanValue enableLightning;
    public final BooleanValue enableLightningStrippingLogs;
    public final IntValue oceanWindScale;

    // Blocks - Farmland
    public final BooleanValue enableFarmlandCreation;
    // Blocks - Grass Path
    public final BooleanValue enableGrassPathCreation;
    // Blocks - Grass
    public final DoubleValue grassSpawningRocksChance;
    // Blocks - Rooted Dirt
    public final BooleanValue enableRootedDirtToDirtCreation;
    // Blocks - Mud
    public final BooleanValue enableDirtToMudCreation;
    // Blocks - Snow
    public final BooleanValue enableSnowSlowEntities;
    public final IntValue snowAccumulateChance;
    public final IntValue snowMeltChance;
    // Blocks - Leaves
    public final DoubleValue leavesMovementModifier;
    // Blocks - Plants
    public final DoubleValue plantGrowthChance;
    public final DoubleValue plantLongGrowthChance;
    public final DoubleValue plantSpreadChance;
    public final DoubleValue plantsMovementModifier;
    // Blocks - Cobblestone
    public final BooleanValue enableMossyRockSpreading;
    public final IntValue mossyRockSpreadRate;
    // Blocks - Chest
    public final EnumValue<Size> chestMaximumItemSize;
    // Blocks - Large Vessel
    public final BooleanValue largeVesselEnableAutomation;
    // Blocks - Quern
    public final BooleanValue quernEnableAutomation;
    // Blocks - Torch
    public final IntValue torchTicks;
    // Blocks - Torch
    public final IntValue candleTicks;
    // Blocks - Drying Bricks
    public final IntValue mudBricksTicks;
    // Blocks - Charcoal Pit
    public final IntValue charcoalTicks;
    // Blocks - Pit Kiln
    public final IntValue pitKilnTicks;
    public final IntValue pitKilnTemperature;
    // Blocks - Crucible
    public final IntValue crucibleCapacity;
    public final IntValue cruciblePouringRate;
    public final IntValue crucibleFastPouringRate;
    public final BooleanValue crucibleEnableAutomation;
    // Blocks - Anvil
    public final IntValue anvilAcceptableWorkRange;
    public final DoubleValue anvilModestlyForgedThreshold;
    public final DoubleValue anvilWellForgedThreshold;
    public final DoubleValue anvilExpertForgedThreshold;
    public final DoubleValue anvilPerfectlyForgedThreshold;
    public final DoubleValue anvilMaxEfficiencyMultiplier;
    public final DoubleValue anvilMaxDurabilityMultiplier;
    public final DoubleValue anvilMaxDamageMultiplier;
    // Blocks - Barrel
    public final IntValue barrelCapacity;
    public final BooleanValue barrelEnableAutomation;
    public final BooleanValue barrelEnableRedstoneSeal;
    // Blocks - Large Vessel
    public final BooleanValue largeVesselEnableRedstoneSeal;
    // Blocks - Composter
    public final IntValue composterTicks;
    public final BooleanValue composterEnableAutomation;
    // Blocks - Sluice
    public final IntValue sluiceTicks;
    // Blocks - Lamp
    public final IntValue lampCapacity;
    // Blocks - Pumpkin
    public final BooleanValue enablePumpkinCarving;
    public final IntValue jackOLanternTicks;
    // Blocks - Bloomery
    public final IntValue bloomeryCapacity;
    public final IntValue bloomeryMaxChimneyHeight;
    // Blocks - Blast Furnace
    public final IntValue blastFurnaceCapacity;
    public final IntValue blastFurnaceFluidCapacity;
    public final IntValue blastFurnaceFuelConsumptionMultiplier;
    public final IntValue blastFurnaceMaxChimneyHeight;
    public final BooleanValue blastFurnaceEnableAutomation;
    // Blocks - Loom
    public final BooleanValue loomEnableAutomation;
    // Blocks - Thatch
    public final DoubleValue thatchMovementMultiplier;
    // Blocks - Thatch Bed
    public final BooleanValue enableThatchBedSpawnSetting;
    public final BooleanValue enableThatchBedSleeping;
    public final BooleanValue thatchBedNoSleepInThunderstorms;
    // Blocks - Item Size
    public final EnumValue<Size> maxPlacedItemSize;
    public final EnumValue<Size> maxPlacedLargeItemSize;
    public final BooleanValue enablePlacingItems;
    public final BooleanValue usePlacedItemWhitelist;
    // Blocks - Leaves
    public final BooleanValue enableLeavesDecaySlowly;
    // Blocks - Charcoal Forge
    public final BooleanValue charcoalForgeEnableAutomation;
    // Blocks - Fire Pit
    public final BooleanValue firePitEnableAutomation;
    // Blocks - Nest Box
    public final BooleanValue nestBoxEnableAutomation;
    // Blocks - Powder Keg
    public final BooleanValue powderKegEnabled;
    public final BooleanValue powderKegOnlyBreaksNaturalBlocks;
    public final BooleanValue powderKegEnableAutomation;
    public final DoubleValue powderKegStrengthModifier;
    public final IntValue powderKegFuseTime;
    // Blocks - Hot Water
    public final DoubleValue hotWaterHealAmount;
    // Blocks - Sapling
    public final DoubleValue globalSaplingGrowthModifier;
    public final DoubleValue globalFruitSaplingGrowthModifier;
    public final EnumMap<Wood, IntValue> saplingGrowthDays;
    public final EnumMap<FruitBlocks.Tree, IntValue> fruitSaplingGrowthDays;
    public final IntValue bananaSaplingGrowthDays;
    // Blocks - Crops
    public final DoubleValue cropGrowthModifier;
    public final DoubleValue cropExpiryModifier;
    // Blocks - Dispenser
    public final BooleanValue dispenserEnableLighting;
    // Blocks - Powder Bowl
    public final BooleanValue powderBowlEnableAutomation;

    // Items - Small Vessel
    public final IntValue smallVesselCapacity;
    public final EnumValue<Size> smallVesselMaximumItemSize;
    public final BooleanValue enableSmallVesselInventoryInteraction;
    // Items - Mold(s)
    public final IntValue moldIngotCapacity;
    public final IntValue moldFireIngotCapacity;
    public final IntValue moldPickaxeHeadCapacity;
    public final IntValue moldPropickHeadCapacity;
    public final IntValue moldAxeHeadCapacity;
    public final IntValue moldShovelHeadCapacity;
    public final IntValue moldHoeHeadCapacity;
    public final IntValue moldChiselHeadCapacity;
    public final IntValue moldHammerHeadCapacity;
    public final IntValue moldSawBladeCapacity;
    public final IntValue moldJavelinHeadCapacity;
    public final IntValue moldSwordBladeCapacity;
    public final IntValue moldMaceHeadCapacity;
    public final IntValue moldKnifeBladeCapacity;
    public final IntValue moldScytheBladeCapacity;
    public final IntValue moldBellCapacity;
    // Items - Jug
    public final IntValue jugCapacity;
    public final DoubleValue jugBreakChance;
    // Items - Glass Bottle
    public final IntValue silicaGlassBottleCapacity;
    public final DoubleValue silicaGlassBottleBreakChance;
    public final IntValue hematiticGlassBottleCapacity;
    public final DoubleValue hematiticGlassBottleBreakChance;
    public final IntValue volcanicGlassBottleCapacity;
    public final DoubleValue volcanicGlassBottleBreakChance;
    public final IntValue olivineGlassBottleCapacity;
    public final DoubleValue olivineGlassBottleBreakChance;
    // Items - Wooden Bucket
    public final IntValue woodenBucketCapacity;
    // Mechanics - Heat
    public final DoubleValue deviceHeatingModifier;
    public final DoubleValue itemHeatingModifier;
    public final DoubleValue itemCoolingModifier;
    public final IntValue ticksBeforeItemCool;
    public final BooleanValue coolHotItemEntities;
    // Mechanics - Collapses
    public final BooleanValue enableBlockCollapsing;
    public final BooleanValue enableExplosionCollapsing;
    public final BooleanValue enableBlockLandslides;
    public final BooleanValue enableChiselsStartCollapses;
    public final DoubleValue collapseTriggerChance;
    public final DoubleValue collapseFakeTriggerChance;
    public final DoubleValue collapsePropagateChance;
    public final DoubleValue collapseExplosionPropagateChance;
    public final IntValue collapseMinRadius;
    public final IntValue collapseRadiusVariance;
    // Mechanics - Player
    public final BooleanValue enablePeacefulDifficultyPassiveRegeneration;
    public final DoubleValue passiveExhaustionModifier;
    public final DoubleValue thirstModifier;
    public final BooleanValue enableThirstOverheating;
    public final DoubleValue thirstGainedFromDrinkingInTheRain;
    public final DoubleValue naturalRegenerationModifier;
    public final IntValue nutritionRotationHungerWindow;
    public final BooleanValue keepNutritionAfterDeath;
    public final IntValue foodDecayStackWindow;
    public final DoubleValue foodDecayModifier;
    public final BooleanValue enableOverburdening;
    public final DoubleValue nutritionMinimumHealthModifier;
    public final DoubleValue nutritionDefaultHealthModifier;
    public final DoubleValue nutritionMaximumHealthModifier;
    // Mechanics - Food Traits
    public final DoubleValue traitSaltedModifier;
    public final DoubleValue traitBrinedModifier;
    public final DoubleValue traitPickledModifier;
    public final DoubleValue traitPreservedModifier;
    public final DoubleValue traitVinegarModifier;
    public final DoubleValue traitCharcoalGrilledModifier;
    public final DoubleValue traitWoodGrilledModifier;
    public final DoubleValue traitBurntToACrispModifier;
    public final DoubleValue traitWildModifier;
    // Mechanics - Fluids
    public final BooleanValue enableBucketsPlacingSources;
    // Mechanics - Vanilla Changes
    public final BooleanValue enableVanillaBonemeal;
    public final BooleanValue enableVanillaWeatherEffects;
    public final BooleanValue enableVanillaSkeletonHorseSpawning;
    public final BooleanValue enableVanillaMobsSpawningWithEnchantments;
    public final BooleanValue enableVanillaMobsSpawningWithVanillaEquipment;
    public final BooleanValue enableVanillaGolems;
    public final BooleanValue enableVanillaMonsters;
    public final BooleanValue enableVanillaMonstersOnSurface;
    public final BooleanValue enableChickenJockies;
    public final BooleanValue enableVanillaEggThrowing;
    public final BooleanValue enableVanillaDrinkingMilkClearsPotionEffects;

    // Animals
    public final MammalConfig pigConfig;
    public final MammalConfig donkeyConfig;
    public final MammalConfig muleConfig;
    public final MammalConfig horseConfig;
    public final MammalConfig catConfig;
    public final MammalConfig dogConfig;
    public final MammalConfig rabbitConfig;
    public final ProducingMammalConfig cowConfig;
    public final ProducingMammalConfig alpacaConfig;
    public final OviparousAnimalConfig chickenConfig;
    public final ProducingMammalConfig yakConfig;
    public final ProducingMammalConfig goatConfig;
    public final ProducingMammalConfig sheepConfig;
    public final ProducingMammalConfig muskOxConfig;
    public final OviparousAnimalConfig duckConfig;
    public final OviparousAnimalConfig quailConfig;
    public final DoubleValue familiarityDecayLimit;

    // Below Everything
    public final BooleanValue farmlandMakesTheBestRaceTracks;

    ServerConfig(ConfigBuilder builder)
    {
        builder.push("general");

        enableNetherPortals = builder.comment("Enable nether portal creation").define("enableNetherPortals", false);
        enableForcedTFCGameRules = builder.comment(
            "Forces a number of game rules to specific values.",
            "  naturalRegeneration = false (Health regen is much slower and not tied to extra saturation)",
            "  doInsomnia = false (No phantoms)",
            "  doTraderSpawning = false (No wandering traders)",
            "  doPatrolSpawning = false (No pillager patrols)"
        ).define("enableForcedTFCGameRules", true);
        enableTimeStopWhenServerEmpty = builder.comment(
            "If true, TFC will stop time when no players are online on a running server.",
            "This prevents food from decaying, the calendar from advancing, and the sun from moving, etc.",
            "!!Disable at your own risk!!"
        ).define("enableTimeStopWhenServerEmpty", true);
        enableFireArrowSpreading = builder.comment("Enable fire arrows and fireballs to spread fire and light blocks.").define("enableFireArrowSpreading", true);
        fireStarterChance = builder.comment("Base probability for a firestarter to start a fire. May change based on circumstances").define("fireStarterChance", 0.5, 0, 1);
        enableInfestations = builder.comment("Enable rat infestations for improperly stored food.").define("enableInfestations", true);
        enableCalendarSensitiveMoonPhases = builder.comment("Enables TFC setting the moon phase based on the progress of the month. The etymology of the English word 'month' is in fact related to the word 'moon'.").define("enableCalendarSensitiveMoonPhases", true);
        enableLightning = builder.comment("If false, vanilla lightning will not strike.").define("enableLightning", true);
        enableLightningStrippingLogs = builder.comment("If true, lightning has a chance of stripping bark off of trees.").define("enableLightningStrippingLogs", true);
        oceanWindScale = builder.comment("Every time the z coordinate reaches a multiple of this point, the wind over oceans will switch directions.").define("oceanWindScale", 5000, 128, Integer.MAX_VALUE);

        builder.swap("blocks").push("farmland");

        enableFarmlandCreation = builder.comment("If TFC soil blocks are able to be created into farmland using a hoe.").define("enableFarmlandCreation", true);

        builder.swap("grassPath");

        enableGrassPathCreation = builder.comment("If TFC soil blocks are able to be created into (grass) path blocks using a hoe.").define("enableGrassPathCreation", true);

        builder.swap("grass");

        grassSpawningRocksChance = builder.comment("The chance that when grass that freezes and thaws it will have a loose rock rise to the surface, provided some conditions are met. Set to 0 to disable.").define("grassSpawningRocksChance", 0.001, 0, 1);

        builder.swap("rootedDirt");

        enableRootedDirtToDirtCreation = builder.comment("If TFC rooted dirt blocks are able to be created into dirt blocks using a hoe.").define("enableRootedDirtToDirtCreation", true);

        builder.swap ("mud");

        enableDirtToMudCreation = builder.comment("If TFC dirt blocks are able to be created into mud blocks using a water-filled fluid container.").define("enableDirtToMudCreation", true);

        builder.swap("snow");

        enableSnowSlowEntities = builder.comment("[Requires MC Restart] If snow will slow players that move on top of it similar to soul sand or honey.").define("enableSnowSlowEntities", true);
        snowAccumulateChance = builder.comment("The chance that snow will accumulate during a storm. Lower values = faster snow accumulation, but also more block updates (aka lag).").define("snowAccumulateChance", 20, 1, Integer.MAX_VALUE);
        snowMeltChance = builder.comment("The chance that snow will melt during a storm. Lower values = faster snow melting, but also more block updates (aka lag).").define("snowMeltChance", 36, 1, Integer.MAX_VALUE);

        builder.swap("plants");

        plantGrowthChance = builder.comment("Chance for a plant to grow each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant growth.").define("plantGrowthChance", 0.04, 0, 1);
        plantLongGrowthChance = builder.comment("Chance for a twisting/weeping/kelp plant to grow each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant growth.").define("plantLongGrowthChance", 0.03, 0, 1);
        plantSpreadChance = builder.comment("Chance for a plant to spread each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant spreading.").define("plantSpreadChance", 0.001, 0, 1);
        plantsMovementModifier = builder.comment("A movement multiplier for players moving through plants. Individual plants will use a ratio of this value, and lower = slower.").define("plantsMovementModifier", 0.2, 0, 1);

        builder.swap("leaves");

        leavesMovementModifier = builder.comment("A movement multiplier for players moving through leaves. Lower = slower.").define("leavesMovementModifier", 0.4, 0, 1);

        builder.swap("cobblestone");

        enableMossyRockSpreading = builder.comment("If mossy rock blocks will spread their moss to nearby rock blocks (bricks and cobble; stairs, slabs and walls thereof).").define("enableMossyRockSpreading", true);
        mossyRockSpreadRate = builder.comment("The rate at which rock blocks will accumulate moss. Higher value = slower.").define("mossyRockSpreadRate", 20, 1, Integer.MAX_VALUE);

        builder.swap("chest");
        chestMaximumItemSize = builder.comment("The largest (inclusive) size of an item that is allowed in a chest.").define("chestMaximumItemSize", Size.LARGE);

        builder.swap("largeVessel");

        largeVesselEnableAutomation = builder.comment("If true, large vessels will interact with in-world automation such as hoppers on a side-specific basis.").define("largeVesselEnableAutomation", true);

        builder.swap("quern");

        quernEnableAutomation = builder.comment("If true, querns will interact with in-world automation such as hoppers on a side-specific basis.").define("quernEnableAutomation", true);

        builder.swap("torch");

        torchTicks = builder.comment("Number of ticks required for a torch to burn out (1000 = 1 in game hour = 50 seconds), default is 72 hours. Set to -1 to disable torch burnout.").define("torchTicks", 72000, -1, Integer.MAX_VALUE);

        builder.swap("candle");

        candleTicks = builder.comment("Number of ticks required for a candle to burn out (1000 = 1 in game hour = 50 seconds), default is 264 hours. Set to -1 to disable candle burnout.").define("candleTicks", 264000, -1, Integer.MAX_VALUE);

        builder.swap("dryingBricks");

        mudBricksTicks = builder.comment("Number of ticks required for mud bricks to dry (1000 = 1 in game hour = 50 seconds), default is 24 hours. Set to -1 to disable drying.").define("mudBricksTicks",  24000, -1, Integer.MAX_VALUE);

        builder.swap("charcoal");

        charcoalTicks = builder.comment("Number of ticks required for charcoal pit to complete. (1000 = 1 in game hour = 50 seconds), default is 18 hours.").define("charcoalTicks", 18000, -1, Integer.MAX_VALUE);

        builder.swap("pitKiln");

        pitKilnTicks = builder.comment("Number of ticks required for a pit kiln to burn out. (1000 = 1 in game hour = 50 seconds), default is 8 hours.").define("pitKilnTicks", 8000, 20, Integer.MAX_VALUE);
        pitKilnTemperature = builder.comment("The maximum temperature which a pit kiln reaches.").define("pitKilnTemperature", 1400, 0, Integer.MAX_VALUE);

        builder.swap("crucible");

        crucibleCapacity = builder.comment("Tank capacity of a crucible (in mB).").define("crucibleCapacity", 4000, 0, FluidAlloy.MAX_ALLOY);
        cruciblePouringRate = builder.comment("A modifier for how fast fluid containers empty into crucibles. Containers will empty 1 mB every (this) number of ticks.").define("cruciblePouringRate", 4, 1, Integer.MAX_VALUE);
        crucibleFastPouringRate = builder.comment("A modifier for how fast fluid containers empty into crucibles when shift is held. Containers will empty 1 mB every (this) number of ticks.").define("crucibleFastPouringRate", 1, 1, Integer.MAX_VALUE);
        crucibleEnableAutomation = builder.comment("If true, barrels will interact with in-world automation such as hoppers on a side-specific basis.").define("crucibleEnableAutomation", true);

        builder.swap("anvil");

        anvilAcceptableWorkRange = builder.comment("The number of pixels that the anvil's result may be off by, but still count as recipe completion. By default this requires pixel perfect accuracy.").define("anvilAcceptableWorkRange", 0, 0, 150);
        anvilModestlyForgedThreshold = builder.comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Modestly Forged'.").define("anvilModestlyForgedThreshold", 10.0, 1.0, Double.MAX_VALUE);
        anvilWellForgedThreshold = builder.comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Well Forged'.").define("anvilWellForgedThreshold", 5.0, 1.0, Double.MAX_VALUE);
        anvilExpertForgedThreshold = builder.comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Expertly Forged'.").define("anvilExpertForgedThreshold", 2.0, 1.0, Double.MAX_VALUE);
        anvilPerfectlyForgedThreshold = builder.comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Perfectly Forged'.").define("anvilPerfectlyForgedThreshold", 1.5, 1.0, Double.MAX_VALUE);
        anvilMaxEfficiencyMultiplier = builder.comment("The multiplier to efficiency (mining speed) that is applied to a 'Perfectly Forged' tool.").define("anvilMaxEfficiencyMultiplier", 1.8, 1, 1000);
        anvilMaxDurabilityMultiplier = builder.comment("The bonus to durability (probability to ignore a point of damage) that is applied to a 'Perfectly Forged' tool. Note that 1 ~ infinite durability, and 0 ~ no bonus.").define("anvilMaxDurabilityMultiplier", 0.5, 0, 1);
        anvilMaxDamageMultiplier = builder.comment("The boost to attack damage that is applied to a 'Perfectly Forged' tool.").define("anvilMaxDamageMultiplier", 1.5, 1, 1000);

        builder.swap("barrel");

        barrelCapacity = builder.comment("Tank capacity of a barrel (in mB).").define("barrelCapacity", 10000, 0, Integer.MAX_VALUE);
        barrelEnableAutomation = builder.comment("If true, barrels will interact with in-world automation such as hoppers on a side-specific basis.").define("barrelEnableAutomation", true);
        barrelEnableRedstoneSeal = builder.comment("If true, barrels will seal and unseal on redstone signal.").define("barrelEnableRedstoneSeal", true);

        builder.swap("largeVessel");

        largeVesselEnableRedstoneSeal = builder.comment("If true, large vessels will seal and unseal on redstone signal.").define("largeVesselEnableRedstoneSeal", true);

        builder.swap("composter");

        composterTicks = builder.comment("Number of ticks required for a composter in normal conditions to complete. (24000 = 1 game day), default is 12 days.").define("composterTicks", 288000, 20, Integer.MAX_VALUE);
        composterEnableAutomation = builder.comment("If true, the composter will interact with in-world automation such as hoppers on a side-specific basis.").define("composterEnableAutomation", true);

        builder.swap("sluice");
        sluiceTicks = builder.comment("Number of ticks required for a sluice to process an item. (20 = 1 second), default is 5 seconds.").define("sluiceTicks", 100, 1, Integer.MAX_VALUE);

        builder.swap("lamp");

        lampCapacity = builder.comment("Tank capacity of a lamp (in mB).").define("lampCapacity", 250, 0, FluidAlloy.MAX_ALLOY);

        builder.swap("pumpkin");

        enablePumpkinCarving = builder.comment("Enables the knifing of pumpkins to carve them.").define("enablePumpkinCarving", true);
        jackOLanternTicks = builder.comment("Number of ticks required for a jack 'o lantern to burn out (1000 = 1 in game hour = 50 seconds), default is 108 hours. Set to -1 to disable burnout.").define("jackOLanternTicks", 108000, -1, Integer.MAX_VALUE);

        builder.swap("bloomery");

        bloomeryCapacity = builder.comment("Inventory capacity (in number of items per level of chimney) of the bloomery.").define("bloomeryCapacity", 16, 1, Integer.MAX_VALUE);
        bloomeryMaxChimneyHeight = builder.comment("The maximum number of levels that can be built in a bloomery multiblock, for added capacity.").define("bloomeryMaxChimneyHeight", 3, 1, Integer.MAX_VALUE);

        builder.swap("blastFurnace");

        blastFurnaceCapacity = builder.comment("Inventory capacity (in number of items per level of chimney) of the blast furnace.").define("blastFurnaceCapacity", 4, 1, Integer.MAX_VALUE);
        blastFurnaceFluidCapacity = builder.comment("Fluid capacity (in mB) of the output tank of the blast furnace.").define("blastFurnaceFluidCapacity", 10_000, 1, Integer.MAX_VALUE);
        blastFurnaceFuelConsumptionMultiplier = builder.comment("A multiplier for how fast the blast furnace consumes fuel. Higher values = faster fuel consumption.").define("blastFurnaceFuelConsumptionMultiplier", 4, 1, Integer.MAX_VALUE);
        blastFurnaceMaxChimneyHeight = builder.comment("The maximum number of levels that can be built in a blast furnace multiblock, for added capacity.").define("blastFurnaceMaxChimneyHeight", 5, 1, Integer.MAX_VALUE);
        blastFurnaceEnableAutomation = builder.comment("If true, blast furnaces will interact with in-world automation such as hoppers on a side-specific basis.").define("blastFurnaceEnableAutomation", true);

        builder.swap("loom");

        loomEnableAutomation = builder.comment("If true, looms will interact with in-world automation such as hoppers on a side-specific basis.").define("loomEnableAutomation", true);

        builder.swap("thatch");

        thatchMovementMultiplier = builder.comment("A movement multiplier for players moving through thatch. Lower = slower.").define("thatchMovementMultiplier", 0.6, 0, 1);

        builder.swap("thatchBed");

        enableThatchBedSpawnSetting = builder.comment("If true, thatch beds can set the player's spawn.").define("enableThatchBedSpawnSetting", true);
        enableThatchBedSleeping = builder.comment("If true, the player can sleep the night in a thatch bed").define("enableThatchBedSleeping", false);
        thatchBedNoSleepInThunderstorms = builder.comment("If true, the player cannot sleep in thatch beds during thunderstorms.").define("thatchBedNoSleepInThunderstorms", true);

        builder.swap("leaves");

        enableLeavesDecaySlowly = builder.comment("If true, then leaves will decay slowly over time when disconnected from logs (vanilla behavior), as opposed to instantly (TFC behavior).").define("enableLeavesDecaySlowly", false);

        builder.swap("placedItems");

        maxPlacedItemSize = builder.comment("The maximum size of items that can be placed as 4 items on the ground with V. If an item is larger than this, it could still be placed with the 'maxPlacedLargeItemSize' option.").define("maxPlacedItemSize", Size.LARGE);
        maxPlacedLargeItemSize = builder.comment("The maximum size of items that can be placed as a single item on the ground with V. Items are checked to see if they're the right size to be placed in a group of 4 items first.").define("maxPlacedLargeItemSize", Size.HUGE);
        enablePlacingItems = builder.comment("If true, players can place items on the ground with V.").define("enablePlacingItems", true);
        usePlacedItemWhitelist = builder.comment("If true, the tag 'tfc:placed_item_whitelist' will be checked to allow items to be in placed items and will exclude everything else.").define("usePlacedItemWhitelist", false);

        builder.swap("charcoalForge");

        charcoalForgeEnableAutomation = builder.comment("If true, charcoal forges will interact with in-world automation such as hoppers on a side-specific basis.").define("charcoalForgeEnableAutomation", true);

        builder.swap("firePitEnableAutomation");

        firePitEnableAutomation = builder.comment("If true, fire pits will interact with in-world automation such as hoppers on a side-specific basis.").define("firePitEnableAutomation", true);

        builder.swap("nestBox");

        nestBoxEnableAutomation = builder.comment("If true, nest boxes will interact with in-world automation such as hoppers on a side-specific basis.").define("nestBoxEnableAutomation", true);

        builder.swap("powderKeg");

        powderKegEnabled = builder.comment("If true, powder kegs can be lit and exploded.").define("powderKegEnabled", true);
        powderKegOnlyBreaksNaturalBlocks = builder.comment("If true, powder kegs will only break stone, ores, gravel, and dirt.").define("powderKegOnlyBreaksNaturalBlocks", false);
        powderKegEnableAutomation = builder.comment("If true, powder kegs will interact with in-world automation such as hoppers on a side-specific basis.").define("powderKegEnableAutomation", true);
        powderKegStrengthModifier = builder.comment("A modifier to the strength of powderkegs when exploding. A max powderkeg explosion is 64, and all explosions are capped to this size no matter the value of the modifier.").define("powderKegStrengthModifier", 1d, 0, 64);
        powderKegFuseTime = builder.comment("The time in ticks for a powderkeg to defuse. Default is 80 ticks, or 4 seconds.").define("powderKegFuseTime", 80, 1, Integer.MAX_VALUE);

        builder.swap("hotWater");

        hotWaterHealAmount = builder.comment("An amount that sitting in hot water will restore health, approximately twice per second.").define("hotWaterHealAmount", 0.08, 0.0, 20.0);

        builder.swap("saplings");

        globalSaplingGrowthModifier = builder.comment("Modifier applied to the growth time of every (non-fruit) sapling. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").define("globalSaplingGrowthModifier", 1d, 0d, Double.MAX_VALUE);
        globalFruitSaplingGrowthModifier = builder.comment("Modifier applied to the growth time of every fruit tree sapling. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").define("globalFruitSaplingGrowthModifier", 1d, 0d, Double.MAX_VALUE);

        saplingGrowthDays = new EnumMap<>(Wood.class);
        for (Wood wood : Wood.VALUES)
        {
            final String valueName = String.format("%sSaplingGrowthDays", wood.getSerializedName());
            saplingGrowthDays.put(wood, builder.comment(String.format("Days for a %s tree sapling to be ready to grow into a full tree.", wood.getSerializedName())).define(valueName, wood.defaultDaysToGrow(), 0, Integer.MAX_VALUE));
        }
        fruitSaplingGrowthDays = new EnumMap<>(FruitBlocks.Tree.class);
        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            final String valueName = String.format("%sSaplingGrowthDays", tree.getSerializedName());
            fruitSaplingGrowthDays.put(tree, builder.comment(String.format("Days for a %s tree sapling to be eligible to grow", tree.getSerializedName())).define(valueName, tree.defaultDaysToGrow(), 0, Integer.MAX_VALUE));
        }
        bananaSaplingGrowthDays = builder.comment("Days for a banana tree sapling to be eligible to grow").define("bananaSaplingGrowthDays", 6, 0, Integer.MAX_VALUE);

        builder.swap("crops");

        cropGrowthModifier = builder.comment("Modifier applied to the growth time of every crop. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").define("cropGrowthModifier", 1, 0.001, 1000);
        cropExpiryModifier = builder.comment("Modifier applied to the expiry time of every crop. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").define("cropExpiryModifier", 1, 0.001, 1000);

        builder.swap("dispenser");

        dispenserEnableLighting = builder.comment("If true, dispensers can light TFC devices.").define("dispenserEnableLighting", true);

        builder.swap("powderBowl");

        powderBowlEnableAutomation = builder.comment("If true, powder bowls will interact with in-world automation such as hoppers on a side-specific basis.").define("powderBowlEnableAutomation", true);

        builder.pop().swap("items").push("smallVessel");

        smallVesselCapacity = builder.comment("Tank capacity of a small vessel (in mB).").define("smallVesselCapacity", 3000, 0, FluidAlloy.MAX_ALLOY);
        smallVesselMaximumItemSize = builder.comment("The largest (inclusive) size of an item that is allowed in a small vessel.").define("smallVesselMaximumItemSize", Size.SMALL);
        enableSmallVesselInventoryInteraction = builder.comment("If true, the vessel can be inserted and extracted from by clicking on it in the inventory. You may want to disable this if you have a inventory-tweaking mod").define("enableSmallVesselInventoryInteraction", true);

        builder.swap("molds");

        moldIngotCapacity = builder.comment("Tank capacity of a Ingot mold (in mB).").define("moldIngotCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldFireIngotCapacity = builder.comment("Tank capacity of a Fire Ingot mold (in mB).").define("moldIngotCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldPickaxeHeadCapacity = builder.comment("Tank capacity of a Pickaxe Head mold (in mB).").define("moldPickaxeHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldPropickHeadCapacity = builder.comment("Tank capacity of a Propick Head mold (in mB).").define("moldPropickHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldAxeHeadCapacity = builder.comment("Tank capacity of a Axe Head mold (in mB).").define("moldAxeHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldShovelHeadCapacity = builder.comment("Tank capacity of a Shovel Head mold (in mB).").define("moldShovelHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldHoeHeadCapacity = builder.comment("Tank capacity of a Hoe Head mold (in mB).").define("moldHoeHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldChiselHeadCapacity = builder.comment("Tank capacity of a Chisel Head mold (in mB).").define("moldChiselHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldHammerHeadCapacity = builder.comment("Tank capacity of a Hammer Head mold (in mB).").define("moldHammerHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldSawBladeCapacity = builder.comment("Tank capacity of a Saw Blade mold (in mB).").define("moldSawBladeCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldJavelinHeadCapacity = builder.comment("Tank capacity of a Javelin Head mold (in mB).").define("moldJavelinHeadCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldSwordBladeCapacity = builder.comment("Tank capacity of a Sword Blade mold (in mB).").define("moldSwordBladeCapacity", 200, 0, FluidAlloy.MAX_ALLOY);
        moldMaceHeadCapacity = builder.comment("Tank capacity of a Mace Head mold (in mB).").define("moldMaceHeadCapacity", 200, 0, FluidAlloy.MAX_ALLOY);
        moldKnifeBladeCapacity = builder.comment("Tank capacity of a Knife Blade mold (in mB).").define("moldKnifeBladeCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldScytheBladeCapacity = builder.comment("Tank capacity of a Scythe Blade mold (in mB).").define("moldScytheBladeCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        moldBellCapacity = builder.comment("Tank capacity of a Bell mold (in mB).").define("moldScytheBladeCapacity", 100, 0, FluidAlloy.MAX_ALLOY);

        builder.swap("jug");

        jugCapacity = builder.comment("Tank capacity of a ceramic jug (in mB).").define("jugCapacity", 100, 0, FluidAlloy.MAX_ALLOY);
        jugBreakChance = builder.comment("The chance a jug will break after drinking.").define("jugBreakChance", 0.02, 0, 1);

        builder.swap("glassBottle");

        silicaGlassBottleCapacity = builder.comment("Tank capacity of a silica glass bottle (in mB).").define("silicaGlassBottleCapacity", 500, 0, FluidAlloy.MAX_ALLOY);
        silicaGlassBottleBreakChance = builder.comment("The chance a silica glass bottle will break after drinking.").define("silicaGlassBottleBreakChance", 0.005, 0, 1);
        hematiticGlassBottleCapacity = builder.comment("Tank capacity of a hematitic glass bottle (in mB).").define("hematiticGlassBottleCapacity", 400, 0, FluidAlloy.MAX_ALLOY);
        hematiticGlassBottleBreakChance = builder.comment("The chance a hematitic glass bottle will break after drinking.").define("hematiticGlassBottleBreakChance", 0.02, 0, 1);
        volcanicGlassBottleCapacity = builder.comment("Tank capacity of a volcanic glass bottle (in mB).").define("volcanicGlassBottleCapacity", 400, 0, FluidAlloy.MAX_ALLOY);
        volcanicGlassBottleBreakChance = builder.comment("The chance a volcanic glass bottle will break after drinking.").define("volcanicGlassBottleBreakChance", 0.04, 0, 1);
        olivineGlassBottleCapacity = builder.comment("Tank capacity of a olivine glass bottle (in mB).").define("olivineGlassBottleCapacity", 400, 0, FluidAlloy.MAX_ALLOY);
        olivineGlassBottleBreakChance = builder.comment("The chance a olivine glass bottle will break after drinking.").define("olivineGlassBottleBreakChance", 0.01, 0, 1);

        builder.swap("woodenBucket");
        woodenBucketCapacity = builder.comment("Tank capacity of a wooden bucket (in mB).").define("woodenBucketCapacity", 1000, 0, FluidAlloy.MAX_ALLOY);

        builder.pop().swap("mechanics").push("heat");

        deviceHeatingModifier = builder.comment("A multiplier for how fast devices themselves heat up. Higher = faster.").define("deviceHeatingModifier", 1, 0, Double.MAX_VALUE);
        itemHeatingModifier = builder.comment("A multiplier for how fast items heat in devices. Higher = faster.").define("itemHeatingModifier", 1, 0, Double.MAX_VALUE);
        itemCoolingModifier = builder.comment("A multiplier for how fast items cool. Higher = faster.").define("itemCoolingModifier", 0.8, 0, Double.MAX_VALUE);
        coolHotItemEntities = builder.comment("Should hot item entities cool off when in contact with blocks like water or snow?").define("coolHotItemEntities", true);
        ticksBeforeItemCool = builder.comment("Ticks between each time an item loses temperature when sitting on a cold block. 20 ticks = 1 second.").define("ticksBeforeItemCool", 10, 1, Integer.MAX_VALUE);

        builder.swap("collapses");

        enableBlockCollapsing = builder.comment("Enable rock collapsing when mining raw stone blocks").define("enableBlockCollapsing", true);
        enableExplosionCollapsing = builder.comment("Enable explosions causing immediate collapses.").define("enableExplosionCollapsing", true);
        enableBlockLandslides = builder.comment("Enable land slides (gravity affected blocks) when placing blocks or on block updates.").define("enableBlockLandslides", true);
        enableChiselsStartCollapses = builder.comment("Enable chisels starting collapses").define("enableChiselsStartCollapses", true);

        collapseTriggerChance = builder.comment("Chance for a collapse to be triggered by mining a block.").define("collapseTriggerChance", 0.1, 0, 1);
        collapseFakeTriggerChance = builder.comment("Chance for a collapse to be fake triggered by mining a block.").define("collapseFakeTriggerChance", 0.35, 0, 1);
        collapsePropagateChance = builder.comment("Chance for a block fo fall from mining collapse. Higher = more likely.").define("collapsePropagateChance", 0.55, 0, 1);
        collapseExplosionPropagateChance = builder.comment("Chance for a block to fall from an explosion triggered collapse. Higher = mor likely.").define("collapseExplosionPropagateChance", 0.3, 0, 1);
        collapseMinRadius = builder.comment("Minimum radius for a collapse").define("collapseMinRadius", 3, 1, 32);
        collapseRadiusVariance = builder.comment("Variance of the radius of a collapse. Total size is in [minRadius, minRadius + radiusVariance]").define("collapseRadiusVariance", 16, 1, 32);

        builder.swap("player");

        enablePeacefulDifficultyPassiveRegeneration = builder.comment("If peaceful difficulty should still have vanilla-esque passive regeneration of health, food, and hunger").define("enablePeacefulDifficultyPassiveRegeneration", false);
        passiveExhaustionModifier = builder.comment(
            "A multiplier for passive exhaustion accumulation.",
            "Exhaustion is the hidden stat which controls when you get hungry. In vanilla it is incremented by running and jumping for example. In TFC, exhaustion is added just by existing.",
            "1.0 = A full hunger bar's worth of exhaustion every 2.5 days. Set to zero to disable completely.").define("passiveExhaustionMultiplier", 1d, 0d, 100d);
        thirstModifier = builder.comment(
            "A multiplier for how quickly the player gets thirsty.",
            "The player loses thirst in sync with when they lose hunger. This represents how much thirst they lose. 0 = None, 100 = the entire thirst bar.").define("thirstModifier", 5d, 0d, 100d);
        enableThirstOverheating = builder.comment("Enables the player losing more thirst in hotter environments.").define("enableThirstOverheating", true);
        thirstGainedFromDrinkingInTheRain = builder.comment("How much thirst the player gains from drinking in the rain (standing outside in the rain and looking up) per tick.").define("thirstGainedFromDrinkingInTheRain", 5d / 24d, 0d, 100d);
        naturalRegenerationModifier = builder.comment(
            "A multiplier for how quickly the player regenerates health, under TFC's passive regeneration.",
            "By default, the player regenerates 0.2 HP/second, or 0.6 HP/second when above 80% hunger and thirst, where 1 HP = 1/50 of a heart.").define("naturalRegenerationModifier", 1d, 0d, 100d);
        nutritionRotationHungerWindow = builder.comment(
            "How much total hunger consumed is required to completely refresh the player's nutrition.",
            "Player nutrition in TFC is calculated based on nutrition of the last few foods eaten - this is how many foods are used to calculate nutrition. By default, all TFC foods restore 4 hunger.").define("nutritionRotationHungerWindow", 80, 1, Integer.MAX_VALUE);
        keepNutritionAfterDeath = builder.comment(
            "If player's nutrition should be kept even after death. Hunger and thirst are not affected and will be reset.").define("keepNutritionAfterDeath", true);
        foodDecayStackWindow = builder.comment(
            "How many hours should different foods ignore when trying to stack together automatically?",
            "Food made with different creation dates doesn't stack by default, unless it's within a specific window. This is the number of hours that different foods will try and stack together at the loss of a little extra expiry time.").define("foodDecayStackWindow", 6, 1, 100);
        foodDecayModifier = builder.comment(
            "A multiplier for food decay, or expiration times. Larger values will result in naturally shorter expiration times.",
            "Setting this to zero will cause decay not to apply.",
            "Note that if you set this to zero **food items will lose their creation dates!!**. This is not reversible!"
        ).define("foodDecayModifier", 1d, 0d, 1000d);
        enableOverburdening = builder.comment("Enables negative effects from carrying too many very heavy items, including potion effects.").define("enableOverburdening", true);
        nutritionMinimumHealthModifier = builder.comment("A multiplier for the minimum health that the player will obtain, based on their nutrition").define("nutritionMinimumHealthModifier", 0.2, 0.001, 1000);
        nutritionDefaultHealthModifier = builder.comment(
            "A multiplier for the default health that the player will have (this is at a average nutrition of 40%, aka the starting nutrition.",
            "Nutrition above this value will linearly scale to the maximum multiplier.",
            "Nutrition below this value will linearly scale to the minimum multiplier."
        ).define("nutritionDefaultHealthModifier", 0.85, 0.001, 1000);
        nutritionMaximumHealthModifier = builder.comment("A multiplier for the maximum health that the player will obtain, based on their nutrition").define("nutritionMaximumHealthModifier", 3.0, 0.001, 1000);

        builder.swap("foodTraits");

        traitSaltedModifier = builder.comment("The modifier for the 'Salted' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitSaltedModifier", 0.5, 0, Double.MAX_VALUE);
        traitBrinedModifier = builder.comment("The modifier for the 'Brined' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitBrinedModifier", 1, 0, Double.MAX_VALUE);
        traitPickledModifier = builder.comment("The modifier for the 'Pickled' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitPickledModifier", 0.5, 0, Double.MAX_VALUE);
        traitPreservedModifier = builder.comment("The modifier for the 'Preserved' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitPreservedModifier", 0.5, 0, Double.MAX_VALUE);
        traitVinegarModifier = builder.comment("The modifier for the 'Vinegar' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitVinegarModifier", 0.1, 0, Double.MAX_VALUE);
        traitCharcoalGrilledModifier = builder.comment("The modifier for the 'Charcoal Grilled' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitCharcoalGrilledModifier", 1.25, 0, Double.MAX_VALUE);
        traitWoodGrilledModifier = builder.comment("The modifier for the 'Wood Grilled' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitWoodGrilledModifier", 0.8, 0, Double.MAX_VALUE);
        traitBurntToACrispModifier = builder.comment("The modifier for the 'Burnt To A Crisp' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitBurntToACrispModifier", 2.5, 0, Double.MAX_VALUE);
        traitWildModifier = builder.comment("The modifier for the 'Wild' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").define("traitWildModifier", 0.5, 0, Double.MAX_VALUE);
        
        builder.swap("fluids");

        enableBucketsPlacingSources = builder.comment("If true, TFC buckets that naturally place sources (colored steel) will place sources. If false, this behavior is disabled.").define("enableBucketsPlacingSources", true);

        builder.swap("vanillaChanges");

        enableVanillaBonemeal = builder.comment("If vanilla bonemeal's instant-growth effect should be enabled.").define("enableVanillaBonemeal", false);
        enableVanillaWeatherEffects = builder.comment("If true, vanilla's snow and ice formation mechanics will be used, and none of the TFC mechanics (improved snow and ice placement, snow stacking, icicle formation, passive snow or ice melting) will exist.").define("enableVanillaWeatherEffects", false);
        enableVanillaSkeletonHorseSpawning = builder.comment("If true, vanilla will attempt to spawn skeleton 'trap' horses during thunderstorms.").define("enableVanillaSkeletonHorseSpawning", false);
        enableVanillaMobsSpawningWithEnchantments = builder.comment("If true, enables the default vanilla behavior of mobs spawning with enchanted weapons sometimes.").define("enableVanillaMobsSpawningWithEnchantments", false);
        enableVanillaMobsSpawningWithVanillaEquipment = builder.comment("If true, enables the default behavior of mobs sapwning with vanilla armor and weapons").define("enableVanillaMobsSpawningWithVanillaEquipment", false);
        enableVanillaGolems = builder.comment("If true, golems can be built").define("enableVanillaGolems", false);
        enableVanillaMonsters = builder.comment("If true, vanilla monsters are able to spawn. If false, the 'enableVanillaMonstersOnSurface' config option is not used, and all spawns are denied.").define("enableVanillaMonsters", true);
        enableVanillaMonstersOnSurface = builder.comment("If true, vanilla monsters will spawn on the surface instead of just underground. If false, vanilla monsters will not spawn on the surface.").define("enableVanillaMonstersOnSurface", false);
        enableChickenJockies = builder.comment("If true, chicken jockies can spawn").define("enableChickenJockies", false);
        enableVanillaEggThrowing = builder.comment("If true, eggs can be thrown.").define("enableVanillaEggThrowing", false);
        enableVanillaDrinkingMilkClearsPotionEffects = builder.comment("If true, drinking milk will clear potion effects and restore no nutrition, as in vanilla.").define("enableVanillaDrinkingMilkClearsPotionEffects", false);

        builder.swap("animals").push("pig");
        pigConfig = MammalConfig.build(builder, "pig", 0.35, 80, 60, true, 19, 10);

        builder.swap("donkey");
        donkeyConfig = MammalConfig.build(builder, "donkey", 0.35, 80, 60, false, 19, 1);

        builder.swap("mule");
        muleConfig = MammalConfig.build(builder, "mule", 0.35, 80, 60, false, 19, 1);

        builder.swap("horse");
        horseConfig = MammalConfig.build(builder, "horse", 0.35, 80, 60, false, 19, 1);

        builder.swap("cat");
        catConfig = MammalConfig.build(builder, "cat", 0.35, 50, 60, false, 19, 6);

        builder.swap("dog");
        dogConfig = MammalConfig.build(builder, "dog", 0.35, 50, 60, true, 19, 2);

        builder.swap("rabbit");
        rabbitConfig = MammalConfig.build(builder, "rabbit", 0.35, 80, 40, true, 19, 6);

        builder.swap("cow");
        cowConfig = ProducingMammalConfig.build(builder, "cow", 0.35, 192, 128, true, 58, 2, 24000, 0.15);

        builder.swap("goat");
        goatConfig = ProducingMammalConfig.build(builder, "goat", 0.35, 96, 60, true, 32, 2, 72000, 0.15);

        builder.swap("yak");
        yakConfig = ProducingMammalConfig.build(builder, "yak", 0.35, 180, 230, false, 64, 1, 23500, 0.15);

        builder.swap("alpaca");
        alpacaConfig = ProducingMammalConfig.build(builder, "alpaca", 0.35, 98, 128, false, 36, 2, 120000, 0.15);

        builder.swap("sheep");
        sheepConfig = ProducingMammalConfig.build(builder, "sheep", 0.35, 56, 60, false, 32, 2, 168000, 0.15);

        builder.swap("muskOx");
        muskOxConfig = ProducingMammalConfig.build(builder, "muskOx", 0.35, 168, 160, false, 64, 1, 96000, 0.15);

        builder.swap("chicken");
        chickenConfig = OviparousAnimalConfig.build(builder, "chicken", 0.35, 24, 100, true, 30000, 0.15, 8);

        builder.swap("duck");
        duckConfig = OviparousAnimalConfig.build(builder, "duck", 0.35, 32, 72, false, 32000, 0.15, 8);

        builder.swap("quail");
        quailConfig = OviparousAnimalConfig.build(builder, "quail", 0.35, 22, 48, true, 28000, 0.15, 8);
        builder.pop(3);

        familiarityDecayLimit = builder.comment("Familiarity value above which familiarity no longer will decay. Default is 0.3, or 30%. Setting it to 0 will cause familiarity to never decay.").define("familiarityDecayLimit", 0.3, 0.0, 1.0);

        builder.push("weird");
        farmlandMakesTheBestRaceTracks = builder.define("farmlandMakesTheBestRaceTracks", false);
        builder.pop();
    }
}
