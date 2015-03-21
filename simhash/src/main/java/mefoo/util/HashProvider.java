package mefoo.util;

public interface HashProvider {
	long hash(String obj);
	default long hash(Object obj) {
		return hash(obj.toString());
	}
}
