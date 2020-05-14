package net.dries007.tfc.config;

import net.minecraftforge.common.ForgeConfigSpec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Server Config
 * - synced, stored per world, can be shipped per instance with default configs
 * - use for the majority of config options, or any that need to be present on both sides
 */
public class ServerConfig
{
    /* Features */
    public final ForgeConfigSpec.BooleanValue enableBlockCollapsing;
    public final ForgeConfigSpec.BooleanValue enableBlockLandslides;

    /* Balance */
    public final ForgeConfigSpec.DoubleValue collapseTriggerChance;
    public final ForgeConfigSpec.DoubleValue collapsePropagateChance;
    public final ForgeConfigSpec.IntValue collapseMinRadius;
    public final ForgeConfigSpec.IntValue collapseRadiusVariance;

    ServerConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("collapses");

        enableBlockCollapsing = builder.comment("Enable rock collapsing when mining blocks").translation(MOD_ID + ".config.enableBlockCollapsing").define("enableBlockCollapsing", true);
        enableBlockLandslides = builder.comment("Enable land slides (gravity affected blocks) when placing blocks or on block updates.").translation(MOD_ID + ".config.enableBlockLandslides").define("enableBlockLandslides", true);

        collapseTriggerChance = builder.comment("Chance for a collapse to be triggered by mining a block.").translation(MOD_ID + ".config.collapseTriggerChance").defineInRange("collapseTriggerChance", 0.1, 0, 1);
        collapsePropagateChance = builder.comment("Chance that collapsing blocks propagate the collapse. Influenced by distance from epicenter of collapse.").translation(MOD_ID + ".config.collapsePropagateChance").defineInRange("collapsePropagateChance", 0.55, 0, 1);
        collapseMinRadius = builder.comment("Minimum radius for a collapse").translation(MOD_ID + ".config.collapseMinRadius").defineInRange("collapseMinRadius", 3, 1, 32);
        collapseRadiusVariance = builder.comment("Variance of the radius of a collapse. Total size is in [minRadius, minRadius + radiusVariance]").translation(MOD_ID + ".config.collapseRadiusVariance").defineInRange("collapseRadiusVariance", 16, 1, 32);

        builder.pop();
    }
}
