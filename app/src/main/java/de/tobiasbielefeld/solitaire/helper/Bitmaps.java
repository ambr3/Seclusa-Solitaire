/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;

import de.tobiasbielefeld.solitaire.BuildConfig;
import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Here is the code to load the individual pictures from the bitmaps located in drawables-nodpi.
 * The bitmaps will first be decoded and the width/height of each individual card of the packets
 * will be set.
 */

public class Bitmaps {

    static int NUM_CARD_THEMES = 10;
    static int NUM_CARD_BACKGROUNDS = 10;

    int menuWidth, menuHeight, stackBackgroundWidth, stackBackgroundHeight,
            cardBackWidth, cardBackHeight, cardFrontWidth, cardFrontHeight,
            cardPreviewWidth, cardPreviewHeight, cardPreview2Width, cardPreview2Height;
    private Resources res;
    private int menuLabelBg = Color.WHITE;
    private int menuLabelFg = Color.BLACK;
    private Bitmap menu, menuText, stackBackground, cardBack, cardFront, cardPreview, cardPreview2;
    private Bitmap[] menuBitMaps;
    private Bitmap[] cardFrontCache;
    private boolean cardFrontCacheFourColors;
    private int cardBackCacheX = -1, cardBackCacheY = -1;
    private Bitmap cardBackCache;
    private int savedCardTheme;

    public boolean checkResources() {
        return res != null;
    }

