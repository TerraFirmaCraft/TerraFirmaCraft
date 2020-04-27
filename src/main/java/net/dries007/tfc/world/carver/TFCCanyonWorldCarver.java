package net.dries007.tfc.world.carver;

import java.util.Set;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.objects.types.RockManager;

public class TFCCanyonWorldCarver extends CanyonWorldCarver
{
    private final Set<Block> originalCarvableBlocks;

    public TFCCanyonWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> dynamic)
    {
        super(dynamic);
        originalCarvableBlocks = carvableBlocks;

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> carvableBlocks = TFCWorldCarvers.fixCarvableBlocksList(carvableBlocks));
    }
}
