//package com.winapp.svcatering.activity;
//
//
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.winapp.svcatering.utils.Constants;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class InvoicePdfDownload extends AsyncTask<String, Integer, String> {
////    private class InvoicePdfDownload extends AsyncTask<String, Integer, String> {
//
//        private Context c;
//        private int file_progress_count = 0;
//        File newFile;
//
//        public InvoicePdfDownload(Context c) {
//            this.c = c;
//        }
//
//        @Override
//        protected String doInBackground(String... sUrl) {
//            InputStream is = null;
//            OutputStream os = null;
//            HttpURLConnection con = null;
//            int length;
//            try {
//                URL url = new URL(sUrl[0]);
//                con = (HttpURLConnection) url.openConnection();
//                con.connect();
//
//                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    return "HTTP CODE: " + con.getResponseCode() + " " + con.getResponseMessage();
//                }
//
//                length = con.getContentLength();
//                pd.setMax(length / (1000));
//                is = con.getInputStream();
//
//                Log.w("DownloadImageURL:", url.toString());
//
//                //String folderPath = Environment.getExternalStorageDirectory() + "/CatalogErp/Products";
//                File folder = new File(Constants.getFolderPath(NewInvoiceListActivity.this));
//                if (!folder.exists()) {
//                    File productsDirectory = new File(Constants.getFolderPath(NewInvoiceListActivity.this));
//                    productsDirectory.mkdirs();
//                }
//
//                //create a new file
//                String filepath = sUrl[1] + "_" + sUrl[2];
//                String newfilePath = filepath.replace("/", "_");
//                newFile = new File(Constants.getFolderPath(NewInvoiceListActivity.this), newfilePath + ".pdf");
//                if (newFile.exists()) {
//                    newFile.delete();
//                }
//                Log.w("GivenFilePath:", newFile.toString());
//                newFile.createNewFile();
//                //os = new FileOutputStream(Environment.getExternalStorageDirectory()+File.separator+"CatalogImages" + File.separator + "a-computer-engineer.jpg");
//                os = new FileOutputStream(newFile);
//                byte data[] = new byte[4096];
//                long total = 0;
//                int count;
//                while ((count = is.read(data)) != -1) {
//                    if (isCancelled()) {
//                        is.close();
//                        return null;
//                    }
//                    total += count;
//                    if (length > 0) {
//                        publishProgress((int) total);
//                    }
//                    this.file_progress_count = (int) ((100 * total) / ((long) length));
//                    os.write(data, 0, count);
//                }
//            } catch (Exception e) {
//                Log.w("File_Write_Error:", e.getMessage());
//                return e.toString();
//            } finally {
//                try {
//                    if (os != null)
//                        os.close();
//                    if (is != null)
//                        is.close();
//                } catch (IOException ioe) {
//                }
//                if (con != null)
//                    con.disconnect();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd.setMessage("Downloading Pdf..");
//            pd.show();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            super.onProgressUpdate(progress);
//            pd.setIndeterminate(false);
//            pd.setProgress(progress[0] / 1000);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            try {
//                Log.w("ProgressCount:", file_progress_count + "");
//                if (file_progress_count == 100) {
//                    pd.dismiss();
//                    if (newFile.exists()) {
//                        if (shareMode.equals("Share")) {
//                            pdfFile = newFile;
//                            displayFromAsset(newFile);
//                        } else {
//                            shareWhatsapp(newFile);
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(), "NO file Download", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                if (result != null) {
//
//                }
//            } catch (Exception exception) {
//            }
//        }
//    }
//
////}
