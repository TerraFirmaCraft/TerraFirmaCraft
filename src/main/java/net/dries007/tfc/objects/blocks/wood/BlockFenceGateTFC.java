package net.dries007.tfc.objects.blocks.wood;

import java.util.EnumMap;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockFenceGateTFC extends BlockFenceGate
{
    private static final EnumMap<Wood, BlockFenceGateTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockFenceGateTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockFenceGateTFC(Wood wood)
    {
        super(BlockPlanks.EnumType.OAK);
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
        setHarvestLevel("axe", 0);
        OreDictionaryHelper.register(this, "fence", "gate");
        OreDictionaryHelper.register(this, "fence", "gate", wood);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}
