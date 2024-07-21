/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.pet;

import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class Dog extends TamableMammal
{
    private float interestedAngle;
    private float interestedAngleO;

    public Dog(EntityType<? extends TFCAnimal> animal, Level level)
    {
        super(animal, level, TFCSounds.DOG, TFCConfig.SERVER.dogConfig);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (isAlive())
        {
            this.interestedAngleO = this.interestedAngle;
            interestedAngle += 0.4f * (isInterested() ? (1f - interestedAngle) : (0f - interestedAngle));
        }
    }

    public float getHeadRollAngle(float partialTick)
    {
        return Mth.lerp(partialTick, this.interestedAngleO, this.interestedAngle) * 0.15F * Mth.PI;
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.DOG_FOOD;
    }

    @Override
    public boolean canAttack(LivingEntity entity)
    {
        return super.canAttack(entity) && (Helpers.isEntity(entity, TFCTags.Entities.HUNTED_BY_DOGS) || entity instanceof Monster);
    }

}
