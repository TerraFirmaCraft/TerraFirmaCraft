/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropDead;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSimple;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@WailaPlugin
public class CropProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockCropTFC)
        {
            BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
            ICrop crop = b.getCrop();
            IBlockState state = accessor.getBlockState();

            return crop.getFoodDrop(state.getValue(b.getStageProperty()));
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockCropTFC)
        {
            BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
            currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        }
        else if (accessor.getBlock() instanceof BlockCropDead)
        {
            BlockCropDead b = (BlockCropDead) accessor.getBlock();
            ICrop crop = b.getCrop();
            currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation("tile.tfc.crop." + crop.toString().toLowerCase() + ".name").getFormattedText());
            return currentTooltip;
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockCropTFC && accessor.getTileEntity() instanceof TECropBase)
        {
            TECropBase te = (TECropBase) accessor.getTileEntity();
            BlockCropSimple bs = (BlockCropSimple) accessor.getBlock();
            ICrop crop = bs.getCrop();
            int maxStage = crop.getMaxStage();
            float totalGrowthTime = crop.getGrowthTime();
            IBlockState state = accessor.getBlockState();
            int curStage = state.getValue(bs.getStageProperty());
            boolean isWild = state.getValue(BlockCropTFC.WILD);
            long tick = te.getLastUpdateTick();
            float totalTime = totalGrowthTime * maxStage;
            float currentTime = (curStage * totalGrowthTime) + (CalendarTFC.PLAYER_TIME.getTicks() - tick);
            int completionPerc = Math.round(currentTime / totalTime * 100);
            float temp = ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), -tick);
            float rainfall = ChunkDataTFC.getRainfall(accessor.getWorld(), accessor.getPosition());

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
            String growth = String.format("%d%%", completionPerc);
            if (completionPerc >= 100)
            {
                growth = new TextComponentTranslation("waila.tfc.crop.mature").getFormattedText();
            }
            currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.growth", growth).getFormattedText());
        }
        else if (accessor.getBlock() instanceof BlockCropDead)
        {
            currentTooltip.add(new TextComponentTranslation("waila.tfc.crop.dead_crop").getFormattedText());
        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerStackProvider(this, BlockCropTFC.class);

        registrar.registerHeadProvider(this, BlockCropTFC.class);
        registrar.registerHeadProvider(this, BlockCropDead.class);

        registrar.registerBodyProvider(this, BlockCropTFC.class);
        registrar.registerBodyProvider(this, BlockCropDead.class);
    }
}
