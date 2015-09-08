package xuml.tools.model.compiler.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Util {

    public static byte[] toBytes(Object object) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bytes);
            oos.writeObject(object);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes.toByteArray();
    }

    public static Object toObject(byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Object object;
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            object = ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

}
