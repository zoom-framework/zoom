package com.jzoom.zoom.web.exception;

public interface RestException {
	int getStatus();
	String getCode();
	String getError();
}
