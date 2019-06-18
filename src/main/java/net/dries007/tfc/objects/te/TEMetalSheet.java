/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.metal.BlockMetalSheet;
import net.dries007.tfc.objects.items.metal.ItemMetalSheet;

@ParametersAreNonnullByDefault
public class TEMetalSheet extends TEBase
{
    private boolean[] faces;

    public TEMetalSheet()
    {
        this.faces = new boolean[6];
    }

    /**
     * Gets the number of faces that are present
     *
     * @return a number in [0, 6]
     */
    public int getFaceCount()
    {
        int n = 0;
        for (boolean b : faces)
        {
            if (b) n++;
        }
        return n;
    }

    /**
     * Checks if sheet is present for the given face
     *
     * @param face The face to check
     * @return true if present
     */
    public boolean getFace(EnumFacing face)
    {
        return faces[face.getIndex()];
    }

    public void setFace(EnumFacing facing, boolean value)
    {
        if (!world.isRemote)
        {
            faces[facing.getIndex()] = value;
            markDirty();
            IBlockState state = world.getBlockState(pos);
            for (EnumFacing face : EnumFacing.values())
            {
                state = state.withProperty(BlockMetalSheet.FACE_PROPERTIES[face.getIndex()], faces[face.getIndex()]);
            }
            world.setBlockState(pos, state);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        for (EnumFacing face : EnumFacing.values())
        {
            faces[face.getIndex()] = nbt.getBoolean(face.getName());
        }
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        for (EnumFacing face : EnumFacing.values())
        {
            nbt.setBoolean(face.getName(), faces[face.getIndex()]);
        }
        return super.writeToNBT(nbt);
    }

    public void onBreakBlock(Metal outMetal)
    {
        Item item = ItemMetalSheet.get(outMetal, Metal.ItemType.SHEET);
        ItemStack output = new ItemStack(item, getFaceCount());
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output);
    }
}