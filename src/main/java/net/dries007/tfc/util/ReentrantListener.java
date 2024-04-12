/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

/**
 * Listens to a value - in practice, a {@link net.minecraft.world.level.GameRules GameRule} rule change, and provides mechanisms for updating that value without triggering recursive or reentrant updates.
 * <ul>
 *     <li>First, the game rule callback is setup to call {@link #onListenerUpdate()}</li>
 *     <li>This will call the provided {@link #onListenerUpdateCallback}</li>
 *     <li>The listener update is then free to make any modifications to the underlying value, without triggering a recursive listener update. This is the key feature: the listener update callback <strong>may change the game rule</strong>.</li>
 *     <li>Finally, {@link #runWithoutTriggeringCallbacks(Runnable)} can be used to run <strong>any task</strong>, including updating the value of the game rule, in a way which does not trigger any callbacks.</li>
 * </ul>
 */
public class ReentrantListener
{
    private final Runnable onListenerUpdateCallback;
    private boolean working = false;

    public ReentrantListener(Runnable onListenerUpdateCallback)
    {
        this.onListenerUpdateCallback = onListenerUpdateCallback;
    }

    /**
     * Runs an action, that may update the underlying value being listened to, but will never call {@link #onListenerUpdateCallback}
     */
    public void runWithoutTriggeringCallbacks(Runnable runner)
    {
        boolean prev = working;
        working = true;
        runner.run();
        working = prev;
    }

    /**
     * Callback to be used to listen to the underlying value change.
     */
    public void onListenerUpdate()
    {
        if (!working)
        {
            working = true;
            onListenerUpdateCallback.run();
            working = false;
        }
    }
}