package xuml.tools.model.compiler.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

public class Util {

    private static final KryoPool pool = createKryoPool();

    public static byte[] toBytes(Object object) {
        if (object == null)
            return new byte[] {};
        Kryo kryo = pool.borrow();
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Output output = new Output(bytes);
            try {
                kryo.writeObject(output, object);
            } finally {
                output.close();
            }
            return bytes.toByteArray();
        } finally {
            pool.release(kryo);
        }
    }

    public static <T> T toObject(byte[] bytes, Class<T> cls) {
        if (bytes.length == 0)
            return null;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Kryo kryo = pool.borrow();
        try {
            Input input = new Input(in);
            try {
                return kryo.readObject(input, cls);
            } finally {
                input.close();
            }
        } finally {
            pool.release(kryo);
        }
    }

    private static KryoPool createKryoPool() {
        KryoFactory factory = new KryoFactory() {
            @Override
            public Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setInstantiatorStrategy(
                        new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                // configure kryo instance, customize settings
                return kryo;
            }
        };
        // Build pool with SoftReferences enabled (optional)
        return new KryoPool.Builder(factory).softReferences().build();
    }

}
