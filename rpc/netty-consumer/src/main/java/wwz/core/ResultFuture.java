package wwz.core;


import wwz.param.ClientRequest;
import wwz.param.Response;

import java.util.Set;
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

    static class FutureThread extends Thread {
        @Override
        public void run() {
            Set<Long> ids = allResultFuture.keySet();
            for (Long id : ids) {
                ResultFuture rf = allResultFuture.get(id);
                if (rf == null) {
                    allResultFuture.remove(rf);
                } else if ((System.currentTimeMillis() - rf.getStartTime()) > rf.getTimeout()){
                    Response resp = new Response();
                    resp.setId(id);
                    resp.setCode("33333");
                    resp.setMsg("链路请求超时");
                    receive(resp);

                }
            }
        }
    }

    static {
        FutureThread futureThread = new FutureThread();
        futureThread.setDaemon(true);
        futureThread.start();
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
