// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.services.updates;

/** Updates and store reviews are disabled for this edition. */
public class UpdateImpl {

    public static UpdateService getUpdateService() {
        return null;
    }

    public static boolean supportsUpdates() {
        return false;
    }
}
