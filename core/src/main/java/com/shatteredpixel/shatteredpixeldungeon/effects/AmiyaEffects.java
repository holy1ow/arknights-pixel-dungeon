// Arknights Pixel Dungeon: adapted by holy1ow, 2026-10-18; Amiya visual integration.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAppearance;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;

/** Optional appearance only. Never owns combat callbacks, spends time, or rolls RNG. */
public final class AmiyaEffects {
    private AmiyaEffects() {}

    public enum Kind {
        ARTS_PROJECTILE("arts-projectile", 18, 12, 80,80,80,80),
        ARTS_IMPACT("arts-impact", 12,12,45,65,80,100),
        SWORD_ARC("sword-arc",12,12,35,45,45,65),
        SWORD_IMPACT("sword-impact",12,12,35,45,55,75),
        STAFF_RELEASE("staff-release",12,12,40,50,60,80),
        HEAL_RETURN("heal-return",12,22,120,140,160,180);
        public final String texture;
        final int ax,ay;
        final int[] durations;
        Kind(String id,int ax,int ay,int... durations) {
            texture="effects/operators/amiya/"+id+".png";
            this.ax=ax;this.ay=ay;this.durations=durations;
        }
    }

    public static boolean eligible(Hero hero) {
        return hero != null && hero.heroClass==HeroClass.AMIYA
                && HeroAppearance.of(hero).identity==HeroClass.AMIYA;
    }

    public static boolean genericMagic(Hero hero,Wand wand) {
        // Explicit whitelist. Inheriting Wand.fx does not make a wand generic.
        return eligible(hero) && wand != null && wand.getClass()==WandOfMagicMissile.class && !wand.cursed;
    }

    private static boolean drawable(Hero hero) {
        return eligible(hero) && hero.sprite!=null && hero.sprite.parent!=null;
    }

    private static PointF artPoint(CharSprite s,float px,float py) {
        return new PointF(s.x+(s.flipHorizontal?18-px:px-2),s.y+py-2);
    }

    public static void wandRelease(Hero hero) {
        if (drawable(hero) && HeroAppearance.of(hero).subclass==HeroSubClass.WARLOCK) {
            attach(hero,Kind.STAFF_RELEASE,0);
        }
    }

    public static void swordSwing(Hero hero,int target) {
        if (drawable(hero) && HeroAppearance.of(hero).subclass==HeroSubClass.BATTLEMAGE
                && !(hero.belongings.attackingWeapon() instanceof MissileWeapon)
                && Dungeon.level!=null && Dungeon.level.adjacent(hero.pos,target)) {
            attach(hero,Kind.SWORD_ARC,0.05f);
        }
    }

    public static void swordHit(Hero hero,Char target,boolean hit) {
        if (hit && drawable(hero) && HeroAppearance.of(hero).subclass==HeroSubClass.BATTLEMAGE
                && !(hero.belongings.attackingWeapon() instanceof MissileWeapon)
                && target!=null && target.sprite!=null && Dungeon.level.adjacent(hero.pos,target.pos)) {
            impact(hero,target.sprite,Kind.SWORD_IMPACT);
        }
    }

    public static void magicHit(Hero hero,Wand wand,Char target) {
        if (genericMagic(hero,wand) && drawable(hero) && target!=null && target.sprite!=null) {
            impact(hero,target.sprite,Kind.ARTS_IMPACT);
        }
    }

    /** Called only inside the original positive SoulMark healing branch. */
    public static void soulRecovery(Hero hero) {
        if (drawable(hero) && HeroAppearance.of(hero).subclass==HeroSubClass.WARLOCK) {
            attach(hero,Kind.HEAL_RETURN,0);
        }
    }

    private static void attach(Hero hero,Kind kind,float delay) {
        Effect image=new Effect(kind,hero);
        image.attached=true;image.age=-delay;image.visible=delay==0 && hero.sprite.visible;
        if (kind==Kind.HEAL_RETURN) hero.sprite.parent.addToBack(image);
        else hero.sprite.parent.addToFront(image);
        image.position();
    }

    private static void impact(Hero hero,CharSprite target,Kind kind) {
        if (!target.visible || target.parent==null) return;
        Effect image=new Effect(kind,hero);
        image.fixed=target.center();image.flipHorizontal=hero.sprite.flipHorizontal;
        target.parent.addToFront(image);image.position();
    }

    public static void decorateMissile(Hero hero,Wand wand,MagicMissile missile,int cell) {
        if (!genericMagic(hero,wand) || !drawable(hero)) return;
        Effect image=new Effect(Kind.ARTS_PROJECTILE,hero);
        image.missile=missile;image.from=hero.sprite.center();
        Char target=Actor.findChar(cell);
        image.to=target!=null && target.sprite!=null?target.sprite.destinationCenter():DungeonTilemap.raisedTileCenterToWorld(cell);
        image.launch=HeroAppearance.of(hero).subclass==HeroSubClass.WARLOCK
                ?artPoint(hero.sprite,19,7):artPoint(hero.sprite,20,15);
        // Hide only the stock drawing. Its emitter, flight, RNG and callback keep running.
        missile.visible=false;
        missile.parent.addToFront(image);image.position();
    }

    public static final class Effect extends Image {
        public final Kind kind;
        private final Hero owner;
        private final TextureFilm film;
        private final HeroSubClass form;
        private float age;
        private boolean attached;
        private MagicMissile missile;
        private PointF fixed,from,to,launch;
        private Effect(Kind kind,Hero owner) {
            super(kind.texture);this.kind=kind;this.owner=owner;form=HeroAppearance.of(owner).subclass;
            film=new TextureFilm(texture,24,24);frame(film.get(0));origin.set(kind.ax,kind.ay);
            visible=owner.sprite.visible;alpha(owner.sprite.am);
        }
        private void position() {
            PointF p=fixed;
            if (attached) {
                CharSprite s=owner.sprite;flipHorizontal=s.flipHorizontal;
                p=kind==Kind.HEAL_RETURN?new PointF(s.x+s.width()/2,s.y+s.height())
                        :kind==Kind.STAFF_RELEASE?artPoint(s,19,7):artPoint(s,15,15);
            } else if (missile!=null) {
                PointF current=new PointF(missile.x+missile.width/2,missile.y+missile.height/2);
                float dx=to.x-from.x,dy=to.y-from.y,len=dx*dx+dy*dy;
                float progress=len==0?1:Math.max(0,Math.min(1,((current.x-from.x)*dx+(current.y-from.y)*dy)/len));
                p=new PointF(current.x+(launch.x-from.x)*(1-progress),current.y+(launch.y-from.y)*(1-progress));
                angle=(float)Math.toDegrees(Math.atan2(to.y-launch.y,to.x-launch.x));
            }
            if (p!=null) {x=p.x-(flipHorizontal?24-kind.ax:kind.ax);y=p.y-kind.ay;}
        }
        @Override public void update() {
            super.update();
            if (!eligible(owner) || owner.sprite==null || owner.sprite.parent!=parent
                    || HeroAppearance.of(owner).subclass!=form || (missile!=null && !missile.on)) {
                killAndErase();return;
            }
            age+=Game.elapsed;
            visible=age>=0 && owner.sprite.visible;alpha(owner.sprite.am);
            if (age<0) return;
            int total=0;for(int d:kind.durations)total+=d;
            float ms=age*1000;
            if (kind==Kind.ARTS_PROJECTILE) ms%=total;
            else if (ms>=total) {killAndErase();return;}
            int i=0;while(i<3 && ms>=kind.durations[i])ms-=kind.durations[i++];
            frame(film.get(i));position();
        }
    }
}
