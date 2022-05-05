package net.dries007.tfc.common.entities.predator;

import net.dries007.tfc.client.RenderHelpers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Cougar extends FelinePredator {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Cougar.class, EntityDataSerializers.INT);

    private static final ResourceLocation[] LOCATIONS = {
        RenderHelpers.animalTexture("cougar"),
        RenderHelpers.animalTexture("panther")
    };

    public Cougar(EntityType<? extends Predator> type, Level level) {
        super(type, level, true);
    }

    public int getVariant()
    {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(int variant)
    {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant);
    }

    public ResourceLocation getTextureLocation()
    {
        return LOCATIONS[getVariant()];
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance diff, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        data = super.finalizeSpawn(level, diff, type, data, tag);

        //1 inb 10 chance for a cougar to be a panther
        if (random.nextInt(10) > 0) {
            setVariant(0);
        } else {
            setVariant(1);
        }

        return data;
    }


    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("Variant"));
    }

}
