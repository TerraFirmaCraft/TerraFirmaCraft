/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

public interface ExtendedCarvingContext
{
    BaseStoneSource getBaseStoneSource();

    class Impl extends CarvingContext implements ExtendedCarvingContext
    {
        private final BaseStoneSource stoneSource;

        public Impl(ChunkGenerator chunkGenerator, LevelHeightAccessor level, BaseStoneSource stoneSource)
        {
            super(chunkGenerator, level);
            this.stoneSource = stoneSource;
        }

        @Override
        public BaseStoneSource getBaseStoneSource()
        {
            return stoneSource;
        }
    }
}
