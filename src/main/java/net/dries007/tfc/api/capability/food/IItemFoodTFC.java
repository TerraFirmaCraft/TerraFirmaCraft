/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * This should be implemented on any addon-added food items that extend {@link net.minecraft.item.ItemFood}, instead of using {@link net.minecraft.item.Item#initCapabilities(ItemStack, NBTTagCompound)}
 *
 * WHY:
 * - TFC will attach a food capability instance for EVERY {@link net.minecraft.item.ItemFood} subclass.
 * - Capabilities that are attached using initCapabilities are not visible to TFC
 * - When duplicate capabilities get attached, they will decay independently and can lead to stackability issues where invisible capability instances are preventing calls such as {@link CapabilityFood#areStacksStackableExceptCreationDate(ItemStack, ItemStack)}
 */
public interface IItemFoodTFC
{
    /**
     * @return A capability provider which exposes an {@link IFood} capability, e.g. {@link FoodHandler}
     */
    ICapabilityProvider getCustomFoodHandler();
}
