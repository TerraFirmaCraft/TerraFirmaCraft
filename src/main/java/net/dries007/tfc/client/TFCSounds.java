/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final RegistryObject<SoundEvent> ROCK_SLIDE_LONG = create("rock_slide_long");
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_SHORT = create("rock_slide_short");
    public static final RegistryObject<SoundEvent> DIRT_SLIDE_SHORT = create("dirt_slide_short");

    public static final RegistryObject<SoundEvent> THATCH_HIT = create("thatch_hit");
    public static final RegistryObject<SoundEvent> THATCH_PLACE = create("thatch_place");
    public static final RegistryObject<SoundEvent> THATCH_STEP = create("thatch_step");
    public static final RegistryObject<SoundEvent> THATCH_FALL = create("thatch_fall");
    public static final RegistryObject<SoundEvent> THATCH_BREAK = create("thatch_break");

    public static final RegistryObject<SoundEvent> PEAT_HIT = create("peat_hit");
    public static final RegistryObject<SoundEvent> PEAT_PLACE = create("peat_place");
    public static final RegistryObject<SoundEvent> PEAT_STEP = create("peat_step");
    public static final RegistryObject<SoundEvent> PEAT_FALL = create("peat_fall");
    public static final RegistryObject<SoundEvent> PEAT_BREAK = create("peat_break");

    public static final RegistryObject<SoundEvent> THIN_HIT = create("thin_hit");
    public static final RegistryObject<SoundEvent> THIN_PLACE = create("thin_place");
    public static final RegistryObject<SoundEvent> THIN_STEP = create("thin_step");
    public static final RegistryObject<SoundEvent> THIN_FALL = create("thin_fall");
    public static final RegistryObject<SoundEvent> THIN_BREAK = create("thin_break");

    public static final ForgeSoundType THATCH = new ForgeSoundType(1.0f, 1.0f, THATCH_BREAK, THATCH_STEP, THATCH_PLACE, THATCH_HIT, THATCH_FALL);
    public static final ForgeSoundType PEAT = new ForgeSoundType(1.0f, 1.0f, PEAT_BREAK, PEAT_STEP, PEAT_PLACE, PEAT_HIT, PEAT_FALL);
    public static final ForgeSoundType THIN = new ForgeSoundType(1.0f, 1.0f, THIN_BREAK, THIN_STEP, THIN_PLACE, THIN_HIT, THIN_FALL);


    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> new SoundEvent(Helpers.identifier(name)));
    }
}