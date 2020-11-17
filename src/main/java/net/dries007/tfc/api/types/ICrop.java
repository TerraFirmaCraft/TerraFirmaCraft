/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This is the "model" that is used by BlockCropTFC
 * In vanilla TFC, they are all used as Crop enum instances
 */
public interface ICrop
{
    /**
     * @return the minimum time the crop will take to grow one stage (in ticks)
     */
    long getGrowthTicks();

    /**
     * @return the maximum stage of growth (when current stage == max stage, the crop is fully grown)
     */
    int getMaxStage();

    /**
     * Checks if the conditions are valid for world gen spawning / living
     *
     * @param temperature the temperature, in -30 - +30
     * @param rainfall    the rainfall, in 0 - 500
     * @return true if the crop should spawn here
     */
    boolean isValidConditions(float temperature, float rainfall);

    /**
     * A stricter version of the above check. Allows the crop to grow (advance in stages)
     *
     * @param temperature the temperature, in -30 - +30
     * @param rainfall    the rainfall, in 0 - 500
     * @return true if the crop is allowed to grow.(false doesn't mean death)
     */
    boolean isValidForGrowth(float temperature, float rainfall);

    /**
     * Get the food item dropped by the crop upon breaking / picking
     *
     * @param growthStage the current growth stage
     */
    ItemStack getFoodDrop(int growthStage);

    /**
     * Add tooltip info
     */
    @SideOnly(Side.CLIENT)
    default void addInfo(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {}
}
