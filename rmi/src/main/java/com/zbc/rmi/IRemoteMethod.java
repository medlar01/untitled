package com.zbc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteMethod extends Remote {
    void sayHello(String name) throws RemoteException;
}
