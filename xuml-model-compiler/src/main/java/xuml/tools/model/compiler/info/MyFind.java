package xuml.tools.model.compiler.info;

import java.util.List;

public class MyFind {

    private final List<MyIndependentAttribute> attributes;

    public MyFind(List<MyIndependentAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<MyIndependentAttribute> getAttributes() {
        return attributes;
    }

}