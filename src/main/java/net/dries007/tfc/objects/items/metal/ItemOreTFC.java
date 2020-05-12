/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.Helpers;
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
                if (ore.getMetal() == Metal.WROUGHT_IRON && ConfigTFC.General.MISC.dictionaryIron)
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
    public String getTranslationKey(@Nonnull ItemStack stack)
    {
        Ore.Grade grade = getGradeFromStack(stack);
        if (grade == Ore.Grade.NORMAL) return super.getTranslationKey(stack);
        return super.getTranslationKey(stack) + "." + grade.getName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn)
    {
        Metal metal = getMetal(stack);
        if (metal != null)
        {
            int smeltAmount = this.getSmeltAmount(stack);
            switch (ConfigTFC.Client.TOOLTIP.oreTooltipMode)
            {
                case HIDE:
                    break;
                case UNIT_ONLY:
                    // Like classic, "Metal: xx units"
                    String info = String.format("%s: %s", I18n.format(Helpers.getTypeName(metal)), I18n.format("tfc.tooltip.units", smeltAmount));
                    tooltip.add(info);
                    break;
                case TOTAL_ONLY:
                    // not like Classic, "Metal: xx total units" Adds the whole stacks worth up.
                    String stackTotal = String.format("%s: %s", I18n.format(Helpers.getTypeName(metal)), I18n.format("tfc.tooltip.units.total", smeltAmount * stack.getCount()));
                    tooltip.add(stackTotal);
                    break;
                case ALL_INFO:
                    // All info: "Metal: xx units / xx total"
                    String infoTotal;
                    if (stack.getCount() > 1)
                    {
                        infoTotal = String.format("%s: %s", I18n.format(Helpers.getTypeName(metal)), I18n.format("tfc.tooltip.units.info_total", smeltAmount, smeltAmount * stack.getCount()));
                    }
                    else
                    {
                        infoTotal = String.format("%s: %s", I18n.format(Helpers.getTypeName(metal)), I18n.format("tfc.tooltip.units", smeltAmount));
                    }
                    tooltip.add(infoTotal);
            }
        }
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
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
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
        return Size.SMALL; // Fits in Small Vessels
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM; // Stacksize = 16
    }
}
