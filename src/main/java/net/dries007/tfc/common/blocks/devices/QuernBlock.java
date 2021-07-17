package net.dries007.tfc.common.blocks.devices;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.tileentity.QuernTileEntity;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.tileentity.QuernTileEntity.*;

public class QuernBlock extends DeviceBlock implements IHighlightHandler
{
    private static final VoxelShape BASE_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);

    private static final VoxelShape HANDSTONE_SHAPE = box(3.0D, 10.0D, 3.0D, 13.0D, 13.76D, 13.0D);
    private static final AxisAlignedBB HANDSTONE_AABB = HANDSTONE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape HANDLE_SHAPE = box(4.34D, 13.76D, 4.34D, 5.36D, 16.24D, 5.36D);
    private static final AxisAlignedBB HANDLE_AABB = HANDLE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape INPUT_SLOT_SHAPE = box(6.0D, 13.76D, 6.0D, 10.0D, 16.24D, 10.0D);
    private static final AxisAlignedBB INPUT_SLOT_AABB = INPUT_SLOT_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape FULL_SHAPE = VoxelShapes.join(VoxelShapes.or(BASE_SHAPE, HANDSTONE_SHAPE, HANDLE_SHAPE), INPUT_SLOT_SHAPE, IBooleanFunction.ONLY_FIRST);

    private static SelectionPlace getPlayerSelection(IBlockReader world, BlockPos pos, PlayerEntity player, BlockRayTraceResult result)
    {
        QuernTileEntity te = Helpers.getTileEntity(world, pos, QuernTileEntity.class);
        if (te != null)
        {
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inventory -> {
                ItemStack held = player.getItemInHand(Hand.MAIN_HAND);
                Vector3d hit = result.getLocation();
                if (te.hasHandstone())
                {
                    if (!te.isGrinding() && HANDLE_AABB.move(pos).contains(hit))
                    {
                        return SelectionPlace.HANDLE;
                    }
                    else if (!te.isGrinding() && !held.isEmpty() || !inventory.getStackInSlot(QuernTileEntity.SLOT_INPUT).isEmpty() && INPUT_SLOT_AABB.move(pos).contains(hit))
                    {
                        return SelectionPlace.INPUT_SLOT;
                    }
                }
                if ((te.hasHandstone() || te.isItemValid(QuernTileEntity.SLOT_HANDSTONE, held)) && HANDSTONE_AABB.move(pos).contains(hit))
                {
                    return SelectionPlace.HANDSTONE;
                }
                return SelectionPlace.BASE;
            }).orElse(SelectionPlace.BASE);
        }
        return SelectionPlace.BASE;
    }

    public QuernBlock(ForgeBlockProperties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (hand == Hand.MAIN_HAND)
        {
            QuernTileEntity teQuern = Helpers.getTileEntity(world, pos, QuernTileEntity.class);
            if (teQuern != null && !teQuern.isGrinding())
            {
                ItemStack heldStack = player.getItemInHand(hand);
                SelectionPlace selection = getPlayerSelection(world, pos, player, hit);
                return teQuern.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inventory -> {
                    if (selection == SelectionPlace.HANDLE)
                    {
                        teQuern.grind();
                        world.playSound(null, pos, TFCSounds.QUERN_DRAG.get(), SoundCategory.BLOCKS, 1, 1 + ((world.random.nextFloat() - world.random.nextFloat()) / 16));
                        return ActionResultType.sidedSuccess(world.isClientSide);
                    }
                    else if (selection == SelectionPlace.INPUT_SLOT)
                    {
                        player.setItemInHand(Hand.MAIN_HAND, teQuern.insertOrSwapItem(SLOT_INPUT, heldStack));
                        teQuern.setAndUpdateSlots(SLOT_INPUT);
                        return ActionResultType.sidedSuccess(world.isClientSide);
                    }
                    else if (selection == SelectionPlace.HANDSTONE && inventory.getStackInSlot(SLOT_HANDSTONE).isEmpty() && inventory.isItemValid(SLOT_HANDSTONE, heldStack))
                    {
                        player.setItemInHand(Hand.MAIN_HAND, teQuern.insertOrSwapItem(SLOT_HANDSTONE, heldStack));
                        teQuern.setAndUpdateSlots(SLOT_HANDSTONE);
                        return ActionResultType.sidedSuccess(world.isClientSide);
                    }
                    else if (selection == SelectionPlace.BASE && !inventory.getStackInSlot(SLOT_OUTPUT).isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(SLOT_OUTPUT, inventory.getStackInSlot(SLOT_OUTPUT).getCount(), false));
                        teQuern.setAndUpdateSlots(SLOT_OUTPUT);
                        return ActionResultType.sidedSuccess(world.isClientSide);
                    }
                    return ActionResultType.FAIL;
                }).orElse(ActionResultType.FAIL);
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        QuernTileEntity te = Helpers.getTileEntity(world, pos, QuernTileEntity.class);
        return te != null && te.hasHandstone() ? FULL_SHAPE : BASE_SHAPE;
    }

    @Override
    public boolean drawHighlight(World world, BlockPos pos, PlayerEntity player, BlockRayTraceResult rayTrace, MatrixStack matrixStack, IRenderTypeBuffer buffers, Vector3d renderPos)
    {
        SelectionPlace selection = getPlayerSelection(world, pos, player, rayTrace);
        if (selection != SelectionPlace.BASE)
        {
            IHighlightHandler.drawBox(matrixStack, selection.shape, buffers, pos, renderPos, 1.0F, 0.0F, 0.0F, 0.4F);
            return true;
        }
        return false;
    }

    /**
     * Just a helper enum to figure out where player is looking at
     * Used to draw selection boxes + handle interaction
     */
    private enum SelectionPlace
    {
        HANDLE(HANDLE_SHAPE),
        HANDSTONE(HANDSTONE_SHAPE),
        INPUT_SLOT(INPUT_SLOT_SHAPE),
        BASE(BASE_SHAPE);

        public final VoxelShape shape;

        SelectionPlace(VoxelShape shape)
        {
            this.shape = shape;
        }
    }
}
