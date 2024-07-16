/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

/**
 * This is a really stupid way to check if we're running in a testing environment... by trying to class load this class, and if it fails, well, we're in dev!
 */
@SuppressWarnings("unused")
public final class TestMarker {}
