/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.util.Cache;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Server Config
 * - synced, stored per world, can be shipped per instance with default configs
 * - use for the majority of config options, or any that need to be present on both sides
 */
public class ServerConfig extends CachingConfig
{
    // General
    public final Cache.Boolean enableNetherPortals;
    public final Cache.Boolean enableFireArrowSpreading;
    public final Cache.Double fireStarterChance;
    // Player
    public final Cache.Boolean enableVanillaNaturalRegeneration;
    public final Cache.Boolean enableForcedTFCGameRules;
    // Climate
    public final Cache.Int temperatureScale;
    public final Cache.Int rainfallScale;
    // Blocks - Farmland
    public final Cache.Boolean enableFarmlandCreation;
    // Blocks - Grass Path
    public final Cache.Boolean enableGrassPathCreation;
    // Blocks - Snow
    public final Cache.Boolean enableSnowAffectedByTemperature;
    public final Cache.Boolean enableSnowSlowEntities;
    // Blocks - Ice
    public final Cache.Boolean enableIceAffectedByTemperature;
    // Blocks - Leaves
    public final Cache.Boolean enableLeavesSlowEntities;
    // Blocks - Plants
    public final Cache.Double plantGrowthChance;
    // Blocks - Cobblestone
    public final Cache.Boolean enableMossyRockSpreading;
    public final Cache.Int mossyRockSpreadRate;
    // Blocks - Torch
    public final Cache.Int torchTicks;
    // Blocks - Charcoal Pit
    public final Cache.Int charcoalTicks;
    // Blocks - Pit Kiln
    public final Cache.Int pitKilnTicks;
    // Mechanics - Heat
    public final Cache.Double itemHeatingModifier;
    public final Cache.Int rainTicks;
    // Mechanics - Collapses
    public final Cache.Boolean enableBlockCollapsing;
    public final Cache.Boolean enableExplosionCollapsing;
    public final Cache.Boolean enableBlockLandslides;
    public final Cache.Double collapseTriggerChance;
    public final Cache.Double collapsePropagateChance;
    public final Cache.Double collapseExplosionPropagateChance;
    public final Cache.Int collapseMinRadius;
    public final Cache.Int collapseRadiusVariance;


    ServerConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        enableNetherPortals = wrap(builder.apply("enableNetherPortals").comment("Enable nether portal creation").define("enableNetherPortals", false));
        enableFireArrowSpreading = wrap(builder.apply("enableFireArrowSpreading").comment("Enable fire arrows and fireballs to spread fire and light blocks.").define("enableFireArrowSpreading", true));
        fireStarterChance = wrap(builder.apply("fireStarterChance").comment("Base probability for a firestarter to start a fire. May change based on circumstances").defineInRange("fireStarterChance", 0.5, 0, 1));

        innerBuilder.pop().push("player");

        enableVanillaNaturalRegeneration = wrap(builder.apply("enableVanillaNaturalRegeneration").comment("Enables the vanilla `naturalRegeneration` gamerule, which regenerates your health much quicker than TFC does.").define("enableVanillaNaturalRegeneration", false));

        enableForcedTFCGameRules = wrap(builder.apply("enableForcedTFCGameRules").comment(
            "Forces a number of game rules to specific values.",
            "  naturalRegeneration = false (Health regen is much slower and not tied to extra saturation)",
            "  doInsomnia = false (No phantoms)",
            "  doTraderSpawning = false (No wandering traders)",
            "  doPatrolSpawning = false (No pillager patrols)"
        ).define("enableForcedTFCGameRules", false));

        innerBuilder.pop().push("climate");

        temperatureScale = wrap(builder.apply("temperatureScale").comment("This is the distance in blocks to the first peak (Either cold or hot) temperature zone, in the north-south direction.").defineInRange("temperatureScale", 20_000, 1_000, 1_000_000));
        rainfallScale = wrap(builder.apply("rainfallScale").comment("This is the distance in blocks to the first peak (Either wet or dry) rainfall zone, in the east-west direction").defineInRange("rainfallScale", 20_000, 1_000, 1_000_000));

        innerBuilder.pop().push("blocks").push("farmland");

        enableFarmlandCreation = wrap(builder.apply("enableFarmlandCreation").comment("If TFC soil blocks are able to be created into farmland").define("enableFarmlandCreation", true));

        innerBuilder.pop().push("grassPath");

        enableGrassPathCreation = wrap(builder.apply("enableGrassPathCreation").comment("If TFC soil blocks are able to be created into (grass) path blocks.").define("enableGrassPathCreation", true));

        innerBuilder.pop().push("snow");

        enableSnowAffectedByTemperature = wrap(builder.apply("enableSnowAffectedByTemperature").comment("If snow will melt in warm temperatures on random ticks").define("enableSnowAffectedByTemperature", true));
        enableSnowSlowEntities = wrap(builder.apply("enableSnowSlowEntities").comment("[Requires MC Restart] If snow will slow players that move on top of it similar to soul sand or honey.").define("enableSnowSlowEntities", true));

