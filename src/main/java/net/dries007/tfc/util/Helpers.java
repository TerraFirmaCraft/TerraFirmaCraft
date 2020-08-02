/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.entity.EntitySeatOn;
import net.dries007.tfc.objects.entity.animal.*;

public final class Helpers
{
    private static final Joiner JOINER_DOT = Joiner.on('.');
    private static final boolean JEI = Loader.isModLoaded("jei");
    /**
     * Vanilla entities that are replaced to TFC counterparts on spawn
     */
    private static final Map<Class<? extends Entity>, Class<? extends Entity>> VANILLA_REPLACEMENTS;

    /**
     * Extra entities that are prevented to spawn on surface and aren't considered monsters (like Skeleton Horses)
     */
    private static final Set<Class<? extends Entity>> PREVENT_ON_SURFACE;

    static
    {
        PREVENT_ON_SURFACE = new HashSet<>();
        PREVENT_ON_SURFACE.add(EntityZombieHorse.class);
        PREVENT_ON_SURFACE.add(EntitySkeletonHorse.class);
        VANILLA_REPLACEMENTS = new HashMap<>();
        VANILLA_REPLACEMENTS.put(EntityCow.class, EntityCowTFC.class);
        VANILLA_REPLACEMENTS.put(EntitySheep.class, EntitySheepTFC.class);
        VANILLA_REPLACEMENTS.put(EntityPig.class, EntityPigTFC.class);
        VANILLA_REPLACEMENTS.put(EntityMule.class, EntityMuleTFC.class);
        VANILLA_REPLACEMENTS.put(EntityHorse.class, EntityHorseTFC.class);
        VANILLA_REPLACEMENTS.put(EntityDonkey.class, EntityDonkeyTFC.class);
        VANILLA_REPLACEMENTS.put(EntityChicken.class, EntityChickenTFC.class);
        VANILLA_REPLACEMENTS.put(EntityRabbit.class, EntityRabbitTFC.class);
        VANILLA_REPLACEMENTS.put(EntityWolf.class, EntityWolfTFC.class);
        VANILLA_REPLACEMENTS.put(EntityOcelot.class, EntityOcelotTFC.class);
        VANILLA_REPLACEMENTS.put(EntityPolarBear.class, EntityPolarBearTFC.class);
        VANILLA_REPLACEMENTS.put(EntityParrot.class, EntityParrotTFC.class);
        VANILLA_REPLACEMENTS.put(EntityLlama.class, EntityLlamaTFC.class);
    }

    public static boolean isJEIEnabled()
    {
        return JEI;
    }

    /**
     * Return true if the entity is from vanilla and have a TFC counterpart
     *
     * @param entity the entity to check
     * @return true if it has a TFC counterpart, false otherwise
     */
    public static boolean isVanillaAnimal(Entity entity)
    {
        return VANILLA_REPLACEMENTS.get(entity.getClass()) != null;
    }

    @Nullable
    public static Entity getTFCReplacement(Entity entity)
    {
        Class<? extends Entity> animalClass = VANILLA_REPLACEMENTS.get(entity.getClass());
        if (animalClass != null)
        {
            try
            {
                return animalClass.getConstructor(World.class).newInstance(entity.world);
            }
            catch (Exception ignored)
            {
            }
        }
        return null;
    }

    public static boolean shouldPreventOnSurface(Entity entity)
    {
        return PREVENT_ON_SURFACE.contains(entity.getClass()) || entity.isCreatureType(EnumCreatureType.MONSTER, false);
    }

