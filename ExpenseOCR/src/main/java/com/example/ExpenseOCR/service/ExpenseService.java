package com.example.ExpenseOCR.service;

import com.example.ExpenseOCR.model.Expense;
import com.example.ExpenseOCR.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByUser(String userId) {
        return expenseRepository.findByUserId(userId);
    }

    public Expense updateExpense(String id, Expense updated) {
        Expense existing = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setDate(updated.getDate());
        existing.setDescription(updated.getDescription());
        return expenseRepository.save(existing);
    }

    public void deleteExpense(String id) {
        expenseRepository.deleteById(id);
    }
}
