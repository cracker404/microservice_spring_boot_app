package com.async.model;

import java.io.Serializable;

public class JmsDto<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Object id;
	private T object;
	private OperationType operation;
	private Class<T> clazz;
	private String index;

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public OperationType getOperation() {
		return operation;
	}

	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "JmsDto [object=" + object + ", operation=" + operation + "]";
	}

}
