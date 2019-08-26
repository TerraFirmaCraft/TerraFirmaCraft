/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.blocks.BlockPeat;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.entity.EntitySeatOn;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public final class Helpers
{
    private static final Joiner JOINER_DOT = Joiner.on('.');

    public static void spreadGrass(World world, BlockPos pos, IBlockState us, Random rand)
    {
        if (world.getLightFromNeighbors(pos.up()) < 4 && world.getBlockState(pos.up()).getLightOpacity(world, pos.up()) > 2)
        {
            if (us.getBlock() instanceof BlockPeat)
            {
                world.setBlockState(pos, BlocksTFC.PEAT.getDefaultState());
            }
            else if (us.getBlock() instanceof BlockRockVariant)
            {
                BlockRockVariant block = ((BlockRockVariant) us.getBlock());
                world.setBlockState(pos, block.getVariant(block.getType().getNonGrassVersion()).getDefaultState());
            }
        }
        else
        {
            if (world.getLightFromNeighbors(pos.up()) < 9) return;

            for (int i = 0; i < 4; ++i)
            {
                BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                if (world.isOutsideBuildHeight(target) || !world.isBlockLoaded(target)) return;
                BlockPos up = target.add(0, 1, 0);

                IBlockState current = world.getBlockState(target);
                if (!BlocksTFC.isSoil(current) || BlocksTFC.isGrass(current)) continue;
                if (world.getLightFromNeighbors(up) < 4 || world.getBlockState(up).getLightOpacity(world, up) > 3)
                    continue;

                if (current.getBlock() instanceof BlockPeat)
                {
                    world.setBlockState(target, BlocksTFC.PEAT_GRASS.getDefaultState());
                }
                else if (current.getBlock() instanceof BlockRockVariant)
                {
                    Rock.Type spreader = Rock.Type.GRASS;
                    if ((us.getBlock() instanceof BlockRockVariant) && ((BlockRockVariant) us.getBlock()).getType() == Rock.Type.DRY_GRASS)
                        spreader = Rock.Type.DRY_GRASS;

                    BlockRockVariant block = ((BlockRockVariant) current.getBlock());
                    world.setBlockState(target, block.getVariant(block.getType().getGrassVersion(spreader)).getDefaultState());
                }
            }

            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.getPlantType() == Plant.PlantType.SHORT_GRASS && rand.nextFloat() < 0.5f)
                {
                    float temp = ClimateTFC.getActualTemp(world, pos.up());
                    BlockShortGrassTFC plantBlock = BlockShortGrassTFC.get(plant);

                    if (world.isAirBlock(pos.up()) &&
                        plant.isValidLocation(temp, ChunkDataTFC.getRainfall(world, pos.up()), Math.subtractExact(world.getLightFor(EnumSkyBlock.SKY, pos.up()), world.getSkylightSubtracted())) &&
                        plant.isValidGrowthTemp(temp) &&
                        rand.nextDouble() < plantBlock.getGrowthRate(world, pos.up()))
                    {
                        world.setBlockState(pos.up(), plantBlock.getDefaultState());
                    }
                }
            }
        }
    }

    /**
     * Gets a map of generated ores for each chunk in radius.
     * It takes account only loaded chunks, so if radius is too big you probably won't get an accurate data.
     *
     * @param world  the WorldObj
     * @param chunkX the center chunk's X position
     * @param chunkZ the center chunk's Z position
     * @param radius the radius to scan. can be 0 to scan only the central chunk
     * @return a map containing all ores generated for each chunk
     */
    public static Map<ChunkPos, Set<Ore>> getChunkOres(World world, int chunkX, int chunkZ, int radius)
    {
        Map<ChunkPos, Set<Ore>> map = new HashMap<>();
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
        {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++)
            {
                ChunkPos chunkPos = new ChunkPos(x, z);
                if (world.isBlockLoaded(chunkPos.getBlock(8, 0, 8)))
                {
                    Chunk chunk = world.getChunk(x, z);
                    ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
                    map.put(chunkPos, chunkData.getChunkOres());
                }
            }
        }
        return map;
    }

    /**
     * Makes an entity sit on a block
     *
     * @param world    the worldObj
     * @param pos      the BlockPos of the block to sit on
     * @param creature the entityLiving that will sit on this block
     * @param yOffset  the y offset of the top facing
     */
    public static void sitOnBlock(World world, BlockPos pos, EntityLiving creature, double yOffset)
    {
        if (!world.isRemote && !world.getBlockState(pos).getMaterial().isReplaceable())
        {
            EntitySeatOn seat = new EntitySeatOn(world, pos, yOffset);
            world.spawnEntity(seat);
            creature.startRiding(seat);
        }
    }

    /**
     * Returns the entity which is sitting on this BlockPos.
     *
     * @param world the WorldObj
     * @param pos   the BlockPos of this block
     * @return the entity which is sitting on this block, or null if none
     */
    @Nullable
    public static Entity getSittingEntity(World world, BlockPos pos)
    {
        if (!world.isRemote)
        {
            List<EntitySeatOn> seats = world.getEntitiesWithinAABB(EntitySeatOn.class, new AxisAlignedBB(pos).grow(1D));
            for (EntitySeatOn seat : seats)
            {
                if (seat.getPos().equals(pos))
                {
                    return seat.getSittingEntity();
                }
            }
        }
        return null;
    }

    /**
     * Copy from Item#rayTrace
     * Returns a RayTraceResult containing first found block in Players reach.
     *
     * @param worldIn    the world obj player stands in.
     * @param playerIn   the player obj
     * @param useLiquids do fluids counts as block?
     */
    @Nullable
    public static RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        Vec3d playerVec = new Vec3d(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
        float cosYaw = MathHelper.cos(-playerIn.rotationYaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-playerIn.rotationYaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-playerIn.rotationPitch * 0.017453292F);
        float sinPitch = MathHelper.sin(-playerIn.rotationPitch * 0.017453292F);
        double reachDistance = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d targetVec = playerVec.add((sinYaw * cosPitch) * reachDistance, sinPitch * reachDistance, (cosYaw * cosPitch) * reachDistance);
        return worldIn.rayTraceBlocks(playerVec, targetVec, useLiquids, !useLiquids, false);
    }

    /**
     * Copied from {@link net.minecraft.entity.Entity#rayTrace(double, float)} as it is client only
     *
     * @param blockReachDistance the reach distance
     * @param partialTicks       idk
     * @return the ray trace result
     */
    @Nullable
    public static RayTraceResult rayTrace(Entity entity, double blockReachDistance, float partialTicks)
    {
        Vec3d eyePosition = entity.getPositionEyes(partialTicks);
        Vec3d lookVector = entity.getLook(partialTicks);
        Vec3d rayTraceVector = eyePosition.add(lookVector.x * blockReachDistance, lookVector.y * blockReachDistance, lookVector.z * blockReachDistance);
        return entity.world.rayTraceBlocks(eyePosition, rayTraceVector, false, false, true);
    }

    public static boolean containsAnyOfCaseInsensitive(Collection<String> input, String... items)
    {
        Set<String> itemsSet = Arrays.stream(items).map(String::toLowerCase).collect(Collectors.toSet());
        return input.stream().map(String::toLowerCase).anyMatch(itemsSet::contains);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> T getTE(IBlockAccess world, BlockPos pos, Class<T> aClass)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!aClass.isInstance(te)) return null;
        return (T) te;
    }

    public static String getEnumName(Enum<?> anEnum)
    {
        return JOINER_DOT.join(TFCConstants.MOD_ID, "enum", anEnum.getDeclaringClass().getSimpleName(), anEnum).toLowerCase();
    }

    public static String getTypeName(IForgeRegistryEntry<?> type)
    {
        //noinspection ConstantConditions
        return JOINER_DOT.join(TFCConstants.MOD_ID, "types", type.getRegistryType().getSimpleName(), type.getRegistryName().getPath()).toLowerCase();
    }

    public static boolean playerHasItemMatchingOre(InventoryPlayer playerInv, String ore)
    {
        for (ItemStack stack : playerInv.mainInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        for (ItemStack stack : playerInv.armorInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        for (ItemStack stack : playerInv.offHandInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, int amount)
    {
        if (stack.getCount() <= amount)
        {
            return ItemStack.EMPTY;
        }
        stack.shrink(amount);
        return stack;
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, EntityPlayer player, int amount)
    {
        return player.isCreative() ? stack : consumeItem(stack, amount);
    }

    public static void damageItem(ItemStack stack)
    {
        damageItem(stack, 1);
    }

    /**
     * Utility method for damaging an item that doesn't take an entity
     *
     * @param stack the stack to be damaged
     */
    public static void damageItem(ItemStack stack, int amount)
    {
        if (stack.attemptDamageItem(amount, Constants.RNG, null))
        {
            stack.shrink(1);
            stack.setItemDamage(0);
        }
    }

    /**
     * Simple method to spawn items in the world at a precise location, rather than using InventoryHelper
     */
    public static void spawnItemStack(World world, BlockPos pos, ItemStack stack)
    {
        if (stack.isEmpty())
            return;
        EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        world.spawnEntity(entityitem);
    }

    /**
     * Method for hanging blocks to check if they can hang. 11/10 description.
     * NOTE: where applicable, remember to still check if the blockstate allows for the specified direction!
     *
     * @param pos    position of the block that makes the check
     * @param facing the direction the block is facing. This is the direction the block should be pointing and the side it hangs ON, not the side it sticks WITH.
     *               e.g: a sign facing north also hangs on the north side of the support block
     * @return true if the side is solid, false otherwise.
     */
    public static boolean canHangAt(World worldIn, BlockPos pos, EnumFacing facing)
    {
        return worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing);
    }

    /**
     * Primarily for use in placing checks. Determines a solid side for the block to attach to.
     *
     * @param pos             position of the block/space to be checked.
     * @param possibleSides   a list/array of all sides the block can attach to.
     * @param preferredFacing this facing is checked first. It can be invalid or null.
     * @return Found facing or null is none is found. This is the direction the block should be pointing and the side it stick TO, not the side it sticks WITH.
     */
    public static EnumFacing getASolidFacing(World worldIn, BlockPos pos, @Nullable EnumFacing preferredFacing, EnumFacing... possibleSides)
    {
        return getASolidFacing(worldIn, pos, preferredFacing, Arrays.asList(possibleSides));
    }

    public static EnumFacing getASolidFacing(World worldIn, BlockPos pos, @Nullable EnumFacing preferredFacing, Collection<EnumFacing> possibleSides)
    {
        if (preferredFacing != null && possibleSides.contains(preferredFacing) && canHangAt(worldIn, pos, preferredFacing))
        {
            return preferredFacing;
        }
        for (EnumFacing side : possibleSides)
        {
            if (side != null && canHangAt(worldIn, pos, side))
            {
                return side;
            }
        }
        return null;
    }

    public static void writeResourceLocation(ByteBuf buf, @Nullable ResourceLocation loc)
    {
        buf.writeBoolean(loc != null);
        if (loc != null)
        {
            ByteBufUtils.writeUTF8String(buf, loc.toString());
        }
    }


    @Nullable
    public static ResourceLocation readResourceLocation(ByteBuf buf)
    {
        if (buf.readBoolean())
        {
            return new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        }
        return null;
    }

    /**
     * This is meant to avoid Intellij's warnings about null fields that are injected to at runtime
     * Use this for things like @ObjectHolder, @CapabilityInject, etc.
     * AKA - The @Nullable is intentional. If it crashes your dev env, then fix your dev env, not this. :)
     *
     * @param <T> anything and everything
     * @return null, but not null
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T getNull()
    {
        return null;
    }
}