/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final RegistryObject<SoundEvent> ROCK_SLIDE_LONG = create("rock_slide_long");
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_SHORT = create("rock_slide_short");
    public static final RegistryObject<SoundEvent> DIRT_SLIDE_SHORT = create("dirt_slide_short");

    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> new SoundEvent(Helpers.identifier(name)));
    }
}
