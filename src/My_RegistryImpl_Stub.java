import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.MarshalException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.registry.Registry;
import java.rmi.server.*;

import sun.rmi.transport.StreamRemoteCall;

public final class My_RegistryImpl_Stub extends RemoteStub implements Registry, Remote {
    private static final Operation[] operations = new Operation[]{new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)")};
    private static final long interfaceHash = 4905912898345647071L;

    public My_RegistryImpl_Stub() {
    }

    public My_RegistryImpl_Stub(RemoteRef var1) {
        super(var1);
    }

    public Remote lookup(Registry registry,Object obj) throws AccessException, NotBoundException, RemoteException {
        try {
            RemoteCall var2 = super.ref.newCall((RemoteObject) registry, operations, 2, 4905912898345647071L);

            try {
                ObjectOutput var3 = var2.getOutputStream();
                Class c = java.io.ObjectOutputStream.class;
                Field f = c.getDeclaredField("enableReplace");
                f.setAccessible(true);
                f.setBoolean(var3,false);
                var3.writeObject(obj);
            } catch (IOException var18) {
                throw new MarshalException("error marshalling arguments", var18);
            }

            super.ref.invoke(var2);

            Remote var23;
            try {
                ObjectInput var6 = var2.getInputStream();
                var23 = (Remote)var6.readObject();
            } catch (IOException var15) {
                throw new UnmarshalException("error unmarshalling return", var15);
            } catch (ClassNotFoundException var16) {
                throw new UnmarshalException("error unmarshalling return", var16);
            } finally {
                super.ref.done(var2);
            }

            return var23;
        } catch (RuntimeException var19) {
            throw var19;
        } catch (RemoteException var20) {
            throw var20;
        } catch (NotBoundException var21) {
            throw var21;
        } catch (Exception var22) {
            throw new UnexpectedException("undeclared checked exception", var22);
        }
    }

    @Override
    public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
        return null;
    }

    @Override
    public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

    }

    @Override
    public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

    }

    @Override
    public void rebind(String name, Remote obj) throws RemoteException, AccessException {

    }

    @Override
    public String[] list() throws RemoteException, AccessException {
        return new String[0];
    }
}
