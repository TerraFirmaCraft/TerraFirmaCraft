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
public class TFCRegistryNames
{
    public static final ResourceLocation ROCK_TYPE = new ResourceLocation(MOD_ID, "rock_type");
    public static final ResourceLocation ROCK = new ResourceLocation(MOD_ID, "rock");
    public static final ResourceLocation ORE = new ResourceLocation(MOD_ID, "ore");
    public static final ResourceLocation TREE = new ResourceLocation(MOD_ID, "tree");
}
