/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.animals.ProducingMammalConfig;
import net.dries007.tfc.util.events.AnimalProductEvent;

// some implementation notes for me to come back to and delete later
// sheep (ie, color-able wooly animals) should extend this and do their own handling rather than all wooly animals secretly having colors
// eating grass should be a property in TFCAnimal, NOT just sheep. And we need to study how that exactly will work
public abstract class WoolyAnimal extends ProducingMammal implements IShearable
{
    public WoolyAnimal(EntityType<? extends WoolyAnimal> animal, Level level, TFCSounds.EntityId sounds, ProducingMammalConfig config)
    {
        super(animal, level, sounds, config);
    }

    @Override
    public boolean isShearable(@Nullable Player player, ItemStack item, Level level, BlockPos pos)
    {
        return isReadyForAnimalProduct();
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos)
    {
        setProductsCooldown();
        playSound(SoundEvents.SHEEP_SHEAR, 1.0f, 1.0f);

        AnimalProductEvent event = new AnimalProductEvent(level, pos, player, this, getWoolItem(), item, 1);
        if (!NeoForge.EVENT_BUS.post(event).isCanceled())
        {
            addUses(event.getUses());
        }
        return List.of(event.getProduct());
    }

    @Override
    public boolean hasProduct()
    {
        return getProducedTick() <= 0 || getProductsCooldown() <= 0 && getAgeType() == Age.ADULT;
    }

    public ItemStack getWoolItem()
    {
        final int amount = getFamiliarity() > 0.99f ? 2 : 1;
        return new ItemStack(TFCItems.WOOL.get(), amount);
    }

    @Override
    public MutableComponent getProductReadyName()
    {
        return Component.translatable("tfc.jade.product.wool");
    }
}
