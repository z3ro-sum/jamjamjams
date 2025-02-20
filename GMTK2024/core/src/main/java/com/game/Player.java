package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.JsonReader;

public class Player extends InputAdapter{
    
    private GameObject obj;
    protected final IntIntMap keys = new IntIntMap();
    public int strafeLeftKey = Keys.A;
    public int strafeRightKey = Keys.D;
    public int forwardKey = Keys.W;
    public int backwardKey = Keys.S;
    public int jumpKey = Keys.SPACE;
    
    public float speed = 5f;
    private boolean grounded = false;
    
    public Player(){
        try {
            Model m = new G3dModelLoader( new JsonReader()).loadModel(Gdx.files.internal("models/player.g3dj"));
            obj = new GameObject(m, "player", new btBoxShape(new Vector3(1f,1f,1f)), 1, Constants.CHARACTER_FLAG, new Vector3(0,10f,0), Constants.PLAYER_TAG, 0);
            Gdx.app.log("Instance", "Model " + "PLAYER" + " loaded to environment and added successfully.");
        } catch (Exception e) {
            Gdx.app.error("Instance", "Error loading model.", e);
        } 
        obj.body.setAngularFactor(new Vector3(0,1,0));
        obj.body.setDamping(0.2f, 1.0f); // Set linear damping to 0.2, angular damping to 1.0
        
        Ray ray = new Ray(obj.body.getCenterOfMassPosition(), Vector3.Y);
    }
    
    @Override
    public boolean keyDown(int keycode){
        keys.put(keycode, keycode);
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode){
        keys.remove(keycode, keycode);
        return true;
    }
    
    public void Update(){
        if(obj.contactedObj == Constants.INTERACTABLE_TAG){
        }
        grounded = obj.contactedObj == Constants.SOLIDSURFACE_TAG;
                 
        Vector3 velocity = new Vector3(0, obj.body.getLinearVelocity().y, 0);

        if (keys.containsKey(forwardKey)) {
            velocity.z -= speed;
        }
        if (keys.containsKey(backwardKey)) {
            velocity.z += speed;
        }
        if (keys.containsKey(strafeLeftKey)) {
            velocity.x -= speed;
        }
        if (keys.containsKey(strafeRightKey)) {
            velocity.x += speed;
        }
        
        //Jump mechanic needs some fixing
        if (keys.containsKey(jumpKey) && grounded){
            obj.body.applyCentralForce(new Vector3(0,320f,0));
        }

        obj.body.setLinearVelocity(velocity);
        obj.body.setAngularVelocity(Vector3.Zero);
    }
    
    public GameObject GetGameObject(){
        return obj;
    }
}
