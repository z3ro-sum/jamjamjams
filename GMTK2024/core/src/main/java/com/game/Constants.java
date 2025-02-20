package com.game;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public class Constants {
    public static final short ALL_FLAG = -1;
    public static final int KINEMATIC_FLAG = btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT;
    public static final int DYNAMIC_FLAG = 0;
    public static final int CHARACTER_FLAG = btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT;
    
    public static final int SOLIDSURFACE_TAG = 4;
    public static final int INTERACTABLE_TAG = 6;
    public static final int PLAYER_TAG = 2;
}
