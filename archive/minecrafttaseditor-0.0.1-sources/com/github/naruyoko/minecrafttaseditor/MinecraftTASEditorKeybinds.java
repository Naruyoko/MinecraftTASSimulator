package com.github.naruyoko.minecrafttaseditor;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class MinecraftTASEditorKeybinds {
    Minecraft mc=Minecraft.func_71410_x();
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        boolean isLShiftDown=Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean isLCtrlDown=Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
        boolean isLMenuDown=Keyboard.isKeyDown(Keyboard.KEY_LMENU);
        byte modifiers=(byte)((isLShiftDown?1:0)|(isLCtrlDown?2:0)|(isLMenuDown?4:0));
        if (modifiers==3&&Keyboard.isKeyDown(Keyboard.KEY_P)) { //+^P
            if (MinecraftTASEditorEditor.isRunning()) {
                MinecraftTASEditorEditor.stop();
            } else {
                MinecraftTASEditorEditor.start();
            }
        }
        if (modifiers==3&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //+^R
            if (MinecraftTASEditorEditor.isRunning()) {
                MinecraftTASEditorEditor.instanciateRunners();
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //R
            if (MinecraftTASEditorEditor.isRunning()) {
                int selectedTick=MinecraftTASEditorEditor.getSelectedTick();
                if (selectedTick!=-1) MinecraftTASEditorEditor.selectTick(selectedTick-1);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //+R
            if (MinecraftTASEditorEditor.isRunning()) {
                MinecraftTASEditorEditor.deselectTick();
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //Y
            if (MinecraftTASEditorEditor.isRunning()) {
                int selectedTick=MinecraftTASEditorEditor.getSelectedTick();
                if (selectedTick+1<MinecraftTASEditorEditor.getInputLength()) MinecraftTASEditorEditor.selectTick(selectedTick+1);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //+Y
            if (MinecraftTASEditorEditor.isRunning()) {
                int selectedTick=MinecraftTASEditorEditor.getSelectedTick();
                if (selectedTick+1<MinecraftTASEditorEditor.getInputLength()) MinecraftTASEditorEditor.selectTick(MinecraftTASEditorEditor.getInputLength()-1);
                else MinecraftTASEditorEditor.selectTick(selectedTick+1);
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //!R
            if (MinecraftTASEditorEditor.isRunning()) {
                int startTick=MinecraftTASEditorEditor.getPredictionStartTick();
                if (startTick!=-1) MinecraftTASEditorEditor.setPredictionStartTick(startTick-1);
            }
        }
        if (modifiers==5&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //!+R
            if (MinecraftTASEditorEditor.isRunning()) {
                MinecraftTASEditorEditor.setPredictionStartTick(-1);
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //!Y
            if (MinecraftTASEditorEditor.isRunning()) {
                int startTick=MinecraftTASEditorEditor.getPredictionStartTick();
                if (startTick+1<MinecraftTASEditorEditor.getSimulator().getComputedTicksN()) MinecraftTASEditorEditor.setPredictionStartTick(startTick+1);
            }
        }
        if (modifiers==5&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //!+Y
            if (MinecraftTASEditorEditor.isRunning()) {
                MinecraftTASEditorEditor.setPredictionStartTick(MinecraftTASEditorEditor.getSimulator().getComputedTicksN()-1);
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_T)) { //!T
            if (MinecraftTASEditorEditor.isRunning()) {
                MinecraftTASEditorEditor.setPredictionStartTick(MinecraftTASEditorEditor.getSelectedTick());
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_C)) { //C
            if (MinecraftTASEditorEditor.isRunning()) {
                int tickLength=MinecraftTASEditorEditor.getTickLength();
                if (tickLength!=-1) MinecraftTASEditorEditor.setTickLength(tickLength-1);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_V)) { //V
            if (MinecraftTASEditorEditor.isRunning()) {
                int tickLength=MinecraftTASEditorEditor.getTickLength();
                MinecraftTASEditorEditor.setTickLength(tickLength+1);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_I)) { //I
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setKeyForward(!input.isKeyForward());
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_J)) { //J
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setKeyLeft(!input.isKeyLeft());
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_K)) { //K
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setKeyBackward(!input.isKeyBackward());
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_L)) { //L
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setKeyRight(!input.isKeyRight());
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_B)) { //B
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setKeyJump(!input.isKeyJump());
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_N)) { //N
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setKeySneak(!input.isKeySneak());
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==0&&Keyboard.isKeyDown(Keyboard.KEY_M)) { //M
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setKeySprint(!input.isKeySprint());
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_I)) { //+I
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setRotationPitch(input.getRotationPitch()+0.15D);
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_J)) { //+J
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setRotationYaw(input.getRotationYaw()-0.15D);
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_K)) { //+K
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setRotationPitch(input.getRotationPitch()-0.15D);
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_L)) { //+L
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setRotationYaw(input.getRotationYaw()+0.15D);
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==1&&Keyboard.isKeyDown(Keyboard.KEY_U)) { //+U
            if (MinecraftTASEditorEditor.isRunning()&&MinecraftTASEditorEditor.isSelectingTick()) {
                MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                input.setRotationYaw(mc.field_71439_g.field_70177_z);
                input.setRotationPitch(mc.field_71439_g.field_70125_A);
                MinecraftTASEditorEditor.setSelectedInput(input);
            }
        }
        if (modifiers==3&&Keyboard.isKeyDown(Keyboard.KEY_Y)) { //+^Y
            if (MinecraftTASEditorEditor.isRunning()) {
                MinecraftTASEditorEditor.getSimulator().appendState();
            }
        }
        if (modifiers==2&&Keyboard.isKeyDown(Keyboard.KEY_R)) { //^R
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isSimulationRunning()) MinecraftTASEditorEditor.abortSimulation();
                else MinecraftTASEditorEditor.startSimulation();
            }
        }
        if (modifiers==2&&Keyboard.isKeyDown(Keyboard.KEY_P)) { //^P
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isPredictionRunning()) MinecraftTASEditorEditor.abortPrediction();
                else MinecraftTASEditorEditor.startPrediction();
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_DELETE)) { //!Delete
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isSelectingTick()) {
                    MinecraftTASEditorEditor.setSelectedInput(null);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_K)) { //!K
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isSelectingTick()) {
                    MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                    MinecraftTASEditorInput lastinput=MinecraftTASEditorEditor.getInputAt(MinecraftTASEditorEditor.getSelectedTick()-1);
                    input.setRotationYaw(lastinput.getRotationYaw());
                    input.setRotationPitch(lastinput.getRotationPitch());
                    MinecraftTASEditorEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_U)) { //!U
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isSelectingTick()) {
                    MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                    input.setRotationYaw(input.getRotationYaw()-45D);
                    MinecraftTASEditorEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_O)) { //!O
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isSelectingTick()) {
                    MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                    input.setRotationYaw(input.getRotationYaw()+45D);
                    MinecraftTASEditorEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_J)) { //!J
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isSelectingTick()) {
                    MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                    input.setRotationYaw(input.getRotationYaw()-90D);
                    MinecraftTASEditorEditor.setSelectedInput(input);
                }
            }
        }
        if (modifiers==4&&Keyboard.isKeyDown(Keyboard.KEY_L)) { //!L
            if (MinecraftTASEditorEditor.isRunning()) {
                if (MinecraftTASEditorEditor.isSelectingTick()) {
                    MinecraftTASEditorInput input=MinecraftTASEditorEditor.getSelectedInput();
                    input.setRotationYaw(input.getRotationYaw()+90D);
                    MinecraftTASEditorEditor.setSelectedInput(input);
                }
            }
        }
    }
}
