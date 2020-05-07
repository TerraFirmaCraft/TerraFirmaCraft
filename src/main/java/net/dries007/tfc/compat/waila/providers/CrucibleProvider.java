/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.te.TECrucible;
import net.dries007.tfc.util.Helpers;

public class CrucibleProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        TECrucible crucible = Helpers.getTE(world, pos, TECrucible.class);
        if (crucible != null)
        {
            if (crucible.getAlloy().getAmount() > 0)
            {
                Metal metal = crucible.getAlloyResult();
                currentTooltip.add(new TextComponentTranslation("waila.tfc.metal.output", crucible.getAlloy().getAmount(), new TextComponentTranslation(metal.getTranslationKey()).getFormattedText()).getFormattedText());
            }
            float temperature = nbt.getFloat("temp");
            String heatTooltip = Heat.getTooltip(temperature);
            if (heatTooltip != null)
            {
                currentTooltip.add(heatTooltip);
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(TECrucible.class);
    }
}
