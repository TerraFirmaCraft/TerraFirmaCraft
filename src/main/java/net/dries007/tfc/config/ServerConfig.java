/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.util.Alloy;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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
    public final ForgeConfigSpec.BooleanValue enableFireArrowSpreading;
    public final ForgeConfigSpec.DoubleValue fireStarterChance;
    // Blocks - Farmland
    public final ForgeConfigSpec.BooleanValue enableFarmlandCreation;
    // Blocks - Grass Path
    public final ForgeConfigSpec.BooleanValue enableGrassPathCreation;
    // Blocks - Snow
    public final ForgeConfigSpec.BooleanValue enableSnowSlowEntities;
    // Blocks - Leaves
    public final ForgeConfigSpec.BooleanValue enableLeavesSlowEntities;
    // Blocks - Plants
    public final ForgeConfigSpec.DoubleValue plantGrowthChance;
    // Blocks - Cobblestone
    public final ForgeConfigSpec.BooleanValue enableMossyRockSpreading;
    public final ForgeConfigSpec.IntValue mossyRockSpreadRate;
    // Blocks - Chest
    public final ForgeConfigSpec.EnumValue<Size> chestMaximumItemSize;
    // Blocks - Torch
    public final ForgeConfigSpec.IntValue torchTicks;
    // Blocks - Torch
    public final ForgeConfigSpec.IntValue candleTicks;
    // Blocks - Charcoal Pit
    public final ForgeConfigSpec.IntValue charcoalTicks;
    // Blocks - Pit Kiln
    public final ForgeConfigSpec.IntValue pitKilnTicks;
    public final ForgeConfigSpec.IntValue pitKilnTemperature;
    // Blocks - Crucible
    public final ForgeConfigSpec.IntValue crucibleCapacity;
    public final ForgeConfigSpec.IntValue cruciblePouringRate;
    // Blocks - Anvil
    public final ForgeConfigSpec.IntValue anvilAcceptableWorkRange;
    // Blocks - Barrel
    public final ForgeConfigSpec.IntValue barrelCapacity;
    // Blocks - Composter
    public final ForgeConfigSpec.IntValue composterTicks;
    public final ForgeConfigSpec.BooleanValue composterRainfallCheck;
    // Blocks - Sluice
    public final ForgeConfigSpec.IntValue sluiceTicks;
    // Blocks - Lamp
    public final ForgeConfigSpec.IntValue lampCapacity;
    // Blocks - Bloomery
    public final ForgeConfigSpec.IntValue bloomeryCapacity;
    public final ForgeConfigSpec.IntValue bloomeryMaxChimneyHeight;
    // Blocks - Blast Furnace
    public final ForgeConfigSpec.IntValue blastFurnaceCapacity;
    public final ForgeConfigSpec.IntValue blastFurnaceFluidCapacity;
    public final ForgeConfigSpec.IntValue blastFurnaceFuelConsumptionMultiplier;
    public final ForgeConfigSpec.IntValue blastFurnaceMaxChimneyHeight;
    // Items - Small Vessel
    public final ForgeConfigSpec.IntValue smallVesselCapacity;
    public final ForgeConfigSpec.EnumValue<Size> smallVesselMaximumItemSize;
    // Items - Mold(s)
    public final ForgeConfigSpec.IntValue moldIngotCapacity;
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
    // Items - Jug
    public final ForgeConfigSpec.IntValue jugCapacity;
    public final ForgeConfigSpec.DoubleValue jugBreakChance;
    // Items - Wooden Bucket
    public final ForgeConfigSpec.IntValue woodenBucketCapacity;
    public final ForgeConfigSpec.BooleanValue enableSourcesFromWoodenBucket;
    // Mechanics - Heat
    public final ForgeConfigSpec.DoubleValue heatingModifier;
    public final ForgeConfigSpec.IntValue ticksBeforeItemCool;
    public final ForgeConfigSpec.BooleanValue coolHotItemEntities;
    // Mechanics - Collapses
    public final ForgeConfigSpec.BooleanValue enableBlockCollapsing;
    public final ForgeConfigSpec.BooleanValue enableExplosionCollapsing;
    public final ForgeConfigSpec.BooleanValue enableBlockLandslides;
    public final ForgeConfigSpec.BooleanValue enableChiselsStartCollapses;
    public final ForgeConfigSpec.DoubleValue collapseTriggerChance;
    public final ForgeConfigSpec.DoubleValue collapsePropagateChance;
    public final ForgeConfigSpec.DoubleValue collapseExplosionPropagateChance;
    public final ForgeConfigSpec.IntValue collapseMinRadius;
    public final ForgeConfigSpec.IntValue collapseRadiusVariance;
    // Mechanics - Food / Nutrition
    public final ForgeConfigSpec.BooleanValue enablePeacefulDifficultyPassiveRegeneration;
    public final ForgeConfigSpec.DoubleValue passiveExhaustionModifier;
    public final ForgeConfigSpec.DoubleValue thirstModifier;
    public final ForgeConfigSpec.DoubleValue thirstGainedFromDrinkingInTheRain;
    public final ForgeConfigSpec.DoubleValue naturalRegenerationModifier;
    public final ForgeConfigSpec.IntValue nutritionRotationHungerWindow;
    public final ForgeConfigSpec.IntValue foodDecayStackWindow;
    public final ForgeConfigSpec.DoubleValue foodDecayModifier;
    // Mechanics - Vanilla Changes
    public final ForgeConfigSpec.BooleanValue enableVanillaBonemeal;
    public final ForgeConfigSpec.BooleanValue enableVanillaWeatherEffects;

    // Animals
    // Pig
    public final ForgeConfigSpec.DoubleValue pigFamiliarityCap;
    public final ForgeConfigSpec.IntValue pigAdulthoodDays;
    public final ForgeConfigSpec.IntValue pigUses;
    public final ForgeConfigSpec.BooleanValue pigEatsRottenFood;
    public final ForgeConfigSpec.IntValue pigGestationDays;
    public final ForgeConfigSpec.IntValue pigChildCount;

    // Cow
    public final ForgeConfigSpec.DoubleValue cowFamiliarityCap;
    public final ForgeConfigSpec.IntValue cowAdulthoodDays;
    public final ForgeConfigSpec.IntValue cowUses;
    public final ForgeConfigSpec.BooleanValue cowEatsRottenFood;
    public final ForgeConfigSpec.IntValue cowGestationDays;
    public final ForgeConfigSpec.IntValue cowChildCount;
    public final ForgeConfigSpec.IntValue cowMilkTicks;
    public final ForgeConfigSpec.DoubleValue cowMinMilkFamiliarity;

    // Yak
    public final ForgeConfigSpec.DoubleValue yakFamiliarityCap;
    public final ForgeConfigSpec.IntValue yakAdulthoodDays;
    public final ForgeConfigSpec.IntValue yakUses;
    public final ForgeConfigSpec.BooleanValue yakEatsRottenFood;
    public final ForgeConfigSpec.IntValue yakGestationDays;
    public final ForgeConfigSpec.IntValue yakChildCount;
    public final ForgeConfigSpec.IntValue yakMilkTicks;
    public final ForgeConfigSpec.DoubleValue yakMinMilkFamiliarity;

    // Goat
    public final ForgeConfigSpec.DoubleValue goatFamiliarityCap;
    public final ForgeConfigSpec.IntValue goatAdulthoodDays;
    public final ForgeConfigSpec.IntValue goatUses;
    public final ForgeConfigSpec.BooleanValue goatEatsRottenFood;
    public final ForgeConfigSpec.IntValue goatGestationDays;
    public final ForgeConfigSpec.IntValue goatChildCount;
    public final ForgeConfigSpec.IntValue goatMilkTicks;
    public final ForgeConfigSpec.DoubleValue goatMinMilkFamiliarity;

    // Alpaca
    public final ForgeConfigSpec.DoubleValue alpacaFamiliarityCap;
    public final ForgeConfigSpec.IntValue alpacaAdulthoodDays;
    public final ForgeConfigSpec.IntValue alpacaUses;
    public final ForgeConfigSpec.BooleanValue alpacaEatsRottenFood;
    public final ForgeConfigSpec.IntValue alpacaGestationDays;
    public final ForgeConfigSpec.IntValue alpacaChildCount;
    public final ForgeConfigSpec.IntValue alpacaWoolTicks;
    public final ForgeConfigSpec.DoubleValue alpacaMinWoolFamiliarity;

    // Sheep
    public final ForgeConfigSpec.DoubleValue sheepFamiliarityCap;
    public final ForgeConfigSpec.IntValue sheepAdulthoodDays;
    public final ForgeConfigSpec.IntValue sheepUses;
    public final ForgeConfigSpec.BooleanValue sheepEatsRottenFood;
    public final ForgeConfigSpec.IntValue sheepGestationDays;
    public final ForgeConfigSpec.IntValue sheepChildCount;
    public final ForgeConfigSpec.IntValue sheepWoolTicks;
    public final ForgeConfigSpec.DoubleValue sheepMinWoolFamiliarity;

    // Musk Ox
    public final ForgeConfigSpec.DoubleValue muskOxFamiliarityCap;
    public final ForgeConfigSpec.IntValue muskOxAdulthoodDays;
    public final ForgeConfigSpec.IntValue muskOxUses;
    public final ForgeConfigSpec.BooleanValue muskOxEatsRottenFood;
    public final ForgeConfigSpec.IntValue muskOxGestationDays;
    public final ForgeConfigSpec.IntValue muskOxChildCount;
    public final ForgeConfigSpec.IntValue muskOxWoolTicks;
    public final ForgeConfigSpec.DoubleValue muskOxMinWoolFamiliarity;

    // Chicken
    public final ForgeConfigSpec.DoubleValue chickenFamiliarityCap;
    public final ForgeConfigSpec.IntValue chickenAdulthoodDays;
    public final ForgeConfigSpec.IntValue chickenUses;
    public final ForgeConfigSpec.BooleanValue chickenEatsRottenFood;
    public final ForgeConfigSpec.IntValue chickenEggTicks;
    public final ForgeConfigSpec.DoubleValue chickenMinEggFamiliarity;
    public final ForgeConfigSpec.IntValue chickenHatchDays;

    // Duck
    public final ForgeConfigSpec.DoubleValue duckFamiliarityCap;
    public final ForgeConfigSpec.IntValue duckAdulthoodDays;
    public final ForgeConfigSpec.IntValue duckUses;
    public final ForgeConfigSpec.BooleanValue duckEatsRottenFood;
    public final ForgeConfigSpec.IntValue duckEggTicks;
    public final ForgeConfigSpec.DoubleValue duckMinEggFamiliarity;
    public final ForgeConfigSpec.IntValue duckHatchDays;

    // Quail
    public final ForgeConfigSpec.DoubleValue quailFamiliarityCap;
    public final ForgeConfigSpec.IntValue quailAdulthoodDays;
    public final ForgeConfigSpec.IntValue quailUses;
    public final ForgeConfigSpec.BooleanValue quailEatsRottenFood;
    public final ForgeConfigSpec.IntValue quailEggTicks;
    public final ForgeConfigSpec.DoubleValue quailMinEggFamiliarity;
    public final ForgeConfigSpec.IntValue quailHatchDays;

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
        enableFireArrowSpreading = builder.apply("enableFireArrowSpreading").comment("Enable fire arrows and fireballs to spread fire and light blocks.").define("enableFireArrowSpreading", true);
        fireStarterChance = builder.apply("fireStarterChance").comment("Base probability for a firestarter to start a fire. May change based on circumstances").defineInRange("fireStarterChance", 0.5, 0, 1);

        innerBuilder.pop().push("blocks").push("farmland");

        enableFarmlandCreation = builder.apply("enableFarmlandCreation").comment("If TFC soil blocks are able to be created into farmland").define("enableFarmlandCreation", true);

        innerBuilder.pop().push("grassPath");

        enableGrassPathCreation = builder.apply("enableGrassPathCreation").comment("If TFC soil blocks are able to be created into (grass) path blocks.").define("enableGrassPathCreation", true);

        innerBuilder.pop().push("snow");

        enableSnowSlowEntities = builder.apply("enableSnowSlowEntities").comment("[Requires MC Restart] If snow will slow players that move on top of it similar to soul sand or honey.").define("enableSnowSlowEntities", true);

        innerBuilder.pop().push("plants");

        plantGrowthChance = builder.apply("plantGrowthChance").comment("Chance for a plant to grow each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant growth.").defineInRange("plantGrowthChance", 0.05, 0, 1);

        innerBuilder.pop().push("leaves");

        enableLeavesSlowEntities = builder.apply("enableLeavesSlowEntities").comment("If leaves will slow entities passing through them and reduce fall damage.").define("enableLeavesSlowEntities", true);

        innerBuilder.pop().push("cobblestone");

        enableMossyRockSpreading = builder.apply("enableMossyRockSpreading").comment("If mossy rock blocks will spread their moss to nearby rock blocks (bricks and cobble; stairs, slabs and walls thereof).").define("enableMossyRockSpreading", true);
        mossyRockSpreadRate = builder.apply("mossyRockSpreadRate").comment("The rate at which rock blocks will accumulate moss. Higher value = slower.").defineInRange("mossyRockSpreadRate", 20, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("chest");
        chestMaximumItemSize = builder.apply("chestMaximumItemSize").comment("The largest (inclusive) size of an item that is allowed in a chest.").defineEnum("chestMaximumItemSize", Size.LARGE);

        innerBuilder.pop().push("torch");

        torchTicks = builder.apply("torchTicks").comment("Number of ticks required for a torch to burn out (1000 = 1 in game hour = 50 seconds), default is 72 hours. Set to -1 to disable torch burnout.").defineInRange("torchTicks", 72000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("candle");

        candleTicks = builder.apply("candleTicks").comment("Number of ticks required for a candle to burn out (1000 = 1 in game hour = 50 seconds), default is 264 hours. Set to -1 to disable candle burnout.").defineInRange("candleTicks", 264000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("charcoal");

        charcoalTicks = builder.apply("charcoalTicks").comment("Number of ticks required for charcoal pit to complete. (1000 = 1 in game hour = 50 seconds), default is 18 hours.").defineInRange("charcoalTicks", 18000, -1, Integer.MAX_VALUE);

        innerBuilder.pop().push("pitKiln");

        pitKilnTicks = builder.apply("pitKilnTicks").comment("Number of ticks required for a pit kiln to burn out. (1000 = 1 in game hour = 50 seconds), default is 8 hours.").defineInRange("pitKilnTicks", 8000, 20, Integer.MAX_VALUE);
        pitKilnTemperature = builder.apply("pitKilnTemperature").comment("The maximum temperature which a pit kiln reaches. (1200 = Yellow**, 1600 = Brilliant White, for reference).").defineInRange("pitKilnTemperature", 1600, 0, Integer.MAX_VALUE);

        innerBuilder.pop().push("crucible");

        crucibleCapacity = builder.apply("crucibleCapacity").comment("Tank capacity of a crucible (in mB).").defineInRange("crucibleCapacity", 4000, 0, Alloy.MAX_ALLOY);
        cruciblePouringRate = builder.apply("cruciblePouringRate").comment("A modifier for how fast fluid containers empty into crucibles. Containers will empty 1 mB every (this) number of ticks.").defineInRange("cruciblePouringRate", 4, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("anvil");

        anvilAcceptableWorkRange = builder.apply("anvilAcceptableWorkRange").comment("The number of pixels that the anvil's result may be off by, but still count as recipe completion. By default this requires pixel perfect accuracy.").defineInRange("anvilAcceptableWorkRange", 0, 0, 150);

        innerBuilder.pop().push("barrel");

        barrelCapacity = builder.apply("barrelCapacity").comment("Tank capacity of a barrel (in mB).").defineInRange("barrelCapacity", 10000, 0, Integer.MAX_VALUE);

        innerBuilder.pop().push("composter");

        composterTicks = builder.apply("composterTicks").comment("Number of ticks required for a composter in normal conditions to complete. (24000 = 1 game day), default is 12 days.").defineInRange("composterTicks", 288000, 20, Integer.MAX_VALUE);
        composterRainfallCheck = builder.apply("composterRainfallCheck").comment("Should the composter work less efficiently at high or low rainfalls?").define("composterRainfallCheck", true);

        innerBuilder.pop().push("sluice");
        sluiceTicks = builder.apply("sluiceTicks").comment("Number of ticks required for a sluice to process an item. (20 = 1 second), default is 5 seconds.").defineInRange("sluiceTicks", 100, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("lamp");

        lampCapacity = builder.apply("lampCapacity").comment("Tank capacity of a lamp (in mB).").defineInRange("lampCapacity", 250, 0, Alloy.MAX_ALLOY);

        innerBuilder.pop().push("bloomery");

        bloomeryCapacity = builder.apply("bloomeryCapacity").comment("Inventory capacity (in number of items per level of chimney) of the bloomery.").defineInRange("bloomeryCapacity", 8, 1, Integer.MAX_VALUE);
        bloomeryMaxChimneyHeight = builder.apply("bloomeryMaxChimneyHeight").comment("The maximum number of levels that can be built in a bloomery multiblock, for added capacity.").defineInRange("bloomeryMaxChimneyHeight", 3, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("blastFurnace");

        blastFurnaceCapacity = builder.apply("blastFurnaceCapacity").comment("Inventory capacity (in number of items per level of chimney) of the blast furnace.").defineInRange("blastFurnaceCapacity", 4, 1, Integer.MAX_VALUE);
        blastFurnaceFluidCapacity = builder.apply("blastFurnaceFluidCapacity").comment("Fluid capacity (in mB) of the output tank of the blast furnace.").defineInRange("blastFurnaceFluidCapacity", 10_000, 1, Integer.MAX_VALUE);
        blastFurnaceFuelConsumptionMultiplier = builder.apply("blastFurnaceFuelConsumptionMultiplier").comment("A multiplier for how fast the blast furnace consumes fuel. Higher values = faster fuel consumption.").defineInRange("blastFurnaceFuelConsumptionMultiplier", 4, 1, Integer.MAX_VALUE);
        blastFurnaceMaxChimneyHeight = builder.apply("blastFurnaceMaxChimneyHeight").comment("The maximum number of levels that can be built in a blast furnace multiblock, for added capacity.").defineInRange("blastFurnaceMaxChimneyHeight", 5, 1, Integer.MAX_VALUE);

        innerBuilder.pop().pop().push("items").push("smallVessel");

        smallVesselCapacity = builder.apply("smallVesselCapacity").comment("Tank capacity of a small vessel (in mB).").defineInRange("smallVesselCapacity", 3000, 0, Alloy.MAX_ALLOY);
        smallVesselMaximumItemSize = builder.apply("smallVesselMaximumItemSize").comment("The largest (inclusive) size of an item that is allowed in a small vessel.").defineEnum("smallVesselMaximumItemSize", Size.SMALL);

        innerBuilder.pop().push("molds");

        moldIngotCapacity = builder.apply("moldIngotCapacity").comment("Tank capacity of a Ingot mold (in mB).").defineInRange("moldIngotCapacity", 100, 0, Alloy.MAX_ALLOY);
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

        innerBuilder.pop().push("jug");

        jugCapacity = builder.apply("jugCapacity").comment("Tank capacity of a ceramic jug (in mB).").defineInRange("jugCapacity", 100, 0, Alloy.MAX_ALLOY);
        jugBreakChance = builder.apply("jugBreakChance").comment("The chance a jug will break after drinking.").defineInRange("jugBreakChance", 0.02, 0, 1);

        innerBuilder.pop().push("woodenBucket");
        woodenBucketCapacity = builder.apply("woodenBucketCapacity").comment("Tank capacity of a wooden bucket (in mB).").defineInRange("woodenBucketCapacity", 1000, 0, Alloy.MAX_ALLOY);
        enableSourcesFromWoodenBucket = builder.apply("enableSourcesFromWoodenBucket").comment("Should the wooden bucket place source blocks?").define("enableSourcesFromWoodenBucket", false);

        innerBuilder.pop().pop().push("mechanics").push("heat");

        heatingModifier = builder.apply("itemHeatingModifier").comment("A multiplier for how fast items heat and cool. Higher = faster.").defineInRange("itemHeatingModifier", 1, 0, Double.MAX_VALUE);
        coolHotItemEntities = builder.apply("coolHotItemEntities").comment("Should hot item entities cool off when in contact with blocks like water or snow?").define("coolHotItemEntities", true);
        ticksBeforeItemCool = builder.apply("ticksBeforeItemCool").comment("Ticks between each time an item loses temperature when sitting on a cold block. 20 ticks = 1 second.").defineInRange("ticksBeforeItemCool", 10, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("collapses");

        enableBlockCollapsing = builder.apply("enableBlockCollapsing").comment("Enable rock collapsing when mining raw stone blocks").define("enableBlockCollapsing", true);
        enableExplosionCollapsing = builder.apply("enableExplosionCollapsing").comment("Enable explosions causing immediate collapses.").define("enableExplosionCollapsing", true);
        enableBlockLandslides = builder.apply("enableBlockLandslides").comment("Enable land slides (gravity affected blocks) when placing blocks or on block updates.").define("enableBlockLandslides", true);
        enableChiselsStartCollapses = builder.apply("enableChiselsStartCollapses").comment("Enable chisels starting collapses").define("enableChiselsStartCollapses", true);

        collapseTriggerChance = builder.apply("collapseTriggerChance").comment("Chance for a collapse to be triggered by mining a block.").defineInRange("collapseTriggerChance", 0.1, 0, 1);
        collapsePropagateChance = builder.apply("collapsePropagateChance").comment("Chance for a block fo fall from mining collapse. Higher = mor likely.").defineInRange("collapsePropagateChance", 0.55, 0, 1);
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
            "The player loses thirst in sync with when they lose hunger. This represents how much thirst they lose. 0 = None, 100 = the entire thirst bar.").defineInRange("thirstModifier", 8d, 0d, 100d);
        thirstGainedFromDrinkingInTheRain = builder.apply("thirstGainedFromDrinkingInTheRain").comment("How much thirst the player gains from drinking in the rain (standing outside in the rain and looking up) per tick.").defineInRange("thirstGainedFromDrinkingInTheRain", 5d / 24d, 0d, 100d);
        naturalRegenerationModifier = builder.apply("naturalRegenerationModifier").comment(
            "A multiplier for how quickly the player regenerates health, under TFC's passive regeneration.",
            "By default, the player regenerates 0.2 HP/second, or 0.6 HP/second when above 80% hunger and thirst, where 1 HP = 1/50 of a heart.").defineInRange("naturalRegenerationModifier", 1d, 0d, 100d);
        nutritionRotationHungerWindow = builder.apply("nutritionRotationHungerWindow").comment(
            "How much total hunger consumed is required to completely refresh the player's nutrition.",
            "Player nutrition in TFC is calculated based on nutrition of the last few foods eaten - this is how many foods are used to calculate nutrition. By default, all TFC foods restore 4 hunger.").defineInRange("nutritionRotationHungerWindow", 80, 1, Integer.MAX_VALUE);
        foodDecayStackWindow = builder.apply("foodDecayStackWindow").comment(
            "How many hours should different foods ignore when trying to stack together automatically?",
            "Food made with different creation dates doesn't stack by default, unless it's within a specific window. This is the number of hours that different foods will try and stack together at the loss of a little extra expiry time.").defineInRange("foodDecayStackWindow", 1, 6, 100);
        foodDecayModifier = builder.apply("foodDecayModifier").comment("A multiplier for food decay, or expiration times. Larger values will result in naturally longer expiration times.").defineInRange("foodDecayModifier", 1d, 0d, 1000d);

        innerBuilder.pop().push("vanillaChanges");

        enableVanillaBonemeal = builder.apply("enableVanillaBonemeal").comment("If vanilla bonemeal's instant-growth effect should be enabled.").define("enableVanillaBonemeal", false);
        enableVanillaWeatherEffects = builder.apply("enableVanillaWeatherEffects").comment("If true, vanilla's snow and ice formation mechanics will be used, and none of the TFC mechanics (improved snow and ice placement, snow stacking, icicle formation, passive snow or ice melting) will exist.").define("enableVanillaWeatherEffects", false);

        innerBuilder.pop().push("animals").push("pig");
        pigAdulthoodDays = builder.apply("pigAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("pigAdulthoodDays", 56, 0, Integer.MAX_VALUE);
        pigChildCount = builder.apply("pigChildCount").comment("Max number of children born").defineInRange("pigChildCount", 10, 0, 100);
        pigEatsRottenFood = builder.apply("pigEatsRottenFood").comment("Does the animal eat rotten food?").define("pigEatsRottenFood", true);
        pigFamiliarityCap = builder.apply("pigFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("pigFamiliarityCap", 0.35, 0, 1);
        pigUses = builder.apply("pigUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("pigUses", 40, 0, Integer.MAX_VALUE);
        pigGestationDays = builder.apply("pigGestationDays").comment("Length of pregnancy in days").defineInRange("pigGestationDays", 20, 0, Integer.MAX_VALUE);

        innerBuilder.pop().push("cow");
        cowAdulthoodDays = builder.apply("cowAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("cowAdulthoodDays", 124, 0, Integer.MAX_VALUE);
        cowChildCount = builder.apply("cowChildCount").comment("Max number of children born").defineInRange("cowChildCount", 1, 0, 100);
        cowEatsRottenFood = builder.apply("cowEatsRottenFood").comment("Does the animal eat rotten food?").define("cowEatsRottenFood", true);
        cowFamiliarityCap = builder.apply("cowFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("cowFamiliarityCap", 0.35, 0, 1);
        cowUses = builder.apply("cowUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("cowUses", 140, 0, Integer.MAX_VALUE);
        cowGestationDays = builder.apply("cowGestationDays").comment("Length of pregnancy in days").defineInRange("cowGestationDays", 64, 0, Integer.MAX_VALUE);
        cowMilkTicks = builder.apply("cowMilkTicks").comment("Ticks until milk is ready").defineInRange("cowMilkTicks", 23500, 0, Integer.MAX_VALUE);
        cowMinMilkFamiliarity = builder.apply("cowMinMilkFamiliarity").comment("Minimum familiarity [0-1] needed to milk. Set above 1 to disable milking.").defineInRange("cowMinMilkFamiliarity", 0.15d, 0, Float.MAX_VALUE);

        innerBuilder.pop().push("goat");
        goatAdulthoodDays = builder.apply("goatAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("goatAdulthoodDays", 96, 0, Integer.MAX_VALUE);
        goatChildCount = builder.apply("goatChildCount").comment("Max number of children born").defineInRange("goatChildCount", 2, 0, 100);
        goatEatsRottenFood = builder.apply("goatEatsRottenFood").comment("Does the animal eat rotten food?").define("goatEatsRottenFood", true);
        goatFamiliarityCap = builder.apply("goatFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("goatFamiliarityCap", 0.35, 0, 1);
        goatUses = builder.apply("goatUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("goatUses", 60, 0, Integer.MAX_VALUE);
        goatGestationDays = builder.apply("goatGestationDays").comment("Length of pregnancy in days").defineInRange("goatGestationDays", 32, 0, Integer.MAX_VALUE);
        goatMilkTicks = builder.apply("goatMilkTicks").comment("Ticks until milk is ready").defineInRange("goatMilkTicks", 72000, 0, Integer.MAX_VALUE);
        goatMinMilkFamiliarity = builder.apply("goatMinMilkFamiliarity").comment("Minimum familiarity [0-1] needed to milk. Set above 1 to disable milking.").defineInRange("goatMinMilkFamiliarity", 0.15d, 0, Float.MAX_VALUE);

        innerBuilder.pop().push("yak");
        yakAdulthoodDays = builder.apply("yakAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("yakAdulthoodDays", 180, 0, Integer.MAX_VALUE);
        yakChildCount = builder.apply("yakChildCount").comment("Max number of children born").defineInRange("yakChildCount", 1, 0, 100);
        yakEatsRottenFood = builder.apply("yakEatsRottenFood").comment("Does the animal eat rotten food?").define("yakEatsRottenFood", false);
        yakFamiliarityCap = builder.apply("yakFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("yakFamiliarityCap", 0.35, 0, 1);
        yakUses = builder.apply("yakUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("yakUses", 230, 0, Integer.MAX_VALUE);
        yakGestationDays = builder.apply("yakGestationDays").comment("Length of pregnancy in days").defineInRange("yakGestationDays", 64, 0, Integer.MAX_VALUE);
        yakMilkTicks = builder.apply("yakMilkTicks").comment("Ticks until milk is ready").defineInRange("yakMilkTicks", 23500, 0, Integer.MAX_VALUE);
        yakMinMilkFamiliarity = builder.apply("yakMinMilkFamiliarity").comment("Minimum familiarity [0-1] needed to milk. Set above 1 to disable milking.").defineInRange("yakMinMilkFamiliarity", 0.15d, 0, Float.MAX_VALUE);

        innerBuilder.pop().push("alpaca");
        alpacaAdulthoodDays = builder.apply("alpacaAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("alpacaAdulthoodDays", 88, 0, Integer.MAX_VALUE);
        alpacaChildCount = builder.apply("alpacaChildCount").comment("Max number of children born").defineInRange("alpacaChildCount", 1, 0, 100);
        alpacaEatsRottenFood = builder.apply("alpacaEatsRottenFood").comment("Does the animal eat rotten food?").define("alpacaEatsRottenFood", false);
        alpacaFamiliarityCap = builder.apply("alpacaFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("alpacaFamiliarityCap", 0.35, 0, 1);
        alpacaUses = builder.apply("alpacaUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("alpacaUses", 100, 0, Integer.MAX_VALUE);
        alpacaGestationDays = builder.apply("alpacaGestationDays").comment("Length of pregnancy in days").defineInRange("alpacaGestationDays", 64, 0, Integer.MAX_VALUE);
        alpacaWoolTicks = builder.apply("alpacaWoolTicks").comment("Ticks until wool is ready").defineInRange("alpacaWoolTicks", 120000, 0, Integer.MAX_VALUE);
        alpacaMinWoolFamiliarity = builder.apply("alpacaMinWoolFamiliarity").comment("Minimum familiarity [0-1] needed to grow wool. Set above 1 to disable shearing.").defineInRange("alpacaMinMilkFamiliarity", 0.15d, 0, Float.MAX_VALUE);

        innerBuilder.pop().push("sheep");
        sheepAdulthoodDays = builder.apply("sheepAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("sheepAdulthoodDays", 56, 0, Integer.MAX_VALUE);
        sheepChildCount = builder.apply("sheepChildCount").comment("Max number of children born").defineInRange("sheepChildCount", 2, 0, 100);
        sheepEatsRottenFood = builder.apply("sheepEatsRottenFood").comment("Does the animal eat rotten food?").define("sheepEatsRottenFood", false);
        sheepFamiliarityCap = builder.apply("sheepFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("sheepFamiliarityCap", 0.35, 0, 1);
        sheepUses = builder.apply("sheepUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("sheepUses", 60, 0, Integer.MAX_VALUE);
        sheepGestationDays = builder.apply("sheepGestationDays").comment("Length of pregnancy in days").defineInRange("sheepGestationDays", 32, 0, Integer.MAX_VALUE);
        sheepWoolTicks = builder.apply("sheepWoolTicks").comment("Ticks until wool is ready").defineInRange("sheepWoolTicks", 168000, 0, Integer.MAX_VALUE);
        sheepMinWoolFamiliarity = builder.apply("sheepMinWoolFamiliarity").comment("Minimum familiarity [0-1] needed to grow wool. Set above 1 to disable shearing.").defineInRange("sheepMinMilkFamiliarity", 0.15d, 0, Float.MAX_VALUE);

        innerBuilder.pop().push("muskOx");
        muskOxAdulthoodDays = builder.apply("muskOxAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("muskOxAdulthoodDays", 168, 0, Integer.MAX_VALUE);
        muskOxChildCount = builder.apply("muskOxChildCount").comment("Max number of children born").defineInRange("muskOxChildCount", 1, 0, 100);
        muskOxEatsRottenFood = builder.apply("muskOxEatsRottenFood").comment("Does the animal eat rotten food?").define("muskOxEatsRottenFood", false);
        muskOxFamiliarityCap = builder.apply("muskOxFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("muskOxFamiliarityCap", 0.35, 0, 1);
        muskOxUses = builder.apply("muskOxUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("muskOxUses", 160, 0, Integer.MAX_VALUE);
        muskOxGestationDays = builder.apply("muskOxGestationDays").comment("Length of pregnancy in days").defineInRange("muskOxGestationDays", 64, 0, Integer.MAX_VALUE);
        muskOxWoolTicks = builder.apply("muskOxWoolTicks").comment("Ticks until wool is ready").defineInRange("muskOxWoolTicks", 96000, 0, Integer.MAX_VALUE);
        muskOxMinWoolFamiliarity = builder.apply("muskOxMinWoolFamiliarity").comment("Minimum familiarity [0-1] needed to grow wool. Set above 1 to disable shearing.").defineInRange("muskOxMinMilkFamiliarity", 0.15d, 0, Float.MAX_VALUE);

        innerBuilder.pop().push("chicken");
        chickenAdulthoodDays = builder.apply("chickenAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("chickenAdulthoodDays", 24, 0, Integer.MAX_VALUE);
        chickenEatsRottenFood = builder.apply("chickenEatsRottenFood").comment("Does the animal eat rotten food?").define("chickenEatsRottenFood", true);
        chickenFamiliarityCap = builder.apply("chickenFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("chickenFamiliarityCap", 0.35, 0, 1);
        chickenUses = builder.apply("chickenUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("chickenUses", 64, 0, Integer.MAX_VALUE);
        chickenEggTicks = builder.apply("chickenEggTicks").comment("Ticks until an egg is ready for laying").defineInRange("chickenEggTicks", 30000, 0, Integer.MAX_VALUE);
        chickenMinEggFamiliarity = builder.apply("chickenMinEggFamiliarity").comment("Minimum familiarity [0-1] needed to lay eggs. Set above 1 to disable egg laying.").defineInRange("chickenMinEggFamiliarity", 0.15d, 0, Float.MAX_VALUE);
        chickenHatchDays = builder.apply("chickenHatchDays").comment("Ticks until egg is ready to hatch").defineInRange("chickenHatchDays", 8, 0, Integer.MAX_VALUE);

        innerBuilder.pop().push("duck");
        duckAdulthoodDays = builder.apply("duckAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("duckAdulthoodDays", 32, 0, Integer.MAX_VALUE);
        duckEatsRottenFood = builder.apply("duckEatsRottenFood").comment("Does the animal eat rotten food?").define("duckEatsRottenFood", false);
        duckFamiliarityCap = builder.apply("duckFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("duckFamiliarityCap", 0.35, 0, 1);
        duckUses = builder.apply("duckUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("duckUses", 72, 0, Integer.MAX_VALUE);
        duckEggTicks = builder.apply("duckEggTicks").comment("Ticks until an egg is ready for laying").defineInRange("duckEggTicks", 32000, 0, Integer.MAX_VALUE);
        duckMinEggFamiliarity = builder.apply("duckMinEggFamiliarity").comment("Minimum familiarity [0-1] needed to lay eggs. Set above 1 to disable egg laying.").defineInRange("duckMinEggFamiliarity", 0.15d, 0, Float.MAX_VALUE);
        duckHatchDays = builder.apply("duckHatchDays").comment("Ticks until egg is ready to hatch").defineInRange("duckHatchDays", 8, 0, Integer.MAX_VALUE);

        innerBuilder.pop().push("quail");
        quailAdulthoodDays = builder.apply("quailAdulthoodDays").comment("Days until animal reaches adulthood").defineInRange("quailAdulthoodDays", 22, 0, Integer.MAX_VALUE);
        quailEatsRottenFood = builder.apply("quailEatsRottenFood").comment("Does the animal eat rotten food?").define("quailEatsRottenFood", true);
        quailFamiliarityCap = builder.apply("quailFamiliarityCap").comment("Max familiarity an adult may reach").defineInRange("quailFamiliarityCap", 0.35, 0, 1);
        quailUses = builder.apply("quailUses").comment("Uses before animal becomes old and can no longer be used").defineInRange("quailUses", 48, 0, Integer.MAX_VALUE);
        quailEggTicks = builder.apply("quailEggTicks").comment("Ticks until an egg is ready for laying").defineInRange("quailEggTicks", 28000, 0, Integer.MAX_VALUE);
        quailMinEggFamiliarity = builder.apply("quailMinEggFamiliarity").comment("Minimum familiarity [0-1] needed to lay eggs. Set above 1 to disable egg laying.").defineInRange("quailMinEggFamiliarity", 0.15d, 0, Float.MAX_VALUE);
        quailHatchDays = builder.apply("quailHatchDays").comment("Ticks until egg is ready to hatch").defineInRange("quailHatchDays", 8, 0, Integer.MAX_VALUE);

        innerBuilder.pop(3);

        farmlandMakesTheBestRaceTracks = builder.apply("farmlandMakesTheBestRaceTracks").define("farmlandMakesTheBestRaceTracks", false);
    }
}