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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.*;
import com.watabou.utils.*;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;
import static com.shatteredpixel.shatteredpixeldungeon.desktop.HeroClassChecks.*;

/** Actual game scenes and production UI callbacks, rendered to native OpenGL framebuffers. */
public class OperatorRuntimeChecks {
    static Path output;
    static List<Object> children(Group group) throws Exception {
        Field field=Group.class.getDeclaredField("members");field.setAccessible(true);
        return new ArrayList<>((List<Object>)field.get(group));
    }
    static <T> List<T> direct(Group group, Class<T> type) throws Exception {
        List<T> list=new ArrayList<>();for(Object child:children(group))if(type.isInstance(child))list.add(type.cast(child));return list;
    }
    static void click(Object button) throws Exception {
        Class<?> type=button.getClass();Method method=null;
        while(type!=null) {try{method=type.getDeclaredMethod("onClick");break;}catch(NoSuchMethodException ignored){type=type.getSuperclass();}}
        method.setAccessible(true);method.invoke(button);
    }
    static void capture(String name) {
        Pixmap image=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        PixmapIO.writePNG(Gdx.files.absolute(output.resolve(name+".png").toString()),image,-1,true);image.dispose();
        System.out.println("CAPTURE "+name);
    }
    static void correct(Hero h) {
        HeroAppearance a=HeroAppearance.of(h);check(h.sprite.texture==TextureCache.get(a.sprite),"actual sprite texture "+a.key);
        eq(h.sprite.width,a.displayWidth());eq(h.sprite.height,a.displayHeight());
    }
    public static void main(String[] args) throws Exception {
        output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);
        Path profile=java.nio.file.Files.createTempDirectory(output,"runtime-profile-");
        Gdx.files=new Lwjgl3Files();
        SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));
        FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile.toString()+"/");
        SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);
        Game.version="0.0.4";Game.versionCode=922;
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false);config.setWindowedMode(1280,720);config.setForegroundFPS(60);config.setIdleFPS(60);
        config.setPreferencesConfig(profile.toString(),Files.FileType.Absolute);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}) {
            HeroClass[] operators={HeroClass.AMIYA,HeroClass.HELLAGUR,HeroClass.CROWNSLAYER,HeroClass.TYPHON,HeroClass.DEGENBRECHER,HeroClass.SHINING};
            int scenario,step,waitFrames=45;boolean switching,launched;TengusMask mask;WndChooseSubclass choice;WndInfoSubclass info;
            boolean basePosesChecked;
            int poseIndex=-1,poseResume;
            String poseForm;
            long began=System.nanoTime();
            HeroClass cls(){return scenario==12?HeroClass.AMIYA:operators[scenario/2];}
            HeroSubClass sub(){return scenario==12 && Dungeon.hero.subClass!=HeroSubClass.NONE?Dungeon.hero.subClass:cls().subClasses()[scenario%2];}
            String prefix(){return (scenario==12?"random-":"")+cls().name().toLowerCase()+"-"+sub().name().toLowerCase();}
            void showPose(Hero h) {
                h.sprite.paused=true;h.sprite.flipHorizontal=(poseIndex%2)==1;
                h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(8+poseIndex/2));
                correct(h);waitFrames=3;
            }
            void beginPoseChecks(Hero h,int resume) {
                poseIndex=0;poseResume=resume;
                poseForm=h.subClass==HeroSubClass.NONE?"base":"subclass";
                showPose(h);
            }
            void startScenario() {
                switching=true;basePosesChecked=false;
                ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                    public void beforeCreate(){
                        try {
                            InterlevelScene.mode=InterlevelScene.Mode.DESCEND;GamesInProgress.curSlot=1;GamesInProgress.selectedClass=cls();Dungeon.seed=12345;Dungeon.init();
                            Dungeon.switchLevel(Dungeon.newLevel(),-1);
                        }catch(Exception e){throw new RuntimeException(e);}
                    }
                    public void afterCreate(){step=0;waitFrames=35;switching=false;}
                });
            }
            @Override public void create(){super.create();}
            @Override public void render() {
                try {
                    super.render();
                    if (!launched) { launched=true; startScenario(); return; }
                    if(System.nanoTime()-began>180_000_000_000L)throw new AssertionError("runtime timeout");
                    if(switching || Game.switchingScene() || --waitFrames>0)return;
                    waitFrames=12;Hero h=Dungeon.hero;
                    if(poseIndex>=0) {
                        capture(prefix()+"-"+poseForm+"-pose-"+(8+poseIndex/2)+(poseIndex%2==0?"-right":"-left"));
                        if(++poseIndex<16) {showPose(h);return;}
                        poseIndex=-1;h.sprite.paused=false;h.sprite.flipHorizontal=false;h.HP=h.HT;h.sprite.idle();step=poseResume;
                        if(poseResume==17) {ShatteredPixelDungeon.switchNoFade(SurfaceScene.class);waitFrames=25;}
                        return;
                    }
                    switch(step++) {
                        case 0:
                            if((cls()==HeroClass.SHINING || cls()==HeroClass.AMIYA) && !basePosesChecked) {
                                basePosesChecked=true;beginPoseChecks(h,0);break;
                            }
                            correct(h);capture(prefix()+"-base");
                            mask=new TengusMask();mask.collect();mask.execute(h,"WEAR");
                            choice=direct(Game.scene(),WndChooseSubclass.class).get(0);break;
                        case 1:
                            check(h.subClass==HeroSubClass.NONE,"opening choice is preview only");capture(prefix()+"-choice");
                            info=new WndInfoSubclass(cls(),sub());GameScene.show(info);break;
                        case 2:
                            check(h.subClass==HeroSubClass.NONE,"info preview does not transform");capture(prefix()+"-preview");info.hide();
                            click(direct(choice,RedButton.class).get(scenario%2));break;
                        case 3:
                            capture(prefix()+"-confirmation");click(direct(direct(Game.scene(),WndOptions.class).get(0),RedButton.class).get(1));
                            check(h.subClass==HeroSubClass.NONE && h.belongings.getItem(TengusMask.class)==mask,"cancel preserves subclass and mask");correct(h);
                            if (scenario==12) click(direct(choice,IconButton.class).get(0));
                            else click(direct(choice,RedButton.class).get(scenario%2));break;
                        case 4:
                            click(direct(direct(Game.scene(),WndOptions.class).get(0),RedButton.class).get(0));
                            check(h.subClass==sub() && Arrays.asList(cls().subClasses()).contains(h.subClass),"real confirmation applied legal subclass");
                            for(WndInfoSubclass window:direct(Game.scene(),WndInfoSubclass.class))window.hide();correct(h);waitFrames=45;break;
                        case 5:
                            capture(prefix()+"-confirmed");
                            float footX=h.sprite.x+h.sprite.width()/2,footY=h.sprite.y+h.sprite.height();
                            h.belongings.armor=new PlateArmor();h.belongings.armor.identify();((HeroSprite)h.sprite).updateArmor();
                            eq(footX,h.sprite.x+h.sprite.width()/2);eq(footY,h.sprite.y+h.sprite.height());correct(h);break;
                        case 6:
                            capture(prefix()+"-armor");Dungeon.saveAll();
                            switching=true;
                            ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                                public void beforeCreate(){try{InterlevelScene.mode=InterlevelScene.Mode.CONTINUE;Dungeon.loadGame(1);Dungeon.switchLevel(Dungeon.loadLevel(1),Dungeon.hero.pos);}catch(Exception e){throw new RuntimeException(e);}}
                                public void afterCreate(){switching=false;waitFrames=25;}
                            });break;
                        case 7:
                            check(h.heroClass==cls() && h.subClass==sub(),"actual disk save/load identity and subclass");correct(h);capture(prefix()+"-loaded");
                            HeroDisguise disguise=new HeroDisguise();Bundle b=new Bundle();b.put("class",HeroClass.SHINING==cls()?HeroClass.HELLAGUR:HeroClass.SHINING);disguise.restoreFromBundle(b);disguise.attachTo(h);correct(h);break;
                        case 8:
                            capture(prefix()+"-disguise");h.buff(HeroDisguise.class).detach();correct(h);
                            h.sprite.flipHorizontal=!h.sprite.flipHorizontal;h.sprite.alpha(.4f);h.sprite.tint(0x4488ff,.4f);
                            MirrorImage mirror=new MirrorImage();mirror.duplicate(h);mirror.pos=h.pos+1;GameScene.add(mirror);
                            PrismaticImage prism=new PrismaticImage();prism.duplicate(h,20);prism.pos=h.pos-1;GameScene.add(prism);break;
                        case 9:
                            capture(prefix()+"-tint-flip-mirrors");h.sprite.alpha(1);h.sprite.resetColor();
                            GamesInProgress.Info record=GamesInProgress.check(1);check(record.heroClass==cls() && record.subClass==sub(),"disk summary context");GameScene.show(new WndGameInProgress(1));break;
                        case 10:
                            capture(prefix()+"-record");for(Window w:direct(Game.scene(),Window.class))w.hide();
                            switching=true;
                            ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                                public void beforeCreate(){InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.depth=2;Dungeon.switchLevel(Dungeon.newLevel(),-1);}
                                public void afterCreate(){switching=false;waitFrames=25;}
                            });break;
                        case 11:
                            check(h.heroClass==cls() && h.subClass==sub(),"floor transition retains identity/form");correct(h);capture(prefix()+"-next-floor");
                            h.HP=0;h.sprite.die();waitFrames=25;break;
                        case 12:
                            correct(h);capture(prefix()+"-death-animation");
                            if(cls()==HeroClass.SHINING || cls()==HeroClass.AMIYA) {beginPoseChecks(h,17);break;}
                            if (cls()==HeroClass.TYPHON || cls()==HeroClass.DEGENBRECHER) {
                                h.sprite.paused=true;h.sprite.flipHorizontal=false;
                                h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(11));break;
                            }
                            step=17;h.HP=h.HT;h.sprite.idle();
                            ShatteredPixelDungeon.switchNoFade(SurfaceScene.class);waitFrames=25;break;
                        case 13:
                            capture(prefix()+"-death-11-right");h.sprite.flipHorizontal=true;
                            h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(11));break;
                        case 14:
                            capture(prefix()+"-death-11-left");h.sprite.flipHorizontal=false;
                            h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(12));break;
                        case 15:
                            capture(prefix()+"-death-12-right");h.sprite.flipHorizontal=true;
                            h.sprite.frame(HeroSprite.film(HeroAppearance.of(h)).get(12));break;
                        case 16:
                            capture(prefix()+"-death-12-left");h.sprite.paused=false;h.HP=h.HT;h.sprite.idle();
                            ShatteredPixelDungeon.switchNoFade(SurfaceScene.class);waitFrames=25;break;
                        case 17:
                            capture(prefix()+"-surface");System.out.println("PASS runtime preview/cancel/confirm/armor/disk-load/disguise/clones/record/surface: "+prefix());
                            if(++scenario==operators.length*2+1){System.out.println("PASS all twelve operator subclass UI lifecycles plus random selection; isolated "+profile);Gdx.app.exit();}
                            else startScenario();break;
                    }
                }catch(Throwable error){error.printStackTrace();System.exit(1);}
            }
        },config);
    }
}