    /**
     * Makes an entity sit on a block
     *
     * @param world    the worldObj
     * @param pos      the BlockPos of the block to sit on
     * @param creature the entityLiving that will sit on this block
     * @param yOffset  the y offset of the top facing
     */
    public static void sitOnBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLiving creature, double yOffset)
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
    public static Entity getSittingEntity(@Nonnull World world, @Nonnull BlockPos pos)
    {
        if (!world.isRemote)
        {
            List<EntitySeatOn> seats = world.getEntitiesWithinAABB(EntitySeatOn.class, new AxisAlignedBB(pos).grow(1D));
            for (EntitySeatOn seat : seats)
            {
                if (pos.equals(seat.getPos()))
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
        return JOINER_DOT.join(TerraFirmaCraft.MOD_ID, "enum", anEnum.getDeclaringClass().getSimpleName(), anEnum).toLowerCase();
    }

    public static String getTypeName(IForgeRegistryEntry<?> type)
    {
        //noinspection ConstantConditions
        return JOINER_DOT.join(TerraFirmaCraft.MOD_ID, "types", type.getRegistryType().getSimpleName(), type.getRegistryName().getPath()).toLowerCase();
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

    /**
     *
     */
    public static void handleRightClickBlockPostEventWithCallbacks(PlayerInteractEvent.RightClickBlock event, @Nullable Supplier<EnumActionResult> onItemUseCallback)
    {
        event.setCanceled(true);
        EnumActionResult result = EnumActionResult.PASS;
        // todo: verify stack is correct
        ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
        // todo: find hit pos from ray trace
        int hitX = 0, hitY = 0, hitZ = 0;
        EnumFacing face = event.getFace() == null ? EnumFacing.UP : event.getFace();
        if (event.getUseItem() != Event.Result.DENY)
        {
            result = stack.onItemUseFirst(event.getEntityPlayer(), event.getWorld(), event.getPos(), event.getHand(), face, hitX, hitY, hitZ);
            if (result != EnumActionResult.PASS)
            {
                event.setCancellationResult(result);
                return;
            }
        }

        boolean bypass = event.getEntityPlayer().getHeldItemMainhand().doesSneakBypassUse(event.getWorld(), event.getPos(), event.getEntityPlayer()) && event.getEntityPlayer().getHeldItemOffhand().doesSneakBypassUse(event.getWorld(), event.getPos(), event.getEntityPlayer());

        if (!event.getEntityPlayer().isSneaking() || bypass || event.getUseBlock() == Event.Result.ALLOW)
        {
            IBlockState iblockstate = event.getWorld().getBlockState(event.getPos());
            if (event.getUseBlock() != Event.Result.DENY)
                if (iblockstate.getBlock().onBlockActivated(event.getWorld(), event.getPos(), iblockstate, event.getEntityPlayer(), event.getHand(), face, hitX, hitY, hitZ))
                {
                    result = EnumActionResult.SUCCESS;
                }
        }

        if (stack.isEmpty())
        {
            event.setCancellationResult(EnumActionResult.PASS);
        }
        else if (event.getEntityPlayer().getCooldownTracker().hasCooldown(stack.getItem()))
        {
            event.setCancellationResult(EnumActionResult.PASS);
        }
        else
        {
            if (stack.getItem() instanceof ItemBlock && !event.getEntityPlayer().canUseCommandBlock())
            {
                Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockCommandBlock || block instanceof BlockStructure)
                {
                    event.setCancellationResult(EnumActionResult.FAIL);
                    return;
                }
            }

            if (event.getEntityPlayer().isCreative())
            {
                int j = stack.getMetadata();
                int i = stack.getCount();
                if (result != EnumActionResult.SUCCESS && event.getUseItem() != Event.Result.DENY
                    || result == EnumActionResult.SUCCESS && event.getUseItem() == Event.Result.ALLOW)
                {
                    EnumActionResult enumactionresult;
                    if (onItemUseCallback != null)
                    {
                        enumactionresult = onItemUseCallback.get();
                    }
                    else
                    {
                        enumactionresult = stack.onItemUse(event.getEntityPlayer(), event.getWorld(), event.getPos(), event.getHand(), face, hitX, hitY, hitZ);
                    }
                    stack.setItemDamage(j);
                    stack.setCount(i);
                    event.setCancellationResult(enumactionresult);
                }
                else
                {
                    event.setCancellationResult(result);
                }
            }
            else
            {
                if (result != EnumActionResult.SUCCESS && event.getUseItem() != Event.Result.DENY
                    || result == EnumActionResult.SUCCESS && event.getUseItem() == Event.Result.ALLOW)
                {
                    ItemStack copyBeforeUse = stack.copy();
                    result = stack.onItemUse(event.getEntityPlayer(), event.getWorld(), event.getPos(), event.getHand(), event.getFace(), hitX, hitY, hitZ);
                    if (stack.isEmpty())
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(event.getEntityPlayer(), copyBeforeUse, event.getHand());
                }
                event.setCancellationResult(result);
            }
        }
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
     * Used because {@link Collections#singletonList(Object)} is immutable
     */
    public static <T> List<T> listOf(T element)
    {
        List<T> list = new ArrayList<>(1);
        list.add(element);
        return list;
    }

    /**
     * Used because {@link Arrays#asList(Object[])} is immutable
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements)
    {
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
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