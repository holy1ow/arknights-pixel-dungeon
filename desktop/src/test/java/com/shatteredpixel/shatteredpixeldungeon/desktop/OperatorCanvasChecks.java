// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany.LightAllySprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.*;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.utils.*;
import java.lang.reflect.*;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.*;
import static com.shatteredpixel.shatteredpixeldungeon.desktop.HeroClassChecks.*;

/** Real renderer, sprite frame geometry, emitter sampling, and production cell-click routing.
 * Uses isolated preferences/saves; baseline mode records padding defects before the fix. */
public class OperatorCanvasChecks {
    static Path output;
    static boolean baseline;
    static List<String> metrics = new ArrayList<>();
    static int frames, clickChecks, emitterChecks;
    static Object field(Object object, Class<?> type, String name) throws Exception {
        Field f=type.getDeclaredField(name); f.setAccessible(true); return f.get(object);
    }
    static class ProbeSelector extends CellSelector {
        int selected;
        ProbeSelector(DungeonTilemap tiles) { super(tiles); }
        @Override public void select(int cell, int button) { selected=cell; }
        int at(float x, float y) {
            Point p=Camera.main.cameraToScreen(x,y);
            onClick(new PointerEvent(p.x,p.y,0,PointerEvent.Type.UP,PointerEvent.LEFT));
            return selected;
        }
    }
    static void capture(String name) {
        Pixmap p=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        PixmapIO.writePNG(Gdx.files.absolute(output.resolve(name+".png").toString()),p,-1,true);p.dispose();
    }
    static void geometry(Hero h) throws Exception {
        HeroSprite s=(HeroSprite)h.sprite;
        PointF tile=DungeonTilemap.tileCenterToWorld(h.pos);
        float footY=(h.pos/Dungeon.level.width()+1)*16-6;
        float x=s.x,y=s.y;
        for (HeroSubClass sub : new HeroSubClass[]{HeroSubClass.NONE,h.heroClass.subClasses()[0],h.heroClass.subClasses()[1]}) {
            h.subClass=sub;s.updateArmor();
            HeroAppearance a=HeroAppearance.of(h);
            if(!baseline) {
                eq(s.bodyWidth(),h.heroClass.isOperator()?16:12);
                MirrorSprite mirror=new MirrorSprite();mirror.updateArmor(6);
                eq(mirror.bodyWidth(),s.bodyWidth());mirror.destroy();
                LightAllySprite ally=new LightAllySprite();ally.setup(h.heroClass);
                eq(ally.bodyWidth(),s.bodyWidth());ally.destroy();
            }
            for(int tier=0;tier<7;tier++) for(int frame=0;frame<21;frame++) for(int side=0;side<2;side++) {
                s.turnTo(h.pos,h.pos+(side==0?1:-1));
                s.frame(HeroSprite.film(HeroAppearance.resolve(h.heroClass,sub,tier)).get(frame));
                eq(s.x,x);eq(s.y,y);eq(s.x+s.width()/2,tile.x);eq(s.y+s.height(),footY);
                eq(s.width(),a.width);eq(s.height(),a.height);
                float[] v=(float[])field(s,Image.class,"vertices");RectF f=s.frame();
                eq(v[2],side==0?f.left:f.right);eq(v[6],side==0?f.right:f.left);
                eq(v[0],0);eq(v[4],a.width);eq(v[9],a.height);
                check(f.left>=0&&f.top>=0&&f.right<=1&&f.bottom<=1,"UV in atlas");frames++;
            }
        }
        // Different-width disguises must retain the same tile anchor.
        for(HeroClass other:HeroClass.values()) {
            s.disguise(other);eq(s.x+s.width()/2,tile.x);eq(s.y+s.height(),footY);
            if(!baseline)eq(s.bodyWidth(),other.isOperator()?16:12);
        }
        s.disguise(h.heroClass);h.subClass=HeroSubClass.NONE;s.updateArmor();s.flipHorizontal=false;s.frame(HeroSprite.film(HeroAppearance.of(h)).get(0));
        s.paused=true;
        PointF next=s.worldToCamera(h.pos+1);eq(next.x+s.width()/2,tile.x+16);eq(next.y+s.height(),footY);
        // Run the same position interpolator used by CharSprite.move in both directions.
        Method interpolate=PosTweener.class.getDeclaredMethod("updateValues",float.class);interpolate.setAccessible(true);
        for(int direction:new int[]{-1,1}) {
            s.move(h.pos,h.pos+direction);
            PosTweener motion=(PosTweener)field(s,CharSprite.class,"motion");
            for(float t:new float[]{0,.25f,.5f,.75f,1}) {
                interpolate.invoke(motion,t);
                eq(s.x+s.width()/2,tile.x+direction*16*t);eq(s.y+s.height(),footY);
                eq(s.destinationCenter().x,tile.x+direction*16);
            }
            motion.killAndErase();Field m=CharSprite.class.getDeclaredField("motion");m.setAccessible(true);m.set(s,null);
            s.isMoving=false;s.place(h.pos);
        }
        s.idle();s.flipHorizontal=false;s.frame(HeroSprite.film(HeroAppearance.of(h)).get(0));
        // Exercise production click routing, including neighboring cell centers and padded edges.
        ProbeSelector selector=new ProbeSelector((DungeonTilemap)field(Game.scene(),GameScene.class,"tiles"));
        try {
            eq(selector.at(tile.x,tile.y),h.pos);clickChecks++;
            for(int direction:new int[]{-1,1}) {
                eq(selector.at(tile.x+direction*16,tile.y),h.pos+direction);clickChecks++;
                int result=selector.at(tile.x+direction*10,tile.y-2);
                metrics.add(h.heroClass+",edge-click-"+direction+","+result+",expected,"+(h.pos+direction));
                if(!baseline) eq(result,h.pos+direction);clickChecks++;
            }
        } finally {selector.destroy();}
        metrics.add(h.heroClass+",frame,"+s.width()+","+s.height());
    }
    static void particles(Hero h) throws Exception {
        CharSprite s=h.sprite;
        Emitter e=s.emitter();e.autoKill=false;
        float[] bounds={Float.MAX_VALUE,Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE};
        Emitter.Factory f=new Emitter.Factory(){public void emit(Emitter emitter,int index,float x,float y){
            bounds[0]=Math.min(bounds[0],x);bounds[1]=Math.min(bounds[1],y);bounds[2]=Math.max(bounds[2],x);bounds[3]=Math.max(bounds[3],y);
            if(!baseline) {
                float width=h.heroClass.isOperator()?16:12;
                check(x>=s.x+(s.width()-width)/2 && x<=s.x+(s.width()+width)/2,"particle inside logical body width");
                check(y>=s.y&&y<=s.y+s.height(),"particle inside height");
            }
            emitterChecks++;
        }};
        e.start(f,1,0);
        Method emit=Emitter.class.getDeclaredMethod("emit",int.class);emit.setAccessible(true);
        for(int i=0;i<512;i++)emit.invoke(e,i);
        metrics.add(h.heroClass+",particle-range,"+(bounds[2]-bounds[0])+","+(bounds[3]-bounds[1]));
        s.x+=16;for(int i=0;i<64;i++)emit.invoke(e,i);s.x-=16;e.killAndErase();
        Emitter bottom=s.bottomEmitter();
        if(!baseline)eq(bottom.width,h.heroClass.isOperator()?16:12);
        metrics.add(h.heroClass+",bottom-width,"+bottom.width);bottom.killAndErase();
    }
    static void effects(Hero h) throws Exception {
        CharSprite s=h.sprite;
        ShieldHalo shield=(ShieldHalo)field(s,CharSprite.class,"shield");
        float radius=(float)field(shield,Halo.class,"radius");
        metrics.add(h.heroClass+",shield-radius,"+radius);
        if(!baseline)eq(radius,(float)Math.hypot((h.heroClass.isOperator()?16:12)/2f,s.height()/2));
        eq(shield.center().x,s.center().x);eq(shield.center().y,s.center().y);
        Visual aura=(Visual)field(s,CharSprite.class,"aura");
        FloatBuffer v=((FloatBuffer)field(aura,com.shatteredpixel.shatteredpixeldungeon.effects.Flare.class,"vertices")).duplicate();
        float max=0;for(int i=4;i<v.capacity();i+=4)max=Math.max(max,(float)Math.hypot(v.get(i),v.get(i+1)));
        metrics.add(h.heroClass+",aura-radius,"+max);
        if(!baseline)eq(max,h.heroClass.isOperator()?25:19);
        eq(aura.x,s.center().x);eq(aura.y,s.center().y);
    }
    public static void main(String[] args) throws Exception {
        output=Path.of(args[0]);baseline=args.length>1&&args[1].equals("baseline");java.nio.file.Files.createDirectories(output);
        Path profile=java.nio.file.Files.createTempDirectory(output,"profile-");Gdx.files=new Lwjgl3Files();
        SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));
        FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");
        SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);
        Game.version="4.0.2";Game.versionCode=922;
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false);config.setWindowedMode(1280,720);config.setForegroundFPS(60);config.setIdleFPS(60);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
            HeroClass[] classes=HeroClass.values();int scenario=0,step=0,wait=35;boolean started,switching;
            long began=System.nanoTime();
            void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                public void beforeCreate(){try{GamesInProgress.curSlot=1;GamesInProgress.selectedClass=classes[scenario];InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;Dungeon.init();Dungeon.switchLevel(Dungeon.newLevel(),-1);}catch(Exception e){throw new RuntimeException(e);}}
                public void afterCreate(){step=0;wait=30;switching=false;}
            });}
            @Override public void render(){try{
                super.render();if(!started){started=true;start();return;}
                if(System.nanoTime()-began>180_000_000_000L)throw new AssertionError("timeout");
                if(switching||Game.switchingScene()||--wait>0)return;
                Hero h=Dungeon.hero;String name=h.heroClass.name().toLowerCase();wait=10;
                switch(step++){
                    case 0: geometry(h);particles(h);break;
                    case 1: capture(name+"-idle-right");h.sprite.turnTo(h.pos,h.pos-1);h.sprite.frame(h.sprite.frame());break;
                    case 2: capture(name+"-idle-left");h.sprite.flipHorizontal=false;h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(11));break;
                    case 3: capture(name+"-death-right");h.sprite.flipHorizontal=true;h.sprite.frame(h.sprite.frame());break;
                    case 4: capture(name+"-death-left");h.sprite.flipHorizontal=false;h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(14));break;
                    case 5: capture(name+"-attack-right");h.sprite.flipHorizontal=true;h.sprite.frame(h.sprite.frame());break;
                    case 6: capture(name+"-attack-left");h.sprite.flipHorizontal=false;h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(0));h.sprite.add(CharSprite.State.SHIELDED);h.sprite.aura(0x55AAFF,8);wait=45;break;
                    case 7: effects(h);capture(name+"-shield-aura");h.sprite.remove(CharSprite.State.SHIELDED);h.sprite.clearAura();h.sprite.add(CharSprite.State.BURNING);h.sprite.add(CharSprite.State.HEALING);wait=50;break;
                    case 8: capture(name+"-particles");h.sprite.remove(CharSprite.State.BURNING);h.sprite.remove(CharSprite.State.HEALING);h.sprite.add(CharSprite.State.FROZEN);wait=35;break;
                    case 9: capture(name+"-frozen");
                        System.out.println("PASS canvas "+name);
                        if(++scenario<classes.length)start();else {
                            java.nio.file.Files.write(output.resolve("metrics.csv"),metrics);
                            System.out.println("PASS "+frames+" frame/flip checks; "+clickChecks+" click probes; "+emitterChecks+" particle samples; 12 identities including six vanilla controls");Gdx.app.exit();
                        }break;
                }
            }catch(Throwable e){e.printStackTrace();System.exit(1);}}
        },config);
    }
}
