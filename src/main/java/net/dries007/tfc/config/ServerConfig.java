/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.EnumMap;
import java.util.function.Function;
import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.config.animals.OviparousAnimalConfig;
import net.dries007.tfc.config.animals.ProducingMammalConfig;
import net.dries007.tfc.util.Alloy;

import static net.dries007.tfc.TerraFirmaCraft.*;

/**
 * Server Config
 * - synced, stored per world, can be shipped per instance with default configs
 * - use for the majority of config options, or any that need to be present on both sides
 */
public class ServerConfig
{
    // General
    public final ForgeConfigSpec.BooleanValue enableNetherPortals;
    public final ForgeConfigSpec.BooleanValue enableForcedTFCGameRules;
    public final ForgeConfigSpec.BooleanValue enableTimeStopWhenServerEmpty;
    public final ForgeConfigSpec.BooleanValue enableFireArrowSpreading;
    public final ForgeConfigSpec.DoubleValue fireStarterChance;
    public final ForgeConfigSpec.BooleanValue enableInfestations;
    public final ForgeConfigSpec.BooleanValue requireOffhandForRockKnapping;
    public final ForgeConfigSpec.BooleanValue enableCalendarSensitiveMoonPhases;
    public final ForgeConfigSpec.BooleanValue enableLightning;
    public final ForgeConfigSpec.BooleanValue enableLightningStrippingLogs;

    // Blocks - Farmland
    public final ForgeConfigSpec.BooleanValue enableFarmlandCreation;
    // Blocks - Grass Path
    public final ForgeConfigSpec.BooleanValue enableGrassPathCreation;
    // Blocks - Grass
    public final ForgeConfigSpec.DoubleValue grassSpawningRocksChance;
    // Blocks - Rooted Dirt
    public final ForgeConfigSpec.BooleanValue enableRootedDirtToDirtCreation;
    // Blocks - Snow
    public final ForgeConfigSpec.BooleanValue enableSnowSlowEntities;
    public final ForgeConfigSpec.IntValue snowAccumulateChance;
    public final ForgeConfigSpec.IntValue snowMeltChance;
    // Blocks - Leaves
    public final ForgeConfigSpec.DoubleValue leavesMovementModifier;
    // Blocks - Plants
    public final ForgeConfigSpec.DoubleValue plantGrowthChance;
    public final ForgeConfigSpec.DoubleValue plantLongGrowthChance;
    public final ForgeConfigSpec.DoubleValue plantSpreadChance;
    public final ForgeConfigSpec.DoubleValue plantsMovementModifier;
    // Blocks - Cobblestone
    public final ForgeConfigSpec.BooleanValue enableMossyRockSpreading;
    public final ForgeConfigSpec.IntValue mossyRockSpreadRate;
    // Blocks - Chest
    public final ForgeConfigSpec.EnumValue<Size> chestMaximumItemSize;
    // Blocks - Large Vessel
    public final ForgeConfigSpec.BooleanValue largeVesselEnableAutomation;
    // Blocks - Torch
    public final ForgeConfigSpec.IntValue torchTicks;
    // Blocks - Torch
    public final ForgeConfigSpec.IntValue candleTicks;
    // Blocks - Drying Bricks
    public final ForgeConfigSpec.IntValue mudBricksTicks;
    // Blocks - Charcoal Pit
    public final ForgeConfigSpec.IntValue charcoalTicks;
    // Blocks - Pit Kiln
    public final ForgeConfigSpec.IntValue pitKilnTicks;
    public final ForgeConfigSpec.IntValue pitKilnTemperature;
    // Blocks - Crucible
    public final ForgeConfigSpec.IntValue crucibleCapacity;
    public final ForgeConfigSpec.IntValue cruciblePouringRate;
    public final ForgeConfigSpec.IntValue crucibleFastPouringRate;
    public final ForgeConfigSpec.BooleanValue crucibleEnableAutomation;
    // Blocks - Anvil
    public final ForgeConfigSpec.IntValue anvilAcceptableWorkRange;
    public final ForgeConfigSpec.DoubleValue anvilPoorlyForgedThreshold;
    public final ForgeConfigSpec.DoubleValue anvilWellForgedThreshold;
    public final ForgeConfigSpec.DoubleValue anvilExpertForgedThreshold;
    public final ForgeConfigSpec.DoubleValue anvilPerfectlyForgedThreshold;
    public final ForgeConfigSpec.DoubleValue anvilMaxEfficiencyMultiplier;
    public final ForgeConfigSpec.DoubleValue anvilMaxDurabilityMultiplier;
    public final ForgeConfigSpec.DoubleValue anvilMaxDamageMultiplier;
    // Blocks - Barrel
    public final ForgeConfigSpec.IntValue barrelCapacity;
    public final ForgeConfigSpec.BooleanValue barrelEnableAutomation;
    public final ForgeConfigSpec.BooleanValue barrelEnableRedstoneSeal;
    // Blocks - Large Vessel
    public final ForgeConfigSpec.BooleanValue largeVesselEnableRedstoneSeal;
    // Blocks - Composter
    public final ForgeConfigSpec.IntValue composterTicks;
    public final ForgeConfigSpec.BooleanValue composterRainfallCheck;
    // Blocks - Sluice
    public final ForgeConfigSpec.IntValue sluiceTicks;
    // Blocks - Lamp
    public final ForgeConfigSpec.IntValue lampCapacity;
    // Blocks - Pumpkin
    public final ForgeConfigSpec.BooleanValue enablePumpkinCarving;
    public final ForgeConfigSpec.IntValue jackOLanternTicks;
    // Blocks - Bloomery
    public final ForgeConfigSpec.IntValue bloomeryCapacity;
    public final ForgeConfigSpec.IntValue bloomeryMaxChimneyHeight;
    // Blocks - Blast Furnace
    public final ForgeConfigSpec.IntValue blastFurnaceCapacity;
    public final ForgeConfigSpec.IntValue blastFurnaceFluidCapacity;
    public final ForgeConfigSpec.IntValue blastFurnaceFuelConsumptionMultiplier;
    public final ForgeConfigSpec.IntValue blastFurnaceMaxChimneyHeight;
    public final ForgeConfigSpec.BooleanValue blastFurnaceEnableAutomation;
    // Blocks - Loom
    public final ForgeConfigSpec.BooleanValue loomEnableAutomation;
    // Blocks - Thatch
    public final ForgeConfigSpec.DoubleValue thatchMovementMultiplier;
    // Blocks - Thatch Bed
    public final ForgeConfigSpec.BooleanValue enableThatchBedSpawnSetting;
    public final ForgeConfigSpec.BooleanValue enableThatchBedSleeping;
    public final ForgeConfigSpec.BooleanValue thatchBedNoSleepInThunderstorms;
    // Blocks - Item Size
    public final ForgeConfigSpec.EnumValue<Size> maxPlacedItemSize;
    public final ForgeConfigSpec.EnumValue<Size> maxPlacedLargeItemSize;
    public final ForgeConfigSpec.BooleanValue enablePlacingItems;
    public final ForgeConfigSpec.BooleanValue usePlacedItemWhitelist;
    // Blocks - Leaves
    public final ForgeConfigSpec.BooleanValue enableLeavesDecaySlowly;
    // Blocks - Charcoal Forge
    public final ForgeConfigSpec.BooleanValue charcoalForgeEnableAutomation;
    // Blocks - Fire Pit
    public final ForgeConfigSpec.BooleanValue firePitEnableAutomation;
    // Blocks - Nest Box
    public final ForgeConfigSpec.BooleanValue nestBoxEnableAutomation;
    // Blocks - Powder Keg
    public final ForgeConfigSpec.BooleanValue powderKegEnabled;
    public final ForgeConfigSpec.BooleanValue powderKegEnableAutomation;
    public final ForgeConfigSpec.DoubleValue powderKegStrengthModifier;
    // Blocks - Hot Water
    public final ForgeConfigSpec.DoubleValue hotWaterHealAmount;
    // Blocks - Sapling
    public final ForgeConfigSpec.DoubleValue globalSaplingGrowthModifier;
    public final ForgeConfigSpec.DoubleValue globalFruitSaplingGrowthModifier;
    public final EnumMap<Wood, ForgeConfigSpec.IntValue> saplingGrowthDays;
    public final EnumMap<FruitBlocks.Tree, ForgeConfigSpec.IntValue> fruitSaplingGrowthDays;
    public final ForgeConfigSpec.IntValue bananaSaplingGrowthDays;
    // Blocks - Crops
    public final ForgeConfigSpec.DoubleValue cropGrowthModifier;
    public final ForgeConfigSpec.DoubleValue cropExpiryModifier;
    // Blocks - Dispenser
    public final ForgeConfigSpec.BooleanValue dispenserEnableLighting;

