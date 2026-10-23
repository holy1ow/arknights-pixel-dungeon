package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon.WarpBeaconTracker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.*;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.*;
import java.nio.file.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;

/** Real packaged textures and production windows/teleport paths in isolated OpenGL GameScenes. */
public class AmiyaIconChecks {
    static int assertions;
    static void check(boolean b,String label){assertions++;if(!b)throw new AssertionError(label);}
    static ArrayList<Talent> talents(){ArrayList<Talent> out=new ArrayList<>();for(Talent t:Talent.values())if((t.icon()>=32&&t.icon()<=57)||t==Talent.HEROIC_ENERGY)out.add(t);return out;}
    static Buff[] buffs(){return new Buff[]{new Recharging(),new Barrier(),new WandEmpower(),new ArtifactRecharge()};}
    static ArrayList<AmiyaWarpVisuals.Visual> visuals(Hero h)throws Exception{
        ArrayList<AmiyaWarpVisuals.Visual> list=new ArrayList<>();for(Gizmo g:AmiyaEffectChecks.members(h.sprite.parent))if(g instanceof AmiyaWarpVisuals.Visual&&g.exists)list.add((AmiyaWarpVisuals.Visual)g);return list;
    }
    static void isolated(Hero h)throws Exception {
        HeroClass initial=h.heroClass;
        check(talents().size()==27,"27 approved talents");
        for(HeroClass cls:HeroClass.values()) {
            h.heroClass=cls;boolean eligible=cls==HeroClass.AMIYA;
            for(Talent t:talents()) {
                String description=t.desc();int max=t.maxPoints();TalentIcon icon=new TalentIcon(t);
                String p=AmiyaIconVisuals.talent(cls,t);
                check((p!=null)==eligible,"talent scope "+cls+" "+t);
                String expected=p==null?com.shatteredpixel.shatteredpixeldungeon.effects.HellagurVisuals.talent(cls,t):p;
                check(icon.texture==TextureCache.get(expected==null?Assets.Interfaces.TALENT_ICONS:expected),"actual talent texture "+cls+" "+t);
                check(icon.width==16&&icon.height==16,"talent dimensions");
                icon.appearance(HeroClass.MAGE);check(icon.texture==TextureCache.get(Assets.Interfaces.TALENT_ICONS),"explicit Mage preview");
                check(description.equals(t.desc())&&max==t.maxPoints(),"talent mechanics untouched");icon.destroy();
            }
            for(Buff buff:buffs()) {
                buff.target=h;String text=buff.iconTextDisplay();float fade=buff.iconFadePercent();
                for(boolean large:new boolean[]{false,true}) {
                    BuffIcon icon=new BuffIcon(buff,large);String p=AmiyaIconVisuals.status(buff,large);
                    check((p!=null)==eligible,"buff scope "+cls+" "+buff.getClass());
                    check(icon.texture==TextureCache.get(p==null?(large?Assets.Interfaces.BUFFS_LARGE:Assets.Interfaces.BUFFS_SMALL):p),"actual buff texture");
                    check(icon.width==(large?16:7),"buff dimensions");
                    if(eligible)check(icon.rm==1&&icon.gm==1&&icon.bm==1,"no old yellow/green tint");
                    icon.refresh(buff.icon());check(icon.texture==TextureCache.get(large?Assets.Interfaces.BUFFS_LARGE:Assets.Interfaces.BUFFS_SMALL),"pooled icon reset");icon.destroy();
                }
                check(text.equals(buff.iconTextDisplay())&&fade==buff.iconFadePercent(),"counter/fade unchanged");
            }
        }
        h.heroClass=HeroClass.AMIYA;
        Buff other=new Recharging();other.target=new Rat();check(AmiyaIconVisuals.status(other,true)==null,"mob buff never reskinned");
        Buff subclass=new Recharging(){};subclass.target=h;check(AmiyaIconVisuals.status(subclass,true)==null,"custom buff subclasses untouched");
        TalentIcon held=new TalentIcon(Talent.LONGRANGE_WARP);
        HeroDisguise disguise=new HeroDisguise();AmiyaItemChecks.set(HeroDisguise.class,disguise,"cls",HeroClass.MAGE);disguise.attachTo(h);
        held.update();check(held.texture==TextureCache.get(Assets.Interfaces.TALENT_ICONS),"disguise fallback");disguise.detach();held.update();check(held.texture==TextureCache.get(AmiyaIconVisuals.talent(HeroClass.AMIYA,Talent.LONGRANGE_WARP)),"disguise restore");held.destroy();
        // Save/load uses only original tracker fields; art is recreated by fx(), never serialized.
        WarpBeaconTracker tracker=new WarpBeaconTracker();
        AmiyaItemChecks.set(WarpBeaconTracker.class,tracker,"pos",h.pos+2);AmiyaItemChecks.set(WarpBeaconTracker.class,tracker,"depth",Dungeon.depth);AmiyaItemChecks.set(WarpBeaconTracker.class,tracker,"branch",Dungeon.branch);
        Bundle b=new Bundle();tracker.storeInBundle(b);WarpBeaconTracker loaded=new WarpBeaconTracker();loaded.restoreFromBundle(b);loaded.attachTo(h);
        check(visuals(h).size()==1,"restored tracker creates one beacon");
        Emitter stock=(Emitter)AmiyaItemChecks.field(WarpBeaconTracker.class,loaded,"e");check(stock.on&&!stock.visible,"stock emission preserved, drawing replaced");
        AmiyaWarpVisuals.Visual v=visuals(h).get(0);int cell=h.pos+2;boolean fov=Dungeon.level.heroFOV[cell];Dungeon.level.heroFOV[cell]=false;v.update();check(!v.visible,"beacon respects FOV");Dungeon.level.heroFOV[cell]=fov;
        h.heroClass=HeroClass.MAGE;v.update();check(!v.visible&&stock.visible,"beacon identity fallback");h.heroClass=HeroClass.AMIYA;v.update();check(v.visible&&!stock.visible,"beacon identity restore");
        loaded.fx(true);check(visuals(h).size()==1,"repeated fx no duplicate");loaded.detach();check(visuals(h).isEmpty(),"detach removes beacon");
        h.heroClass=HeroClass.MAGE;AmiyaWarpVisuals.teleport(h,h.pos,true);check(visuals(h).isEmpty(),"Mage teleport unchanged");
        h.heroClass=initial;
    }
    static final String[] CASES={"amiya-talents-guard","amiya-talents-medic","amiya-elemental-talents","amiya-wild-talents","amiya-beacon-talents","amiya-t26","amiya-status","mage-status-control","amiya-warp","mage-warp-control","amiya-cross-floor"};
    static int room(Level l){l.mobs.clear();l.heaps.clear();l.traps.clear();l.plants.clear();int w=l.width(),p=w*(l.height()/2)+w/2;for(int y=-6;y<=6;y++)for(int x=-8;x<=8;x++)l.map[p+y*w+x]=Terrain.EMPTY;l.buildFlagMaps();l.cleanWalls();return p;}
    static void chooseTeleport()throws Exception {
        for(Gizmo g:AmiyaEffectChecks.members(Game.scene()))if(g instanceof WndOptions){Method m=g.getClass().getDeclaredMethod("onSelect",int.class);m.setAccessible(true);m.invoke(g,0);((Window)g).hide();return;}throw new AssertionError("teleport prompt missing");
    }
    public static void main(String[] args)throws Exception {
        Path output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);AmiyaItemChecks.output=output;AmiyaItemChecks.rows.clear();AmiyaItemChecks.rows.add("scenario,frame,seconds,heroX,heroY");
        Path profile=java.nio.file.Files.createTempDirectory(output,"profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);Game.version="0.0.4";Game.versionCode=922;
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();config.setInitialVisible(false);config.setWindowedMode(1280,720);config.setForegroundFPS(60);config.setIdleFPS(60);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
            int scenario,wait=20,frame,destination,originalDepth;boolean started,switching,fired,sawDepart,sawArrive;float time,expectedCharge;MageArmor armor;Method activate;
            void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                public void beforeCreate(){try{
                    String n=CASES[scenario];GamesInProgress.curSlot=1;GamesInProgress.selectedClass=n.startsWith("mage-")?HeroClass.MAGE:HeroClass.AMIYA;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;GameLog.wipe();Dungeon.init();Level l=Dungeon.newLevel();int p=room(l);Dungeon.switchLevel(l,p);destination=p;
                    if(n.endsWith("cross-floor")){Dungeon.saveAll();Dungeon.depth=2;l=Dungeon.newLevel();p=room(l);Dungeon.switchLevel(l,p);}
                    Hero h=Dungeon.hero;h.lvl=30;h.subClass=n.endsWith("medic")?HeroSubClass.WARLOCK:HeroSubClass.BATTLEMAGE;
                    h.armorAbility=n.contains("elemental")?new ElementalBlast():n.contains("wild")?new WildMagic():new WarpBeacon();Talent.initClassTalents(h);Talent.initSubclassTalents(h);Talent.initArmorTalents(h);h.talents.get(3).put(Talent.REMOTE_BEACON,1);h.talents.get(3).put(Talent.LONGRANGE_WARP,4);
                }catch(Exception e){throw new RuntimeException(e);}}
                public void afterCreate(){try{
                    Hero h=Dungeon.hero;h.sprite.idle();armor=new MageArmor();armor.identify();armor.charge=100;h.belongings.armor=armor;Dungeon.observe();if(scenario==0)isolated(h);
                    activate=WarpBeacon.class.getDeclaredMethod("activate",com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor.class,Hero.class,Integer.class);activate.setAccessible(true);
                    if(CASES[scenario].contains("warp")){destination=h.pos+3;activate.invoke(h.armorAbility,armor,h,destination);check(h.buff(WarpBeaconTracker.class)!=null&&armor.charge==100,"beacon placement free");}
                    if(CASES[scenario].endsWith("cross-floor")){WarpBeaconTracker t=new WarpBeaconTracker();AmiyaItemChecks.set(WarpBeaconTracker.class,t,"pos",destination);AmiyaItemChecks.set(WarpBeaconTracker.class,t,"depth",1);AmiyaItemChecks.set(WarpBeaconTracker.class,t,"branch",0);t.attachTo(h);check(visuals(h).isEmpty(),"different floor beacon hidden");}
                    frame=0;time=0;wait=20;fired=false;sawDepart=false;sawArrive=false;originalDepth=Dungeon.depth;switching=false;
                }catch(Exception e){throw new RuntimeException(e);}}
            });}
            void fire()throws Exception {
                Hero h=Dungeon.hero;String n=CASES[scenario];
                if(n.contains("warp")||n.endsWith("cross-floor")){
                    expectedCharge=100-h.armorAbility.chargeUse(h)*(n.endsWith("cross-floor")?(1.833f-.333f*4):1);
                    activate.invoke(h.armorAbility,armor,h,h.pos);chooseTeleport();check(Math.abs(armor.charge-expectedCharge)<.001f,"original teleport charge");
                    if(!n.endsWith("cross-floor"))check(h.pos==destination,"original same-floor destination");
                }else if(n.contains("status")){
                    Buff.prolong(h,Recharging.class,30);Buff.affect(h,Barrier.class).setShield(12);Buff.affect(h,WandEmpower.class).set(3,3);Buff.affect(h,ArtifactRecharge.class).set(9);BuffIndicator.refreshHero();WndHero.lastIdx=0;GameScene.show(new WndHero());
                }else if(n.endsWith("t26")){GameScene.show(new WndInfoTalent(Talent.LONGRANGE_WARP,4,null));}
                else if(n.contains("elemental")||n.contains("wild")||n.contains("beacon")){GameScene.show(new WndInfoArmorAbility(h.heroClass,h.armorAbility));}
                else{WndHero.lastIdx=1;GameScene.show(new WndHero());}
            }
            @Override protected void update(){Game.elapsed=1f/60f;Game.timeTotal+=Game.elapsed;Game.realTime=com.badlogic.gdx.utils.TimeUtils.millis();Game.inputHandler.processAllEvents();com.watabou.noosa.audio.Music.INSTANCE.update();com.watabou.noosa.audio.Sample.INSTANCE.update();Game.scene().update();Camera.updateAll();}
            @Override public void render(){try{
                super.render();if(!started){started=true;start();return;}if(switching||Game.switchingScene()||!(Game.scene() instanceof GameScene)||--wait>0)return;time+=Game.elapsed;
                // Observe before firing: captures transient arrival on the first frame after a floor change.
                for(AmiyaWarpVisuals.Visual v:visuals(Dungeon.hero)){sawDepart|=v.visible&&v.kind.equals("warp-depart");sawArrive|=v.visible&&v.kind.equals("warp-arrive");}
                if(!fired&&time>=.5f){fired=true;fire();}
                String n=CASES[scenario];if(n.contains("warp")||n.endsWith("cross-floor")){if(frame%3==0)AmiyaItemChecks.capture(n,frame,time,Dungeon.hero);}else if(frame==60)AmiyaItemChecks.capture(n,frame,time,Dungeon.hero);frame++;
                if(time>(n.endsWith("cross-floor")?2.2f:1.8f)){
                    if(n.equals("amiya-warp"))check(sawDepart&&sawArrive,"both teleport sequences rendered");
                    if(n.equals("mage-warp-control"))check(!sawDepart&&!sawArrive,"Mage original teleport only");
                    if(n.endsWith("cross-floor")){check(originalDepth==2&&Dungeon.depth==1&&Dungeon.hero.pos==destination,"production cross-floor return");check(sawArrive,"cross-floor arrival rendered");}
                    for(AmiyaWarpVisuals.Visual v:visuals(Dungeon.hero))check(v.kind.equals("beacon-idle"),"one-shots expire");
                    java.nio.file.Files.write(output.resolve("frames.csv"),AmiyaItemChecks.rows);System.out.println("PASS "+n);if(++scenario<CASES.length)start();else{java.nio.file.Files.writeString(output.resolve("result.txt"),"PASS "+assertions+" assertions; "+CASES.length+" actual render scenarios\n");Gdx.app.exit();}
                }
            }catch(Throwable e){e.printStackTrace();System.exit(1);}}
        },config);
    }
}
