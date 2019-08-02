/*
 * Copyright 2018 Mike Penz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.newmedia.erxeslibrary.iconics.animation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import com.newmedia.erxeslibrary.iconics.IconicsBrush;


/**
 * @author pa.gulko zTrap (30.11.2018)
 */
public class BlinkScaleProcessor extends IconicsAnimationProcessor {
    /**
     * Duration used for all instances of this processor by default. 500 ms by default.
     */
    public static int defaultDuration = 500;

    @FloatRange(from = 0)
    private float mMinimumScale = 0;
    @FloatRange(from = 0)
    private float mMaximumScale = 1;

    {
        mRepeatMode = REVERSE;
        mDuration = defaultDuration;
    }

    /**
     * @param minimumScale The scale which will be used as minimal available value.
     */
    @NonNull
    public BlinkScaleProcessor minimumScale(@FloatRange(from = 0) float minimumScale) {
        mMinimumScale = minimumScale;
        return this;
    }

    /**
     * @param maximumScale The scale which will be used as maximal available value.
     */
    @NonNull
    public BlinkScaleProcessor maximumScale(@FloatRange(from = 0) float maximumScale) {
        mMaximumScale = maximumScale;
        return this;
    }

    /**
     * @return The minimal available scale.
     */
    @FloatRange(from = 0)
    public float getMinimumScale() {
        return mMinimumScale;
    }

    /**
     * @return The minimal available scale.
     */
    @FloatRange(from = 0)
    public float getMaximumScale() {
        return mMaximumScale;
    }

    @Override
    @NonNull
    public String animationTag() {
        return "blink_scale";
    }

    @Override
    protected void processPreDraw(
            @NonNull Canvas canvas,
            @NonNull IconicsBrush<TextPaint> iconBrush,
            @NonNull IconicsBrush<Paint> iconContourBrush,
            @NonNull IconicsBrush<Paint> backgroundBrush,
            @NonNull IconicsBrush<Paint> backgroundContourBrush) {

        float scaleByPercent = (mMaximumScale - mMinimumScale) / 100;

        float scale = getAnimatedPercent() * scaleByPercent;
        Rect bounds = getDrawableBounds();

        canvas.save();
        canvas.scale(scale, scale, bounds.width() / 2, bounds.height() / 2);
    }

    @Override
    protected void processPostDraw(@NonNull Canvas canvas) {
        canvas.restore();
    }
}
