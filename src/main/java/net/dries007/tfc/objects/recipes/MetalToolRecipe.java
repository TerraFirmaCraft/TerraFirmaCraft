/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetal;

import static net.dries007.tfc.Constants.MOD_ID;

public class MetalToolRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private static final NonNullList<ItemStack> STICKS = OreDictionary.getOres("stickWood");

    private final Metal.ItemType inp;
    private final Metal.ItemType outp;

    public MetalToolRecipe(Metal.ItemType inp, Metal.ItemType outp)
    {
        this.inp = inp;
        this.outp = outp;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        boolean stick = false;
        boolean toolhead = false;
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            if (STICKS.stream().anyMatch(x -> OreDictionary.itemMatches(x, stack, false)))
            {
                if (stick) return false;
                stick = true;
            }
            else if (stack.getItem() instanceof ItemMetal)
            {
                ItemMetal metal = ((ItemMetal) stack.getItem());
                if (metal.type != inp) return false;
                if (toolhead) return false;
                toolhead = true;
            }
            else
            {
                return false;
            }
        }
        return stick && toolhead;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        boolean stick = false;
        ItemMetal toolhead = null;
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            if (STICKS.stream().anyMatch(x -> OreDictionary.itemMatches(x, stack, false)))
            {
                if (stick) return null;
                stick = true;
            }
            else if (stack.getItem() instanceof ItemMetal)
            {
                ItemMetal metal = ((ItemMetal) stack.getItem());
                if (metal.type != inp) return null;
                if (toolhead != null) return null;
                toolhead = metal;
            }
            else
            {
                return null;
            }
        }
        if (!stick || toolhead == null) return null;
        return new ItemStack(ItemMetal.get(toolhead.metal, outp));
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width * height > 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public String getGroup()
    {
        return MOD_ID + ":metal_" + outp.name().toLowerCase();
    }
}
