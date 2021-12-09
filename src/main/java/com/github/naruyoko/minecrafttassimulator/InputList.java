package com.github.naruyoko.minecrafttassimulator;

import java.util.ArrayList;

public class InputList extends ArrayList<Input> {
    /**
     * 
     */
    private static final long serialVersionUID = -1916440990095217109L;
    public InputList(ArrayList<Input> content) {
        super();
        for (int i=0;i<content.size();i++) {
            Input item=content.get(i);
            if (item==null) add(null);
            else add(item.clone());
        }
    }
    public InputList() {
        super();
    }
    /**
     * Gets a {@link Input}.
     * @param index
     * @param safe If {@literal true}, gets value where {@literal null}s are exterpolated as "same as last" and out of bounds are handled safely.
     * @return
     */
    public Input get(int index,boolean safe) {
        if (safe) {
            if (index<0||size()==0) return new Input();
            if (index>=size()) index=size()-1;
            while (isNull(index)) {
                if (index==0) return new Input();
                index--;
            }
            return super.get(index).clone();
        } else {
            Input r=super.get(index);
            if (r==null) return null;
            else return r.clone();
        }
    }
    /**
     * Gets a {@link Input} where {@literal null}s are exterpolated as "same as last" and out of bounds are handled safely.
     * @param index
     * @return 
     */
    @Override
    public Input get(int index) {
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
     * Sets the input at the index while padding with null.
     */
    @Override
    public Input set(int index,Input item) {
        Input oldValue=get(index);
        padInputTo(index);
        super.set(index,item);
        return oldValue;
    }
    private void padInputTo(int index) {
        while (size()<=index) add(null);
    }
}
