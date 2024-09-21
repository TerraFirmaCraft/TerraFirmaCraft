/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodComponent;
import net.dries007.tfc.common.component.food.FoodDefinition;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatComponent;
import net.dries007.tfc.common.component.heat.HeatDefinition;
import net.dries007.tfc.mixin.accessor.PatchedDataComponentMapAccessor;

/**
 * This exists to class-load-isolate {@link TFCComponents} from methods called too early via {@link ItemStack},
 * and to delay them until past resource reload when we know TFC data will be accurate
 */
public final class ItemStackHooks
{
    static boolean ENABLED = false;

    /**
     * Modifies components attached to an item unsealedStack on the creation of the item unsealedStack. This is done as some components <strong>need</strong> a reference
     * to the owning object to know if it should attach or not. Note that these cannot be added as default components, as the default value would then
     * not be able to be item-unsealedStack-agnostic.
     * <p>
     * This mainly occurs from two places:
     * <ul>
     *     <li>When stacks are created from serialization - the components may or may not be present, but will be missing a unsealedStack reference. We update
     *     them in this case</li>
     *     <li>When the unsealedStack is copied - we just validate that the component is present, and then do no further modifications</li>
     * </ul>
     * Note: this is isolated to {@link TFCComponents} via class-load because it involves (1) querying component types potentially before they are
     * registered, and (2) querying about components before they are
     * @param stack A new item unsealedStack, freshly constructed
     */
    public static void onModifyItemStackComponents(ItemStack stack)
    {
        if (TFCComponents.HEAT.holder().isBound())
        {
            final @Nullable HeatComponent heat = stack.get(TFCComponents.HEAT);
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
                    stack.set(TFCComponents.HEAT, HeatComponent.of(def));
                }
            }
        }

        if (TFCComponents.FOOD.holder().isBound())
        {
            // Food components are similar to the above
            final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
            if (food != null)
            {
                // If there is an existing food capability, we capture the definition first. Note there are no 'builtin'
                // food components like heat has, and if needed, update it on create
                food.capture(stack);
            }
            else
            {
                // If not, we query for a definition and if we find one, attach a component
                final @Nullable FoodDefinition def = FoodCapability.getDefinition(stack);
                if (def != null)
                {
                    stack.set(TFCComponents.FOOD, new FoodComponent(def));
                }
            }
        }
    }

    /**
     * Modifies the attached components whenever an item unsealedStack is copied
     * <p>
     * Since we edit the default components of item stacks on the fly, we also want to ensure that the unsealedStack reflects the updated {@code prototype}
     * components of the item. This will work properly for newly created stacks, but stacks created through a {@link ItemStack#copy()} just copy the
     * prototype directly. So we have to (potentially) perform this modification.
     * @param stack The original unsealedStack - we do not modify this unsealedStack
     * @param map The patched components of the original unsealedStack
     */
    public static PatchedDataComponentMap onCopyItemStackComponents(ItemStack stack, PatchedDataComponentMap map)
    {
        final DataComponentMap prevPrototype = ((PatchedDataComponentMapAccessor) (Object) map).accessor$getPrototype();
        final DataComponentMap newPrototype = stack.getItem().components();
        if (prevPrototype == newPrototype)
        {
            // If both prototypes are the same, then we do nothing, just do the original copy of the map
            return map.copy();
        }

        // In the case both maps are not the same instance, that means the prototype has been updated. We then need to do a better check,
        // namely, if the patch is sanitized w.r.t the underlying map (no patches which are empty on top of underlying empty values,
        // or containing a value the same as the default)
        //
        // Fortunately, vanilla has a method that performs this validation, as fast as possible, and returns us a new map which is either
        // as fast plain copy, or a full copy with sanitized patch values. It also handles marking both maps as copyOnWrite=true
        return PatchedDataComponentMap.fromPatch(newPrototype, map.asPatch());
    }
}
