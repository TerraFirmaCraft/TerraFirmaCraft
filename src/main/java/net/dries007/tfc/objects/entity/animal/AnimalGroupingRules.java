/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.entity.EntityLiving;

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

/**
 * Helper enum with some default grouping rules for animals
 */
public enum AnimalGroupingRules implements BiConsumer<List<EntityLiving>, Random>
{
    MOTHER_AND_CHILDREN_OR_SOLO_MALE // One individual group = male / Two or more = Mother and children
        {
            @Override
            public void accept(List<EntityLiving> entityLivings, Random random)
            {
                for (int i = 0; i < entityLivings.size(); i++)
                {
                    EntityLiving living = entityLivings.get(i);
                    if (living instanceof IAnimalTFC)
                    {
                        IAnimalTFC animal = (IAnimalTFC) living;
                        if (i == 0)
                        {
                            // Mother
                            int lifeTimeDays = 1 + (int) Math.ceil(animal.getDaysToAdulthood() + animal.getDaysToElderly() * (0.05 + random.nextDouble()));
                            animal.setGender(entityLivings.size() > 1 ? IAnimalTFC.Gender.FEMALE : IAnimalTFC.Gender.MALE);
                            animal.setBirthDay((int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays));
                        }
                        else
                        {
                            // Children
                            int lifeTimeDays = random.nextInt(animal.getDaysToAdulthood());
                            animal.setGender(IAnimalTFC.Gender.valueOf(random.nextBoolean()));
                            animal.setBirthDay((int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays));
                        }
                    }
                }
            }
        },
    ELDER_AND_POPULATION // First always adult
        {
            @Override
            public void accept(List<EntityLiving> entityLivings, Random random)
            {
                for (int i = 0; i < entityLivings.size(); i++)
                {
                    EntityLiving living = entityLivings.get(i);
                    if (living instanceof IAnimalTFC)
                    {
                        IAnimalTFC animal = (IAnimalTFC) living;
                        if (i == 0)
                        {
                            // Elder
                            int lifeTimeDays = 1 + (int) Math.ceil(animal.getDaysToAdulthood() + animal.getDaysToElderly() * (0.33 + random.nextDouble()));
                            animal.setGender(IAnimalTFC.Gender.valueOf(random.nextBoolean()));
                            animal.setBirthDay((int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays));
                        }
                        else
                        {
                            // Weighted towards adult individuals
                            double growth = Math.pow(random.nextDouble(), 0.5D);
                            double maxLifetime = 1 + animal.getDaysToAdulthood() * 1.25D + animal.getDaysToElderly();
                            int lifeTimeDays = (int) (maxLifetime * growth);
                            animal.setGender(IAnimalTFC.Gender.FEMALE);
                            animal.setBirthDay((int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays));
                        }
                    }
                }
            }
        },
    MALE_AND_FEMALES // One adult male (or solo males) + random females
        {
            @Override
            public void accept(List<EntityLiving> entityLivings, Random random)
            {
                for (int i = 0; i < entityLivings.size(); i++)
                {
                    EntityLiving living = entityLivings.get(i);
                    if (living instanceof IAnimalTFC)
                    {
                        IAnimalTFC animal = (IAnimalTFC) living;
                        if (i == 0)
                        {
                            // Male
                            int lifeTimeDays = 1 + (int) Math.ceil(animal.getDaysToAdulthood() + animal.getDaysToElderly() * (0.2 + random.nextDouble()));
                            animal.setGender(IAnimalTFC.Gender.MALE);
                            animal.setBirthDay((int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays));
                        }
                        else
                        {
                            // Weighted towards adult individuals
                            double growth = Math.pow(random.nextDouble(), 0.5D);
                            double maxLifetime = 1 + animal.getDaysToAdulthood() * 1.25D + animal.getDaysToElderly();
                            int lifeTimeDays = (int) (maxLifetime * growth);
                            animal.setGender(IAnimalTFC.Gender.FEMALE);
                            animal.setBirthDay((int) (CalendarTFC.PLAYER_TIME.getTotalDays() - lifeTimeDays));
                        }
                    }
                }
            }
        }

}