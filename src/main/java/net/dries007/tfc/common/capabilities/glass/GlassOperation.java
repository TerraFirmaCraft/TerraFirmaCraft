/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.glass;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.items.GlassworkingItem;

public enum GlassOperation
{
    BLOW,
    WET_ROLL,
    HARD_ROLL,
    STRETCH,
    REHEAT,
    PINCH,
    EXPAND,
    FLATTEN,
    ANNEAL,
    SAW
    ;

    public static final GlassOperation[] VALUES = values();

    @Nullable
    public static GlassOperation byIndex(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : null;
    }

    @Nullable
    public static GlassOperation get(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return BLOW;
        }
        if (stack.getItem() instanceof GlassworkingItem item)
        {
            return item.getOperation();
        }
        return null;
    }

    public SoundEvent getSound()
    {
        return this == BLOW ? TFCSounds.BELLOWS_BLOW.get() : SoundEvents.ANVIL_USE;
    }

    public boolean hasRequiredTemperature(ItemStack stack)
    {
        if (this == SAW)
        {
            return true;
        }
        return stack.getCapability(HeatCapability.CAPABILITY).map(cap -> {
            return cap.getTemperature() > 480f;
        }).orElse(false);
    }
}
