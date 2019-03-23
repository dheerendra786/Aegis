package aegis.com.aegis.eventbus;

import android.net.Uri;

public class RecordingData {
    public final Uri targetUri;
    public final String phone;
    public final String contactName;

    public RecordingData(Uri targetUri, String phone, String contactName) {
        this.targetUri = targetUri;
        this.phone = phone;
        this.contactName = contactName;
    }
}
