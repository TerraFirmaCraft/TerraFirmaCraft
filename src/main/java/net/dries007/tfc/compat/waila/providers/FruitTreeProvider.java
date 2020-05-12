/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeBranch;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeSapling;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeTrunk;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class FruitTreeProvider implements IWailaBlock
{
    private static void addInfo(IFruitTree tree, TETickCounter te, float temperature, float rainfall, List<String> currentTooltip)
    {
        if (tree.isValidForGrowth(temperature, rainfall))
        {
            currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.growing").getFormattedText());
            if (te != null)
            {
                long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                // Don't show 100% since it still needs to check on randomTick to grow
                float perc = Math.min(0.99F, hours / (tree.getGrowthTime() * (float) ConfigTFC.General.FOOD.fruitTreeGrowthTimeModifier)) * 100;
                String growth = String.format("%d%%", Math.round(perc));
                currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.growth", growth).getFormattedText());
            }
        }
        else
        {
            currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.not_growing").getFormattedText());
        }
    }

    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockFruitTreeLeaves)
        {
            BlockFruitTreeLeaves block = (BlockFruitTreeLeaves) state.getBlock();
            if (state.getValue(BlockFruitTreeLeaves.HARVESTABLE) && block.getTree().isHarvestMonth(CalendarTFC.CALENDAR_TIME.getMonthOfYear()))
            {
                if (state.getValue(BlockFruitTreeLeaves.LEAF_STATE) != BlockFruitTreeLeaves.EnumLeafState.FRUIT)
                {
                    TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
                    addInfo(block.getTree(), te, ClimateTFC.getActualTemp(world, pos), ChunkDataTFC.getRainfall(world, pos), currentTooltip);
                }
            }
            else
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.agriculture.harvesting_months").getFormattedText());
                for (Month month : Month.values())
                {
                    if (block.getTree().isHarvestMonth(month))
                    {
                        currentTooltip.add(TerraFirmaCraft.getProxy().getMonthName(month, true));
                    }
                }
            }
        }
        else if (state.getBlock() instanceof BlockFruitTreeSapling)
        {
            BlockFruitTreeSapling block = (BlockFruitTreeSapling) state.getBlock();
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            addInfo(block.getTree(), te, ClimateTFC.getActualTemp(world, pos), ChunkDataTFC.getRainfall(world, pos), currentTooltip);
        }
        else if (state.getBlock() instanceof BlockFruitTreeTrunk)
        {
            // Gets the top most trunk, since that one is the responsible for growth
            IBlockState topMost = state;
            while (world.getBlockState(pos.up()).getBlock() instanceof BlockFruitTreeTrunk)
            {
                pos = pos.up();
                topMost = world.getBlockState(pos);
            }
            if (world.getBlockState(pos.up()).getBlock() instanceof BlockFruitTreeBranch)
            {
                // Abort, this tree is already fully grown
                return currentTooltip;
            }
            BlockFruitTreeTrunk block = (BlockFruitTreeTrunk) topMost.getBlock();
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            addInfo(block.getTree(), te, ClimateTFC.getActualTemp(world, pos), ChunkDataTFC.getRainfall(world, pos), currentTooltip);
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return ImmutableList.of(BlockFruitTreeLeaves.class, BlockFruitTreeSapling.class, BlockFruitTreeTrunk.class);
    }
}
