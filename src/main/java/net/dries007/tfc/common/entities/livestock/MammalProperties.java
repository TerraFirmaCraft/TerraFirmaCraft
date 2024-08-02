/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.util.calendar.Calendars;

import org.jetbrains.annotations.Nullable;

public interface MammalProperties extends TFCAnimalProperties
{
    MammalConfig getMammalConfig();

    long getPregnantTime();

    void setPregnantTime(long time);

    @Nullable
    CompoundTag getGenes();

    void setGenes(@Nullable CompoundTag tag);

    @Override
    default void tickAnimalData()
    {
        TFCAnimalProperties.super.tickAnimalData();
        Level level = getEntity().level();
        if (!level.isClientSide && level.getGameTime() % 20 == 0)
        {
            if (getPregnantTime() > 0 && Calendars.SERVER.getTotalDays() >= getPregnantTime() + getGestationDays() && isFertilized())
            {
                birthChildren();
                setFertilized(false);
                setPregnantTime(-1L);
                addUses(10);
            }
        }
    }

    default void birthChildren()
    {
        LivingEntity entity = getEntity();
        if (entity.level() instanceof ServerLevel server && entity instanceof AgeableMob ageable)
        {
            var random = entity.getRandom();
            final int kids = Mth.nextInt(random, 1, getChildCount());
            for (int i = 0; i < kids; i++)
            {
                AgeableMob offspring = ageable.getBreedOffspring(server, ageable);
                if (offspring == null) continue;
                if (offspring instanceof MammalProperties animal)
                {
                    offspring.setPos(entity.position());
                    animal.setFamiliarity(getFamiliarity() < 0.9F ? getFamiliarity() / 2.0F : getFamiliarity() * 0.9F);
                    server.addFreshEntity(offspring);
                }
            }
        }
    }

    @Override
    default boolean isReadyToMate()
    {
        return getPregnantTime() <= 0 && TFCAnimalProperties.super.isReadyToMate();
    }

    @Override
    default void onFertilized(TFCAnimalProperties male)
    {
        //Mark the day this female became pregnant
        TFCAnimalProperties.super.onFertilized(male); // setFertilized(true)
        setPregnantTime(calendar().getTotalDays());

        CompoundTag genes = new CompoundTag();
        createGenes(genes, male);
        setGenes(genes.isEmpty() ? null : genes);
    }

    default void createGenes(CompoundTag tag, TFCAnimalProperties male)
    {
        tag.putInt("size", male.getGeneticSize() + getGeneticSize());
        tag.putBoolean("runt", getEntity().getRandom().nextInt(20) == 0);
    }

    @Override
    default void setBabyTraits(TFCAnimalProperties baby)
    {
        TFCAnimalProperties.super.setBabyTraits(baby);
        if (getGenes() != null)
        {
            applyGenes(getGenes(), (MammalProperties) baby);
        }
    }

    /**
     * @param baby refers to the BABY ANIMAL! Do not modify the mother animal (which is the caller)
     */
    default void applyGenes(CompoundTag tag, MammalProperties baby)
    {
        baby.setGeneticSize(Mth.floor(EntityHelpers.getIntOrDefault(tag, "size", 16) / 2d + Mth.nextInt(baby.getEntity().getRandom(), -3, 3)));
        if (tag.getBoolean("runt"))
        {
            baby.setGeneticSize(1);
        }
    }

    @Override
    default void showExtraClickInfo(Player player)
    {
        if (isFertilized())
        {
            player.displayClientMessage(Component.translatable("tfc.tooltip.animal.pregnant", getEntity().getName().getString()), true);
        }
    }

    @Override
    default void saveCommonAnimalData(CompoundTag nbt)
    {
        TFCAnimalProperties.super.saveCommonAnimalData(nbt);
        nbt.putLong("pregnant", getPregnantTime());
        if (getGenes() != null)
        {
            nbt.put("genes", getGenes());
        }
    }

    @Override
    default void readCommonAnimalData(CompoundTag nbt)
    {
        TFCAnimalProperties.super.readCommonAnimalData(nbt);
        setPregnantTime(nbt.getLong("pregnant"));
        if (nbt.contains("genes"))
        {
            setGenes(nbt.getCompound("genes"));
        }
    }

    default int getChildCount()
    {
        return getMammalConfig().childCount().get();
    }

    default long getGestationDays()
    {
        return getMammalConfig().gestationDays().get();
    }
}
