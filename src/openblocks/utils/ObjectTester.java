package openblocks.utils;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ObjectTester<T> implements ITester<T> {
	public static class ClassTester<T> implements ITester<T> {

		private final Class<? extends T> cls;
		private final Result onMatch;

		public ClassTester(Class<? extends T> cls, Result onMatch) {
			this.cls = cls;
			this.onMatch = onMatch;
		}

		public ClassTester(Class<? extends T> cls) {
			this(cls, Result.ACCEPT);
		}

		@Override
		public Result test(T o) {
			return cls.isAssignableFrom(o.getClass())? onMatch : Result.CONTINUE;
		}
	}

	public static <T> ITester<T> createClassTester(Class<? extends T> cls) {
		return new ClassTester<T>(cls);
	}

	public static class ClassNameTester<T> implements ITester<T> {
		private final Set<String> names = Sets.newHashSet();
		private final Result onMatch;

		public ClassNameTester(Result onMatch) {
			this.onMatch = onMatch;
		}

		public ClassNameTester() {
			this(Result.ACCEPT);
		}

		public ClassNameTester<T> addClasses(String... names) {
			for (String name : names)
				this.names.add(name);
			return this;
		}

		public ClassNameTester<T> addClasses(Class<? extends T>... classes) {
			for (Class<? extends T> cls : classes)
				names.add(cls.getName());
			return this;
		}

		@Override
		public Result test(T o) {
			return names.contains(o.getClass().getName())? onMatch : Result.CONTINUE;
		}
	}

	public static <T> ObjectTester<T> create() {
		return new ObjectTester<T>();
	}

	private List<ITester<T>> testers = Lists.newArrayList();

	public ObjectTester<T> addTester(ITester<T> tester) {
		testers.add(tester);
		return this;
	}

	@Override
	public Result test(T o) {
		for (ITester<T> tester : testers) {
			Result r = tester.test(o);
			if (r != Result.CONTINUE) return r;
		}

		return Result.CONTINUE;
	}

	public boolean check(T o) {
		return test(o) == Result.ACCEPT;
	}
}
