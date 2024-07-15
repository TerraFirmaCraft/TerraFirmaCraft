/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import java.lang.reflect.Field;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.internal.RegistrationEvents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.capabilities.food.BowlComponent;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodComponent;
import net.dries007.tfc.common.capabilities.food.FoodDefinition;
import net.dries007.tfc.common.capabilities.food.IngredientsComponent;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatComponent;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.forge.ForgingComponent;
import net.dries007.tfc.common.component.glass.GlassOperations;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

public final class TFCComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TerraFirmaCraft.MOD_ID);

    public static final Id<ForgingComponent> FORGING = register("forging", ForgingComponent.CODEC, ForgingComponent.STREAM_CODEC);
    public static final Id<ForgingBonus> FORGING_BONUS = register("forging_bonus", ForgingBonus.CODEC, ForgingBonus.STREAM_CODEC);

    public static final Id<GlassOperations> GLASS = register("glass", GlassOperations.CODEC, GlassOperations.STREAM_CODEC);

    public static final Id<HeatComponent> HEAT = register("heat", HeatComponent.CODEC, HeatComponent.STREAM_CODEC);
    public static final Id<FoodComponent> FOOD = register("food", FoodComponent.CODEC, FoodComponent.STREAM_CODEC);

    public static final Id<BowlComponent> BOWL = register("bowl", BowlComponent.CODEC, BowlComponent.STREAM_CODEC);
    public static final Id<IngredientsComponent> INGREDIENTS = register("ingredients", IngredientsComponent.CODEC, IngredientsComponent.STREAM_CODEC);
    public static final Id<PannableComponent> PANNABLE = register("pan", PannableComponent.CODEC, PannableComponent.STREAM_CODEC);
    public static final Id<BaitComponent> BAIT = register("bait", BaitComponent.CODEC, BaitComponent.STREAM_CODEC);

    public static final Id<EggComponent> EGG = register("egg", EggComponent.CODEC, EggComponent.STREAM_CODEC);

    /**
     * Modifies the default components of all items. This adds the default components to all items' prototype, so that the default
     * values are never serialized.
     */
    public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event)
    {
        event.modifyMatching(e -> true, builder -> builder
            .set(FORGING.get(), ForgingComponent.DEFAULT)
            .set(FORGING_BONUS.get(), ForgingBonus.DEFAULT)
            .set(GLASS.get(), GlassOperations.DEFAULT)
        );
    }

    /**
     * Modify the default food, and stack size components after a resource reload. This is not allowed in Neo because it will not modify
     * the prototype maps of existing item stacks. In TFC, this is a reasonable tradeoff to make - we are only modifying stack size and presence
     * of food, and in normal gameplay, these will only miss stacks created before the initial resource reload, which in TFC of the past, would not
     * have had correct food/size properties anyway.
     * <p>
     * All this to say, we use the Neo provided modification event infrastructure, with a little bit of reflection to avoid the safeguards Neo
     * puts in place to prevent exactly what we are doing.
     *
     * @see ModifyDefaultComponentsEvent
     */
    public static void onModifyDefaultComponentsAfterResourceReload()
    {
        // A default instance, which is used in vanilla to signal an item is edible.
        final FoodProperties food = new FoodProperties.Builder().build();

        setAllowComponentModifications(true);
        for (Item item : BuiltInRegistries.ITEM)
        {
            final ItemStack stack = new ItemStack(item);

            final boolean hasFood = item.components().has(DataComponents.FOOD);
            final boolean needsFood = FoodCapability.getDefinition(stack) != null;

            final int prevSize = item.components().getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
            final int requestedSize = ItemSizeManager.getDefinition(stack).weight().stackSize;

            // Only perform the modification if we want to do any modifications, to avoid otherwise expensive operations
            // Only perform item size modifications if the original item doesn't request "1" stack size
            if (hasFood != needsFood || (prevSize != 1 && prevSize != requestedSize))
            {
                final DataComponentPatch.Builder builder = DataComponentPatch.builder();

                if (hasFood) builder.remove(DataComponents.FOOD);
                if (needsFood) builder.set(DataComponents.FOOD, food);
                if (prevSize != 1 && prevSize != requestedSize) builder.set(DataComponents.MAX_STACK_SIZE, requestedSize);

                final DataComponentPatch patch = builder.build();
                if (!patch.isEmpty())
                {
                    modifyDefaultComponentsFrom(item, patch);
                }
            }
        }
        setAllowComponentModifications(false);
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private static void modifyDefaultComponentsFrom(Item item, DataComponentPatch patch)
    {
        item.modifyDefaultComponentsFrom(patch);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void setAllowComponentModifications(boolean value)
    {
        try
        {
            final Field field = RegistrationEvents.class.getDeclaredField("canModifyComponents");
            field.setAccessible(true);
            field.set(null, value);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            Helpers.throwAsUnchecked(e);
        }
    }

    /**
     * Modifies components attached to an item stack on the creation of the item stack. This is done as some components <strong>need</strong> a reference
     * to the owning object to know if it should attach or not. Note that these cannot be added as default components, as the default value would then
     * not be able to be item-stack-agnostic.
     * <p>
     * This mainly occurs from two places:
     * <ul>
     *     <li>When stacks are created from serialization - the components may or may not be present, but will be missing a stack reference. We update them in this case</li>
     *     <li>When the stack is copied - we just validate that the component is present, and then do no further modifications</li>
     * </ul>
     * @param stack A new item stack, freshly constructed
     */
    public static void onModifyItemStackComponents(ItemStack stack)
    {
        final @Nullable HeatComponent heat = stack.get(HEAT);
        if (heat != null)
        {
            // A heat component already exists, but we might need to populate the heat definition
            // We delay actually querying the definition here, as it might be (1) already present, i.e. from a `copy()`,
            // or (2) a builtin component that has and needs no definition
            heat.capture(stack);
        }
        else
        {
            // No heat component exists, so query for a definition and if we find one, attach a component
            final @Nullable HeatDefinition def = HeatCapability.getDefinition(stack);
            if (def != null)
            {
                stack.set(HEAT, HeatComponent.with(def));
            }
        }

        // The semantics of food components is identical to heat components above
        final @Nullable FoodComponent food = stack.get(FOOD);
        if (food != null)
        {
            food.capture(stack);
        }
        else
        {
            final @Nullable FoodDefinition def = FoodCapability.getDefinition(stack);
            if (def != null)
            {
                stack.set(FOOD, FoodComponent.with(def));
            }
        }
    }

    private static <T> Id<T> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new Id<>(COMPONENTS.register(name, () -> new DataComponentType.Builder<T>()
            .persistent(codec)
            .networkSynchronized(streamCodec)
            .build()));
    }

    public record Id<T>(DeferredHolder<DataComponentType<?>, DataComponentType<T>> holder)
        implements RegistryHolder<DataComponentType<?>, DataComponentType<T>> {}
}
