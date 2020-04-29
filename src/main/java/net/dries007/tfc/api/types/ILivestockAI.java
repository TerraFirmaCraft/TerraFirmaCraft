/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;


import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.objects.entity.animal.EntityLionTFC;
import net.dries007.tfc.objects.entity.animal.EntitySaberToothTFC;
import net.dries007.tfc.objects.entity.animal.EntityWolfTFC;

/**
 * Use this to add AI Tasks that are the same between all TFC Livestock
 * Does not apply to Vanilla derived mobs: Horse, Donkey, Mule, Llama, Camel
 * Does apply to at least Pig, Cow, Chicken, Sheep, Goat, Duck, Alpaca,
 * Used only in TFC worlds.
 */
public interface ILivestockAI extends ILivestock
{
    default void addCommonTasks (EntityAnimal animal)
    {
        animal.tasks.addTask(0, new EntityAISwimming(animal));
        animal.tasks.addTask(1, new EntityAIPanic(animal, 1.3D));
        animal.tasks.addTask(2, new EntityAIMate(animal, 1.0D));
        for (ItemStack is : OreDictionary.getOres("grain"))
        {
            Item item = is.getItem();
            animal.tasks.addTask(3, new EntityAITempt(animal, 1.1D, item, false));
        }
        animal.tasks.addTask(4, new EntityAIAvoidEntity<>(animal, EntityPlayer.class, 12.0F, 0.5D, 1.1D));
        animal.tasks.addTask(4, new EntityAIAvoidEntity<>(animal, EntityWolfTFC.class, 8.0F, 0.5D, 1.1D));
        animal.tasks.addTask(4, new EntityAIAvoidEntity<>(animal, EntityLionTFC.class, 8.0F, 0.5D, 1.1D)); //every IPredator is an Entity. Hmm.
        animal.tasks.addTask(4, new EntityAIAvoidEntity<>(animal, EntitySaberToothTFC.class, 8.0F, 0.5D, 1.1D)); //every IPredator is an Entity. Hmm.
        // space for follow parent for mammals, and find nest for oviparous
        animal.tasks.addTask(6, new EntityAIEatGrass(animal));
        animal.tasks.addTask(7, new EntityAIWanderAvoidWater(animal, 1.0D));
        animal.tasks.addTask(8, new EntityAIWatchClosest(animal, EntityPlayer.class, 6.0F));
        animal.tasks.addTask(9, new EntityAILookIdle(animal));
    }
}