package net.dries007.tfc.objects.blocks.wood;

import java.util.EnumMap;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockWorkbenchTFC extends BlockWorkbench
{
    private static final EnumMap<Wood, BlockWorkbenchTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockWorkbenchTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockWorkbenchTFC(Wood wood)
    {
        super();
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
        setSoundType(SoundType.WOOD);
        setHardness(2.0F).setResistance(5.0F);
        setHarvestLevel("axe", 0);
        OreDictionaryHelper.register(this, "workbench");
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
}