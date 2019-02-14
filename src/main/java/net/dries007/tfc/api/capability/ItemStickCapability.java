/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.heat.ItemHeatHandler;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class ItemStickCapability extends ItemHeatHandler
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "stick");
    private static final float MELTING_POINT = 40f;
    private static final float HEAT_CAPACITY = 1f;

    public ItemStickCapability(@Nullable NBTTagCompound nbt)
    {
        super(nbt, HEAT_CAPACITY, MELTING_POINT);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addHeatInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        float temperature = getTemperature();
        if (temperature > getMeltTemp() * 0.9f)
            text.add(I18n.format("tfc.enum.heat.torch.lit"));
        else if (temperature > 1f)
            text.add(I18n.format("tfc.enum.heat.torch.catching_fire"));
    }
}