    // Items - Small Vessel
    public final ForgeConfigSpec.IntValue smallVesselCapacity;
    public final ForgeConfigSpec.EnumValue<Size> smallVesselMaximumItemSize;
    public final ForgeConfigSpec.BooleanValue enableSmallVesselInventoryInteraction;
    // Items - Mold(s)
    public final ForgeConfigSpec.IntValue moldIngotCapacity;
    public final ForgeConfigSpec.IntValue moldFireIngotCapacity;
    public final ForgeConfigSpec.IntValue moldPickaxeHeadCapacity;
    public final ForgeConfigSpec.IntValue moldPropickHeadCapacity;
    public final ForgeConfigSpec.IntValue moldAxeHeadCapacity;
    public final ForgeConfigSpec.IntValue moldShovelHeadCapacity;
    public final ForgeConfigSpec.IntValue moldHoeHeadCapacity;
    public final ForgeConfigSpec.IntValue moldChiselHeadCapacity;
    public final ForgeConfigSpec.IntValue moldHammerHeadCapacity;
    public final ForgeConfigSpec.IntValue moldSawBladeCapacity;
    public final ForgeConfigSpec.IntValue moldJavelinHeadCapacity;
    public final ForgeConfigSpec.IntValue moldSwordBladeCapacity;
    public final ForgeConfigSpec.IntValue moldMaceHeadCapacity;
    public final ForgeConfigSpec.IntValue moldKnifeBladeCapacity;
    public final ForgeConfigSpec.IntValue moldScytheBladeCapacity;
    public final ForgeConfigSpec.IntValue moldBellCapacity;
    // Items - Jug
    public final ForgeConfigSpec.IntValue jugCapacity;
    public final ForgeConfigSpec.DoubleValue jugBreakChance;
    // Items - Wooden Bucket
    public final ForgeConfigSpec.IntValue woodenBucketCapacity;
    // Mechanics - Heat
    public final ForgeConfigSpec.DoubleValue deviceHeatingModifier;
    public final ForgeConfigSpec.DoubleValue itemHeatingModifier;
    public final ForgeConfigSpec.DoubleValue itemCoolingModifier;
    public final ForgeConfigSpec.IntValue ticksBeforeItemCool;
    public final ForgeConfigSpec.BooleanValue coolHotItemEntities;
    // Mechanics - Collapses
    public final ForgeConfigSpec.BooleanValue enableBlockCollapsing;
    public final ForgeConfigSpec.BooleanValue enableExplosionCollapsing;
    public final ForgeConfigSpec.BooleanValue enableBlockLandslides;
    public final ForgeConfigSpec.BooleanValue enableChiselsStartCollapses;
    public final ForgeConfigSpec.DoubleValue collapseTriggerChance;
    public final ForgeConfigSpec.DoubleValue collapseFakeTriggerChance;
    public final ForgeConfigSpec.DoubleValue collapsePropagateChance;
    public final ForgeConfigSpec.DoubleValue collapseExplosionPropagateChance;
    public final ForgeConfigSpec.IntValue collapseMinRadius;
    public final ForgeConfigSpec.IntValue collapseRadiusVariance;
    // Mechanics - Player
    public final ForgeConfigSpec.BooleanValue enablePeacefulDifficultyPassiveRegeneration;
    public final ForgeConfigSpec.DoubleValue passiveExhaustionModifier;
    public final ForgeConfigSpec.DoubleValue thirstModifier;
    public final ForgeConfigSpec.BooleanValue enableThirstOverheating;
    public final ForgeConfigSpec.DoubleValue thirstGainedFromDrinkingInTheRain;
    public final ForgeConfigSpec.DoubleValue naturalRegenerationModifier;
    public final ForgeConfigSpec.IntValue nutritionRotationHungerWindow;
    public final ForgeConfigSpec.BooleanValue keepNutritionAfterDeath;
    public final ForgeConfigSpec.IntValue foodDecayStackWindow;
    public final ForgeConfigSpec.DoubleValue foodDecayModifier;
    public final ForgeConfigSpec.BooleanValue enableOverburdening;
    public final ForgeConfigSpec.DoubleValue nutritionMinimumHealthModifier;
    public final ForgeConfigSpec.DoubleValue nutritionDefaultHealthModifier;
    public final ForgeConfigSpec.DoubleValue nutritionMaximumHealthModifier;
    // Mechanics - Food Traits
    public final ForgeConfigSpec.DoubleValue traitSaltedModifier;
    public final ForgeConfigSpec.DoubleValue traitBrinedModifier;
    public final ForgeConfigSpec.DoubleValue traitPickledModifier;
    public final ForgeConfigSpec.DoubleValue traitPreservedModifier;
    public final ForgeConfigSpec.DoubleValue traitVinegarModifier;
    public final ForgeConfigSpec.DoubleValue traitCharcoalGrilledModifier;
    public final ForgeConfigSpec.DoubleValue traitWoodGrilledModifier;
    public final ForgeConfigSpec.DoubleValue traitBurntToACrispModifier;
    public final ForgeConfigSpec.DoubleValue traitWildModifier;
    // Mechanics - Fluids
    public final ForgeConfigSpec.BooleanValue enableBucketsPlacingSources;
    // Mechanics - Vanilla Changes
    public final ForgeConfigSpec.BooleanValue enableVanillaBonemeal;
    public final ForgeConfigSpec.BooleanValue enableVanillaWeatherEffects;
    public final ForgeConfigSpec.BooleanValue enableVanillaSkeletonHorseSpawning;
    public final ForgeConfigSpec.BooleanValue enableVanillaMobsSpawningWithEnchantments;
    public final ForgeConfigSpec.BooleanValue enableVanillaMobsSpawningWithVanillaEquipment;
    public final ForgeConfigSpec.BooleanValue enableVanillaGolems;
    public final ForgeConfigSpec.BooleanValue enableVanillaMonsters;
    public final ForgeConfigSpec.BooleanValue enableVanillaMonstersOnSurface;
    public final ForgeConfigSpec.BooleanValue enableChickenJockies;
    public final ForgeConfigSpec.BooleanValue enableVanillaEggThrowing;
    public final ForgeConfigSpec.BooleanValue enableVanillaDrinkingMilkClearsPotionEffects;

