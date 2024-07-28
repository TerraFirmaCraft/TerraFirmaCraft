/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blockentities.ComposterBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;

public class TFCComposterBlock extends BottomSupportedDeviceBlock implements EntityBlockExtension, HoeOverlayBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_8;
    public static final EnumProperty<CompostType> TYPE = TFCBlockStateProperties.COMPOST_TYPE;

    private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[9], shapes -> {
        shapes[0] = Block.box(1D, 1D, 1D, 15D, 16D, 15D);
        for (int i = 1; i < 9; ++i)
        {
            shapes[i] = Shapes.join(Shapes.block(), Block.box(1.0D, Math.max(2, i * 2), 1.0D, 15.0D, 16.0D, 15.0D), BooleanOp.ONLY_FIRST);
        }
    });

    public TFCComposterBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0).setValue(TYPE, CompostType.NORMAL));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        return level.getBlockEntity(pos, TFCBlockEntities.COMPOSTER.get())
            .map(composter -> composter.use(player.getItemInHand(hand), player, level.isClientSide))
            .orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        level.getBlockEntity(pos, TFCBlockEntities.COMPOSTER.get()).ifPresent(ComposterBlockEntity::randomTick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        final CompostType type = state.getValue(TYPE);
        if (type == CompostType.NORMAL) return;
        SimpleParticleType particle = type == CompostType.READY ? TFCParticles.COMPOST_READY.get().getType() : TFCParticles.COMPOST_ROTTEN.get().getType();

        final double x = pos.getX() + random.nextDouble();
        final double y = pos.getY() + 1 + random.nextDouble() / 5D;
        final double z = pos.getZ() + random.nextDouble();
        final int count = Mth.nextInt(random, 0, 4);
        for (int i = 0; i < count; i++)
        {
            level.addParticle(particle, x, y, z, 0D, 0D, 0D);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(STAGE)];
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean isDebug)
    {
        if (level.getBlockEntity(pos) instanceof ComposterBlockEntity composter && state.getValue(TYPE) != CompostType.ROTTEN)
        {
            text.accept(Component.translatable("tfc.composter.green_items", composter.getGreen()).withStyle(ChatFormatting.GREEN));
            text.accept(Component.translatable("tfc.composter.brown_items", composter.getBrown()).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STAGE, TYPE));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof ComposterBlockEntity composter)
        {
            composter.resetCounter();
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(TYPE) != CompostType.ROTTEN;
    }

    public enum CompostType implements StringRepresentable
    {
        NORMAL, READY, ROTTEN;

        private final String serializedName;

        CompostType()
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}
