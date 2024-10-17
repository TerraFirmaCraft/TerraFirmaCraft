/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

/**
 * Documentation for {@link InteractionResult} and the interaction flow.
 *
 * <h3>Client Interaction Flow</h3>
 *
 * The control flow of interacting starts from {@link Minecraft#startUseItem()}, on client. This will attempt to use both hands in sequence,
 * based on what we interact with (an entity, block, or empty hit). This may react to the action taken by a number of steps:
 * <ul>
 *     <li>If the result is <strong>consumed</strong> by the action, we inform the server, and we do not process off-hand. If the result
 *     <strong>fails</strong> while interacting with a block, we do not process off-hand.</li>
 *     <li>If the result will <strong>swing the player's arm</strong>, we do that.</li>
 * </ul>
 * At this level, we have {@link InteractionResult#SUCCESS SUCCESS} (consume and swing), {@link InteractionResult#CONSUME CONSUME} (consume only),
 * {@link InteractionResult#FAIL FAIL} (consume, only if interacting with a block), and {@link InteractionResult#PASS PASS}.
 *
 * <h5>Interacting with Blocks</h5>
 *
 * Next, in {@link MultiPlayerGameMode#performUseItemOn}, we do a series of attempted interactions, with a given hand. In all cases, {@code PASS}
 * will pass to the next action in sequence, and consuming will stop the processing.
 * <ol>
 *     <li>{@link RightClickBlock RightClickBlock} - If cancelled, will return</li>
 *     <li>{@link ItemStack#onItemUseFirst} - If consumed, OR failed, will return</li>
 *     <li>{@link BlockState#useItemOn} - If consumed, will return. Note that this can also skip the next step (via
 *     {@link ItemInteractionResult#SKIP_DEFAULT_BLOCK_INTERACTION SKIP_DEFAULT_BLOCK_INTERACTION}), without consuming.</li>
 *     <li>{@link BlockState#useWithoutItem} - If consumed, will return.</li>
 *     <li>{@link ItemStack#useOn}</li>
 * </ol>
 *
 * <h5>Interacting with Air</h5>
 *
 * If we did not target a block, the flow is different. Notably, if the stack is empty, we only receive a client event and have to recreate any
 * handling ourselves. Otherwise, we essentially delegate to {@link ItemStack#use}, which is notable in that it replaces the used item stack
 * with the returned one - hence returning an {@link InteractionResultHolder}.
 *
 * <h3>Server Interaction Flow</h3>
 *
 * The server-side flow for both cases is similar - the client tries to mirror the logic, and predict what the server would do to minimize latency.
 * We also have additional actions that are performed only on the server:
 * <ul>
 *     <li>If the action is consumed, we invoke criteria triggers for use-item-on-block, or use-block, respectively.</li>
 *     <li>If the action <strong>indicates an item was used</strong>, we increment the stat for that item use.</li>
 * </ul>
 * Both of these are typically going to be fine with the "default handling" in all or almost-all cases. Vanilla only uses
 * {@link InteractionResult#SUCCESS_NO_ITEM_USED} when using an item to cause a dog to sit, for example.
 *
 * <h3>Sided Success</h3>
 *
 * {@link InteractionResult#sidedSuccess} either returns {@code SUCCESS} on client (causing the action to consume, and swing), or {@code CONSUME} on
 * server (causing the action to consume). This is related to how arm swinging is handled:
 * <ul>
 *     <li>When a swing is invoked on server, the server swings the arm, then broadcasts to the player and all others, that the arm was swung</li>
 *     <li>When a swing is invoked on client, the client swings the arm, then broadcasts to the server to swing the arm, and notify other players</li>
 * </ul>
 * Using sided-success just enforces the swing only happening once (rather than twice, if {@code SUCCESS} was used). This reduces network traffic and,
 * in high-latency scenarios, might cause a player to visually swing their arm twice. As such, we should almost-never be returning {@code SUCCESS}
 * directly, unless we are sure we are in a client-only environment.
 */
@SuppressWarnings("unused")
public interface DocumentedInteractionResult {}
