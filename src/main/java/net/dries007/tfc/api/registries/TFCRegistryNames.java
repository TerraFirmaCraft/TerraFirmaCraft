/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.registries;

import net.minecraft.util.ResourceLocation;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * The names are separate from the instances TFCRegistries so they can be used without loading the class prematurely.
 */
public final class TFCRegistryNames
{
    public static final ResourceLocation ROCK_TYPE = new ResourceLocation(MOD_ID, "rock_type");
    public static final ResourceLocation ROCK = new ResourceLocation(MOD_ID, "rock");
    public static final ResourceLocation ORE = new ResourceLocation(MOD_ID, "ore");
    public static final ResourceLocation TREE = new ResourceLocation(MOD_ID, "tree");
    public static final ResourceLocation METAL = new ResourceLocation(MOD_ID, "metal");
    public static final ResourceLocation PLANT = new ResourceLocation(MOD_ID, "plant");

    public static final ResourceLocation ALLOY_RECIPE = new ResourceLocation(MOD_ID, "alloy_recipe");
    public static final ResourceLocation KNAPPING_RECIPE = new ResourceLocation(MOD_ID, "knapping_recipe");
    public static final ResourceLocation ANVIL_RECIPE = new ResourceLocation(MOD_ID, "anvil_recipe");
    public static final ResourceLocation WELDING_RECIPE = new ResourceLocation(MOD_ID, "welding_recipe");
    public static final ResourceLocation HEAT_RECIPE = new ResourceLocation(MOD_ID, "pit_kiln_recipe");
    public static final ResourceLocation BARREL_RECIPE = new ResourceLocation(MOD_ID, "barrel_recipe");
    public static final ResourceLocation LOOM_RECIPE = new ResourceLocation(MOD_ID, "loom_recipe");
    public static final ResourceLocation QUERN_RECIPE = new ResourceLocation(MOD_ID, "quern_recipe");
    public static final ResourceLocation CHISEL_RECIPE = new ResourceLocation(MOD_ID, "chisel_recipe");
    public static final ResourceLocation BLOOMERY_RECIPE = new ResourceLocation(MOD_ID, "bloomery_recipe");
    public static final ResourceLocation BLAST_FURNACE_RECIPE = new ResourceLocation(MOD_ID, "blast_furnace_recipe");
}
