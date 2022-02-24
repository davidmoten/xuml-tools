package xuml.tools.model.compiler.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;

public class Util {

    private static final Pool<Kryo> pool = createKryoPool();

    public static byte[] toBytes(Object object) {
        if (object == null)
            return new byte[] {};
        Kryo kryo = pool.obtain();
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
            pool.free(kryo);
        }
    }

    public static <T> T toObject(byte[] bytes, Class<T> cls) {
        if (bytes.length == 0)
            return null;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Kryo kryo = pool.obtain();
        try {
            Input input = new Input(in);
            try {
                return kryo.readObject(input, cls);
            } finally {
                input.close();
            }
        } finally {
            pool.free(kryo);
        }
    }

    private static Pool<Kryo> createKryoPool() {
        return  new Pool<Kryo>(true, true, 8) {
            @Override
            public Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setRegistrationRequired(false);
                kryo.setInstantiatorStrategy(
                        new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                // configure kryo instance, customize settings
                return kryo;
            }
        };
    }

}
