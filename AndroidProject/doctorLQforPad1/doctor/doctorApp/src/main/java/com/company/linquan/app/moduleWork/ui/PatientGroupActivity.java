package com.company.linquan.app.moduleWork.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.linquan.app.R;
import com.company.linquan.app.base.BaseActivity;
import com.company.linquan.app.bean.DiseaseBean;
import com.company.linquan.app.bean.DiseaseGroupBean;
import com.company.linquan.app.bean.MemberBean;
import com.company.linquan.app.bean.PatientBean;
import com.company.linquan.app.config.C;
import com.company.linquan.app.moduleWork.WorkInterface;
import com.company.linquan.app.moduleWork.imp.MemberListPresenterImp;
import com.company.linquan.app.moduleWork.imp.PatientGroupPresenterImp;
import com.company.linquan.app.util.ExitApp;
import com.company.linquan.app.util.MyDialog;
import com.company.linquan.app.util.MyToast;
import com.company.linquan.app.util.ToolSharePerference;
import com.company.linquan.app.view.MyTextView;
import com.company.linquan.app.view.RoundImageView;
import com.hb.dialog.myDialog.MyAlertInputDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.os.FileObserver.CREATE;

/**
 * Created by YC on 2018/7/15.
 */

public class PatientGroupActivity extends BaseActivity implements WorkInterface.DiseaseListInterface,View.OnClickListener{

    private Dialog myDialog;

    private RecyclerView recordRecycler;
    private ArrayList<DiseaseGroupBean> array;
    private RecordListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    int sw;
    private String docID;
    private Spinner spinner1;
    private List<String> teamList;
    private TextView textView;
    private ArrayAdapter<String> arrayAdapter;
    private PatientGroupPresenterImp presenter;
    private MyTextView rightTV1,rightTV2;
    private static String isEdit="??????";
    private final static int ADD =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addActivity(this);		// add Activity ????????????
        requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????????????????????
        setContentView(R.layout.activity_patient_list);
        initData();
        initHead();
        initView();
        setListener();
        getData();
    }

    private void getData() {
        presenter.getDiseaseGroup(docID); //??????
    }

    private void initData() {
        docID = ToolSharePerference.getStringData(getContext(), C.fileconfig,C.UserID);
    }

    private void initHead() {
        RelativeLayout head = (RelativeLayout) findViewById(R.id.head_layout);
        MyTextView title = (MyTextView) head.findViewById(R.id.head_top_title);
        title.setText("????????????");
        ImageView leftIV = (ImageView)head.findViewById(R.id.head_top_image);
        rightTV1 = (MyTextView) head.findViewById(R.id.head_top_right_text1);
        rightTV1.setText("??????");
        rightTV1.setVisibility(View.VISIBLE);
        rightTV2 = (MyTextView) head.findViewById(R.id.head_top_right_text2);
        rightTV2.setText("??????");
        rightTV2.setVisibility(View.VISIBLE);
        leftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rightTV1.setOnClickListener(this);
        rightTV2.setOnClickListener(this);

    }

    private void initView(){
        presenter = new PatientGroupPresenterImp (this);
        sw = getContext().getResources().getDisplayMetrics().widthPixels;
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.member_list_refresh);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this,R.color.base_red_color));
        recordRecycler = (RecyclerView)findViewById(R.id.member_list_recycler);
        recordRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        array = new ArrayList<>();
        adapter = new RecordListAdapter(getContext(),array);
        recordRecycler.setAdapter(adapter);
        recordRecycler.setItemAnimator(new DefaultItemAnimator());


//===========================================================================
//        teamList = new ArrayList<>();
//        initList();
////        textView = findViewById(R.id.spinner_textview);
//        spinner1 = findViewById(R.id.spinner1);
//        arrayAdapter = new ArrayAdapter<String>(PatientGroupActivity.this,android.R.layout.simple_spinner_item,teamList){
//            @NonNull
//            @Override
//            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//                return setCentered(super.getView(position, convertView, parent));
//            }
//
//            @Override
//            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
//                return setCentered(super.getDropDownView(position, convertView, parent));
//            }
//
//            private View setCentered(View view) {
//                TextView textView = view.findViewById(android.R.id.text1);
//                textView.setGravity(Gravity.CENTER);//???????????????
//                return view;
//            }
//        };

//        //???????????????????????????
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //???adapter ?????????spinner???
//        spinner1.setAdapter(arrayAdapter);
//        //??????????????????
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                textView.setText(teamList.get(i));
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//
//        textView=findViewById(R.id.search);
//        textView.setOnClickListener(this);
    }
