/*
 * Copyright (c) 2016 Cisco and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openvpp.jvpp.test;

import static org.openvpp.jvpp.test.NotificationUtils.getChangeInterfaceState;
import static org.openvpp.jvpp.test.NotificationUtils.getDisableInterfaceNotificationsReq;
import static org.openvpp.jvpp.test.NotificationUtils.getEnableInterfaceNotificationsReq;

import org.openvpp.jvpp.VppJNIConnection;
import org.openvpp.jvpp.future.FutureJVppFacade;

public class FutureApiNotificationTest {

    private static void testFutureApi() throws Exception {
        System.out.println("Testing Java future API for notifications");

        final org.openvpp.jvpp.JVppImpl impl =
                new org.openvpp.jvpp.JVppImpl(new VppJNIConnection("FutureApiTest"));
        final FutureJVppFacade jvppFacade = new FutureJVppFacade(impl);
        System.out.println("Successfully connected to VPP");

        final AutoCloseable notificationListenerReg =
            jvppFacade.getNotificationRegistry().registerSwInterfaceSetFlagsNotificationCallback(NotificationUtils::printNotification);

        jvppFacade.wantInterfaceEvents(getEnableInterfaceNotificationsReq()).toCompletableFuture().get();
        System.out.println("Interface events started");

        System.out.println("Changing interface configuration");
        jvppFacade.swInterfaceSetFlags(getChangeInterfaceState()).toCompletableFuture().get();

        Thread.sleep(1000);

        jvppFacade.wantInterfaceEvents(getDisableInterfaceNotificationsReq()).toCompletableFuture().get();
        System.out.println("Interface events stopped");

        notificationListenerReg.close();

        System.out.println("Disconnecting...");
        // TODO we should consider adding jvpp.close(); to the facade
        impl.close();
    }

    public static void main(String[] args) throws Exception {
        testFutureApi();
    }
}
