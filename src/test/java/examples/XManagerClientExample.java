package examples;

import com.samourai.wallet.httpClient.IHttpClient;
import com.samourai.wallet.xmanagerClient.XManagerClient;
import com.samourai.xmanager.protocol.XManagerEnv;
import com.samourai.xmanager.protocol.XManagerService;
import com.samourai.xmanager.protocol.rest.AddressIndexResponse;

public class XManagerClientExample {
  public void example() {
    // configuration
    boolean testnet = true;
    boolean onion = false;
    IHttpClient httpClient = null; // TODO provide AndroidHttpClient or CliHttpClient

    // instantiation
    XManagerClient xManagerClient = new XManagerClient(httpClient, testnet, onion);
    XManagerEnv xManagerEnv = XManagerEnv.get(testnet);

    // get address (or default when server unavailable)
    XManagerService xmService = XManagerService.XM000; // TODO provide ID
    String address = xManagerClient.getAddressOrDefault(xmService);

    // get address + index
    AddressIndexResponse addressIndexResponse = xManagerClient.getAddressIndexOrDefault(xmService);
    System.out.println(
        "address=" + addressIndexResponse.address + ", index=" + addressIndexResponse.index);

    // validate address + index
    String addressToValidate = "...";
    int indexToValidate = 0;
    try {
      boolean valid =
          xManagerClient.verifyAddressIndexResponse(
                  xmService, addressToValidate, indexToValidate);
    } catch (Exception e) {
      // server not available
    }
  }
}
