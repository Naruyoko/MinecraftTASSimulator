package com.github.naruyoko.minecrafttaseditor;

import java.util.ArrayList;

public class MinecraftTASEditorInputList extends ArrayList<MinecraftTASEditorInput> {
    /**
     * 
     */
    private static final long serialVersionUID = -1916440990095217109L;
    public MinecraftTASEditorInputList(ArrayList<MinecraftTASEditorInput> content) {
        super();
        for (int i=0;i<content.size();i++) {
            MinecraftTASEditorInput item=content.get(i);
            if (item==null) add(null);
            else add(item.clone());
        }
    }
    public MinecraftTASEditorInputList() {
        super();
    }
    /**
     * Gets a {@link MinecraftTASEditorInput}.
     * @param index
     * @param safe If {@literal true}, gets value where {@literal null}s are exterpolated as "same as last" and out of bounds are handled safely.
     * @return
     */
    public MinecraftTASEditorInput get(int index,boolean safe) {
        if (safe) {
            if (index<0||size()==0) return new MinecraftTASEditorInput();
            if (index>=size()) index=size()-1;
            while (isNull(index)) {
                if (index==0) return new MinecraftTASEditorInput();
                index--;
            }
            return super.get(index).clone();
        } else {
            return super.get(index).clone();
        }
    }
    /**
     * Gets a {@link MinecraftTASEditorInput} where {@literal null}s are exterpolated as "same as last" and out of bounds are handled safely.
     * @param index
     * @return 
     */
    @Override
    public MinecraftTASEditorInput get(int index) {
        return get(index,true);
    }
    /**
     * Gets whether the input is null ("same as the last")
     * @param index
     * @return boolean
     */
    public boolean isNull(int index) {
        return index<0||index>=size()||super.get(index)==null;
    }
    /**
     * Sets the input at the index, padding inputs with null.
     */
    @Override
    public MinecraftTASEditorInput set(int index,MinecraftTASEditorInput item) {
        MinecraftTASEditorInput oldValue=get(index);
        padInputTo(index);
        super.set(index,item);
        return oldValue;
    }
    private void padInputTo(int index) {
        while (size()<=index) add(null);
    }
}
