package org.example;

import lombok.Data;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankTreadLocks {

    static Random rnd = new Random();
    static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {

        final AccountLock[] accounts = new AccountLock[] {
                new AccountLock(100), new AccountLock(0),
                new AccountLock(100), new AccountLock(0),
                new AccountLock(100), new AccountLock(0),
                new AccountLock(100), new AccountLock(0),
                new AccountLock(100), new AccountLock(0),
        };

        for (int k = 0; k < 4 * Runtime.getRuntime().availableProcessors(); k++) {
            executorService.submit(() -> {
                while(true) {
                    int from = rnd.nextInt(accounts.length);
                    int to = rnd.nextInt(accounts.length);
                    if (from != to) {
                        int amount = rnd.nextInt(50);
                        transfer(accounts[from], accounts[to], amount);
                    }
                }
            });
        }

        Thread.sleep(1000);
        System.out.println(sum(accounts));
        System.out.println(toStr(accounts));
    }

    // OLTP (Online Transaction Processing)
    public static void transfer(final AccountLock from, final AccountLock to, final int amount) {
        AccountLock firstAccount = (from.id < to.id) ? from : to;
        AccountLock secondAccount = (from.id >= to.id) ? from : to;
        firstAccount.lock.lock();
        try {
            secondAccount.lock.lock();
            try {
                if (from.incrementBalance(-amount)) {
                    if (!to.incrementBalance(+amount)) {
                        from.incrementBalance(+amount); // Rollback
                    }
                }
            } finally {
                secondAccount.lock.unlock();
            }
        } finally {
            firstAccount.lock.unlock();
        }
    }

    // OLAP (Online Analytical Processing)
    public static int sum(final AccountLock[] accounts) throws Exception {
        final AccountLock[] tmp = accounts.clone();
        Arrays.sort(tmp, (acc00, acc01) -> acc00.id - acc01.id);
        return lockRecursively(tmp, () -> {
            int result = 0;
            for (AccountLock acc : tmp) {
                result += acc.getBalance();
            }
            return result;
        });
    }

    //OLTP vs OLAP
    public static String toStr(final AccountLock[] accounts) throws Exception {
        final AccountLock[] tmp = accounts.clone();
        Arrays.sort(tmp, (acc00, acc01) -> acc00.id - acc01.id);
//        Arrays.sort(tmp, new Comparator<AccountLock>() {
//            @Override
//            public int compare(AccountLock o1, AccountLock o2) {
//                return o1.id - o2.id;
//            }
        return Arrays.toString(tmp);
    }


    private static <T> T lockRecursively(AccountLock[] accounts, Callable<T> c) throws Exception {
        if (accounts.length > 0) {
            accounts[0].lock.lock();
            try {
                return lockRecursively(Arrays.copyOfRange(accounts, 1, accounts.length), c);
            } finally {
                accounts[0].lock.unlock();
            }
        } else {
            return c.call();
        }
    }
}

@Data
class AccountLock {
    private final static AtomicInteger idGenerator = new AtomicInteger(0);
    public final int id = idGenerator.getAndIncrement();
    public final Lock lock = new ReentrantLock();
    private int balance;

    public AccountLock(int balance) {
        this.balance = balance;
    }

    public boolean incrementBalance(int amount) {
        if (balance + amount >= 0) {
            balance += amount;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Acc{" + balance + "}";
    }
}
