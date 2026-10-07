package com.sales.controller;

import com.sales.dao.CustomerDAO;
import com.sales.exception.DuplicateRecordException;
import com.sales.model.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {

    private final CustomerDAO dao = new CustomerDAO();

    public List<Customer> getAll() throws SQLException {
        return dao.getAllCustomers();
    }

    public Customer getById(int id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Customer c) throws SQLException, DuplicateRecordException {
        dao.addCustomer(c);
    }

    public void update(Customer c) throws SQLException {
        dao.updateCustomer(c);
    }

    public void delete(int id) throws SQLException {
        dao.deleteCustomer(id);
    }

    public List<Customer> search(String keyword) throws SQLException {
        return dao.searchCustomers(keyword);
    }
}
