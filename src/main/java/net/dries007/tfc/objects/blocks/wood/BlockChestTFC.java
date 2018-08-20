/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.te.TEChestTFC;

public class BlockChestTFC extends BlockChest
{
    private static final Map<Tree, BlockChestTFC> MAP_BASIC = new HashMap<>();
    private static final Map<Tree, BlockChestTFC> MAP_TRAP = new HashMap<>();

    public static BlockChestTFC getBasic(Tree wood)
    {
        return MAP_BASIC.get(wood);
    }

    public static BlockChestTFC getTrap(Tree wood)
    {
        return MAP_TRAP.get(wood);
    }

    public final Tree wood;

    public BlockChestTFC(Type type, Tree wood)
    {
        super(type);
        this.wood = wood;
        setHardness(2.5F);
        setSoundType(SoundType.WOOD);
        switch (type)
        {
            case BASIC:
                if (MAP_BASIC.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
                break;
            case TRAP:
                if (MAP_TRAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
                break;
            default:
                throw new IllegalStateException();
        }
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEChestTFC();
    }
}
