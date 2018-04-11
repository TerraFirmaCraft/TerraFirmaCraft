package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.items.ItemDoorTFC;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Random;

public class BlockDoorTFC extends BlockDoor
{
    private static final EnumMap<Wood, BlockDoorTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockDoorTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    protected BlockDoorTFC(Wood wood)
    {
        super(Material.WOOD);
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
        setSoundType(SoundType.WOOD);
        setHardness(3.0F);
        disableStats();
    }

    public Item getItem()
    {
        return ItemDoorTFC.get(wood);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : getItem();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(getItem());
    }
}
