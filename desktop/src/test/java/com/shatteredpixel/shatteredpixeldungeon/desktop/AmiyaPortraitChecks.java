package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SurfaceScene;
import com.watabou.noosa.*;
import com.watabou.utils.FileUtils;
import java.nio.file.Path;

/** Exercise the actual production SurfaceScene portrait consumer; isolated profile only. */
public class AmiyaPortraitChecks {
    public static void main(String[] args)throws Exception{
        Path out=Path.of(args[0]);java.nio.file.Files.createDirectories(out);Path profile=java.nio.file.Files.createTempDirectory(out,"profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);Game.version="0.0.4";Game.versionCode=922;
        String[] names={"medic-surface","caster-surface","guard-surface","mage-surface","shining-surface"};
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();config.setInitialVisible(false);config.setWindowedMode(1280,720);config.setForegroundFPS(60);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
            int index,frame,wait=20;boolean started,switching;
            void start(){switching=true;ShatteredPixelDungeon.switchNoFade(SurfaceScene.class,new Game.SceneChangeCallback(){
                public void beforeCreate(){GamesInProgress.curSlot=1;GamesInProgress.selectedClass=index==3?HeroClass.MAGE:index==4?HeroClass.SHINING:HeroClass.AMIYA;Dungeon.init();Dungeon.hero.subClass=index==0?HeroSubClass.WARLOCK:index==2?HeroSubClass.BATTLEMAGE:HeroSubClass.NONE;}
                public void afterCreate(){frame=0;wait=20;switching=false;}
            });}
            @Override public void render(){try{
                super.render();if(!started){started=true;start();return;}if(switching||Game.switchingScene()||--wait>0)return;
                if(frame==60){
                    Pixmap pix=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());PixmapIO.writePNG(Gdx.files.absolute(out.resolve(names[index]+".png").toString()),pix,1,true);pix.dispose();
                    if(!(Game.scene() instanceof SurfaceScene))throw new AssertionError("Wrong production scene");
                    System.out.println("PASS "+names[index]);
                }
                if(++frame>65){if(++index<names.length)start();else{java.nio.file.Files.writeString(out.resolve("result.txt"),"PASS 5 actual SurfaceScene portrait render scenarios\n");Gdx.app.exit();}}
            }catch(Throwable e){e.printStackTrace();System.exit(1);}}
        },config);
    }
}
