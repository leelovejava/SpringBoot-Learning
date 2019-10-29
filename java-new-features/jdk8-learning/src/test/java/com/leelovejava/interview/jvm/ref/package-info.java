/**
 * 引用
 * 强引用: 死都不会收
 * 软引用: 内存不足时回收,充足时不会被回收(softReference)
 * 弱引用: 不管内存是否足够,gc一律回收(WeakReference)
 * 虚引用: 如果一个对象仅持有虚引用,那么它就和没有任何引用一样,在任何时候都可能被垃圾回收(PhantomReference)
 */
package com.leelovejava.interview.jvm.ref;
