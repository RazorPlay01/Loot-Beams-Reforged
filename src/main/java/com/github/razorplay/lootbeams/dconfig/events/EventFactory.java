package com.github.razorplay.lootbeams.dconfig.events;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EventFactory {
   public static <T> Event<T> createArrayBacked(Class<? super T> type, EventInvokerFactory<T> invokerFactory) {
      return new Event<>(type, invokerFactory);
   }

   public static class Event<T> {
      private final List<T> listeners = new ArrayList<>();
      private final EventInvokerFactory<T> invokerFactory;
      private final Class<?> listenerClass;
      private T invoker;

      public Event(Class<? super T> listenerClass, EventInvokerFactory<T> invokerFactory) {
         this.listenerClass = listenerClass;
         this.invokerFactory = invokerFactory;
         this.updateInvoker();
      }

      public T invoker() {
         return this.invoker;
      }

      public void register(T listener) {
         this.listeners.add(listener);
         this.updateInvoker();
      }

      private void updateInvoker() {
         T[] listenerArray = (T[])((Object[])Array.newInstance(this.listenerClass, this.listeners.size()));
         this.invoker = this.invokerFactory.create((T[])this.listeners.toArray(listenerArray));
      }
   }

   @FunctionalInterface
   public interface EventInvokerFactory<T> {
      T create(T[] var1);
   }
}
