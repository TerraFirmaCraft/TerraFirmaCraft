/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.items.TFCItems;

public class Bluegill extends TFCSalmon
{
    public Bluegill(EntityType<? extends TFCSalmon> type, Level level)
    {
        super(type, level);
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(TFCItems.BLUEGILL_BUCKET.get());
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return TFCSounds.BLUEGILL.flop().get();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return TFCSounds.BLUEGILL.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return TFCSounds.BLUEGILL.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSounds.BLUEGILL.death().get();
    }
}
