/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.entities.aquatic.Fish;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("unused")
public final class TFCSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    // Items
    public static final Id KNAP_STONE = register("item.knapping.stone");
    public static final Id KNAP_CLAY = register("item.knapping.clay");
    public static final Id KNAP_LEATHER = register("item.knapping.leather");
    public static final Id FIRESTARTER = register("item.firestarter.use");
    public static final Id CERAMIC_BREAK = register("item.ceramic.break");
    public static final Id JUG_BLOW = register("item.jug.blow");
    public static final Id PANNING = register("item.pan.use");
    public static final Id FERTILIZER_USE = register("item.fertilizer.use");
    public static final Id JAVELIN_HIT = register("item.javelin.hit");
    public static final Id JAVELIN_HIT_GROUND = register("item.javelin.hit_ground");
    public static final Id JAVELIN_THROWN = register("item.javelin.throw");
    public static final Id ITEM_COOL = register("item.cool");

    // Blocks
    public static final Id QUERN_DRAG = register("block.quern.drag");
    public static final Id LOOM_WEAVE = register("block.loom.weave");
    public static final Id TOOL_RACK_PLACE = register("block.tool_rack.place");
    public static final Id BELLOWS_BLOW = register("block.bellows.blow");
    public static final Id SCRIBING_TABLE = register("block.scribing_table.use");
    public static final Id OPEN_BARREL = register("block.barrel.open");
    public static final Id CLOSE_BARREL = register("block.barrel.close");
    public static final Id OPEN_VESSEL = register("block.large_vessel.open");
    public static final Id CLOSE_VESSEL = register("block.large_vessel.close");
    public static final Id WATTLE_DYED = register("block.wattle.dyed");
    public static final Id WATTLE_DAUBED = register("block.wattle.daubed");
    public static final Id WATTLE_WOVEN = register("block.wattle.woven");
    public static final Id ANVIL_HIT = register("block.anvil.hit");
    public static final Id CROP_STICK_ADD = register("block.crop.stick_add");
    public static final Id BLOOMERY_CRACKLE = register("block.bloomery.crackle");
    public static final Id BARREL_DRIP = register("block.barrel.drip");

    // Armor
    public static final Id COPPER_EQUIP = register("item.armor.equip_copper");
    public static final Id BISMUTH_BRONZE_EQUIP = register("item.armor.equip_bismuth_bronze");
    public static final Id BLACK_BRONZE_EQUIP = register("item.armor.equip_black_bronze");
    public static final Id BRONZE_EQUIP = register("item.armor.equip_bronze");
    public static final Id WROUGHT_IRON_EQUIP = register("item.armor.equip_wrought_iron");
    public static final Id STEEL_EQUIP = register("item.armor.equip_steel");
    public static final Id BLACK_STEEL_EQUIP = register("item.armor.equip_black_steel");
    public static final Id BLUE_STEEL_EQUIP = register("item.armor.equip_blue_steel");
    public static final Id RED_STEEL_EQUIP = register("item.armor.equip_red_steel");

    public static final SoundType CHARCOAL = registerBlock("charcoal");
    public static final SoundType THATCH = registerBlock("thatch");
    public static final SoundType PEAT = registerBlock("peat");
    public static final SoundType THIN = registerBlock("thin");

    // Entities
    public static final EntityId PIG = registerEntity(SoundEvents.PIG_AMBIENT, SoundEvents.PIG_DEATH, SoundEvents.PIG_HURT, SoundEvents.PIG_STEP);
    public static final EntityId COW = registerEntity(SoundEvents.COW_AMBIENT, SoundEvents.COW_DEATH, SoundEvents.COW_HURT, SoundEvents.COW_STEP);
    public static final EntityId CHICKEN = registerEntity(SoundEvents.CHICKEN_AMBIENT, SoundEvents.CHICKEN_DEATH, SoundEvents.CHICKEN_HURT, SoundEvents.CHICKEN_STEP);
    public static final EntityId GOAT = registerEntity(SoundEvents.GOAT_AMBIENT, SoundEvents.GOAT_DEATH, SoundEvents.GOAT_HURT, SoundEvents.GOAT_STEP);
    public static final EntityId SHEEP = registerEntity(SoundEvents.SHEEP_AMBIENT, SoundEvents.SHEEP_DEATH, SoundEvents.SHEEP_HURT, SoundEvents.SHEEP_STEP);
    public static final EntityId DONKEY = registerEntity(SoundEvents.DONKEY_AMBIENT, SoundEvents.DONKEY_DEATH, SoundEvents.DONKEY_HURT, SoundEvents.HORSE_STEP);
    public static final EntityId MULE = registerEntity(SoundEvents.MULE_AMBIENT, SoundEvents.MULE_DEATH, SoundEvents.MULE_HURT, SoundEvents.HORSE_STEP);
    public static final EntityId HORSE = registerEntity(SoundEvents.HORSE_AMBIENT, SoundEvents.HORSE_DEATH, SoundEvents.HORSE_HURT, SoundEvents.HORSE_STEP);
    public static final EntityId CAT = new EntityId(() -> SoundEvents.CAT_AMBIENT, () -> SoundEvents.CAT_DEATH, () -> SoundEvents.CAT_HURT, () -> SoundEvents.CHICKEN_STEP, Optional.of(() -> SoundEvents.CAT_HISS), Optional.of(() -> SoundEvents.CAT_PURR));
    public static final EntityId TURTLE = registerEntity(SoundEvents.TURTLE_AMBIENT_LAND, SoundEvents.TURTLE_DEATH, SoundEvents.TURTLE_HURT, SoundEvents.TURTLE_SHAMBLE);

    public static final EntityId DOG = registerEntity("dog", true, true);
    public static final EntityId TFC_WOLF = registerEntity("tfc_wolf", true, true);
    public static final EntityId HYENA = registerEntity("hyena", true, true);
    public static final EntityId ALPACA = registerEntity("alpaca", false, false);
    public static final EntityId YAK = registerEntity("yak", false, false);
    public static final EntityId MUSK_OX = registerEntity("musk_ox", false, false);
    public static final EntityId DUCK = registerEntity("duck", false, false);
    public static final EntityId PENGUIN = registerEntity("penguin", false, false);
    public static final EntityId QUAIL = registerEntity("quail", false, false);
    public static final EntityId LION = registerEntity("lion", true, true);
    public static final EntityId COUGAR = registerEntity("cougar", true, true);
    public static final EntityId SABERTOOTH = registerEntity("sabertooth", true, true);
    public static final EntityId TIGER = registerEntity("tiger", true, true);
    public static final EntityId CROCODILE = registerEntity("crocodile", true, true);
    public static final EntityId BEAR = registerEntity("bear", true, true);
    public static final EntityId DEER = registerEntity("deer", false, false);
    public static final EntityId BOAR = registerEntity("boar", true, false);
    public static final EntityId WILDEBEEST = registerEntity("wildebeest", true, false);
    public static final EntityId MOOSE = registerEntity("moose", true, false);
    public static final EntityId GROUSE = registerEntity("grouse", false, false);
    public static final EntityId PHEASANT = registerEntity("pheasant", false, false);
    public static final EntityId TURKEY = registerEntity("turkey", false, false);
    public static final EntityId PEAFOWL = registerEntity("peafowl", false, false);
    public static final EntityId RAT = registerEntity("rat", false, false);
    public static final EntityId BONGO = registerEntity("bongo", false, false);
    public static final EntityId CARIBOU = registerEntity("caribou", false, false);
    public static final EntityId GAZELLE = registerEntity("gazelle", false, false);

    public static final FishId MANATEE = registerFish("manatee");
    public static final FishId JELLYFISH = registerFish("jellyfish");

    public static final Map<Fish, FishId> FRESHWATER_FISHES = Helpers.mapOf(Fish.class, Fish::makeSound);

    public static final Id ROOSTER_CRY = register("entity.rooster.cry");
    public static final Id RAMMING_IMPACT = register("entity.ramming.impact");

    // Random
    public static final Id ROCK_SLIDE_LONG = register("random.rock_slide_long");
    public static final Id ROCK_SLIDE_LONG_FAKE = register("random.rock_slide_long_fake");
    public static final Id ROCK_SLIDE_SHORT = register("random.rock_slide_short");
    public static final Id DIRT_SLIDE_SHORT = register("random.dirt_slide_short");
    public static final Id ROCK_SMASH = register("random.rock_smash");


    private static Id register(String name)
    {
        return new Id(SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(Helpers.identifier(name))));
    }

    private static SoundType registerBlock(String name)
    {
        return new DeferredSoundType(
            1.0f, 1.0f,
            register("block.%s.break".formatted(name)),
            register("block.%s.step".formatted(name)),
            register("block.%s.place".formatted(name)),
            register("block.%s.hit".formatted(name)),
            register("block.%s.fall".formatted(name))
        );
    }

    private static EntityId registerEntity(SoundEvent ambient, SoundEvent death, SoundEvent hurt, SoundEvent step)
    {
        return new EntityId(() -> ambient, () -> death, () -> hurt, () -> step, Optional.empty(), Optional.empty());
    }

    private static EntityId registerEntity(String name, boolean attack, boolean sleep)
    {
        return new EntityId(
            register("entity.%s.ambient".formatted(name)),
            register("entity.%s.death".formatted(name)),
            register("entity.%s.hurt".formatted(name)),
            register("entity.%s.step".formatted(name)),
            attack ? Optional.of(register("entity.%s.attack".formatted(name))) : Optional.empty(),
            sleep ? Optional.of(register("entity.%s.sleep".formatted(name))) : Optional.empty()
        );
    }

    public static FishId registerFish(String name)
    {
        return new FishId(
            register("entity.%s.ambient".formatted(name)),
            register("entity.%s.death".formatted(name)),
            register("entity.%s.hurt".formatted(name)),
            register("entity.%s.flop".formatted(name))
        );
    }
    
    public record Id(DeferredHolder<SoundEvent, SoundEvent> holder) implements RegistryHolder<SoundEvent, SoundEvent> {}

    public record EntityId(Supplier<SoundEvent> ambient, Supplier<SoundEvent> death, Supplier<SoundEvent> hurt, Supplier<SoundEvent> step, Optional<Supplier<SoundEvent>> attack, Optional<Supplier<SoundEvent>> sleep) {}

    public record FishId(Supplier<SoundEvent> ambient, Supplier<SoundEvent> death, Supplier<SoundEvent> hurt, Supplier<SoundEvent> flop) {}
}