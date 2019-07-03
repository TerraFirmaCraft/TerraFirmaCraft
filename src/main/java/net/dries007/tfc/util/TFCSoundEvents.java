/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.api.util.TFCConstants;

public class TFCSoundEvents
{
    static Set<SoundEvent> EVENTS = new HashSet();
    public static SoundEvent ROCK_SLIDE_LONG = registerSound("rock.slide.long");
    public static SoundEvent ROCK_SLIDE_SHORT = registerSound("rock.slide.short");
    public static SoundEvent DIRT_SLIDE_SHORT = registerSound("dirt.slide.short");
    public static SoundEvent BELLOWS_BLOW_AIR = registerSound("bellows.blow.air");
    public static SoundEvent QUERN_USE = registerSound("quern.stonedrag");
    public static SoundEvent CERAMIC_BREAK = registerSound("item.ceramicbreak");
    public static SoundEvent ANVIL_IMPACT = registerSound("anvil.metalimpact");

    private static SoundEvent registerSound(String name)
    {
        ResourceLocation location = new ResourceLocation(TFCConstants.MOD_ID, name);
        SoundEvent event = new SoundEvent(location);
        EVENTS.add(event.setRegistryName(location));
        return event;
    }

    public static void init()
    {
        for(SoundEvent event : EVENTS)
            ForgeRegistries.SOUND_EVENTS.register(event);
    }
}
