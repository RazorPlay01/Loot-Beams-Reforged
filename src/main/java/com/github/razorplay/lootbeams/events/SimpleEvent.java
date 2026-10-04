package com.github.razorplay.lootbeams.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class SimpleEvent<T> {
	private final List<T> listeners = new ArrayList<>();
	private final T invoker;

	public SimpleEvent(Function<List<T>, T> invokerFactory) {
		this.invoker = invokerFactory.apply(this.listeners);
	}

	public void register(T listener) {
		listeners.add(listener);
	}

	public T invoker() {
		return invoker;
	}
}
