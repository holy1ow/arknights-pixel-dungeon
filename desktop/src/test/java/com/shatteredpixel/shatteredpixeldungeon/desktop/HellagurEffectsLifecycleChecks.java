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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.*;
import com.watabou.utils.*;
import java.nio.file.Path;
import java.util.*;

/** Continuous checkpoint progression using real item/UI/save/ability/ending paths.
 * Key rewards and floors are supplied by the fixture. This is not an unaided playthrough. */
public class HellagurEffectsLifecycleChecks {
 static void prepareStatuses(Hero h)throws Exception {
 Berserk b=Buff.affect(h,Berserk.class);AmiyaItemChecks.set(Berserk.class,b,"power",0.6f);
 Combo c=Buff.affect(h,Combo.class);AmiyaItemChecks.set(Combo.class,c,"count",6);AmiyaItemChecks.set(Combo.class,c,"comboTime",20f);AmiyaItemChecks.set(Combo.class,c,"initialComboTime",20f);
 BrokenSeal.WarriorShield s=Buff.affect(h,BrokenSeal.WarriorShield.class);AmiyaItemChecks.set(BrokenSeal.WarriorShield.class,s,"cooldown",37);
 Buff.affect(h,HoldFast.class);Buff.prolong(h,Talent.ProvokedAngerTracker.class,20);
 com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure.EndureTracker t=Buff.prolong(h,com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure.EndureTracker.class,12);t.enduring=false;t.damageBonus=24;t.hitsLeft=2;
 }
 static void verifyStatuses(Hero h)throws Exception {
 Class<?>[] classes={Berserk.class,Combo.class,BrokenSeal.WarriorShield.class,HoldFast.class,Talent.ProvokedAngerTracker.class,com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure.EndureTracker.class};
 for(Class<?> cls:classes){Buff found=null;for(Buff b:h.buffs())if(b.getClass()==cls)found=b;check(found!=null,"saved status restored "+cls);for(boolean large:new boolean[]{false,true}){BuffIcon bi=new BuffIcon(found,large);String path=HellagurEffects.status(found,large);check(path!=null&&bi.texture==TextureCache.get(path),"saved status chosen texture "+cls);bi.destroy();}}
 check(h.buff(Combo.class).getComboCount()==6,"combo count persists");check(((Integer)AmiyaItemChecks.field(BrokenSeal.WarriorShield.class,h.buff(BrokenSeal.WarriorShield.class),"cooldown"))==37,"shield cooldown persists");
 com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure.EndureTracker t=h.buff(com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure.EndureTracker.class);check(!t.enduring&&t.damageBonus==24&&t.hitsLeft==2,"Endure original fields persist");
 for(com.watabou.noosa.Gizmo g:AmiyaEffectChecks.members(h.sprite.parent))check(!(g instanceof HellagurEffects.Effect),"readiness/burst not replayed on reload");
 }
 static Path output;static int assertions;static List<String> records=new ArrayList<>();
 static void check(boolean b,String s){assertions++;if(!b)throw new AssertionError(s);}
 static <T> T one(Group group,Class<T> t)throws Exception{return OperatorRuntimeChecks.direct(group,t).get(0);}
 static void click(Object o)throws Exception{OperatorRuntimeChecks.click(o);}
 static void correct(){Hero h=Dungeon.hero;HeroAppearance a=HeroAppearance.of(h);check(h.heroClass==HeroClass.HELLAGUR,"Hellagur identity");check(h.sprite.texture==TextureCache.get(a.sprite),"actual sprite matches form");check(h.sprite.width==a.displayWidth()&&h.sprite.height==a.displayHeight(),"logical sprite dimensions");}
 static void capture(String prefix,String step){
  String file=prefix+"-"+step;Pixmap p=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());PixmapIO.writePNG(Gdx.files.absolute(output.resolve(file+".png").toString()),p,1,true);p.dispose();records.add(file+","+Dungeon.depth+","+Dungeon.hero.subClass+","+Game.scene().getClass().getSimpleName());
 }
 public static void main(String[] args)throws Exception{
  output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);Path profile=java.nio.file.Files.createTempDirectory(output,"isolated-profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);
  int width=args.length>1?Integer.parseInt(args[1]):1280,height=args.length>2?Integer.parseInt(args[2]):720;boolean portrait=width<height;SPDSettings.interfaceSize(portrait?0:2);Game.version="0.0.4";Game.versionCode=922;
  Lwjgl3ApplicationConfiguration cfg=new Lwjgl3ApplicationConfiguration();cfg.setInitialVisible(false);cfg.setWindowedMode(width,height);cfg.setForegroundFPS(60);cfg.setIdleFPS(60);
  new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
   int[] routes=portrait?new int[]{2,5}:new int[]{0,1,2,3,4,5,6};int index,stage,wait=20,region;boolean started,switching;long began=System.nanoTime();TengusMask mask;KingsCrown crown;WndChooseSubclass subWindow;WndChooseAbility armorWindow;float savedCharge;int savedHP;Ankh ankh;
   int route(){return routes[index];}int form(){return route()==6?0:route()/3;}int ability(){return route()%3;}HeroSubClass chosen(){return form()==0?HeroSubClass.BERSERKER:HeroSubClass.GLADIATOR;}
   String prefix(){return (form()==0?"berserker":"gladiator")+"-"+new String[]{"leap","shockwave","endure"}[ability()]+(route()==6?"-death":"");}
   void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
    public void beforeCreate(){try{GamesInProgress.curSlot=1;GamesInProgress.selectedClass=HeroClass.HELLAGUR;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;GameLog.wipe();Dungeon.init();Dungeon.switchLevel(Dungeon.newLevel(),-1);}catch(Exception e){throw new RuntimeException(e);}}
    public void afterCreate(){stage=0;wait=45;switching=false;}
   });}
   void reload()throws Exception{Dungeon.saveAll();switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
    public void beforeCreate(){try{InterlevelScene.mode=InterlevelScene.Mode.CONTINUE;Dungeon.loadGame(1);Dungeon.switchLevel(Dungeon.loadLevel(1),Dungeon.hero.pos);}catch(Exception e){throw new RuntimeException(e);}}
    public void afterCreate(){switching=false;wait=25;}
   });}
   void floor(int depth)throws Exception{Dungeon.saveAll();switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
    public void beforeCreate(){InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.depth=depth;Dungeon.switchLevel(Dungeon.newLevel(),-1);}
    public void afterCreate(){switching=false;wait=35;}
   });}
   void finishRoute()throws Exception{java.nio.file.Files.write(output.resolve("captures.csv"),records);System.out.println("PASS continuous route "+prefix());if(++index<routes.length)start();else{java.nio.file.Files.writeString(output.resolve("result.txt"),"PASS "+assertions+" assertions; "+routes.length+" checkpoint progression routes; "+records.size()+" actual screenshots; "+width+"x"+height+"; rewards/floors injected, not a natural playthrough\n");Gdx.app.exit();}}
   @Override public void render(){try{
    super.render();if(!started){started=true;start();return;}if(System.nanoTime()-began>360_000_000_000L)throw new AssertionError("Lifecycle timeout at "+prefix()+" stage "+stage);
    if(switching||Game.switchingScene()||--wait>0)return;Hero h=Dungeon.hero;String pre=prefix();wait=15;
    switch(stage){
     case 0:correct();check(h.subClass==HeroSubClass.NONE&&h.belongings.weapon instanceof com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword,"original Warrior starter");capture(pre,"01-start");mask=new TengusMask();mask.collect();mask.execute(h,"WEAR");subWindow=one(Game.scene(),WndChooseSubclass.class);stage++;break;
     case 1:check(h.subClass==HeroSubClass.NONE,"choice preview preserves base");capture(pre,"02-subclass-choice");click(OperatorRuntimeChecks.direct(subWindow,RedButton.class).get(form()));stage++;break;
     case 2:capture(pre,"03-subclass-confirm");click(OperatorRuntimeChecks.direct(one(Game.scene(),WndOptions.class),RedButton.class).get(1));check(h.subClass==HeroSubClass.NONE&&h.belongings.getItem(TengusMask.class)==mask,"cancel preserves mask");click(OperatorRuntimeChecks.direct(subWindow,RedButton.class).get(form()));stage++;break;
     case 3:click(OperatorRuntimeChecks.direct(one(Game.scene(),WndOptions.class),RedButton.class).get(0));stage++;wait=50;break;
     case 4:check(h.subClass==chosen()&&h.belongings.getItem(TengusMask.class)==null,"confirmed subclass consumes mask");correct();capture(pre,"04-subclass-applied");stage++;reload();break;
     case 5:check(h.subClass==chosen(),"subclass disk reload");correct();capture(pre,"05-subclass-reload");PlateArmor plate=new PlateArmor();plate.identify();plate.collect();plate.doEquip(h);stage++;wait=40;break;
     case 6:check(h.belongings.armor instanceof PlateArmor,"real plate equip");correct();capture(pre,"06-plate-armor");crown=new KingsCrown();crown.collect();crown.execute(h,"WEAR");armorWindow=one(Game.scene(),WndChooseAbility.class);stage++;break;
     case 7:check(h.armorAbility==null,"ability choice is preview");capture(pre,"07-armor-choice");click(OperatorRuntimeChecks.direct(armorWindow,RedButton.class).get(ability()));stage++;break;
     case 8:capture(pre,"08-armor-confirm");click(OperatorRuntimeChecks.direct(one(Game.scene(),WndOptions.class),RedButton.class).get(1));check(h.armorAbility==null&&h.belongings.getItem(KingsCrown.class)==crown,"cancel crown preserved");click(OperatorRuntimeChecks.direct(armorWindow,RedButton.class).get(ability()));stage++;break;
     case 9:click(OperatorRuntimeChecks.direct(one(Game.scene(),WndOptions.class),RedButton.class).get(0));stage++;wait=50;break;
     case 10:check(h.belongings.armor instanceof WarriorArmor&&h.belongings.getItem(KingsCrown.class)==null,"class armor upgrade/consume");check(h.armorAbility.getClass()==h.heroClass.armorAbilities()[ability()].getClass(),"selected ability");correct();capture(pre,"09-class-armor");stage++;reload();break;
     case 11:check(h.subClass==chosen()&&h.belongings.armor instanceof WarriorArmor,"class armor disk load");check(h.armorAbility.getClass()==h.heroClass.armorAbilities()[ability()].getClass(),"ability disk load");correct();capture(pre,"10-armor-reload");((ClassArmor)h.belongings.armor).charge=100;new WandOfFrost().identify().collect();new WandOfMagicMissile().identify().collect();((ClassArmor)h.belongings.armor).execute(h,"ABILITY");if(h.armorAbility.targetingPrompt()!=null){int target=-1;for(int n:PathFinder.NEIGHBOURS8)if(Dungeon.level.passable[h.pos+n]){target=h.pos+n;break;}check(target>=0,"passable ability target");((CellSelector)AmiyaItemChecks.field(GameScene.class,null,"cellSelector")).listener.onSelect(target);}stage++;wait=90;break;
     case 12:ClassArmor a=(ClassArmor)h.belongings.armor;check(a.charge<100&&a.charge>=100-h.armorAbility.chargeUse(h)-.01f,"original warrior ability charge spent");capture(pre,"11-ability-used");Buff.affect(h,Barrier.class).setShield(12);Buff.prolong(h,Recharging.class,20);BuffIndicator.refreshHero();prepareStatuses(h);savedCharge=a.charge;savedHP=h.HP;stage++;reload();break;
     case 13:verifyStatuses(h);check(h.HP==savedHP&&Math.abs(((ClassArmor)h.belongings.armor).charge-savedCharge)<.01,"HP and charge disk persistence");check(h.buff(Barrier.class)!=null&&h.buff(Barrier.class).shielding()==12,"shield persists");check(h.buff(Recharging.class)!=null,"generic charge status persists");for(Buff b:new Buff[]{h.buff(Barrier.class),h.buff(Recharging.class)})for(boolean big:new boolean[]{false,true}){BuffIcon bi=new BuffIcon(b,big);check(bi.texture==TextureCache.get(big?Assets.Interfaces.BUFFS_LARGE:Assets.Interfaces.BUFFS_SMALL),"original status textures both sizes");bi.destroy();}check(HellagurVisuals.itemTexture(ItemSpriteSheet.ARMOR_WARRIOR)!=null,"loaded armor route");TalentIcon loadedTalent=new TalentIcon(Talent.HEARTY_MEAL);check(loadedTalent.texture==TextureCache.get(HellagurVisuals.ROOT+"talents/hearty_meal.png"),"loaded talent route");loadedTalent.destroy();correct();capture(pre,"12-status-reload");GameScene.show(new WndGameInProgress(1));stage++;break;
     case 14:capture(pre,"13-save-summary");AmiyaItemChecks.closeWindows();HeroDisguise d=new HeroDisguise();Bundle bd=new Bundle();bd.put("class",HeroClass.WARRIOR);d.restoreFromBundle(bd);d.attachTo(h);stage++;break;
     case 15:check(!HellagurVisuals.eligible(h),"disguise suppresses Hellagur visuals");correct();capture(pre,"14-disguise");h.buff(HeroDisguise.class).detach();correct();MirrorImage mi=new MirrorImage();mi.duplicate(h);mi.pos=h.pos+1;GameScene.add(mi);h.sprite.flipHorizontal=true;stage++;break;
     case 16:correct();capture(pre,"15-restored-mirror");h.sprite.flipHorizontal=false;region=0;stage++;floor(6);break;
     case 17:int[] regions={6,11,16,21,26};check(Dungeon.depth==regions[region],"regional floor depth");check(h.subClass==chosen()&&h.armorAbility!=null,"regional form and ability persist");correct();capture(pre,"16-region-"+Dungeon.depth);if(++region<regions.length)floor(regions[region]);else{ankh=new Ankh();ankh.bless();ankh.collect();h.HP=0;h.die(new Object());check(h.HP==h.HT/4&&h.belongings.getItem(Ankh.class)==null,"blessed Ankh original recovery and consumption");stage++;wait=60;}break;
     case 18:correct();capture(pre,"17-ankh-revived");if(route()==6){Buff.detach(h,Invulnerability.class);h.HP=0;h.die(new Object());stage=22;wait=80;}else{Amulet am=new Amulet();am.collect();am.execute(h,"END");stage++;wait=90;}break;
     case 19:check(Game.scene() instanceof AmuletScene,"real Amulet ending scene");capture(pre,"18-amulet-ending");click(AmiyaItemChecks.field(AmuletScene.class,Game.scene(),"btnExit"));stage++;wait=220;break;
     case 20:if(!(Game.scene() instanceof RankingsScene)){wait=15;break;}capture(pre,"19-victory-ranking");check(!h.isAlive()||h.heroClass==HeroClass.HELLAGUR,"ending identity");finishRoute();break;
     case 22:check(!h.isAlive(),"actual fatal death");capture(pre,"18-fatal-gameover");finishRoute();break;
    }
   }catch(Throwable e){e.printStackTrace();System.exit(1);}}
  },cfg);
 }
}
