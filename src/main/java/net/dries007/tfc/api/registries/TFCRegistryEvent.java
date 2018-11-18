/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;
import net.minecraftforge.fml.common.eventhandler.IContextSetter;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A collection of events classes.
 */
public class TFCRegistryEvent
{
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
