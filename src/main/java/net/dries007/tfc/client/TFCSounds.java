/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public final class TFCSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    // Items
    public static final RegistryObject<SoundEvent> KNAP_STONE = create("item.knapping.stone");
    public static final RegistryObject<SoundEvent> KNAP_CLAY = create("item.knapping.clay");
    public static final RegistryObject<SoundEvent> KNAP_LEATHER = create("item.knapping.leather");
    public static final RegistryObject<SoundEvent> FIRESTARTER = create("item.firestarter.use");
    public static final RegistryObject<SoundEvent> CERAMIC_BREAK = create("item.ceramic.break");
    public static final RegistryObject<SoundEvent> JUG_BLOW = create("item.jug.blow");
    public static final RegistryObject<SoundEvent> PANNING = create("item.pan.use");
    public static final RegistryObject<SoundEvent> FERTILIZER_USE = create("item.fertilizer.use");

    // Blocks
    public static final RegistryObject<SoundEvent> QUERN_DRAG = create("block.quern.drag");
    public static final RegistryObject<SoundEvent> LOOM_WEAVE = create("block.loom.weave");
    public static final RegistryObject<SoundEvent> TOOL_RACK_PLACE = create("block.tool_rack.place");
    public static final RegistryObject<SoundEvent> BELLOWS_BLOW = create("block.bellows.blow");
    public static final RegistryObject<SoundEvent> SCRIBING_TABLE = create("block.scribing_table.use");
    public static final RegistryObject<SoundEvent> OPEN_VESSEL = create("block.large_vessel.open");
    public static final RegistryObject<SoundEvent> CLOSE_VESSEL = create("block.large_vessel.close");
    public static final RegistryObject<SoundEvent> WATTLE_DYED = create("block.wattle.dyed");
    public static final RegistryObject<SoundEvent> WATTLE_DAUBED = create("block.wattle.daubed");
    public static final RegistryObject<SoundEvent> WATTLE_WOVEN = create("block.wattle.woven");
    public static final RegistryObject<SoundEvent> ANVIL_HIT = create("block.anvil.hit");
    public static final RegistryObject<SoundEvent> CROP_STICK_ADD = create("block.crop.stick_add");
    public static final RegistryObject<SoundEvent> BLOOMERY_CRACKLE = create("block.bloomery.crackle");
    public static final RegistryObject<SoundEvent> BARREL_DRIP = create("block.barrel.drip");

    // Armor
    public static final Map<TFCArmorMaterials, RegistryObject<SoundEvent>> ARMOR_EQUIP = Helpers.mapOfKeys(TFCArmorMaterials.class, mat -> create("item.armor.equip_" + mat.getId().getPath()));

    public static final ForgeSoundType CHARCOAL = createBlock("charcoal");
    public static final ForgeSoundType THATCH = createBlock("thatch");
    public static final ForgeSoundType PEAT = createBlock("peat");
    public static final ForgeSoundType THIN = createBlock("thin");

    // Entities
    public static final EntitySound PIG = new EntitySound(() -> SoundEvents.PIG_AMBIENT, () -> SoundEvents.PIG_DEATH, () -> SoundEvents.PIG_HURT, () -> SoundEvents.PIG_STEP);
    public static final EntitySound COW = new EntitySound(() -> SoundEvents.COW_AMBIENT, () -> SoundEvents.COW_DEATH, () -> SoundEvents.COW_HURT, () -> SoundEvents.COW_STEP);
    public static final EntitySound CHICKEN = new EntitySound(() -> SoundEvents.CHICKEN_AMBIENT, () -> SoundEvents.CHICKEN_DEATH, () -> SoundEvents.CHICKEN_HURT, () -> SoundEvents.CHICKEN_STEP);
    public static final EntitySound GOAT = new EntitySound(() -> SoundEvents.GOAT_AMBIENT, () -> SoundEvents.GOAT_DEATH, () -> SoundEvents.GOAT_HURT, () -> SoundEvents.GOAT_STEP);
    public static final EntitySound SHEEP = new EntitySound(() -> SoundEvents.SHEEP_AMBIENT, () -> SoundEvents.SHEEP_DEATH, () -> SoundEvents.SHEEP_HURT, () -> SoundEvents.SHEEP_STEP);
    public static final EntitySound DONKEY = new EntitySound(() -> SoundEvents.DONKEY_AMBIENT, () -> SoundEvents.DONKEY_DEATH, () -> SoundEvents.DONKEY_HURT, () -> SoundEvents.HORSE_STEP);
    public static final EntitySound MULE = new EntitySound(() -> SoundEvents.MULE_AMBIENT, () -> SoundEvents.MULE_DEATH, () -> SoundEvents.MULE_HURT, () -> SoundEvents.HORSE_STEP);
    public static final EntitySound HORSE = new EntitySound(() -> SoundEvents.HORSE_AMBIENT, () -> SoundEvents.HORSE_DEATH, () -> SoundEvents.HORSE_HURT, () -> SoundEvents.HORSE_STEP);
    public static final EntitySound CAT = new EntitySound(() -> SoundEvents.CAT_AMBIENT, () -> SoundEvents.CAT_DEATH, () -> SoundEvents.CAT_HURT, () -> SoundEvents.CHICKEN_STEP, Optional.of(() -> SoundEvents.CAT_HISS), Optional.of(() -> SoundEvents.CAT_PURR));
    public static final EntitySound DOG = new EntitySound(() -> SoundEvents.WOLF_AMBIENT, () -> SoundEvents.WOLF_DEATH, () -> SoundEvents.WOLF_HURT, () -> SoundEvents.WOLF_STEP, Optional.of(() -> SoundEvents.WOLF_GROWL), Optional.of(() -> SoundEvents.WOLF_WHINE));
    public static final EntitySound TURTLE = new EntitySound(() -> SoundEvents.TURTLE_AMBIENT_LAND, () -> SoundEvents.TURTLE_DEATH, () -> SoundEvents.TURTLE_HURT, () -> SoundEvents.TURTLE_SHAMBLE);
    public static final EntitySound ALPACA = createEntity("alpaca", false, false);
    public static final EntitySound YAK = createEntity("yak", false, false);
    public static final EntitySound MUSK_OX = createEntity("musk_ox", false, false);
    public static final EntitySound DUCK = createEntity("duck", false, false);
    public static final EntitySound PENGUIN = createEntity("penguin", false, false);
    public static final EntitySound QUAIL = createEntity("quail", false, false);
    public static final EntitySound LION = createEntity("lion", true, true);
    public static final EntitySound COUGAR = createEntity("cougar", true, true);
    public static final EntitySound SABERTOOTH = createEntity("sabertooth", true, true);
    public static final EntitySound BEAR = createEntity("bear", true, true);
    public static final EntitySound DEER = createEntity("deer", false, false);
    public static final EntitySound MOOSE = createEntity("moose", false, false);
    public static final EntitySound GROUSE = createEntity("grouse", false, false);
    public static final EntitySound PHEASANT = createEntity("pheasant", false, false);
    public static final EntitySound TURKEY = createEntity("turkey", false, false);
    public static final EntitySound RAT = createEntity("rat", false, false);
    public static final FishSound MANATEE = createFish("manatee");
    public static final FishSound JELLYFISH = createFish("jellyfish");
    public static final FishSound BLUEGILL = createFish("bluegill");

    public static final RegistryObject<SoundEvent> ROOSTER_CRY = create("entity.rooster.cry");

    // Random
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_LONG = create("random.rock_slide_long");
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_LONG_FAKE = create("random.rock_slide_long_fake");
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_SHORT = create("random.rock_slide_short");
    public static final RegistryObject<SoundEvent> DIRT_SLIDE_SHORT = create("random.dirt_slide_short");
    public static final RegistryObject<SoundEvent> ROCK_SMASH = create("random.rock_smash");

    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> new SoundEvent(Helpers.identifier(name)));
    }

    private static Optional<Supplier<SoundEvent>> createOptional(String name, boolean present)
    {
        return Optional.ofNullable(present ? create(name) : null);
    }

    private static ForgeSoundType createBlock(String name)
    {
        return new ForgeSoundType(1.0f, 1.0f, create("block.%s.break".formatted(name)), create("block.%s.step".formatted(name)), create("block.%s.place".formatted(name)), create("block.%s.hit".formatted(name)), create("block.%s.fall".formatted(name)));
    }

    private static EntitySound createEntity(String name, boolean attack, boolean sleep)
    {
        return new EntitySound(create("entity.%s.ambient".formatted(name)), create("entity.%s.death".formatted(name)), create("entity.%s.hurt".formatted(name)), create("entity.%s.step".formatted(name)), createOptional("entity.%s.attack".formatted(name), attack), createOptional("entity.%s.sleep".formatted(name), sleep));
    }

    public record EntitySound(Supplier<SoundEvent> ambient, Supplier<SoundEvent> death, Supplier<SoundEvent> hurt, Supplier<SoundEvent> step, Optional<Supplier<SoundEvent>> attack, Optional<Supplier<SoundEvent>> sleep)
    {
        public EntitySound(Supplier<SoundEvent> ambient, Supplier<SoundEvent> death, Supplier<SoundEvent> hurt, Supplier<SoundEvent> step)
        {
            this(ambient, death, hurt, step, Optional.empty(), Optional.empty());
        }
    }

    private static FishSound createFish(String name)
    {
        return new FishSound(create("entity.%s.ambient".formatted(name)), create("entity.%s.death".formatted(name)), create("entity.%s.hurt".formatted(name)), create("entity.%s.flop".formatted(name)));
    }

    public record FishSound(Supplier<SoundEvent> ambient, Supplier<SoundEvent> death, Supplier<SoundEvent> hurt, Supplier<SoundEvent> flop) {}
}