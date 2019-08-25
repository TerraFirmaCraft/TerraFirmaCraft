/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.skills;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.api.capability.skill.IPlayerSkills;
import net.dries007.tfc.api.capability.skill.Skill;
import net.dries007.tfc.api.capability.skill.SkillTier;

public class SimpleSkill extends Skill
{
    private float amount;
    private SkillTier tier;

    public SimpleSkill(IPlayerSkills rootSkills)
    {
        super(rootSkills);

        this.amount = 0;
        this.tier = SkillTier.NOVICE;
    }

    @Override
    @Nonnull
    public SkillTier getTier()
    {
        return tier;
    }

    @Override
    public float getLevel()
    {
        return amount;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("amount", amount);
        nbt.setInteger("tier", tier.ordinal());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            amount = nbt.getFloat("amount");
            tier = SkillTier.valueOf(nbt.getInteger("tier"));
        }
    }
}
