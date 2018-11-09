package com.jzoom.zoom.dao;

import java.sql.SQLException;

public class DaoException extends RuntimeException {

	public DaoException(Throwable e) {
		super(e);
	}
	
	public DaoException(String message,Throwable e) {
		super(message,e);
	}
	
	public DaoException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1008765440134574580L;

}
