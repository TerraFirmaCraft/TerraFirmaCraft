/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.player;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketPlayerDataUpdate;
import net.dries007.tfc.util.skills.Skill;
import net.dries007.tfc.util.skills.SkillType;

public class PlayerDataHandler implements ICapabilitySerializable<NBTTagCompound>, IPlayerData
{
    private final Map<String, Skill> skills;
    private final EntityPlayer player;

    private ChiselMode chiselMode = ChiselMode.SMOOTH;

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
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            skills.forEach((k, v) -> v.deserializeNBT(nbt.getCompoundTag(k)));
        }
        chiselMode = ChiselMode.values()[nbt.getByte("chiselMode")];
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <S extends Skill> S getSkill(SkillType<S> skillType)
    {
        return (S) skills.get(skillType.getName());
    }

    @Override
    @Nonnull
    public ChiselMode getChiselMode()
    {
        return chiselMode;
    }

    @Override
    public void setChiselMode(@Nonnull ChiselMode chiselMode)
    {
        this.chiselMode = chiselMode;
    }

    @Nonnull
    @Override
    public EntityPlayer getPlayer()
    {
        return player;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayerData.CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityPlayerData.CAPABILITY ? (T) this : null;
    }
}
