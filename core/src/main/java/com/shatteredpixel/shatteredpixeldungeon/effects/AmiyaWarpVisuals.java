// Arknights Pixel Dungeon: Amiya frozen visual integration, holy1ow, 2026-10-20.
// SPDX-License-Identifier: GPL-3.0-or-later

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon.WarpBeaconTracker;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;

/** Fixed-cell cosmetic animations. No RNG, turns, movement, or gameplay callbacks. */
public final class AmiyaWarpVisuals {
    private AmiyaWarpVisuals() {}

    public static Image beacon(Char owner, WarpBeaconTracker tracker, int cell, int branch, Emitter stock) {
        if (!(owner instanceof Hero) || ((Hero)owner).heroClass != HeroClass.AMIYA
                || owner.sprite == null || owner.sprite.parent == null || branch != Dungeon.branch) return null;
        Visual image = new Visual((Hero)owner, cell, "beacon-idle", new int[]{250,250,250,250});
        image.tracker = tracker;
        image.stock = stock;
        owner.sprite.parent.addToBack(image);
        image.refreshVisibility();
        return image;
    }

    public static void teleport(Char owner, int cell, boolean arriving) {
        teleport(owner, cell, arriving, 0);
    }

    /** PixelScene's standard black fade lasts one second; delay only the drawing. */
    public static void arrivalAfterTransition(Char owner) {
        teleport(owner, owner.pos, true, 1f);
    }

    private static void teleport(Char owner, int cell, boolean arriving, float visualDelay) {
        if (!(owner instanceof Hero) || !AmiyaEffects.eligible((Hero)owner)
                || owner.sprite == null || owner.sprite.parent == null) return;
        Visual image = new Visual((Hero)owner, cell, arriving ? "warp-arrive" : "warp-depart",
                arriving ? new int[]{80,80,100,120} : new int[]{80,80,80,100});
        image.age = -visualDelay;
        owner.sprite.parent.addToFront(image);
        image.refreshVisibility();
    }

    public static final class Visual extends Image {
        public final String kind;
        private final Hero owner;
        private final int cell, depth, branch;
        private final TextureFilm film;
        private final int[] durations;
        private float age;
        private WarpBeaconTracker tracker;
        private Emitter stock;

        private Visual(Hero owner, int cell, String kind, int[] durations) {
            super("effects/operators/amiya/" + kind + ".png");
            this.owner=owner;this.cell=cell;this.kind=kind;this.durations=durations;
            depth=Dungeon.depth;branch=Dungeon.branch;
            film=new TextureFilm(texture,24,24);frame(film.get(0));
            PointF p=DungeonTilemap.tileToWorld(cell);
            x=p.x+8-12;y=p.y+16-21;
        }

        private void refreshVisibility() {
            boolean active=AmiyaEffects.eligible(owner);
            visible=age >= 0 && active && Dungeon.level.heroFOV[cell];
            if (tracker == null) visible &= owner.invisible == 0;
            // Keep the stock emitter running to preserve its original lifecycle/RNG.
            if (stock != null) stock.visible=!active;
        }

        @Override public void update() {
            super.update();
            if (Dungeon.hero != owner || Dungeon.depth != depth || Dungeon.branch != branch
                    || owner.sprite == null || owner.sprite.parent != parent
                    || (tracker != null && owner.buff(WarpBeaconTracker.class) != tracker)) {
                killAndErase();return;
            }
            age+=Game.elapsed;float ms=age*1000;int total=0;
            if (age < 0) { refreshVisibility(); return; }
            for(int duration:durations)total+=duration;
            if(tracker != null)ms%=total;
            else if(ms>=total){killAndErase();return;}
            int i=0;while(i<3 && ms>=durations[i])ms-=durations[i++];
            frame(film.get(i));refreshVisibility();
        }

        @Override public void kill() {
            if(stock != null)stock.visible=true;
            super.kill();
        }
    }
}
