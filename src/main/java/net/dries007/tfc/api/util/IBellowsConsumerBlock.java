/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.dries007.tfc.objects.te.TEBellows;

/**
 * Blocks(not TEs) must implement this interface in order to work with bellows
 * and must provide an offset for them to check by calling {@link TEBellows#addBellowsOffset(Vec3i)}
 */
public interface IBellowsConsumerBlock
{

    /**
     * standard handlers should check if they have been accessed by belows from a legal offset
     *
     * @param te     bellows that query
     * @param offset that the bellows used to reach this block, NOT ROTATED accordingly!
     * @param facing direction the bellows output to
     * @return self-explanatory
     */
    boolean canIntakeFrom(@Nonnull TEBellows te, @Nonnull Vec3i offset, @Nonnull EnumFacing facing);

    /**
     * @param te        the bellows that give the air intake.
     * @param airAmount the amount of air that the bellows give. For reference, TFC bellows always give 200.
     */
    void onAirIntake(@Nonnull TEBellows te, @Nonnull World world, @Nonnull BlockPos pos, int airAmount);
}