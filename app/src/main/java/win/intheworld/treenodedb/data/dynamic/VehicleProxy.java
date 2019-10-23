package win.intheworld.treenodedb.data.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface IVehicle {

    void run();

}

//concrete implementation
class Car implements IVehicle{

    public void run() {
        System.out.println("Car is running");
    }

}

//proxy class
public class VehicleProxy {

    private IVehicle vehicle;

    public VehicleProxy(IVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public IVehicle create(){
        final Class<?>[] interfaces = new Class[]{IVehicle.class};
        final VehicleInvocationHandler handler = new VehicleInvocationHandler(vehicle);

        return (IVehicle) Proxy.newProxyInstance(IVehicle.class.getClassLoader(), interfaces, handler);
    }

    public class VehicleInvocationHandler implements InvocationHandler {

        private final IVehicle vehicle;

        public VehicleInvocationHandler(IVehicle vehicle) {
            this.vehicle = vehicle;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            System.out.println("--before running...");
            Object ret = method.invoke(vehicle, args);
            System.out.println("--after running...");

            return ret;
        }
    }

    public static void main(String[] args) {
        IVehicle car = new Car();
        VehicleProxy proxy = new VehicleProxy(car);

        IVehicle proxyObj = proxy.create();
        proxyObj.run();
    }
}
