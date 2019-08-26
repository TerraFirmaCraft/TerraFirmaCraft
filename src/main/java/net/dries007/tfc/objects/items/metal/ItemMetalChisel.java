package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.api.capability.skill.CapabilityPlayerSkills;
import net.dries007.tfc.api.capability.skill.IPlayerSkills;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockRaw;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.container.ContainerEmpty;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemMetalChisel extends ItemMetalTool
{
    private static final int[] STAIR_PATTERN_INDICES = {0, 3, 4, 6, 7, 8};
    private static final int[] SLAB_PATTERN_INDICES = {0, 1, 2};

    public ItemMetalChisel(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        // no chiseling if no hammer is present
        if (!hasHammerInToolbar(player))
            return EnumActionResult.FAIL;

        IBlockState state = worldIn.getBlockState(pos);

        // no chiseling for raw stone that is blocked
        if (isRawAndBlocked(worldIn, state, pos))
            return EnumActionResult.FAIL;

        IPlayerSkills capability = player.getCapability(CapabilityPlayerSkills.CAPABILITY, null);

        // if the capability for chisel modes is gone there's nothing the chisel can do.
        if (capability == null)
            return EnumActionResult.FAIL;
        
        Block newBlock = null;
        int metadataPtr[] = new int[] {0};

        switch (capability.getChiselMode())
        {
            case SMOOTH:
            {
                if (BlocksTFC.isRawStone(state))
                {
                    BlockRockRaw rawBlock = (BlockRockRaw) state.getBlock();
                    newBlock = BlockRockVariant.get(rawBlock.getRock(), Rock.Type.SMOOTH);
                }
            }
            break;
            case SLAB:
            {
                newBlock = findCraftingResult(worldIn, state.getBlock(), SLAB_PATTERN_INDICES, metadataPtr);
                if (!(newBlock instanceof BlockSlab))
                    newBlock = null;
            }
            break;
            case STAIR:
            {
                newBlock = findCraftingResult(worldIn, state.getBlock(), STAIR_PATTERN_INDICES, metadataPtr);
                if (!(newBlock instanceof BlockStairs))
                    newBlock = null;
            }
            break;
        }

        // no new block means no updates
        if (newBlock == null)
        {
            return EnumActionResult.FAIL;
        }

        // play a sound matching the new block
        SoundType soundType = newBlock.getSoundType(state, worldIn, pos, player);
        worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, 1.0f, soundType.getPitch());

        // only update the world state on the server side
        if (!worldIn.isRemote)
        {
            // get the placement state
            if (facing.getAxis().getPlane() != EnumFacing.Plane.VERTICAL)
                hitY = 1 - hitY;
            IBlockState newState = newBlock.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, metadataPtr[0], player);

            // replace the block with a new block
            worldIn.setBlockState(pos, newState, 3);

            // reduce durability by 1
            player.getHeldItem(hand).damageItem(1, player);
        }

        return EnumActionResult.SUCCESS;
    }

    @Nullable
    private static Block findCraftingResult(World world, Block craftingBlock, int[] craftingIndices, int[] metadata)
    {
        ItemStack ingredient = new ItemStack(craftingBlock);
        InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerEmpty(), 3, 3);
        for (int index : craftingIndices)
        {
            craftMatrix.setInventorySlotContents(index, ingredient.copy());
        }

        for (IRecipe recipe : ForgeRegistries.RECIPES.getValuesCollection())
        {
            if (recipe.matches(craftMatrix, world))
            {
                // Found matching recipe, try and extract a block
                ItemStack stackOut = recipe.getCraftingResult(craftMatrix);
                if (stackOut.getItem() instanceof ItemBlock)
                {
                    metadata[0] = stackOut.getMetadata();
                    return ((ItemBlock) stackOut.getItem()).getBlock();
                }
                return null;
            }
        }
        return null;
    }

    private static boolean hasHammerInToolbar(EntityPlayer player)
    {
        for (int i = 0; i < 9; i++)
        {
            if (OreDictionaryHelper.doesStackMatchOre(player.inventory.mainInventory.get(i), "hammer"))
            {
                return true;
            }
        }

        return false;
    }

    private static boolean isRawAndBlocked(World world, IBlockState state, BlockPos pos)
    {
        if (!BlocksTFC.isRawStone(state))
        {
            return false;
        }

        IBlockState above1 = world.getBlockState(pos.up(1));
        IBlockState above2 = world.getBlockState(pos.up(2));

        return BlocksTFC.isRawStone(above1) && BlocksTFC.isRawStone(above2);

    }
}
