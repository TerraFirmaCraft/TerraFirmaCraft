/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemSmallOre extends ItemTFC implements IMetalObject
{
    private static final Map<Ore, ItemSmallOre> MAP = new HashMap<>();

    public static ItemSmallOre get(Ore ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(Ore ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    private final Ore ore;

    public ItemSmallOre(Ore ore)
    {
        this.ore = ore;
        if (MAP.put(ore, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "ore");
        //noinspection ConstantConditions
        OreDictionaryHelper.register(this, "ore", ore.getRegistryName().getPath());
        OreDictionaryHelper.register(this, "ore", ore.getRegistryName().getPath(), "small");
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return ore.getMetal();
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return ConfigTFC.GENERAL.smallOreMetalAmount;
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
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Nonnull
    public Ore getOre()
    {
        return ore;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return ore.getMetal() != null ? new ItemHeatHandler(nbt, ore.getMetal().getSpecificHeat(), ore.getMetal().getMeltTemp()) : null;
    }
}