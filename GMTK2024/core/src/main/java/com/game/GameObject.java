package com.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ModelInstance implements Disposable{
    
    public final btRigidBody body;
    public final ObjMotionState motionState;
    public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
    private static Vector3 localInertia = new Vector3();
    
    public btCollisionShape shape;
    
    private final static BoundingBox bounds = new BoundingBox();
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public final float radius;
    public int contactedObj;
    
    public GameObject(Model m, String n, btCollisionShape s, float mass, int cflag, Vector3 pos, int contactFlag, int contactFilter){
        super(m, n); //Set the model and name for the modelinstance
        if(s == null){
            this.shape = Bullet.obtainStaticNodeShape(m.nodes);
        } else this.shape = s;
        if(mass > 0f) shape.calculateLocalInertia(mass, localInertia);
        else localInertia.set(0, 0, 0);
        
        this.constructionInfo = new btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        this.body = new btRigidBody(constructionInfo);
        this.motionState = new ObjMotionState();
        motionState.transform = transform;
        body.setMotionState(motionState);
        body.setContactCallbackFlag(contactFlag);
        body.setContactCallbackFilter(contactFilter);
        body.userData = this;
        
        calculateBoundingBox(bounds);
        
        Vector3 v = new Vector3();
        bounds.getCenter(v);
        if (pos != null) body.translate(pos);
        else body.translate(v);
        
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2f;
        
        body.setCollisionFlags(body.getCollisionFlags() | cflag);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
    }

    @Override
    public void dispose() {
        body.dispose();
        motionState.dispose();
        constructionInfo.dispose();
    }
    
}
