/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

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
    // Player
    public final ForgeConfigSpec.BooleanValue enableVanillaNaturalRegeneration;
    // Climate
    public final ForgeConfigSpec.IntValue temperatureScale;
    public final ForgeConfigSpec.IntValue rainfallScale;
    // Blocks - Farmland
    public final ForgeConfigSpec.BooleanValue enableFarmlandCreation;
    // Blocks - Grass Path
    public final ForgeConfigSpec.BooleanValue enableGrassPathCreation;
    // Blocks - Snow
    public final ForgeConfigSpec.BooleanValue enableSnowAffectedByTemperature;
    public final ForgeConfigSpec.BooleanValue enableSnowSlowEntities;
    // Blocks - Leaves
    public final ForgeConfigSpec.BooleanValue enableLeavesSlowEntities;
    // Blocks - Plants
    public final ForgeConfigSpec.DoubleValue plantGrowthChance;
    // Blocks - Cobblestone
    public final ForgeConfigSpec.BooleanValue enableMossyRockSpreading;
    public final ForgeConfigSpec.IntValue mossyRockSpreadRate;
    // Mechanics - Heat
    public final ForgeConfigSpec.DoubleValue itemHeatingModifier;
    // Mechanics - Collapses
    public final ForgeConfigSpec.BooleanValue enableBlockCollapsing;
    public final ForgeConfigSpec.BooleanValue enableExplosionCollapsing;
    public final ForgeConfigSpec.BooleanValue enableBlockLandslides;
    public final ForgeConfigSpec.DoubleValue collapseTriggerChance;
    public final ForgeConfigSpec.DoubleValue collapsePropagateChance;
    public final ForgeConfigSpec.DoubleValue collapseExplosionPropagateChance;
    public final ForgeConfigSpec.IntValue collapseMinRadius;
    public final ForgeConfigSpec.IntValue collapseRadiusVariance;


    ServerConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        enableNetherPortals = builder.apply("enableNetherPortals").comment("Enable nether portal creation").define("enableNetherPortals", false);

        innerBuilder.pop().push("player");

        enableVanillaNaturalRegeneration = builder.apply("enableVanillaNaturalRegeneration").comment("Enables the vanilla `naturalRegeneration` gamerule, which regenerates your health much quicker than TFC does.").define("enableVanillaNaturalRegeneration", false);

        innerBuilder.pop().push("climate");

        temperatureScale = builder.apply("temperatureScale").comment("This is the distance in blocks to the first peak (Either cold or hot) temperature zone, in the north-south direction.").defineInRange("temperatureScale", 20_000, 1_000, 1_000_000);
        rainfallScale = builder.apply("rainfallScale").comment("This is the distance in blocks to the first peak (Either wet or dry) rainfall zone, in the east-west direction").defineInRange("rainfallScale", 20_000, 1_000, 1_000_000);

        innerBuilder.pop().push("blocks").push("farmland");

        enableFarmlandCreation = builder.apply("enableFarmlandCreation").comment("If TFC soil blocks are able to be created into farmland").define("enableFarmlandCreation", true);

        innerBuilder.pop().push("grassPath");

        enableGrassPathCreation = builder.apply("enableGrassPathCreation").comment("If TFC soil blocks are able to be created into (grass) path blocks.").define("enableGrassPathCreation", true);

        innerBuilder.pop().push("snow");

        enableSnowAffectedByTemperature = builder.apply("enableSnowAffectedByTemperature").comment("If snow will melt in warm temperatures on random ticks").define("enableSnowAffectedByTemperature", true);
        enableSnowSlowEntities = builder.apply("enableSnowSlowEntities").comment("[Requires MC Restart] If snow will slow players that move on top of it similar to soul sand or honey.").define("enableSnowSlowEntities", true);

        innerBuilder.pop().push("plants");

        plantGrowthChance = builder.apply("plantGrowthChance").comment("Chance for a plant to grow each random tick, does not include crops. Lower = slower growth. Set to 0 to disable random plant growth.").defineInRange("plantGrowthChance", 0.05, 0, 1);

        innerBuilder.pop().push("leaves");

        enableLeavesSlowEntities = builder.apply("enableLeavesSlowEntities").comment("If leaves will slow entities passing through them and reduce fall damage.").define("enableLeavesSlowEntities", true);

        innerBuilder.pop().push("cobblestone");

        enableMossyRockSpreading = builder.apply("enableMossyRockSpreading").comment("If mossy rock blocks will spread their moss to nearby rock blocks (bricks and cobble; stairs, slabs and walls thereof).").define("enableMossyRockSpreading", true);
        mossyRockSpreadRate = builder.apply("mossyRockSpreadRate").comment("The rate at which rock blocks will accumulate moss. Higher value = slower.").defineInRange("mossyRockSpreadRate", 20, 1, Integer.MAX_VALUE);

        innerBuilder.pop().pop().push("mechanics").push("heat");

        itemHeatingModifier = builder.apply("itemHeatingModifier").comment("A multiplier for how fast items heat and cool. Higher = faster.").defineInRange("itemHeatingModifier", 1, 0, Double.MAX_VALUE);

        innerBuilder.pop().push("collapses");

        enableBlockCollapsing = builder.apply("enableBlockCollapsing").comment("Enable rock collapsing when mining raw stone blocks").define("enableBlockCollapsing", true);
        enableExplosionCollapsing = builder.apply("enableExplosionCollapsing").comment("Enable explosions causing immediate collapses.").define("enableExplosionCollapsing", true);
        enableBlockLandslides = builder.apply("enableBlockLandslides").comment("Enable land slides (gravity affected blocks) when placing blocks or on block updates.").define("enableBlockLandslides", true);

        collapseTriggerChance = builder.apply("collapseTriggerChance").comment("Chance for a collapse to be triggered by mining a block.").defineInRange("collapseTriggerChance", 0.1, 0, 1);
        collapsePropagateChance = builder.apply("collapsePropagateChance").comment("Chance for a block fo fall from mining collapse. Higher = mor likely.").defineInRange("collapsePropagateChance", 0.55, 0, 1);
        collapseExplosionPropagateChance = builder.apply("collapseExplosionPropagateChance").comment("Chance for a block to fall from an explosion triggered collapse. Higher = mor likely.").defineInRange("collapseExplosionPropagateChance", 0.3, 0, 1);
        collapseMinRadius = builder.apply("collapseMinRadius").comment("Minimum radius for a collapse").defineInRange("collapseMinRadius", 3, 1, 32);
        collapseRadiusVariance = builder.apply("collapseRadiusVariance").comment("Variance of the radius of a collapse. Total size is in [minRadius, minRadius + radiusVariance]").defineInRange("collapseRadiusVariance", 16, 1, 32);

        innerBuilder.pop().pop();
    }
}