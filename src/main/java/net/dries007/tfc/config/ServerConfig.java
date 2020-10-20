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
    // Collapses
    public final ForgeConfigSpec.BooleanValue enableBlockCollapsing;
    public final ForgeConfigSpec.BooleanValue enableExplosionCollapsing;
    public final ForgeConfigSpec.BooleanValue enableBlockLandslides;
    public final ForgeConfigSpec.DoubleValue collapseTriggerChance;
    public final ForgeConfigSpec.DoubleValue collapsePropagateChance;
    public final ForgeConfigSpec.DoubleValue collapseExplosionPropagateChance;
    public final ForgeConfigSpec.IntValue collapseMinRadius;
    public final ForgeConfigSpec.IntValue collapseRadiusVariance;
    // Player
    public final ForgeConfigSpec.BooleanValue enableVanillaNaturalRegeneration;
    // Climate
    public final ForgeConfigSpec.IntValue temperatureScale;
    public final ForgeConfigSpec.IntValue rainfallScale;
    // Blocks
    public final ForgeConfigSpec.BooleanValue enableFarmlandCreation;
    public final ForgeConfigSpec.BooleanValue enableGrassPathCreation;
    // Blocks - Snow
    public final ForgeConfigSpec.BooleanValue enableSnowAffectedByTemperature;
    public final ForgeConfigSpec.BooleanValue enableSnowMovementModifier;
    // Blocks - Leaves
    public final ForgeConfigSpec.BooleanValue leavesDecayVanilla;
    public final ForgeConfigSpec.BooleanValue leavesSolidBlocks;
    public final ForgeConfigSpec.DoubleValue leavesMovementModifier;


    ServerConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        enableNetherPortals = builder.apply("enableNetherPortals").comment("Enable nether portal creation").define("enableNetherPortals", false);

        innerBuilder.pop().push("collapses");

        enableBlockCollapsing = builder.apply("enableBlockCollapsing").comment("Enable rock collapsing when mining raw stone blocks").define("enableBlockCollapsing", true);
        enableExplosionCollapsing = builder.apply("enableExplosionCollapsing").comment("Enable explosions causing immediate collapses.").define("enableExplosionCollapsing", true);
        enableBlockLandslides = builder.apply("enableBlockLandslides").comment("Enable land slides (gravity affected blocks) when placing blocks or on block updates.").define("enableBlockLandslides", true);

        collapseTriggerChance = builder.apply("collapseTriggerChance").comment("Chance for a collapse to be triggered by mining a block.").defineInRange("collapseTriggerChance", 0.1, 0, 1);
        collapsePropagateChance = builder.apply("collapsePropagateChance").comment("Chance for a block fo fall from mining collapse. Higher = mor likely.").defineInRange("collapsePropagateChance", 0.55, 0, 1);
        collapseExplosionPropagateChance = builder.apply("collapseExplosionPropagateChance").comment("Chance for a block to fall from an explosion triggered collapse. Higher = mor likely.").defineInRange("collapseExplosionPropagateChance", 0.3, 0, 1);
        collapseMinRadius = builder.apply("collapseMinRadius").comment("Minimum radius for a collapse").defineInRange("collapseMinRadius", 3, 1, 32);
        collapseRadiusVariance = builder.apply("collapseRadiusVariance").comment("Variance of the radius of a collapse. Total size is in [minRadius, minRadius + radiusVariance]").defineInRange("collapseRadiusVariance", 16, 1, 32);

        innerBuilder.pop().push("player");

        enableVanillaNaturalRegeneration = builder.apply("enableVanillaNaturalRegeneration").comment("Enables the vanilla `naturalRegeneration` gamerule, which regenerates your health much quicker than TFC does.").define("enableVanillaNaturalRegeneration", false);

        innerBuilder.pop().push("climate");

        temperatureScale = builder.apply("temperatureScale").comment("This is the distance in blocks to the first peak (Either cold or hot) temperature zone, in the north-south direction.").defineInRange("temperatureScale", 20_000, 1_000, 1_000_000);
        rainfallScale = builder.apply("rainfallScale").comment("This is the distance in blocks to the first peak (Either wet or dry) rainfall zone, in the east-west direction").defineInRange("rainfallScale", 20_000, 1_000, 1_000_000);

        innerBuilder.pop().push("blocks");

        enableFarmlandCreation = builder.apply("enableFarmlandCreation").comment("If TFC soil blocks are able to be created into farmland").define("enableFarmlandCreation", true);
        enableGrassPathCreation = builder.apply("enableGrassPathCreation").comment("If TFC soil blocks are able to be created into (grass) path blocks.").define("enableGrassPathCreation", true);

        innerBuilder.push("snow");

        enableSnowAffectedByTemperature = builder.apply("enableSnowAffectedByTemperature").comment("If snow will melt in warm temperatures on random ticks").define("enableSnowAffectedByTemperature", true);
        enableSnowMovementModifier = builder.apply("enableSnowMovementModifier").comment("[Requires MC Restart] If snow will slow players that move on top of it similar to soul sand or honey").define("enableSnowMovementModifier", true);

        innerBuilder.pop().push("leaves");

        leavesDecayVanilla = builder.apply("leavesDecayVanilla").comment("Should leaves decay over time like vanilla?").define("leavesDecayVanilla", false);
        leavesSolidBlocks = builder.apply("leavesSolidBlocks").comment("Are leaves solid blocks and non-passable?").define("leavesSolidBlocks", false);
        leavesMovementModifier = builder.apply("leavesMovementModifier").comment("How much to leaves slow entities passing through them?").defineInRange("leavesMovementModifier", 0.8, 0, 1);

        innerBuilder.pop();
    }
}