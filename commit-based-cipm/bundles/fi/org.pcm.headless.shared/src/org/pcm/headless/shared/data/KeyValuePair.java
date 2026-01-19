package org.pcm.headless.shared.data;

public class KeyValuePair<A, B> {

	private A key;
	private B value;

	public A getKey() {
		return key;
	}

	public void setKey(A key) {
		this.key = key;
	}

	public B getValue() {
		return value;
	}

	public void setValue(B value) {
		this.value = value;
	}

	public KeyValuePair() {
	}

	public KeyValuePair(A a, B b) {
		this.key = a;
		this.value = b;
	}

}
