
/**
 * ShipServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package ups.generated.axis2.ship;

    /**
     *  ShipServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ShipServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ShipServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ShipServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for processShipment method
            * override this method for handling normal response from processShipment operation
            */
           public void receiveResultprocessShipment(
                    ups.generated.axis2.ship.ShipServiceStub.ShipmentResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from processShipment operation
           */
            public void receiveErrorprocessShipment(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for processShipAccept method
            * override this method for handling normal response from processShipAccept operation
            */
           public void receiveResultprocessShipAccept(
                    ups.generated.axis2.ship.ShipServiceStub.ShipAcceptResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from processShipAccept operation
           */
            public void receiveErrorprocessShipAccept(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for processShipConfirm method
            * override this method for handling normal response from processShipConfirm operation
            */
           public void receiveResultprocessShipConfirm(
                    ups.generated.axis2.ship.ShipServiceStub.ShipConfirmResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from processShipConfirm operation
           */
            public void receiveErrorprocessShipConfirm(java.lang.Exception e) {
            }
                


    }
    