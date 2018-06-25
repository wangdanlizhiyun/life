package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by lizhiyun on 2018/2/12.
 */

public class CheckCamera implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        Camera camera = null;
        SurfaceView surfaceView = new SurfaceView(context);
        SurfaceHolder mHolder = surfaceView.getHolder();
        mHolder.addCallback(CALLBACK);
        try {
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            camera.setParameters(parameters);
            camera.setPreviewDisplay(mHolder);
            camera.setPreviewCallback(PREVIEW_CALLBACK);
            camera.startPreview();
            return true;
        } finally {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewDisplay(null);
                camera.setPreviewCallback(null);
                camera.release();
            }
        }
    }
    private static final Camera.PreviewCallback PREVIEW_CALLBACK = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    };

    private static final SurfaceHolder.Callback CALLBACK = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };
}
