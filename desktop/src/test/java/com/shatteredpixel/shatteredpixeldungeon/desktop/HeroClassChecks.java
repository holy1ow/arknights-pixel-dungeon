// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany.LightAllySprite;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.RectF;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.*;

/** Real game initialization, serialization and native texture decoding; never uses player saves. */
public class HeroClassChecks {
    private static int checks;
    static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    static void eq(float a,float b) { check(Math.abs(a-b)<.001f,a+" != "+b); }
    static class UnlinkedHeroSprite extends HeroSprite {
        @Override public void link(Char hero) { ch=hero; hero.sprite=this; }
    }
    public static void main(String[] args) throws Exception {
        GdxNativesLoader.load();
        Gdx.files=new Lwjgl3Files();
        Path scratch=java.nio.file.Files.createTempDirectory(Path.of(args[0]),"profile-");
        SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(scratch.resolve("settings.xml").toString(),Files.FileType.Absolute)));
        FileUtils.setDefaultFileProperties(Files.FileType.Absolute,scratch.toString()+"/");
        Gdx.app=(Application)Proxy.newProxyInstance(Application.class.getClassLoader(),new Class[]{Application.class},(p,m,a)->{
            if (m.getName().equals("getType")) return Application.ApplicationType.Desktop;
            if (m.getName().equals("log") || m.getName().equals("error")) System.out.println(Arrays.toString(a));
            return null;
        });
        Game.version="0.0.3"; Game.versionCode=922;
        Game.platform=new DesktopPlatformSupport();
        new ShatteredPixelDungeon(Game.platform);
        SPDSettings.challenges(0);
        Messages.setup(Languages.CHI_SMPL);
        String[] original={"WARRIOR","MAGE","ROGUE","HUNTRESS","DUELIST","CLERIC"};
        for(int i=0;i<original.length;i++) check(HeroClass.values()[i].name().equals(original[i]),"old ordinal "+i);
        check(HeroClass.values().length==12 && HeroClass.AMIYA.ordinal()==6,"appended identity");
        check(HeroClass.selectableClasses().length==6,"six public reskins");
        for(int i=0;i<6;i++) {
            HeroClass skin=HeroClass.selectableClasses()[i], base=HeroClass.gameplayClasses()[i];
            check(skin.isOperator() && skin.gameplayClass()==base,"public gameplay mapping");
            check(base.asSelectable()==skin && skin.asSelectable()==skin,"selection normalization");
        }
        check(HeroClass.AMIYA.title().equals("阿米娅") && HeroClass.MAGE.title().equals("法师"),"independent Chinese names");
        check(HeroClass.AMIYA.gameplayClass()==HeroClass.MAGE,"Mage gameplay inheritance");
        check(!HeroClass.AMIYA.spritesheet().equals(HeroClass.MAGE.spritesheet()),"independent sprite");
        check(!HeroClass.AMIYA.splashArt().equals(HeroClass.MAGE.splashArt()),"independent splash");
        check(Arrays.equals(HeroClass.AMIYA.subClasses(),HeroClass.MAGE.subClasses()),"same subclasses");
        for(int i=0;i<3;i++) check(HeroClass.AMIYA.armorAbilities()[i].getClass()==HeroClass.MAGE.armorAbilities()[i].getClass(),"armor ability "+i);
        Badges.loadGlobal();
        Badges.unlock(Badges.Badge.UNLOCK_MAGE);
        check(HeroClass.MAGE.isUnlocked() && HeroClass.AMIYA.isUnlocked(),"shared unlock requirement");
        ArrayList<LinkedHashMap<Talent,Integer>> mageTalents=new ArrayList<>();
        Talent.initClassTalents(HeroClass.MAGE,mageTalents);
        for(HeroClass cl:Arrays.asList(HeroClass.MAGE,HeroClass.AMIYA)) {
            GamesInProgress.selectedClass=cl; Dungeon.seed=12345; Dungeon.init();
            Hero hero=Dungeon.hero;
            check(hero.heroClass==cl,"new run identity "+cl);
            check(hero.belongings.weapon instanceof MagesStaff,"staff loadout "+cl);
            check(hero.belongings.armor!=null && Dungeon.quickslot.getItem(0)==hero.belongings.weapon,"armor and quickslot "+cl);
            check(new ScrollOfUpgrade().isKnown() && new PotionOfLiquidFlame().isKnown(),"identified items "+cl);
            check(hero.talents.equals(mageTalents),"all class talent tiers "+cl);
            check(ClassArmor.upgrade(hero,hero.belongings.armor) instanceof MageArmor,"class armor "+cl);
            check(RemainsItem.get(cl).getClass()==RemainsItem.get(HeroClass.MAGE).getClass(),"remains "+cl);
            for(HeroSubClass sub:cl.subClasses()) {
                hero.talents.clear(); Talent.initClassTalents(hero);
                hero.subClass=sub; Talent.initSubclassTalents(hero);
                check(!hero.talents.get(2).isEmpty(),"subclass talents "+sub);
            }
            hero.armorAbility=cl.armorAbilities()[2];
            Talent.initArmorTalents(hero);
            hero.talents.get(0).put(Talent.EMPOWERING_MEAL,1);
            Bundle saved=new Bundle(); hero.storeInBundle(saved);
            ByteArrayOutputStream bytes=new ByteArrayOutputStream(); check(Bundle.write(saved,bytes),"serialize "+cl);
            Hero loaded=new Hero(); Dungeon.hero=loaded;
            loaded.restoreFromBundle(Bundle.read(new ByteArrayInputStream(bytes.toByteArray())));
            check(loaded.heroClass==cl && loaded.belongings.weapon instanceof MagesStaff,"load identity and inventory "+cl);
            check(loaded.subClass==HeroSubClass.WARLOCK && loaded.armorAbility.getClass()==hero.armorAbility.getClass(),"load abilities "+cl);
            check(loaded.talents.equals(hero.talents),"load talents "+cl);
            System.out.println("PASS new run and save/load: "+cl);
        }
        for(HeroClass cl:HeroClass.values()) {
            int w=(cl==HeroClass.DEGENBRECHER || cl==HeroClass.SHINING)?36:cl==HeroClass.TYPHON?32:cl==HeroClass.AMIYA?24:cl==HeroClass.HELLAGUR?20:cl.isOperator()?16:12,h=cl==HeroClass.AMIYA?24:cl.isOperator()?21:15;
            if (cl==HeroClass.TYPHON || cl==HeroClass.DEGENBRECHER || cl==HeroClass.SHINING) {
                for (HeroSubClass sub : new HeroSubClass[]{HeroSubClass.NONE,cl.subClasses()[0],cl.subClasses()[1]}) {
                    HeroAppearance appearance=HeroAppearance.resolve(cl,sub,0);
                    com.badlogic.gdx.graphics.Pixmap atlas=new com.badlogic.gdx.graphics.Pixmap(Gdx.files.internal(appearance.sprite));
                    try {
                        eq(atlas.getWidth(),1024);eq(atlas.getHeight(),256);
                        for (int tier=0;tier<7;tier++) {
                            TextureFilm film=HeroSprite.film(HeroAppearance.resolve(cl,sub,tier));
                            for (int frame=0;frame<21;frame++) {
                                RectF bounds=film.get(frame);
                                eq(bounds.left*atlas.getWidth(),frame*w);
                                eq(bounds.top*atlas.getHeight(),tier*21);
                                eq(bounds.width()*atlas.getWidth(),w);
                                eq(bounds.height()*atlas.getHeight(),21);
                            }
                            for (int frame : cl==HeroClass.SHINING ? new int[]{8,9,10,11,12,13,14,15} : new int[]{11,12}) {
                                int outsideOldFrame=0;
                                int oldLeft=(w-16)/2;
                                for (int x=0;x<w;x++) for (int y=0;y<21;y++) {
                                    if ((x<oldLeft || x>=oldLeft+16) && (atlas.getPixel(frame*w+x,tier*21+y)&255)!=0) outsideOldFrame++;
                                }
                                check(outsideOldFrame>0,cl+" pose pixels retained beyond centered 16px frame: "+sub+" / "+frame);
                                for (int y=0;y<21;y++) check((atlas.getPixel(frame*w,tier*21+y)&255)==0 && (atlas.getPixel(frame*w+w-1,tier*21+y)&255)==0,cl+" pose has horizontal safety margins");
                            }
                        }
                    } finally { atlas.dispose(); }
                }
            }
            if (cl==HeroClass.HELLAGUR) {
                for (HeroSubClass sub : new HeroSubClass[]{HeroSubClass.NONE,HeroSubClass.BERSERKER,HeroSubClass.GLADIATOR}) {
                    HeroAppearance appearance=HeroAppearance.resolve(cl,sub,0);
                    eq(appearance.width,20);
                    com.badlogic.gdx.graphics.Pixmap atlas=new com.badlogic.gdx.graphics.Pixmap(Gdx.files.internal(appearance.sprite));
                    try {
                        for (int tier=0;tier<7;tier++) {
                            for (int frame=0;frame<21;frame++) {
                                for (int y=0;y<21;y++) {
                                    check((atlas.getPixel(frame*20,tier*21+y)&255)==0 && (atlas.getPixel(frame*20+1,tier*21+y)&255)==0,"Hellagur left anchor padding");
                                }
                            }
                            for (int frame : new int[]{11,12}) {
                                int edgePixels=0;
                                for (int x=18;x<20;x++) for (int y=0;y<21;y++) if ((atlas.getPixel(frame*20+x,tier*21+y)&255)!=0) edgePixels++;
                                check(edgePixels>0,"Hellagur death head retained beyond original frame: "+sub+" / "+frame);
                            }
                        }
                    } finally { atlas.dispose(); }
                }
            }
            var tx=TextureCache.get(cl.spritesheet());
            for(int tier=0;tier<7;tier++) {
                TextureFilm film=HeroSprite.film(cl,tier);
                for(int i=0;i<21;i++) {
                    RectF f=film.get(i); eq(f.left*tx.width,i*w); eq(f.top*tx.height,tier*h);
                    eq(f.width()*tx.width,w); eq(f.height()*tx.height,h);
                    check(f.right<=1 && f.bottom<=1,"frame in bounds");
                }
                Image icon=HeroSprite.icon(cl,tier); eq(icon.width,w); eq(icon.height,h);
                Image avatar=HeroSprite.avatar(cl,tier); eq(avatar.height,h);
            }
            check(Icons.get(cl)!=null,"class icon "+cl);
            check(!cl.desc().equals(Messages.NO_TEXT_FOUND),"description "+cl);
            Dungeon.hero=new Hero(); Dungeon.hero.heroClass=cl;
            UnlinkedHeroSprite sprite=new UnlinkedHeroSprite(); sprite.x=40;sprite.y=30;
            float footX=sprite.x+sprite.width()/2,footY=sprite.y+sprite.height();
            for(HeroClass disguise:HeroClass.values()) {
                sprite.disguise(disguise);eq(sprite.x+sprite.width()/2,footX);eq(sprite.y+sprite.height(),footY);
                sprite.updateArmor();eq(sprite.height(),disguise==HeroClass.AMIYA?21:HeroSprite.frameHeight(disguise));
            }
            MirrorSprite mirror=new MirrorSprite(); mirror.updateArmor(6);eq(mirror.width,cl==HeroClass.AMIYA?16:w);eq(mirror.height,cl==HeroClass.AMIYA?21:h);
            LightAllySprite ally=new LightAllySprite();ally.setup(cl);eq(ally.width,cl==HeroClass.AMIYA?16:w);eq(ally.height,cl==HeroClass.AMIYA?21:h);
            System.out.println("PASS frames, icons, disguises and clones: "+cl);
        }
        OperatorChecks.run();
        Messages.setup(Languages.ENGLISH);
        check(HeroClass.AMIYA.title().equals("Amiya") && !HeroClass.AMIYA.desc().contains(Messages.NO_TEXT_FOUND),"English fallback");
        SPDSettings.lastClass(HeroClass.AMIYA.ordinal());check(SPDSettings.lastClass()==6,"last-class persistence");
        System.out.println("PASS "+checks+" checks; isolated profile: "+scratch);
    }
}


