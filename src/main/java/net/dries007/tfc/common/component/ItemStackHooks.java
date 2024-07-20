/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;

/**
 * This exists to class-load-isolate {@link TFCComponents} from methods called too early via {@link ItemStack},
 * and to delay them until past resource reload when we know TFC data will be accurate
 */
public final class ItemStackHooks
{
    static boolean ENABLED = false;

    public static void onModifyItemStackComponents(ItemStack stack)
    {
        if (ENABLED) TFCComponents.onModifyItemStackComponents(stack);
    }

    public static PatchedDataComponentMap onCopyItemStackComponents(ItemStack stack, PatchedDataComponentMap map)
    {
        return ENABLED ? TFCComponents.onCopyItemStackComponents(stack, map) : map;
    }
}
