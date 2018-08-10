/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

import net.dries007.tfc.api.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.types.Tree;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.api.TFCRegistries.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CustomRegistries
{
    private static final Map<ResourceLocation, IForgeRegistry<?>> preBlockRegistries = new HashMap<>();
    private static IForgeRegistry<RockCategory> rockCategoryRegistry;
    private static IForgeRegistry<Rock> rockRegistry;
    private static IForgeRegistry<Ore> oreRegistry;
    private static IForgeRegistry<Tree> treeRegistry;

    @Nonnull
    public static Collection<Tree> getTrees()
    {
        return Collections.unmodifiableCollection(treeRegistry.getValuesCollection());
    }

    @Nullable
    public static Tree getTree(String name)
    {
        return treeRegistry.getValuesCollection().stream().filter(tree -> tree.name.equals(name)).findFirst().orElse(null);
    }

    @Nonnull
    public static Collection<Rock> getRocks()
    {
        return Collections.unmodifiableCollection(rockRegistry.getValuesCollection());
    }

    @Nonnull
    public static Collection<RockCategory> getRockCategories()
    {
        return Collections.unmodifiableCollection(rockCategoryRegistry.getValuesCollection());
    }

    @Nonnull
    public static Collection<Ore> getOres()
    {
        return Collections.unmodifiableCollection(oreRegistry.getValuesCollection());
    }

    @Nullable
    public static Ore getOre(String name)
    {
        return oreRegistry.getValuesCollection().stream().filter(o -> o.name.equals(name)).findFirst().orElse(null);
    }

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry event)
    {
        rockCategoryRegistry = newRegistry(ROCK_TYPE, RockCategory.class, true);
        rockRegistry = newRegistry(ROCK, Rock.class, true);
        oreRegistry = newRegistry(ORE, Ore.class, true);
        treeRegistry = newRegistry(TREE, Tree.class, true);
    }

    /**
     * Danger: dirty hack.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterBlock(RegistryEvent.Register<Block> event)
    {
        preBlockRegistries.forEach((e, r) -> MinecraftForge.EVENT_BUS.post(new TFCRegistries.RegisterPreBlock<>(e, r)));
    }

    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> newRegistry(ResourceLocation name, Class<T> tClass, boolean isPreBlockRegistry)
    {
        IForgeRegistry<T> reg = new RegistryBuilder<T>().setName(name).setType(tClass).create();
        if (isPreBlockRegistry) preBlockRegistries.put(name, reg);
        return reg;
    }
}
