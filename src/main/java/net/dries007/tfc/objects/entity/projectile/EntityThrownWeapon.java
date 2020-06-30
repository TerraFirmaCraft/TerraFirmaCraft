/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.projectile;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

import io.netty.buffer.ByteBuf;

public class EntityThrownWeapon extends EntityArrow implements IThrowableEntity, IEntityAdditionalSpawnData
{
    private ItemStack weapon = ItemStack.EMPTY;
    private int knockbackStrength = 0;

    protected EntityThrownWeapon(World world) { super(world); }

    protected EntityThrownWeapon(World world, double x, double y, double z) { super(world, x, y, z); }

    protected EntityThrownWeapon(World world, EntityLivingBase shooter) { super(world, shooter); }

    @Override
    public Entity getThrower()
    {
        return this.shootingEntity;
    }

    @Override
    public void setThrower(Entity entity)
    {
        this.shootingEntity = entity;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        ByteBufUtils.writeItemStack(buffer, this.weapon);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        setWeapon(ByteBufUtils.readItemStack(additionalData));
    }

    public ItemStack getWeapon()
    {
        return this.weapon;
    }

    public void setWeapon(ItemStack stack) { this.weapon = stack.copy(); }

    @Override
    protected void onHit(@Nonnull RayTraceResult raytraceResultIn)
    {
        Entity entity = raytraceResultIn.entityHit;

        // Damage item
        if (getThrower() instanceof EntityLivingBase)
        {
            EntityLivingBase thrower = (EntityLivingBase) getThrower();
            weapon.damageItem(1, thrower);
        }

        if (entity != null)
        {
            ItemStack weapon = this.getWeapon();
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            float finalDamage = f * (float) this.getDamage();

            if (this.getIsCritical())
            {
                finalDamage *= 2.0f;
            }

            DamageSource damagesource;

            if (this.shootingEntity == null)
            {
                //TODO custom damage sources?
                damagesource = DamageSource.causeArrowDamage(this, this);
            }
            else
            {
                damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
            }

            if (this.isBurning() && !(entity instanceof EntityEnderman))
            {
                entity.setFire(5);
            }

            if (entity.attackEntityFrom(damagesource, finalDamage))
            {
                if (entity instanceof EntityLivingBase)
                {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) entity;

                    if (this.knockbackStrength > 0)
                    {
                        float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                        if (f1 > 0.0F)
                        {
                            entitylivingbase.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1, 0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1);
                        }
                    }

                    this.arrowHit(entitylivingbase);

                    if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP) this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                    }
                }

                //TODO custom sound for thrown weapon hit?
                this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.entityDropItem(weapon, 0.1F);
                this.setDead();
            }
            else
            {
                this.motionX *= -0.10000000149011612D;
                this.motionY *= -0.10000000149011612D;
                this.motionZ *= -0.10000000149011612D;
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;

                if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D)
                {
                    this.entityDropItem(weapon, 0.1F);
                    this.setDead();
                }
            }
        }
        else
        {
            super.onHit(raytraceResultIn);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        NBTTagList tag = new NBTTagList();
        tag.appendTag(weapon.serializeNBT());
        compound.setTag("weapon", tag);

        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        NBTTagList tag = compound.getTagList("weapon", Constants.NBT.TAG_COMPOUND);
        weapon = tag.tagCount() > 0 ? new ItemStack(tag.getCompoundTagAt(0)) : ItemStack.EMPTY;
        super.readEntityFromNBT(compound);
    }

    @Override
    @Nonnull
    protected ItemStack getArrowStack()
    {
        return this.weapon;
    }

    public void setKnockbackStrength(int knockbackStrength)
    {
        this.knockbackStrength = knockbackStrength;
    }
}
