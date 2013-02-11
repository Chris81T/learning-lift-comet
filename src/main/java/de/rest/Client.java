package de.rest;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * Created with IntelliJ IDEA.
 * User: christian
 * Date: 2/9/13
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    /**
     * using provided rest-easy client framework
     * @param args
     */
    public static void main(String... args) {
        System.out.println("JAVA CLIENT WILL TRY TO MAKE A REQUEST TO EXISTING EXAMPLE REST ENDPOINT");

        try {
            ClientRequest request = new ClientRequest("http://localhost:8080/lift/hello/world");
            ClientResponse response = request.get();
            System.out.println("GET RESPONSE: " + response.getResponseStatus());

            System.out.println("START LONG POLLING REQUEST....");

            ClientRequest longRequest = new ClientRequest("http://localhost:8080/lift/actors");
            ClientResponse longResponse = longRequest.get();
            System.out.println("GET RESPONSE LONG: " + longResponse.getResponseStatus());
            System.out.println("GET RESPONSE LONG: " + longResponse.getAttributes());

        } catch (Exception e) {
            System.err.println("SOMETHING WENT WRONG !!!");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
