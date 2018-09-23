/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import net.dries007.tfc.api.capability.IBellowsHandler;

import static net.minecraft.block.BlockHorizontal.FACING;

public class TEBellows extends TileEntity
{
    /**
     * The list of position offsets that bellows must check to input air into.
     * Directions go like this:
     * X: front of the bellows, positive values go forward
     * Y: vertical, self-explanatory, negative is below.
     * Z: X but rotated 90 degrees clockwise, positive values go right. in most cases you want this to be 0
     *
     * For example: a block that must sit right in front of the bellows(like fire pits) must register Vec3i(1,0,0),
     * meanwhile, block that sink into ground(like forges) might want to register Vec3i(1,-1,0).
     * If there is a guarantee for another block to register that position,
     * then there is no need do do it anymore with yours.
     */
    public static final Set<Vec3i> offsets = new HashSet<>();

    private long lastPushed = 0L;

    //number of ticks
    @SuppressWarnings("FieldCanBeLocal")
    private int pushRate = 20;

    //public EnumFacing direction;
    /*public TEBellows(EnumFacing facing){
        super();
        //direction = facing;
    }*/

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("lastPushed"))
            lastPushed = tag.getLong("timer");
        super.readFromNBT(tag);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        if (world.getTotalWorldTime() - lastPushed < pushRate)
            tag.setLong("timer", lastPushed);
        return super.writeToNBT(tag);
    }

    public boolean onRightClick()
    {
        long time = world.getTotalWorldTime() - lastPushed;
        if (time < pushRate)
            return true;
        EnumFacing direction = world.getBlockState(pos).getValue(FACING); // It is a better idea to inherit the direction directly from the block.
        for (Vec3i offset : offsets)
        {
            BlockPos posx = pos.up(offset.getY())
                .offset(direction, offset.getX())
                .offset(direction.rotateY(), offset.getZ());
            Block block = world.getBlockState(posx).getBlock();
            if (block instanceof IBellowsHandler && ((IBellowsHandler) block).canIntakeFrom(this, offset, direction))
            {
                ((IBellowsHandler) block).onAirIntake(this, posx, 1f);
                lastPushed = world.getTotalWorldTime();
                if (world.isRemote)
                {
                    //TODO: actual sound, better particles and animation
                    posx = pos.offset(direction);
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posx.getX() + .5d, posx.getY() + .5d, posx.getZ() + .5d, 0, 0, 0);
                }
                return true;
            }
        }
        return false;
    }
}