    // Animals
    public final MammalConfig pigConfig;
    public final MammalConfig donkeyConfig;
    public final MammalConfig muleConfig;
    public final MammalConfig horseConfig;
    public final MammalConfig catConfig;
    public final MammalConfig dogConfig;
    public final ProducingMammalConfig cowConfig;
    public final ProducingMammalConfig alpacaConfig;
    public final OviparousAnimalConfig chickenConfig;
    public final ProducingMammalConfig yakConfig;
    public final ProducingMammalConfig goatConfig;
    public final ProducingMammalConfig sheepConfig;
    public final ProducingMammalConfig muskOxConfig;
    public final OviparousAnimalConfig duckConfig;
    public final OviparousAnimalConfig quailConfig;


    // Below Everything
    public final ForgeConfigSpec.BooleanValue farmlandMakesTheBestRaceTracks;

    ServerConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        enableNetherPortals = builder.apply("enableNetherPortals").comment("Enable nether portal creation").define("enableNetherPortals", false);
        enableForcedTFCGameRules = builder.apply("enableForcedTFCGameRules").comment(
            "Forces a number of game rules to specific values.",
            "  naturalRegeneration = false (Health regen is much slower and not tied to extra saturation)",
            "  doInsomnia = false (No phantoms)",
            "  doTraderSpawning = false (No wandering traders)",
            "  doPatrolSpawning = false (No pillager patrols)"
        ).define("enableForcedTFCGameRules", true);
        enableTimeStopWhenServerEmpty = builder.apply("enableTimeStopWhenServerEmpty").comment(
            "If true, TFC will stop time when no players are online on a running server.",
            "This prevents food from decaying, the calendar from advancing, and the sun from moving, etc.",
            "!!Disable at your own risk!!"
        ).define("enableTimeStopWhenServerEmpty", true);
        enableFireArrowSpreading = builder.apply("enableFireArrowSpreading").comment("Enable fire arrows and fireballs to spread fire and light blocks.").define("enableFireArrowSpreading", true);
        fireStarterChance = builder.apply("fireStarterChance").comment("Base probability for a firestarter to start a fire. May change based on circumstances").defineInRange("fireStarterChance", 0.5, 0, 1);
        enableInfestations = builder.apply("enableInfestations").comment("Enable rat infestations for improperly stored food.").define("enableInfestations", true);
        requireOffhandForRockKnapping = builder.apply("requireOffhandForRockKnapping").comment(
            "If true, knapping with rocks will only work when one rock is held in each hand (main hand and off hand)",
            "If false, knapping with rocks will work either with main and off hand, or by holding at least two rocks in the main hand"
        ).define("requireOffhandForRockKnapping", false);
        enableCalendarSensitiveMoonPhases = builder.apply("enableCalendarSensitiveMoonPhases").comment("Enables TFC setting the moon phase based on the progress of the month. The etymology of the English word 'month' is in fact related to the word 'moon'.").define("enableCalendarSensitiveMoonPhases", true);
        enableLightning = builder.apply("enableLightning").comment("If false, vanilla lightning will not strike.").define("enableLightning", true);
        enableLightningStrippingLogs = builder.apply("enableLightningStrippingLogs").comment("If true, lightning has a chance of stripping bark off of trees.").define("enableLightningStrippingLogs", true);

        innerBuilder.pop().push("blocks").push("farmland");

        enableFarmlandCreation = builder.apply("enableFarmlandCreation").comment("If TFC soil blocks are able to be created into farmland using a hoe.").define("enableFarmlandCreation", true);

        innerBuilder.pop().push("grassPath");

        enableGrassPathCreation = builder.apply("enableGrassPathCreation").comment("If TFC soil blocks are able to be created into (grass) path blocks using a hoe.").define("enableGrassPathCreation", true);

        innerBuilder.pop().push("grass");

        grassSpawningRocksChance = builder.apply("grassSpawningRocksChance").comment("The chance that when grass that freezes and thaws it will have a loose rock rise to the surface, provided some conditions are met. Set to 0 to disable.").defineInRange("grassSpawningRocksChance", 0.001, 0, 1);

        innerBuilder.pop().push("rootedDirt");

        enableRootedDirtToDirtCreation = builder.apply("enableRootedDirtToDirtCreation").comment("If TFC rooted dirt blocks are able to be created into dirt blocks using a hoe.").define("enableRootedDirtToDirtCreation", true);

        innerBuilder.pop().push("snow");

