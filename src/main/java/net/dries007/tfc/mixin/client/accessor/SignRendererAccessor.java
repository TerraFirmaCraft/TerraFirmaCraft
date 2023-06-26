package net.dries007.tfc.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SignRenderer.class)
public interface SignRendererAccessor
{
    @Invoker("renderSignWithText")
    void invoke$renderSignWithText(SignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, BlockState state, SignBlock block, WoodType wood, Model model);
}
