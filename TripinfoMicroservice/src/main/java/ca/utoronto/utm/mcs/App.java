package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws ClassNotFoundException, IOException {

      // This code must be used when you connect this service to MongoDB
      // Failure to use environment variables will result in a 0 for A2 correctness
      // and any remark requests will be denied.

      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/trip/request", new TripReq());
      server.createContext("/trip/confirm", new TripConf());
      server.createContext("/trip/", new Trip());
      server.createContext("/trip/passenger/", new Passenger());
      server.createContext("/trip/driver/", new Driver());
      server.createContext("/trip/driverTime", new DriverTime());

      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