        innerBuilder.pop().push("ice");

        enableIceAffectedByTemperature = wrap(builder.apply("enableIceAffectedByTemperature").comment("If ice will melt in warm temperatures on random ticks").define("enableIceAffectedByTemperature", true));

        innerBuilder.pop().push("plants");

        plantGrowthChance = wrap(builder.apply("plantGrowthChance").comment("Chance for a plant to grow each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant growth.").defineInRange("plantGrowthChance", 0.05, 0, 1));

        innerBuilder.pop().push("leaves");

        enableLeavesSlowEntities = wrap(builder.apply("enableLeavesSlowEntities").comment("If leaves will slow entities passing through them and reduce fall damage.").define("enableLeavesSlowEntities", true));

        innerBuilder.pop().push("cobblestone");

        enableMossyRockSpreading = wrap(builder.apply("enableMossyRockSpreading").comment("If mossy rock blocks will spread their moss to nearby rock blocks (bricks and cobble; stairs, slabs and walls thereof).").define("enableMossyRockSpreading", true));
        mossyRockSpreadRate = wrap(builder.apply("mossyRockSpreadRate").comment("The rate at which rock blocks will accumulate moss. Higher value = slower.").defineInRange("mossyRockSpreadRate", 20, 1, Integer.MAX_VALUE));

        innerBuilder.pop().push("torch");

        torchTicks = wrap(builder.apply("torchTicks").comment("Number of ticks required for a torch to burn out (72000 = 1 in game hour = 50 seconds), default is 72 hours. Set to -1 to disable torch burnout.").defineInRange("torchTicks", 7200, -1, Integer.MAX_VALUE));

        innerBuilder.pop().push("charcoal");

        charcoalTicks = wrap(builder.apply("charcoalTicks").comment("Number of ticks required for charcoal pit to complete. (1000 = 1 in game hour = 50 seconds), default is 18 hours.").defineInRange("charcoalTicks", 18000, -1, Integer.MAX_VALUE));

        innerBuilder.pop().push("pit_kiln");

        pitKilnTicks = wrap(builder.apply("pitKilnTicks").comment("Number of ticks required for a pit kiln to burn out. (1000 = 1 in game hour = 50 seconds), default is 8 hours.").defineInRange("pitKilnTicks", 8000, 20, Integer.MAX_VALUE));

        innerBuilder.pop().pop().push("mechanics").push("heat");

        itemHeatingModifier = wrap(builder.apply("itemHeatingModifier").comment("A multiplier for how fast items heat and cool. Higher = faster.").defineInRange("itemHeatingModifier", 1, 0, Double.MAX_VALUE));
        rainTicks = wrap(builder.apply("rainTicks").comment("Number of burning ticks that is removed when the fire pit is on rain (random ticks). Makes fuel burn faster.").defineInRange("rainTicks", 1000, 0, Integer.MAX_VALUE));

        innerBuilder.pop().push("collapses");

        enableBlockCollapsing = wrap(builder.apply("enableBlockCollapsing").comment("Enable rock collapsing when mining raw stone blocks").define("enableBlockCollapsing", true));
        enableExplosionCollapsing = wrap(builder.apply("enableExplosionCollapsing").comment("Enable explosions causing immediate collapses.").define("enableExplosionCollapsing", true));
        enableBlockLandslides = wrap(builder.apply("enableBlockLandslides").comment("Enable land slides (gravity affected blocks) when placing blocks or on block updates.").define("enableBlockLandslides", true));

        collapseTriggerChance = wrap(builder.apply("collapseTriggerChance").comment("Chance for a collapse to be triggered by mining a block.").defineInRange("collapseTriggerChance", 0.1, 0, 1));
        collapsePropagateChance = wrap(builder.apply("collapsePropagateChance").comment("Chance for a block fo fall from mining collapse. Higher = mor likely.").defineInRange("collapsePropagateChance", 0.55, 0, 1));
        collapseExplosionPropagateChance = wrap(builder.apply("collapseExplosionPropagateChance").comment("Chance for a block to fall from an explosion triggered collapse. Higher = mor likely.").defineInRange("collapseExplosionPropagateChance", 0.3, 0, 1));
        collapseMinRadius = wrap(builder.apply("collapseMinRadius").comment("Minimum radius for a collapse").defineInRange("collapseMinRadius", 3, 1, 32));
        collapseRadiusVariance = wrap(builder.apply("collapseRadiusVariance").comment("Variance of the radius of a collapse. Total size is in [minRadius, minRadius + radiusVariance]").defineInRange("collapseRadiusVariance", 16, 1, 32));

        innerBuilder.pop().pop();
    }
}