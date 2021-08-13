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
