package wwz.netty.client;

import wwz.netty.util.Response;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResultFuture {
    public static ConcurrentHashMap<Long, ResultFuture> allResultFuture = new ConcurrentHashMap<>();
    final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Response response;
    private long timeout = 2 * 60 * 1000l;
    private long startTime = System.currentTimeMillis();


    public ResultFuture(ClientRequest request) {
        allResultFuture.put(request.getId(), this);
    }

    public Response get() {
        lock.lock();
        try {
            while(!done()) {
                condition.await();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    public Response get(Long time) {
        lock.lock();
        try {
            while(!done()) {
                condition.await(time, TimeUnit.SECONDS);
                if ((System.currentTimeMillis() - startTime) > time) {
                    System.out.println("请求超时");
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    public static void receive(Response response) {
        ResultFuture rf = allResultFuture.get(response.getId());
        if (rf != null) {
            Lock lock = rf.lock;
            lock.lock();
            try {
                rf.setResponse(response);
                rf.condition.signal();
                allResultFuture.remove(rf);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean done() {
        if (this.response != null) {
            return true;
        }
        return false;
    }


    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getStartTime() {
        return startTime;
    }
}
