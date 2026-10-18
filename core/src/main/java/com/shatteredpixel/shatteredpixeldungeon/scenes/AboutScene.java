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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class AboutScene extends PixelScene {

	@Override
	public void create() {
		super.create();

		int w = Camera.main.width;
		int h = Camera.main.height;
		RectF insets = getCommonInsets();
		float colWidth = Math.min(240, w - insets.left - insets.right - 24);
		float fullWidth = colWidth;

		add(new TitleBackground(w, h));
		add(new ColorBlock(w, h, 0x44000000));

		ScrollPane list = new ScrollPane(new Component());
		add(list);
		Component content = list.content();

		CreditsBlock license = new CreditsBlock(true, Window.TITLE_COLOR,
				Messages.get(this, "title"), null,
				"Arknights Pixel Dungeon 0.0.1\n" +
				"Modified by holy1ow, 2026-10-18.\nBased on Shattered Pixel Dungeon 4.0.2.\n" +
				"GNU GPL v3 or later; no warranty.\n\n" +
				"Pixel Dungeon: Copyright (C) 2012-2015 Oleg Dolya (Watabou)\n" +
				"Shattered Pixel Dungeon: Copyright (C) 2014-2026 Evan Debenham",
				"GNU GPL v3", "https://www.gnu.org/licenses/gpl-3.0.html");
		license.setRect((w - colWidth)/2f, insets.top + 8, colWidth, 0);
		content.add(license);
		CreditsBlock fork = new CreditsBlock(true, Window.TITLE_COLOR,
				"Arknights Pixel Dungeon", null,
				"Reskin adaptation: holy1ow\nUnofficial fan project. Character designs belong to their respective rights holders.",
				"Source code / GPL", "https://github.com/holy1ow/arknights-pixel-dungeon");
		fork.setRect(license.left(), license.bottom() + 12, colWidth, 0);
		content.add(fork);

		CreditsBlock credits = new CreditsBlock(true, Window.TITLE_COLOR,
				Messages.get(this, "credits"), null,
				"Inspired by Brian Walker's Brogue\n" +
				"Splash & Dungeon Art: Aleksandar Komitov\n" +
				"Composer: Lumine Haaristo\n" +
				"Item Pixel Art: PumpkinVolt\n" +
				"Sound Effects: Celesti\n" +
				"Additional Pixel Art: Alastair Braun\n" +
				"Music: Cube Code\n" +
				"Pixel Dungeon GDX: Edu García\n" +
				"Shattered GDX Help: Kevin MacMartin\n" +
				"Engine: libGDX\n\n" + Messages.get(this, "translations"),
				null, null);
		credits.setRect(fork.left(), fork.bottom() + 12, colWidth, 0);
		content.add(credits);
		addLine(credits.top() - 5, content);

		//*** Freesound Credits ***

		CreditsBlock freesound = new CreditsBlock(true,
				Window.TITLE_COLOR,
				null,
				null,
				Messages.get(this, "sounds") + "\n\n" +

				"Creative Commons Attribution License:\n" +
				"_SFX ATTACK SWORD 001.wav_ by _JoelAudio_\n" +
				"_Pack: Slingshots and Longbows_ by _saturdaysoundguy_\n" +
				"_Cracking/Crunching, A.wav_ by _InspectorJ_\n" +
				"_Extracting a sword.mp3_ by _Taira Komori_\n" +
				"_Pack: Uni Sound Library_ by _timmy h123_\n\n" +

				"Creative Commons Zero License:\n" +
				"_Pack: Movie Foley: Swords_ by _Black Snow_\n" +
				"_machine gun shot 2.flac_ by _qubodup_\n" +
				"_m240h machine gun burst 4.flac_ by _qubodup_\n" +
				"_Pack: Onomatopoeia_ by _Adam N_\n" +
				"_Pack: Watermelon_ by _lolamadeus_\n" +
				"_metal chain_ by _Mediapaja2009_\n" +
				"_Pack: Sword Clashes Pack_ by _JohnBuhr_\n" +
				"_Pack: Metal Clangs and Pings_ by _wilhellboy_\n" +
				"_Pack: Stabbing Stomachs & Crushing Skulls_ by _TheFilmLook_\n" +
				"_Sheep bleating_ by _zachrau_\n" +
				"_Lemon,Juicy,Squeeze,Fruit.wav_ by _Filipe Chagas_\n" +
				"_Lemon,Squeeze,Squishy,Fruit.wav_ by _Filipe Chagas_",
				"freesound.org",
				"https://www.freesound.org");
		freesound.setRect(credits.left(), credits.bottom() + 12, colWidth, 0);
		content.add(freesound);

		content.setSize( fullWidth, freesound.bottom()+10 + insets.bottom );

		list.setRect( 0, 0, w, h );
		list.scrollTo(0, 0);

		ExitButton btnExit = new ExitButton();
		int ofs = PixelScene.landscape() ? 0 : 4;
		btnExit.setPos( Camera.main.width - btnExit.width() - ofs, ofs );
		add( btnExit );

		//fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(TitleScene.class);
	}

	private void addLine( float y, Group content ){
		ColorBlock line = new ColorBlock(Camera.main.width, 1, 0xFF333333);
		line.y = y;
		content.add(line);
	}

	private static class CreditsBlock extends Component {

		boolean large;
		RenderedTextBlock title;
		Image avatar;
		Flare flare;
		RenderedTextBlock body;

		RenderedTextBlock link;
		ColorBlock linkUnderline;
		PointerArea linkButton;

		//many elements can be null, but body is assumed to have content.
		private CreditsBlock(boolean large, int highlight, String title, Image avatar, String body, String linkText, String linkUrl){
			super();

			this.large = large;

			if (title != null) {
				this.title = PixelScene.renderTextBlock(title, large ? 8 : 6);
				if (highlight != -1) this.title.hardlight(highlight);
				add(this.title);
			}

			if (avatar != null){
				this.avatar = avatar;
				add(this.avatar);
			}

			if (large && highlight != -1 && this.avatar != null){
				this.flare = new Flare( 7, 24 ).color( highlight, true ).show(this.avatar, 0);
				this.flare.angularSpeed = 20;
			}

			this.body = PixelScene.renderTextBlock(body, 6);
			if (highlight != -1) this.body.setHightlighting(true, highlight);
			if (large) this.body.align(RenderedTextBlock.CENTER_ALIGN);
			add(this.body);

			if (linkText != null && linkUrl != null){

				int color = 0xFFFFFFFF;
				if (highlight != -1) color = 0xFF000000 | highlight;
				this.linkUnderline = new ColorBlock(1, 1, color);
				add(this.linkUnderline);

				this.link = PixelScene.renderTextBlock(linkText, 6);
				if (highlight != -1) this.link.hardlight(highlight);
				add(this.link);

				linkButton = new PointerArea(0, 0, 0, 0){
					@Override
					protected void onClick( PointerEvent event ) {
						ShatteredPixelDungeon.platform.openURI( linkUrl );
					}
				};
				add(linkButton);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			float topY = top();

			if (title != null){
				title.maxWidth((int)width());
				title.setPos( x + (width() - title.width())/2f, topY);
				topY += title.height() + (large ? 2 : 1);
			}

			if (large){

				if (avatar != null){
					avatar.x = x + (width()-avatar.width())/2f;
					avatar.y = topY;
					PixelScene.align(avatar);
					if (flare != null){
						flare.point(avatar.center());
					}
					topY = avatar.y + avatar.height() + 2;
				}

				body.maxWidth((int)width());
				body.setPos( x + (width() - body.width())/2f, topY);
				topY += body.height() + 2;

			} else {

				if (avatar != null){
					avatar.x = x;
					body.maxWidth((int)(width() - avatar.width - 1));

					float fullAvHeight = Math.max(avatar.height(), 16);
					if (fullAvHeight > body.height()){
						avatar.y = topY + (fullAvHeight - avatar.height())/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY + (fullAvHeight - body.height())/2f);
						topY += fullAvHeight + 1;
					} else {
						avatar.y = topY + (body.height() - fullAvHeight)/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY);
						topY += body.height() + 2;
					}

				} else {
					topY += 1;
					body.maxWidth((int)width());
					body.setPos( x, topY);
					topY += body.height()+2;
				}

			}

			if (link != null){
				if (large) topY += 1;
				link.maxWidth((int)width());
				link.setPos( x + (width() - link.width())/2f, topY);
				topY += link.height() + 2;

				linkButton.x = link.left()-1;
				linkButton.y = link.top()-1;
				linkButton.width = link.width()+2;
				linkButton.height = link.height()+2;

				linkUnderline.size(link.width(), PixelScene.align(0.49f));
				linkUnderline.x = link.left();
				linkUnderline.y = link.bottom()+1;

			}

			topY -= 2;

			height = Math.max(height, topY - top());
		}
	}
}
