/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.recipes.WeldingRecipe;
import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.*;

import static net.dries007.tfc.api.registries.TFCRegistryNames.*;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class Registries
{
    private static final Map<ResourceLocation, IForgeRegistry<?>> preBlockRegistries = new LinkedHashMap<>(); // Needs to respect insertion order

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry event)
    {
        // Pre Block registries (dirty hack)
        newRegistry(ROCK_TYPE, RockCategory.class, true); // Required before: ROCK
        newRegistry(ROCK, Rock.class, true);
        newRegistry(METAL, Metal.class, true);// Required before: ORE, ALLOY_RECIPE, WELDING_RECIPE
        newRegistry(ORE, Ore.class, true);
        newRegistry(TREE, Tree.class, true);
        newRegistry(PLANT, Plant.class, true);

        // Normal registries
        newRegistry(ALLOY_RECIPE, AlloyRecipe.class, false);
        newRegistry(KNAPPING_RECIPE, KnappingRecipe.class, false);
        newRegistry(ANVIL_RECIPE, AnvilRecipe.class, false);
        newRegistry(WELDING_RECIPE, WeldingRecipe.class, false);
    }

    /**
     * Danger: dirty hack.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterBlock(RegistryEvent.Register<Block> event)
    {
        preBlockRegistries.forEach((e, r) -> MinecraftForge.EVENT_BUS.post(new TFCRegistryEvent.RegisterPreBlock<>(e, r)));
    }

    private static <T extends IForgeRegistryEntry<T>> void newRegistry(ResourceLocation name, Class<T> tClass, boolean isPreBlockRegistry)
    {
        IForgeRegistry<T> reg = new RegistryBuilder<T>().setName(name).setType(tClass).create();
        if (isPreBlockRegistry)
        {
            preBlockRegistries.put(name, reg);
        }
    }
}
