/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

@SuppressWarnings("WeakerAccess")
public class ItemOreTFC extends ItemTFC implements IMetalItem
{
    private static final Map<Ore, ItemOreTFC> MAP = new HashMap<>();

    public static ItemOreTFC get(Ore ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(Ore ore, Ore.Grade grade, int amount)
    {
        return new ItemStack(MAP.get(ore), amount, ore.isGraded() ? grade.getMeta() : 0);
    }

    public static ItemStack get(Ore ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    public final Ore ore;

    public ItemOreTFC(Ore ore)
    {
        this.ore = ore;
        if (MAP.put(ore, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        if (ore.getMetal() != null)
        {
            setHasSubtypes(true);

            for (Ore.Grade grade : Ore.Grade.values())
            {
                //noinspection ConstantConditions
                OreDictionaryHelper.registerMeta(this, grade.getMeta(), "ore", ore.getMetal().getRegistryName().getPath(), grade);
                if (ore.getMetal() == Metal.WROUGHT_IRON && ConfigTFC.GENERAL.oreDictIron)
                {
                    OreDictionaryHelper.registerMeta(this, grade.getMeta(), "ore", "iron", grade);
                }
            }
        }
        else // Mineral
        {
            //noinspection ConstantConditions
            String oreName = ore.getRegistryName().getPath();
            switch (oreName)
            {
                case "lapis_lazuli":
                    OreDictionaryHelper.register(this, "gem", "lapis");
                    break;
                case "bituminous_coal":
                    OreDictionaryHelper.register(this, "gem", "coal");
                    break;
                case "lignite":
                    OreDictionaryHelper.register(this, "gem", "lignite");
                    break;
                default:
                    OreDictionaryHelper.register(this, "gem", ore);
            }
        }
    }

    @Nonnull
    public Ore.Grade getGradeFromStack(ItemStack stack)
    {
        return Ore.Grade.valueOf(stack.getItemDamage());
    }

    @Override
    @Nonnull
    public String getTranslationKey(ItemStack stack)
    {
        Ore.Grade grade = getGradeFromStack(stack);
        if (grade == Ore.Grade.NORMAL) return super.getTranslationKey(stack);
        return super.getTranslationKey(stack) + "." + grade.getName();
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            if (ore.isGraded())
            {
                for (Ore.Grade grade : Ore.Grade.values())
                {
                    items.add(new ItemStack(this, 1, grade.getMeta()));
                }
            }
            else
            {
                items.add(new ItemStack(this));
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return ore.getMetal() != null ? new ItemHeatHandler(nbt, ore.getMetal().getSpecificHeat(), ore.getMetal().getMeltTemp()) : null;
    }

    @Override
    @Nullable
    public Metal getMetal(ItemStack stack)
    {
        return ore.getMetal();
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return getGradeFromStack(stack).getSmeltAmount();
    }

    @Override
    public boolean canMelt(ItemStack stack)
    {
        return ore.canMelt();
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM;
    }
}
