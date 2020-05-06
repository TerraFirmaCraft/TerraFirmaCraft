/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropDead;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSimple;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class CropProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public ItemStack getStack(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockCropTFC)
        {
            BlockCropTFC b = (BlockCropTFC) state.getBlock();
            ICrop crop = b.getCrop();
            return crop.getFoodDrop(state.getValue(b.getStageProperty()));
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public List<String> getHeadTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> tooltip = new ArrayList<>();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockCropTFC)
        {
            BlockCropTFC b = (BlockCropTFC) state.getBlock();
            tooltip.add(TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        }
        else if (state.getBlock() instanceof BlockCropDead)
        {
            BlockCropDead b = (BlockCropDead) state.getBlock();
            ICrop crop = b.getCrop();
            tooltip.add(TextFormatting.WHITE.toString() + new TextComponentTranslation("tile.tfc.crop." + crop.toString().toLowerCase() + ".name").getFormattedText());
        }
        return tooltip;
    }

    @Nonnull
    @Override
    public List<String> getBodyTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        IBlockState state = world.getBlockState(pos);
        TECropBase te = Helpers.getTE(world, pos, TECropBase.class);
        if (state.getBlock() instanceof BlockCropTFC && te != null)
        {
            BlockCropTFC bs = (BlockCropTFC) state.getBlock();
            ICrop crop = bs.getCrop();

            boolean isWild = state.getValue(BlockCropTFC.WILD);
            float temp = ClimateTFC.getActualTemp(world, pos, - te.getLastUpdateTick());
            float rainfall = ChunkDataTFC.getRainfall(world, pos);

            if (isWild)
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.wild").getFormattedText());
            }
            else if (crop.isValidForGrowth(temp, rainfall))
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.growing").getFormattedText());
            }
            else
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.not_growing").getFormattedText());
            }

            int curStage = state.getValue(bs.getStageProperty());
            int maxStage = crop.getMaxStage();

            if(curStage == maxStage)
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.growth", new TextComponentTranslation("waila.tfc.crop.mature").getFormattedText()).getFormattedText());
            }
            else
            {
                float remainingTicksToGrow = Math.max(0, crop.getGrowthTime() - te.getTicksSinceUpdate());
                float curStagePerc = 1.0F - remainingTicksToGrow / crop.getGrowthTime();
                // Don't show 100% since it still needs to check on randomTick to grow
                float totalPerc = Math.min(0.99f, curStagePerc / maxStage + (float)curStage / maxStage) * 100;
                String growth = String.format("%d%%", Math.round(totalPerc));
                currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.growth", growth).getFormattedText());
            }
        }
        else if (state.getBlock() instanceof BlockCropDead)
        {
            currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.dead_crop").getFormattedText());
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getStackClassList()
    {
        return Collections.singletonList(BlockCropTFC.class);
    }

    @Nonnull
    @Override
    public List<Class<?>> getHeadClassList()
    {
        return ImmutableList.of(BlockCropTFC.class, BlockCropDead.class);
    }

    @Nonnull
    @Override
    public List<Class<?>> getBodyClassList()
    {
        return ImmutableList.of(BlockCropTFC.class, BlockCropDead.class);
    }
}
