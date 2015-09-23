package xuml.tools.model.compiler.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

public class Util {

    private static final KryoPool pool = createKryoPool();

    public static byte[] toBytes(Object object) {
        Kryo kryo = pool.borrow();
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            kryo.writeObject(new Output(bytes), object);
            return bytes.toByteArray();
        } finally {
            pool.release(kryo);
        }
    }

    public static Object toObject(byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Kryo kryo = pool.borrow();
        try {
            return kryo.readObject(new Input(in), Object.class);
        } finally {
            pool.release(kryo);
        }
    }

    private static KryoPool createKryoPool() {
        KryoFactory factory = new KryoFactory() {
            @Override
            public Kryo create() {
                Kryo kryo = new Kryo();
                // configure kryo instance, customize settings
                return kryo;
            }
        };
        // Build pool with SoftReferences enabled (optional)
        return new KryoPool.Builder(factory).softReferences().build();
    }

}