    public void setResources(Context context) {
        this.res = context.getResources();
        TypedValue value = new TypedValue();
        // Stronger themed label strip (primary / onPrimary) vs flat white.
        if (context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true)) {
            menuLabelBg = value.data;
        }
        if (context.getTheme().resolveAttribute(R.attr.colorOnPrimary, value, true)) {
            menuLabelFg = value.data;
        }
        // Theme may have changed — rebuild menu previews with new label colours.
        menuBitMaps = null;
    }

    /**
     * Gets the menu previews
     *
     * @param index The position of the game, as in the order the user set up in the settings
     * @return a single bitmap
     */
    public Bitmap getMenu(int index) {
        Bitmap bitmap;

        if (menuBitMaps == null) {
            menuBitMaps = new Bitmap[lg.getGameCount()];
        } else if (menuBitMaps[index] != null) {
            return menuBitMaps[index];
        }

        if (menu == null) {
            menu = BitmapFactory.decodeResource(res, R.drawable.backgrounds_menu);
            menuWidth = menu.getWidth() / 6;
            menuHeight = menu.getHeight() / 4;
        }

        if (menuText == null) {
            menuText = BitmapFactory.decodeResource(res, R.drawable.backgrounds_menu_text);
        }

        int posX = index % 6;
        int posY = index / 6;

        Bitmap gamePicture;

        //get the preview of the game itself
        try {
            gamePicture = Bitmap.createBitmap(menu, posX * menuWidth, posY * menuHeight, menuWidth, menuHeight);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e("Bitmap.getMenu()", "No picture for current game available\n" + e.toString());
            }
            gamePicture = BitmapFactory.decodeResource(res, R.drawable.no_picture_available);
        }

        //get the game name picture
        Bitmap gameText = drawTextToBitmap(lg.getGameName(res, index));
        //append both parts, then round + shadow so tiles pop on the menu
        bitmap = polishMenuTile(putTogether(gamePicture, gameText));

        menuBitMaps[index] = bitmap;

        return bitmap;
    }

    /** Rounded face, light rim and soft drop-shadow baked into the menu preview. */
    private Bitmap polishMenuTile(Bitmap src) {
        float density = res.getDisplayMetrics().density;
        // Minimal pad — keep the face bulky; just enough for rim + light shadow.
        int pad = Math.max(3, Math.round(3 * density));
        float radius = 14f * density;
        int w = src.getWidth() + pad * 2;
        int h = src.getHeight() + pad * 2;

        Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);

        Paint shadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadow.setColor(Color.argb(110, 0, 0, 0));
        shadow.setMaskFilter(new BlurMaskFilter(3.5f * density, BlurMaskFilter.Blur.NORMAL));
        RectF shadowRect = new RectF(pad * 0.5f, pad * 0.7f, w - pad * 0.35f, h - pad * 0.25f);
        canvas.drawRoundRect(shadowRect, radius, radius, shadow);

        RectF face = new RectF(pad, pad, w - pad, h - pad);
        Path clip = new Path();
        clip.addRoundRect(face, radius, radius, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clip);
        canvas.drawBitmap(src, pad, pad, null);
        canvas.restore();

        Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
        glow.setStyle(Paint.Style.STROKE);
        glow.setStrokeWidth(3f * density);
        glow.setColor(Color.argb(140, Color.red(menuLabelBg), Color.green(menuLabelBg), Color.blue(menuLabelBg)));
        RectF glowRect = new RectF(face.left - 0.5f * density, face.top - 0.5f * density,
                face.right + 0.5f * density, face.bottom + 0.5f * density);
        canvas.drawRoundRect(glowRect, radius + 0.5f * density, radius + 0.5f * density, glow);

        Paint rim = new Paint(Paint.ANTI_ALIAS_FLAG);
        rim.setStyle(Paint.Style.STROKE);
        rim.setStrokeWidth(2.5f * density);
        rim.setColor(Color.argb(230, 255, 255, 255));
        canvas.drawRoundRect(face, radius, radius, rim);

        return out;
    }

    /*
     * draw text on the pictures.
     *
     * Thanks to this article for the code!
     * https://www.skoumal.net/en/android-drawing-multiline-text-on-bitmap/
     */
    private Bitmap drawTextToBitmap(String text) {
        float scale = res.getDisplayMetrics().density;

        if (menuText == null) {
            menuText = BitmapFactory.decodeResource(res, R.drawable.backgrounds_menu_text);
        }

        int width = menuText.getWidth();
        int height = menuText.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Themed label strip — solid primary with a light top edge for depth.
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(menuLabelBg);
        canvas.drawRect(0, 0, width, height, bgPaint);
        Paint edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        edgePaint.setColor(Color.argb(60, 255, 255, 255));
        canvas.drawRect(0, 0, width, Math.max(1, scale), edgePaint);

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(menuLabelFg);
        paint.setShadowLayer(2f * scale, 0f, 1f * scale, Color.argb(90, 0, 0, 0));

        int textWidth = canvas.getWidth() - (int) (5 * scale);
        int textHeight;
        int textScale = 80;
        StaticLayout textLayout;

        do {
            paint.setTextSize(textScale);
            textLayout = new StaticLayout(text, paint, textWidth,
                    Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            textHeight = textLayout.getHeight();
            textScale--;
        } while (textHeight >= bitmap.getHeight() && textScale > 10);

        float x = (bitmap.getWidth() - textWidth) / 2f;
        float y = (bitmap.getHeight() - textHeight) / 2f;

        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    /*
     * puts two bitmaps vertically together
     */
    private static Bitmap putTogether(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight() + bmp2.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        return bmOverlay;
    }

    /**
     * Gets the stack backgrounds
     *
     * @param posX X-coordinate of the background in the file
     * @param posY Y-coordinate of the background in the file
     * @return a single bitmap
     */
    public Bitmap getStackBackground(int posX, int posY) {

        if (stackBackground == null) {
            stackBackground = BitmapFactory.decodeResource(res, R.drawable.backgrounds_stacks);
            stackBackgroundWidth = stackBackground.getWidth() / 9;
            stackBackgroundHeight = stackBackground.getHeight() / 2;
        }

        return Bitmap.createBitmap(stackBackground, posX * stackBackgroundWidth,
                posY * stackBackgroundHeight, stackBackgroundWidth, stackBackgroundHeight);
    }

    /**
     * Gets the card themes, according to the preference
     *
     * @param posX X-coordinate of the card in the file
     * @param posY Y-coordinate of the card in the file
     * @return a single bitmap of the card
     */
    public Bitmap getCardFront(int posX, int posY) {
        return Bitmap.createBitmap(getCardFrontSheet(), posX * cardFrontWidth,
                posY * cardFrontHeight, cardFrontWidth, cardFrontHeight);
    }

    /**
     * Returns the 52 individual card bitmaps of the currently selected theme, decoded and cached.
     * Used on the game start path so the theme sheet only has to be cropped once per theme change.
     *
     * @param fourColors Whether the four-color mode is enabled
     * @return an array of 52 card bitmaps, in the same order as the cards array
     */
    public Bitmap[] getCardFrontDrawables(boolean fourColors) {
        if (cardFrontCache == null || savedCardTheme != prefs.getSavedCardTheme()
                || cardFrontCacheFourColors != fourColors) {
            cardFrontCache = new Bitmap[52];
            cardFrontCacheFourColors = fourColors;

            for (int i = 0; i < 13; i++) {
                cardFrontCache[i] = getCardFront(i, fourColors ? 1 : 0);
                cardFrontCache[13 + i] = getCardFront(i, 2);
                cardFrontCache[26 + i] = getCardFront(i, 3);
                cardFrontCache[39 + i] = getCardFront(i, fourColors ? 5 : 4);
            }
        }

        return cardFrontCache;
    }

    /**
     * Warms up the card assets, so they don't have to be decoded on the game start path. Called
     * while the game selector screen is shown.
     */
    public void preloadCardAssets() {
        getCardFrontDrawables(prefs.getSavedFourColorMode());
        getCardBack(prefs.getSavedCardBackground(), prefs.getSavedCardBackgroundColor());
    }

    /**
     * Decodes the card theme sheet according to the preference, if it isn't already cached.
     *
     * @return the decoded card theme sheet
     */
    private Bitmap getCardFrontSheet() {

        if (cardFront == null || savedCardTheme != prefs.getSavedCardTheme()) {

            savedCardTheme = prefs.getSavedCardTheme();
            int resID;

            switch (savedCardTheme) {
                default:
                case 1:
                    resID = R.drawable.cards_basic;
                    break;
                case 2:
                    resID = R.drawable.cards_classic;
                    break;
                case 3:
                    resID = R.drawable.cards_abstract;
                    break;
                case 4:
                    resID = R.drawable.cards_simple;
                    break;
                case 5:
                    resID = R.drawable.cards_modern;
                    break;
                case 6:
                    resID = R.drawable.cards_oxygen_dark;
                    break;
                case 7:
                    resID = R.drawable.cards_oxygen_light;
                    break;
                case 8:
                    resID = R.drawable.cards_poker;
                    break;
                case 9:
                    resID = R.drawable.cards_paris;
                    break;
                case 10:
                    resID = R.drawable.cards_dondorf;
                    break;
            }

            cardFront = BitmapFactory.decodeResource(res, resID);
            cardFrontWidth = cardFront.getWidth() / 13;
            cardFrontHeight = cardFront.getHeight() / 6;
        }

        return cardFront;
    }

    /**
     * Gets the card backgrounds
     *
     * @param posX X-coordinate of the background in the file
     * @param posY Y-coordinate of the background in the file
     * @return a single bitmap
     */
    public Bitmap getCardBack(int posX, int posY) {

        if (cardBackCache != null && cardBackCacheX == posX && cardBackCacheY == posY) {
            return cardBackCache;
        }

        if (cardBack == null) {
            cardBack = BitmapFactory.decodeResource(res, R.drawable.backgrounds_cards);
            cardBackWidth = cardBack.getWidth() / NUM_CARD_BACKGROUNDS;
            cardBackHeight = cardBack.getHeight() / 4;
        }

        Bitmap source = Bitmap.createBitmap(cardBack, posX * cardBackWidth,
                posY * cardBackHeight, cardBackWidth, cardBackHeight);

        //the card fronts are rounded in the source files, but the backs are not. Round the back the
        //same way, so face-down cards don't show a square edge next to rounded face-up cards.
        Bitmap rounded = Bitmap.createBitmap(cardBackWidth, cardBackHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounded);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float radius = cardBackWidth * 0.10f;
        Path path = new Path();
        RectF rect = new RectF(0, 0, cardBackWidth, cardBackHeight);
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap(source, 0, 0, paint);

        cardBackCache = rounded;
        cardBackCacheX = posX;
        cardBackCacheY = posY;

        return rounded;
    }

    /**
     * Gets the preview of the card themes.
     *
     * @param posX X-coordinate of the preview in the file
     * @param posY Y-coordinate of the preview in the file
     * @return a single bitmap
     */
    public Bitmap getCardPreview(int posX, int posY) {

        if (cardPreview == null) {
            cardPreview = BitmapFactory.decodeResource(res, R.drawable.card_previews);
            cardPreviewWidth = cardPreview.getWidth() / NUM_CARD_THEMES;
            cardPreviewHeight = cardPreview.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardPreview, posX * cardPreviewWidth,
                posY * cardPreviewHeight, cardPreviewWidth, cardPreviewHeight);
    }

    /**
     * Gets the card preview shown in the preference screen. It uses the same file as getCardPreview
     * put it only returns the King-image.
     *
     * @param posX X-coordinate of the preview in the file
     * @param posY Y-coordinate of the preview in the file
     * @return a single bitmap
     */
    public Bitmap getCardPreview2(int posX, int posY) {

        posX = posX * 2 + 1;

        if (cardPreview2 == null) {
            cardPreview2 = BitmapFactory.decodeResource(res, R.drawable.card_previews);
            cardPreview2Width = cardPreview2.getWidth() / (NUM_CARD_THEMES * 2);
            cardPreview2Height = cardPreview2.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardPreview2, posX * cardPreview2Width,
                posY * cardPreview2Height, cardPreview2Width, cardPreview2Height);
    }

    /**
     * Resets the menu preview. Used after changing the locale, so the correct new previews will be shown
     */
    public void resetMenuPreviews() {
        menuBitMaps = null;
    }
}
