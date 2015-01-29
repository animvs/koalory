package br.com.animvs.koalory.entities.physics;

import com.badlogic.gdx.physics.box2d.Body;

public interface PhysicBodyHolder {
    public Body getBody();
    public void setBody(Body body);
}
