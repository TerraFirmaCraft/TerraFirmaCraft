package net.dries007.tfc.objects.blocks.wood;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.te.TEChestTFC;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.EnumMap;

public class BlockChestTFC extends BlockChest
{
    private static final EnumMap<Wood, BlockChestTFC> MAP_BASIC = new EnumMap<>(Wood.class);
    private static final EnumMap<Wood, BlockChestTFC> MAP_TRAP = new EnumMap<>(Wood.class);

    public static BlockChestTFC getBasic(Wood wood)
    {
        return MAP_BASIC.get(wood);
    }
    public static BlockChestTFC getTrap(Wood wood)
    {
        return MAP_TRAP.get(wood);
    }

    public final Wood wood;

    public BlockChestTFC(Type type, Wood wood)
    {
        super(type);
        this.wood = wood;
        setHardness(2.5F);
        setSoundType(SoundType.WOOD);
        switch (type)
        {
            case BASIC: if (MAP_BASIC.put(wood, this) != null) throw new IllegalStateException("There can only be one."); break;
            case TRAP: if (MAP_TRAP.put(wood, this) != null) throw new IllegalStateException("There can only be one."); break;
            default:
                throw new IllegalStateException();
        }
        TileEntity.register(TEChestTFC.ID.toString(), TEChestTFC.class);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEChestTFC();
    }
}
