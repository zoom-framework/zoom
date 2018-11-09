package org.zoomdev.zoom.dao.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class Transactions {


	
	private int level;
	
	private List<Transaction> transactions = new ArrayList<Transaction>();
	
	public Connection getConnection(DataSource dataSource) throws SQLException {
		final List<Transaction> transactions = this.transactions;
		
		for (Transaction transaction : transactions) {
			if(transaction.dataSource == dataSource) {
				return transaction.getConnection();
			}
		}
		Transaction transaction = new Transaction(level, dataSource );
		transactions.add(transaction);
		return transaction.getConnection();
	}
	
	public Transactions(int level) {
		this.level = level;
	}

	public void commit() {
		for (Transaction transaction : transactions) {
			try {
				transaction.commit();
			} catch (Throwable e) {
				//失败
				
			}
		
		}
		clean();
	}

	private void clean() {
		for (Transaction transaction : transactions) {
			transaction.clean();
		}
		transactions.clear();
	}

	public void rollback() {
		for (Transaction transaction : transactions) {
			try {
				transaction.rollback();
			}catch (Exception e) {
				
			}
			
		}
		clean();
	}
	
}
