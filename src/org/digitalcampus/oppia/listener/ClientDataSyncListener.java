package org.digitalcampus.oppia.listener;

import org.digitalcampus.oppia.task.Payload;

/**
 * Created by ronak on 15/2/15.
 */
public interface ClientDataSyncListener {
    void clientDataSyncComplete(Payload response);
}
