/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.skills;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.api.capability.player.IPlayerData;

public class SmithingSkill extends Skill
{
    private static final int MAX_GENERAL = 80;
    private static final int MAX_TOOLS = 40;
    private static final int MAX_WEAPONS = 20;
    private static final int MAX_ARMOR = 20;

    private int generalLevel, toolsLevel, weaponsLevel, armorLevel;

    public SmithingSkill(IPlayerData rootSkills)
    {
        super(rootSkills);
    }

    @Override
    @Nonnull
    public SkillTier getTier()
    {
        return SkillTier.valueOf((generalLevel + toolsLevel + weaponsLevel + armorLevel) / 40);
    }

    @Override
    public float getLevel()
    {
        return ((generalLevel + toolsLevel + weaponsLevel + armorLevel) % 40) / 40.0f;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("general", generalLevel);
        nbt.setInteger("tools", toolsLevel);
        nbt.setInteger("weapons", weaponsLevel);
        nbt.setInteger("armor", armorLevel);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            generalLevel = nbt.getInteger("general");
            toolsLevel = nbt.getInteger("tools");
            weaponsLevel = nbt.getInteger("weapons");
            armorLevel = nbt.getInteger("armor");
        }
    }

    public void addGeneralSkill(int amount)
    {
        generalLevel += amount;
        if (generalLevel > MAX_GENERAL)
        {
            generalLevel = MAX_GENERAL;
        }
    }

    public void addToolsSkill(int amount)
    {
        toolsLevel += amount;
        if (toolsLevel > MAX_TOOLS)
        {
            toolsLevel = MAX_TOOLS;
        }
    }

    public void addWeaponsSkill(int amount)
    {
        weaponsLevel += amount;
        if (weaponsLevel > MAX_WEAPONS)
        {
            weaponsLevel = MAX_WEAPONS;
        }
    }

    public void addArmorSkill(int amount)
    {
        armorLevel += amount;
        if (armorLevel > MAX_ARMOR)
        {
            armorLevel = MAX_ARMOR;
        }
    }
}
