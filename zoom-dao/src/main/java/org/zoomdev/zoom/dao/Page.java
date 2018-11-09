package org.zoomdev.zoom.dao;

import java.util.List;

public class Page<T> {

	private List<T> list;
	private int pageSize;
	private int page;
	private int total;
	
	public Page() {
	
	}
	
	public Page( List<T> list,int page,int pageSize,int total ) {
		this();
		this.list = list;
		this.page = page;
		this.pageSize = pageSize;
		this.total = total;
	}
	
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	@Override
	public String toString() {
		return String.format("Page(page:%d,total:%d,size:%d,list:%d)", page,total,pageSize,list.size());	
	}
	
	
	
	
	
	
}
