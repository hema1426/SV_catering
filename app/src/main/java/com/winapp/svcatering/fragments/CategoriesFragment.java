package com.winapp.svcatering.fragments;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.adapter.CategoriesAdapter;
import com.winapp.svcatering.adapter.SortAdapter;
import com.winapp.svcatering.R;
import com.winapp.svcatering.model.AllCategories;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.GridSpacingItemDecoration;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CategoriesFragment extends Fragment {

    private View progressBarLayout;
    private CategoriesAdapter productsAdapter;
    private RecyclerView categoriesView;
    public ArrayList<AllCategories> allCategoriesList;
    SweetAlertDialog pDialog;
    private RecyclerView lettersRecyclerview;
    SortAdapter adapter;
    ArrayList<String> letters;
    private LinearLayout emptyLayout;

    HashMap<String ,String> user;
    SessionManager session;
    String companyCode;
    boolean _areLecturesLoaded = false;


    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View view=inflater.inflate(R.layout.fragment_categories, container, false);
        progressBarLayout = view.findViewById(R.id.progress_layout);
        categoriesView =view.findViewById(R.id.categoriesView);
        progressBarLayout.setVisibility(View.GONE);

        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);

        lettersRecyclerview=view.findViewById(R.id.sorting_letters);
        emptyLayout=view.findViewById(R.id.empty_layout);
        // define the sorting letters
        lettersRecyclerview.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lettersRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        SortAdapter adapter = new SortAdapter(Utils.getSorting(), new SortAdapter.CallBack() {
            @Override
            public void sortProduct(String letter) {
                if (letter.equals("All")){
                    getCategories();
                }else {
                    filter(letter);
                }
            }
        });
        lettersRecyclerview.setAdapter(adapter);

        if (HomePageModel.categoriesList !=null){
            if (HomePageModel.categoriesList.size()>0){
                populateCategoriesData(HomePageModel.categoriesList);
            }else {
                getCategories();
            }
        }else {
            getCategories();
        }


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            _areLecturesLoaded = true;
        }
    }


    public void getCategories(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url=Utils.getBaseUrl(getContext()) +"ProductApi/GetCategory_All?Requestdata={CompanyCode:"+companyCode+"}";
        // Initialize a new JsonArrayRequest instance
        allCategoriesList=new ArrayList<>();
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Categories Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject categoryObject = response.getJSONObject(i);
                            AllCategories categories=new AllCategories();
                            if (categoryObject.optBoolean("IsActive")){
                                categories.setCompanyCode(categoryObject.optString("CompanyCode"));
                                categories.setCategoryCode(categoryObject.optString("CategoryCode"));
                                categories.setCateGoryGroupName(categoryObject.optString("CategoryGroupName"));
                                categories.setDescription(categoryObject.optString("Description"));
                                categories.setDisplayOrder(categoryObject.optString("DisplayOrder"));
                                categories.setShowOnPos(categoryObject.optBoolean("ShowOnPOS"));
                                categories.setActive(categoryObject.optBoolean("IsActive"));
                                categories.setCategoryImage(categoryObject.getString("CategoryImagePath"));
                                allCategoriesList.add(categories);
                            }
                        }
                        pDialog.dismiss();
                        if (allCategoriesList.size()>0){
                            HomePageModel.categoriesList=allCategoriesList;
                            populateCategoriesData(allCategoriesList);
                        }else {
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
                    pDialog.dismiss();
                    Log.w("Error_throwing:",error.toString());
                }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }


    private void populateCategoriesData(ArrayList<AllCategories> categories) {
        try {
            productsAdapter = new CategoriesAdapter(getActivity(), categories, new CategoriesAdapter.CallBack() {
                @Override
                public void callDescription() {

                }
            });
            int mNoOfColumns = Utils.calculateNoOfColumns(getActivity(),200);
            // GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);
            categoriesView.setLayoutManager(mLayoutManager);
            // categoriesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            categoriesView.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(getActivity(),2), true));
            categoriesView.setItemAnimator(new DefaultItemAnimator());
            categoriesView.setAdapter(productsAdapter);
            categoriesView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }catch (Exception ex){
            Log.e("TAG","Error in Populating the data:"+ex.getMessage());
        }
    }

    public void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<AllCategories> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (AllCategories s : allCategoriesList) {
                //if the existing elements contains the search input
                int diff = 0;
                if (s.getCategoryName()!=null && !s.getCategoryName().isEmpty()){
                    diff=s.getCategoryName().charAt(0)-text.charAt(0);
                }else {
                    diff=s.getDescription().charAt(0)-text.charAt(0);
                }
                if (diff==0) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            if (filterdNames.size()>0){
                if (productsAdapter!=null){
                    productsAdapter.filterList(filterdNames);
                }
                productsAdapter.filterList(filterdNames);
                categoriesView.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }else {
                emptyLayout.setVisibility(View.VISIBLE);
                categoriesView.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }
}