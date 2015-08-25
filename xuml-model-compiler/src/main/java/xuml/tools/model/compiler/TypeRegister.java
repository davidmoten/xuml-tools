package xuml.tools.model.compiler;

import java.util.TreeSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TypeRegister {

    /**
     * Full type name -> abbr (if possible)
     */
    private final BiMap<String, String> types = HashBiMap.create();

    public String addType(java.lang.Class<?> clsWithoutGenerics) {
        return addType(new Type(clsWithoutGenerics.getName()));
    }

    public String addType(Type type) {
        StringBuilder result = new StringBuilder(addType(type.getBase()));
        StringBuilder typeParams = new StringBuilder();
        for (Type t : type.getGenerics()) {
            String typeParameter = addType(t);
            if (typeParams.length() > 0)
                typeParams.append(",");
            typeParams.append(typeParameter);
        }
        if (typeParams.length() > 0) {
            result.append("<");
            result.append(typeParams);
            result.append(">");
        }
        if (type.isArray())
            result.append("[]");
        return result.toString();
    }

    String addType(String type) {
        String abbr = types.get(type);
        if (abbr != null)
            return abbr;
        else {
            int i = type.lastIndexOf(".");
            if (i >= 0) {
                String last = type.substring(i + 1);
                if (types.inverse().get(last) != null)
                    return type;
                else {
                    types.put(type, last);
                    return last;
                }
            } else
                return type;
        }
    }

    public String getImports(String relativeToClass) {
        TreeSet<String> set = new TreeSet<String>(types.keySet());
        StringBuilder s = new StringBuilder();
        for (String t : set) {
            boolean isImmediateChildOfRelativeClass = t.startsWith(relativeToClass)
                    && t.length() > relativeToClass.length()
                    && t.indexOf('.', relativeToClass.length() + 1) == -1;
            if (!isImmediateChildOfRelativeClass)
                s.append("import " + t + ";\n");
        }
        return s.toString();
    }

    public void addTypes(java.lang.Class<?>... classes) {
        for (Class<?> cls : classes) {
            addType(cls);
        }
    }
}
