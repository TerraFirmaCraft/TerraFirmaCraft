/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Map;
import java.util.stream.Collectors;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.GrillBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.GrillBlockEntity.*;

public class GrillBlock extends FirepitBlock implements IHighlightHandler
{
    public static int getSlotForSelection(BlockHitResult result)
    {
        final Vec3 location = result.getLocation();
        final BlockPos pos = result.getBlockPos();
        for (Map.Entry<Integer, AABB> entry : SLOT_BOUNDS.entrySet())
        {
            if (entry.getValue().move(pos).contains(location))
            {
                return entry.getKey();
            }
        }
        return -1;
    }

    private static final Map<Integer, VoxelShape> SLOT_RENDER_SHAPES = Map.of(
        SLOT_EXTRA_INPUT_END, Shapes.box(0.4, 0.65, 0.4, 0.6, 0.7, 0.6),
        SLOT_EXTRA_INPUT_START + 3, Shapes.box(0.6, 0.6, 0.6, 0.8, 0.7, 0.8),
        SLOT_EXTRA_INPUT_START + 2, Shapes.box(0.6, 0.6, 0.2, 0.8, 0.7, 0.4),
        SLOT_EXTRA_INPUT_START + 1, Shapes.box(0.2, 0.6, 0.6, 0.4, 0.7, 0.8),
        SLOT_EXTRA_INPUT_START, Shapes.box(0.2, 0.65, 0.2, 0.4, 0.7, 0.4)
    );

    private static final Map<Integer, AABB> SLOT_BOUNDS = SLOT_RENDER_SHAPES.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().bounds().inflate(0.01f)));
    public static final Map<Integer, Vec3> SLOT_CENTERS = SLOT_BOUNDS.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getCenter().multiply(1, 0, 1).add(0, 0.625, 0)));

    private static final VoxelShape GRILL_SHAPE = Shapes.or(BASE_SHAPE, box(2, 0, 2, 14, 11, 14));

    public GrillBlock(ExtendedProperties properties)
    {
        super(properties, GRILL_SHAPE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        super.animateTick(state, level, pos, random);
        if (state.getValue(LIT))
        {
            if (level.getBlockEntity(pos) instanceof GrillBlockEntity grill)
            {
                final var inv = Helpers.getCapability(grill, Capabilities.ITEM);
                if (inv != null)
                {
                    SLOT_CENTERS.forEach((slot, vec) -> {
                        if (!inv.getStackInSlot(slot).isEmpty() && random.nextFloat() < 0.4f)
                        {
                            final double x = vec.x + pos.getX();
                            final double y = vec.y + pos.getY();
                            final double z = vec.z + pos.getZ();
                            level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.25F, random.nextFloat() * 0.7F + 0.4F, false);
                            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
                        }
                    });
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof GrillBlockEntity grill)
        {
            final ItemStack stack = player.getItemInHand(hand);
            final var inv = Helpers.getCapability(grill, Capabilities.ITEM);
            final int slot = getSlotForSelection(result);
            final ItemStack current = slot == -1 || inv == null ? ItemStack.EMPTY : inv.getStackInSlot(slot);
            if (!stack.isEmpty() && inv != null && slot != -1 && current.isEmpty() && inv.isItemValid(slot, stack))
            {
                ItemHandlerHelper.giveItemToPlayer(player, inv.insertItem(slot, stack.split(1), false));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (stack.isEmpty() && slot != -1 && inv != null && !current.isEmpty())
            {
                // if we are shifting or if there's no possible recipe (eg, this heating has already been completed)
                if (!inv.isItemValid(slot, current) || player.isShiftKeyDown())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inv.extractItem(slot, 64, false));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            if (stack.isEmpty() && player.isShiftKeyDown())
            {
                if (!level.isClientSide)
                {
                    if (state.getValue(LIT))
                    {
                        TFCDamageSources.grill(player, 1f);
                        Helpers.playSound(level, pos, TFCSounds.ITEM_COOL.get());
                    }
                    else
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCItems.WROUGHT_IRON_GRILL.get()));
                        AbstractFirepitBlockEntity.convertTo(level, pos, state, grill, TFCBlocks.FIREPIT.get());
                    }
                }
                return InteractionResult.SUCCESS;
            }
            else if (tryInsertLog(player, stack, grill, result.getLocation().y - pos.getY() < 0.6))
            {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else
            {
                if (player instanceof ServerPlayer serverPlayer)
                {
                    Helpers.openScreen(serverPlayer, grill, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean drawHighlight(Level level, BlockPos pos, Player player, BlockHitResult rayTrace, PoseStack stack, MultiBufferSource buffers, Vec3 rendererPosition)
    {
        final int slot = getSlotForSelection(rayTrace);
        if (slot != -1)
        {
            IHighlightHandler.drawBox(stack, SLOT_RENDER_SHAPES.get(slot), buffers, pos, rendererPosition, 1f, 0f, 0f, 1f);
            return true;
        }
        return false;
    }

    @Override
    public double getParticleHeightOffset()
    {
        return 0.8D;
    }
}
