/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Consumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;


public class BlowpipeItem extends Item
{
    public static Item transform(Item blowpipe)
    {
        if (blowpipe == TFCItems.BLOWPIPE.get())
        {
            return TFCItems.BLOWPIPE_WITH_GLASS.get();
        }
        if (blowpipe == TFCItems.BLOWPIPE_WITH_GLASS.get())
        {
            return TFCItems.BLOWPIPE.get();
        }
        if (blowpipe == TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.get())
        {
            return TFCItems.CERAMIC_BLOWPIPE.get();
        }
        if (blowpipe == TFCItems.CERAMIC_BLOWPIPE.get())
        {
            return TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS.get();
        }
        return blowpipe;
    }

    public BlowpipeItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 80;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.SPYGLASS;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess)
            {
                if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0)
                {
                    poseStack.translate(0f, -0.125f, 0f);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
}
