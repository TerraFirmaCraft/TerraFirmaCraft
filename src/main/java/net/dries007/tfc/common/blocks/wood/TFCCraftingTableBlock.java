package net.dries007.tfc.common.blocks.wood;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.container.TFCContainerProviders;

public class TFCCraftingTableBlock extends Block implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;

    public TFCCraftingTableBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isClientSide)
        {
            return ActionResultType.SUCCESS;
        }
        else
        {
            NetworkHooks.openGui((ServerPlayerEntity) player, TFCContainerProviders.WORKBENCH, pos);
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
