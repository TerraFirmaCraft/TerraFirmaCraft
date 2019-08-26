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
import net.minecraft.item.Item;
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
import net.dries007.tfc.objects.blocks.BlockSlabTFC;
import net.dries007.tfc.objects.blocks.BlockStairsTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockRaw;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.container.ContainerEmpty;

import static net.minecraft.block.BlockSlab.EnumBlockHalf.BOTTOM;
import static net.minecraft.block.BlockSlab.EnumBlockHalf.TOP;
import static net.minecraft.block.BlockSlab.HALF;

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
        IBlockState state = worldIn.getBlockState(pos);
        IPlayerSkills capability = player.getCapability(CapabilityPlayerSkills.CAPABILITY, null);

        if (capability != null)
        {
            Block newBlock = null;
            int metadata[] = new int[]{0};

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
                    newBlock = findCraftingResult(worldIn, state.getBlock(), SLAB_PATTERN_INDICES, metadata);
                    if (!(newBlock instanceof BlockSlab))
                        newBlock = null;
                }
                break;
                case STAIR:
                {
                    newBlock = findCraftingResult(worldIn, state.getBlock(), STAIR_PATTERN_INDICES, metadata);
                    if (!(newBlock instanceof BlockStairs))
                        newBlock = null;
                }
                break;
            }

            if (newBlock != null)
            {
                // play a sound matching the new block
                SoundType soundType = newBlock.getSoundType(state, worldIn, pos, player);
                worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, 1.0f, soundType.getPitch());

                if (!worldIn.isRemote)
                {
                    // get the placement state
                    if (facing.getAxis().getPlane() != EnumFacing.Plane.VERTICAL)
                        hitY = 1 - hitY;
                    IBlockState newState = newBlock.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, metadata[0], player);

                    // replace the block with a new block
                    worldIn.setBlockState(pos, newState, 3);

                    // reduce durability by 1
                    player.getHeldItem(hand).damageItem(1, player);
                }

                return EnumActionResult.SUCCESS;
            }
            else
            {
                return EnumActionResult.FAIL;
            }
        }

        return EnumActionResult.FAIL;
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
}
