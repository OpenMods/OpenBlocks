package openperipheral.api.adapter.method;

import java.util.Map;

public enum ReturnType {
	TABLE(Map.class),
	NUMBER(Double.class),
	STRING(String.class),
	VOID(void.class),
	BOOLEAN(Boolean.class),
	OBJECT(Object.class);

	private Class<?> javaType;

	ReturnType(Class<?> javaType) {
		this.javaType = javaType;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public String getName() {
		return name().toLowerCase();
	}
}
