package utils.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class NetChooser {
    private List<NetworkInterface> interfaces;
    private final NetworkInterface networkInterface;

    public NetChooser() {
        loadInterfaces();
        Scanner console = new Scanner(System.in);
        String[] allInterfaceNames = getInterfaces();
        for(int index=0; index < allInterfaceNames.length; ++index) {
            System.out.printf("%d. %s\n", index, allInterfaceNames[index]);
        }
        System.out.print("Select your interface :");
        NetworkInterface selected = getInterfacesByIndex(console.nextInt());
        networkInterface = selected;
        System.out.printf("Selected interface: %s\n", selected.getDisplayName());
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    private void loadInterfaces() {
        try {
            interfaces = new ArrayList<>();
            Enumeration<NetworkInterface> discoveredInterfaces = NetworkInterface.getNetworkInterfaces();
            while (discoveredInterfaces.hasMoreElements()) {
                NetworkInterface currentInterface = discoveredInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = currentInterface.getInetAddresses();
                int ipCount = 0;
                while(inetAddresses.hasMoreElements()) {
                    InetAddress currentAddress = inetAddresses.nextElement();
                    ipCount++;
                }
                if(ipCount > 0)
                    interfaces.add(currentInterface);
            }
        } catch(SocketException ex) {
            ex.printStackTrace();
        }

    }

    public NetworkInterface getInterfacesByIndex(int i) {
        if(i >= 0)
            return interfaces.get(i);
        else
            return null;
    }

    public String[] getInterfaces() {
        if(interfaces.size() > 0) {
            String[] result = new String[interfaces.size()];
            for (int i = 0; i < interfaces.size(); ++i) {
                result[i] = interfaces.get(i).getDisplayName();
            }
            return result;
        } else
            return null;
    }
}