        enableSnowSlowEntities = builder.apply("enableSnowSlowEntities").comment("[Requires MC Restart] If snow will slow players that move on top of it similar to soul sand or honey.").define("enableSnowSlowEntities", true);
        snowAccumulateChance = builder.apply("snowAccumulateChance").comment("The chance that snow will accumulate during a storm. Lower values = faster snow accumulation, but also more block updates (aka lag).").defineInRange("snowAccumulateChance", 20, 1, Integer.MAX_VALUE);
        snowMeltChance = builder.apply("snowMeltChance").comment("The chance that snow will melt during a storm. Lower values = faster snow melting, but also more block updates (aka lag).").defineInRange("snowMeltChance", 36, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("plants");

        plantGrowthChance = builder.apply("plantGrowthChance").comment("Chance for a plant to grow each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant growth.").defineInRange("plantGrowthChance", 0.04, 0, 1);
        plantLongGrowthChance = builder.apply("plantLongGrowthChance").comment("Chance for a twisting/weeping/kelp plant to grow each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant growth.").defineInRange("plantLongGrowthChance", 0.03, 0, 1);
        plantSpreadChance = builder.apply("plantSpreadChance").comment("Chance for a plant to spread each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant spreading.").defineInRange("plantSpreadChance", 0.001, 0, 1);
        plantsMovementModifier = builder.apply("plantsMovementModifier").comment("A movement multiplier for players moving through plants. Individual plants will use a ratio of this value, and lower = slower.").defineInRange("plantsMovementModifier", 0.2, 0, 1);

        innerBuilder.pop().push("leaves");

        leavesMovementModifier = builder.apply("leavesMovementModifier").comment("A movement multiplier for players moving through leaves. Lower = slower.").defineInRange("leavesMovementModifier", 0.4, 0, 1);

        innerBuilder.pop().push("cobblestone");

        enableMossyRockSpreading = builder.apply("enableMossyRockSpreading").comment("If mossy rock blocks will spread their moss to nearby rock blocks (bricks and cobble; stairs, slabs and walls thereof).").define("enableMossyRockSpreading", true);
        mossyRockSpreadRate = builder.apply("mossyRockSpreadRate").comment("The rate at which rock blocks will accumulate moss. Higher value = slower.").defineInRange("mossyRockSpreadRate", 20, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("chest");
        chestMaximumItemSize = builder.apply("chestMaximumItemSize").comment("The largest (inclusive) size of an item that is allowed in a chest.").defineEnum("chestMaximumItemSize", Size.LARGE);

        innerBuilder.pop().push("largeVessel");

        largeVesselEnableAutomation = builder.apply("largeVesselEnableAutomation").comment("If true, large vessels will interact with in-world automation such as hoppers on a side-specific basis.").define("largeVesselEnableAutomation", true);

        innerBuilder.pop().push("torch");

        torchTicks = builder.apply("torchTicks").comment("Number of ticks required for a torch to burn out (1000 = 1 in game hour = 50 seconds), default is 72 hours. Set to -1 to disable torch burnout.").defineInRange("torchTicks", 72000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("candle");

        candleTicks = builder.apply("candleTicks").comment("Number of ticks required for a candle to burn out (1000 = 1 in game hour = 50 seconds), default is 264 hours. Set to -1 to disable candle burnout.").defineInRange("candleTicks", 264000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("dryingBricks");

        mudBricksTicks = builder.apply("mudBricksTicks").comment("Number of ticks required for mud bricks to dry (1000 = 1 in game hour = 50 seconds), default is 24 hours. Set to -1 to disable drying.").defineInRange("mudBricksTicks",  24000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("charcoal");

        charcoalTicks = builder.apply("charcoalTicks").comment("Number of ticks required for charcoal pit to complete. (1000 = 1 in game hour = 50 seconds), default is 18 hours.").defineInRange("charcoalTicks", 18000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("pitKiln");

        pitKilnTicks = builder.apply("pitKilnTicks").comment("Number of ticks required for a pit kiln to burn out. (1000 = 1 in game hour = 50 seconds), default is 8 hours.").defineInRange("pitKilnTicks", 8000, 20, Integer.MAX_VALUE);
        pitKilnTemperature = builder.apply("pitKilnTemperature1").comment("The maximum temperature which a pit kiln reaches.").defineInRange("pitKilnTemperature1", 1400, 0, Integer.MAX_VALUE);

        innerBuilder.pop().push("crucible");

        crucibleCapacity = builder.apply("crucibleCapacity").comment("Tank capacity of a crucible (in mB).").defineInRange("crucibleCapacity", 4000, 0, Alloy.MAX_ALLOY);
        cruciblePouringRate = builder.apply("cruciblePouringRate").comment("A modifier for how fast fluid containers empty into crucibles. Containers will empty 1 mB every (this) number of ticks.").defineInRange("cruciblePouringRate", 4, 1, Integer.MAX_VALUE);
        crucibleFastPouringRate = builder.apply("crucibleFastPouringRate").comment("A modifier for how fast fluid containers empty into crucibles when shift is held. Containers will empty 1 mB every (this) number of ticks.").defineInRange("crucibleFastPouringRate", 1, 1, Integer.MAX_VALUE);
        crucibleEnableAutomation = builder.apply("crucibleEnableAutomation").comment("If true, barrels will interact with in-world automation such as hoppers on a side-specific basis.").define("crucibleEnableAutomation", true);

        innerBuilder.pop().push("anvil");

        anvilAcceptableWorkRange = builder.apply("anvilAcceptableWorkRange").comment("The number of pixels that the anvil's result may be off by, but still count as recipe completion. By default this requires pixel perfect accuracy.").defineInRange("anvilAcceptableWorkRange", 0, 0, 150);
        anvilPoorlyForgedThreshold = builder.apply("anvilPoorlyForgedThreshold").comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Poorly Forged'.").defineInRange("anvilPoorlyForgedThreshold", 10.0, 1.0, Double.MAX_VALUE);
        anvilWellForgedThreshold = builder.apply("anvilWellForgedThreshold").comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Well Forged'.").defineInRange("anvilWellForgedThreshold", 5.0, 1.0, Double.MAX_VALUE);
        anvilExpertForgedThreshold = builder.apply("anvilExpertForgedThreshold").comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Expertly Forged'.").defineInRange("anvilExpertForgedThreshold", 2.0, 1.0, Double.MAX_VALUE);
        anvilPerfectlyForgedThreshold = builder.apply("anvilPerfectlyForgedThreshold").comment("The minimum efficiency (ratio of number of steps taken / minimum number of steps required) that must be passed for a item to be considered 'Perfectly Forged'.").defineInRange("anvilPerfectlyForgedThreshold", 1.5, 1.0, Double.MAX_VALUE);
        anvilMaxEfficiencyMultiplier = builder.apply("anvilMaxEfficiencyMultiplier").comment("The multiplier to efficiency (mining speed) that is applied to a 'Perfectly Forged' tool.").defineInRange("anvilMaxEfficiencyMultiplier", 1.8, 1, 1000);
        anvilMaxDurabilityMultiplier = builder.apply("anvilMaxDurabilityMultiplier").comment("The bonus to durability (probability to ignore a point of damage) that is applied to a 'Perfectly Forged' tool. Note that 1 ~ infinite durability, and 0 ~ no bonus.").defineInRange("anvilMaxDurabilityMultiplier", 0.5, 0, 1);
        anvilMaxDamageMultiplier = builder.apply("anvilMaxDamageMultiplier").comment("The boost to attack damage that is applied to a 'Perfectly Forged' tool.").defineInRange("anvilMaxDamageMultiplier", 1.5, 1, 1000);

        innerBuilder.pop().push("barrel");

        barrelCapacity = builder.apply("barrelCapacity").comment("Tank capacity of a barrel (in mB).").defineInRange("barrelCapacity", 10000, 0, Integer.MAX_VALUE);
        barrelEnableAutomation = builder.apply("barrelEnableAutomation").comment("If true, barrels will interact with in-world automation such as hoppers on a side-specific basis.").define("barrelEnableAutomation", true);
        barrelEnableRedstoneSeal = builder.apply("barrelEnableRedstoneSeal").comment("If true, barrels will seal and unseal on redstone signal.").define("barrelEnableRedstoneSeal", true);

        innerBuilder.pop().push("largeVessel");

        largeVesselEnableRedstoneSeal = builder.apply("largeVesselEnableRedstoneSeal").comment("If true, large vessels will seal and unseal on redstone signal.").define("largeVesselEnableRedstoneSeal", true);

        innerBuilder.pop().push("composter");

        composterTicks = builder.apply("composterTicks").comment("Number of ticks required for a composter in normal conditions to complete. (24000 = 1 game day), default is 12 days.").defineInRange("composterTicks", 288000, 20, Integer.MAX_VALUE);
        composterRainfallCheck = builder.apply("composterRainfallCheck").comment("Should the composter work less efficiently at high or low rainfalls?").define("composterRainfallCheck", true);

        innerBuilder.pop().push("sluice");
        sluiceTicks = builder.apply("sluiceTicks").comment("Number of ticks required for a sluice to process an item. (20 = 1 second), default is 5 seconds.").defineInRange("sluiceTicks", 100, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("lamp");

        lampCapacity = builder.apply("lampCapacity").comment("Tank capacity of a lamp (in mB).").defineInRange("lampCapacity", 250, 0, Alloy.MAX_ALLOY);

        innerBuilder.pop().push("pumpkin");

        enablePumpkinCarving = builder.apply("enablePumpkinCarving").comment("Enables the knifing of pumpkins to carve them.").define("enablePumpkinCarving", true);
        jackOLanternTicks = builder.apply("jackOLanternTicks").comment("Number of ticks required for a jack 'o lantern to burn out (1000 = 1 in game hour = 50 seconds), default is 108 hours. Set to -1 to disable burnout.").defineInRange("jackOLanternTicks", 108000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("bloomery");

        bloomeryCapacity = builder.apply("bloomeryCapacity").comment("Inventory capacity (in number of items per level of chimney) of the bloomery.").defineInRange("bloomeryCapacity", 8, 1, Integer.MAX_VALUE);
        bloomeryMaxChimneyHeight = builder.apply("bloomeryMaxChimneyHeight").comment("The maximum number of levels that can be built in a bloomery multiblock, for added capacity.").defineInRange("bloomeryMaxChimneyHeight", 3, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("blastFurnace");

        blastFurnaceCapacity = builder.apply("blastFurnaceCapacity").comment("Inventory capacity (in number of items per level of chimney) of the blast furnace.").defineInRange("blastFurnaceCapacity", 4, 1, Integer.MAX_VALUE);
        blastFurnaceFluidCapacity = builder.apply("blastFurnaceFluidCapacity").comment("Fluid capacity (in mB) of the output tank of the blast furnace.").defineInRange("blastFurnaceFluidCapacity", 10_000, 1, Integer.MAX_VALUE);
        blastFurnaceFuelConsumptionMultiplier = builder.apply("blastFurnaceFuelConsumptionMultiplier").comment("A multiplier for how fast the blast furnace consumes fuel. Higher values = faster fuel consumption.").defineInRange("blastFurnaceFuelConsumptionMultiplier", 4, 1, Integer.MAX_VALUE);
        blastFurnaceMaxChimneyHeight = builder.apply("blastFurnaceMaxChimneyHeight").comment("The maximum number of levels that can be built in a blast furnace multiblock, for added capacity.").defineInRange("blastFurnaceMaxChimneyHeight", 5, 1, Integer.MAX_VALUE);
        blastFurnaceEnableAutomation = builder.apply("blastFurnaceEnableAutomation").comment("If true, blast furnaces will interact with in-world automation such as hoppers on a side-specific basis.").define("blastFurnaceEnableAutomation", true);

        innerBuilder.pop().push("loom");

        loomEnableAutomation = builder.apply("loomEnableAutomation").comment("If true, looms will interact with in-world automation such as hoppers on a side-specific basis.").define("loomEnableAutomation", true);

        innerBuilder.pop().push("thatch");

        thatchMovementMultiplier = builder.apply("thatchMovementMultiplier").comment("A movement multiplier for players moving through thatch. Lower = slower.").defineInRange("thatchMovementMultiplier", 0.6, 0, 1);

        innerBuilder.pop().push("thatchBed");

        enableThatchBedSpawnSetting = builder.apply("enableThatchBedSpawnSetting").comment("If true, thatch beds can set the player's spawn.").define("enableThatchBedSpawnSetting", true);
        enableThatchBedSleeping = builder.apply("enableThatchBedSleeping").comment("If true, the player can sleep the night in a thatch bed").define("enableThatchBedSleeping", false);
        thatchBedNoSleepInThunderstorms = builder.apply("thatchBedNoSleepInThunderstorms").comment("If true, the player cannot sleep in thatch beds during thunderstorms.").define("thatchBedNoSleepInThunderstorms", true);

        innerBuilder.pop().push("leaves");

        enableLeavesDecaySlowly = builder.apply("enableLeavesDecaySlowly").comment("If true, then leaves will decay slowly over time when disconnected from logs (vanilla behavior), as opposed to instantly (TFC behavior).").define("enableLeavesDecaySlowly", false);

        innerBuilder.pop().push("placedItems");

        maxPlacedItemSize = builder.apply("maxPlacedItemSize").comment("The maximum size of items that can be placed as 4 items on the ground with V. If an item is larger than this, it could still be placed with the 'maxPlacedLargeItemSize' option.").defineEnum("maxPlacedItemSize", Size.LARGE);
        maxPlacedLargeItemSize = builder.apply("maxPlacedLargeItemSize").comment("The maximum size of items that can be placed as a single item on the ground with V. Items are checked to see if they're the right size to be placed in a group of 4 items first.").defineEnum("maxPlacedLargeItemSize", Size.HUGE);
        enablePlacingItems = builder.apply("enablePlacingItems").comment("If true, players can place items on the ground with V.").define("enablePlacingItems", true);
        usePlacedItemWhitelist = builder.apply("usePlacedItemWhitelist").comment("If true, the tag 'tfc:placed_item_whitelist' will be checked to allow items to be in placed items and will exclude everything else.").define("usePlacedItemWhitelist", false);

        innerBuilder.pop().push("charcoalForge");

        charcoalForgeEnableAutomation = builder.apply("charcoalForgeEnableAutomation").comment("If true, charcoal forges will interact with in-world automation such as hoppers on a side-specific basis.").define("charcoalForgeEnableAutomation", true);

        innerBuilder.pop().push("firePitEnableAutomation");

        firePitEnableAutomation = builder.apply("firePitEnableAutomation").comment("If true, fire pits will interact with in-world automation such as hoppers on a side-specific basis.").define("firePitEnableAutomation", true);

        innerBuilder.pop().push("nestBox");

        nestBoxEnableAutomation = builder.apply("nestBoxEnableAutomation").comment("If true, nest boxes will interact with in-world automation such as hoppers on a side-specific basis.").define("nestBoxEnableAutomation", true);

        innerBuilder.pop().push("powderKeg");

        powderKegEnabled = builder.apply("powderKegEnabled").comment("If true, powder kegs can be lit and exploded.").define("powderKegEnabled", true);
        powderKegEnableAutomation = builder.apply("powderKegEnableAutomation").comment("If true, powder kegs will interact with in-world automation such as hoppers on a side-specific basis.").define("powderKegEnableAutomation", true);
        powderKegStrengthModifier = builder.apply("powderKegStrengthModifier").comment("A modifier to the strength of powderkegs when exploding. A max powderkeg explosion is 64, and all explosions are capped to this size no matter the value of the modifier.").defineInRange("powderKegStrengthModifier", 1d, 0, 64);

        innerBuilder.pop().push("hotWater");

        hotWaterHealAmount = builder.apply("hotWaterHealAmount").comment("An amount that sitting in hot water will restore health, approximately twice per second.").defineInRange("hotWaterHealAmount", 0.08, 0.0, 20.0);

        innerBuilder.pop().push("saplings");

        globalSaplingGrowthModifier = builder.apply("globalSaplingGrowthModifier").comment("Modifier applied to the growth time of every (non-fruit) sapling. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").defineInRange("globalSaplingGrowthModifier", 1d, 0d, Double.MAX_VALUE);
        globalFruitSaplingGrowthModifier = builder.apply("globalFruitSaplingGrowthModifier").comment("Modifier applied to the growth time of every fruit tree sapling. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").defineInRange("globalFruitSaplingGrowthModifier", 1d, 0d, Double.MAX_VALUE);

        saplingGrowthDays = new EnumMap<>(Wood.class);
        for (Wood wood : Wood.VALUES)
        {
            final String valueName = String.format("%sSaplingGrowthDays", wood.getSerializedName());
            saplingGrowthDays.put(wood, builder.apply(valueName).comment(String.format("Days for a %s tree sapling to be ready to grow into a full tree.", wood.getSerializedName())).defineInRange(valueName, wood.defaultDaysToGrow(), 0, Integer.MAX_VALUE));
        }
        fruitSaplingGrowthDays = new EnumMap<>(FruitBlocks.Tree.class);
        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            final String valueName = String.format("%sSaplingGrowthDays1", tree.getSerializedName());
            fruitSaplingGrowthDays.put(tree, builder.apply(valueName).comment(String.format("Days for a %s tree sapling to be eligible to grow", tree.getSerializedName())).defineInRange(valueName, tree.defaultDaysToGrow(), 0, Integer.MAX_VALUE));
        }
        bananaSaplingGrowthDays = builder.apply("bananaSaplingGrowthDays").comment("Days for a banana tree sapling to be eligible to grow").defineInRange("bananaSaplingGrowthDays", 6, 0, Integer.MAX_VALUE);

        innerBuilder.pop().push("crops");

        cropGrowthModifier = builder.apply("cropGrowthModifier").comment("Modifier applied to the growth time of every crop. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").defineInRange("cropGrowthModifier", 1, 0.001, 1000);
        cropExpiryModifier = builder.apply("cropExpiryModifier").comment("Modifier applied to the expiry time of every crop. The modifier multiplies the ticks it takes to grow, so larger values cause longer growth times. For example, a value of 2 doubles the growth time.").defineInRange("cropExpiryModifier", 1, 0.001, 1000);

        innerBuilder.pop().push("dispenser");

        dispenserEnableLighting = builder.apply("dispenserEnableLighting").comment("If true, dispensers can light TFC devices.").define("dispenserEnableLighting", true);

        innerBuilder.pop().pop().push("items").push("smallVessel");

        smallVesselCapacity = builder.apply("smallVesselCapacity").comment("Tank capacity of a small vessel (in mB).").defineInRange("smallVesselCapacity", 3000, 0, Alloy.MAX_ALLOY);
        smallVesselMaximumItemSize = builder.apply("smallVesselMaximumItemSize").comment("The largest (inclusive) size of an item that is allowed in a small vessel.").defineEnum("smallVesselMaximumItemSize", Size.SMALL);
        enableSmallVesselInventoryInteraction = builder.apply("enableSmallVesselInventoryInteraction").comment("If true, the vessel can be inserted and extracted from by clicking on it in the inventory. You may want to disable this if you have a inventory-tweaking mod").define("enableSmallVesselInventoryInteraction", true);

        innerBuilder.pop().push("molds");

        moldIngotCapacity = builder.apply("moldIngotCapacity").comment("Tank capacity of a Ingot mold (in mB).").defineInRange("moldIngotCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldFireIngotCapacity = builder.apply("moldFireIngotCapacity").comment("Tank capacity of a Fire Ingot mold (in mB).").defineInRange("moldIngotCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldPickaxeHeadCapacity = builder.apply("moldPickaxeHeadCapacity").comment("Tank capacity of a Pickaxe Head mold (in mB).").defineInRange("moldPickaxeHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldPropickHeadCapacity = builder.apply("moldPropickHeadCapacity").comment("Tank capacity of a Propick Head mold (in mB).").defineInRange("moldPropickHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldAxeHeadCapacity = builder.apply("moldAxeHeadCapacity").comment("Tank capacity of a Axe Head mold (in mB).").defineInRange("moldAxeHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldShovelHeadCapacity = builder.apply("moldShovelHeadCapacity").comment("Tank capacity of a Shovel Head mold (in mB).").defineInRange("moldShovelHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldHoeHeadCapacity = builder.apply("moldHoeHeadCapacity").comment("Tank capacity of a Hoe Head mold (in mB).").defineInRange("moldHoeHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldChiselHeadCapacity = builder.apply("moldChiselHeadCapacity").comment("Tank capacity of a Chisel Head mold (in mB).").defineInRange("moldChiselHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldHammerHeadCapacity = builder.apply("moldHammerHeadCapacity").comment("Tank capacity of a Hammer Head mold (in mB).").defineInRange("moldHammerHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldSawBladeCapacity = builder.apply("moldSawBladeCapacity").comment("Tank capacity of a Saw Blade mold (in mB).").defineInRange("moldSawBladeCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldJavelinHeadCapacity = builder.apply("moldJavelinHeadCapacity").comment("Tank capacity of a Javelin Head mold (in mB).").defineInRange("moldJavelinHeadCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldSwordBladeCapacity = builder.apply("moldSwordBladeCapacity").comment("Tank capacity of a Sword Blade mold (in mB).").defineInRange("moldSwordBladeCapacity", 200, 0, Alloy.MAX_ALLOY);
        moldMaceHeadCapacity = builder.apply("moldMaceHeadCapacity").comment("Tank capacity of a Mace Head mold (in mB).").defineInRange("moldMaceHeadCapacity", 200, 0, Alloy.MAX_ALLOY);
        moldKnifeBladeCapacity = builder.apply("moldKnifeBladeCapacity").comment("Tank capacity of a Knife Blade mold (in mB).").defineInRange("moldKnifeBladeCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldScytheBladeCapacity = builder.apply("moldScytheBladeCapacity").comment("Tank capacity of a Scythe Blade mold (in mB).").defineInRange("moldScytheBladeCapacity", 100, 0, Alloy.MAX_ALLOY);
        moldBellCapacity = builder.apply("moldBellCapacity").comment("Tank capacity of a Bell mold (in mB).").defineInRange("moldScytheBladeCapacity", 100, 0, Alloy.MAX_ALLOY);

        innerBuilder.pop().push("jug");

        jugCapacity = builder.apply("jugCapacity").comment("Tank capacity of a ceramic jug (in mB).").defineInRange("jugCapacity", 100, 0, Alloy.MAX_ALLOY);
        jugBreakChance = builder.apply("jugBreakChance").comment("The chance a jug will break after drinking.").defineInRange("jugBreakChance", 0.02, 0, 1);

        innerBuilder.pop().push("woodenBucket");
        woodenBucketCapacity = builder.apply("woodenBucketCapacity").comment("Tank capacity of a wooden bucket (in mB).").defineInRange("woodenBucketCapacity", 1000, 0, Alloy.MAX_ALLOY);

        innerBuilder.pop().pop().push("mechanics").push("heat");

        deviceHeatingModifier = builder.apply("deviceHeatingModifier").comment("A multiplier for how fast devices themselves heat up. Higher = faster.").defineInRange("deviceHeatingModifier", 1, 0, Double.MAX_VALUE);
        itemHeatingModifier = builder.apply("itemHeatingModifier").comment("A multiplier for how fast items heat in devices. Higher = faster.").defineInRange("itemHeatingModifier", 1, 0, Double.MAX_VALUE);
        itemCoolingModifier = builder.apply("itemCoolingModifier").comment("A multiplier for how fast items cool. Higher = faster.").defineInRange("itemCoolingModifier", 0.8, 0, Double.MAX_VALUE);
        coolHotItemEntities = builder.apply("coolHotItemEntities").comment("Should hot item entities cool off when in contact with blocks like water or snow?").define("coolHotItemEntities", true);
        ticksBeforeItemCool = builder.apply("ticksBeforeItemCool").comment("Ticks between each time an item loses temperature when sitting on a cold block. 20 ticks = 1 second.").defineInRange("ticksBeforeItemCool", 10, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("collapses");

        enableBlockCollapsing = builder.apply("enableBlockCollapsing").comment("Enable rock collapsing when mining raw stone blocks").define("enableBlockCollapsing", true);
        enableExplosionCollapsing = builder.apply("enableExplosionCollapsing").comment("Enable explosions causing immediate collapses.").define("enableExplosionCollapsing", true);
        enableBlockLandslides = builder.apply("enableBlockLandslides").comment("Enable land slides (gravity affected blocks) when placing blocks or on block updates.").define("enableBlockLandslides", true);
        enableChiselsStartCollapses = builder.apply("enableChiselsStartCollapses").comment("Enable chisels starting collapses").define("enableChiselsStartCollapses", true);

        collapseTriggerChance = builder.apply("collapseTriggerChance").comment("Chance for a collapse to be triggered by mining a block.").defineInRange("collapseTriggerChance", 0.1, 0, 1);
        collapseFakeTriggerChance = builder.apply("collapseFakeTriggerChance").comment("Chance for a collapse to be fake triggered by mining a block.").defineInRange("collapseFakeTriggerChance", 0.35, 0, 1);
        collapsePropagateChance = builder.apply("collapsePropagateChance").comment("Chance for a block fo fall from mining collapse. Higher = more likely.").defineInRange("collapsePropagateChance", 0.55, 0, 1);
        collapseExplosionPropagateChance = builder.apply("collapseExplosionPropagateChance").comment("Chance for a block to fall from an explosion triggered collapse. Higher = mor likely.").defineInRange("collapseExplosionPropagateChance", 0.3, 0, 1);
        collapseMinRadius = builder.apply("collapseMinRadius").comment("Minimum radius for a collapse").defineInRange("collapseMinRadius", 3, 1, 32);
        collapseRadiusVariance = builder.apply("collapseRadiusVariance").comment("Variance of the radius of a collapse. Total size is in [minRadius, minRadius + radiusVariance]").defineInRange("collapseRadiusVariance", 16, 1, 32);

        innerBuilder.pop().push("player");

        enablePeacefulDifficultyPassiveRegeneration = builder.apply("enablePeacefulDifficultyPassiveRegeneration").comment("If peaceful difficulty should still have vanilla-esque passive regeneration of health, food, and hunger").define("enablePeacefulDifficultyPassiveRegeneration", false);
        passiveExhaustionModifier = builder.apply("passiveExhaustionMultiplier").comment(
            "A multiplier for passive exhaustion accumulation.",
            "Exhaustion is the hidden stat which controls when you get hungry. In vanilla it is incremented by running and jumping for example. In TFC, exhaustion is added just by existing.",
            "1.0 = A full hunger bar's worth of exhaustion every 2.5 days. Set to zero to disable completely.").defineInRange("passiveExhaustionMultiplier", 1d, 0d, 100d);
        thirstModifier = builder.apply("thirstModifier").comment(
            "A multiplier for how quickly the player gets thirsty.",
            "The player loses thirst in sync with when they lose hunger. This represents how much thirst they lose. 0 = None, 100 = the entire thirst bar.").defineInRange("thirstModifier1", 5d, 0d, 100d);
        enableThirstOverheating = builder.apply("enableThirstOverheating").comment("Enables the player losing more thirst in hotter environments.").define("enableThirstOverheating", true);
        thirstGainedFromDrinkingInTheRain = builder.apply("thirstGainedFromDrinkingInTheRain").comment("How much thirst the player gains from drinking in the rain (standing outside in the rain and looking up) per tick.").defineInRange("thirstGainedFromDrinkingInTheRain", 5d / 24d, 0d, 100d);
        naturalRegenerationModifier = builder.apply("naturalRegenerationModifier").comment(
            "A multiplier for how quickly the player regenerates health, under TFC's passive regeneration.",
            "By default, the player regenerates 0.2 HP/second, or 0.6 HP/second when above 80% hunger and thirst, where 1 HP = 1/50 of a heart.").defineInRange("naturalRegenerationModifier", 1d, 0d, 100d);
        nutritionRotationHungerWindow = builder.apply("nutritionRotationHungerWindow").comment(
            "How much total hunger consumed is required to completely refresh the player's nutrition.",
            "Player nutrition in TFC is calculated based on nutrition of the last few foods eaten - this is how many foods are used to calculate nutrition. By default, all TFC foods restore 4 hunger.").defineInRange("nutritionRotationHungerWindow", 80, 1, Integer.MAX_VALUE);
        keepNutritionAfterDeath = builder.apply("keepNutritionAfterDeath").comment(
            "If player's nutrition should be kept even after death. Hunger and thirst are not affected and will be reset.").define("keepNutritionAfterDeath", true);
        foodDecayStackWindow = builder.apply("foodDecayStackWindow").comment(
            "How many hours should different foods ignore when trying to stack together automatically?",
            "Food made with different creation dates doesn't stack by default, unless it's within a specific window. This is the number of hours that different foods will try and stack together at the loss of a little extra expiry time.").defineInRange("foodDecayStackWindow", 6, 1, 100);
        foodDecayModifier = builder.apply("foodDecayModifier").comment(
            " A multiplier for food decay, or expiration times. Larger values will result in naturally shorter expiration times.",
            " Setting this to zero will cause decay not to apply.",
            " Note that if you set this to zero **food items will lose their creation dates!!**. This is not reversible!"
        ).defineInRange("foodDecayModifier", 1d, 0d, 1000d);
        enableOverburdening = builder.apply("enableOverburdening").comment("Enables negative effects from carrying too many very heavy items, including potion effects.").define("enableOverburdening", true);
        nutritionMinimumHealthModifier = builder.apply("nutritionMinimumHealthModifier").comment("A multiplier for the minimum health that the player will obtain, based on their nutrition").defineInRange("nutritionMinimumHealthModifier", 0.2, 0.001, 1000);
        nutritionDefaultHealthModifier = builder.apply("nutritionDefaultHealthModifier").comment(
            "A multiplier for the default health that the player will have (this is at a average nutrition of 40%, aka the starting nutrition.",
            "Nutrition above this value will linearly scale to the maximum multiplier.",
            "Nutrition below this value will linearly scale to the minimum multiplier."
        ).defineInRange("nutritionDefaultHealthModifier", 0.85, 0.001, 1000);
        nutritionMaximumHealthModifier = builder.apply("nutritionMaximumHealthModifier").comment("A multiplier for the maximum health that the player will obtain, based on their nutrition").defineInRange("nutritionMaximumHealthModifier", 3.0, 0.001, 1000);

        innerBuilder.pop().push("foodTraits");

        traitSaltedModifier = builder.apply("traitSaltedModifier").comment("The modifier for the 'Salted' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitSaltedModifier", 0.5, 0, Double.MAX_VALUE);
        traitBrinedModifier = builder.apply("traitBrinedModifier").comment("The modifier for the 'Brined' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitBrinedModifier", 1, 0, Double.MAX_VALUE);
        traitPickledModifier = builder.apply("traitPickledModifier").comment("The modifier for the 'Pickled' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitPickledModifier", 0.5, 0, Double.MAX_VALUE);
        traitPreservedModifier = builder.apply("traitPreservedModifier").comment("The modifier for the 'Preserved' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitPreservedModifier", 0.5, 0, Double.MAX_VALUE);
        traitVinegarModifier = builder.apply("traitVinegarModifier").comment("The modifier for the 'Vinegar' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitVinegarModifier", 0.1, 0, Double.MAX_VALUE);
        traitCharcoalGrilledModifier = builder.apply("traitCharcoalGrilledModifier").comment("The modifier for the 'Charcoal Grilled' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitCharcoalGrilledModifier", 1.25, 0, Double.MAX_VALUE);
        traitWoodGrilledModifier = builder.apply("traitWoodGrilledModifier").comment("The modifier for the 'Wood Grilled' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitWoodGrilledModifier", 0.8, 0, Double.MAX_VALUE);
        traitBurntToACrispModifier = builder.apply("traitBurntToACrispModifier").comment("The modifier for the 'Burnt To A Crisp' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitBurntToACrispModifier", 2.5, 0, Double.MAX_VALUE);
        traitWildModifier = builder.apply("traitWildModifier").comment("The modifier for the 'Wild' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.").defineInRange("traitWildModifier", 0.5, 0, Double.MAX_VALUE);
        
        innerBuilder.pop().push("fluids");

        enableBucketsPlacingSources = builder.apply("enableBucketsPlacingSources").comment("If true, TFC buckets that naturally place sources (colored steel) will place sources. If false, this behavior is disabled.").define("enableBucketsPlacingSources", true);

        innerBuilder.pop().push("vanillaChanges");

        enableVanillaBonemeal = builder.apply("enableVanillaBonemeal").comment("If vanilla bonemeal's instant-growth effect should be enabled.").define("enableVanillaBonemeal", false);
        enableVanillaWeatherEffects = builder.apply("enableVanillaWeatherEffects").comment("If true, vanilla's snow and ice formation mechanics will be used, and none of the TFC mechanics (improved snow and ice placement, snow stacking, icicle formation, passive snow or ice melting) will exist.").define("enableVanillaWeatherEffects", false);
        enableVanillaSkeletonHorseSpawning = builder.apply("enableVanillaSkeletonHorseSpawning").comment("If true, vanilla will attempt to spawn skeleton 'trap' horses during thunderstorms.").define("enableVanillaSkeletonHorseSpawning", false);
        enableVanillaMobsSpawningWithEnchantments = builder.apply("enableVanillaMobsSpawningWithEnchantments").comment("If true, enables the default vanilla behavior of mobs spawning with enchanted weapons sometimes.").define("enableVanillaMobsSpawningWithEnchantments", false);
        enableVanillaMobsSpawningWithVanillaEquipment = builder.apply("enableVanillaMobsSpawningWithVanillaEquipment").comment("If true, enables the default behavior of mobs sapwning with vanilla armor and weapons").define("enableVanillaMobsSpawningWithVanillaEquipment", false);
        enableVanillaGolems = builder.apply("enableVanillaGolems").comment("If true, golems can be built").define("enableVanillaGolems", false);
        enableVanillaMonsters = builder.apply("enableVanillaMonsters").comment("If true, vanilla monsters are able to spawn. If false, the 'enableVanillaMonstersOnSurface' config option is not used, and all spawns are denied.").define("enableVanillaMonsters", true);
        enableVanillaMonstersOnSurface = builder.apply("enableVanillaMonstersOnSurface").comment("If true, vanilla monsters will spawn on the surface instead of just underground. If false, vanilla monsters will not spawn on the surface.").define("enableVanillaMonstersOnSurface", false);
        enableChickenJockies = builder.apply("enableChickenJockies").comment("If true, chicken jockies can spawn").define("enableChickenJockies", false);
        enableVanillaEggThrowing = builder.apply("enableVanillaEggThrowing").comment("If true, eggs can be thrown.").define("enableVanillaEggThrowing", false);
        enableVanillaDrinkingMilkClearsPotionEffects = builder.apply("enableVanillaDrinkingMilkClearsPotionEffects").comment("If true, drinking milk will clear potion effects and restore no nutrition, as in vanilla.").define("enableVanillaDrinkingMilkClearsPotionEffects", false);

        innerBuilder.pop().push("animals").push("pig");
        pigConfig = MammalConfig.build(builder, "pig", 0.35, 80, 60, true, 19, 10);

        innerBuilder.pop().push("donkey");
        donkeyConfig = MammalConfig.build(builder, "donkey", 0.35, 80, 60, false, 19, 1);

        innerBuilder.pop().push("mule");
        muleConfig = MammalConfig.build(builder, "mule", 0.35, 80, 60, false, 19, 1);

        innerBuilder.pop().push("horse");
        horseConfig = MammalConfig.build(builder, "horse", 0.35, 80, 60, false, 19, 1);

        innerBuilder.pop().push("cat");
        catConfig = MammalConfig.build(builder, "cat", 0.35, 50, 60, false, 19, 6);

        innerBuilder.pop().push("dog");
        dogConfig = MammalConfig.build(builder, "dog", 0.35, 50, 60, true, 19, 2);

        innerBuilder.pop().push("cow");
        cowConfig = ProducingMammalConfig.build(builder, "cow", 0.35, 192, 128, true, 58, 2, 24000, 0.15);

        innerBuilder.pop().push("goat");
        goatConfig = ProducingMammalConfig.build(builder, "goat", 0.35, 96, 60, true, 32, 2, 72000, 0.15);

        innerBuilder.pop().push("yak");
        yakConfig = ProducingMammalConfig.build(builder, "yak", 0.35, 180, 230, false, 64, 1, 23500, 0.15);

        innerBuilder.pop().push("alpaca");
        alpacaConfig = ProducingMammalConfig.build(builder, "alpaca", 0.35, 98, 128, false, 36, 2, 120000, 0.15);

        innerBuilder.pop().push("sheep");
        sheepConfig = ProducingMammalConfig.build(builder, "sheep", 0.35, 56, 60, false, 32, 2, 168000, 0.15);

        innerBuilder.pop().push("muskOx");
        muskOxConfig = ProducingMammalConfig.build(builder, "muskOx", 0.35, 168, 160, false, 64, 1, 96000, 0.15);

        innerBuilder.pop().push("chicken");
        chickenConfig = OviparousAnimalConfig.build(builder, "chicken", 0.35, 24, 100, true, 30000, 0.15, 8);

        innerBuilder.pop().push("duck");
        duckConfig = OviparousAnimalConfig.build(builder, "duck", 0.35, 32, 72, false, 32000, 0.15, 8);

        innerBuilder.pop().push("quail");
        quailConfig = OviparousAnimalConfig.build(builder, "quail", 0.35, 22, 48, true, 28000, 0.15, 8);
        innerBuilder.pop(3);

        farmlandMakesTheBestRaceTracks = builder.apply("farmlandMakesTheBestRaceTracks").define("farmlandMakesTheBestRaceTracks", false);
    }
}