//    public void initList(){
//        teamList.add("??????");
//        teamList.add("????????????");
//        teamList.add("????????????");
//        teamList.add("AC??????");
//    }
    private void setListener() {
        adapter.setOnItemClickListener(new OnItemClick() {
            @Override
            public void onItemClick(View view, ViewName viewName, int position) {
                switch (view.getId()){
                    case R.id.list_item_update:
                        final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(PatientGroupActivity.this).builder()
                                .setTitle("?????????????????????")
                                .setEditText("");
                        myAlertInputDialog.setPositiveButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.updateDiseaseGroup(docID,myAlertInputDialog.getResult(),array.get(position).getGroupid());
                                myAlertInputDialog.dismiss();
                            }
                        }).setNegativeButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myAlertInputDialog.dismiss();
                            }
                        });
                        myAlertInputDialog.show();
                        break;
                    case R.id.list_item_delete:
                        presenter.deleteDiseaseGroup(array.get(position).getGroupid());
                        break;
                    default:
                        Intent intent = new Intent();
                        ArrayList<DiseaseBean> diseaseBeans = new ArrayList<DiseaseBean>();
//                diseaseBeans.add(new DiseaseBean("John","","","","","",""));
                        diseaseBeans=array.get(position).getGroupInfoList();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("diseaseBeans", (Serializable) diseaseBeans);
                        intent.putExtras(bundle);
                        intent.setClass(PatientGroupActivity.this,DiseaseActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.head_top_right_text1){//????????????
            Intent intent = new Intent();
            intent.setClass(this,CreateDiseaseActivity.class);
            startActivityForResult(intent,ADD);
        }
        if(view.getId()==R.id.head_top_right_text2){//????????????
            if(rightTV2.getText()=="??????"){
                rightTV2.setText("??????");
                isEdit="??????";
            }else{
                rightTV2.setText("??????");
                isEdit="??????";
            }
            adapter.setList(array);
        }
    }



    @Override
    public void reloadList(ArrayList<DiseaseGroupBean> list) {
        refreshLayout.setRefreshing(false);
        array = list;
        adapter.setList(array);
    }

    @Override
    public void reloadDiseaseList(ArrayList<DiseaseBean> list) {

    }

    @Override
    public void reloadPatientList(ArrayList<PatientBean> list) {

    }

    @Override
    public void checkType(String json) {

    }

    @Override
    public void reload() {
        getData();
    }

    @Override
    public void showDialog() {
        if (myDialog == null) myDialog = MyDialog.showLoadingDialog(this);
        myDialog.show();
    }

    @Override
    public void dismissDialog() {
        if (myDialog != null) myDialog.dismiss();
    }

    @Override
    public void showToast(String txt) {
        MyToast.showToast(this,txt, Toast.LENGTH_SHORT);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void finishActivity() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==ADD&&resultCode==RESULT_OK){
            getData();
        }
    }

    /**
     * recycler ?????????
     */
    private class RecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private Context mContext;
        private ArrayList<DiseaseGroupBean> mList;
        private OnItemClick onItemClickListener;

        public RecordListAdapter(Context context,ArrayList<DiseaseGroupBean> list ){
            this.mContext = context;
            this.mList = list;
        }


        public void setList(ArrayList<DiseaseGroupBean> list){
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_select_data,parent,false);
            return new RecordViewHolder(view,onItemClickListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof RecordViewHolder){
                initView((RecordViewHolder) holder, mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private void setOnItemClickListener(OnItemClick listener){
            onItemClickListener = listener;
        }

        private void initView(RecordViewHolder handler, DiseaseGroupBean bean){

            if (bean == null)return;

            int w2 = (94*sw)/720;
            int h2 = (94*sw)/720;
//            Glide.with(mContext).load(bean.getMemberHeadUrl()).apply(new RequestOptions().override(w2,h2).centerCrop()).into(handler.photoIV);
            handler.titleTV.setText(bean.getGroupname());
            if("??????".equals(rightTV2.getText())){
                handler.updateTV.setVisibility(View.VISIBLE);
                handler.deleteTV.setVisibility(View.VISIBLE);
            }else {
                handler.updateTV.setVisibility(View.GONE);
                handler.deleteTV.setVisibility(View.GONE);
            }
        }
    }
    //item????????????????????????????????????item+item???????????????
    public enum ViewName {
        ITEM,
        PRACTISE
    }
    public interface OnItemClick{
        public void onItemClick(View view, ViewName viewName, int position);
    }

    /**
     * ?????????
     */
    private static class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyTextView titleTV, updateTV, deleteTV;
        private OnItemClick onItemClick;

        public RecordViewHolder(View view, OnItemClick onItemClick) {
            super(view);
            this.onItemClick = onItemClick;
            view.setOnClickListener(this);
            titleTV = (MyTextView) view.findViewById(R.id.list_item_data);
            updateTV = (MyTextView) view.findViewById(R.id.list_item_update);
            deleteTV = (MyTextView) view.findViewById(R.id.list_item_delete);
            updateTV.setOnClickListener(this);
            deleteTV.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
//            int position = (int) view.getTag();
            if (onItemClick != null) {
//                onItemClick.onItemClick(view,getLayoutPosition());
                switch (view.getId()) {
                    case R.id.member_list_recycler:
                        onItemClick.onItemClick(view, ViewName.PRACTISE, getLayoutPosition());
                        break;
                    default:
                        onItemClick.onItemClick(view, ViewName.ITEM, getLayoutPosition());
                        break;
                }
            }
        }


    }
}
