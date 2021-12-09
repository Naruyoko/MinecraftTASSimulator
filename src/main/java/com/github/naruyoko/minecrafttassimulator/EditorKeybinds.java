package com.github.naruyoko.minecrafttassimulator;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EditorKeybinds {
    Minecraft mc=Minecraft.getMinecraft();
    private boolean blockToggleEditor=false;
    private boolean blockInstantiateRunners=false;
    private boolean blockToggleSimulation=false;
    private boolean blockTogglePrediction=false;
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        boolean isLShiftDown=Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean isLCtrlDown=Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
        boolean isLMenuDown=Keyboard.isKeyDown(Keyboard.KEY_LMENU);
        byte modifiers=(byte)((isLShiftDown?1:0)|(isLCtrlDown?2:0)|(isLMenuDown?4:0));
        if (!blockToggleEditor&&modifiers==3&&Keyboard.isKeyDown(Keyboard.KEY_P)) { //+^P
            if (InputEditor.isRunning()) {
                InputEditor.stop();
            } else {
                InputEditor.start();
            }
            blockToggleEditor=true;
        }
        if (!blockInstantiateRunners&&modifiers==3&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //+^R
            if (InputEditor.isRunning()) {
                InputEditor.instanciateRunners();
            }
            blockInstantiateRunners=true;
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //R
            if (InputEditor.isRunning()) {
                int selectedTick=InputEditor.getSelectedTick();
                if (selectedTick!=-1) InputEditor.selectTick(selectedTick-1);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //+R
            if (InputEditor.isRunning()) {
                InputEditor.deselectTick();
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //Y
            if (InputEditor.isRunning()) {
                int selectedTick=InputEditor.getSelectedTick();
                if (selectedTick+1<InputEditor.getInputLength()) InputEditor.selectTick(selectedTick+1);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //+Y
            if (InputEditor.isRunning()) {
                int selectedTick=InputEditor.getSelectedTick();
                if (selectedTick+1<InputEditor.getInputLength()) InputEditor.selectTick(InputEditor.getInputLength()-1);
                else InputEditor.selectTick(selectedTick+1);
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //!R
            if (InputEditor.isRunning()) {
                int startTick=InputEditor.getPredictionStartTick();
                if (startTick!=-1) InputEditor.setPredictionStartTick(startTick-1);
            }
        }
        if (modifiers==5&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //!+R
            if (InputEditor.isRunning()) {
                InputEditor.setPredictionStartTick(-1);
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //!Y
            if (InputEditor.isRunning()) {
                int startTick=InputEditor.getPredictionStartTick();
                if (startTick+1<InputEditor.getSimulator().getComputedTicksN()) InputEditor.setPredictionStartTick(startTick+1);
            }
        }
        if (modifiers==5&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //!+Y
            if (InputEditor.isRunning()) {
                InputEditor.setPredictionStartTick(InputEditor.getSimulator().getComputedTicksN()-1);
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_T)) { //!T
            if (InputEditor.isRunning()) {
                InputEditor.setPredictionStartTick(InputEditor.getSelectedTick());
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_C)) { //C
            if (InputEditor.isRunning()) {
                int tickLength=InputEditor.getTickLength();
                if (tickLength!=-1) InputEditor.setTickLength(tickLength-1);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_V)) { //V
            if (InputEditor.isRunning()) {
                int tickLength=InputEditor.getTickLength();
                InputEditor.setTickLength(tickLength+1);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_I)) { //I
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setKeyForward(!input.isKeyForward());
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_J)) { //J
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setKeyLeft(!input.isKeyLeft());
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_K)) { //K
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setKeyBackward(!input.isKeyBackward());
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_L)) { //L
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setKeyRight(!input.isKeyRight());
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_B)) { //B
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setKeyJump(!input.isKeyJump());
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_N)) { //N
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setKeySneak(!input.isKeySneak());
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_M)) { //M
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setKeySprint(!input.isKeySprint());
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_I)) { //+I
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setRotationPitch(input.getRotationPitch()-SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity()));
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_J)) { //+J
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setRotationYaw(input.getRotationYaw()-SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity()));
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_K)) { //+K
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setRotationPitch(input.getRotationPitch()+SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity()));
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_L)) { //+L
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setRotationYaw(input.getRotationYaw()+SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity()));
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_U)) { //+U
            if (InputEditor.isRunning()&&InputEditor.isSelectingTick()) {
                Input input=InputEditor.getSelectedInput();
                input.setRotationYaw(mc.thePlayer.rotationYaw);
                input.setRotationPitch(mc.thePlayer.rotationPitch);
                InputEditor.setSelectedInput(input);
            }
        }
        if (modifiers==3&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //+^Y
            if (InputEditor.isRunning()) {
                InputEditor.getSimulator().appendState();
            }
        }
        if (!blockToggleSimulation&&modifiers==2&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //^R
            if (InputEditor.isRunning()) {
                if (InputEditor.isSimulationRunning()) InputEditor.abortSimulation();
                else InputEditor.startSimulation();
            }
            blockToggleSimulation=true;
        }
        if (!blockTogglePrediction&&modifiers==2&&Keyboard.isKeyDown(Keyboard.KEY_P)) { //^P
            if (InputEditor.isRunning()) {
                if (InputEditor.isPredictionRunning()) InputEditor.abortPrediction();
                else InputEditor.startPrediction();
            }
            blockTogglePrediction=true;
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_DELETE)) { //!Delete
            if (InputEditor.isRunning()) {
                if (InputEditor.isSelectingTick()) {
                    InputEditor.setSelectedInput(null);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_K)) { //!K
            if (InputEditor.isRunning()) {
                if (InputEditor.isSelectingTick()) {
                    Input input=InputEditor.getSelectedInput();
                    Input lastinput=InputEditor.getInputAt(InputEditor.getSelectedTick()-1);
                    input.setRotationYaw(lastinput.getRotationYaw());
                    input.setRotationPitch(lastinput.getRotationPitch());
                    InputEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_U)) { //!U
            if (InputEditor.isRunning()) {
                if (InputEditor.isSelectingTick()) {
                    Input input=InputEditor.getSelectedInput();
                    double rotationPerPixel=SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity());
                    int pixels=(int)Math.round(-45D/rotationPerPixel);
                    input.setRotationYaw(input.getRotationYaw()+pixels*rotationPerPixel);
                    InputEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_O)) { //!O
            if (InputEditor.isRunning()) {
                if (InputEditor.isSelectingTick()) {
                    Input input=InputEditor.getSelectedInput();
                    double rotationPerPixel=SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity());
                    int pixels=(int)Math.round(45D/rotationPerPixel);
                    input.setRotationYaw(input.getRotationYaw()+pixels*rotationPerPixel);
                    InputEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_J)) { //!J
            if (InputEditor.isRunning()) {
                if (InputEditor.isSelectingTick()) {
                    Input input=InputEditor.getSelectedInput();
                    double rotationPerPixel=SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity());
                    int pixels=(int)Math.round(-90D/rotationPerPixel);
                    input.setRotationYaw(input.getRotationYaw()+pixels*rotationPerPixel);
                    InputEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_L)) { //!L
            if (InputEditor.isRunning()) {
                if (InputEditor.isSelectingTick()) {
                    Input input=InputEditor.getSelectedInput();
                    double rotationPerPixel=SimulatorUtil.getRotationPerPixel(InputEditor.getMouseSensitivity());
                    int pixels=(int)Math.round(90D/rotationPerPixel);
                    input.setRotationYaw(input.getRotationYaw()+pixels*rotationPerPixel);
                    InputEditor.setSelectedInput(input);
                }
            }
        }
    }
    public void unblockKeyBinds(TickEvent.ClientTickEvent event) {
        if (event.phase==TickEvent.Phase.END) {
            blockToggleEditor=false;
            blockInstantiateRunners=false;
            blockToggleSimulation=false;
            blockTogglePrediction=false;
        }
    }
}
