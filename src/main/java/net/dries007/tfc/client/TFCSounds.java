/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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

    public static final ForgeSoundType CHARCOAL = createBlock("charcoal");
    public static final ForgeSoundType THATCH = createBlock("thatch");
    public static final ForgeSoundType PEAT = createBlock("peat");
    public static final ForgeSoundType THIN = createBlock("thin");

    // Entities
    public static final EntitySound PIG = new EntitySound(() -> SoundEvents.PIG_AMBIENT, () -> SoundEvents.PIG_DEATH, () -> SoundEvents.PIG_HURT, () -> SoundEvents.PIG_STEP);
    public static final EntitySound COW = new EntitySound(() -> SoundEvents.COW_AMBIENT, () -> SoundEvents.COW_DEATH, () -> SoundEvents.COW_HURT, () -> SoundEvents.COW_STEP);
    public static final EntitySound CHICKEN = new EntitySound(() -> SoundEvents.CHICKEN_AMBIENT, () -> SoundEvents.CHICKEN_DEATH, () -> SoundEvents.CHICKEN_HURT, () -> SoundEvents.CHICKEN_STEP);
    public static final EntitySound ALPACA = createEntity("alpaca", false, false);
    public static final EntitySound LION = createEntity("lion", true, true);
    public static final EntitySound COUGAR = createEntity("cougar", true, true);
    public static final EntitySound SABERTOOTH = createEntity("sabertooth", true, true);
    public static final EntitySound BEAR = createEntity("bear", true, true);

    // Random
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_LONG = create("random.rock_slide_long");
    public static final RegistryObject<SoundEvent> ROCK_SLIDE_SHORT = create("random.rock_slide_short");
    public static final RegistryObject<SoundEvent> DIRT_SLIDE_SHORT = create("random.dirt_slide_short");

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
        return new ForgeSoundType(1.0f, 1.0f,
            create("block.%s.break".formatted(name)),
            create("block.%s.step".formatted(name)),
            create("block.%s.place".formatted(name)),
            create("block.%s.hit".formatted(name)),
            create("block.%s.fall".formatted(name))
        );
    }

    private static EntitySound createEntity(String name, boolean attack, boolean sleep)
    {
        return new EntitySound(
            create("entity.%s.ambient".formatted(name)),
            create("entity.%s.death".formatted(name)),
            create("entity.%s.hurt".formatted(name)),
            create("entity.%s.step".formatted(name)),
            createOptional("entity.%s.attack".formatted(name), attack),
            createOptional("entity.%s.sleep".formatted(name), sleep)
        );
    }

    public record EntitySound(
        Supplier<SoundEvent> ambient,
        Supplier<SoundEvent> death,
        Supplier<SoundEvent> hurt,
        Supplier<SoundEvent> step,
        Optional<Supplier<SoundEvent>> attack,
        Optional<Supplier<SoundEvent>> sleep
    )
    {
        public EntitySound(Supplier<SoundEvent> ambient, Supplier<SoundEvent> death, Supplier<SoundEvent> hurt, Supplier<SoundEvent> step)
        {
            this(ambient, death, hurt, step, Optional.empty(), Optional.empty());
        }
    }
}