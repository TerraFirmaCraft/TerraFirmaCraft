package net.dries007.tfc;

/**
 * This is a really stupid way to check if we're running in a testing environment... by trying to class load this class, and if it fails, well, we're in dev!
 */
@SuppressWarnings("unused")
public final class TestMarker {}
