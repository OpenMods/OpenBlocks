package openperipheral.api;

import java.util.Map;

public enum LuaType {

	TABLE(Map.class, "table"),
	NUMBER(Double.class, "number"),
	STRING(String.class, "string"),
	VOID(void.class, "void"),
	BOOLEAN(Boolean.class, "boolean"),
	OBJECT(Object.class, "object");

	private Class<?> javaType;
	private String name;

	LuaType(Class<?> javaType, String name) {
		this.javaType = javaType;
		this.name = name;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public String getName() {
		return name;
	}
}
