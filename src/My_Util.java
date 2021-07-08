

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.StubNotFoundException;
import java.rmi.server.LogStream;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonNotFoundException;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.rmi.runtime.Log;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public final class My_Util {
    private static final boolean ignoreStubClasses;
    private static final Map<Class<?>, Void> withoutStubs;
    private static final Class<?>[] stubConsParamTypes;

    private My_Util() {
    }

    public static Remote createProxy(Class<?> var0, RemoteRef var1, boolean var2) throws StubNotFoundException {
        Class var3;
        try {
            var3 = getRemoteClass(var0);
        } catch (ClassNotFoundException var9) {
            throw new StubNotFoundException("object does not implement a remote interface: " + var0.getName());
        }

        if (var2 || !ignoreStubClasses && stubClassExists(var3)) {
            return createStub(var3, var1);
        } else {
            final ClassLoader var4 = var0.getClassLoader();
            final Class[] var5 = getRemoteInterfaces(var0);
            final RemoteObjectInvocationHandler var6 = new RemoteObjectInvocationHandler(var1);

            try {
                return (Remote)AccessController.doPrivileged(new PrivilegedAction<Remote>() {
                    public Remote run() {
                        return (Remote)Proxy.newProxyInstance(var4, var5, var6);
                    }
                });
            } catch (IllegalArgumentException var8) {
                throw new StubNotFoundException("unable to create proxy", var8);
            }
        }
    }

    private static boolean stubClassExists(Class<?> var0) {
        if (!withoutStubs.containsKey(var0)) {
            try {
                Class.forName(var0.getName() + "_Stub", false, var0.getClassLoader());
                return true;
            } catch (ClassNotFoundException var2) {
                withoutStubs.put(var0, null);
            }
        }

        return false;
    }

    private static Class<?> getRemoteClass(Class<?> var0) throws ClassNotFoundException {
        while(var0 != null) {
            Class[] var1 = var0.getInterfaces();

            for(int var2 = var1.length - 1; var2 >= 0; --var2) {
                if (Remote.class.isAssignableFrom(var1[var2])) {
                    return var0;
                }
            }

            var0 = var0.getSuperclass();
        }

        throw new ClassNotFoundException("class does not implement java.rmi.Remote");
    }

    private static Class<?>[] getRemoteInterfaces(Class<?> var0) {
        ArrayList var1 = new ArrayList();
        getRemoteInterfaces(var1, var0);
        return (Class[])var1.toArray(new Class[var1.size()]);
    }

    private static void getRemoteInterfaces(ArrayList<Class<?>> var0, Class<?> var1) {
        Class var2 = var1.getSuperclass();
        if (var2 != null) {
            getRemoteInterfaces(var0, var2);
        }

        Class[] var3 = var1.getInterfaces();

        for(int var4 = 0; var4 < var3.length; ++var4) {
            Class var5 = var3[var4];
            if (Remote.class.isAssignableFrom(var5) && !var0.contains(var5)) {
                Method[] var6 = var5.getMethods();

                for(int var7 = 0; var7 < var6.length; ++var7) {
                    checkMethod(var6[var7]);
                }

                var0.add(var5);
            }
        }

    }

    private static void checkMethod(Method var0) {
        Class[] var1 = var0.getExceptionTypes();

        for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].isAssignableFrom(RemoteException.class)) {
                return;
            }
        }

        throw new IllegalArgumentException("illegal remote method encountered: " + var0);
    }

    private static RemoteStub createStub(Class<?> var0, RemoteRef var1) throws StubNotFoundException {
        String var2 = "My_RegistryImpl_Stub";

        try {
//            Class var3 = Class.forName(var2, false, var0.getClassLoader());
            Class var3 = Class.forName(var2);
            Constructor var4 = var3.getConstructor(stubConsParamTypes);
            return (RemoteStub)var4.newInstance(var1);
        } catch (ClassNotFoundException var5) {
            throw new StubNotFoundException("Stub class not found: " + var2, var5);
        } catch (NoSuchMethodException var6) {
            throw new StubNotFoundException("Stub class missing constructor: " + var2, var6);
        } catch (InstantiationException var7) {
            throw new StubNotFoundException("Can't create instance of stub class: " + var2, var7);
        } catch (IllegalAccessException var8) {
            throw new StubNotFoundException("Stub class constructor not public: " + var2, var8);
        } catch (InvocationTargetException var9) {
            throw new StubNotFoundException("Exception creating instance of stub class: " + var2, var9);
        } catch (ClassCastException var10) {
            throw new StubNotFoundException("Stub class not instance of RemoteStub: " + var2, var10);
        }
    }

    private static String getMethodNameAndDescriptor(Method var0) {
        StringBuffer var1 = new StringBuffer(var0.getName());
        var1.append('(');
        Class[] var2 = var0.getParameterTypes();

        for(int var3 = 0; var3 < var2.length; ++var3) {
            var1.append(getTypeDescriptor(var2[var3]));
        }

        var1.append(')');
        Class var4 = var0.getReturnType();
        if (var4 == Void.TYPE) {
            var1.append('V');
        } else {
            var1.append(getTypeDescriptor(var4));
        }

        return var1.toString();
    }

    private static String getTypeDescriptor(Class<?> var0) {
        if (var0.isPrimitive()) {
            if (var0 == Integer.TYPE) {
                return "I";
            } else if (var0 == Boolean.TYPE) {
                return "Z";
            } else if (var0 == Byte.TYPE) {
                return "B";
            } else if (var0 == Character.TYPE) {
                return "C";
            } else if (var0 == Short.TYPE) {
                return "S";
            } else if (var0 == Long.TYPE) {
                return "J";
            } else if (var0 == Float.TYPE) {
                return "F";
            } else if (var0 == Double.TYPE) {
                return "D";
            } else if (var0 == Void.TYPE) {
                return "V";
            } else {
                throw new Error("unrecognized primitive type: " + var0);
            }
        } else {
            return var0.isArray() ? var0.getName().replace('.', '/') : "L" + var0.getName().replace('.', '/') + ";";
        }
    }

    static {
        ignoreStubClasses = (Boolean)AccessController.doPrivileged(new GetBooleanAction("java.rmi.server.ignoreStubClasses"));
        withoutStubs = Collections.synchronizedMap(new WeakHashMap(11));
        stubConsParamTypes = new Class[]{RemoteRef.class};
    }
}
