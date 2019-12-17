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

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.skill.CapabilityPlayerSkills;
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

    private static final int COOLDOWN = 10; // todo: make optional cooldown scale by metal tier or tool speed

    public ItemMetalChisel(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    /**
     * attempts to change a block in place using the chisel.
     * If the chiselMode is stair and the block can be crafted into a stair, it will be turned into that stair.
     * If the chiselMode is slab and the block can be crafted into a slab, it will be crafted into a slab.
     * If the chiselMode is polish and the block is a TFC Raw stone, it will be crafted into a polished stone.
     *
     * @param player  player player who clicked on the block
     * @param worldIn world the block is in
     * @param pos     pos of block interacted with
     * @param hand    hand that was used to interact with the block
     * @param facing  side of block that was hit
     * @param hitX    hit position on block : x dimension
     * @param hitY    hit position on block : y dimension
     * @param hitZ    hit position on block : z dimension
     * @return SUCCESS if the block was chiseled, FAIL if no block was changed
     */
    @Override
    @Nonnull
    public EnumActionResult onItemUse(
        @Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos,
        @Nonnull EnumHand hand, @Nonnull EnumFacing facing,
        float hitX, float hitY, float hitZ)
    {
        // Find the block to place for this action
        IBlockState newState = getChiselResultState(player, worldIn, pos, facing, hitX, hitY, hitZ);

        // no new block means no updates
        if (newState == null)
            return EnumActionResult.FAIL;

        // play a sound matching the new block
        SoundType soundType = newState.getBlock().getSoundType(newState, worldIn, pos, player);
        worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, 1.0f, soundType.getPitch());

        // only update the world state on the server side
        if (!worldIn.isRemote)
        {
            // replace the block with a new block
            worldIn.setBlockState(pos, newState);

            // use tool
            player.getHeldItem(hand).damageItem(1, player);

            // if setting is on for chisel cooldown, trigger cooldown
            if (ConfigTFC.GENERAL.chiselDelay)
                player.getCooldownTracker().setCooldown(this, COOLDOWN);
        }

        return EnumActionResult.SUCCESS;
    }

    /**
     * Calculates the block that would be set in the specified position if the chisel were used.<br>
     * <br>
     * the logic for determining chisel results is complicated. Here's an overview:<br>
     * <br>
     * // we always get the state from looking at the world<br>
     * state = world.getStateAt(pos)<br>
     * <br>
     * // this part can be skipped by caching state + pos to a resultBlock<br>
     * block = state.getBlock()<br>
     * itemStack = block.getPickBlock(state, pos)<br>
     * resultStack = CraftingInventory.getResult(itemStack)<br>
     * resultBlock = (resultStack.getItem() as ItemBlock).getBlock()<br>
     * <br>
     * // we always calculate the placement state from the block as the glance<br>
     * // moves around<br>
     * resultState = resultBlock.getPlacementState()<br>
     * <br>
     * On the topic of caching results:<br>
     * The problem that needed to be solved was that this function calculates
     * the resulting block using inventory crafting, without any form of gui. The
     * client has to render boxes that tells the player how their chisel will
     * affect the block. That box has to be rendered every frame, and could change
     * every frame based on what block the player was looking at, what chisel mode
     * the player is in, the all the details concerning how the player is looking
     * at the block.<br>
     * The solution was to cache the results of the transformation from source
     * block state and pos to the result block type. This way, the calculation only
     * ever needs to be done once every time the player looks at a new block. The
     * result can still differ using the getPlacementState() function, but the game
     * doesn't have to do the expensive call to check crafting results any more.<br>
     * It should be noted that the cache should only be used on client side
     * for the rendering calls. The server side must do its own calculation so that
     * it is certain that the result is valid, and it doesn't ever need to do that
     * calculation more than once. The client side needs the results all the time
     * though, so it uses an un-synced capability that maps from player to cache. <br>
     * The cache is also affected by chiselMode, so the entire key for the
     * cached block type result is (pos, state, chiselMode). When this method is
     * called from the client, and not every one of these arguments match, the
     * cache is recalculated.
     *
     * @param player  player who clicked on the block
     * @param worldIn world the block is in
     * @param pos     pos of block interacted with
     * @param facing  side of block that was hit
     * @param hitX    hit position on block : x dimension
     * @param hitY    hit position on block : y dimension
     * @param hitZ    hit position on block : z dimension
     * @return null if the operation would not succeed. resulting state for if it would succeed.
     */
    public static IBlockState getChiselResultState(
        @Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing,
        float hitX, float hitY, float hitZ)
    {
        // no chiseling if no hammer is present
        if (!hasHammerForChisel(player))
            return null;

        IBlockState state = worldIn.getBlockState(pos);

        // no chiseling for raw stone that is blocked
        if (isRawAndBlocked(worldIn, state, pos))
            return null;

        // get the capability that tells us the current player selected mode for chiseling
        IPlayerSkills capability = player.getCapability(CapabilityPlayerSkills.CAPABILITY, null);

        // if the capability for chisel modes is gone there's nothing the chisel can do.
        if (capability == null)
            return null;

        // get the type of block and metadata according to the state and capability
        int[] newMetadataPtr = new int[] {-1};
        Block newBlock = getBlockAndMetadata(player, worldIn, pos, capability.getChiselMode(), state, newMetadataPtr);

        if (newBlock == null || newMetadataPtr[0] == -1)
            return null;

        return getPlacementState(worldIn, pos, facing, hitX, hitY, hitZ, player, newBlock, newMetadataPtr[0]);
    }


    private static Block getBlockAndMetadata(
        @Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos,
        @Nonnull ChiselMode chiselMode, IBlockState targetState, int[] metadataPtr)
    {
        Block newBlock = null;
        int newMetadata = -1;

        if (chiselMode == ChiselMode.SMOOTH)
        {
            if (BlocksTFC.isRawStone(targetState))
            {
                BlockRockRaw rawBlock = (BlockRockRaw) targetState.getBlock();
                newBlock = BlockRockVariant.get(rawBlock.getRock(), Rock.Type.SMOOTH);
                newMetadata = 0;
            }
        }
        else if (chiselMode == ChiselMode.SLAB || chiselMode == ChiselMode.STAIR)
        {
            ItemStack resultItemStack = findCraftingResult(worldIn, targetState.getBlock().getPickBlock(
                targetState, null, worldIn, pos, player),
                (chiselMode == ChiselMode.SLAB ? SLAB_PATTERN_INDICES : STAIR_PATTERN_INDICES));

            if (resultItemStack != null)
            {
                Item resultItem = resultItemStack.getItem();
                Block block;
                if (resultItem instanceof ItemBlock)
                {
                    block = ((ItemBlock) resultItem).getBlock();

                    if ((chiselMode == ChiselMode.SLAB && block instanceof BlockSlab)
                        || (chiselMode == ChiselMode.STAIR && block instanceof BlockStairs))
                    {
                        newBlock = block;
                        newMetadata = resultItemStack.getMetadata();
                    }
                }
            }
        }

        if (newBlock == null || newMetadata == -1)
            return null;

        metadataPtr[0] = newMetadata;
        return newBlock;
    }

    /**
     * Determines what ItemBlocks can be crafted from a single ingredient in a given pattern
     *
     * @param world              world instance
     * @param craftingIngredient ingredient to use to try to craft stairs or slabs with
     * @param craftingIndices    stair pattern or slab pattern indices
     * @return the result of crafting the ingredient in the listed pattern, null if no crafting result
     */
    @Nullable
    private static ItemStack findCraftingResult(World world, ItemStack craftingIngredient, int[] craftingIndices)
    {
        InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerEmpty(), 3, 3);
        for (int index : craftingIndices)
        {
            craftMatrix.setInventorySlotContents(index, craftingIngredient.copy());
        }

        for (IRecipe recipe : ForgeRegistries.RECIPES.getValuesCollection())
        {
            if (recipe.matches(craftMatrix, world))
            {
                // Found matching recipe, make sure it represents a block first
                ItemStack stackOut = recipe.getCraftingResult(craftMatrix);
                if (stackOut.getItem() instanceof ItemBlock)
                {
                    return stackOut;
                }
            }
        }
        return null;
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    private static IBlockState getPlacementState(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing,
                                                 float hitX, float hitY, float hitZ, @Nonnull EntityPlayer player, @Nonnull Block block, int metadata)
    {
        if (facing.getAxis().getPlane() != EnumFacing.Plane.VERTICAL)
            hitY = 1 - hitY;
        return block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, metadata, player);
    }

    private static boolean hasHammerForChisel(@Nonnull EntityPlayer player)
    {
        // offhand always counts as a hammer slot
        if (OreDictionaryHelper.doesStackMatchOre(player.inventory.offHandInventory.get(0), "hammer"))
            return true;

        // config alters whether toolbar counts as a hammer slot or not.
        if (!ConfigTFC.GENERAL.requireHammerInOffHand)
        {
            for (int i = 0; i < 9; i++)
            {
                if (OreDictionaryHelper.doesStackMatchOre(player.inventory.mainInventory.get(i), "hammer"))
                {
                    return true;
                }
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
