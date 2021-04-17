package com.zbc.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class ZkLock implements Lock {
    private final String path;
    private final CuratorFramework client;
    private CountDownLatch cdl;
    private CuratorCache cache;
    private boolean tryLock = true;
    private String currentPath;
    private String beforePath;

    public ZkLock(CuratorFramework client, String path) throws Exception {
        this.path = path;
        this.client = client;
        try {
            client.create()
                    .creatingParentContainersIfNeeded()
                    .forPath(path);
        } catch (KeeperException.NodeExistsException ignore) {}
    }

    @Override
    public void lock() {
        tryLock = false;
        boolean isLock = tryLock();
        if (!isLock) {
            await();
            lock();
        }
    }

    private void await() {
        if (cache == null) {
            cache = CuratorCache.build(client, beforePath);
            cache.listenable()
                    .addListener((t, o, n) -> {
                        if (t == CuratorCacheListener.Type.NODE_DELETED) {
                            if (cdl != null) {
//                                System.out.println("addListener ---------------。>>> " + new Date() + "_" + beforePath);
                                cdl.countDown();
                            }
                        }
                    })
            ;
            cache.start();
        }
        try {
            Stat stat = client.checkExists()
                    .forPath(beforePath);
//            System.out.println("checkExists ---------------。>>> " + new Date() + "_" + beforePath);
            if (stat == null) {
                return;
            }
            cdl = new CountDownLatch(1);
            cdl.await();
            cache.close();
            cache = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            if (this.currentPath == null) {
                this.currentPath = client.create()
                        .creatingParentContainersIfNeeded()
                        // 创建一个有序的节点
                        .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .forPath(this.path + "/node_");
            }

            List<String> children = client.getChildren()
                    .forPath(this.path);
            Collections.sort(children);
            int search = Collections.binarySearch(children, this.currentPath.substring(this.path.length() + 1));
            if (search == 0) {
                return true;
            }

            if (tryLock) {
                client.delete()
                        .deletingChildrenIfNeeded()
                        .forPath(path);
            } else {
                this.beforePath = this.path + "/" + children.get(search - 1);
            }
        } catch (KeeperException.NodeExistsException ignore) {
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        try {
            client.delete()
                    .deletingChildrenIfNeeded()
                    .forPath(currentPath);
//            System.out.println("unlock ---------------。>>> " + new Date() + "_" + currentPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
