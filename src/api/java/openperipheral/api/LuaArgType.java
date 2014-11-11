package openperipheral.api;

public enum LuaArgType {

	TABLE,
	NUMBER,
	STRING,
	VOID,
	BOOLEAN,
	OBJECT,
	AUTO {
		@Override
		public String getName() {
			return "<invalid>";
		}
	};

	public String getName() {
		return name().toLowerCase();
	}
}
