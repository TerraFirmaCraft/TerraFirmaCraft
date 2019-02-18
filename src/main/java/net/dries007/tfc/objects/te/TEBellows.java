/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
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
     * For example: a block that must sit right in front of the bellows(like fire pits) must register {@code Vec3i(1,0,0)},
     * {@link net.dries007.tfc.objects.blocks.BlockFirePit}
     * meanwhile, block that sink into ground(like forges) might want to register {@code Vec3i(1,-1,0)}.
     * {@link net.dries007.tfc.objects.blocks.BlockCharcoalForge}
     * If there is a guarantee for another block to register that position,
     * then there is no need do do it anymore with yours.
     */
    private static final Set<Vec3i> offsets = new HashSet<>();

    public static void addBellowsOffset(Vec3i offset)
    {
        offsets.add(offset);
    }

    private long lastPushed = 0L;

    // Min 0.125, Max 0.875
    @SideOnly(Side.CLIENT)
    public double getHeight()
    {
        int time = (int) (world.getTotalWorldTime() - lastPushed);
        if (time < 10)
            return (double) time * 0.075 + 0.125;
        else if (time < 20)
            return (double) (20 - time) * 0.075 + 0.125;
        return 0.125;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        lastPushed = tag.getLong("lastPushed");
        super.readFromNBT(tag);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setLong("lastPushed", lastPushed);
        return super.writeToNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 127, getUpdateTag());
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
        updateBlock();
    }

    public boolean onRightClick()
    {
        long time = world.getTotalWorldTime() - lastPushed;
        if (time < 20)
            return true;
        lastPushed = world.getTotalWorldTime();
        EnumFacing direction = world.getBlockState(pos).getValue(FACING); // It is a better idea to inherit the direction directly from the block.
        for (Vec3i offset : offsets)
        {
            BlockPos posx = pos.up(offset.getY())
                .offset(direction, offset.getX())
                .offset(direction.rotateY(), offset.getZ());
            Block block = world.getBlockState(posx).getBlock();
            if (block instanceof IBellowsHandler && ((IBellowsHandler) block).canIntakeFrom(this, offset, direction))
            {
                ((IBellowsHandler) block).onAirIntake(this, world, posx, 1f);
                if (world.isRemote)
                {
                    //TODO: actual sound, better particles and animation
                    // old bellows just shot particles outwards, that was simpler.
                    posx = pos.offset(direction);
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posx.getX() + .5d, posx.getY() + .5d, posx.getZ() + .5d, 0, 0, 0);
                }
                return true;
            }
        }
        return false;
    }

    public void debug()
    {
        TerraFirmaCraft.getLog().debug("Debugging Bellows");
        TerraFirmaCraft.getLog().debug("Now: {} | Then: {} | Difference: {}", world.getTotalWorldTime(), lastPushed, (world.getTotalWorldTime() - lastPushed));
        TerraFirmaCraft.getLog().debug("Total Height: {}", getHeight());
    }

    private void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3); // sync TE
        markDirty(); // make sure everything saves to disk
    }
}
