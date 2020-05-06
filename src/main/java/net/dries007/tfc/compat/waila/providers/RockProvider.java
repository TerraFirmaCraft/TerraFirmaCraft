/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

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

public class RockProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTailTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        int temperature = Math.round(ClimateTFC.getActualTemp(world, pos, 0));
        currentTooltip.add(new TextComponentTranslation("waila.tfc.temperature", temperature).getFormattedText());
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getTailClassList()
    {
        return Collections.singletonList(BlockRockVariant.class);
    }
}
