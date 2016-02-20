package uk.co.odeon.androidapp.util.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;

public class DrawableManager {
    private static DrawableManager INSTANCE = null;
    private static final String TAG;
    private static final int THREAD_POOL_SIZE = 10;
    private File cacheDir;
    private Map<String, FastBitmapDrawable> drawableMap;
    private ThreadPoolExecutor executor;
    private SparseArray<FastBitmapDrawable> resImageCache;

    /* renamed from: uk.co.odeon.androidapp.util.drawable.DrawableManager.1 */
    class AnonymousClass1 extends Handler {
        private final /* synthetic */ ImageView val$imageView;
        private final /* synthetic */ String val$mapKey;
        private final /* synthetic */ int val$noImageRes;
        private final /* synthetic */ boolean val$overlayCenter;
        private final /* synthetic */ FastBitmapDrawable val$overlayDrawable;
        private final /* synthetic */ String val$url;

        AnonymousClass1(String str, int i, FastBitmapDrawable fastBitmapDrawable, boolean z, String str2, ImageView imageView) {
            this.val$url = str;
            this.val$noImageRes = i;
            this.val$overlayDrawable = fastBitmapDrawable;
            this.val$overlayCenter = z;
            this.val$mapKey = str2;
            this.val$imageView = imageView;
        }

        public void handleMessage(Message msg) {
            Log.v(DrawableManager.TAG, "Handling loaded image: " + this.val$url + " msg: " + msg.what);
            FastBitmapDrawable fbd = null;
            boolean isNoImage = false;
            if (msg.what == Constants.MSG_BITMAP_ERROR) {
                fbd = DrawableManager.this.loadCachedDrawableResource(this.val$noImageRes);
                isNoImage = true;
            } else if (msg.what == Constants.MSG_BITMAP_LOADED) {
                if (this.val$overlayDrawable == null) {
                    fbd = new FastBitmapDrawable((Bitmap) msg.obj);
                } else if (msg.obj == null) {
                    Log.e(DrawableManager.TAG, "Null bitmap received?!?");
                    return;
                } else {
                    Bitmap origBitmap = msg.obj;
                    Bitmap overlayedBitmap = Bitmap.createBitmap(origBitmap.getWidth(), origBitmap.getHeight(), origBitmap.getConfig());
                    Canvas canvas = new Canvas(overlayedBitmap);
                    canvas.drawBitmap(origBitmap, 0.0f, 0.0f, null);
                    Bitmap overlayBitmap = this.val$overlayDrawable.getBitmap();
                    int offX = 0;
                    int offY = 0;
                    if (this.val$overlayCenter) {
                        offX = (origBitmap.getWidth() - overlayBitmap.getWidth()) / 2;
                        offY = (origBitmap.getHeight() - overlayBitmap.getHeight()) / 2;
                    }
                    canvas.drawBitmap(this.val$overlayDrawable.getBitmap(), (float) offX, (float) offY, null);
                    fbd = new FastBitmapDrawable(overlayedBitmap);
                }
            }
            if (fbd != null) {
                if (!isNoImage) {
                    DrawableManager.this.drawableMap.put(this.val$mapKey, fbd);
                }
                if (this.val$url.equals((String) this.val$imageView.getTag())) {
                    this.val$imageView.setImageDrawable(fbd);
                }
            }
        }
    }

    static {
        TAG = DrawableManager.class.getSimpleName();
    }

    private DrawableManager() {
        this.resImageCache = new SparseArray();
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.drawableMap = new HashMap();
        this.cacheDir = ODEONApplication.getInstance().getCacheDir();
    }

    public static synchronized DrawableManager getInstance() {
        DrawableManager drawableManager;
        synchronized (DrawableManager.class) {
            if (INSTANCE == null) {
                drawableManager = new DrawableManager();
                INSTANCE = drawableManager;
            } else {
                drawableManager = INSTANCE;
            }
        }
        return drawableManager;
    }

    public void loadDrawable(String url, ImageView imageView, int loadingImageRes, int noImageRes) {
        loadDrawable(url, imageView, null, loadingImageRes, noImageRes);
    }

    public void loadDrawable(String url, ImageView imageView, File targetCacheFile, int loadingImageRes, int noImageRes) {
        loadDrawable(url, imageView, targetCacheFile, loadingImageRes, noImageRes, null, false);
    }

    private String getDrawableMapKey(String url, Integer overlayRes) {
        return new StringBuilder(String.valueOf(url)).append("?overlayRes=").append(overlayRes).toString();
    }

    public void loadDrawable(String url, ImageView imageView, File targetCacheFile, int loadingImageRes, int noImageRes, Integer overlayRes, boolean overlayCenter) {
        String mapKey = getDrawableMapKey(url, overlayRes);
        if (url == null || !url.equals((String) imageView.getTag())) {
            imageView.setTag(url);
            if (url == null) {
                imageView.setImageDrawable(loadCachedDrawableResource(noImageRes));
                return;
            }
            if (this.drawableMap.containsKey(mapKey)) {
                FastBitmapDrawable fbd = (FastBitmapDrawable) this.drawableMap.get(mapKey);
                if (fbd != null) {
                    imageView.setImageDrawable(fbd);
                    return;
                }
                this.drawableMap.remove(mapKey);
            }
            imageView.setImageDrawable(loadCachedDrawableResource(loadingImageRes));
            this.executor.execute(new BitmapLoader(url, targetCacheFile, new AnonymousClass1(url, noImageRes, overlayRes == null ? null : loadCachedDrawableResource(overlayRes.intValue()), overlayCenter, mapKey, imageView)));
        }
    }

    public void loadDrawable(String url, Handler handler) {
        if (this.drawableMap.containsKey(url)) {
            FastBitmapDrawable fbd = (FastBitmapDrawable) this.drawableMap.get(url);
            if (fbd != null) {
                Message message = new Message();
                message.what = Constants.MSG_BITMAP_LOADED;
                message.obj = fbd.getBitmap();
                handler.sendMessage(message);
                return;
            }
            this.drawableMap.remove(url);
        }
        this.executor.execute(new BitmapLoader(url, handler));
    }

    public FastBitmapDrawable loadCachedDrawableResource(int res) {
        if (this.resImageCache.get(res) == null) {
            this.resImageCache.put(res, new FastBitmapDrawable(ODEONApplication.getInstance().getResources().getDrawable(res)));
        }
        return (FastBitmapDrawable) this.resImageCache.get(res);
    }

    public File buildImageCacheFileBasedOnURLFilename(String imageURL) {
        if (imageURL == null) {
            return null;
        }
        try {
            return new File(this.cacheDir, new File(new URL(imageURL).getFile()).getName());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Failed to parse URL: " + imageURL, e);
            return null;
        }
    }

    public File buildImageCacheFileBasedOnCustomName(String imageName) {
        if (imageName == null) {
            return null;
        }
        return new File(this.cacheDir, imageName);
    }

    public void deleteImageCacheFilesByPattern(String pattern) {
        try {
            for (File file : this.cacheDir.listFiles()) {
                if (file.getName().matches(pattern)) {
                    Log.d(TAG, "Delete file: " + file.getName());
                    file.delete();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete images by pattern: " + pattern, e);
        }
    }
}
