package mefoo.util;
public interface Spliter {
	String[] split(String document);
	default Object[] split(Object document) {
		return (Object[])split(document.toString());
	}
}
