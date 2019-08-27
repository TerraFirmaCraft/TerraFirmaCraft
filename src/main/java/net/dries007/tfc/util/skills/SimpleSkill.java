/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.skills;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.api.capability.player.IPlayerData;

public class SimpleSkill extends Skill
{
    private float amount;

    public SimpleSkill(IPlayerData rootSkills)
    {
        super(rootSkills);
        this.amount = 0;
    }

    @Override
    @Nonnull
    public SkillTier getTier()
    {
        return SkillTier.valueOf((int) amount);
    }

    @Override
    public float getLevel()
    {
        return 0.25f * amount;
    }

    public void add(float amount)
    {
        this.amount += amount;
        if (this.amount > 4f)
        {
            this.amount = 4f;
        }
        updateAndSync();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("amount", amount);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            amount = nbt.getFloat("amount");
        }
    }
}
