package rpLidar.clustering;

import java.util.ArrayList;
import java.util.List;

public class FixedArrayContainer<T> {

	Object elements[];
	int currentIndex;

	public FixedArrayContainer(int maxSize) {
		elements = new Object[maxSize];
		this.currentIndex = 0;
	}
	
	public void add(T elt) {
		this.elements[currentIndex++] = elt;
	}
	
	public boolean canAdd() {
		return this.currentIndex < elements.length;
	}
	
	public boolean isEmpty() {
		return this.currentIndex == 0;
	}
	
	@SuppressWarnings("unchecked")
	public T getLast() {
		return (T) this.elements[currentIndex-1];
	}

	public int count() {
		return this.currentIndex;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int i) {
		return (T) this.elements[i];
	}
	
	@SuppressWarnings("unchecked")
	public List<T> asList() {
		List<Object> list = new ArrayList<Object>();
		for (Object elt : elements) {
			if (elt != null) {
				list.add(elt);	
			}
		}
		
		return (List<T>) list;
	}
	
}
