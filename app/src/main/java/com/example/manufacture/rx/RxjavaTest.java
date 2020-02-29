package com.example.manufacture.rx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.internal.fuseable.ScalarSupplier;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxjavaTest {

    private static int i = 12;

    public static void main(String[] args) {

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
        

    }



}