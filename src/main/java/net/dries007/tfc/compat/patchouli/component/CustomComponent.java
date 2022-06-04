/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.Optional;
import java.util.function.UnaryOperator;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import org.slf4j.Logger;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public abstract class CustomComponent implements ICustomComponent
{
    private static final Logger LOGGER = LogUtils.getLogger();

    protected transient int x, y;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public abstract void render(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY);

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {}

    protected void renderSetup(PoseStack poseStack)
    {
        poseStack.pushPose();
        poseStack.translate(x, y, 0);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, PatchouliIntegration.TEXTURE);
    }

    protected void renderFluidStack(PoseStack stack, FluidStack fluid, int x, int y)
    {
        if (!fluid.isEmpty())
        {
            final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(fluid);
            GuiComponent.blit(stack, x, y, 0, 16, 16, sprite);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends Recipe<?>> Optional<T> asRecipe(String variable, RecipeType<T> type)
    {
        return asResourceLocation(variable)
            .flatMap(e -> {
                final Level level = ClientHelpers.getLevel();
                assert level != null;
                return level.getRecipeManager().byKey(e)
                    .flatMap(recipe -> {
                        if (recipe.getType() != type)
                        {
                            LOGGER.error("The recipe {} of type {} is not of type {}", e, Registry.RECIPE_TYPE.getKey(recipe.getType()), type);
                            return Optional.empty();
                        }
                        return Optional.of((T) recipe);
                    })
                    .or(() -> {
                        LOGGER.error("No recipe of type {} named {} ", Registry.RECIPE_TYPE.getKey(type), e);
                        return Optional.empty();
                    });
            });
    }

    protected Optional<ResourceLocation> asResourceLocation(String variable)
    {
        try
        {
            return Optional.of(new ResourceLocation(variable));
        }
        catch (ResourceLocationException e)
        {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }
}
