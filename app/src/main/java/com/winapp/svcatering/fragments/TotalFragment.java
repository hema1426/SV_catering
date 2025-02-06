package com.winapp.svcatering.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.winapp.svcatering.BuildConfig;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.CashCollectionActivity;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.utils.FileCompressor;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.LocationTrack;
import com.winapp.svcatering.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TotalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TotalFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView totalOutstanding;
    private TextView totalPaidAmount;
    private TextView discountAmount;
    private TextView balanceAmount;
    private TextView returnAmount;
    private Button getImage;
    private ImageView closeLayout;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    public static String imageString;
    public ImageView receiptImage;
    public ImageView deleteImage;
    public FrameLayout imageLayout;

    LocationTrack locationTrack;
    TextView locationText;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;
    DBHelper dbHelper;
    View view;

    public TotalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TotalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TotalFragment newInstance(String param1, String param2) {
        TotalFragment fragment = new TotalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (Utils.isTablet(requireActivity())){
             view=inflater.inflate(R.layout.fragment_total,container,false);
        }else {
            view=inflater.inflate(R.layout.fragment_total_mobile,container,false);
        }

        dbHelper=new DBHelper(getActivity());
        mCompressor = new FileCompressor(requireActivity());

        totalOutstanding=view.findViewById(R.id.total_outstanding);
        totalPaidAmount=view.findViewById(R.id.total_paid_amount);
        discountAmount=view.findViewById(R.id.discount_amount);
        balanceAmount=view.findViewById(R.id.balance_amount);
        returnAmount=view.findViewById(R.id.return_amount);
        closeLayout=view.findViewById(R.id.close_layout);
        getImage=view.findViewById(R.id.get_image);
        receiptImage=view.findViewById(R.id.receipt_image);
        deleteImage=view.findViewById(R.id.delete_image);
        imageLayout=view.findViewById(R.id.image_layout);

        setNetTotal();

        if (CashCollectionActivity.imageFile!=null){
            imageLayout.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(CashCollectionActivity.imageFile)
                    .into(receiptImage);
        }

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashCollectionActivity.closeSheet();
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });
        return view;
    }

    public void showAlert(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage("Are you sure want to remove image ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CashCollectionActivity.imageFile=null;
                imageLayout.setVisibility(View.GONE);
                mPhotoFile=null;
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    // Adding the function for the Image and Signature adding function to Apply the Requrements
    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {
                "Take Photo",
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } /*else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } */else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error -> Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }
    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Need Permissions");
        builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    /**
     * Create file with current timestamp name
     *
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }
    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    CashCollectionActivity.imageFile=mPhotoFile;
                    imageString= ImageUtil.getBase64StringImage(mPhotoFile);
                    //  Log.w("GivenImage1:",imageString);
                    Utils.w("GivenImage1",imageString);
                    //showImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageLayout.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(mPhotoFile)
                        .into(receiptImage);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                   // selectImage.setText(selectedImage.toString());

                    imageString=ImageUtil.getBase64StringImage(mPhotoFile);
                    //  Log.w("GivenImage1:",imageString);
                    Utils.w("GivenImage2",imageString);

                } catch (IOException e) {
                    e.printStackTrace();
                }

              /*
                Glide.with(MainActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions().centerCrop()
                                .circleCrop()
                                .placeholder(R.drawable.profile_pic_place_holder))
                        .into(imageViewProfilePic);*/
            }
        }
    }



    public void setNetTotal(){
        try {
          /*  double net_value=0.00;
            double net_discount=0.00;
            double net_outstanding=0.00;
            double paid_amount=0.00;
            double net_sales_return=0.0;
            ArrayList<CashCollectionInvoiceModel> list=dbHelper.getAllInvoices();
            for(CashCollectionInvoiceModel model:list){
                if (!model.getBalance().isEmpty()){
                    net_outstanding+=Double.parseDouble(model.getBalance());
                }
                if (!model.getNetTotal().isEmpty()){
                    net_value+=Double.parseDouble(model.getNetTotal());
                }
                if (!model.getDiscount().isEmpty()){
                    net_discount+=Double.parseDouble(model.getDiscount());
                }

                if (!model.getPayable().isEmpty()){
                    paid_amount+=Double.parseDouble(model.getPayable());
                }
            }*/
            returnAmount.setText(CashCollectionActivity.returnAmountEditText.getText().toString());
            totalOutstanding.setText(CashCollectionActivity.totalOutstanding.getText().toString());
            totalPaidAmount.setText(CashCollectionActivity.amountText.getText().toString());
            balanceAmount.setText(CashCollectionActivity.totalOutstanding.getText().toString());
            discountAmount.setText(CashCollectionActivity.totalDiscount.getText().toString());
        }catch (Exception ex){}
    }
}