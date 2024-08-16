/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.TFCIngredients;
import net.dries007.tfc.common.recipes.input.BarrelInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;


public class BarrelRecipe implements INoopInputRecipe, IRecipePredicate<BarrelInventory>
{
    public static final MapCodec<BarrelRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        SizedIngredient.FLAT_CODEC.optionalFieldOf("input_item").forGetter(c -> c.inputItem),
        SizedFluidIngredient.FLAT_CODEC.fieldOf("input_fluid").forGetter(c -> c.inputFluid),
        ItemStackProvider.CODEC.optionalFieldOf("output_item", ItemStackProvider.empty()).forGetter(c -> c.outputItem),
        FluidStack.CODEC.optionalFieldOf("output_fluid", FluidStack.EMPTY).forGetter(c -> c.outputFluid),
        SoundEvent.CODEC.optionalFieldOf("sound", Holder.direct(SoundEvents.BREWING_STAND_BREW)).forGetter(c -> c.sound)
    ).apply(i, BarrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BarrelRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC), c -> c.inputItem,
        SizedFluidIngredient.STREAM_CODEC, c -> c.inputFluid,
        ItemStackProvider.STREAM_CODEC, c -> c.outputItem,
        FluidStack.OPTIONAL_STREAM_CODEC, c -> c.outputFluid,
        ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), c -> c.sound,
        BarrelRecipe::new
    );

    @Nullable
    public static <B extends BarrelRecipe> RecipeHolder<B> get(Level level, Supplier<RecipeType<B>> type, BarrelInventory input)
    {
        return RecipeHelpers.getHolder(level, type, input);
    }

    protected final Optional<SizedIngredient> inputItem;
    protected final SizedFluidIngredient inputFluid;
    protected final ItemStackProvider outputItem;
    protected final FluidStack outputFluid;
    protected final Holder<SoundEvent> sound;

    protected BarrelRecipe(BarrelRecipe parent)
    {
        this(parent.inputItem, parent.inputFluid, parent.outputItem, parent.outputFluid, parent.sound);
    }

    protected BarrelRecipe(Optional<SizedIngredient> inputItem, SizedFluidIngredient inputFluid, ItemStackProvider outputItem, FluidStack outputFluid, Holder<SoundEvent> sound)
    {
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.sound = sound;
    }

    public boolean matches(BarrelInventory input)
    {
        return (inputItem.isEmpty() || inputItem.get().test(input.getStackInSlot(BarrelBlockEntity.SLOT_ITEM)))
            && inputFluid.test(input.getFluidInTank(0));
    }

    public void assembleOutputs(BarrelInventory inventory)
    {
        // Require the inventory to be mutable, as we use insert/extract methods, but will expect it to be modifiable despite being sealed.
        inventory.whileMutable(() -> {
            // Remove all inputs
            final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
            final FluidStack fluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

            // Calculate the multiplier in use for this recipe
            int multiplier;
            if (inputItem.isEmpty())
            {
                multiplier = fluid.getAmount() / inputFluid.amount();
            }
            else
            {
                multiplier = Math.min(fluid.getAmount() / inputFluid.amount(), stack.getCount() / inputItem.get().count());
            }

            // Trim multiplier to a maximum fluid capacity of output
            if (!outputFluid.isEmpty())
            {
                int capacity = TFCConfig.SERVER.barrelCapacity.get();
                if (FluidStack.isSameFluidSameComponents(outputFluid, fluid))
                {
                    capacity -= fluid.getAmount();
                }
                int maxMultiplier = capacity / outputFluid.getAmount();
                multiplier = Math.min(multiplier, maxMultiplier);
            }

            // Output items
            // All output items, and then remaining input items, get inserted into the output overflow
            final ItemStack outputItem = this.outputItem.getSingleStack(stack);
            if (!outputItem.isEmpty())
            {
                Helpers.consumeInStackSizeIncrements(outputItem, multiplier * outputItem.getCount(), inventory::insertItemWithOverflow);
            }
            final int remainingItemCount = stack.getCount() - multiplier * inputItem.map(SizedIngredient::count).orElse(0);
            if (remainingItemCount > 0)
            {
                final ItemStack remainingStack = stack.copy();
                remainingStack.setCount(remainingItemCount);
                inventory.insertItemWithOverflow(remainingStack);
            }

            // Output fluid
            // If there's no output fluid, keep as much of the input as possible
            // If there is an output fluid, excess input is voided
            final FluidStack outputFluid = this.outputFluid.copy();
            if (outputFluid.isEmpty())
            {
                // Try and keep as much of the original input as possible
                final int retainAmount = fluid.getAmount() - (multiplier * this.inputFluid.amount());
                if (retainAmount > 0)
                {
                    final FluidStack retainedFluid = fluid.copy();
                    retainedFluid.setAmount(retainAmount);
                    inventory.fill(retainedFluid, IFluidHandler.FluidAction.EXECUTE);
                }
            }
            else
            {
                int amount = outputFluid.getAmount() * multiplier;
                if (FluidStack.isSameFluidSameComponents(outputFluid, fluid))
                {
                    amount = amount + fluid.getAmount();
                }
                outputFluid.setAmount(Math.min(TFCConfig.SERVER.barrelCapacity.get(), amount));
                inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }

    public ItemStack getResultItem()
    {
        return outputItem.getEmptyStack();
    }

    @Override
    public ItemStack getResultItem(@Nullable HolderLookup.Provider provider)
    {
        return outputItem.getEmptyStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeType<?> getType()
    {
        throw new UnsupportedOperationException();
    }

    public SizedIngredient getInputItem()
    {
        return inputItem.orElse(TFCIngredients.EMPTY_ITEM);
    }

    public ItemStackProvider getOutputItem()
    {
        return outputItem;
    }

    public SizedFluidIngredient getInputFluid()
    {
        return inputFluid;
    }

    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    public SoundEvent getCompleteSound()
    {
        return sound.value();
    }

    /**
     * A builder capable of building all types of barrel recipes currently implemented by TFC
     */
    public static class Builder
    {
        private final Consumer<BarrelRecipe> onFinish;
        private Optional<SizedIngredient> inputItem = Optional.empty();
        private @Nullable SizedFluidIngredient inputFluid = null;
        private ItemStackProvider outputItem = ItemStackProvider.empty();
        private FluidStack outputFluid = FluidStack.EMPTY;
        private Holder<SoundEvent> sound = Holder.direct(SoundEvents.BREWING_STAND_BREW);

        public Builder(Consumer<BarrelRecipe> onFinish)
        {
            this.onFinish = onFinish;
        }

        public Builder input(ItemLike item) { return input(SizedIngredient.of(item, 1)); }
        public Builder input(TagKey<Item> item) { return input(SizedIngredient.of(item, 1)); }
        public Builder input(Ingredient item) { return input(new SizedIngredient(item, 1)); }
        public Builder input(SizedIngredient item)
        {
            this.inputItem = Optional.of(item);
            return this;
        }

        public Builder input(TagKey<Fluid> fluid, int amount) { return input(SizedFluidIngredient.of(fluid, amount)); }
        public Builder input(Fluid fluid, int amount)  { return input(SizedFluidIngredient.of(fluid, amount)); }
        public Builder input(SizedFluidIngredient fluid)
        {
            this.inputFluid = fluid;
            return this;
        }

        public Builder output(ItemLike item) { return output(ItemStackProvider.of(item)); }
        public Builder output(ItemStackProvider item)
        {
            this.outputItem = item;
            return this;
        }

        public Builder output(Fluid fluid, int amount) { return output(new FluidStack(fluid, amount)); }
        public Builder output(FluidStack fluid)
        {
            this.outputFluid = fluid;
            return this;
        }

        public Builder sound(SoundEvent sound)
        {
            this.sound = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);
            return this;
        }

        public void instant()
        {
            onFinish.accept(new InstantBarrelRecipe(parent()));
        }

        public void instantOnAdd(Fluid fluid) { instantOnAdd(SizedFluidIngredient.of(fluid, 1)); }
        public void instantOnAdd(SizedFluidIngredient addedFluid)
        {
            onFinish.accept(new InstantFluidBarrelRecipe(Objects.requireNonNull(inputFluid, "Missing input fluid"), addedFluid, outputFluid, sound));
        }

        public void sealed(int duration)
        {
            onFinish.accept(new SealedBarrelRecipe(parent(), duration, Optional.empty(), Optional.empty()));
        }

        public void sealed(ItemStackProvider onSeal, ItemStackProvider onUnseal)
        {
            onFinish.accept(new SealedBarrelRecipe(parent(), -1, Optional.of(onSeal), Optional.of(onUnseal)));
        }

        private BarrelRecipe parent()
        {
            return new BarrelRecipe(inputItem, Objects.requireNonNull(inputFluid, "Missing input fluid"), outputItem, outputFluid, sound);
        }
    }
}
