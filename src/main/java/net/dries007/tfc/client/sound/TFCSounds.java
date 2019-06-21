/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class TFCSounds
{
    public static final SoundEvent QUERN_GRIND = Helpers.getNull();

    @SubscribeEvent
    public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> r = event.getRegistry();

        register(r, "quern_grind");
    }

    private static void register(IForgeRegistry<SoundEvent> registry, String name)
    {
        final ResourceLocation loc = new ResourceLocation(MOD_ID, name);
        registry.register(new SoundEvent(loc).setRegistryName(loc));
    }
}
