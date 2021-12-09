package com.github.naruyoko.minecrafttassimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class Input {
    private boolean keyForward = false;
    private boolean keyLeft = false;
    private boolean keyBackward = false;
    private boolean keyRight = false;
    private boolean keyJump = false;
    private boolean keySneak = false;
    private boolean keySprint = false;
    private double rotationYaw = 0;
    private double rotationPitch = 0;
    private boolean isRotationExact = true;
    private List<MouseButtonInputEnum> mouseButtonInputs = null;

    public Input(boolean keyForward, boolean keyLeft, boolean keyBackward, boolean keyRight,
            boolean keyJump, boolean keySneak, boolean keySprint, double rotationYaw, double rotationPitch,
            boolean isRotationExact, List<MouseButtonInputEnum> mouseButtonInputs) {
        this.keyForward = keyForward;
        this.keyLeft = keyLeft;
        this.keyBackward = keyBackward;
        this.keyRight = keyRight;
        this.keyJump = keyJump;
        this.keySneak = keySneak;
        this.keySprint = keySprint;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.isRotationExact = isRotationExact;
        this.mouseButtonInputs = Collections.unmodifiableList(mouseButtonInputs);
    }

    public Input() {
        this(false, false, false, false, false, false, false, 0, 0, false, Collections.<MouseButtonInputEnum>emptyList());
    }

    @Override
    public Input clone() {
        return new Input(keyForward, keyLeft, keyBackward, keyRight, keyJump, keySneak, keySprint,
                rotationYaw, rotationPitch, isRotationExact, mouseButtonInputs);
    }

    public boolean isKeyForward() {
        return keyForward;
    }

    public void setKeyForward(boolean keyForward) {
        this.keyForward = keyForward;
    }

    public boolean isKeyLeft() {
        return keyLeft;
    }

    public void setKeyLeft(boolean keyLeft) {
        this.keyLeft = keyLeft;
    }

    public boolean isKeyBackward() {
        return keyBackward;
    }

    public void setKeyBackward(boolean keyBackward) {
        this.keyBackward = keyBackward;
    }

    public boolean isKeyRight() {
        return keyRight;
    }

    public void setKeyRight(boolean keyRight) {
        this.keyRight = keyRight;
    }

    public boolean isKeyJump() {
        return keyJump;
    }

    public void setKeyJump(boolean keyJump) {
        this.keyJump = keyJump;
    }

    public boolean isKeySneak() {
        return keySneak;
    }

    public void setKeySneak(boolean keySneak) {
        this.keySneak = keySneak;
    }

    public boolean isKeySprint() {
        return keySprint;
    }

    public void setKeySprint(boolean keySprint) {
        this.keySprint = keySprint;
    }

    public double getRotationYaw() {
        return rotationYaw;
    }

    public void setRotationYaw(double rotationYaw) {
        this.rotationYaw = rotationYaw;
    }

    public double getRotationPitch() {
        return rotationPitch;
    }

    public void setRotationPitch(double rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public boolean isRotationExact() {
        return isRotationExact;
    }

    public void setRotationExact(boolean isRotationExact) {
        this.isRotationExact = isRotationExact;
    }

    public List<MouseButtonInputEnum> getMouseButtonInputs() {
        return mouseButtonInputs;
    }

    public void setMouseButtonInputs(List<MouseButtonInputEnum> mouseButtonInputs) {
        this.mouseButtonInputs = Collections.unmodifiableList(new ArrayList<MouseButtonInputEnum>(mouseButtonInputs));
    }

    /**
     * {@link net.minecraft.util.MovementInputFromOptions#updatePlayerMoveState()}
     */
    public float movementForward() {
        float r = 0.0F;
        if (keyForward)
            ++r;
        if (keyBackward)
            --r;
        if (keySneak)
            r = (float) ((double) r * 0.3D);
        return r;
    }

    /**
     * {@link net.minecraft.util.MovementInputFromOptions#updatePlayerMoveState()}
     */
    public float movementStrafe() {
        float r = 0.0F;
        if (keyLeft)
            ++r;
        if (keyRight)
            --r;
        if (keySneak)
            r = (float) ((double) r * 0.3D);
        return r;
    }

    /**
     * Creates a new {@link Input}
     * 
     * @param player
     * @param gameSettings
     * @return
     */
    public static Input from(EntityPlayerSP player, GameSettings gameSettings) {
        return new Input(gameSettings.keyBindForward.isKeyDown(),
                gameSettings.keyBindLeft.isKeyDown(), gameSettings.keyBindBack.isKeyDown(),
                gameSettings.keyBindRight.isKeyDown(), gameSettings.keyBindJump.isKeyDown(),
                gameSettings.keyBindSneak.isKeyDown(), gameSettings.keyBindSprint.isKeyDown(), player.rotationYaw,
                player.rotationPitch, false, Collections.<MouseButtonInputEnum>emptyList());
    }

    /**
     * Creates a new {@link Input}
     * 
     * @param mc
     * @return
     */
    public static Input from(Minecraft mc) {
        return Input.from(mc.thePlayer, mc.gameSettings);
    }

    /**
     * Applies this to the player and settings provided.
     * 
     * @param mc
     */
    public void apply(Minecraft mc) {
        EntityPlayerSP player=mc.thePlayer;
        GameSettings gameSettings=mc.gameSettings;
        KeyBinding.setKeyBindState(gameSettings.keyBindForward.getKeyCode(), keyForward);
        KeyBinding.setKeyBindState(gameSettings.keyBindLeft.getKeyCode(), keyLeft);
        KeyBinding.setKeyBindState(gameSettings.keyBindBack.getKeyCode(), keyBackward);
        KeyBinding.setKeyBindState(gameSettings.keyBindRight.getKeyCode(), keyRight);
        KeyBinding.setKeyBindState(gameSettings.keyBindJump.getKeyCode(), keyJump);
        KeyBinding.setKeyBindState(gameSettings.keyBindSneak.getKeyCode(), keySneak);
        KeyBinding.setKeyBindState(gameSettings.keyBindSprint.getKeyCode(), keySprint);
        player.rotationYaw = (float) rotationYaw;
        player.rotationPitch = (float) rotationPitch;
        player.rotationYawHead = player.rotationYaw;
    }

    public enum MouseButtonInputEnum {
        LEFT_DOWN  ('L',0,1),
        LEFT_UP    ('l',0,0),
        RIGHT_DOWN ('R',1,1),
        RIGHT_UP   ('r',1,0);
        private static final Map<Character,MouseButtonInputEnum> lookup=new HashMap<Character,MouseButtonInputEnum>();
        static {
            for (MouseButtonInputEnum v:MouseButtonInputEnum.values())
                lookup.put(v.code,v);
        }
        private final char code;
        private final byte button;
        private final byte state;
        MouseButtonInputEnum(char key,byte button,byte state) {
            this.code=key;
            this.button=button;
            this.state=state;
        }
        MouseButtonInputEnum(char key,int button,int state){
            this(key,(byte)button,(byte)state);
        }
        public char getCode() {
            return code;
        }
        public static MouseButtonInputEnum get(char code) {
            return lookup.get(code);
        }
        public byte getButton() {
            return button;
        }
        public byte getState() {
            return state;
        }
    }
}