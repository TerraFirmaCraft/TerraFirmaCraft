/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.glass;

import java.util.Map;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.Gem;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.items.GlassworkingItem;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;

public enum GlassOperation
{
    // Blowpipe
    BLOW,
    ROLL,
    STRETCH,
    PINCH,
    FLATTEN,
    SAW,
    // Powder Bowl
    AMETHYST,
    SODA_ASH,
    SULFUR,
    IRON,
    RUBY,
    LAPIS_LAZULI,
    PYRITE,
    SAPPHIRE,
    GOLD,
    GRAPHITE,
    COPPER,
    NICKEL,
    TIN,
    SILVER,
    // Pours
    TABLE_POUR,
    BASIN_POUR,
    ;

    public static final GlassOperation[] VALUES = values();

    public static final Supplier<Map<Item, GlassOperation>> POWDERS = Suppliers.memoize(() -> {
            ImmutableMap.Builder<Item, GlassOperation> builder = ImmutableMap.builder();
            builder.put(TFCItems.POWDERS.get(Powder.SODA_ASH).get(), SODA_ASH);
            builder.put(TFCItems.POWDERS.get(Powder.SULFUR).get(), SULFUR);
            builder.put(TFCItems.POWDERS.get(Powder.GRAPHITE).get(), GRAPHITE);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.HEMATITE).get(), IRON);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.LIMONITE).get(), IRON);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.MAGNETITE).get(), IRON);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.NATIVE_GOLD).get(), GOLD);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.NATIVE_COPPER).get(), COPPER);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.MALACHITE).get(), COPPER);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.TETRAHEDRITE).get(), COPPER);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.CASSITERITE).get(), TIN);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.GARNIERITE).get(), NICKEL);
            builder.put(TFCItems.ORE_POWDERS.get(Ore.NATIVE_SILVER).get(), SILVER);
            builder.put(TFCItems.GEM_DUST.get(Gem.AMETHYST).get(), AMETHYST);
            builder.put(TFCItems.GEM_DUST.get(Gem.RUBY).get(), RUBY);
            builder.put(TFCItems.GEM_DUST.get(Gem.LAPIS_LAZULI).get(), LAPIS_LAZULI);
            builder.put(TFCItems.GEM_DUST.get(Gem.PYRITE).get(), PYRITE);
            builder.put(TFCItems.GEM_DUST.get(Gem.SAPPHIRE).get(), SAPPHIRE);
            return builder.build();
        }
    );

    @Nullable
    public static GlassOperation byIndex(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : null;
    }

    @Nullable
    public static GlassOperation get(ItemStack stack, Player player)
    {
        if (stack.isEmpty())
        {
            return player.getLookAngle().y < -0.95 ? STRETCH : BLOW;
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

    public SoundEvent getSound()
    {
        return this == BLOW ? TFCSounds.BELLOWS_BLOW.get() : SoundEvents.ANVIL_USE;
    }

    public boolean hasRequiredTemperature(ItemStack stack)
    {
        return this == SAW
            || HeatCapability.getTemperature(stack) > Heat.FAINT_RED.getMin();
    }
}
