package org.zoomdev.zoom.dao.builder;

public interface TableBuilder {
	
	
	TableBuilder addColumn(String column);

	TableBuilder setName(String name);
	
	TableBuilder setPrimary();
	
	TableBuilder setAutoIncrease();
	
	TableBuilder setType();
	
	TableBuilder setLength(int length);

	TableBuilder selectColumn(String name);
	
	TableBuilder setIndex();
	
	TableBuilder setUnique();
	
	
}
