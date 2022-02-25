package xuml.tools.model.compiler;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import jakarta.xml.bind.JAXBElement;
import xuml.tools.miuml.metamodel.jaxb.Class;
import xuml.tools.miuml.metamodel.jaxb.Domain;
import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.Marshaller;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.miuml.metamodel.jaxb.Perspective;
import xuml.tools.miuml.metamodel.jaxb.Subsystem;
import xuml.tools.miuml.metamodel.jaxb.SubsystemElement;

public class Util {

    public static String getMultiplicityAbbreviation(Perspective p) {
        if (p.isConditional() && p.isOnePerspective())
            return "0..1";
        else if (!p.isConditional() && p.isOnePerspective())
            return "1";
        else if (p.isConditional() && !p.isOnePerspective())
            return "*";
        else
            return "1..*";
    }

    public static String lowerFirst(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String upperFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String toClassSimpleName(String name) {
        return upperFirst(toJavaIdentifier(name));
    }

    public static String toJavaIdentifier(String name) {

        StringBuilder s = new StringBuilder();
        boolean capitalize = false;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if ((i != 0 && !Character.isJavaIdentifierStart(ch))
                    || !Character.isJavaIdentifierPart(ch)) {
                capitalize = true;
            } else if (capitalize) {
                s.append(Character.toUpperCase(ch));
                capitalize = false;
            } else
                s.append(ch);
        }
        return lowerFirst(s.toString());
    }

    public static String toJavaConstantIdentifier(String name) {
        StringBuilder s = new StringBuilder();
        boolean funnyCharacter = false;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if ((i == 0 && !Character.isJavaIdentifierStart(ch))
                    || (i > 0 && !Character.isJavaIdentifierPart(ch))) {
                funnyCharacter = true;
            } else if (funnyCharacter) {
                s.append("_");
                s.append(Character.toUpperCase(ch));
                funnyCharacter = false;
            } else
                s.append(Character.toUpperCase(ch));
        }
        return s.toString();
    }

    @VisibleForTesting
    static boolean isLettersAndDigits(String s) {
        return Pattern.compile("[0-9a-zA-Z]*").matcher(s).matches();
    }

    public static String camelCaseToLowerUnderscore(String s) {
        if (s.toUpperCase().equals(s) && isLettersAndDigits(s)) {
            return s.toLowerCase();
        }

        StringBuilder b = new StringBuilder();
        b.append(s.charAt(0));
        boolean underscoreAdded = false;
        boolean lastCharacterUppercase = false;
        for (int i = 1; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!Character.isLetterOrDigit(ch)) {
                if (!underscoreAdded)
                    b.append('_');
                underscoreAdded = true;
                lastCharacterUppercase = false;
            } else if (Character.isUpperCase(ch)) {
                if (!underscoreAdded && !lastCharacterUppercase) {
                    b.append("_");
                }
                b.append(ch);
                underscoreAdded = false;
                lastCharacterUppercase = true;
            } else {
                b.append(ch);
                underscoreAdded = false;
                lastCharacterUppercase = false;
            }
        }
        return b.toString().toLowerCase();
    }

    public static String toTableName(String className) {
        return camelCaseToLowerUnderscore(className);
    }

    public static String toTableIdName(String className) {
        return toTableName(className) + "_id";
    }

    public static String toColumnName(String attributeName) {
        return camelCaseToLowerUnderscore(attributeName);
    }

    public static ModeledDomain getModeledDomain(Domains domains, String name) {
        for (JAXBElement<? extends Domain> domain : domains.getDomain()) {
            if (domain.getValue() instanceof ModeledDomain
                    && name.equals(domain.getValue().getName())) {
                return (ModeledDomain) domain.getValue();
            }
        }
        return null;
    }

    public static ModeledDomain getModeledDomain(InputStream is, String domainName) {
        Domains domains = new Marshaller().unmarshal(is);
        return domains.getDomain().stream().map(d -> d.getValue())
                .filter(d -> d instanceof ModeledDomain).map(d -> (ModeledDomain) d)
                .filter(d -> d.getName().equals(domainName)).findFirst().get();

    }

    public static String getPackage(String className) {
        if (!className.contains("."))
            return className;
        else
            return className.substring(0, className.lastIndexOf("."));
    }

    public static String getSimpleClassName(String className) {
        if (!className.contains("."))
            return className;
        else
            return className.substring(className.lastIndexOf(".") + 1, className.length());
    }

    public static List<Class> getClasses(ModeledDomain domain) {
        List<Class> list = Lists.newArrayList();
        for (Subsystem subsystem : domain.getSubsystem()) {
            for (JAXBElement<? extends SubsystemElement> element : subsystem
                    .getSubsystemElement()) {
                if (element.getValue() instanceof Class) {
                    Class cls = (Class) element.getValue();
                    list.add(cls);
                }
            }
        }
        return list;
    }

}