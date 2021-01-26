package net.dries007.tfc.objects.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.util.IFallingBlock;

public class EntityFallingBlockTFC extends EntityFallingBlock implements IEntityAdditionalSpawnData
{

    public EntityFallingBlockTFC(World world)
    {
        super(world);
    }

    public EntityFallingBlockTFC(World world, BlockPos pos, IBlockState fallingBlockState)
    {
        super(world, pos.getX(), pos.getY(), pos.getZ(), fallingBlockState);
        setPosition(pos.getX() + 0.5f, pos.getY() + (double) ((1.0F - height) / 2.0F), pos.getZ() + 0.5f);
    }

    public EntityFallingBlockTFC(World world, double x, double y, double z, IBlockState fallingBlockState)
    {
        super(world, x, y, z, fallingBlockState);
        setPosition(x + 0.5f, y + (double) ((1.0F - height) / 2.0F), z + 0.5f);
    }

    public IBlockState getState()
    {
        return getBlock();
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
        BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY - 0.20000000298023224D), MathHelper.floor(this.posZ));
        IBlockState state = this.world.getBlockState(pos);
        if (!world.isAirBlock(pos) && IFallingBlock.canFallThrough(world, pos, this.fallTile.getMaterial(), state))
        {
            world.destroyBlock(pos, true);
        }
        else if (ConfigTFC.General.FALLABLE.destroyOres && state instanceof BlockOreTFC)
        {
            world.destroyBlock(pos, false);
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        if (this.fallTile != null) {
            buffer.writeByte(0b1);
            buffer.writeInt(Block.getStateId(this.fallTile));
        } else {
            buffer.writeByte(0b0);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        byte validation = additionalData.readByte();
        if (validation == 0b1) {
            this.fallTile = Block.getStateById(additionalData.readInt());
        }
    }

    @Override
    public String getName()
    {
        return I18n.format("entity.falling_block.name", this.fallTile.getBlock().getLocalizedName());
    }
}
