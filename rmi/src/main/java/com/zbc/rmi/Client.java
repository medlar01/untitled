package com.zbc.rmi;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(1099);
        Remote remote = registry.lookup("IRemoteMethod");
        if (remote instanceof IRemoteMethod) {
            IRemoteMethod method = (IRemoteMethod) remote;
            method.sayHello("李白");
        }
        System.out.println("remote call success.");
    }
}
