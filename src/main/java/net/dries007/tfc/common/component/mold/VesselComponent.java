/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.mold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.CachedMut;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.util.FluidAlloy;
import net.dries007.tfc.util.Helpers;

/**
 * A component attached to vessels, which manage an internal tri-state between inventory, fluid (alloy) inventory,
 * and heat. It also stores a recipe cache, which is invalidated on modification to the item inventory.
 */
public record VesselComponent(
    List<ItemStack> itemContent,
    List<CachedMut<HeatingRecipe>> cachedRecipes,
    FluidAlloy fluidContent
)
{
    public static final int SLOTS = 4;
    public static final VesselComponent EMPTY = new VesselComponent(
        Collections.nCopies(SLOTS, ItemStack.EMPTY),
        Collections.nCopies(SLOTS, CachedMut.empty()),
        FluidAlloy.empty()
    );

    public static final Codec<VesselComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        ItemStack.OPTIONAL_CODEC.listOf(SLOTS, SLOTS).fieldOf("items").forGetter(c -> c.itemContent),
        FluidAlloy.CODEC.fieldOf("fluid").forGetter(c -> c.fluidContent)
    ).apply(i, VesselComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VesselComponent> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(SLOTS)), c -> c.itemContent,
        FluidAlloy.STREAM_CODEC, c -> c.fluidContent,
        VesselComponent::new
    );

    private VesselComponent(List<ItemStack> itemContent, FluidAlloy fluidContent)
    {
        this(itemContent, Helpers.immutableCopies(SLOTS, CachedMut::unloaded), fluidContent);
    }

    /**
     * Creates a copy of this vessel, which has interior mutability, allowing modification. This is mildly expensive, and in the interest
     * of preventing unnecessary copies, we don't necessarily re-copy this as immutable after the fact.
     * @return A new {@link VesselComponent} with the same values, which can be modified.
     */
    VesselComponent copyMut()
    {
        return new VesselComponent(new ArrayList<>(itemContent), new ArrayList<>(cachedRecipes), fluidContent.copy());
    }

    VesselComponent with(int slot, ItemStack stack)
    {
        return new VesselComponent(
            Helpers.immutableSwap(itemContent, stack, slot), // Copy and swap in the target slot
            Helpers.immutableSwap(cachedRecipes, CachedMut.unloaded(), slot), // Copy and invalidate in the target slot
            fluidContent // No need for a copy, as we aren't invalidating anything
        );
    }

    VesselComponent with(FluidAlloy fluidContent)
    {
        return new VesselComponent(itemContent, cachedRecipes, fluidContent);
    }
}
