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

import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.climate.ClimateTFC;

public class InfoProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        int temperature = Math.round(ClimateTFC.getActualTemp(world, pos, 0));
        int rainfall = Math.round(ClimateTFC.getRainfall(world, pos));
        currentTooltip.add(new TextComponentTranslation("waila.tfc.temperature", temperature).getFormattedText());
        currentTooltip.add(new TextComponentTranslation("waila.tfc.rainfall", rainfall).getFormattedText());
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(BlockRockVariant.class);
    }
}
