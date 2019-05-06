/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class ItemBlockPlant extends ItemBlockTFC
{
    protected BlockPlantTFC block;
    private Plant.PlantValidity tempValidity;
    private Plant.PlantValidity rainValidity;

    public ItemBlockPlant(BlockPlantTFC block)
    {
        super(block);
        this.block = block;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, @Nullable Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (worldIn.isRemote && entityIn != null && entityIn.ticksExisted % 10 == 0)
        {
            tempValidity = block.getPlant().getTempValidity(ClimateTFC.getHeightAdjustedTemp(worldIn, entityIn.getPosition()));
            rainValidity = block.getPlant().getRainValidity(ChunkDataTFC.getRainfall(worldIn, entityIn.getPosition()));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        // todo: useful for testing, not sure if this feature will stay for release, tie to agriculture skill if we keep it
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 0.0D;
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack)
    {
        if (tempValidity != null && rainValidity != null)
        {
            switch (tempValidity)
            {
                case COLD:
                    return 5636095;
                case HOT:
                    return 16733525;
            }
            switch (rainValidity)
            {
                case DRY:
                    return 16755200;
                case WET:
                    return 5592575;
            }
            return 43520;
        }
        return 11184810;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn)
    {
        // todo: tie plant tooltips into agriculture skill, probably add more detail at higher levels and less at lower levels

        String temp = I18n.format(MOD_ID + ".tooltip.plant_temp");
        String rain = I18n.format(MOD_ID + ".tooltip.plant_rain");

        if (tempValidity != null)
        {
            switch (tempValidity)
            {
                case COLD:
                    temp += I18n.format(MOD_ID + ".tooltip.plant_cold", TextFormatting.AQUA, TextFormatting.RESET);
                    break;
                case HOT:
                    temp += I18n.format(MOD_ID + ".tooltip.plant_hot", TextFormatting.RED, TextFormatting.RESET);
                    break;
                default:
                    temp += I18n.format(MOD_ID + ".tooltip.plant_valid", TextFormatting.DARK_GREEN, TextFormatting.RESET);
            }
        }

        if (rainValidity != null)
        {
            switch (rainValidity)
            {
                case DRY:
                    rain += I18n.format(MOD_ID + ".tooltip.plant_dry", TextFormatting.GOLD, TextFormatting.RESET);
                    break;
                case WET:
                    rain += I18n.format(MOD_ID + ".tooltip.plant_wet", TextFormatting.BLUE, TextFormatting.RESET);
                    break;
                default:
                    rain += I18n.format(MOD_ID + ".tooltip.plant_valid", TextFormatting.DARK_GREEN, TextFormatting.RESET);
            }
        }

        tooltip.add(temp);
        tooltip.add(rain);
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
