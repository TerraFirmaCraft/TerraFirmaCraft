/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.types;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;
import net.minecraftforge.fml.common.eventhandler.IContextSetter;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import static net.dries007.tfc.api.types.TFCRegistries.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class TFCRegistries
{
    static final String MOD_ID = "tfc"; // This is here to avoid a import statement.

    private static final ResourceLocation ROCK_TYPE = new ResourceLocation(MOD_ID, "rock_type");
    private static final ResourceLocation ROCK = new ResourceLocation(MOD_ID, "rock");
    private static final ResourceLocation ORE = new ResourceLocation(MOD_ID, "ore");
    private static final ResourceLocation TREE = new ResourceLocation(MOD_ID, "tree");
    private static final Map<ResourceLocation, IForgeRegistry<?>> preBlockRegistries = new HashMap<>();
    private static IForgeRegistry<RockCategory> rockCategoryRegistry;
    private static IForgeRegistry<Rock> rockRegistry;
    private static IForgeRegistry<Ore> oreRegistry;
    private static IForgeRegistry<Tree> treeRegistry;

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

    static IForgeRegistry<Rock> getRocks()
    {
        return rockRegistry;
    }

    static IForgeRegistry<Ore> getOres()
    {
        return oreRegistry;
    }

    static IForgeRegistry<Tree> getTrees()
    {
        return treeRegistry;
    }

    static IForgeRegistry<RockCategory> getRockCategories()
    {
        return rockCategoryRegistry;
    }

    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> newRegistry(ResourceLocation name, Class<T> tClass, boolean isPreBlockRegistry)
    {
        IForgeRegistry<T> reg = new RegistryBuilder<T>().setName(name).setType(tClass).create();
        if (isPreBlockRegistry) preBlockRegistries.put(name, reg);
        return reg;
    }

    /**
     * Required (for now) because of https://github.com/MinecraftForge/MinecraftForge/issues/4987
     * **Beware, dirty hack.**
     *
     * This even it called inside a HIGHEST priority registry event for the BLOCKS registry.
     * This is used to allow us to expose our list of stone types and other things to addons,
     * so dynamic adding of the appropriate blocks is automatic.
     *
     * This against Forge's policy of "Every mod registers it's own blocks/items"!
     *
     * @param <T>
     */
    public static class RegisterPreBlock<T extends IForgeRegistryEntry<T>> extends GenericEvent<T> implements IContextSetter
    {
        private final IForgeRegistry<T> registry;
        private final ResourceLocation name;

        public RegisterPreBlock(ResourceLocation name, IForgeRegistry<T> registry)
        {
            super(registry.getRegistrySuperType());
            this.name = name;
            this.registry = registry;
        }

        public IForgeRegistry<T> getRegistry()
        {
            return registry;
        }

        public ResourceLocation getName()
        {
            return name;
        }

    }
}
