/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class EntityJavelin extends EntityArrow
{
    private ItemStack thrown;

    public EntityJavelin(World worldIn)
    {
        super(worldIn);
        thrown = ItemStack.EMPTY;
    }

    public EntityJavelin(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
        thrown = ItemStack.EMPTY;
    }

    public EntityJavelin(World worldIn, EntityLivingBase shooter)
    {
        super(worldIn, shooter);
        thrown = ItemStack.EMPTY;
    }

    public void setThrowItem(ItemStack it)
    {
        thrown = it;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        NBTTagList tag = new NBTTagList();
        tag.appendTag(thrown.serializeNBT());
        compound.setTag("thrownitem", tag);

        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        NBTTagList tag = compound.getTagList("thrownitem", Constants.NBT.TAG_COMPOUND);
        thrown = tag.tagCount() > 0 ? new ItemStack(tag.getCompoundTagAt(0)) : ItemStack.EMPTY;
        super.readEntityFromNBT(compound);
    }

    @Override
    @Nonnull
    protected ItemStack getArrowStack()
    {
        return thrown;
    }
}
