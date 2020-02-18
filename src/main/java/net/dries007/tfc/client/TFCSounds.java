/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.util.Helpers.getNull;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class TFCSounds
{
    @GameRegistry.ObjectHolder(MOD_ID + ":rock.slide.long")
    public static final SoundEvent ROCK_SLIDE_LONG = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":rock.slide.short")
    public static final SoundEvent ROCK_SLIDE_SHORT = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":dirt.slide.short")
    public static final SoundEvent DIRT_SLIDE_SHORT = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":bellows.blow.air")
    public static final SoundEvent BELLOWS_BLOW_AIR = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":quern.stonedrag")
    public static final SoundEvent QUERN_USE = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":item.ceramicbreak")
    public static final SoundEvent CERAMIC_BREAK = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":anvil.metalimpact")
    public static final SoundEvent ANVIL_IMPACT = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":item.throw")
    public static final SoundEvent ITEM_THROW = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":item.jug.blow")
    public static final SoundEvent JUG_BLOW = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":item.firestarter")
    public static final SoundEvent FIRE_STARTER = getNull();

    @GameRegistry.ObjectHolder(MOD_ID + ":animal.bear.say")
    public static final SoundEvent ANIMAL_BEAR_SAY = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.bear.cry")
    public static final SoundEvent ANIMAL_BEAR_CRY = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.bear.hurt")
    public static final SoundEvent ANIMAL_BEAR_HURT = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.bear.death")
    public static final SoundEvent ANIMAL_BEAR_DEATH = getNull();

    @GameRegistry.ObjectHolder(MOD_ID + ":animal.deer.say")
    public static final SoundEvent ANIMAL_DEER_SAY = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.deer.cry")
    public static final SoundEvent ANIMAL_DEER_CRY = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.deer.hurt")
    public static final SoundEvent ANIMAL_DEER_HURT = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.deer.death")
    public static final SoundEvent ANIMAL_DEER_DEATH = getNull();

    @GameRegistry.ObjectHolder(MOD_ID + ":animal.pheasant.say")
    public static final SoundEvent ANIMAL_PHEASANT_SAY = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.pheasant.hurt")
    public static final SoundEvent ANIMAL_PHEASANT_HURT = getNull();
    @GameRegistry.ObjectHolder(MOD_ID + ":animal.pheasant.death")
    public static final SoundEvent ANIMAL_PHEASANT_DEATH = getNull();

    @GameRegistry.ObjectHolder(MOD_ID + ":animal.rooster.cry")
    public static final SoundEvent ANIMAL_ROOSTER_CRY = getNull();

    // These are static initialized because we need a custom sound type which uses the sounds before initialization
    private static final SoundEvent CHARCOAL_PILE_BREAK = createSoundEvent("block.charcoal.break");
    private static final SoundEvent CHARCOAL_PILE_FALL = createSoundEvent("block.charcoal.fall");
    private static final SoundEvent CHARCOAL_PILE_HIT = createSoundEvent("block.charcoal.hit");
    private static final SoundEvent CHARCOAL_PILE_PLACE = createSoundEvent("block.charcoal.place");
    private static final SoundEvent CHARCOAL_PILE_STEP = createSoundEvent("block.charcoal.step");

    public static final SoundType CHARCOAL_PILE = new SoundType(1.0F, 1.0F, CHARCOAL_PILE_BREAK, CHARCOAL_PILE_STEP, CHARCOAL_PILE_PLACE, CHARCOAL_PILE_HIT, CHARCOAL_PILE_FALL);

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        event.getRegistry().registerAll(
            // Custom block sounds
            CHARCOAL_PILE_BREAK,
            CHARCOAL_PILE_FALL,
            CHARCOAL_PILE_HIT,
            CHARCOAL_PILE_PLACE,
            CHARCOAL_PILE_STEP,
            // Misc
            createSoundEvent("rock.slide.long"),
            createSoundEvent("rock.slide.short"),
            createSoundEvent("dirt.slide.short"),
            createSoundEvent("bellows.blow.air"),
            createSoundEvent("quern.stonedrag"),
            createSoundEvent("item.ceramicbreak"),
            createSoundEvent("anvil.metalimpact"),
            createSoundEvent("item.throw"),
            createSoundEvent("item.jug.blow"),
            createSoundEvent("item.firestarter"),
            // Animals
            createSoundEvent("animal.bear.cry"),
            createSoundEvent("animal.bear.say"),
            createSoundEvent("animal.bear.hurt"),
            createSoundEvent("animal.bear.death"),
            createSoundEvent("animal.deer.cry"),
            createSoundEvent("animal.deer.say"),
            createSoundEvent("animal.deer.hurt"),
            createSoundEvent("animal.deer.death"),
            createSoundEvent("animal.pheasant.say"),
            createSoundEvent("animal.pheasant.hurt"),
            createSoundEvent("animal.pheasant.death"),
            createSoundEvent("animal.rooster.cry")
        );
    }

    private static SoundEvent createSoundEvent(String name)
    {
        final ResourceLocation soundID = new ResourceLocation(MOD_ID, name);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }
}
