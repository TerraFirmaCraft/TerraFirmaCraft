/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class WoodenBucketItem extends TFCBucketItem
{
    public WoodenBucketItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist)
    {
        super(properties, capacity, whitelist);
    }

    /**
     * Follows the super without interacting with fluid logging or containers
     */
    @Override
    public boolean emptyContents(IFluidHandler handler, @Nullable Player player, Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit)
    {
        Fluid fluid = handler.getFluidInTank(0).getFluid();
        if (state.isAir() || state.canBeReplaced(fluid))
        {
            if (level.dimensionType().ultraWarm() && Helpers.isFluid(fluid, FluidTags.WATER))
            {
                Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                return true;
            }
            else
            {
                if (!level.isClientSide && !state.getMaterial().isLiquid())
                {
                    level.destroyBlock(pos, true);
                }

                BlockState toPlace = fluid.defaultFluidState().createLegacyBlock();
                if (!TFCConfig.SERVER.enableSourcesFromWoodenBucket.get())
                {
                    toPlace = toPlace.setValue(LiquidBlock.LEVEL, 2);
                }

                if (!level.setBlock(pos, toPlace, 11))
                {
                    return false;
                }
                else
                {
                    playEmptySound(fluid, player, level, pos);
                    return true;
                }
            }
        }
        else if (hit != null)
        {
            BlockPos newPos = pos.relative(hit.getDirection());
            return emptyContents(handler, player, level, newPos, level.getBlockState(newPos), null);
        }
        return false;
    }
}
