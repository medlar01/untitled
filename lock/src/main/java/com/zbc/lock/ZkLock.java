package com.zbc.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static org.apache.curator.framework.recipes.cache.CuratorCacheListener.Type.NODE_DELETED;


public class ZkLock implements Lock {
    private final String path;
    private final CuratorFramework client;
    private CountDownLatch cdl;
    private final CuratorCache cache;

    public ZkLock(CuratorFramework client, String path) throws Exception {
        this.path = path;
        this.client = client;
        this.cache = CuratorCache.build(client, path);
        this.cache.start();
    }

    @Override
    public void lock() {
        boolean isLock = tryLock();
        if (!isLock) {
            try {
                listen();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lock();
        }
    }

    private void listen() throws InterruptedException {
        CuratorCacheListener listener = (t, o, n) -> {
            if (t == NODE_DELETED) {
                System.out.println(Thread.currentThread() + " --------------。>>> CuratorCache: " + t);
                cdl.countDown();
            }
        };
        cache.listenable().addListener(listener);
        cdl.await();
        cache.listenable().removeListener(listener);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        this.cdl = new CountDownLatch(1);
        try {
            Stat stat = client.checkExists()
                    .forPath(path);
            if (stat == null) {
                client.create()
                        .creatingParentsIfNeeded()
                        .forPath(path);
                return true;
            }
        } catch (KeeperException.NodeExistsException ignore) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread() + ":NodeExistsException ----------------。>>>");
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        boolean isLock = tryLock();
        if (!isLock) {
            try {
//            this.cache = new PathChildrenCache(client, path, false);
//            this.cache.getListenable().addListener((client, event) -> {
//                if (event.getType() == CHILD_REMOVED) {
//                    this.cdl.countDown();
//                }
//            });
//            this.cache.start();
                if (!this.cdl.await(time, unit)) {
                    return false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isLock = tryLock(time, unit);
        }
        return isLock;
    }

    @Override
    public void unlock() {
        try {
            client.delete()
                    .deletingChildrenIfNeeded()
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
