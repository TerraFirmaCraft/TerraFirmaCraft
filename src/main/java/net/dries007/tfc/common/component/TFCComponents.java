/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import java.lang.reflect.Field;
import com.mojang.logging.LogUtils;
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
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.internal.RegistrationEvents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodComponent;
import net.dries007.tfc.common.component.food.FoodDefinition;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.forge.ForgingComponent;
import net.dries007.tfc.common.component.glass.GlassOperations;
import net.dries007.tfc.common.component.heat.HeatComponent;
import net.dries007.tfc.common.component.mold.VesselComponent;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

public final class TFCComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TerraFirmaCraft.MOD_ID);

    private static final Logger LOG = LogUtils.getLogger();

    // Added to all stacks
    public static final Id<ForgingComponent> FORGING = register("forging", ForgingComponent.CODEC, ForgingComponent.STREAM_CODEC);
    public static final Id<ForgingBonus> FORGING_BONUS = register("forging_bonus", ForgingBonus.CODEC, ForgingBonus.STREAM_CODEC);

    // Added to TFC-added blowpipes with glass, via item constructor
    public static final Id<GlassOperations> GLASS = register("glass", GlassOperations.CODEC, GlassOperations.STREAM_CODEC);

    // Added specially to patch of all stacks
    public static final Id<HeatComponent> HEAT = register("heat", HeatComponent.CODEC, HeatComponent.STREAM_CODEC);
    public static final Id<FoodComponent> FOOD = register("food", FoodComponent.CODEC, FoodComponent.STREAM_CODEC);

    // Added only as extra values, with no default 'empty' state
    public static final Id<ItemStackComponent> BOWL = register("bowl", ItemStackComponent.CODEC, ItemStackComponent.STREAM_CODEC);
    public static final Id<IngredientsComponent> INGREDIENTS = register("ingredients", IngredientsComponent.CODEC, IngredientsComponent.STREAM_CODEC);
    public static final Id<ItemStackComponent> DEPOSIT = register("deposit", ItemStackComponent.CODEC, ItemStackComponent.STREAM_CODEC);
    public static final Id<ItemStackComponent> BAIT = register("bait", ItemStackComponent.CODEC, ItemStackComponent.STREAM_CODEC);

    // Added only to Items.EGG, via modify event
    public static final Id<EggComponent> EGG = register("egg", EggComponent.CODEC, EggComponent.STREAM_CODEC);

    public static final Id<FluidComponent> FLUID = register("fluid", FluidComponent.CODEC, FluidComponent.STREAM_CODEC);
    public static final Id<VesselComponent> VESSEL = register("vessel", VesselComponent.CODEC, VesselComponent.STREAM_CODEC);

    /**
     * Modifies the default components of all items. This adds the default components to all items' prototype, so that the default
     * values are never serialized.
     */
    public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event)
    {
        // Add default component values for various TFC components. Forging and forging bonus can be applied to most arbitrary items,
        // so we need to modify all items here
        event.modifyMatching(e -> true, b -> b
            .set(FORGING.get(), ForgingComponent.DEFAULT)
            .set(FORGING_BONUS.get(), ForgingBonus.DEFAULT));

        // Modify the damage value of flint and steel to match other TFC steel items
        event.modify(Items.FLINT_AND_STEEL, b -> b.set(DataComponents.MAX_DAMAGE, TFCTiers.STEEL.getUses()));

        // Modify eggs to add the egg component's default non-fertilized value
        event.modify(Items.EGG, b -> b.set(EGG.get(), EggComponent.DEFAULT));

        // Bump minecarts default stack size up, to make them modifiable
        event.modify(Items.MINECART, b -> b.set(DataComponents.MAX_STACK_SIZE, 64));
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
        ItemStackHooks.ENABLED = true;

        int count = 0;

        // A default instance, which is used in vanilla to signal an item is edible.
        final FoodProperties food = new FoodProperties.Builder().build();

        setAllowComponentModifications(true);
        for (Item item : BuiltInRegistries.ITEM)
        {
            final ItemStack stack = new ItemStack(item);

            final boolean hasFood = item.components().has(DataComponents.FOOD);
            final @Nullable FoodDefinition foodDef = FoodCapability.getDefinition(stack);
            final boolean needsFood = foodDef != null && foodDef.edible();

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
                    count++;
                }
            }
        }
        setAllowComponentModifications(false);
        LOG.info("Modified default components of {} items after resource reload", count);
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private static void modifyDefaultComponentsFrom(Item item, DataComponentPatch patch)
    {
        item.modifyDefaultComponentsFrom(patch);
    }

    private static final Field CAN_MODIFY_COMPONENTS = Helpers.uncheck(() -> {
        @SuppressWarnings("UnstableApiUsage") final Field field = RegistrationEvents.class.getDeclaredField("canModifyComponents");
        field.setAccessible(true);
        return field;
    });

    private static void setAllowComponentModifications(boolean value)
    {
        Helpers.uncheck(() -> CAN_MODIFY_COMPONENTS.set(null, value));
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
