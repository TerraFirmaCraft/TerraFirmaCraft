/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemGoldPan extends ItemTFC
{
    public static final String[] TYPES = new String[] {"empty", "sand", "gravel", "clay", "dirt"};

    public ItemGoldPan()
    {
        setMaxDamage(0);
        setMaxStackSize(1);
        setNoRepair();
        setHasSubtypes(true);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (hand != EnumHand.MAIN_HAND)
        {
            return EnumActionResult.PASS;
        }
        if (player.getHeldItem(hand).getItemDamage() > 0 && !canPan(worldIn, player)) return EnumActionResult.FAIL;
        player.setActiveHand(hand);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        return super.getTranslationKey(stack) + "." + TYPES[stack.getItemDamage()];
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entityLiving)
    {
        if (!stack.isEmpty() && !world.isRemote && entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            if (stack.getItemDamage() > 0)
            {
                // Check if player still is looking at water
                if (canPan(world, player))
                {
                    Chunk chunk = world.getChunk(player.getPosition());
                    ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(chunk);
                    // Only pan for native nuggets in sand + gravel
                    if (stack.getItemDamage() == 1 || stack.getItemDamage() == 2)
                    {
                        if (chunkDataTFC.canWork(6))
                        {
                            Random rand = new Random(world.getSeed() + chunk.getPos().x * 241179128412L + chunk.getPos().z * 327910215471L);
                            List<Ore> chunkOres = TFCRegistries.ORES.getValuesCollection()
                                .stream().filter(Ore::canPan).filter(x -> rand.nextDouble() < x.getChunkChance())
                                .collect(Collectors.toList());

                            chunkOres.forEach(x -> {
                                if (Constants.RNG.nextDouble() < x.getPanChance())
                                {
                                    Helpers.spawnItemStack(world, player.getPosition(), new ItemStack(ItemSmallOre.get(x)));
                                }
                            });
                            chunkDataTFC.addWork(6);
                            stack.setItemDamage(0);
                        }
                        // todo: pan for seeds or stuff in dirt / grass / clay????
                        else
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.goldpan.chunkworked"));
                        }
                    }

                    if (Constants.RNG.nextFloat() < 0.01) // 1/100 chance, same as 1.7.10
                    {
                        stack.shrink(1);
                        world.playSound(null, entityLiving.getPosition(), TFCSounds.CERAMIC_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    }
                }
            }
            else
            {
                //Try to fill the gold pan
                RayTraceResult result = rayTrace(world, player, false);
                //noinspection ConstantConditions
                if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) return stack;
                BlockPos pos = result.getBlockPos();
                IBlockState state = world.getBlockState(pos);
                if (!(state.getBlock() instanceof BlockRockVariant) || (result.sideHit != null && world.getBlockState(pos.offset(result.sideHit)).getMaterial().isLiquid()))
                {
                    return stack;
                }
                Rock.Type type = ((BlockRockVariant) state.getBlock()).getType();
                if (type == Rock.Type.SAND)
                {
                    stack.setItemDamage(1);
                }
                else if (type == Rock.Type.GRAVEL)
                {
                    stack.setItemDamage(2);
                }
                else if (type == Rock.Type.CLAY)
                {
                    stack.setItemDamage(3);
                }
                else if (type == Rock.Type.DIRT || type == Rock.Type.GRASS)
                {
                    stack.setItemDamage(4);
                }
                if (stack.getItemDamage() > 0 && Constants.RNG.nextFloat() < 0.10f)
                {
                    world.setBlockToAir(pos);
                }
            }
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return stack.getItemDamage() > 0 ? 54 : 1;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            for (int meta = 0; meta < TYPES.length; meta++)
            {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    private boolean canPan(World world, EntityPlayer player)
    {
        RayTraceResult result = rayTrace(world, player, true);
        //noinspection ConstantConditions
        if (result == null) return false;
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) return false;
        BlockPos pos = result.getBlockPos();
        return world.getBlockState(pos).getMaterial() == Material.WATER;
    }
}
