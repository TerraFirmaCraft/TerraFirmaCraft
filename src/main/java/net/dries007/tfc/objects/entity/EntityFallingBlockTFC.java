/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.util.IFallingBlock;


@ParametersAreNonnullByDefault
public class EntityFallingBlockTFC extends Entity
{
    private static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(EntityFallingBlockTFC.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Optional<IBlockState>> BLOCK = EntityDataManager.createKey(EntityFallingBlockTFC.class, DataSerializers.OPTIONAL_BLOCK_STATE);

    private IFallingBlock falling;
    private int fallTime;
    private NBTTagCompound teData;
    private boolean failedBreakCheck;

    @SuppressWarnings("unused")
    public EntityFallingBlockTFC(World worldIn)
    {
        super(worldIn);
    }

    public EntityFallingBlockTFC(World world, BlockPos start, IFallingBlock falling, IBlockState state)
    {
        this(world);
        this.falling = falling;
        setSize(0.98F, 0.98F);
        setPosition(start.getX() + 0.5f, start.getY() + (double) ((1.0F - height) / 2.0F), start.getZ() + 0.5f);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posY;
        dataManager.set(ORIGIN, start);
        dataManager.set(BLOCK, Optional.of(state));
    }

    public IBlockState getState()
    {
        return dataManager.get(BLOCK).orNull();
    }

    public BlockPos getOrigin()
    {
        return dataManager.get(ORIGIN);
    }

    @Override
    protected void entityInit()
    {
        dataManager.register(ORIGIN, BlockPos.ORIGIN);
        dataManager.register(BLOCK, Optional.absent());
    }

    @Override
    public void onUpdate()
    {
        IBlockState state = getState();
        if (state == null) return;

        Block block = state.getBlock();

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        // First tick only, make block air if it's still the same block we started from otherwise, die.
        if (fallTime++ == 0)
        {
            BlockPos pos = new BlockPos(this);
            if (world.getBlockState(pos) == state)
            {
                world.setBlockToAir(pos);
            }
            else if (!world.isRemote)
            {
                setDead();
                return;
            }
        }

        if (!hasNoGravity())
        {
            motionY -= 0.03999999910593033D;
        }

        move(MoverType.SELF, motionX, motionY, motionZ);

        motionX *= 0.9800000190734863D;
        motionY *= 0.9800000190734863D;
        motionZ *= 0.9800000190734863D;

        final BlockPos pos = new BlockPos(this); // Post move position

        if (!onGround)
        {
            // Still falling
            failedBreakCheck = false;
            if ((fallTime > 100 && (pos.getY() < 1 || pos.getY() > 256) || fallTime > 600))
            {
                if (!world.isRemote)
                {
                    if (world.getGameRules().getBoolean("doEntityDrops"))
                    {
                        falling.getDropsFromFall(world, pos, state, teData, fallTime, fallDistance).forEach(x -> entityDropItem(x, 0));
                    }
                    setDead();
                }
            }
        }
        else
        {
            // On ground
            if (!failedBreakCheck)
            {
                if (!world.isAirBlock(pos) && IFallingBlock.canFallThrough(world, pos, state.getMaterial()))
                {
                    world.destroyBlock(pos, true);
                    failedBreakCheck = true;
                    return;
                }
                else if (!world.isAirBlock(pos.down()) && IFallingBlock.canFallThrough(world, pos.down(), state.getMaterial()))
                {
                    world.destroyBlock(pos.down(), true);
                    failedBreakCheck = true;
                    return;
                }
                else if (ConfigTFC.General.FALLABLE.destroyOres && world.getBlockState(pos.down()).getBlock() instanceof BlockOreTFC)
                {
                    world.destroyBlock(pos.down(), false);
                    failedBreakCheck = true;
                    return;
                }
            }

            final IBlockState current = world.getBlockState(pos);

            motionX *= 0.699999988079071D;
            motionZ *= 0.699999988079071D;
            motionY *= -0.5D;

            if (current.getBlock() != Blocks.PISTON_EXTENSION)
            {
                setDead();

                if (IFallingBlock.canFallThrough(world, pos, state.getMaterial()))
                {
                    if (!world.isRemote)
                    {
                        world.destroyBlock(pos, true);
                        world.setBlockState(pos, state, 3);

                        // Copy all TE data over default data (except pos[X,Y,Z]) if the TE is there. This is vanilla code.
                        if (teData != null && block.hasTileEntity(state))
                        {
                            TileEntity te = world.getTileEntity(pos);
                            if (te != null)
                            {
                                NBTTagCompound currentTeData = te.writeToNBT(new NBTTagCompound());
                                for (String s : teData.getKeySet())
                                {
                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s))
                                    {
                                        currentTeData.setTag(s, teData.getTag(s).copy());
                                    }
                                }
                                te.readFromNBT(currentTeData);
                                te.markDirty();
                            }
                        }
                    }
                }
                else if (world.getGameRules().getBoolean("doEntityDrops") && !world.isRemote)
                {
                    falling.getDropsFromFall(world, pos, state, teData, fallTime, fallDistance).forEach(x -> entityDropItem(x, 0));
                }
            }
            else
            {
                return;
            }

        }
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public void fall(float distance, float damageMultiplier)
    {
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
        for (Entity entity : list)
        {
            if (ConfigTFC.General.FALLABLE.hurtEntities && distance > 1.0F && entity instanceof EntityLivingBase)
            {
                entity.attackEntityFrom(DamageSource.FALLING_BLOCK, distance);
            }
            else if (ConfigTFC.General.FALLABLE.destroyItems && entity instanceof EntityItem)
            {
                entity.setDead();
            }
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        IBlockState state = NBTUtil.readBlockState(compound.getCompoundTag("State"));
        this.falling = (IFallingBlock) state.getBlock();
        if (compound.hasKey("State"))
        {
            dataManager.set(BLOCK, Optional.of(state));
        }
        fallTime = compound.getInteger("FallTime");
        if (compound.hasKey("TileEntityData"))
        {
            teData = compound.getCompoundTag("TileEntityData");
        }

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        IBlockState state = getState();
        if (state != null)
        {
            compound.setTag("State", NBTUtil.writeBlockState(new NBTTagCompound(), state));
        }
        compound.setInteger("FallTime", fallTime);
        if (teData != null)
        {
            compound.setTag("TileEntityData", teData);
        }
    }

    @Override
    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    @Override
    public void addEntityCrashInfo(CrashReportCategory category)
    {
        super.addEntityCrashInfo(category);

        category.addCrashSection("Origin", getOrigin());
        category.addCrashSection("State", getState());
        category.addCrashSection("FallTime", fallTime);
        category.addCrashSection("TileEntityData", teData);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderOnFire()
    {
        return false;
    }

    @Override
    public boolean ignoreItemEntityData()
    {
        return true;
    }
}
