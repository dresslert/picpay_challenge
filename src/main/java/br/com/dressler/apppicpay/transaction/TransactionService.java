package br.com.dressler.apppicpay.transaction;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.dressler.apppicpay.wallet.Wallet;
import br.com.dressler.apppicpay.wallet.WalletRepository;
import br.com.dressler.apppicpay.wallet.WalletType;
import br.com.dressler.apppicpay.authorization.AuthorizerService;
import br.com.dressler.apppicpay.notification.NotificationService;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository, 
        WalletRepository walletRepository, AuthorizerService authorizerService, 
        NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Transaction create(Transaction transaction){
        // 1 - validação
        validate(transaction);

        // 2 - criar transação
        var newTransaction = transactionRepository.save(transaction);

        // 3 - debitar da carteira
        var wallet = walletRepository.findById(transaction.payer()).get();
        walletRepository.save(wallet.debit(transaction.value()));

        // 4 - chamar serviços externos
        authorizerService.authorize(transaction);

        // 5 - notificação
        notificationService.notify(transaction);

        return newTransaction;
    }

    private void validate(Transaction transaction) {
        walletRepository.findById(transaction.payee())
        .map(payee -> walletRepository.findById(transaction.payer())
            .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
            .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction))))
        .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction)));
                
    }

    private boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() &&
            payer.balance().compareTo(transaction.value()) >= 0 &&
            !payer.id().equals(transaction.payee());
    }

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }

}
