/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.util.IBellowsConsumerBlock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.blocks.devices.BlockCharcoalForge;
import net.dries007.tfc.objects.blocks.devices.BlockFirePit;

import static net.minecraft.block.BlockHorizontal.FACING;

@ParametersAreNonnullByDefault
public class TEBellows extends TEBase
{
    public static final Vec3i OFFSET_LEVEL = new Vec3i(1, 0, 0);
    public static final Vec3i OFFSET_INSET = new Vec3i(1, -1, 0);

    private static final Set<Vec3i> OFFSETS = new HashSet<>();
    private static final int BELLOWS_AIR = 200;

    static
    {
        addBellowsOffset(OFFSET_LEVEL);
        addBellowsOffset(OFFSET_INSET);
    }

    /**
     * Notify the bellows that it should check a certain offset when blowing air
     * X: front of the bellows, positive values go forward
     * Y: vertical, self-explanatory, negative is below.
     * Z: X but rotated 90 degrees clockwise, positive values go right. in most cases you want this to be 0
     * For example: a block that must sit right in front of the bellows(like fire pits) must check {@code Vec3i(1,0,0)},
     * {@link BlockFirePit}
     * meanwhile, blocks that sink into ground( like forges) must check {@code Vec3i(1,-1,0)}.
     * {@link BlockCharcoalForge}
     *
     * @param offset The offset to check
     */
    public static void addBellowsOffset(Vec3i offset)
    {
        OFFSETS.add(offset);
    }

    private long lastPushed = 0L;

    // Min 0.125, Max 0.625
    @SideOnly(Side.CLIENT)
    public double getHeight()
    {
        int time = (int) (world.getTotalWorldTime() - lastPushed);
        if (time < 10)
        {
            return (double) time * 0.05 + 0.125;
        }
        else if (time < 20)
        {
            return (double) (20 - time) * 0.05 + 0.125;
        }
        return 0.125;
    }

    public boolean onRightClick()
    {
        long time = world.getTotalWorldTime() - lastPushed;
        if (time < 20)
            return true;
        world.playSound(null, pos, TFCSounds.BELLOWS_BLOW_AIR, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!world.isRemote)
        {
            lastPushed = world.getTotalWorldTime();
            markForBlockUpdate();
        }

        EnumFacing direction = world.getBlockState(pos).getValue(FACING); // It is a better idea to inherit the direction directly from the block.
        for (Vec3i offset : OFFSETS)
        {
            BlockPos posx = pos.up(offset.getY())
                .offset(direction, offset.getX())
                .offset(direction.rotateY(), offset.getZ());
            Block block = world.getBlockState(posx).getBlock();
            if (block instanceof IBellowsConsumerBlock && ((IBellowsConsumerBlock) block).canIntakeFrom(this, offset, direction))
            {
                ((IBellowsConsumerBlock) block).onAirIntake(this, world, posx, BELLOWS_AIR);
                if (world.isRemote)
                {
                    posx = pos.offset(direction);
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posx.getX() + .5d, posx.getY() + .5d, posx.getZ() + .5d, 0, 0, 0);
                }
                return true;
            }
        }
        return true;
    }

    public void debug()
    {
        TerraFirmaCraft.getLog().debug("Debugging Bellows");
        TerraFirmaCraft.getLog().debug("Now: {} | Then: {} | Difference: {}", world.getTotalWorldTime(), lastPushed, (world.getTotalWorldTime() - lastPushed));
        TerraFirmaCraft.getLog().debug("Total Height: {}", getHeight());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        lastPushed = nbt.getLong("lastPushed");
        super.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("lastPushed", lastPushed);
        return super.writeToNBT(nbt);
    }
}