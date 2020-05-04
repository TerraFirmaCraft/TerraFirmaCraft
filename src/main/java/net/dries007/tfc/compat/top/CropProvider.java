package net.dries007.tfc.compat.top;


import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import mcjty.theoneprobe.api.*;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropDead;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSimple;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CropProvider implements Function<ITheOneProbe, Void>, IProbeInfoProvider, IBlockDisplayOverride
{
    public static ITheOneProbe probe;

    @Override
    public String getID()
    {
        return MOD_ID + ":top_crop_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData iProbeHitData)
    {
        if (state.getBlock() instanceof BlockCropTFC)
        {
            TECropBase te = (TECropBase) world.getTileEntity(iProbeHitData.getPos());
            BlockCropSimple bs = (BlockCropSimple) state.getBlock();
            ICrop crop = bs.getCrop();
            int maxStage = crop.getMaxStage();
            float totalGrowthTime = crop.getGrowthTime();
            int curStage = state.getValue(bs.getStageProperty());
            boolean isWild = state.getValue(BlockCropTFC.WILD);
            long tick = te.getLastUpdateTick();
            float totalTime = totalGrowthTime * maxStage;
            float currentTime = (curStage * totalGrowthTime) + (CalendarTFC.PLAYER_TIME.getTicks() - tick);
            int completionPerc = Math.round(currentTime / totalTime * 100);
            float temp = ClimateTFC.getActualTemp(world, iProbeHitData.getPos(), -tick);
            float rainfall = ChunkDataTFC.getRainfall(world, iProbeHitData.getPos());

            if (isWild)
            {
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.crop.wild").getFormattedText());
            }
            else if (crop.isValidForGrowth(temp, rainfall))
            {
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.crop.growing").getFormattedText());
            }
            else
            {
                TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.crop.not_growing").getFormattedText());
            }
            String growth = String.format("%d%%", completionPerc);
            if (completionPerc >= 100)
            {
                growth = new TextComponentTranslation("waila.tfc.crop.mature").getFormattedText();
            }
            TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.crop.growth", growth).getFormattedText());
        }
        else if (state.getBlock() instanceof BlockCropDead)
        {
            TOPPlugin.outputHorizontalText(iProbeInfo, new TextComponentTranslation("waila.tfc.crop.dead_crop").getFormattedText());
        }
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo iProbeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data)
    {

        if (state.getBlock() instanceof BlockCropTFC)
        {
            BlockCropTFC b = (BlockCropTFC) state.getBlock();
            ICrop crop = b.getCrop();
            int maxStage = crop.getMaxStage();
            ItemStack stack = crop.getFoodDrop(maxStage);
            iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(stack)
                .vertical()
                .itemLabel(stack)
                .text(TextStyleClass.MODNAME + TerraFirmaCraft.MOD_NAME);

            return true;
        }
        else if (state.getBlock() instanceof BlockCropDead)
        {
            BlockCropDead b = (BlockCropDead) state.getBlock();
            ICrop crop = b.getCrop();
            int maxStage = crop.getMaxStage();
            ItemStack stack = crop.getFoodDrop(maxStage);
            iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(stack)
                .vertical()
                .itemLabel(stack)
                .text(TextStyleClass.MODNAME + TerraFirmaCraft.MOD_NAME);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Void apply(ITheOneProbe iTheOneProbe)
    {
        probe = iTheOneProbe;
        probe.registerBlockDisplayOverride((mode, iProbeInfo, player, world, state, iProbeHitData) -> {
            TileEntity te = world.getTileEntity(iProbeHitData.getPos());
            if (state.getBlock() instanceof BlockCropTFC | state.getBlock() instanceof BlockCropDead)
            {
                return overrideStandardInfo(mode, iProbeInfo, player, world, state, iProbeHitData);
            }
            return false;
        });

        iTheOneProbe.registerProvider(this);
        return null;
    }
}
