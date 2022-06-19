package net.dries007.tfc.common.entities.livestock.horse;

import java.util.function.Supplier;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.animals.MammalConfig;
import org.jetbrains.annotations.Nullable;

public class TFCMule extends TFCChestedHorse
{
    public TFCMule(EntityType<? extends TFCMule> type, Level level)
    {
        super(type, level, TFCSounds.MULE, () -> SoundEvents.MULE_EAT, () -> SoundEvents.MULE_ANGRY, TFCConfig.SERVER.muleConfig);
    }

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        return false;
    }

    @Override
    public boolean checkExtraBreedConditions(TFCAnimalProperties other)
    {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        return TFCEntities.MULE.get().create(level); // left for spawn egg creation
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.MULE_FOOD;
    }
}
