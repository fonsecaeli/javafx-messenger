import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

public class GetIP {

   public static void main(String[] args) {
      String ip = getPublicIpAddress();
      //System.out.println(getPublicIpAddress());
      getIp();
   }
   
   private static String getPublicIpAddress() {
      String res = null;
      try {
         String localhost = InetAddress.getLocalHost().getHostAddress();
         Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
         while (e.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e.nextElement();
            if(ni.isLoopback())
               continue;
            if(ni.isPointToPoint())
               continue;
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while(addresses.hasMoreElements()) {
               InetAddress address = (InetAddress) addresses.nextElement();
               if(address instanceof Inet4Address) {
                  String ip = address.getHostAddress();
                  if(!ip.equals(localhost)) 
                     System.out.println((res = ip));
               }
            }
         }
      } 
      catch (Exception e) {
         e.printStackTrace();
      }
      return res;
   }
   
   private static void getIp() {
      	try{
    	
    	
    	java.net.URL URL = new java.net.URL("http://bot.whatismyipaddress.com");
 
		java.net.HttpURLConnection Conn = (HttpURLConnection)URL.openConnection();
		 
		java.io.InputStream InStream = Conn.getInputStream();
		 
		java.io.InputStreamReader Isr = new java.io.InputStreamReader(InStream);
		 
		java.io.BufferedReader Br = new java.io.BufferedReader(Isr);
		 
		System.out.print("Your IP address is " + Br.readLine());
		//JOptionPane.showMessageDialog(null, "IP is: " + Br.readLine() );
		
    	}catch(Exception e){e.printStackTrace();}
		
    	}
   
   
}