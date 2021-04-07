package com.zbc.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteMethodImpl extends UnicastRemoteObject implements IRemoteMethod {

    protected RemoteMethodImpl() throws RemoteException {
    }

    @Override
    public void sayHello(String name) throws RemoteException{
        System.out.println(name + ": hello!");
    }
}
