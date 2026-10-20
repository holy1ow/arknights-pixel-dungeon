package com.shatteredpixel.shatteredpixeldungeon.trainer;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.input.KeyEvent;

/** The only implementation compiled in ordinary builds. */
public final class TrainerBridge {
    public static boolean enabled() { return false; }
    public static String dataPath() { return null; }
    public static void bootstrap() { }
    public static void title(PixelScene scene) { }
    public static void open() { }
    public static boolean key(KeyEvent event) { return false; }
    public static boolean tick(Thread actorThread) { return false; }
    public static void actorIdle(boolean idle) { }
    public static boolean invulnerable() { return false; }
    public static boolean noHunger(Char target) { return false; }
    public static boolean[] fog(boolean[] original) { return original; }
}
