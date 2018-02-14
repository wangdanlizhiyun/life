package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.File;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckRECORD_AUDIO implements Check {

    private File mTempFile = null;

    @Override
    public Boolean check(Context context) throws Throwable {
        MediaRecorder mediaRecorder = new MediaRecorder();
        try {
            mTempFile = File.createTempFile("permission", "test");
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(mTempFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            return true;
        } finally {
            if (mediaRecorder != null) {
                try {
                    mediaRecorder.stop();
                } catch (Exception ignored) {
                }
                try {
                    mediaRecorder.release();
                } catch (Exception ignored) {
                }
            }
            if (mTempFile != null && mTempFile.exists()) {
                mTempFile.delete();
            }
        }
    }
}
