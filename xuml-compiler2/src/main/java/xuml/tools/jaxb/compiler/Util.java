package xuml.tools.jaxb.compiler;

import miuml.jaxb.Perspective;

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
			if ((i != 0 && !Character.isJavaIdentifierStart(ch))
					|| !Character.isJavaIdentifierPart(ch)) {
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

	public static String camelCaseToLowerUnderscore(String s) {
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

}