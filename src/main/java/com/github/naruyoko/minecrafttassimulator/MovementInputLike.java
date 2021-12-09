package com.github.naruyoko.minecrafttassimulator;

import net.minecraft.util.MovementInput;

public class MovementInputLike extends MovementInput {
    public void updatePlayerMoveState(Input input) {
        this.moveStrafe=0.0F;
        this.moveForward=0.0F;
        if (input.isKeyForward()) ++this.moveForward;
        if (input.isKeyBackward()) --this.moveForward;
        if (input.isKeyLeft()) ++this.moveStrafe;
        if (input.isKeyRight()) --this.moveStrafe;
        this.jump=input.isKeyJump();
        this.sneak=input.isKeySneak();
        if (this.sneak) {
            this.moveStrafe=(float)((double)this.moveStrafe*0.3D);
            this.moveForward=(float)((double)this.moveForward*0.3D);
        }
    }
}
