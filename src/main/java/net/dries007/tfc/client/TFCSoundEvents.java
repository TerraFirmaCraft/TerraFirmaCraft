/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

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
    @GameRegistry.ObjectHolder("item.throw")
    public static final SoundEvent ITEM_THROW = getNull();

    @GameRegistry.ObjectHolder("animal.bear.say")
    public static final SoundEvent ANIMAL_BEAR_SAY = getNull();
    @GameRegistry.ObjectHolder("animal.bear.cry")
    public static final SoundEvent ANIMAL_BEAR_CRY = getNull();
    @GameRegistry.ObjectHolder("animal.bear.hurt")
    public static final SoundEvent ANIMAL_BEAR_HURT = getNull();
    @GameRegistry.ObjectHolder("animal.bear.death")
    public static final SoundEvent ANIMAL_BEAR_DEATH = getNull();

    @GameRegistry.ObjectHolder("animal.deer.say")
    public static final SoundEvent ANIMAL_DEER_SAY = getNull();
    @GameRegistry.ObjectHolder("animal.deer.cry")
    public static final SoundEvent ANIMAL_DEER_CRY = getNull();
    @GameRegistry.ObjectHolder("animal.deer.hurt")
    public static final SoundEvent ANIMAL_DEER_HURT = getNull();
    @GameRegistry.ObjectHolder("animal.deer.death")
    public static final SoundEvent ANIMAL_DEER_DEATH = getNull();

    @GameRegistry.ObjectHolder("animal.pheasant.say")
    public static final SoundEvent ANIMAL_PHEASANT_SAY = getNull();
    @GameRegistry.ObjectHolder("animal.pheasant.hurt")
    public static final SoundEvent ANIMAL_PHEASANT_HURT = getNull();
    @GameRegistry.ObjectHolder("animal.pheasant.death")
    public static final SoundEvent ANIMAL_PHEASANT_DEATH = getNull();

    @GameRegistry.ObjectHolder("animal.rooster.cry")
    public static final SoundEvent ANIMAL_ROOSTER_CRY = getNull();

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> r = event.getRegistry();
        register(r, "rock.slide.long");
        register(r, "rock.slide.short");
        register(r, "dirt.slide.short");
        register(r, "bellows.blow.air");
        register(r, "quern.stonedrag");
        register(r, "item.ceramicbreak");
        register(r, "anvil.metalimpact");
        register(r, "item.throw");

        register(r, "animal.bear.cry");
        register(r, "animal.bear.say");
        register(r, "animal.bear.hurt");
        register(r, "animal.bear.death");
        register(r, "animal.deer.cry");
        register(r, "animal.deer.say");
        register(r, "animal.deer.hurt");
        register(r, "animal.deer.death");
        register(r, "animal.pheasant.say");
        register(r, "animal.pheasant.hurt");
        register(r, "animal.pheasant.death");
        register(r, "animal.rooster.cry");
    }

    private static void register(IForgeRegistry<SoundEvent> r, String name)
    {
        ResourceLocation soundID = new ResourceLocation(MOD_ID, name);
        r.register(new SoundEvent(soundID).setRegistryName(soundID));
    }
}
