// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

/** Render the actual selection scene in an invisible GL window, in an isolated profile. */
public class HeroSelectionChecks {
    public static void main(String[] args) throws Exception {
        Path output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);
        Path profile=java.nio.file.Files.createTempDirectory(output,"selection-profile-");
        Gdx.files=new Lwjgl3Files();
        SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));
        FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile.toString()+"/");
        SPDSettings.language(args.length>3 && args[3].equals("en")?Languages.ENGLISH:Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);
        Game.version="0.0.3";Game.versionCode=922;
        if(!SPDSettings.intro()) throw new AssertionError("fresh profile must enable introduction");
        Badges.loadGlobal();
        int unlocked=0;
        for(HeroClass cl:HeroClass.selectableClasses()) if(cl.isUnlocked()) {
            unlocked++;
            if(cl!=HeroClass.HELLAGUR) throw new AssertionError("fresh profile unlocked " + cl);
        }
        if(unlocked!=1) throw new AssertionError("fresh profile must unlock only the warrior reskin");
        System.out.println("PASS fresh profile: only Hellagur unlocked; introduction enabled");
        SPDSettings.intro(false);
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false);config.setWindowedMode(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        config.setForegroundFPS(60);config.setIdleFPS(60);
        config.setPreferencesConfig(profile.toString(),Files.FileType.Absolute);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){
            @Override public void updateSystemUI() { /* Keep the requested test window size. */ }
        }) {
            int frames;
            long began=System.nanoTime();
            @Override public void create() {
                super.create();Badges.loadGlobal();
                GamesInProgress.selectedClass=HeroClass.WARRIOR;
                ShatteredPixelDungeon.switchNoFade(HeroSelectScene.class);
            }
            @Override public void render() {
                try {
                    super.render();
                    if ((System.nanoTime()-began)>30_000_000_000L) throw new AssertionError("selection timeout");
                    if (!(Game.scene() instanceof HeroSelectScene) || Game.switchingScene()) return;
                    if (++frames>=30 && frames%15==0) {
                        Field f=HeroSelectScene.class.getDeclaredField("heroBtns");f.setAccessible(true);
                        List<?> buttons=(List<?>)f.get(Game.scene());
                        if(buttons.size()!=6)throw new AssertionError("expected six choices");
                        if(!GamesInProgress.selectedClass.isOperator()) throw new AssertionError("original identity selected");
                        Field identity=buttons.get(0).getClass().getDeclaredField("cl"); identity.setAccessible(true);
                        for(Object button:buttons) if(!((HeroClass)identity.get(button)).isOperator())
                            throw new AssertionError("original class in public roster");
                        for(Object value:buttons){
                            StyledButton b=(StyledButton)value;
                            // Portrait tabs intentionally extend their bottom border three pixels offscreen.
                            float bottomLimit=Camera.main.height+(com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.landscape()?0:3);
                            if(b.left()<0 || b.right()>Camera.main.width || b.top()<0 || b.bottom()>bottomLimit)
                                throw new AssertionError("button outside viewport: "+b.left()+","+b.top()+".."+b.right()+","+b.bottom());
                        }
                        for(int i=0;i<buttons.size();i++) for(int j=i+1;j<buttons.size();j++) {
                            StyledButton a=(StyledButton)buttons.get(i),b=(StyledButton)buttons.get(j);
                            if(a.left()<b.right() && a.right()>b.left() && a.top()<b.bottom() && a.bottom()>b.top())
                                throw new AssertionError("overlapping choices "+i+" "+j);
                        }
                        String name=GamesInProgress.selectedClass.name().toLowerCase()+"-"+args[1]+"x"+args[2]+(args.length>3 && args[3].equals("en")?"-en":"")+".png";
                        Pixmap image=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
                        PixmapIO.writePNG(Gdx.files.absolute(output.resolve(name).toString()),image,-1,true);image.dispose();
                        System.out.println("PASS selection render: "+name+", six buttons fit");
                        if(frames<30+15*(HeroClass.selectableClasses().length-1)){
                            Method select=HeroSelectScene.class.getDeclaredMethod("setSelectedHero",HeroClass.class);
                            select.setAccessible(true);select.invoke(Game.scene(),HeroClass.selectableClasses()[(frames-30)/15+1]);
                        } else Gdx.app.exit();
                    }
                } catch(Throwable error) { error.printStackTrace();System.exit(1); }
            }
        },config);
    }
}
