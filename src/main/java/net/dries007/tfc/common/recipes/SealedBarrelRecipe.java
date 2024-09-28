/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import com.google.common.collect.BiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.recipes.input.BarrelInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class SealedBarrelRecipe extends BarrelRecipe
{
    public static final MapCodec<SealedBarrelRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BarrelRecipe.CODEC.forGetter(c -> c),
        Codec.INT.fieldOf("duration").forGetter(c -> c.duration),
        ItemStackProvider.CODEC.optionalFieldOf("on_seal").forGetter(c -> c.onSeal),
        ItemStackProvider.CODEC.optionalFieldOf("on_unseal").forGetter(c -> c.onUnseal)
    ).apply(i, SealedBarrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SealedBarrelRecipe> STREAM_CODEC = StreamCodec.composite(
        BarrelRecipe.STREAM_CODEC, c -> c,
        ByteBufCodecs.VAR_INT, c -> c.duration,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.onSeal,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.onUnseal,
        SealedBarrelRecipe::new
    );

    private final int duration;
    private final Optional<ItemStackProvider> onSeal;
    private final Optional<ItemStackProvider> onUnseal;

    public SealedBarrelRecipe(BarrelRecipe parent, int duration, Optional<ItemStackProvider> onSeal, Optional<ItemStackProvider> onUnseal)
    {
        super(parent);

        this.duration = duration;
        this.onSeal = onSeal;
        this.onUnseal = onUnseal;
    }

    @Override
    public boolean matches(BarrelInventory container)
    {
        return super.matches(container) && moreFluidThanItems(container);
    }

    /**
     * Sealed <strong>infinite</strong> recipes should only match if they have more fluid than items. This is done because {@code onSeal},
     * and {@code onUnseal}, by definition, operate independently of stack size, and since infinite recipes have no other outputs, they
     * must satisfy the fluid requirement in order to be valid.
     * <p>
     * N.B. This is the <em>opposite</em> ratio requirement of instant barrel recipes, which is kind of poetic.
     */
    private boolean moreFluidThanItems(BarrelInventory input)
    {
        return !isInfinite()
            || inputItem.isEmpty()
            || input.getFluidInTank(0).getAmount() / inputFluid.amount() >= input.getStackInSlot(BarrelBlockEntity.SLOT_ITEM).getCount() / inputItem.get().count();
    }

    public int getDuration()
    {
        return duration;
    }

    public boolean isInfinite()
    {
        return duration <= 0;
    }

    @Nullable
    @Contract(pure = true)
    public ItemStackProvider onSeal()
    {
        return onSeal.orElse(null);
    }

    @Nullable
    @Contract(pure = true)
    public ItemStackProvider onUnseal()
    {
        return onUnseal.orElse(null);
    }

    public void onSealed(BarrelInventory inventory)
    {
        onSeal.ifPresent(onSeal ->
            inventory.whileMutable(() -> {
                final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
                inventory.insertItem(BarrelBlockEntity.SLOT_ITEM, onSeal.getStack(stack), false);
            }));
    }

    public void onUnsealed(BarrelInventory inventory)
    {
        onUnseal.ifPresent(onUnseal ->
            inventory.whileMutable(() -> {
                final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
                inventory.insertItem(BarrelBlockEntity.SLOT_ITEM, onUnseal.getStack(stack), false);
            }));
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BARREL_SEALED.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SEALED_BARREL.get();
    }
}
