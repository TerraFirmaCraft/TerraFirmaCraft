/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.registries;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import net.dries007.tfc.api.types.Rock;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.api.registries.TFCRegistryNames.ROCK;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TFCRegistryHandler
{
    private static final Map<ResourceLocation, IForgeRegistry<?>> preBlockRegistries = new LinkedHashMap<>(); // Needs to respect insertion order
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry event)
    {
        LOGGER.info("Creating New Registries");
        // Pre Block registries (dirty hack)

        newRegistry(ROCK, Rock.class, true);
        //newRegistry(METAL, Metal.class, true);// Required before: ORE, ALLOY_RECIPE, WELDING_RECIPE
        //newRegistry(ORE, Ore.class, true);
        //newRegistry(TREE, Tree.class, true);
        //newRegistry(PLANT, Plant.class, true);

        // Normal registries
        //newRegistry(ALLOY_RECIPE, AlloyRecipe.class, false);
        //newRegistry(KNAPPING_RECIPE, KnappingRecipe.class, false);
        //newRegistry(ANVIL_RECIPE, AnvilRecipe.class, false);
        //newRegistry(WELDING_RECIPE, WeldingRecipe.class, false);
        //newRegistry(HEAT_RECIPE, HeatRecipe.class, false);
        //newRegistry(BARREL_RECIPE, BarrelRecipe.class, false);
        //newRegistry(LOOM_RECIPE, LoomRecipe.class, false);
        //newRegistry(QUERN_RECIPE, QuernRecipe.class, false);
    }

    /**
     * Danger: dirty hack.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterBlock(RegistryEvent.Register<Block> event)
    {
        preBlockRegistries.forEach((e, r) -> FMLJavaModLoadingContext.get().getModEventBus().post(new TFCRegistryHandler.RegisterPreBlock<>(e, r)));
    }

    private static <T extends IForgeRegistryEntry<T>> void newRegistry(ResourceLocation name, Class<T> tClass, boolean isPreBlockRegistry)
    {
        IForgeRegistry<T> reg = new RegistryBuilder<T>().setName(name).allowModification().setType(tClass).create();
        if (isPreBlockRegistry)
        {
            preBlockRegistries.put(name, reg);
        }
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
    public static class RegisterPreBlock<T extends IForgeRegistryEntry<T>> extends GenericEvent<T>
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
