package xuml.tools.model.compiler.info;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import xuml.tools.model.compiler.Type;

public final class MyTypeDefinition {
    private final String name;
    private final MyType myType;
    private final Type type;
    private final String units;
    private final BigInteger precision;
    private final BigDecimal lowerLimit;
    private final BigDecimal upperLimit;
    private final String defaultValue;
    private final List<String> enumeration;
    private final BigInteger minLength;
    private final BigInteger maxLength;
    private final String prefix;
    private final String suffix;
    private final String validationPattern;

    public MyTypeDefinition(String name, MyType myType, Type type, String units,
            BigInteger precision, BigDecimal lowerLimit, BigDecimal upperLimit, String defaultValue,
            List<String> enumeration, BigInteger minLength, BigInteger maxLength, String prefix,
            String suffix, String validationPattern) {
        this.name = name;
        this.myType = myType;
        this.type = type;
        this.units = units;
        this.precision = precision;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.defaultValue = defaultValue;
        this.enumeration = enumeration;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.prefix = prefix;
        this.suffix = suffix;
        this.validationPattern = validationPattern;
    }

    public String getName() {
        return name;
    }

    public MyType getMyType() {
        return myType;
    }

    public Type getType() {
        return type;
    }

    public String getUnits() {
        return units;
    }

    public BigInteger getPrecision() {
        return precision;
    }

    public BigDecimal getLowerLimit() {
        return lowerLimit;
    }

    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public List<String> getEnumeration() {
        return enumeration;
    }

    public BigInteger getMinLength() {
        return minLength;
    }

    public BigInteger getMaxLength() {
        return maxLength;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getValidationPattern() {
        return validationPattern;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MyTypeDefinition [name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", units=");
        builder.append(units);
        builder.append(", precision=");
        builder.append(precision);
        builder.append(", lowerLimit=");
        builder.append(lowerLimit);
        builder.append(", upperLimit=");
        builder.append(upperLimit);
        builder.append(", defaultValue=");
        builder.append(defaultValue);
        builder.append(", enumeration=");
        builder.append(enumeration);
        builder.append(", minLength=");
        builder.append(minLength);
        builder.append(", maxLength=");
        builder.append(maxLength);
        builder.append(", prefix=");
        builder.append(prefix);
        builder.append(", suffix=");
        builder.append(suffix);
        builder.append(", validationPattern=");
        builder.append(validationPattern);
        builder.append("]");
        return builder.toString();
    }

}