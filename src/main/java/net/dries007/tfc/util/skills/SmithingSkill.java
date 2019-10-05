/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.skills;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.dries007.tfc.api.capability.player.IPlayerData;

public class SmithingSkill extends Skill
{
    private static final String SKILL_VALUE = "tfc_smithing_value";
    private static final String SKILL_TYPE = "tfc_smithing_type";

    public static void applySkillBonus(SmithingSkill skill, ItemStack stack, Type bonusType)
    {
        // General types don't receive skill bonuses
        if (bonusType != Type.GENERAL)
        {
            if (!stack.hasTagCompound())
            {
                stack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null)
            {
                nbt.setFloat(SKILL_VALUE, skill.getSkillSum() / 160f);
                nbt.setInteger(SKILL_TYPE, bonusType.ordinal());
            }
        }
    }

    public static void copySkillBonus(ItemStack to, ItemStack from)
    {
        NBTTagCompound fromNbt = from.getTagCompound();
        if (fromNbt != null)
        {
            if (!to.hasTagCompound())
            {
                to.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound toNbt = to.getTagCompound();
            //noinspection ConstantConditions
            toNbt.setInteger(SKILL_TYPE, fromNbt.getInteger(SKILL_TYPE));
            toNbt.setFloat(SKILL_VALUE, fromNbt.getFloat(SKILL_VALUE));
        }
    }

    public static float getSkillBonus(ItemStack stack)
    {
        return getSkillBonus(stack, null);
    }

    /**
     * @param type the type to get skill for, null if it doesn't matter
     * @return a value between 0 and 1
     */
    public static float getSkillBonus(ItemStack stack, @Nullable Type type)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null)
        {
            return type == null || nbt.getInteger(SKILL_TYPE) == type.ordinal() ? nbt.getFloat(SKILL_VALUE) : 0;
        }
        return 0;
    }

    private final int[] skillLevels = new int[4];

    public SmithingSkill(IPlayerData rootSkills)
    {
        super(rootSkills);
    }

    @Override
    @Nonnull
    public SkillTier getTier()
    {
        return SkillTier.valueOf(getSkillSum() / 40);
    }

    @Override
    public float getLevel()
    {
        int totalSkill = getSkillSum();
        // checks >=160 for full progress bar in MASTER tier.
        return totalSkill >= 160 ? 1.0F : (totalSkill % 40) / 40.0f;
    }

    @Override
    public void setTotalLevel(double value)
    {
        if (value < 0)
        {
            value = 0;
        }
        if (value > 1)
        {
            value = 1;
        }
        // Evenly distribute value accordingly
        for (Type smithType : Type.values())
        {
            skillLevels[smithType.ordinal()] = (int) (value * smithType.getMax());
        }
        updateAndSync();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Type type : Type.values())
        {
            nbt.setInteger(type.name().toLowerCase(), skillLevels[type.ordinal()]);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            for (Type type : Type.values())
            {
                skillLevels[type.ordinal()] = nbt.getInteger(type.name().toLowerCase());
            }
        }
    }

    public void addSkill(Type type, int amount)
    {
        skillLevels[type.ordinal()] += amount;
        if (skillLevels[type.ordinal()] > type.getMax())
        {
            skillLevels[type.ordinal()] = type.getMax();
        }
        updateAndSync();
    }

    private int getSkillSum()
    {
        int sum = 0;
        for (int skill : skillLevels)
        {
            sum += skill;
        }
        return sum;
    }

    public enum Type
    {
        GENERAL(80),
        TOOLS(40),
        WEAPONS(20),
        ARMOR(20);

        private final int max;

        Type(int max)
        {
            this.max = max;
        }

        public int getMax()
        {
            return max;
        }
    }
}
