/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.objects.te.TEAnvilTFC.SLOT_HAMMER;

@ParametersAreNonnullByDefault
public class BlockStoneAnvil extends Block implements IRockObject
{
    private static final Map<Rock, BlockStoneAnvil> MAP = new HashMap<>();
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.875, 1);

    public static BlockStoneAnvil get(Rock rock)
    {
        return MAP.get(rock);
    }

    private final Rock rock;

    public BlockStoneAnvil(Rock rock)
    {
        super(Material.ROCK);

        setSoundType(SoundType.STONE);
        setHardness(2.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 0);

        this.rock = rock;
        MAP.put(rock, this);
    }

    @Nonnull
    @Override
    public Rock getRock(ItemStack stack)
    {
        return rock;
    }

    @Nonnull
    @Override
    public RockCategory getRockCategory(ItemStack stack)
    {
        return rock.getRockCategory();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTopSolid(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return AABB;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 1 + random.nextInt(3);
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ItemRock.get(rock);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEAnvilTFC te = Helpers.getTE(worldIn, pos, TEAnvilTFC.class);
        if (te == null)
        {
            return false;
        }
        IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (cap == null)
        {
            return false;
        }
        ItemStack heldItem = playerIn.getHeldItem(hand);
        // First check for a possible recipe action (welding)
        if (te.isItemValid(SLOT_HAMMER, heldItem))
        {
            if (te.attemptWelding(playerIn))
            {
                // Valid welding occurred.
                worldIn.playSound(null, pos, TFCSounds.ANVIL_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return true;
            }
        }
        if (playerIn.isSneaking())
        {
            // Extract requires an empty hand
            if (heldItem.isEmpty())
            {
                // Only check the input slots
                for (int i = 0; i < 2; i++)
                {
                    ItemStack stack = cap.getStackInSlot(i);
                    if (!stack.isEmpty())
                    {
                        // Give the item to player in the main hand
                        ItemStack result = cap.extractItem(i, 1, false);
                        playerIn.setHeldItem(hand, result);

                    }
                }
            }
        }
        else
        {
            // Not sneaking = insert items
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!stack.isEmpty())
            {
                for (int i = 0; i <= 4; i++)
                {
                    // Check the input slots and flux. Do NOT check the hammer slot
                    if (i == SLOT_HAMMER) continue;
                    // Try to insert an item
                    // Do not insert hammers into the input slots
                    if (te.isItemValid(i, stack) && cap.getStackInSlot(i).isEmpty() && !te.isItemValid(SLOT_HAMMER, stack))
                    {
                        if (!worldIn.isRemote)
                        {
                            ItemStack result = cap.insertItem(i, stack, false);
                            playerIn.setHeldItem(hand, result);
                            TerraFirmaCraft.getLog().info("Inserted {} into slot {}", stack.getDisplayName(), i);
                        }
                        return true;
                    }
                }
            }

            // No insertion happened, so try and open GUI
            if (!worldIn.isRemote)
            {
                TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.ANVIL);
            }
            return true;
        }
        return false;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && te instanceof TEInventory)
        {
            ((TEInventory) te).onBreakBlock(worldIn, pos);
        }
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.DOWN;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEAnvilTFC();
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(BlockRockRaw.get(rock, Rock.Type.RAW));
    }
}
