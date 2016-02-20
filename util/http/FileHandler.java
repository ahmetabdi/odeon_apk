package uk.co.odeon.androidapp.util.http;

import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;

public class FileHandler implements ResponseHandler {
    private File targetFile;

    public FileHandler(File targetFile) {
        this.targetFile = targetFile;
    }

    public File getTargetFile() {
        return this.targetFile;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public boolean handleResponse(HttpResponse response, Uri uri) throws IOException {
        InputStream urlStream = new FlushedInputStream(response.getEntity().getContent());
        FileOutputStream fout = new FileOutputStream(this.targetFile);
        byte[] bytes = new byte[256];
        int r;
        do {
            r = urlStream.read(bytes);
            if (r >= 0) {
                fout.write(bytes, 0, r);
                continue;
            }
        } while (r >= 0);
        urlStream.close();
        fout.close();
        return true;
    }
}
