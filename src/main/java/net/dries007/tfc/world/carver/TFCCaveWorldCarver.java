package net.dries007.tfc.world.carver;

import java.util.Set;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.objects.types.RockManager;

public class TFCCaveWorldCarver extends CaveWorldCarver
{
    private final Set<Block> originalCarvableBlocks;

    public TFCCaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> dynamic, int maxHeight)
    {
        super(dynamic, maxHeight);
        originalCarvableBlocks = carvableBlocks;

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> carvableBlocks = TFCWorldCarvers.fixCarvableBlocksList(originalCarvableBlocks));
    }
}
