/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
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
        SoundEvent.CODEC.optionalFieldOf("sound", Holder.direct(SoundEvents.BREWING_STAND_BREW)).forGetter(c -> c.sound),
        Codec.STRING.fieldOf("tooltip").forGetter(c -> c.tooltip)
    ).apply(i, BarrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BarrelRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC), c -> c.inputItem,
        SizedFluidIngredient.STREAM_CODEC, c -> c.inputFluid,
        ItemStackProvider.STREAM_CODEC, c -> c.outputItem,
        FluidStack.STREAM_CODEC, c -> c.outputFluid,
        ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), c -> c.sound,
        ByteBufCodecs.STRING_UTF8, c -> c.tooltip,
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
    protected final String tooltip;

    protected BarrelRecipe(BarrelRecipe parent)
    {
        this(parent.inputItem, parent.inputFluid, parent.outputItem, parent.outputFluid, parent.sound, parent.tooltip);
    }

    protected BarrelRecipe(Optional<SizedIngredient> inputItem, SizedFluidIngredient inputFluid, ItemStackProvider outputItem, FluidStack outputFluid, Holder<SoundEvent> sound, String tooltip)
    {
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.sound = sound;
        this.tooltip = tooltip;
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

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
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
        return inputItem.orElseThrow();
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

    public MutableComponent getTranslationComponent()
    {
        return Component.translatable("tfc.recipe.barrel." + tooltip);
    }
}
