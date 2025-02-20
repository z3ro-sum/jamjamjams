
package com.game;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public class EContactListener extends ContactListener{
    
    @Override
    public void onContactStarted(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
        GameObject obj0 = (GameObject) colObj0.userData;
        GameObject obj1 = (GameObject) colObj1.userData;
        
        if (match0){
            //NOTHING FOR NOW IDK
        }
        if (match1){
            obj0.contactedObj = obj1.body.getContactCallbackFlag();
        }
    }
    
    
    @Override
    public void onContactEnded(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
        GameObject obj0 = (GameObject) colObj0.userData;
        GameObject obj1 = (GameObject) colObj1.userData;
        if (match1){
            //Set contact bit off
            obj0.contactedObj = 1;
        }
    }
}
