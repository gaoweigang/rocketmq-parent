package com.gwg.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://segmentfault.com/a/1190000007462741
 * @author hp
 *
 */
public class NetworkInterfaceTest {
	
	private static final Logger log = LoggerFactory.getLogger(NetworkInterfaceTest.class);
	
	@Test
	public void TestOne() throws SocketException{
		this.getLocalAddress();
	}
	 public static String getLocalAddress() {
	        try {
	            // Traversal Network interface to get the first non-loopback and non-private address
	            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
	            ArrayList<String> ipv4Result = new ArrayList<String>();
	            ArrayList<String> ipv6Result = new ArrayList<String>();
	            while (enumeration.hasMoreElements()) {
	                final NetworkInterface networkInterface = enumeration.nextElement();
	                //getInetAddresses 方法返回绑定到该网卡的所有的 IP 地址
	                final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
	                while (en.hasMoreElements()) {
	                    final InetAddress address = en.nextElement();
		                log.info("网卡接口名称：{}，网卡展示名称：{}, 网卡接口地址：{}", networkInterface.getName(),networkInterface.getDisplayName(), address.getHostAddress());
	                    if (!address.isLoopbackAddress()) {//判断是不是本地环回地址
	                        if (address instanceof Inet6Address) {//判断是不是ipv6
	                            ipv6Result.add(normalizeHostAddress(address));
	                        } else {
	                            ipv4Result.add(normalizeHostAddress(address));
	                        }
	                    }
	                }
	            }

	            // prefer ipv4
	            if (!ipv4Result.isEmpty()) {
	                for (String ip : ipv4Result) {
	                    if (ip.startsWith("127.0") || ip.startsWith("192.168")) {
	                        continue;
	                    }

	                    return ip;
	                }

	                return ipv4Result.get(ipv4Result.size() - 1);
	            } else if (!ipv6Result.isEmpty()) {
	                return ipv6Result.get(0);
	            }
	            //If failed to find,fall back to localhost
	            final InetAddress localHost = InetAddress.getLocalHost();
	            return normalizeHostAddress(localHost);
	        } catch (Exception e) {
	            log.error("Failed to obtain local address", e);
	        }

	        return null;
	    }
	 
    public static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        } else {
            return localHost.getHostAddress();
        }
    }


}
