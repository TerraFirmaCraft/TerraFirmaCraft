package net.dries007.tfc.objects.blocks.wood;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumMap;

public class BlockBookshelfTFC extends Block {
    private static final EnumMap<Wood, BlockBookshelfTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockBookshelfTFC get(Wood wood) {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockBookshelfTFC(Wood wood) {
        super(Material.WOOD);
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
        setSoundType(SoundType.WOOD);
        setHardness(2.0F).setResistance(5.0F);
        setHarvestLevel("axe", 0);
        OreDictionaryHelper.register(this, "bookshelf");
        Blocks.FIRE.setFireInfo(this, 30, 20);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

}