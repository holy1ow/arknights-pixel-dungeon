// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HeroDisguise;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAppearance;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaEffects;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;

public class HeroSprite extends CharSprite {
	
	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;
	// Amiya keeps the stock body height; her ears extend into extra frame space.
	private static final int AMIYA_FRAME_WIDTH = 16;
	
	private static final int RUN_FRAMERATE	= 20;
	
	private static TextureFilm tiers;
	
	private Animation fly;
	private Animation read;
	private HeroClass visualClass;

	public HeroSprite() {
		super();
		
		visualClass = HeroAppearance.of(Dungeon.hero).identity;
		texture( visualClass.spritesheet() );
		updateArmor();
		
		link( Dungeon.hero );

		if (ch.isAlive())
			idle();
		else
			die();
	}

	public void disguise(HeroClass cls){
		float footX = x + width()/2f;
		float footY = y + height();
		visualClass = cls;
		texture( cls.spritesheet() );
		updateArmor();
		// Changing frame dimensions must not move the feet on the dungeon tile.
		x = footX - width()/2f;
		y = footY - height();
	}
	
	public void updateArmor() {

		HeroAppearance appearance = HeroAppearance.resolve(visualClass, Dungeon.hero.subClass, Dungeon.hero.tier());
		logicalBodyWidth = appearance.identity.isOperator() ? AMIYA_FRAME_WIDTH : 0;
		setArtPadding(appearance.paddingLeft, appearance.paddingTop, appearance.paddingRight, appearance.paddingBottom);
		texture(appearance.sprite);
		TextureFilm film = film(appearance);
		
		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );
		
		run = new Animation( RUN_FRAMERATE, true );
		run.frames( film, 2, 3, 4, 5, 6, 7 );
		
		die = new Animation( 20, false );
		die.frames( film, 8, 9, 10, 11, 12, 11 );
		
		attack = new Animation( 15, false );
		attack.frames( film, 13, 14, 15, 0 );
		
		zap = attack.clone();
		
		operate = new Animation( 8, false );
		operate.frames( film, 16, 17, 16, 17 );
		
		fly = new Animation( 1, true );
		fly.frames( film, 18 );

		read = new Animation( 20, false );
		read.frames( film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19 );
		
		if (Dungeon.hero.isAlive())
			idle();
		else
			die();
	}
	
	@Override
	public void place( int p ) {
		super.place( p );
		if (Game.scene() instanceof GameScene) Camera.main.panFollow(this, 5f);
	}

	@Override
	public void move( int from, int to ) {
		super.move( from, to );
		if (ch != null && ch.flying) {
			play( fly );
		}
		Camera.main.panFollow(this, 20f);
	}

	@Override
	public void idle() {
		super.idle();
		if (ch != null && ch.flying) {
			play( fly );
		}
	}

	@Override
	public synchronized void attack(int cell, Callback callback) {
		super.attack(cell, callback);
		if (ch instanceof Hero) AmiyaEffects.swordSwing((Hero) ch, cell);
	}

	@Override
	public void jump( int from, int to, float height, float duration,  Callback callback ) {
		super.jump( from, to, height, duration, callback );
		play( fly );
		Camera.main.panFollow(this, 20f);
	}

	public synchronized void read() {
		animCallback = new Callback() {
			@Override
			public void call() {
				idle();
				ch.onOperateComplete();
			}
		};
		play( read );
	}

	@Override
	public void bloodBurstA(PointF from, int damage) {
		//Does nothing.

		/*
		 * This is both for visual clarity, and also for content ratings regarding violence
		 * towards human characters. The heroes are the only human or human-like characters which
		 * participate in combat, so removing all blood associated with them is a simple way to
		 * reduce the violence rating of the game.
		 */
	}

	@Override
	public void update() {
		sleeping = ch.isAlive() && ((Hero)ch).resting;
		
		super.update();
	}
	
	public void sprint( float speed ) {
		run.delay = 1f / speed / RUN_FRAMERATE;
	}
	
	public static TextureFilm tiers() {
		if (tiers == null) {
			SmartTexture texture = TextureCache.get( Assets.Sprites.ROGUE );
			tiers = new TextureFilm( texture, texture.width, FRAME_HEIGHT );
		}
		
		return tiers;
	}

	public static int frameWidth(HeroClass cl) {
		return HeroAppearance.resolve(cl, HeroSubClass.NONE, 0).width;
	}

	public static int frameHeight(HeroClass cl) {
		return HeroAppearance.resolve(cl, HeroSubClass.NONE, 0).height;
	}

	public static TextureFilm film(HeroClass cl, int armorTier) {
		return film(HeroAppearance.resolve(cl, HeroSubClass.NONE, armorTier));
	}

	public static TextureFilm film(HeroAppearance appearance) {
		HeroClass cl = appearance.identity;
		int armorTier = appearance.armorTier;
		SmartTexture texture = TextureCache.get(appearance.sprite);
		TextureFilm rows = new TextureFilm(texture, texture.width, appearance.height);
		return new TextureFilm(rows, armorTier, appearance.width, appearance.height);
	}

	public static Image icon(HeroClass cl, int armorTier) {
		return icon(cl, HeroSubClass.NONE, armorTier);
	}

	public static Image icon(HeroClass cl, HeroSubClass sub, int tier) {
		HeroAppearance a=HeroAppearance.resolve(cl, sub, tier);
		return new Image(a.sprite, 0, tier*a.height, a.width, a.height);
	}

	public static Image avatar( Hero hero ){
		if (hero.buff(HeroDisguise.class) != null){
			return avatar(hero.buff(HeroDisguise.class).getDisguise(), hero.subClass, hero.tier());
		} else {
			return avatar(hero.heroClass, hero.subClass, hero.tier());
		}
	}
	
	public static Image avatar(HeroClass cl, HeroSubClass sub, int armorTier) {
		if (cl.isOperator()) return icon(cl, sub, armorTier);
		return avatar(cl, armorTier);
	}

	public static Image avatar( HeroClass cl, int armorTier ) {
		if (cl.isOperator()) return icon(cl, armorTier);
		
		RectF patch = tiers().get( armorTier );
		Image avatar = new Image( cl.spritesheet() );
		RectF frame = avatar.texture.uvRect( 1, 0, FRAME_WIDTH, FRAME_HEIGHT );
		frame.shift( patch.left, patch.top );
		avatar.frame( frame );
		
		return avatar;
	}
}
