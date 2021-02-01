package net.dries007.tfc.objects.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.util.FallingBlockManager;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;

public class EntityFallingBlockTFC extends EntityFallingBlock implements IEntityAdditionalSpawnData
{

    public EntityFallingBlockTFC(World world)
    {
        super(world);
    }

    public EntityFallingBlockTFC(World world, BlockPos pos, IBlockState fallingBlockState)
    {
        this(world, pos.getX(), pos.getY(), pos.getZ(), fallingBlockState);
    }

    public EntityFallingBlockTFC(World world, double x, double y, double z, IBlockState fallingBlockState)
    {
        super(world);
        this.fallTile = fallingBlockState;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.setPosition(x + 0.5f, y + (double) ((1.0F - height) / 2.0F), z + 0.5f);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setOrigin(new BlockPos(this));
    }

    public IBlockState getState()
    {
        return getBlock();
    }

    @Override
    public void onUpdate()
    {
        Block block = fallTile.getBlock();

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        BlockPos pos = new BlockPos(this);

        if (fallTime++ == 0)
        {
            if (world.getBlockState(pos) == fallTile)
            {
                world.getGameRules().setOrCreateGameRule("doTileDrops", Boolean.toString(false));
                world.setBlockToAir(pos);
                world.destroyBlock(pos, false);
                world.getGameRules().setOrCreateGameRule("doTileDrops", Boolean.toString(true));
            }
            else if (!world.isRemote)
            {
                FallingBlockManager.getSpecification(fallTile).beginFall(world, pos);
                setDead();
                return;
            }
        }

        if (!hasNoGravity())
        {
            motionY -= 0.03999999910593033D;
        }

        move(MoverType.SELF, motionX, motionY, motionZ);

        if (!world.isRemote)
        {
            if (!onGround)
            {
                if (fallTime > 100 && (pos.getY() < 1 || pos.getY() > 256) || fallTime > 600)
                {
                    FallingBlockManager.getSpecification(fallTile).endFall(world, pos);
                    setDead();

                    if (world.getGameRules().getBoolean("doEntityDrops"))
                    {
                        dropItems(pos);
                    }
                }
            }
            else
            {
                final IBlockState current = world.getBlockState(pos);
                boolean fallThroughCurrentState = FallingBlockManager.canFallThrough(world, pos, fallTile.getMaterial(), current);

                if (fallThroughCurrentState && !current.getBlock().isAir(current, world, pos))
                {
                    world.destroyBlock(pos, true);
                    return;
                }

                BlockPos downPos = pos.down();
                IBlockState downState = world.getBlockState(downPos);

                if (!downState.getBlock().isAir(downState, world, downPos) && FallingBlockManager.canFallThrough(world, downPos, fallTile.getMaterial(), downState))
                {
                    world.destroyBlock(downPos, true);
                    return;
                }
                else if (ConfigTFC.General.FALLABLE.destroyOres && downState.getBlock() instanceof BlockOreTFC)
                {
                    world.destroyBlock(downPos, false);
                    return;
                }

                motionX *= 0.699999988079071D;
                motionZ *= 0.699999988079071D;
                motionY *= -0.5D;

                if (current.getBlock() != Blocks.PISTON_EXTENSION)
                {
                    FallingBlockManager.getSpecification(fallTile).endFall(world, pos);
                    setDead();

                    if (fallThroughCurrentState)
                    {
                        world.destroyBlock(pos, true);
                        IBlockState placeState = FallingBlockManager.getSpecification(fallTile).getResultingState(fallTile);
                        world.setBlockState(pos, placeState, 3);

                        // Only persist TE data when resulting state is the same as the beginning state
                        if (placeState == fallTile && tileEntityData != null && block.hasTileEntity(fallTile))
                        {
                            TileEntity te = world.getTileEntity(pos);
                            if (te != null)
                            {
                                NBTTagCompound currentTeData = te.writeToNBT(new NBTTagCompound());
                                for (String s : tileEntityData.getKeySet())
                                {
                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s))
                                    {
                                        currentTeData.setTag(s, tileEntityData.getTag(s).copy());
                                    }
                                }
                                te.readFromNBT(currentTeData);
                                te.markDirty();
                            }
                        }
                    }
                    else if (world.getGameRules().getBoolean("doEntityDrops"))
                    {
                        dropItems(pos);
                    }
                }
            }
        }

        motionX *= 0.9800000190734863D;
        motionY *= 0.9800000190734863D;
        motionZ *= 0.9800000190734863D;

    }

    private void dropItems(BlockPos pos)
    {
        FallingBlockManager.Specification spec = FallingBlockManager.getSpecification(fallTile);
        IBlockState dropState = spec.getResultingState(fallTile);
        spec.getDrops(world, pos, dropState, tileEntityData, fallTime, fallDistance).forEach(x -> entityDropItem(x, 0));
    }

    @Override
    public void fall(float distance, float damageMultiplier)
    {
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
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
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("State", Block.getStateId(fallTile == null ? Blocks.AIR.getDefaultState() : fallTile));
        compound.setInteger("Time", this.fallTime);
        if (this.tileEntityData != null)
        {
            compound.setTag("TileEntityData", this.tileEntityData);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        this.fallTile = Block.getStateById(compound.getInteger("State"));
        this.fallTime = compound.getInteger("Time");
        this.shouldDropItem = true; // Unused
        if (compound.hasKey("TileEntityData", 10))
        {
            this.tileEntityData = compound.getCompoundTag("TileEntityData");
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        if (fallTile != null) {
            buffer.writeByte(0b1);
            buffer.writeInt(Block.getStateId(fallTile));
        } else {
            buffer.writeByte(0b0);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        byte validation = additionalData.readByte();
        if (validation == 0b1) {
            fallTile = Block.getStateById(additionalData.readInt());
        }
    }

    @Override
    public String getName()
    {
        return I18n.format("entity.falling_block.name", fallTile.getBlock().getLocalizedName());
    }
}
