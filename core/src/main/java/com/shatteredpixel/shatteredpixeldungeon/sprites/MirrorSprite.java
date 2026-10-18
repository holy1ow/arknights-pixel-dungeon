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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAppearance;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;

public class MirrorSprite extends MobSprite {
	
	private HeroAppearance appearance;
	private int armorTier;
	
	public MirrorSprite() {
		super();
		
		appearance = Dungeon.hero != null ? HeroAppearance.of(Dungeon.hero) : HeroAppearance.resolve(HeroClass.WARRIOR, HeroSubClass.NONE, 0);
		texture( appearance.sprite );
		updateArmor( 0 );
		idle();
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		updateArmor();
	}

	@Override
	public void bloodBurstA(PointF from, int damage) {
		//do nothing
	}

	public void updateArmor(){
		updateArmor( ((MirrorImage)ch).armTier );
	}
	
	public void updateArmor( int tier ) {
		float footX = x + width()/2f, footY = y + height();
		armorTier = tier;
		if (Dungeon.hero != null) appearance = HeroAppearance.of(Dungeon.hero);
		logicalBodyWidth = appearance.identity.isOperator() ? 16 : 0;
		setArtPadding(appearance.paddingLeft, appearance.paddingTop, appearance.paddingRight, appearance.paddingBottom);
		texture(appearance.sprite);
		TextureFilm film = HeroSprite.film(HeroAppearance.resolve(appearance.identity, appearance.subclass, tier));
		
		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );
		
		run = new Animation( 20, true );
		run.frames( film, 2, 3, 4, 5, 6, 7 );
		
		die = new Animation( 20, false );
		die.frames( film, 0 );
		
		attack = new Animation( 15, false );
		attack.frames( film, 13, 14, 15, 0 );
		
		idle();
		x = footX-width()/2f; y = footY-height();
	}

	@Override public void update() {
		// Finish any active attack before refreshing its display frames.
		if (curAnim == idle && Dungeon.hero != null
				&& !appearance.sprite.equals(HeroAppearance.of(Dungeon.hero).sprite)) updateArmor(armorTier);
		super.update();
	}
}
