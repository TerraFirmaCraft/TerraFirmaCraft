/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final RegistryObject<SoundEvent> ROCK_SLIDE_LONG = create("rock_slide_long");
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_SHORT = create("rock_slide_short");
    public static final RegistryObject<SoundEvent> DIRT_SLIDE_SHORT = create("dirt_slide_short");

    public static final RegistryObject<SoundEvent> THATCH_HIT = create("block.thatch.hit");
    public static final RegistryObject<SoundEvent> THATCH_PLACE = create("block.thatch.place");
    public static final RegistryObject<SoundEvent> THATCH_STEP = create("block.thatch.step");
    public static final RegistryObject<SoundEvent> THATCH_FALL = create("block.thatch.fall");
    public static final RegistryObject<SoundEvent> THATCH_BREAK = create("block.thatch.break");

    public static final RegistryObject<SoundEvent> PEAT_HIT = create("block.peat.hit");
    public static final RegistryObject<SoundEvent> PEAT_PLACE = create("block.peat.place");
    public static final RegistryObject<SoundEvent> PEAT_STEP = create("block.peat.step");
    public static final RegistryObject<SoundEvent> PEAT_FALL = create("block.peat.fall");
    public static final RegistryObject<SoundEvent> PEAT_BREAK = create("block.peat.break");

    public static final RegistryObject<SoundEvent> THIN_HIT = create("block.thin.hit");
    public static final RegistryObject<SoundEvent> THIN_PLACE = create("block.thin.place");
    public static final RegistryObject<SoundEvent> THIN_STEP = create("block.thin.step");
    public static final RegistryObject<SoundEvent> THIN_FALL = create("block.thin.fall");
    public static final RegistryObject<SoundEvent> THIN_BREAK = create("block.thin.break");

    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_HIT = create("block.charcoal.hit");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_PLACE = create("block.charcoal.place");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_STEP = create("block.charcoal.step");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_FALL = create("block.charcoal.fall");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_BREAK = create("block.charcoal.break");

    public static final RegistryObject<SoundEvent> FIRESTARTER = create("firestarter");

    public static final RegistryObject<SoundEvent> QUERN_DRAG = create("quern_drag");
    public static final RegistryObject<SoundEvent> LOOM_WEAVE = create("loom_weave");
    public static final RegistryObject<SoundEvent> TOOL_RACK_PLACE = create("tool_rack_place");

    public static final RegistryObject<SoundEvent> KNAP_STONE = create("knap_stone");
    public static final RegistryObject<SoundEvent> KNAP_CLAY = create("knap_clay");
    public static final RegistryObject<SoundEvent> KNAP_LEATHER = create("knap_leather");

    public static final RegistryObject<SoundEvent> CERAMIC_BREAK = create("ceramic_break");
    public static final RegistryObject<SoundEvent> JUG_BLOW = create("jug_blow");

    public static final RegistryObject<SoundEvent> BELLOWS = create("bellows.blow.air");

    public static final RegistryObject<SoundEvent> PANNING = create("panning");

    public static final ForgeSoundType CHARCOAL = new ForgeSoundType(1.0F, 1.0F, CHARCOAL_PILE_BREAK, CHARCOAL_PILE_STEP, CHARCOAL_PILE_PLACE, CHARCOAL_PILE_HIT, CHARCOAL_PILE_FALL);
    public static final ForgeSoundType THATCH = new ForgeSoundType(1.0f, 1.0f, THATCH_BREAK, THATCH_STEP, THATCH_PLACE, THATCH_HIT, THATCH_FALL);
    public static final ForgeSoundType PEAT = new ForgeSoundType(1.0f, 1.0f, PEAT_BREAK, PEAT_STEP, PEAT_PLACE, PEAT_HIT, PEAT_FALL);
    public static final ForgeSoundType THIN = new ForgeSoundType(1.0f, 1.0f, THIN_BREAK, THIN_STEP, THIN_PLACE, THIN_HIT, THIN_FALL);

    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> new SoundEvent(Helpers.identifier(name)));
    }
}