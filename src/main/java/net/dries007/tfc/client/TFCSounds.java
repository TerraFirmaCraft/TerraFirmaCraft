/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.resources.sounds.Sound;
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

    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_BREAK = create("charcoal_break");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_FALL = create("charcoal_fall");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_HIT = create("charcoal_hit");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_PLACE = create("charcoal_place");
    public static final RegistryObject<SoundEvent> CHARCOAL_PILE_STEP = create("charcoal_step");

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

    public static final RegistryObject<SoundEvent> SCRIBING_TABLE = create("scribing_table");
    public static final RegistryObject<SoundEvent> OPEN_VESSEL = create("open_vessel");
    public static final RegistryObject<SoundEvent> CLOSE_VESSEL = create("close_vessel");

    public static final RegistryObject<SoundEvent> WATTLE_DYED = create("wattle_dyed");
    public static final RegistryObject<SoundEvent> WATTLE_DAUBED = create("wattle_daubed");
    public static final RegistryObject<SoundEvent> WATTLE_WOVEN = create("wattle_woven");

    public static final RegistryObject<SoundEvent> ALPACA_HURT = create("animal.alpaca.hurt");
    public static final RegistryObject<SoundEvent> ALPACA_STEP = create("animal.alpaca.step");
    public static final RegistryObject<SoundEvent> ALPACA_AMBIENT = create("animal.alpaca.ambient");
    public static final RegistryObject<SoundEvent> ALPACA_DEATH = create("animal.alpaca.death");

    public static final RegistryObject<SoundEvent> YAK_HURT = create("animal.yak.hurt");
    public static final RegistryObject<SoundEvent> YAK_STEP = create("animal.yak.step");
    public static final RegistryObject<SoundEvent> YAK_AMBIENT = create("animal.yak.ambient");
    public static final RegistryObject<SoundEvent> YAK_DEATH = create("animal.yak.death");

    public static final RegistryObject<SoundEvent> MUSK_OX_HURT = create("animal.musk_ox.hurt");
    public static final RegistryObject<SoundEvent> MUSK_OX_STEP = create("animal.musk_ox.step");
    public static final RegistryObject<SoundEvent> MUSK_OX_AMBIENT = create("animal.musk_ox.ambient");
    public static final RegistryObject<SoundEvent> MUSK_OX_DEATH = create("animal.musk_ox.death");

    public static final RegistryObject<SoundEvent> DUCK_HURT = create("animal.duck.hurt");
    public static final RegistryObject<SoundEvent> DUCK_STEP = create("animal.duck.step");
    public static final RegistryObject<SoundEvent> DUCK_AMBIENT = create("animal.duck.ambient");
    public static final RegistryObject<SoundEvent> DUCK_DEATH = create("animal.duck.death");

    public static final RegistryObject<SoundEvent> QUAIL_HURT = create("animal.quail.hurt");
    public static final RegistryObject<SoundEvent> QUAIL_STEP = create("animal.quail.step");
    public static final RegistryObject<SoundEvent> QUAIL_AMBIENT = create("animal.quail.ambient");
    public static final RegistryObject<SoundEvent> QUAIL_DEATH = create("animal.quail.death");

    public static final RegistryObject<SoundEvent> PREDATOR_SLEEP = create("animal.predator.sleep");

    public static final RegistryObject<SoundEvent> COUGAR_AMBIENT = create("animal.cougar.ambient");
    public static final RegistryObject<SoundEvent> COUGAR_ATTACK = create("animal.cougar.attack");
    public static final RegistryObject<SoundEvent> COUGAR_DEATH = create("animal.cougar.death");
    public static final RegistryObject<SoundEvent> COUGAR_HURT = create("animal.cougar.hurt");

    public static final RegistryObject<SoundEvent> LION_AMBIENT = create("animal.lion.ambient");
    public static final RegistryObject<SoundEvent> LION_ATTACK = create("animal.lion.attack");
    public static final RegistryObject<SoundEvent> LION_DEATH = create("animal.lion.death");
    public static final RegistryObject<SoundEvent> LION_HURT = create("animal.lion.hurt");

    public static final RegistryObject<SoundEvent> SABERTOOTH_AMBIENT = create("animal.sabertooth.ambient");
    public static final RegistryObject<SoundEvent> SABERTOOTH_ATTACK = create("animal.sabertooth.attack");
    public static final RegistryObject<SoundEvent> SABERTOOTH_DEATH = create("animal.sabertooth.death");
    public static final RegistryObject<SoundEvent> SABERTOOTH_HURT = create("animal.sabertooth.hurt");


    public static final ForgeSoundType CHARCOAL = new ForgeSoundType(1.0F, 1.0F, CHARCOAL_PILE_BREAK, CHARCOAL_PILE_STEP, CHARCOAL_PILE_PLACE, CHARCOAL_PILE_HIT, CHARCOAL_PILE_FALL);
    public static final ForgeSoundType THATCH = new ForgeSoundType(1.0f, 1.0f, THATCH_BREAK, THATCH_STEP, THATCH_PLACE, THATCH_HIT, THATCH_FALL);
    public static final ForgeSoundType PEAT = new ForgeSoundType(1.0f, 1.0f, PEAT_BREAK, PEAT_STEP, PEAT_PLACE, PEAT_HIT, PEAT_FALL);
    public static final ForgeSoundType THIN = new ForgeSoundType(1.0f, 1.0f, THIN_BREAK, THIN_STEP, THIN_PLACE, THIN_HIT, THIN_FALL);

    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> new SoundEvent(Helpers.identifier(name)));
    }
}