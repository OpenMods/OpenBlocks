package openperipheral.api.adapter.method;

public enum ArgType {

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
