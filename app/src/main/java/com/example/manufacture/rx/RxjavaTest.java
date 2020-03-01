package com.example.manufacture.rx;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.internal.fuseable.ScalarSupplier;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxjavaTest {

    private static int i = 12;

    public static void main(String[] args) {
        Observable<String> sObservable = Observable.just("a","b","c");
        Observable.just(1,"2",3)
                .cast(Integer.class)
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Throwable {
                        return true;
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println(integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println(e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    //生产者生生的速度大于消费者消费的速度，这个现象称为Backpressure背压
    // Flowable就是为了解决Backpressure问题而产生的。
    //如果生产者和消费者不在一个线程，如果生产者速度大于消费者速度，
    // 就会产生backpressure问题，即在异步情况下
    private void aboutFlowalble(){
        //在异步调用的时候，rxjava有个缓存池，用来缓存消费者暂时处理不了缓存下来的数据，缓存池的大小为128，
        // 只能缓存128个事件，无论request中传入的数字比128大还是小，
        // 缓存池中在刚开始都会存入128个事件，如果本身没有那么多事件就不会存128个
        //request代表下流处理事件的能力，为0的话代表没有处理能力，在同步情况下报错
        //异步情况下发送事件，但下流接收不到事件，事件存储在缓存池
        //创建时指定背压策略：
        //1.ERROR：如果缓存池溢出，就会立刻抛出MissingBackpressureException异常。
        //2.BUFFER:把默认容量为128的缓存池换成一个大的缓存池，支持更多的数据
        // 消费者通过request传入一个很大的数据，生产者也会生产事件
        // 并把处理不了的事件缓存，但是这种方式比较消耗内存
        //3.DROP:消费者通过request传入其需求n
        // 生产着把n个事件传递给消费者供其消费，其他消费不掉的事件就丢弃
        //4.LATEST:基本和DROP一致,唯一的区别是LATEST总能使消费者能够接收到生产者产生的最后一个事件

        //create时指定策略，如果Flowable通过创建的，
        // 可以采用onBackpressureBuffer onBackpressureDrop onBackpressureLatest方式指定
        @NonNull Disposable subscribe = Flowable.just(1, 2, 3)
                .onBackpressureBuffer()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {

                    }
                });
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Throwable {

            }
        }, BackpressureStrategy.BUFFER).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                //传递过来的是Subscripiton而不再是disposable
                // 也可切断观察者和被观察者直接按的联系，调用Subscription.cancel方法即可
                // 不同的是Subscription增加了一个void request(long n)方法
                //这个方法用来向生产者申请可以消费的事件数量
            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    //创建类的操作符
    private void aboutCreatedOperator(){

        //1.create: 用于创建一个具有发射事件能力的被观察者
        Observable observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                //发送数据
                emitter.onNext("777");
            }
        });

        String[] s = new String[]{"1","2","3"};

        //2.just: 只是简单的原样发射,可将数组或Iterable当做单个数据。它接受一至九个参数
        Disposable d = Observable.just(s).subscribe(new Consumer<String[]>() {
            @Override
            public void accept(String[] serializable) throws Throwable {

            }
        });

        //3.from :将数组或Iterable的元素拿出来，做成多个事件进行发射(fromArray,fromIterable,fromRunnable...)
        //just(item1,item2, ...) 底层调用 fromArray
        Observable.fromRunnable(new Runnable() {
            @Override
            public void run() {

            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Throwable {

            }
        });
        Disposable disposable = Observable.fromArray(s).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {

            }
        });

        //4.Interval : 按特定时间间隔发出一个整数序列
        Observable.interval(500, TimeUnit.MILLISECONDS, Schedulers.io()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Throwable {
                //每隔500毫秒收到一个数字，0,1,2,3 ...
            }
        });

        //5.repeat : 创建一个重复发送指定数据或数据序列的Observable(括号内为重复次数)
        Observable.just(s).repeat(500).subscribe(new Consumer<String[]>() {
            @Override
            public void accept(String[] strings) throws Throwable {
                System.out.println(strings.length+"");
            }
        });

        //6.repeatWhen : 有条件创建一个重复发送指定数据或数据序列的Observable
        Observable.just("1").repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Throwable {
                return objectObservable.delay(1,TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                System.out.println(s);
            }
        });

        //7.timer : 在一定时间间隔后发送单个数据0
        Observable.timer(2,TimeUnit.SECONDS,Schedulers.io()).map(new Function<Long,String>() {
            @Override
            public String apply(Long aLong) throws Throwable {
                return "finish";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                System.out.println(s);
            }
        });

        //8.range : 创建一个发送指定范围内的整数数列的observable
        Observable.range(0,7).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer+"");
            }
        });

        //9.defer :当订阅者订阅才发送数据
        //使得观察者可以很容易地获得序列的最新版本
        @NonNull Observable<Integer> defer = Observable.defer(new Supplier<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> get() throws Throwable {
                return Observable.just(i);
            }
        });
        i = 15;

        defer.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer o) throws Throwable {
                System.out.println(o+"");
            }
        });

        //10.empty :创建一个不发射任何数据但是正常终止的被观察者
        //11.never :创建一个不发射数据也不终止的被观察者
        //12.error :创建一个不发射数据以一个错误终止的被观察者

    }

    //转化类操作符 将一个被观察者通过某种方法，转化为另外的被观察者
    private void aboutChangeOperator(){
        //1.map :将每一个发射的事件，应用于一个函数，从而可以对事件做相应处理，事件数量不会变化
        Observable.just(1,2,3).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Throwable {
                return "emit:"+integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                System.out.println(s);
            }
        });

        //2.FlatMap:将一个被观察者转化为多个被观察者，然后再将他们合并到一个被观察者中
        //3.ConcatMap : 同FlatMap，但FlatMap不保证最后事件顺序，ConcatMap保证最后事件顺序
        Observable.just(1,2,3).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Throwable {
                //为每个事件 创建返回一个新的Observable
                return Observable.just("emit:"+integer);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                //将新产生的所有Observable合并成一个，将他们的事件发送到观察者，不保证顺序
                System.out.println(s);
            }
        });

        //4.buffer : 定时或定量缓存打包事件，将其发射到下游，而不是一次一个发射
        //buffer(count,skip) 第一个参数count代表每次缓存的数量，skip表示下一次缓存的间隔
        //默认skip等于count 如skip是1 表示下一次从2开始缓存
        //下例输出结果：
        //begin
        //1 2 3
        //begin
        //4 5
        //count为3，则每次缓存3个，第一次发送1,2,3
        //skip默认同count，为3.则下次缓存从4开始缓存3个，由于不足3个，所以发送4,5
        Observable.just(1,2,3,4,5)
                .buffer(3)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Throwable {
                        System.out.println("begin");
                        for (Integer i:integers){
                            System.out.print(i+" ");
                        }
                        System.out.println("");
                    }
                });
        //interval表示每隔500ms发射一个事件
        Observable.interval(500, TimeUnit.MILLISECONDS, Schedulers.io())
                .take(10)//表示只取前面10个事件
                .buffer(1, TimeUnit.SECONDS, 2)//每隔1s 打包一次事件将其发射到下游，count是每次打包的最大数量
                .subscribe(new Consumer<List<Long>>() {
                    @Override
                    public void accept(List<Long> longs) throws Exception {
                        //每隔 1 s 打包一次事件，所以列表可能为空
                        System.out.println("begin");
                        for (Long aLong : longs) {
                            System.out.println(aLong+" ");
                        }
                        System.out.println("");
                    }
                });

        //5. count : 计算事件总数并发送到下流
        Observable.just(1,2,3).count().subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Throwable {
                System.out.println(aLong); //3
            }
        });
    }

    //关于过滤操作符
    private void aboutFilterOperator(){
        // 1.distinct :去除重复事件
        //结果为 1,2,3
        Observable.just(1,2,1,2,3,3).distinct().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //自定义过滤条件，比如自定义判断对象相等的条件
        // 比如类Person(Age,Name，No) 当属性No相等则认为两个对象一样
        //在apply方法中 定义 return person.getNo()
        //下面例子 结果为1,2,4（因为3/2 结果与 2/2 结果一样，所以被过滤了）
        Observable.just(1,2,1,2,3,3,4).distinct(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) throws Throwable {
                return integer/2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });

        //2.Filter : 接收一个参数，过滤掉不符合条件的
        //下例结果为2,4,6 则过滤掉奇数
        Observable.just(1,2,3,4,5,6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Throwable {
                return  integer % 2 == 0;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });

        //3. elementAt : 只返回指定下标的事件，如果没有这一项，就返回默认值
        Observable.just(1,2,3)
                .elementAt(5, 0)//只发射第6个数据， 若找不到就发射默认值0
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println(integer);//0
                    }
                });
        Observable.just(1,2,3)
                .elementAt(2)//只发射第三个数据，若找不到就不发射
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println(integer);//3
                    }
                });


        //4.take : 接收符合条件的 连续 个数据
        //指定接收前3个数 结果为 1 2 3
        Observable.just(1,2,3,4,5,6).take(3).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //指定接收某个时间前数据 下例接收前3秒数据 结果为 0 1 2 3 4
        Observable.interval(500,TimeUnit.MILLISECONDS,Schedulers.trampoline())
                .take(3,TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        System.out.println(aLong);
                    }
                });
        //takeLast:接收后2个数据 结果：2 3
        Observable.just(1,2,3).takeLast(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //takeWhile:接收跳出循环前数据 （当 test 方法中 return false 跳出循环）
        //结果为 1 （2 < 2 为false 跳出循环）
        Observable.just(1,2,3,4).takeWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Throwable {
                return integer<2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //takeUntil: 接收数据直到跳出循环 与 takeWhile区别:跟while()与do{}while() 区别一样
        //所以下例结果为 1 （至少接收 1 个数据）
        Observable.just(1,2,3,4)
                .takeUntil(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer aLong) throws Throwable {
                        return aLong>0;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //takeUntil(另一种方式):接收数据直到 takeUntil 中的Observable开始发送数据
        //下例结果为：0 1 2
        Observable.interval(500,TimeUnit.MILLISECONDS,Schedulers.trampoline())
                .take(10)
                .takeUntil(Observable.interval(2,TimeUnit.SECONDS))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        System.out.println(aLong);
                    }
                });

        //5.skip 丢弃指定条件数据
        // 丢弃前N项数据
        Observable.range(0, 5)
                .skip(3)//丢弃前3个数据
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println(integer); // 3 4
                    }
                });

        //丢弃从开始到指定时间间隔 之间 发射的数据
        //interval 表示 每隔500ms 发射一个事件
        Observable.interval(500, TimeUnit.MILLISECONDS, Schedulers.trampoline())
                .skip(3000, TimeUnit.MILLISECONDS)//丢弃前3000ms发射的数据
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        System.out.println(aLong); //5 6 7 8 ...
                    }
                });
        //丢弃最后2个数据 结果为 1 2 3
        Observable.just(1,2,3,4,5).skipLast(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //丢弃最后一秒的数据 下例结果为 0.1.2.3.4.5.6.7.8（9 被丢弃）
        Observable.interval(500,TimeUnit.MILLISECONDS,Schedulers.trampoline())
                  .take(10)
                  .skipLast(1,TimeUnit.SECONDS)
                  .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Throwable {
                System.out.println(aLong);
            }
        });
        //丢弃数据直到skipUtil 中的 Observable 开始发送数据
        //下例结果：3 4 5 6 7 8 9 （0 1 2 被丢弃）
        Observable.interval(500,TimeUnit.MILLISECONDS,Schedulers.trampoline())
                .take(10)
                .skipUntil(Observable.interval(2,TimeUnit.SECONDS).take(5))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        System.out.println(aLong);
                    }
                });
        //skipWhile 一直丢弃数据直到跳出循环（test 中 return false）
        //结果为 4 3 5 6 （1 2 被丢弃）
        Observable.just(1,2,4,3,5,6)
                .skipWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Throwable {
                        return integer<4;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
    }

    //关于合并操作符
    private void aboutMergeOperator(){
        //1.zip : 组合多个Observable发射的数据集合，然后再发射这个结果。
        // 如果多个Observable发射的数据量不一样，则以最少的Observable为标准进行压合！
        //结果只有2条数据：1-a 2-b
        Observable<String> sObservable = Observable.just("a","b","c");
        Observable<Integer> iObservable = Observable.just(1,2);
        Observable.zip(sObservable, iObservable, new BiFunction<String, Integer,String>() {
            @Override
            public String apply(String s, Integer integer) throws Throwable {
                return integer+"-"+s;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                System.out.println(s);
            }
        });

        //2.merge/mergeWith:将两个Observable合并成一个Observable
        //结果为：a b c 1 2
        Observable.merge(sObservable,iObservable).subscribe(new Consumer<Serializable>() {
            @Override
            public void accept(Serializable serializable) throws Throwable {
                System.out.println(serializable);
            }
        });
        //mergeWith 只能合并与本身类型相同的Observable
        // 报错： sObservable.mergeWith(iObservable)
        //结果：a b c 1 2
        final Observable observable = Observable.just("1","2");
        sObservable.mergeWith(observable).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                System.out.println(o);
            }
        });

        //startWith系列：
        //startWithItem:开始前先发送item 结果 0 1 2 3
        Observable.just(1,2,3)
                .startWithItem(0)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println(integer);
                    }
                });
        //startWithArray 开始前先发送 array 内容 结果 0 1 2 3 4 5
        Observable.just(3,4,5)
                .startWithArray(0,1,2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println(integer);
                    }
                });
        //结果：a b c 1 2
        Observable.just("1","2").startWith(sObservable).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                System.out.println(s);
            }
        });
    }

    private void aboutErrorOperator(){
        //1.retry : 当原始Observable在遇到错误时进行重试，目的是希望本次订阅不以失败事件结束
        //运行结果：1 1 1 1 java.lang.String cannot be cast to java.lang.Integer
        Observable.just(1, "2")
                .cast(Integer.class)//将被观察者发送的事件数据类型强转为Integer
                .retry(3)//如果出错 重试三次， 如果不写参数，表示一直重复尝试
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        System.out.println(integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println(e.toString());
                    }

                    @Override
                    public void onComplete() { }
                });

        //Catch : 捕获错误，并进行处理！
        //onErrorReturn 当原先的被观察者遇到错误时，再去发射一个事件
        //结果：1 0
        Observable.just(1,"2")
                .cast(Integer.class)
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Throwable {
                        return 0;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //onErrorReturnItem：结果同上 底层调用 onErrorReturn
        Observable.just(1,"2")
                .cast(Integer.class)
                .onErrorReturnItem(0)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println(integer);
                    }
                });
        //onErrorResumeNext： 当原始Observable在遇到错误时，使用其他Observable的数据序列
        //结果 1 0
        Observable.just(1, "2")
                .cast(Integer.class)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(Throwable throwable) throws Throwable {
                        return Observable.just(0);
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
        //onErrorComplete:出错就调用onComplete（）
        //结果：1 onComplete
        Observable.just(1, "2",3)
                .cast(Integer.class)
                .onErrorComplete()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        System.out.println(integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {}

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
        //onErrorComplete: test方法返回true执行 onComplete() 返回false执行onError()
        Observable.just(1,"2",3)
                .cast(Integer.class)
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Throwable {
                        return false;
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println(integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println(e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    private void aboutUtilOperator(){
        //1.delay: 延迟发送
        Observable.fromArray(1,2,3)
                .delay(3, TimeUnit.SECONDS)//3s以后再去发射事件 整体延迟 区别于interval对每个事件的延迟效果
                .subscribeOn(Schedulers.io())//io线程发射事件
                .observeOn(Schedulers.trampoline())//主线程获取通知
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println(integer);
                    }
                });
        


    }


}