/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.registries;

import net.minecraft.util.ResourceLocation;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

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
}
