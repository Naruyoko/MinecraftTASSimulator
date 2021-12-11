package com.github.naruyoko.minecrafttassimulator;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public enum InputFileWorkerEnum {
    MCSIM(".mcsim",MCSimFileReader.class,MCSimFileWriter.class);
    private final String fileExtension;
    private final Class<? extends InputFileReader> readerClass;
    private final Class<? extends InputFileWriter> writerClass;
    private static final Map<String,InputFileWorkerEnum> lookup=new HashMap<String,InputFileWorkerEnum>();
    static {
        for (InputFileWorkerEnum v:InputFileWorkerEnum.values())
            lookup.put(v.getFileExtension(),v);
    }
    private InputFileWorkerEnum(String fileExtension, Class<? extends InputFileReader> readerClass,
            Class<? extends InputFileWriter> writerClass) {
        this.fileExtension = fileExtension;
        this.readerClass = readerClass;
        this.writerClass = writerClass;
    }
    public String getFileExtension() {
        return fileExtension;
    }
    public Class<? extends InputFileReader> getReaderClass() {
        return readerClass;
    }
    public Class<? extends InputFileWriter> getWriterClass() {
        return writerClass;
    }
    public static Set<String> getReadableExtensions() {
        Set<String> r=new HashSet<String>();
        for (Entry<String,InputFileWorkerEnum> entry:lookup.entrySet()) {
            if (entry.getValue().getReaderClass()!=null) r.add(entry.getKey());
        }
        return r;
    }
    public static Set<String> getWritableExtensions() {
        Set<String> r=new HashSet<String>();
        for (Entry<String,InputFileWorkerEnum> entry:lookup.entrySet()) {
            if (entry.getValue().getWriterClass()!=null) r.add(entry.getKey());
        }
        return r;
    }
    private static String formatFileExtension(String fileExtension) {
        return '.'+fileExtension.substring(fileExtension.charAt(0)=='.'?1:0).toLowerCase();
    }
    public static boolean has(String fileExtension) {
        return lookup.containsKey(formatFileExtension(fileExtension));
    }
    public static InputFileWorkerEnum get(String fileExtension) {
        return lookup.get(formatFileExtension(fileExtension));
    }
    public static boolean isReadableExtension(String fileExtension) {
        return has(fileExtension)&&get(fileExtension).getReaderClass()!=null;
    }
    public static boolean isWritableExtension(String fileExtension) {
        return has(fileExtension)&&get(fileExtension).getWriterClass()!=null;
    }
    public static InputFileReader newReader(String fileExtension,File file) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<? extends InputFileReader> readerClass=get(fileExtension).getReaderClass();
        Constructor<? extends InputFileReader> constructor=readerClass.getConstructor(File.class);
        return constructor.newInstance(file);
    }
    public static InputFileWriter newWriter(String fileExtension,File file,InputFileInformation inputFileInformation) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<? extends InputFileWriter> writerClass=get(fileExtension).getWriterClass();
        Constructor<? extends InputFileWriter> constructor=writerClass.getConstructor(File.class,InputFileInformation.class);
        return constructor.newInstance(file,inputFileInformation);
    }
}
