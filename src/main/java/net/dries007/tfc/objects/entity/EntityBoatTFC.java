/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.items.wood.ItemBoatTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EntityBoatTFC extends EntityBoat
{
    private static final DataParameter<String> WOOD_NAME = EntityDataManager.createKey(EntityBoatTFC.class, DataSerializers.STRING);

    public EntityBoatTFC(World worldIn)
    {
        super(worldIn);
    }

    public EntityBoatTFC(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    @Nullable
    public Tree getWood()
    {
        //noinspection ConstantConditions
        return TFCRegistries.TREES.getValuesCollection().stream()
            .filter(x -> x.getRegistryName().getPath().equalsIgnoreCase(this.dataManager.get(WOOD_NAME)))
            .findFirst().orElse(null);
    }

    public void setWood(@Nullable Tree wood)
    {
        String woodName = "";
        if (wood != null)
        {
            //noinspection ConstantConditions
            woodName = wood.getRegistryName().getPath().toLowerCase();
        }
        this.dataManager.set(WOOD_NAME, woodName);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(WOOD_NAME, "");
    }

    @Override
    public Item getItemBoat()
    {
        Tree wood = getWood();
        if (wood != null)
        {
            return ItemBoatTFC.get(wood);
        }
        return super.getItemBoat();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        Tree wood = getWood();
        if (wood != null)
        {
            //noinspection ConstantConditions
            compound.setString("Wood", this.getWood().getRegistryName().getPath().toLowerCase());
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Wood"))
        {
            this.dataManager.set(WOOD_NAME, compound.getString("Wood"));
        }
    }
}
