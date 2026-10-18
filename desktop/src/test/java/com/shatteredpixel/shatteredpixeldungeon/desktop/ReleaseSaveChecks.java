// Arknights Pixel Dungeon: holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.*;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import java.lang.reflect.Proxy;
import java.nio.file.Path;

/** Run the same fixture writer against v0.0.1, then its reader against v0.0.2.
 * The fixture directory must be a dedicated test directory, never a player profile. */
public class ReleaseSaveChecks {
    public static void main(String[] args) throws Exception {
        boolean write=args[0].equals("write");
        Path fixtures=Path.of(args[1]);java.nio.file.Files.createDirectories(fixtures);
        Path profile=java.nio.file.Files.createTempDirectory(fixtures,"isolated-");
        GdxNativesLoader.load();Gdx.files=new Lwjgl3Files();
        SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));
        FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");
        Gdx.app=(Application)Proxy.newProxyInstance(Application.class.getClassLoader(),new Class[]{Application.class},(p,m,a)->m.getName().equals("getType")?Application.ApplicationType.Desktop:null);
        // The fixture class and launcher share a package; read the launcher's actual
        // JAR manifest rather than package metadata first defined by the test directory.
        try(java.util.jar.JarFile jar=new java.util.jar.JarFile(Path.of(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile())) {
            Game.version=jar.getManifest().getMainAttributes().getValue("Specification-Version");
        }
        if (!(write?"0.0.1":"0.0.2").equals(Game.version)) throw new AssertionError("Wrong runtime version: "+Game.version);
        Game.versionCode=922;Game.platform=new DesktopPlatformSupport();new ShatteredPixelDungeon(Game.platform);
        SPDSettings.challenges(0);Messages.setup(Languages.ENGLISH);
        int count=0;
        for(HeroClass cl:HeroClass.selectableClasses()) {
            for(HeroSubClass sub:new HeroSubClass[]{HeroSubClass.NONE,cl.subClasses()[0],cl.subClasses()[1]}) {
                GamesInProgress.selectedClass=cl;Dungeon.seed=12345;Dungeon.init();
                Hero expected=Dungeon.hero;expected.subClass=sub;Talent.initSubclassTalents(expected);
                expected.lvl=3;expected.exp=7;expected.HP=13;expected.HT=20;expected.STR=11;
                Path file=fixtures.resolve(cl.name()+"-"+sub.name()+".bundle");
                if(write) {
                    if(java.nio.file.Files.exists(file)) throw new AssertionError("Refusing to overwrite fixture "+file);
                    Bundle saved=new Bundle();expected.storeInBundle(saved);
                    try(java.io.OutputStream stream=java.nio.file.Files.newOutputStream(file)) {
                        if(!Bundle.write(saved,stream)) throw new AssertionError("write failed");
                    }
                } else {
                    Hero loaded=new Hero();Dungeon.hero=loaded;
                    try(java.io.InputStream stream=java.nio.file.Files.newInputStream(file)) {loaded.restoreFromBundle(Bundle.read(stream));}
                    if(loaded.heroClass!=cl || loaded.subClass!=sub || loaded.lvl!=3 || loaded.exp!=7 || loaded.HP!=13 || loaded.HT!=20 || loaded.STR!=11)
                        throw new AssertionError("identity/stats mismatch "+file.getFileName());
                    if(!loaded.talents.equals(expected.talents)
                            || loaded.belongings.weapon.getClass()!=expected.belongings.weapon.getClass()
                            || loaded.belongings.armor.getClass()!=expected.belongings.armor.getClass())
                        throw new AssertionError("talent/equipment mismatch "+file.getFileName());
                    if(!HeroAppearance.of(loaded).key.equals(HeroAppearance.of(expected).key)) throw new AssertionError("appearance mapping mismatch");
                }
                count++;
            }
        }
        System.out.println("PASS "+(write?"wrote":"read")+" "+count+" isolated hero fixtures with actual runtime "+Game.version);
    }
}
