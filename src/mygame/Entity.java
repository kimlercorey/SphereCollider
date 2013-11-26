/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

public class Entity {
    private float speed;
    private Vector3f position;
    private Vector3f direction;
    
    public Entity(){
        this.speed = 0.0f;
        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.direction = new Vector3f(0.0f, 0.0f, 0.0f);
    }
    
    public Entity(float speed, Vector3f position, Vector3f direction){
        this.speed = speed;
        this.position = position;
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
    
    // maybe we can change this to an easier format
    @Override
    public String toString() {
        return "Entity{" + "speed=" + speed + ", position=" + position + ", direction=" + direction + '}';
    }
    
    
}
