/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.player;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.api.recipes.ChiselRecipe;
import net.dries007.tfc.util.skills.Skill;
import net.dries007.tfc.util.skills.SkillType;

@ParametersAreNonnullByDefault
public class PlayerDataHandler implements ICapabilitySerializable<NBTTagCompound>, IPlayerData
{
    private final Map<String, Skill> skills;
    private final EntityPlayer player;

    private ChiselRecipe.Mode chiselMode = ChiselRecipe.Mode.SMOOTH;

    public PlayerDataHandler(EntityPlayer player)
    {
        this.skills = SkillType.createSkillMap(this);
        this.player = player;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        skills.forEach((k, v) -> nbt.setTag(k, v.serializeNBT()));
        nbt.setTag("chiselMode", new NBTTagByte((byte) chiselMode.ordinal()));
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            skills.forEach((k, v) -> v.deserializeNBT(nbt.getCompoundTag(k)));
            chiselMode = ChiselRecipe.Mode.valueOf(nbt.getByte("chiselMode"));
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <S extends Skill> S getSkill(SkillType<S> skillType)
    {
        return (S) skills.get(skillType.getName());
    }

    @Nonnull
    @Override
    public EntityPlayer getPlayer()
    {
        return player;
    }

    @Override
    @Nonnull
    public ChiselRecipe.Mode getChiselMode()
    {
        return chiselMode;
    }

    @Override
    public void setChiselMode(ChiselRecipe.Mode chiselMode)
    {
        this.chiselMode = chiselMode;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayerData.CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayerData.CAPABILITY ? (T) this : null;
    }
}
