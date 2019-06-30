/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class PlayerSkillsHandler implements IPlayerSkills, ICapabilitySerializable<NBTTagCompound>
{
    private final TObjectIntMap<String> skillValues;

    public PlayerSkillsHandler()
    {
        this(null);
    }

    public PlayerSkillsHandler(@Nullable NBTTagCompound nbt)
    {
        this.skillValues = new TObjectIntHashMap<>();

        deserializeNBT(nbt);
    }

    @Override
    public int getSkill(ISkill instance)
    {
        return skillValues.get(instance.getName());
    }

    @Override
    public void setSkill(ISkill instance, int value)
    {
        skillValues.put(instance.getName(), value);
    }

    @Override
    public void addSkill(ISkill instance)
    {
        skillValues.increment(instance.getName());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayerSkills.CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayerSkills.CAPABILITY ? (T) this : null;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        for (ISkill skill : CapabilityPlayerSkills.getAllSkills())
        {
            nbt.setInteger(skill.getName(), skillValues.get(skill.getName()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            skillValues.clear();
            for (ISkill skill : CapabilityPlayerSkills.getAllSkills())
            {
                skillValues.put(skill.getName(), nbt.getInteger(skill.getName()));
            }
        }
    }
}
