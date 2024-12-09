
public class SQNode {
	private Object element;
	private SQNode next;

	public SQNode(Object element) {
		this(element, null);
	}

	public SQNode(Object element, SQNode next) {
		this.element = element;
		this.next = next;

	}

	public Object getElement() {
		return element;
	}

	public void setElement(Object element) {
		this.element = element;
	}

	public SQNode getNext() {
		return next;
	}

	public void setNext(SQNode next) {
		this.next = next;
	}

}
