/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCParrot extends Parrot
{
    public static List<Variant> getClimateVariants(LevelReader level, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(level, pos);
        final float temperature = EnvironmentHelpers.adjustAvgTempForElev(pos.getY(), data.getAverageSeaLevelTemp(pos));
        final float groundwater = data.getAverageGroundwater(pos);
        final int forest = data.getForestType().getDensity();

        final List<Variant> variants = new ArrayList<>();
        if (temperature >= 21 && groundwater >= 250 && forest >= 2) variants.add(Variant.RED_BLUE);
        if (temperature >= 20 && groundwater >= 150 && groundwater <= 300 && forest <= 2) variants.add(Variant.BLUE);
        if (temperature >= 18 && groundwater >= 350 && forest >= 3) variants.add(Variant.GREEN);
        if (temperature >= 21 && groundwater >= 350) variants.add(Variant.YELLOW_BLUE);
        if (temperature >= 12 && temperature <= 30 && groundwater >= 60 && groundwater <= 200 && forest <= 1) variants.add(Variant.GRAY);
        return variants;
    }

    public TFCParrot(EntityType<? extends Parrot> type, Level level)
    {
        super(type, level);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        final List<Variant> variants = getClimateVariants(level, blockPosition());
        if (!variants.isEmpty())
        {
            setVariant(variants.get(random.nextInt(variants.size())));
        }
        return spawnData;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType)
    {
        final BlockPos pos = blockPosition();
        final int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
        return pos.getY() >= surfaceY
            && level.getFluidState(pos.atY(surfaceY - 1)).isEmpty()
            && !getClimateVariants(level, pos).isEmpty()
            && super.checkSpawnRules(level, spawnType);
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (!isTame() && Helpers.isItem(player.getItemInHand(hand), ItemTags.PARROT_FOOD))
        {
            return InteractionResult.PASS; // wild parrots cannot be tamed
        }
        return super.mobInteract(player, hand);
    }
}
