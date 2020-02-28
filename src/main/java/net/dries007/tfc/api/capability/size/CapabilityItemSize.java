/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.size;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockLadder;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.api.capability.ItemStickCapability;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.Helpers;

public final class CapabilityItemSize
{
    @CapabilityInject(IItemSize.class)
    public static final Capability<IItemSize> ITEM_SIZE_CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(TerraFirmaCraft.MOD_ID, "item_size");

    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_ITEMS = new LinkedHashMap<>(); //Used inside CT, set custom IItemSize for items outside TFC

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IItemSize.class, new DumbStorage<>(), ItemSizeHandler::new);

        // Add hardcoded size values for vanilla items
        CUSTOM_ITEMS.put(IIngredient.of(Items.COAL), () -> new ItemSizeHandler(Size.SMALL, Weight.MEDIUM, true));
        CUSTOM_ITEMS.put(IIngredient.of(Items.STICK), ItemStickCapability::new);
        CUSTOM_ITEMS.put(IIngredient.of(Items.CLAY_BALL), () -> new ItemSizeHandler(Size.SMALL, Weight.LIGHT, true));
        CUSTOM_ITEMS.put(IIngredient.of(Items.BED), () -> new ItemSizeHandler(Size.LARGE, Weight.MEDIUM, false));
        CUSTOM_ITEMS.put(IIngredient.of(Items.MINECART), () -> new ItemSizeHandler(Size.VERY_LARGE, Weight.HEAVY, false));
        CUSTOM_ITEMS.put(IIngredient.of(Items.ARMOR_STAND), () -> new ItemSizeHandler(Size.LARGE, Weight.LIGHT, true));
        CUSTOM_ITEMS.put(IIngredient.of(Items.CAULDRON), () -> new ItemSizeHandler(Size.SMALL, Weight.MEDIUM, true));
        CUSTOM_ITEMS.put(IIngredient.of(Blocks.TRIPWIRE_HOOK), () -> new ItemSizeHandler(Size.VERY_SMALL, Weight.LIGHT, true));
    }

    /**
     * Checks if an item is of a given size and weight
     */
    public static boolean checkItemSize(ItemStack stack, Size size, Weight weight)
    {
        IItemSize cap = getIItemSize(stack);
        if (cap != null)
        {
            return cap.getWeight(stack) == weight && cap.getSize(stack) == size;
        }
        return false;
    }

    /**
     * Gets the IItemSize instance from an itemstack, either via capability or via interface
     *
     * @param stack The stack
     * @return The IItemSize if it exists, or null if it doesn't
     */
    @Nullable
    public static IItemSize getIItemSize(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof IItemSize)
            {
                return (IItemSize) stack.getItem();
            }
            else if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IItemSize)
            {
                return (IItemSize) ((ItemBlock) stack.getItem()).getBlock();
            }
            return stack.getCapability(ITEM_SIZE_CAPABILITY, null);
        }
        return null;
    }

    @Nonnull
    public static ICapabilityProvider getCustomSize(ItemStack stack)
    {
        for (Map.Entry<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> entry : CUSTOM_ITEMS.entrySet())
        {
            if (entry.getKey().testIgnoreCount(stack))
            {
                return entry.getValue().get();
            }
        }
        // Check for generic item types
        Item item = stack.getItem();
        if (item instanceof ItemTool)
        {
            return new ItemSizeHandler(Size.LARGE, Weight.MEDIUM, true);
        }
        else if (item instanceof ItemArmor)
        {
            return new ItemSizeHandler(Size.LARGE, Weight.HEAVY, true);
        }
        else if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof BlockLadder)
        {
            return new ItemSizeHandler(Size.SMALL, Weight.LIGHT, true);
        }
        else if (item instanceof ItemBlock)
        {
            return new ItemSizeHandler(Size.SMALL, Weight.MEDIUM, true); // does not match classic, needed to fix #561
        }
        else
        {
            return new ItemSizeHandler(Size.VERY_SMALL, Weight.LIGHT, true);
        }
    }
}
