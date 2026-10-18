// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.services.news;

/** News is disabled until a feed is configured for this edition. */
public class NewsImpl {

    public static NewsService getNewsService() {
        return null;
    }

    public static boolean supportsNews() {
        return false;
    }
}
