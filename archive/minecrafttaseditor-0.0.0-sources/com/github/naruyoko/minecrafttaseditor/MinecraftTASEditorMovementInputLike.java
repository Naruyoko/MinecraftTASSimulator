package com.github.naruyoko.minecrafttaseditor;

import net.minecraft.util.MovementInput;

public class MinecraftTASEditorMovementInputLike extends MovementInput {
    public void updatePlayerMoveState(MinecraftTASEditorInput input) {
        this.field_78902_a=0.0F;
        this.field_78900_b=0.0F;
        if (input.isKeyForward()) ++this.field_78900_b;
        if (input.isKeyBackward()) --this.field_78900_b;
        if (input.isKeyLeft()) ++this.field_78902_a;
        if (input.isKeyRight()) --this.field_78902_a;
        this.field_78901_c=input.isKeyJump();
        this.field_78899_d=input.isKeySneak();
        if (this.field_78899_d) {
            this.field_78902_a=(float)((double)this.field_78902_a*0.3D);
            this.field_78900_b=(float)((double)this.field_78900_b*0.3D);
        }
    }
}
