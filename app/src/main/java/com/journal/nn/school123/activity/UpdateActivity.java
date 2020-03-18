package com.journal.nn.school123.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.journal.nn.school123.BuildConfig;
import com.journal.nn.school123.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import static android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR;
import static android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES;
import static com.journal.nn.school123.rest.update.Update.INDEX_ADDRESS;
import static com.journal.nn.school123.rest.update.Update.ZORAS_ADDRESS;
import static com.journal.nn.school123.util.UpdateUtil.startUsersActivity;

public class UpdateActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    private ProgressDialog progressDialog;
    private String version;
    private String fileName;
    private boolean forceUpgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent intent = getIntent();
        String date = intent.getStringExtra(TransferConstants.DATE);
        version = String.valueOf(intent.getIntExtra(TransferConstants.VERSION, BuildConfig.VERSION_CODE));
        fileName = String.valueOf(intent.getStringExtra(TransferConstants.FILE_NAME));
        forceUpgrade = intent.getBooleanExtra(TransferConstants.FORCE_UPGRADE, false);
        String description = intent.getStringExtra(TransferConstants.DESCRIPTION);
        description = description.replace("\n", "<br>");
        Button continueButton = findViewById(R.id.do_continue);

        StringBuilder builder = new StringBuilder();
        builder.append("<br><br>");
        if (forceUpgrade) {
            continueButton.setVisibility(View.INVISIBLE);
            builder.append("<b>Обнаружено критическое обновление!</b>").append("<br>")
                    .append("Либо удалите приложение и установите его заново вручную с <a href='" + INDEX_ADDRESS + "'>сайта</a>, " +
                            "либо нажмите кнопку внизу для обновления в автоматическом режиме");
        } else {
            continueButton.setOnClickListener(view -> startUsersActivity(this));
            builder.append("<b>Обнаружено обновление!</b>");
        }
        builder.append("<br><br>")
                .append("<b>Новая версия:</b> ").append(version).append("<br><br>")
                .append("<b>Текущая версия:</b> ").append(BuildConfig.VERSION_CODE).append("<br><br>")
                .append("<b>Дата обновления:</b> ").append(date).append("<br><br>")
                .append("<b>Изменения:</b> ").append(description);
        TextView versionInfoView = findViewById(R.id.version_info);
        versionInfoView.setText(Html.fromHtml(builder.toString()));
        versionInfoView.setMovementMethod(LinkMovementMethod.getInstance());
        Button updateButton = findViewById(R.id.do_update);
        updateButton.setOnClickListener(view -> checkGrants());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загружаем обновление...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
    }

    private void checkGrants() {
        int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            download();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download();
                } else {
                    Toast.makeText(this, getString(R.string.no_grants_to_write_to_storage), Toast.LENGTH_LONG).show();
                    startUsersActivity(this);
                }
            }
        }
    }

    private void download() {
        File file = getFile();
        if (file.exists()) {
            startUpgradeActivity();
        } else {
            String url = ZORAS_ADDRESS + version + "/" + fileName;
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String name = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url));
            request.setTitle(name);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            long id = downloadManager.enqueue(request);
            DownloadTask downloadTask = new DownloadTask(downloadManager,
                    progressDialog,
                    this,
                    id);
            downloadTask.execute();
        }
    }

    @Override
    public void onBackPressed() {
        if (!forceUpgrade) {
            startUsersActivity(this);
        }
    }

    private void startUpgradeActivity() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File file = getFile();
        Uri uri = FileProvider.getUriForFile(this, getPackageName(), file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        finishAffinity();
        startActivity(intent);
    }

    private File getFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
    }

    private static class DownloadTask extends AsyncTask<Void, Integer, Boolean> {
        private final DownloadManager downloadManager;
        private final ProgressDialog progressDialog;
        private final WeakReference<UpdateActivity> weakActivity;
        private final Long id;

        DownloadTask(DownloadManager downloadManager,
                     ProgressDialog progressDialog,
                     UpdateActivity activity,
                     Long id) {
            this.downloadManager = downloadManager;
            this.progressDialog = progressDialog;
            this.weakActivity = new WeakReference<>(activity);
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            while (!isCancelled()) {
                synchronized (this) {
                    try {
                        long endTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.toMillis(100);
                        wait(endTime - System.currentTimeMillis());
                        Cursor cursor = getCursor();
                        int downloaded = cursor.getInt(cursor.getColumnIndex(COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int total = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_SIZE_BYTES));
                        int progress = (int) ((downloaded * 100L) / total);
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(() -> progressDialog.setProgress(progress));
                        }
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        cursor.close();
                        switch (status) {
                            case DownloadManager.STATUS_SUCCESSFUL:
                                return true;
                        }
                    } catch (Exception e) {
                        System.out.println("Could not interrupt:" + e);
                    }
                }
            }
            return false;
        }

        @NonNull
        private Cursor getCursor() {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(id);
            Cursor cursor = downloadManager.query(q);
            cursor.moveToFirst();
            return cursor;
        }

        @Nullable
        private UpdateActivity getActivity() {
            UpdateActivity activity = weakActivity.get();
            if (activity != null
                    && !activity.isFinishing()
                    && !activity.isDestroyed()) {
                return activity;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setIndeterminate(false);
            progressDialog.setOnCancelListener(dialog -> cancel(true));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            UpdateActivity activity = getActivity();
            if (activity != null) {
                if (result) {
                    activity.startUpgradeActivity();
                } else {
                    Toast.makeText(activity, activity.getString(R.string.download_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
