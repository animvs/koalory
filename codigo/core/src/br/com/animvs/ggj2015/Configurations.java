package br.com.animvs.ggj2015;

import com.badlogic.gdx.math.Vector2;

public final class Configurations {
    public static final boolean DEBUG_PLAYER_IMMORTAL = false;
    public static final boolean DEBUG_PHYSICS = true;
    public static final boolean SIMULATE_MOBILE_ON_DESKTOP = false;

    public static final Vector2 RESOLUTION_REAL = new Vector2(1280f, 768f);

    public static final String LEVEL_LAYER_COLLISION = "collision";
    public static final String LEVEL_LAYER_ITEMS = "items";
    public static final String LEVEL_LAYER_PLATFORMS = "platform";

    public static final int H_C = 924763985;

    public static final String CORE_LANGUAGE_PATH = "data/lang/";
    public static final float CORE_CAMERA_SPEED_MULTIPLIER = 1f;
    public static final float CORE_PLAYER_ANIM_SPEED_MULTIPLIER = 3f;
    public static final float CORE_TILE_SIZE = 64f;
    public static final float CORE_PLATFORM_SIZE_Y = 18f;
    public static final String CORE_PLATFORM_USER_DATA = "p";
    public static final float CORE_PLATFORM_SPEED = 5f;
    public static final float CORE_PLATFORM_PATH_DISTANCE_TOLERANCE = 50f;
    public static final int CORE_GAMEPAD_BUTTON_ACTION = 1;

    public static final String GRAPHICS_SHADER_COLOR_FRAG = "data/graphics/shaders/frag/color.frag";
    public static final String GRAPHICS_SHADER_DEFAULT_VERT = "data/graphics/shaders/vert/default.vert";

    public static final Vector2 GAMEPLAY_PLAYER_START = new Vector2(175f, 1734f);

    public static final float GAMEPLAY_ZOOM_MAX = 1.6f;

    public static final float GAMEPLAY_ENTITY_SIZE_X = 50f;
    public static final float GAMEPLAY_ENTITY_SIZE_Y = 100f;

    public static final float GAMEPLAY_MOVEMENT_SPEED = 1f;
    public static final float GAMEPLAY_JUMP_FORCE = 10f;
    public static final int GAMEPLAY_MAX_PLAYERS = 5;
    public static final int GAMEPLAY_LIVES_AT_START = 5;

    /*public static final float GAMEPLAY_FOE_SPEED = -0.35f;*/
    /*public static final float GAMEPLAY_JUMP_INTERVAL = 0.25f;*/
}
