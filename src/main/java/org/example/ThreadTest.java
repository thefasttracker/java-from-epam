package org.example;

import lombok.NonNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ThreadTest {
    public static void main(String[] args) {

        int end = 1_000_000;
        long resultParallel;
        long sec = System.currentTimeMillis();
        List<Integer> listInt = Arrays.asList(1, 10, 100, 1_000, 10_000, 100_000, 1_000_000);
        List<Integer> list = IntStream.range(0, 1_000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> numbers = LongStream.range(0, end)
                .boxed()
                .collect(Collectors.toList());
        List<Double> numbersDouble = IntStream.range(1, 1_000_000)
                .asDoubleStream()
                .boxed()
                .parallel()
                .peek(n -> System.out.printf("%5.2f; ", n))
                .collect(Collectors.toList());

        // new thread object
        WalkThread walk = new WalkThread();
        walk.setName("walk: ");
        walk.start(); // start of thread

        // new Runnable thread object
        Thread talk = new Thread(new TalkThread());
        talk.setName("talk: ");
        talk.start(); // start of thread
        // TalkThread t = new TalkThread(); just an object, not a thread
        // t.run(); or talk.run();
        // method will execute, but thread will not start!

        // Executor with Callable thread object
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new ActionCallable(list));
        Future<?> future1 = executor.submit(new TalkThread()); // Runnable
        Future<Integer> future3 = executor.submit(new I3());
        executor.shutdown(); // stops service but not thread
        // executor.submit(new Thread()); /* attempt to start will throw an exception */
        // executor.shutdownNow(); // stops service and all running threads
        try {
            System.out.println(future.get());
            System.out.println(future3.get());
            System.out.println(future1.get()); //null, cos it's Runnable
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Future<Integer> inte = Executors.newSingleThreadExecutor().submit(new ActionCallable(list));

        // ForkJoin with RecursiveTask (~ Callable) thread object
        ForkJoinTask<Long> task = new SumRecursiveTask(numbers);
        long result = new ForkJoinPool().invoke(task);
        System.out.println(result);

        //ForkJoin with RecursiveAction (~ Runnable)  thread object
        new UnaryAction<>(numbersDouble, Math::sqrt).invoke();
        numbersDouble.forEach(r -> System.out.printf("%7.4f %n ", r));

        //ParallelStream stream API (uses ForkJoin)
        resultParallel = LongStream.range(0, 1_000_000_000)
                .boxed()
                .parallel()
                .map(x -> x / 7)
                .peek(v -> System.out.println(Thread.currentThread().getName()))
                .reduce((x,y)-> x + (int) (3 * Math.sin(y)))
                .get();
        System.out.println(resultParallel);

        // Пользовательский пул потоков, созданный на основе Runnable или Callable,
        // непосредственно сделать выполняемым parallel() не существует возможности,
        // однако его можно обернуть в ForkJoinPool.
        Callable<Integer> taskCallable = () -> IntStream.range(0, 1_000_000_000)
                .boxed()
                .parallel()
                .map(x -> x / 3)
                .peek(th -> System.out.println(Thread.currentThread().getName()))
                .reduce((x, y) -> x + (int)(3 * Math.sin(y)))
                .get();
        ForkJoinPool pool = new ForkJoinPool(8);// 8 processors
        try {
            int resultCallableInParallelStream = pool.submit(taskCallable).get();
            System.out.println(resultCallableInParallelStream);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println((System.currentTimeMillis() - sec) / 1000.);

        //Timer (TimerTask, implements Runnable)
        Timer timer = new Timer();
        timer.schedule(new TimerCounter(), 100, 3000);

        //Thread Priority
        Thread walkMin = new Thread(new WalkThread(), "Min");
        Thread talkMax = new Thread(new TalkThread(), "Max");
        walkMin.setPriority(Thread.MIN_PRIORITY);
        talkMax.setPriority(Thread.MAX_PRIORITY);
        talkMax.start();
        walkMin.start();

        //Join Thread
        new Thread(() -> {
            System.out.println("start 1");
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("end 1");
        }).start();
        JoinThread thread = new JoinThread();
        thread.start();
        try {
            thread.join(100); // or join(100)
            // or //TimeUnit.MILLISECONDS.timedJoin(thread0, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end of " + Thread.currentThread().getName());
        //start 1, Start, End, end of main, end 1

        //Thread.yield
        new Thread(() -> {
            System.out.println("start 1");
            Thread.yield();
            Thread.yield();
            System.out.println("end 1");
        }).start();
        new Thread(() -> {
            System.out.println("start 2");
            System.out.println("end 2");
        }).start();
        //start 1, start 2, end 2, end 1

        //Daemon Thread
        SimpleThread normal = new SimpleThread();
        SimpleThread daemon = new SimpleThread();
        daemon.setDaemon(true);
        daemon.start();
        normal.start();
        System.out.println("end of main");
    }
}

class WalkThread extends Thread { //1
    public void run() {
        try {
            for (int i = 0; i < 70; i++) {
                System.out.println("Walk " + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace(); }
            }
        } finally {
            System.out.println(Thread.currentThread().getName());
        }
    }
}

class TalkThread implements Runnable{ //2
    @Override
    public void run() {
        try { // - зачем лишний try / catch?
            for (int i = 0; i < 70; i++) {
                System.out.println("Talk -->" + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            System.out.println(Thread.currentThread().getName());
        }
    }
}

class ActionCallable implements Callable<Integer> {
    private List<Integer> integers;
    public ActionCallable(List<Integer> integers) {
        this.integers = integers;
    }
    @Override
    public Integer call() {
        int sum = 0;
        for (int number : integers) {
            sum += number;
        }
        return sum;
    }
}

class I3 implements Callable<Integer> {
    @Override
    public Integer call() {
        return 5;
    }

    public static void main(String[] args) {
        List<Integer> phones = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,0,null));
        phones.sort(Comparator.comparing(o -> o == null ? 0 : -o));
        System.out.println(phones);
        phones.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(a -> a))
                .parallel()
                .unordered()
                .forEach(System.out::println);

    }
}

class SumRecursiveTask extends RecursiveTask<Long> {
    private List<Long> longList;
    private int begin;
    private int end;
    public static final long THRESHOLD = 10_000;
    public SumRecursiveTask(List<Long> longList) {
        this(longList, 0, longList.size());
    }
    private SumRecursiveTask(List<Long> longList, int begin, int end) {
        this.longList = longList;
        this.begin = begin;
        this.end = end;
    }
    @Override
    protected Long compute() {
        int length = end - begin;
        long result = 0;
        if (length <= THRESHOLD) {
            for (int i = begin; i < end; i++) {
                result += longList.get(i);
            }
        } else {
            int middle = begin + length >>> 1; // divide by 2
            SumRecursiveTask taskLeft = new SumRecursiveTask(longList, begin, middle);
            taskLeft.fork(); // run async
            SumRecursiveTask taskRight = new SumRecursiveTask(longList, middle, end);
            taskRight.fork(); //or compute()
            Long leftSum = taskLeft.join();
            Long rightSum = taskRight.join();
            result = leftSum + rightSum;
        }
        return result;
    }

    // same as compute(), but with stream API and lambda
    protected Long compute1() {
        int length = end - begin;
        long result = 0;
        if (length <= THRESHOLD) {
            for (int i = begin; i < end; i++) {
                result += longList.get(i);
            }
        } else {
            int middle = begin + length / 2;
            List<SumRecursiveTask> tasks = new ArrayList<>();
            tasks.add(new SumRecursiveTask(longList, begin, middle));
            tasks.add(new SumRecursiveTask(longList, middle, end));
            tasks.stream().forEach(RecursiveTask::fork);
            result = tasks.stream()
                    .map(RecursiveTask::join)
                    .reduce(Long::sum)
                    .orElse(0L);
        }
        return result;
    }
}

class UnaryAction<T> extends RecursiveAction {
    private List<T> subjectList;
    private UnaryOperator<T> operator;
    private int begin;
    private int end;
    private static final int THRESHOLD = 100000;
    public UnaryAction(List<T> subjectList, UnaryOperator<T> operator, int begin, int end) {
        this.operator = operator;
        this.subjectList = subjectList;
        this.begin = begin;
        this.end = end;
    }
    public UnaryAction(List<T> subjectList, UnaryOperator<T> operator) {
        this(subjectList, operator, 0, subjectList.size());
    }
    @Override
    protected void compute() {
        if (end - begin < THRESHOLD) {
            System.out.printf("from %d, to %d - thread %s%n",
                    begin, end, Thread.currentThread().getName());
            for (int i = begin; i < end; i++) {
                subjectList.set(i, operator.apply(subjectList.get(i)));
            }
        } else {
            int oneThird = (begin + end) / 3;
            int middle = (begin + end) / 2;
            invokeAll(
                    new UnaryAction<T>(subjectList, operator, begin, middle),
                    new UnaryAction<T>(subjectList, operator, middle, end)
            );
        }
    }
}

class TimerCounter extends TimerTask {
    private static int i;
    @Override
    public void run() {
        System.out.print(++i);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\t" + i);
    }
}

class JoinThread extends Thread {
    public void run() {
        System.out.println("Start");
    try {
        TimeUnit.MILLISECONDS.sleep(10);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
        System.out.println("End");
    }
}

class SimpleThread extends Thread {
    public void run() {
        try {
            if (isDaemon()) {
                System.out.println("start of daemon thread");
                TimeUnit.MILLISECONDS.sleep(10);
            } else {
                System.out.println("start of normal thread");
            }
        } catch (InterruptedException e) {
            System.err.print(e);
        } finally {
            if (!isDaemon()) {
                System.out.println("normal thread completion");
            } else {
                System.out.println("daemon thread completion");
            }
        }
    }
}

//Exeption в запущенном из main потоке не влияет на main поток
class ThreadExceptionMain {
    public static void main(String[] args) {
        new Thread(()-> {
        if(Boolean.TRUE) {
            throw new RuntimeException();
        }
        System.out.println("end of Thread");
        }).start();
        try {
            TimeUnit.MILLISECONDS.sleep(20); }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end of main thread");
    }
}

//Прекращение main потока не влияет на запущенные из него потоки
class ThreadExceptionMain2 {
    public static void main(String[] args) {
        new Thread(() -> {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end of Thread");
        }).start();
        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(Boolean.TRUE) {
            throw new RuntimeException();
        }
        System.out.println("end of main thread");
    }
}

/* --- Atomic --- */
class Market extends Thread {
    private AtomicLong index;
    private final Random generator = new Random();
    public Market(AtomicLong index) {
        System.out.println("Market!");
        this.index = index;
    }
    public AtomicLong getIndex() {
        return index;
    }
    @Override
    public void run() {
        try {
            while (true) {
                index.addAndGet(generator.nextInt(21) - 10);
                Thread.sleep(generator.nextInt(500));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Broker extends Thread {
    private static Market market;
    private static final int PAUSE_IN_MILLIS = 500;
    public static void initMarket(Market market) {
        Broker.market = market;
    }
    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + " Current index: " + market.getIndex());
                TimeUnit.MILLISECONDS.sleep(PAUSE_IN_MILLIS);
            }
        } catch (InterruptedException e) { e.printStackTrace();
        }
    }
}

class AtomicMain {
    private static final int NUMBER_BROKERS = 30;
    public static void main(String[] args) {
        Market market = new Market(new AtomicLong(100));
        Broker.initMarket(market);
        market.start();
        for (int i = 0; i < NUMBER_BROKERS; i++) {
            new Broker().start();
        }
    }
}
/* --- End Atomic --- */

/* -- Synchronized Запись из разных потоков в общий файл -- */
class CommonResource implements AutoCloseable {
    private FileWriter fileWriter;
    public CommonResource(String file) throws IOException {
        fileWriter = new FileWriter(file, false);
    }
    public synchronized void writing(String info, int i) {
        try {
            fileWriter.append(info + i);
            System.out.print(info + i);
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500));
            fileWriter.append("->" + info.charAt(0) + i + " ");
            System.out.print("->" + info.charAt(0) + i + " ");
        } catch (IOException | InterruptedException e) {
            System.err.print(e);
        }
    }
    @Override
    public void close() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}

class UseFileThread extends Thread {
    private CommonResource resource;
    public UseFileThread(String name, CommonResource resource) {
        super(name);
        this.resource = resource;
    }
    public void run() {
        for (int i = 0; i < 5; i++) {
            resource.writing(this.getName(), i); // synchronized method call
         }
    }
}

class SynchroMain {
    public static void main(String[] args) {
        try(CommonResource resource = new CommonResource("data/thread.txt")) {
            UseFileThread thread1 = new UseFileThread("First", resource);
            UseFileThread thread2 = new UseFileThread("Second", resource);
            thread1.start();
            thread2.start();
            TimeUnit.SECONDS.sleep(5);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
/* -- End Запись из разных потоков в общий файл -- */

/* -- Synchronized block of code -- */
class SynchroBlockMain {
    static int counter;
    public static void main(String[] args) {
        StringBuilder info = new StringBuilder();
        StringBuilder test = new StringBuilder();
        new Thread(() -> {
            synchronized (info) {
                do {
                    info.append('A');
                    test.append('1');
                    System.out.println(info);
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (counter++ < 2);
            }
        }).start();
        new Thread(() -> {
            synchronized (info) {
                while(counter++ < 6) {
                    info.append('Z');
                    test.append('2');
                    System.out.println(info);
                }
            }
        }).start();
    }
}
/* -- End Synchronized block of code -- */

/* --- wait() / notify() --- */
class Payment {
    private int amount;
    public synchronized void doPayment() {
        try {
            System.out.println(Thread.currentThread().getName() + ": Start payment");
            if (amount <= 0) {
                this.wait();
            }
            System.out.println(Thread.currentThread().getName() + ": Did payment " + amount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": Payment is closed");
    }
    public synchronized void init() {
        System.out.println(Thread.currentThread().getName() + ": Init amount:");
        amount = new Scanner(System.in).nextInt();
        this.notifyAll();
    }
}

class PaymentMain {
    public static void main(String[] args) {
        Payment payment = new Payment();
        for (int i = 0; i < 5; i++) {
            new Thread(payment::doPayment).start();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        payment.init();
    }
}
/* --- End wait() / notify()--- */


/* --- стандартное решение Producer\Consumer --- */
class ProducerConsumer {
    private boolean ready;
    public synchronized void consume() {
        while(!ready) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ready = false;
    }
    public synchronized void produce() {
        ready = true;
        notify();
    }
}

class ProducerConsumerMain {
    public static void main(String[] args) {
        ProducerConsumer producerConsumer = new ProducerConsumer();
        new Thread(producerConsumer::consume);
        producerConsumer.produce();
    }
}
/* --- End стандартное решение Producer\Consumer --- */

class BlockingQueueMain {
    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                    queue.put("Java " + i);
                    System.out.println("Element " + i + " added");
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("Element " + queue.take() + " took");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

class CopyOnWriteMain {
    public static void main(String[] args) {
        List<Integer> temp = new Random()
            .ints(5, 0, 10)
            .boxed()
            .collect(Collectors.toList());
        List<Integer> newList = new ArrayList<>();
        CopyOnWriteArrayList<Integer> copyList = new CopyOnWriteArrayList<>(temp);
        System.out.printf("%17s: %s%n ", "copyList before", temp);
        // ArrayList<Integer> list = new ArrayList<>(temp);
        new Thread(() -> { // thread # 1
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                    e.printStackTrace();
            }
            Iterator<Integer> iterator = copyList.iterator();
            while (iterator.hasNext()) {
                Integer current = iterator.next();
                newList.add(current);
            }
            System.out.printf("%16s: %s%n ", "newList Th #1", newList);
        }).start();
        new Thread(() -> { // thread # 2
            for (int i = 0; i < 10; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);// change to 100
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                copyList.addIfAbsent(i);
            }
        }).start();
        try {
            TimeUnit.SECONDS.timedJoin(Thread.currentThread(), 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%16s: %s%n ", "copyList Th #2", copyList);
        System.out.printf("%16s: %s%n ", "newList Th #1", newList);
    }
}

class WaitTMain {
    public static void main(String [] args) {
        System.out.print("0");
        new Thread(() -> {
            synchronized (args) {
                try {
                    TimeUnit.SECONDS.sleep(2);// change to 100
                    args.notify();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        synchronized(args){
            System.out.print("1");
            try {
                args.wait();
            }
            catch(InterruptedException e) {

            }
        }
        System.out.print("2");
        AtomicInteger i = new AtomicInteger(0);
        Runnable runnable = () -> System.out.print(i.incrementAndGet());
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();

        Runnable runnable1 = () -> System.out.print("R");
        Callable<String> callable = () -> "C"; // line 1
        ExecutorService service = Executors.newSingleThreadExecutor();
         service.submit(runnable1); // line 2
        Future<String> future = service.submit(callable);

        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }
}

class rerew {
    public static void main(String[] args) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> inte1 = es.submit(new ActionCallable(
                IntStream.range(1, 100).boxed().collect(Collectors.toList())));
        Future<Integer> inte2 = es.submit(() -> 5);
        es.shutdown();
        try {
            System.out.println(inte1.get());
            System.out.println(inte2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Runnable tt = () -> {
            for (int i = 0; i < 70; i++) {
                System.out.println("Talk -->" + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread talk2 = new Thread(tt);
        talk2.start();
    }
}

class Appe {
    public static volatile int value = 0;
    public static AtomicInteger atomic = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                value++;
                atomic.incrementAndGet();
            }
        };
        for (int i = 0; i < 3; i++) {
            new Thread(task).start();
        }
        Thread.sleep(300);
        System.out.println(value);
        System.out.println(atomic.get());

        Callable<String> callable = () -> Thread.currentThread().getName();
        ExecutorService service = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 5; i++) {
            Future result = service.submit(callable);
            System.out.println(result.get());
        }
        service.shutdown();
    }
}

class App00 {
    static void f() {
        System.out.println("f");
    }
    public static void main(String ...args) {
        final Object monitor = new Object();
        App00 app = null;
        app.f();
        new Thread(() -> {
            synchronized (monitor) {
                System.out.println(Thread.holdsLock(monitor));
                while (true) {
                    System.out.print("A");
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(() -> {
            System.out.println(Thread.holdsLock(monitor));
            synchronized (monitor) {
                while (true) {
                    System.out.print("B");
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

//synchronised or volatile do happens-before, but volatile - only atomic operations
class App01 {
    static /*volatile*/ int counter;
    static volatile boolean finish1;
    static volatile boolean finish2;

    private static synchronized void inc() {
        counter++;
    }

    public static void main(String... args) throws InterruptedException {
        new Thread(() -> {
            for (int i=0;i<10_000_000;i++) {
                inc();
            }
            finish1 = true;
        }).start();
        new Thread(() -> {
            for (int i=0;i<10_000_000;i++) {
                inc();
            }
            finish2 = true;
        }).start();
        while(!(finish1 && finish2));
        System.out.println(counter);
        Collections.emptyList();
    }

}

// Producer / Consumer Thread
class ThreadQueue {
    public static void main(String[] args) {
        final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(16);

        //Producer
        new Thread(() -> {
            int counter = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    queue.put(++counter);
                    System.out.println("put: " + counter);
                } catch (InterruptedException ignore) { /*NOP*/; }
            }
        }).start();

        //Consumer
        new Thread(() -> {
            while (true) {
                try {
                    int data = queue.take();
                    System.out.println("take: " + data);
                } catch (InterruptedException ignore) { /*NOP*/; }
            }
        }).start();
    }
}

//Single element blocking queue
class SingleElementBlockingQueue{
    private Integer el = null;
    public synchronized void put(int newEl) throws InterruptedException {
        while (el != null) {
            wait();
        }
        el = newEl;
        notifyAll();
    }
    public synchronized int get() throws InterruptedException {
        while (el == null) {
            wait();
        }
        Integer result = el;
        el = null;
        notifyAll();
        return result;
    }
}


//kill Tread if other Thread died with UncaughtExceptionHandler
class P {
    public static void main(String[] args) {
        final ThreadGroup group = new ThreadGroup("stub");
        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                group.interrupt();
            }
        };
        Thread p0 = new Thread(group, () -> {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                System.out.println("p0 Killed");
            }
        });
        p0.setUncaughtExceptionHandler(exceptionHandler);
        p0.start();

        Thread p1 = new Thread(group, () -> {
            System.out.println("p1 dead");
            throw new Error();
        });
        p1.setUncaughtExceptionHandler(exceptionHandler);
        p1.start();
    }
}

//1. fairness 2. Lock Unlock независимы
class SingleElemBuffer {
    private final Lock lock = new ReentrantLock(true); //fairness
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();
    private Integer el = null;

    public void put(int newEl) throws InterruptedException {
        lock.lock();
        try {
            while (el != null) { notFull.await(); }
            el = newEl;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    public int get() throws InterruptedException {
        lock.lock();
        try {
            while (el == null) {
                notEmpty.await();
            }
            Integer result = el;
            el = null;
            notFull.signal();
            return result;
       } finally {
            lock.unlock();
        }
    }
}

//prints rr or w
class TTT {
    public static void main(String[] args) {
        ReadWriteLock rw = new ReentrantReadWriteLock();
        final Lock r = rw.readLock();
        final Lock w = rw.writeLock();

        new Thread(() -> {
            r.lock(); System.out.println("r0"); while(true); //r
        }).start();

        new Thread(() -> {
            r.lock(); System.out.println("r1"); while(true); //r
        }).start();

        new Thread(() -> {
            w.lock(); System.out.println("w0"); while(true); //w
        }).start();
    }
}

//ReentrantReadWriteBlock
class Cache {
    private Map<Integer, String> map = new ConcurrentHashMap<>(1024, 0.75f,1024);
    public String get(String key) {return key;}
    public void put(Integer key, String value) {};
    public <K, V> Map<K, V> synchronizedMap(Map<K, V> m) {
        return new myMap<>();
    }
    private static class myMap<T0, T1> implements Map<T0, T1> {
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final Lock rLock = rwLock.readLock();
        private final Lock wLock = rwLock.writeLock();
        private Map<T0, T1> m;
        @Override
        public int size() { rLock.lock(); try { return m.size(); } finally { rLock.unlock(); } }
        @Override
        public T1 put(T0 key, T1 value) { wLock.lock(); try { return m.put(key, value); } finally { rLock.unlock();} }
        @Override
        public boolean isEmpty() { return false; }
        @Override
        public boolean containsKey(Object key) { return false; }
        @Override
        public boolean containsValue(Object value) { return false; }
        @Override
        public T1 get(Object key) { return null; }
        @Override
        public T1 remove(Object key) { return null; }
        @Override
        public void putAll(@NonNull Map<? extends T0, ? extends T1> m) { }
        @Override
        public void clear() { }
        @NonNull @Override
        public Set<T0> keySet() { return null;  }
        @NonNull @Override
        public Collection<T1> values() { return null; }
        @NonNull  @Override
        public Set<Entry<T0, T1>> entrySet() { return null; }
    }
}

//My ThreadPool and Executor realization
class MyThreadPool implements Executor{
//    private final BlockingQueue<Runnable> taskQue = new ArrayBlockingQueue<>(256);
    private final BlockingQueue<Runnable> taskQue = new LinkedBlockingDeque<>(256);
    private final Thread[] pool;
    private final AtomicInteger adder = new AtomicInteger(0);
    private final AtomicInteger taker = new AtomicInteger(0);
    public MyThreadPool(int threadCount) {
        this.pool = new Thread[threadCount];
        for (int k = 0; k < threadCount; k++) {
            pool[k] = new Thread(() -> {
                while(true) {
                    try {
                        System.out.println("taskQue.size() " + taskQue.size());
                        Runnable nextTask = taskQue.take();
                        nextTask.run();
                        System.out.println("task " + taker.getAndIncrement() + " taken");
                    } catch (InterruptedException e) { break; }
                }
            });
            pool[k].start();
        }
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if(!taskQue.offer(command)) { System.out.println("Rejected!");
        } else { System.out.println("Task " + adder.getAndIncrement() + " added!"); }
    }
}

class Demo004_MyThreadPool {
    public static void main(String[] args) {
        Executor executor = new MyThreadPool(2);
        executor.execute(getTask());
        executor.execute(getTask());
        executor.execute(getTask());
        executor.execute(getTask());
    }
    private static Runnable getTask() {
        return new Runnable() {
            @Override
            public void run() { System.out.println("Hello from " + Thread.currentThread());}
        };
    }
}
//End of My ThreadPool and Executor realization

//My Blocked Stack with synchronized
class MyBlockedLinkedStack<T> {

    private Node<T> tail = null;
    private Object mutex = new Object();

    public void push(T newElem) {
        synchronized (mutex) {
            this.tail = new Node<>(newElem, tail);
        }
    }
    public T pop() {
        synchronized (mutex) {
            T result = tail.value;
            this.tail = tail.next;
            return result;
        }
    }

    private static class Node<E> {
        final E value;
        final Node<E> next;
        public Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }

    }
}
//End of My Blocked Stack with synchronized

//My Blocked Stack with Atomic
class MyBlockedLinkedStackAtomic<T> {

    private AtomicReference<Node<T>> tail = null;

    public void push(T newElem) {
        Node<T> newTail = new Node<T>(newElem, null);
        while(true) {
            Node<T> oldTail = this.tail.get();
            newTail.next = oldTail;
            if(tail.compareAndSet(oldTail, newTail)) {
                break;
            }
        }
    }
    public T pop() {
        while (true) {
            Node<T> oldTail = tail.get();
            Node<T> newTail = oldTail.next;
            if(tail.compareAndSet(oldTail, newTail)) {
                return oldTail.value;
            }
        }
    }

    private static class Node<E> {
        final E value;
        Node<E> next;
        public Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }
}
//End of My Blocked Stack with Atomic

// future runnable when complete returns null or monitor exception
class RunnableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> 5, executorService);
        Future future = executorService.submit(new Runnable() {
            public void run() {
                System.out.println("Asynchronous task");
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        future.get();  //returns null if the task has finished correctly.
    }
}
