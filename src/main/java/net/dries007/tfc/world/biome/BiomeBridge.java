/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.function.Supplier;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

/**
 * The bridge interface for biomes, allowing them to cache their TFC specific property on them.
 * Do not call directly.
 *
 * @see TFCBiomes#getExtension(CommonLevelAccessor, Biome)
 */
public interface BiomeBridge
{
    @Nullable
    BiomeExtension tfc$getExtension(CommonLevelAccessor level);
}
