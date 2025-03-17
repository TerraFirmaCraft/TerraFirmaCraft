/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.glass;

import com.google.common.base.Suppliers;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class GlassOperation
{

    public static final ResourceKey<Registry<GlassOperation>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("glass_operation"));
    public static final Registry<GlassOperation> REGISTRY = new RegistryBuilder<>(KEY)
        .sync(true)
        .create();
    public static final DeferredRegister<GlassOperation> OPERATIONS = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    public static final Supplier<Map<Item, GlassOperation>> POWDERS = Suppliers.memoize(() -> {
        final Map<Item, GlassOperation> map = new IdentityHashMap<>();
        REGISTRY.stream().filter(op -> op.powder).forEach(op -> {
            for (Holder<Item> item : op.items)
            {
                map.put(item.value(), op);
            }
        });
        return map;
    });

    // Blowpipe
    public static final DeferredHolder<GlassOperation, GlassOperation> BLOW = OPERATIONS.register("blow", () -> new GlassOperation(Set.of(TFCItems.BLOWPIPE_WITH_GLASS.holder(), TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.holder()), TFCSounds.BELLOWS_BLOW.holder(), Heat.FAINT_RED.getMin(), false));
    public static final DeferredHolder<GlassOperation, GlassOperation> ROLL = register("roll", TFCItems.WOOL_CLOTH);
    public static final DeferredHolder<GlassOperation, GlassOperation> STRETCH = register("stretch", Set.of(TFCItems.BLOWPIPE_WITH_GLASS.holder(), TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.holder()));
    public static final DeferredHolder<GlassOperation, GlassOperation> PINCH = register("pinch", TFCItems.JACKS);
    public static final DeferredHolder<GlassOperation, GlassOperation> FLATTEN = register("flatten", TFCItems.PADDLE);
    public static final DeferredHolder<GlassOperation, GlassOperation> SAW = OPERATIONS.register("saw", () -> new GlassOperation(Set.of(TFCItems.GEM_SAW.holder()), Holder.direct(SoundEvents.ANVIL_USE), 0, false));
    // Powder Bowl
    public static final DeferredHolder<GlassOperation, GlassOperation> AMETHYST = powder("amethyst", TFCItems.ORE_POWDERS.get(Ore.AMETHYST));
    public static final DeferredHolder<GlassOperation, GlassOperation> SODA_ASH = powder("soda_ash", TFCItems.POWDERS.get(Powder.SODA_ASH));
    public static final DeferredHolder<GlassOperation, GlassOperation> SULFUR = powder("sulfur", TFCItems.ORE_POWDERS.get(Ore.SULFUR));
    public static final DeferredHolder<GlassOperation, GlassOperation> IRON = powder("iron", Set.of(TFCItems.ORE_POWDERS.get(Ore.HEMATITE).holder(), TFCItems.ORE_POWDERS.get(Ore.LIMONITE).holder(), TFCItems.ORE_POWDERS.get(Ore.MAGNETITE).holder()));
    public static final DeferredHolder<GlassOperation, GlassOperation> RUBY = powder("ruby", TFCItems.ORE_POWDERS.get(Ore.RUBY));
    public static final DeferredHolder<GlassOperation, GlassOperation> LAPIS_LAZULI = powder("lapis_lazuli", TFCItems.ORE_POWDERS.get(Ore.LAPIS_LAZULI));
    public static final DeferredHolder<GlassOperation, GlassOperation> PYRITE = powder("pyrite", TFCItems.ORE_POWDERS.get(Ore.PYRITE));
    public static final DeferredHolder<GlassOperation, GlassOperation> SAPPHIRE = powder("sapphire", TFCItems.ORE_POWDERS.get(Ore.SAPPHIRE));
    public static final DeferredHolder<GlassOperation, GlassOperation> GOLD = powder("gold", TFCItems.ORE_POWDERS.get(Ore.NATIVE_GOLD));
    public static final DeferredHolder<GlassOperation, GlassOperation> GRAPHITE = powder("graphite", TFCItems.ORE_POWDERS.get(Ore.GRAPHITE));
    public static final DeferredHolder<GlassOperation, GlassOperation> COPPER = powder("copper", Set.of(TFCItems.ORE_POWDERS.get(Ore.NATIVE_COPPER).holder(), TFCItems.ORE_POWDERS.get(Ore.MALACHITE).holder(), TFCItems.ORE_POWDERS.get(Ore.TETRAHEDRITE).holder()));
    public static final DeferredHolder<GlassOperation, GlassOperation> NICKEL = powder("nickel", TFCItems.ORE_POWDERS.get(Ore.GARNIERITE));
    public static final DeferredHolder<GlassOperation, GlassOperation> TIN = powder("tin", TFCItems.ORE_POWDERS.get(Ore.CASSITERITE));
    public static final DeferredHolder<GlassOperation, GlassOperation> SILVER = powder("silver", TFCItems.ORE_POWDERS.get(Ore.NATIVE_SILVER));
    // Pours
    public static final DeferredHolder<GlassOperation, GlassOperation> TABLE_POUR = register("table_pour", Set.of(TFCItems.BLOWPIPE_WITH_GLASS.holder(), TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.holder()));
    public static final DeferredHolder<GlassOperation, GlassOperation> BASIN_POUR = register("basin_pour", Set.of(TFCItems.BLOWPIPE_WITH_GLASS.holder(), TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.holder()));


    @Nullable
    public static GlassOperation get(ItemStack stack, Player player)
    {
        if (stack.isEmpty())
        {
            return (player.getLookAngle().y < -0.95 ? STRETCH : BLOW).get();
        }
        if (stack.getItem() instanceof IGlassworkingTool tool)
        {
            return tool.getOperation();
        }
        return null;
    }

    @Nullable
    public static GlassOperation getByPowder(ItemStack stack)
    {
        return POWDERS.get().get(stack.getItem());
    }

    private final Set<Holder<Item>> items;
    private final Holder<SoundEvent> sound;
    private final float minTemperature;
    private final boolean powder;
    @Nullable
    private String translationId;

    public GlassOperation(Holder<Item> item, boolean isPowder)
    {
        this(Set.of(item), Holder.direct(SoundEvents.ANVIL_USE), Heat.FAINT_RED.getMin(), isPowder);
    }

    public GlassOperation(Set<Holder<Item>> items, Holder<SoundEvent> sound, float minTemperature, boolean isPowder)
    {
        this.items = items;
        this.sound = sound;
        this.minTemperature = minTemperature;
        powder = isPowder;
    }

    public String getTranslationId()
    {
        if (translationId == null)
        {
            translationId = Util.makeDescriptionId("glass_operation", REGISTRY.getKey(this));
        }
        return translationId;
    }

    public Set<Holder<Item>> getItems()
    {
        return items;
    }

    public SoundEvent getSound()
    {
        return sound.value();
    }

    public boolean hasRequiredTemperature(ItemStack stack)
    {
        return HeatCapability.getTemperature(stack) > minTemperature;
    }

    private static DeferredHolder<GlassOperation, GlassOperation> powder(String name, TFCItems.ItemId item)
    {
        return OPERATIONS.register(name, () -> new GlassOperation(item.holder(), true));
    }

    private static DeferredHolder<GlassOperation, GlassOperation> powder(String name, Set<Holder<Item>> items)
    {
        return OPERATIONS.register(name, () -> new GlassOperation(items, Holder.direct(SoundEvents.ANVIL_USE), Heat.FAINT_RED.getMin(), true));
    }

    private static DeferredHolder<GlassOperation, GlassOperation> register(String name, Set<Holder<Item>> items)
    {
        return OPERATIONS.register(name, () -> new GlassOperation(items, Holder.direct(SoundEvents.ANVIL_USE), Heat.FAINT_RED.getMin(), false));
    }

    private static DeferredHolder<GlassOperation, GlassOperation> register(String name, TFCItems.ItemId item)
    {
        return OPERATIONS.register(name, () -> new GlassOperation(item.holder(), false));
    }
}
