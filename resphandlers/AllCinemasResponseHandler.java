package uk.co.odeon.androidapp.resphandlers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.util.http.AbstractJSONResponseHandler;

public class AllCinemasResponseHandler extends AbstractJSONResponseHandler {
    private static final String TAG;
    private ContentResolver contentResolver;
    private SharedPreferences prefs;

    public boolean handleJSONRepsonse(org.json.JSONObject r27, android.net.Uri r28) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0134 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r26 = this;
        r14 = 0;
        r2 = "config";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r27;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r8 = r0.getJSONObject(r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = "dataHash";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r12 = r8.getString(r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = "data";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r27;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r11 = r0.optJSONObject(r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r11 != 0) goto L_0x002d;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0019:
        r2 = TAG;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = "Empty data in JSON response";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        android.util.Log.i(r2, r3);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r14 == 0) goto L_0x002b;
    L_0x0022:
        r2 = r14.isClosed();
        if (r2 != 0) goto L_0x002b;
    L_0x0028:
        r14.close();
    L_0x002b:
        r15 = 0;
    L_0x002c:
        return r15;
    L_0x002d:
        r2 = "sites";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r25 = r11.optJSONObject(r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r25 == 0) goto L_0x003b;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0035:
        r2 = r25.length();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r2 != 0) goto L_0x004f;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x003b:
        r2 = TAG;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = "Empty site list in JSON response";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        android.util.Log.i(r2, r3);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r14 == 0) goto L_0x004d;
    L_0x0044:
        r2 = r14.isClosed();
        if (r2 != 0) goto L_0x004d;
    L_0x004a:
        r14.close();
    L_0x004d:
        r15 = 0;
        goto L_0x002c;
    L_0x004f:
        r2 = 1;
        r4 = new java.lang.String[r2];	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = "Site._id";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r4[r2] = r3;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r26;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r0.contentResolver;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = uk.co.odeon.androidapp.provider.SiteContent.SiteColumns.CONTENT_URI;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r6 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r7 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r14 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r23 = new java.util.HashSet;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r23.<init>();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = "_id";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r19 = r14.getColumnIndex(r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r14 == 0) goto L_0x007a;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0071:
        r14.moveToFirst();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0074:
        r2 = r14.isAfterLast();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r2 == 0) goto L_0x00fc;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x007a:
        r10 = new java.util.HashSet;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r10.<init>();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r17 = r25.keys();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0083:
        r2 = r17.hasNext();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r2 != 0) goto L_0x0137;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0089:
        r2 = r10.size();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r2 <= 0) goto L_0x0180;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x008f:
        r16 = 1;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0091:
        if (r16 == 0) goto L_0x00c3;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x0093:
        r2 = TAG;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = "Inserting ";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3.<init>(r5);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = r10.size();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = r3.append(r5);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = " new/updated sites";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = r3.append(r5);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = r3.toString();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        android.util.Log.i(r2, r3);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r26;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = r0.contentResolver;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = uk.co.odeon.androidapp.provider.SiteContent.SiteColumns.CONTENT_URI;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = new android.content.ContentValues[r2];	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r10.toArray(r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = (android.content.ContentValues[]) r2;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3.bulkInsert(r5, r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00c3:
        r2 = r23.isEmpty();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r2 != 0) goto L_0x00d3;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00c9:
        r2 = r23.iterator();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00cd:
        r3 = r2.hasNext();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r3 != 0) goto L_0x0184;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00d3:
        if (r16 != 0) goto L_0x01b8;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00d5:
        r2 = r23.isEmpty();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r2 == 0) goto L_0x01b8;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00db:
        r15 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00dc:
        if (r15 == 0) goto L_0x00ef;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00de:
        r0 = r26;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r0.prefs;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r2.edit();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = "dataHashForSites";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r2.putString(r3, r12);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2.commit();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
    L_0x00ef:
        if (r14 == 0) goto L_0x002c;
    L_0x00f1:
        r2 = r14.isClosed();
        if (r2 != 0) goto L_0x002c;
    L_0x00f7:
        r14.close();
        goto L_0x002c;
    L_0x00fc:
        r0 = r19;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r18 = r14.getInt(r0);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = java.lang.Integer.valueOf(r18);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r23;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0.add(r2);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r14.moveToNext();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        goto L_0x0074;
    L_0x0110:
        r13 = move-exception;
        r2 = TAG;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = "Failed to store sites in DB";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        android.util.Log.e(r2, r3, r13);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r26;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r0.prefs;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r2.edit();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = "dataHash";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r2.remove(r3);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2.commit();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        if (r14 == 0) goto L_0x0134;
    L_0x012b:
        r2 = r14.isClosed();
        if (r2 != 0) goto L_0x0134;
    L_0x0131:
        r14.close();
    L_0x0134:
        r15 = 0;
        goto L_0x002c;
    L_0x0137:
        r21 = r17.next();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r21 = (java.lang.String) r21;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r20 = java.lang.Integer.valueOf(r21);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = TAG;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = "Converting site #";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3.<init>(r5);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r20;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = r3.append(r0);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = r3.toString();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        android.util.Log.d(r2, r3);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r25;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r1 = r21;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r2 = r0.getJSONObject(r1);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r26;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r1 = r20;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r9 = r0.jsonSiteToContentValues(r2, r1);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r10.add(r9);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r23;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r1 = r20;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0.remove(r1);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        goto L_0x0083;
    L_0x0173:
        r2 = move-exception;
        if (r14 == 0) goto L_0x017f;
    L_0x0176:
        r3 = r14.isClosed();
        if (r3 != 0) goto L_0x017f;
    L_0x017c:
        r14.close();
    L_0x017f:
        throw r2;
    L_0x0180:
        r16 = 0;
        goto L_0x0091;
    L_0x0184:
        r22 = r2.next();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r22 = (java.lang.Integer) r22;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = TAG;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r6 = "Deleting removed site #";	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5.<init>(r6);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r22;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = r5.append(r0);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = r5.toString();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        android.util.Log.i(r3, r5);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = uk.co.odeon.androidapp.provider.SiteContent.SiteColumns.CONTENT_URI;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = r22.intValue();	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = (long) r5;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r24 = android.content.ContentUris.withAppendedId(r3, r5);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r26;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3 = r0.contentResolver;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r5 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r6 = 0;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r0 = r24;	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        r3.delete(r0, r5, r6);	 Catch:{ JSONException -> 0x0110, all -> 0x0173 }
        goto L_0x00cd;
    L_0x01b8:
        r15 = 1;
        goto L_0x00dc;
        */
        throw new UnsupportedOperationException("Method not decompiled: uk.co.odeon.androidapp.resphandlers.AllCinemasResponseHandler.handleJSONRepsonse(org.json.JSONObject, android.net.Uri):boolean");
    }

    static {
        TAG = AllCinemasResponseHandler.class.getSimpleName();
    }

    public AllCinemasResponseHandler(ContentResolver contentResolver, SharedPreferences prefs) {
        this.contentResolver = null;
        this.contentResolver = contentResolver;
        this.prefs = prefs;
    }

    protected ContentValues jsonSiteToContentValues(JSONObject site, Integer siteId) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put("_id", siteId);
        cv.put(SiteColumns.NAME, site.getString("siteName"));
        cv.put(SiteColumns.POSTCODE, site.optString("sitePostcode"));
        cv.put(SiteColumns.PHONE, site.optString("siteTelephone"));
        cv.put(SiteColumns.LONGITUDE, site.optString("siteLongitude"));
        cv.put(SiteColumns.LATITUDE, site.optString("siteLatitude"));
        String addr = "";
        for (int a = 1; a <= 4; a++) {
            String addrLine = site.optString("siteAddress" + a);
            if (!(addrLine == null || addrLine.length() == 0 || addrLine.equals("null"))) {
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(addr));
                if (addr.length() != 0) {
                    addrLine = "\n" + addrLine;
                }
                addr = stringBuilder.append(addrLine).toString();
            }
        }
        cv.put(SiteColumns.ADDR, addr);
        return cv;
    }
}
