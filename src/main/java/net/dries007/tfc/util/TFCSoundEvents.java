/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.util.Helpers.getNull;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class TFCSoundEvents
{

    @GameRegistry.ObjectHolder("rock.slide.long")
    public static final SoundEvent ROCK_SLIDE_LONG = getNull();
    @GameRegistry.ObjectHolder("rock.slide.short")
    public static final SoundEvent ROCK_SLIDE_SHORT = getNull();
    @GameRegistry.ObjectHolder("dirt.slide.short")
    public static final SoundEvent DIRT_SLIDE_SHORT = getNull();
    @GameRegistry.ObjectHolder("bellows.blow.air")
    public static final SoundEvent BELLOWS_BLOW_AIR = getNull();
    @GameRegistry.ObjectHolder("quern.stonedrag")
    public static final SoundEvent QUERN_USE = getNull();
    @GameRegistry.ObjectHolder("item.ceramicbreak")
    public static final SoundEvent CERAMIC_BREAK = getNull();
    @GameRegistry.ObjectHolder("anvil.metalimpact")
    public static final SoundEvent ANVIL_IMPACT = getNull();




    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> r = event.getRegistry();
        register(r,"rock.slide.long");
        register(r,"rock.slide.short");
        register(r,"dirt.slide.short");
        register(r,"bellows.blow.air");
        register(r,"quern.stonedrag");
        register(r,"item.ceramicbreak");
        register(r,"anvil.metalimpact");
    }

    private static void register(IForgeRegistry<SoundEvent> r, String name)
    {
        ResourceLocation soundID = new ResourceLocation(MOD_ID, name);
        r.register(new SoundEvent(soundID).setRegistryName(soundID));
    }
}
