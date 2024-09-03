package br.com.dressler.apppicpay.transaction;

import org.springframework.data.repository.ListCrudRepository;

public interface TransactionRepository extends ListCrudRepository< Transaction, Long>{
    
}
