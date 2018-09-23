/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import net.dries007.tfc.objects.te.TEBellows;

//This if here to help you implement, copy-paste and/or if you're on IJ, Ctrl+click
import static net.dries007.tfc.objects.te.TEBellows.offsets;

/**
 * Blocks(not TEs) must implement this interface in order to work with bellows
 * and must provide an offset for them to check. See unused static above.
 */
public interface IBellowsHandler
{
    /**
     * standard handlers should check if they have been accessed by belows from a legal offset
     *
     * @param te     bellows that query
     * @param offset that the bellows used to reach this block, NOT ROTATED accordingly!
     * @param facing direction the bellows output to
     * @return self-explanatory
     */
    boolean canIntakeFrom(TEBellows te, Vec3i offset, EnumFacing facing);

    /**
     * @param te        the bellows that give the air intake.
     * @param airAmount the amount of air that the bellows give. For reference, TFC bellows always give 1.
     * @return the amount of air actually used, idk, I was thinking someone might make some custom bellows that use that.
     */
    @SuppressWarnings("UnusedReturnValue")
    float onAirIntake(TEBellows te, BlockPos pos, float airAmount);
}
