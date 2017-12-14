package com.jerbotron_mac.spotishake.shared;

import android.support.annotation.IntDef;

import com.jerbotron_mac.spotishake.R;

import java.util.Random;

public class MaterialColor {

    private static final int SIZE = 18;     // keep this updated

    private static final Random random = new Random();

    public static int getRandomColor() {
        return random.nextInt(SIZE);
    }

    public static int getValue(@MaterialColors int color) {
        switch(color) {
            case MaterialColors.RED:
                return R.color.material_red;
            case MaterialColors.PINK:
                return R.color.material_pink;
            case MaterialColors.PURPLE:
                return R.color.material_purple;
            case MaterialColors.DEEP_PURPLE:
                return R.color.material_deep_purple;
            case MaterialColors.INDIGO:
                return R.color.material_indigo;
            case MaterialColors.BLUE:
                return R.color.material_blue;
            case MaterialColors.LIGHT_BLUE:
                return R.color.material_light_blue;
            case MaterialColors.CYAN:
                return R.color.material_cyan;
            case MaterialColors.TEAL:
                return R.color.material_teal;
            case MaterialColors.GREEN:
                return R.color.material_green;
            case MaterialColors.LIGHT_GREEN:
                return R.color.material_light_green;
            case MaterialColors.LIME:
                return R.color.material_lime;
            case MaterialColors.YELLOW:
                return R.color.material_yellow;
            case MaterialColors.AMBER:
                return R.color.material_amber;
            case MaterialColors.ORANGE:
                return R.color.material_orange;
            case MaterialColors.DEEP_ORANGE:
                return R.color.material_deep_orange;
            case MaterialColors.BROWN:
                return R.color.material_brown;
            case MaterialColors.BLUE_GREY:
                return R.color.material_blue_grey;
        }
        return R.color.material_grey;
    }

    public static boolean shouldUseBlackText(@MaterialColors int color) {
        switch (color) {
            case MaterialColors.BLUE:
            case MaterialColors.LIGHT_BLUE:
            case MaterialColors.CYAN:
            case MaterialColors.GREEN:
            case MaterialColors.LIGHT_GREEN:
            case MaterialColors.LIME:
            case MaterialColors.YELLOW:
            case MaterialColors.AMBER:
            case MaterialColors.ORANGE:
            case MaterialColors.DEEP_ORANGE:
            return true;
        }
        return false;
    }

    @IntDef({MaterialColors.RED,
            MaterialColors.PINK,
            MaterialColors.PURPLE,
            MaterialColors.DEEP_PURPLE,
            MaterialColors.INDIGO,
            MaterialColors.BLUE,
            MaterialColors.LIGHT_BLUE,
            MaterialColors.CYAN,
            MaterialColors.TEAL,
            MaterialColors.GREEN,
            MaterialColors.LIGHT_GREEN,
            MaterialColors.LIME,
            MaterialColors.YELLOW,
            MaterialColors.AMBER,
            MaterialColors.ORANGE,
            MaterialColors.DEEP_ORANGE,
            MaterialColors.BROWN,
            MaterialColors.BLUE_GREY})
    public @interface MaterialColors {
        int RED = 0;
        int PINK = 1;
        int PURPLE = 2;
        int DEEP_PURPLE = 3;
        int INDIGO = 4;
        int BLUE = 5;
        int LIGHT_BLUE = 6;
        int CYAN = 7;
        int TEAL = 8;
        int GREEN = 9;
        int LIGHT_GREEN = 10;
        int LIME = 11;
        int YELLOW = 12;
        int AMBER = 13;
        int ORANGE = 14;
        int DEEP_ORANGE = 15;
        int BROWN = 16;
        int BLUE_GREY = 17;
    }
}
