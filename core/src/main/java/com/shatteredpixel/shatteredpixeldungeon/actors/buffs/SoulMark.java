// Arknights Pixel Dungeon: Amiya frozen visual integration, holy1ow, 2026-10-20.
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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaItemVisuals;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class SoulMark extends FlavourBuff {

    // Cosmetic provenance only; absent on old saves means original visuals.
    private boolean amiyaSource;
    private Image amiyaIcon;
    public boolean amiyaVisual(){return amiyaSource && AmiyaItemVisuals.active();}
    public void visualSource(com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero hero){
        amiyaSource=com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaEffects.eligible(hero);
        if(amiyaSource && (amiyaIcon==null || amiyaIcon.parent==null))amiyaIcon=AmiyaItemVisuals.soul(this,target);
    }
    @Override public void storeInBundle(com.watabou.utils.Bundle b){super.storeInBundle(b);b.put("amiya_visual_source",amiyaSource);}
    @Override public void restoreFromBundle(com.watabou.utils.Bundle b){super.restoreFromBundle(b);amiyaSource=b.getBoolean("amiya_visual_source");}
	public static final float DURATION	= 10f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.INVERT_MARK;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.5f, 0.2f, 1f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.MARKED);
		else target.sprite.remove(CharSprite.State.MARKED);
        if(on && amiyaSource){
            if(amiyaIcon!=null && amiyaIcon.parent!=target.sprite.parent){amiyaIcon.killAndErase();amiyaIcon=null;}
            if(amiyaIcon==null)amiyaIcon=AmiyaItemVisuals.soul(this,target);
        }
        if(!on && amiyaIcon!=null){amiyaIcon.killAndErase();amiyaIcon=null;}
	}
}
