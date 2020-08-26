package net.dries007.tfc.objects.items;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemStickBundle extends ItemTFC
{
    public ItemStickBundle()
    {
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "log", "wood");
        OreDictionaryHelper.register(this,"stick", "bundle");

    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.VERY_LARGE;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }
}
