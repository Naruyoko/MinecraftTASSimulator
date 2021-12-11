package com.github.naruyoko.minecrafttassimulator;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;

public abstract class InputFileReader extends InputFileWorkerBase {
    public InputFileReader(File file) {
        super(file);
    }
    /**
     * Indicate if the file format to be read is an external one.
     * @return If the file format is external.
     */
    public static boolean isExternalFormat() {
        return true;
    }
    /**
     * Read from the file held with the inputs in a format. It should be marked that the format is external if it is.
     * @return Some value
     */
    public abstract ReadReturnValue read();
    public class MessageWithLevelAndLineNum {
        public Level level;
        public MessageWithLevelAndLineNum(Level level, String message, int lineN) {
            super();
            this.level = level;
            this.message = message;
            this.lineN = lineN;
        }
        public String message;
        public int lineN;
    }
    public class ReadReturnValue {
        public List<MessageWithLevelAndLineNum> messages;
        public ReadReturnValue(List<MessageWithLevelAndLineNum> messages) {
            this.messages = messages;
        }
        public ReadReturnValue() {
            this(Collections.<MessageWithLevelAndLineNum>emptyList());
        }
    }
    public class ReadSuccess extends ReadReturnValue {
        public InputFileInformation inputFileInformation;
        public ReadSuccess(List<MessageWithLevelAndLineNum> messages,InputFileInformation inputFileInformation) {
            super(messages);
            this.inputFileInformation = inputFileInformation;
        }
    }
    public class ReadFailure extends ReadReturnValue {
        public String message;
        public int lineN;
        public ReadFailure(List<MessageWithLevelAndLineNum> messages,String message,int lineN) {
            super(messages);
            this.message = message;
            this.lineN = lineN;
        }
    }
    public class ReadException extends ReadReturnValue {
        public Exception exception;
        public int lineN;
        public ReadException(List<MessageWithLevelAndLineNum> messages,Exception exception, int lineN) {
            super(messages);
            this.exception = exception;
            this.lineN = lineN;
        }
    }
}
