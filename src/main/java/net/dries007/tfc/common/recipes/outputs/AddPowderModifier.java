package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.glass.GlassOperation;
import net.dries007.tfc.common.capabilities.glass.GlassWorkData;
import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public enum AddPowderModifier implements ItemStackModifier.SingleInstance<AddPowderModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        final CraftingContainer inv = RecipeHelpers.getCraftingContainer();
        if (inv != null)
        {
            for (int i = 0; i < inv.getContainerSize(); i++)
            {
                final ItemStack item = inv.getItem(i);
                final GlassOperation op = GlassOperation.getByPowder(item);
                if (op != null)
                {
                    GlassWorkData.apply(stack, op);
                    return stack;
                }
            }
        }
        return stack;
    }

    @Override
    public AddPowderModifier instance()
    {
        return INSTANCE;
    }
}
