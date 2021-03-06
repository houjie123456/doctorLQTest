package com.company.wanbei.app.moduleWork.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.wanbei.app.base.BaseActivity;
import com.company.wanbei.app.bean.HomeCareBean;
import com.company.wanbei.app.bean.NurseListBean;
import com.company.wanbei.app.bean.NurseServiceBean;
import com.company.wanbei.app.bean.OperationBean;
import com.company.wanbei.app.bean.OperationListBean;
import com.company.wanbei.app.bean.OperationScheduleBean;
import com.company.wanbei.app.bean.UserInfoBean;
import com.company.wanbei.app.moduleWork.WorkInterface;
import com.company.wanbei.app.moduleWork.imp.WorkNursePresenterImp;
import com.company.wanbei.app.tim.VisitInfomation;
import com.company.wanbei.app.tim.utils.TUIUtils;
import com.company.wanbei.app.util.ExitApp;
import com.tencent.qcloud.tuikit.tuichat.fromApp.util.MyDialog;
import com.tencent.qcloud.tuikit.tuichat.fromApp.util.MyToast;
import com.tencent.qcloud.tuikit.tuichat.fromApp.view.MyTextView;
import com.tencent.qcloud.tuikit.tuichat.fromApp.view.RoundImageView;
import com.company.wanbei.app.R;
import com.hb.dialog.myDialog.MyAlertDialog;
import com.tencent.imsdk.v2.V2TIMConversation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.tencent.qcloud.tuikit.tuichat.fromApp.config.C.INQUERYIDS;
import static com.tencent.qcloud.tuikit.tuichat.fromApp.config.C.VISIDS;
import static com.tencent.qcloud.tuikit.tuichat.fromApp.config.C.VISNAMES;

public class WorkNurseActivity extends BaseActivity implements WorkInterface.NurseInterface,View.OnClickListener{
    private Dialog myDialog;

    private RadioGroup radioGroup_gender;
    private String id,state;

    private RecyclerView recyclerView, recordRecycler,createRecycler;
    private ArrayList<NurseServiceBean> array;
    private SwipeRefreshLayout refreshLayout,createRefresh;
    private MyListAdapter adapter;
    private ArrayList<HomeCareBean> arrayList;
    private ArrayList<NurseListBean> arrayCreate;
    private RecordListAdapter recordListAdapter;
    private CreateRecordListAdapter createRecordListAdapter;
    private WorkNursePresenterImp presenter;
    private MyTextView nurseRecordTV,createRecordTV,caseTV;
    int sw;

    private String confirmState;

    private int topPosition, topIndex,page=1,pageCreate=1;

    private static final int STOP = 1;
    private static final int CONFIRM=2;
    private static final int UPDATE=3,CREATE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addActivity(this);		// add Activity ????????????
//        requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????????????????????
        setContentView(R.layout.activity_work_nurse);
        initHead();
        initLeftView();
        initView();
        getData();
        setListener();
    }


    private void initHead() {
        RelativeLayout head = (RelativeLayout) findViewById(R.id.layout_head);
        MyTextView title = (MyTextView)head. findViewById(R.id.head_top_title);
        title.setText("????????????");
        ImageView leftIV = (ImageView)head.findViewById(R.id.head_top_image);
        leftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        MyTextView rightTV = (MyTextView)head.findViewById(R.id.head_top_right_text2);
        rightTV.setText("????????????");
        rightTV.setTextColor(getResources().getColor(R.color.colorBtn));
        rightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                intent1.setClass(WorkNurseActivity.this, NurseHistoryActivity.class);
                startActivity(intent1);
            }
        });
    }

    private void initLeftView() {
        LinearLayout leftLayout = (LinearLayout)findViewById(R.id.nurse_left_layout);
        MyTextView txt1 = (MyTextView)leftLayout.findViewById(R.id.list_item_week);
        txt1.setText("??????");

        MyTextView txt2 = (MyTextView)leftLayout.findViewById(R.id.list_item_month);
        txt2.setVisibility(View.GONE);

        LinearLayout top1 = (LinearLayout)leftLayout.findViewById(R.id.list_item_right_top_layout);
        top1.setVisibility(View.GONE);

        MyTextView txt3 = (MyTextView)leftLayout.findViewById(R.id.list_item_title_second);
        txt3.setText("??????");

        ImageView image1 = (ImageView) leftLayout.findViewById(R.id.list_item_image_second);
        image1.setVisibility(View.GONE);

        MyTextView txt4 = (MyTextView)leftLayout.findViewById(R.id.list_item_title_three);
        txt4.setText("??????");

        ImageView image2 = (ImageView) leftLayout.findViewById(R.id.list_item_image_three);
        image2.setVisibility(View.GONE);

    }

    private void initView(){

        sw = getContext().getResources().getDisplayMetrics().widthPixels;

        presenter = new WorkNursePresenterImp(this);

        sw = getContext().getResources().getDisplayMetrics().widthPixels;
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.work_nurse_refresh);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this,R.color.base_red_color));
        refreshLayout.setVisibility(View.VISIBLE);

        createRefresh = (SwipeRefreshLayout)findViewById(R.id.work_create_refresh);
        createRefresh.setColorSchemeColors(ContextCompat.getColor(this,R.color.base_red_color));
        createRefresh.setVisibility(View.GONE);

        caseTV=findViewById(R.id.work_nurse_txt);
        caseTV.setText("????????????");

        recyclerView = (RecyclerView)findViewById(R.id.work_nurse_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(WorkNurseActivity.this,LinearLayoutManager.HORIZONTAL, false));
        arrayList = new ArrayList<>();
        adapter = new MyListAdapter(getContext(),arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recordRecycler = (RecyclerView)findViewById(R.id.work_nurse_record_recycler);
        recordRecycler.setLayoutManager(new LinearLayoutManager(WorkNurseActivity.this,RecyclerView.VERTICAL, false));
        array = new ArrayList<>();
        recordListAdapter = new RecordListAdapter(getContext(),array);
        recordRecycler.setAdapter(recordListAdapter);
        recordRecycler.setItemAnimator(new DefaultItemAnimator());

        recordRecycler.setVisibility(View.VISIBLE);

        createRecycler = (RecyclerView)findViewById(R.id.work_create_record_recycler);
        createRecycler.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));
        arrayCreate = new ArrayList<>();
        createRecordListAdapter = new CreateRecordListAdapter(getContext(),arrayCreate);
        createRecycler.setAdapter(createRecordListAdapter);
        createRecycler.setItemAnimator(new DefaultItemAnimator());


        nurseRecordTV=findViewById(R.id.nurse_record_tv);
        nurseRecordTV.setTag(R.drawable.shape_corner_btn_group1);
        createRecordTV=findViewById(R.id.create_record_tv);
        createRecordTV.setTag(R.drawable.shape_corner_btn_group2);
        nurseRecordTV.setOnClickListener(this);
        createRecordTV.setOnClickListener(this);

        findViewById(R.id.work_nurse_mid_layout).setOnClickListener(this);
    }

    private void getData() {
        presenter.getNurseList();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                presenter.getNurseRecordList(arrayList.get(topPosition).getDateStr(),topIndex+"");
            }
        });
        createRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCreate=1;
                presenter.getStopNurseRecordList(arrayList.get(topPosition).getDateStr(),topIndex+"");
            }
        });
        recordRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //?????????????????????????????????????????????????????????????????????
            boolean isSlidingToLast = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // ???????????????
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //?????????????????????????????????ItemPosition
                    int lastVisiblePositions = manager.findLastVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    // ???????????????????????????
                    if (lastVisiblePositions >= (totalItemCount -2) && isSlidingToLast) {
                        //???????????????????????????
                        page = page + 1;
                        presenter.getNurseRecordList(arrayList.get(topPosition).getDateStr(),topIndex+"");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx?????????????????????????????????dy??????????????????????????????
                if(dy > 0){
                    //??????0???????????????????????????
                    isSlidingToLast = true;
                }else{
                    //????????????0 ???????????????????????????
                    isSlidingToLast = false;
                }
            }
        });
        createRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //?????????????????????????????????????????????????????????????????????
            boolean isSlidingToLast = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // ???????????????
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //?????????????????????????????????ItemPosition
                    int lastVisiblePositions = manager.findLastVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    // ???????????????????????????
                    if (lastVisiblePositions >= (totalItemCount -2) && isSlidingToLast) {
                        //???????????????????????????
                        pageCreate = pageCreate + 1;
                        presenter.getStopNurseRecordList(arrayList.get(topPosition).getDateStr(),topIndex+"");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx?????????????????????????????????dy??????????????????????????????
                if(dy > 0){
                    //??????0???????????????????????????
                    isSlidingToLast = true;
                }else{
                    //????????????0 ???????????????????????????
                    isSlidingToLast = false;
                }
            }
        });
        createRecordListAdapter.setOnItemClickListener(new WorkNurseActivity.OnItemClick() {
            @Override
            public void onItemClick(View view, int position, int index) {
                if (position < 0){
                    return;
                }

                int s=view.getId();
                int l=R.id.list_item_stop_layout;
                if(view.getId()==R.id.list_item_stop_layout){
                    //-????????????   ??? 0.????????? 1.????????????
                    if ("1".equals(arrayCreate.get(position).getAppointState())){
                        showToast("???????????????????????????????????????????????????");
                        return;
                    }
                    final MyAlertDialog alertDialog=new MyAlertDialog(WorkNurseActivity.this).builder().setTitle("???????????????????????????");
                    alertDialog.setPositiveButton("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.stopNurse(arrayCreate.get(position).getId());
                        }
                    }).setNegativeButton("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }

            }
        });

        adapter.setOnItemClickListener(new WorkNurseActivity.OnItemClick() {
            @Override
            public void onItemClick(View view, int position, int index) {

                if (position < 0){
                    return;
                }
                topPosition=position;
                topIndex=index;
                for(int i=0;i<arrayList.size();i++){
                    if(i == position){
                        arrayList.get(i).setSelected(true);
                    }else {
                        arrayList.get(i).setSelected(false);
                    }
                }
                adapter.setList(arrayList);
                if(caseTV.getText().equals("????????????")){
                    presenter.getNurseRecordList(arrayList.get(position).getDateStr(), String.valueOf(index));
                }else {
                    presenter.getStopNurseRecordList(arrayList.get(position).getDateStr(), String.valueOf(index));
                }
            }
        });
        recordListAdapter.setOnItemClickListener(new OnItemClick() {
            @Override
            public void onItemClick(View view, int position, int index) {
                if (position < 0){
                    return;
                }
                if(view.getId()==R.id.list_item_update_state){
                    if(array.get(position).getServicestate().equals("2")){
                        showToast("?????????????????????????????????????????????");
                        return;
                    }
                    if(array.get(position).getServicestate().equals("3")){
                        showToast("?????????????????????????????????????????????");
                        return;
                    }
                    if(array.get(position).getCheckState().equals("0")){
                        showToast("????????????????????????????????????????????????");
                        return;
                    }
                    if(array.get(position).getCheckState().equals("2")){
                        showToast("??????????????????????????????????????????????????????");
                        return;
                    }
                    //???????????? 0.????????? 1.????????? 2.?????? 3.??????????????????
                    id = array.get(position).getId();
                    state = array.get(position).getServicestate();
                    showTwoBtnDialog("??????????????????",state);
                }
                if(view.getId()==R.id.list_item_contact){
                    if(array.get(position).getServicestate().equals("2")){
                        showToast("???????????????????????????????????????");
                        return;
                    }
                    if(array.get(position).getServicestate().equals("3")){
                        showToast("???????????????????????????????????????");
                        return;
                    }
                    if(array.get(position).getCheckState().equals("2")){
                        showToast("?????????????????????????????????????????????");
                        return;
                    }
                    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//??????
                    try {
//                        Date date=format.parse(array.get(position).getAppointstarttime());//??????????????????????????????
                        Date date1=format.parse(array.get(position).getAppointendtime());//??????????????????????????????
                        String datestr=format.format(new Date());
                        Date datecurrent=format.parse(datestr);//???????????????????????????
                        if (datecurrent.getTime()>date1.getTime()){//??????
                            showToast("??????????????????????????????????????????");
                        }else {
                            if(array.get(position).getAccountID()!=null&&array.get(position).getAccountID()!=""){
                                VisitInfomation.getInstance().setBespeakId(array.get(position).getId());
                                UserInfoBean.getInstance().setWyyID(array.get(position).getAccountID());
                                VisitInfomation.getInstance().setWyyID(array.get(position).getAccountID());
//                                ToolSharePerference.putStringData(getContext(),"doctor","bespeakID",array.get(position).getId());
                                VisitInfomation.getInstance().setBespeakId(array.get(position).getId());
                                INQUERYIDS=new ArrayList<>();
                                VISIDS=new ArrayList<>();
                                VISNAMES=new ArrayList<>();
                                TUIUtils.startChat(array.get(position).getAccountID(), array.get(position).getPatientName(), V2TIMConversation.V2TIM_C2C);
                            }else {
                                showToast("??????????????????");
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(view.getId()==R.id.nurse_record_info){
//                    if(array.get(position).getServicestate().equals("2")){
//                        showToast("?????????????????????????????????");
//                        return;
//                    }
//                    if(array.get(position).getServicestate().equals("3")){
//                        showToast("?????????????????????????????????");
//                        return;
//                    }
                    Intent intent = new Intent();
                    intent.setClass(WorkNurseActivity.this, ConfirmNurseActivity.class);

                    intent.putExtra("visitHead",array.get(position).getPatientHeadUrl());
                    intent.putExtra("id",array.get(position).getId());
                    intent.putExtra("patientName",array.get(position).getPatientName());
                    intent.putExtra("phonenum",array.get(position).getPhonenum());
                    intent.putExtra("age",array.get(position).getAge());
                    intent.putExtra("appointstarttime",array.get(position).getAppointstarttime());
                    intent.putExtra("appointendtime",array.get(position).getAppointendtime());
                    intent.putExtra("address",array.get(position).getAddress());
                    intent.putExtra("amount",array.get(position).getAmount());
                    intent.putExtra("paystate",array.get(position).getPaystate());
                    intent.putExtra("servicestate",array.get(position).getServicestate());
                    intent.putExtra("illnessdescr",array.get(position).getIllnessdescr());
                    intent.putExtra("sex",array.get(position).getSex());
                    intent.putExtra("birthdate",array.get(position).getBirthdate());
                    intent.putExtra("idcardno",array.get(position).getIdcardno());
                    intent.putExtra("idCardFront",array.get(position).getIdCardFront());
                    intent.putExtra("idCardAgainst",array.get(position).getIdCardAgainst());
                    intent.putExtra("orderid",array.get(position).getOrderid());
                    intent.putExtra("describe",array.get(position).getDescribe());
                    intent.putExtra("checkRemark",array.get(position).getCheckRemark());
                    intent.putExtra("confirmState",array.get(position).getCheckState());

                    intent.putExtra("position",position);
                    startActivityForResult(intent,CONFIRM);
                }
                if(view.getId()==R.id.list_item_confirm){
                    if(array.get(position).getServicestate().equals("2")){
                        showToast("?????????????????????????????????");
                        return;
                    }
                    if(array.get(position).getServicestate().equals("3")){
                        showToast("?????????????????????????????????");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(WorkNurseActivity.this, ConfirmNurseActivity.class);

                    intent.putExtra("visitHead",array.get(position).getPatientHeadUrl());
                    intent.putExtra("id",array.get(position).getId());
                    intent.putExtra("patientName",array.get(position).getPatientName());
                    intent.putExtra("phonenum",array.get(position).getPhonenum());
                    intent.putExtra("age",array.get(position).getAge());
                    intent.putExtra("appointstarttime",array.get(position).getAppointstarttime());
                    intent.putExtra("appointendtime",array.get(position).getAppointendtime());
                    intent.putExtra("address",array.get(position).getAddress());
                    intent.putExtra("amount",array.get(position).getAmount());
                    intent.putExtra("paystate",array.get(position).getPaystate());
                    intent.putExtra("servicestate",array.get(position).getServicestate());
                    intent.putExtra("illnessdescr",array.get(position).getIllnessdescr());
                    intent.putExtra("sex",array.get(position).getSex());
                    intent.putExtra("birthdate",array.get(position).getBirthdate());
                    intent.putExtra("idcardno",array.get(position).getIdcardno());
                    intent.putExtra("idCardFront",array.get(position).getIdCardFront());
                    intent.putExtra("idCardAgainst",array.get(position).getIdCardAgainst());
                    intent.putExtra("orderid",array.get(position).getOrderid());
                    intent.putExtra("describe",array.get(position).getDescribe());
                    intent.putExtra("checkRemark",array.get(position).getCheckRemark());
                    intent.putExtra("confirmState",array.get(position).getCheckState());

                    intent.putExtra("position",position);
                    startActivityForResult(intent,CONFIRM);
                }
            }
        });
    }
    public void showTwoBtnDialog(String txt,String serviceState){
        final Dialog mDialog = new Dialog(this, R.style.custom_dialog);
        final View mDialogContentView= LayoutInflater.from(this).inflate(R.layout.dialog_item_update_state,null);

        MyTextView albumTV = (MyTextView)mDialogContentView.findViewById(R.id.dialog_item_sure);
        MyTextView cameraTV = (MyTextView)mDialogContentView.findViewById(R.id.dialog_item_cancel);
        cameraTV.setVisibility(View.GONE);
        MyTextView contentTV = (MyTextView)mDialogContentView.findViewById(R.id.dialog_item_title);
        contentTV.setText(txt);

        this.radioGroup_gender= (RadioGroup)mDialogContentView.findViewById(R.id.radioGroup_gender);

        RadioButton radioButton0 = (RadioButton)mDialogContentView.findViewById(R.id.radioButton0);
        RadioButton radioButton1 = (RadioButton)mDialogContentView.findViewById(R.id.radioButton1);
        RadioButton radioButton3 = (RadioButton)mDialogContentView.findViewById(R.id.radioButton3);
        if(serviceState.equals("0")){
            radioButton0.setChecked(true);
            radioButton1.setChecked(false);
            radioButton3.setChecked(false);
        }
        if(serviceState.equals("1")){
            radioButton1.setChecked(true);
            radioButton0.setChecked(false);
            radioButton3.setChecked(false);
        }
        if(serviceState.equals("3")){
            radioButton3.setChecked(true);
            radioButton1.setChecked(false);
            radioButton0.setChecked(false);
        }

        this.radioGroup_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton0:
                        state="0";
                        break;
                    case R.id.radioButton1:
                        state="1";
                        break;
                    case R.id.radioButton3:
                        state="3";
                        break;
                }
            }
        });
        cameraTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                cancelBtnClick();
            }
        });

        albumTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                presenter.UpdateServiceState(id,state);
            }
        });

        mDialog.setContentView(mDialogContentView);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = mDialog.getWindow().getAttributes(); // ?????????????????????????????????
        p.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // ????????????????????????0.85
        mDialog.getWindow().setAttributes(p);

        mDialog.show();
        mDialog.getWindow().setWindowAnimations(R.style.anim_dialog);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nurse_record_tv:
                int r1 = R.drawable.shape_corner_btn_group1;
                int r2 = (int) nurseRecordTV.getTag();
                if(r1==r2){
                    nurseRecordTV.setBackgroundResource(R.drawable.shape_corner_btn_group1_click);
                    nurseRecordTV.setTextColor(getResources().getColor(R.color.white));
                    createRecordTV.setBackgroundResource(R.drawable.shape_corner_btn_group2);
                    createRecordTV.setTextColor(getResources().getColor(R.color.colorBtn));

                    caseTV.setText("????????????");
                    page = 1;
                    presenter.getNurseRecordList(arrayList.get(topPosition).getDateStr(),topIndex+"");
                    refreshLayout.setVisibility(View.VISIBLE);
                    createRefresh.setVisibility(View.GONE);
                }
                break;
            case R.id.create_record_tv:
                int r3 = R.drawable.shape_corner_btn_group2;
                int r4 = (int) createRecordTV.getTag();
                if(r3==r4){
                    createRecordTV.setBackgroundResource(R.drawable.shape_corner_btn_group2_click);
                    createRecordTV.setTextColor(getResources().getColor(R.color.white));
                    nurseRecordTV.setBackgroundResource(R.drawable.shape_corner_btn_group1);
                    nurseRecordTV.setTextColor(getResources().getColor(R.color.colorBtn));

                    caseTV.setText("????????????");
                    pageCreate=1;
                    presenter.getStopNurseRecordList(arrayList.get(topPosition).getDateStr(),topIndex+"");
                    refreshLayout.setVisibility(View.GONE);
                    createRefresh.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.work_nurse_mid_layout:
                startActivityForResult(new Intent(this, CreateNurseActivity.class),CREATE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE||requestCode == CONFIRM||requestCode == UPDATE){
            getData();
            if(caseTV.getText().equals("????????????")){
                presenter.getNurseRecordList(arrayList.get(topPosition).getDateStr(), String.valueOf(topIndex));
            }else {
                presenter.getStopNurseRecordList(arrayList.get(topPosition).getDateStr(), String.valueOf(topIndex));
            }
        }
//        if (requestCode == CONFIRM && resultCode == RESULT_OK){
//            int position= Integer.parseInt(data.getStringExtra("position"));
//            array.get(position).setConfirmstate("?????????");
//            getData();
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * recycler ?????????
     */
    private class MyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private Context mContext;
        private ArrayList<HomeCareBean> mList;
        private WorkNurseActivity.OnItemClick onItemClickListener;

        public MyListAdapter(Context context,ArrayList<HomeCareBean> list ){
            this.mContext = context;
            this.mList = list;
        }


        public void setList(ArrayList<HomeCareBean> list){
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_nurse,parent,false);
            return new MyViewHolder(view,onItemClickListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof MyViewHolder){
                initView((MyViewHolder) holder, mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private void setOnItemClickListener(WorkNurseActivity.OnItemClick listener){
            onItemClickListener = listener;
        }

        private void initView(MyViewHolder handler, HomeCareBean bean){
            if (bean == null) return;
            handler.weekTV.setText(bean.getWeekStr());
            handler.monthTV.setText(bean.getDateStr().substring(5,bean.getDateStr().length()));
            if ("0".equals(bean.getAfternoonSign()) && "0".equals(bean.getMorningSign())){
                handler.rightTopLayout.setVisibility(View.GONE);
            }else{
                handler.rightTopLayout.setVisibility(View.VISIBLE);
            }

            if ("1".equals(bean.getMorningSign())){
                handler.titleSecondTV.setVisibility(View.VISIBLE);
                handler.titleSecondTV.setText(bean.getMorning()+"/"+bean.getAllNumberMorning());
                handler.imageSecondIV.setVisibility(View.GONE);
                if (bean.isSelected() && topIndex == 1){
                    handler.titleSecondTV.setTextColor(getResources().getColor(R.color.colorBtn));
                }else{
                    handler.titleSecondTV.setTextColor(getResources().getColor(R.color.black));
                }
            }else{
                handler.titleSecondTV.setVisibility(View.GONE);
                handler.imageSecondIV.setVisibility(View.VISIBLE);
            }

            if ("1".equals(bean.getAfternoonSign())){
                handler.titleThreeTV.setVisibility(View.VISIBLE);
                handler.titleThreeTV.setText(bean.getAfternoon()+"/"+bean.getAllNumberAfternoon());
                handler.imageThreeIV.setVisibility(View.GONE);
                if (bean.isSelected() && topIndex == 2){
                    handler.titleThreeTV.setTextColor(getResources().getColor(R.color.colorBtn));
                }else{
                    handler.titleThreeTV.setTextColor(getResources().getColor(R.color.black));
                }
            }else{
                handler.titleThreeTV.setVisibility(View.GONE);
                handler.imageThreeIV.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnItemClick{
        public void onItemClick(View view, int position, int index);
    }

    /**
     * ?????????
     */
    private static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public MyTextView weekTV;
        public LinearLayout rightTopLayout;
        public MyTextView monthTV;
        public MyTextView titleSecondTV;
        public MyTextView titleThreeTV;
        public ImageView imageSecondIV;
        public ImageView imageThreeIV;
        private WorkNurseActivity.OnItemClick onItemClick;

        public MyViewHolder(View view, WorkNurseActivity.OnItemClick onItemClick){
            super(view);
            this.onItemClick = onItemClick;
            weekTV = (MyTextView) view.findViewById(R.id.list_item_week);
            monthTV = (MyTextView) view.findViewById(R.id.list_item_month);
            titleSecondTV = (MyTextView) view.findViewById(R.id.list_item_title_second);
            titleThreeTV = (MyTextView) view.findViewById(R.id.list_item_title_three);
            imageSecondIV = (ImageView)view.findViewById(R.id.list_item_image_second);
            imageThreeIV = (ImageView)view.findViewById(R.id.list_item_image_three);
            rightTopLayout = (LinearLayout) view.findViewById(R.id.list_item_right_top_layout);
            view.findViewById(R.id.list_item_second_layout).setOnClickListener(this);
            view.findViewById(R.id.list_item_three_layout).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onItemClick != null && getLayoutPosition() >= 0){

                if (view.getId() == R.id.list_item_second_layout){
                    onItemClick.onItemClick(view,getLayoutPosition(),1);
                }

                if (view.getId() == R.id.list_item_three_layout){
                    onItemClick.onItemClick(view,getLayoutPosition(),2);
                }
            }
        }
    }


    /**
     * recycler ?????????
     */
    private class RecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private Context mContext;
        private ArrayList<NurseServiceBean> mList;
        private WorkNurseActivity.OnItemClick onItemClickListener;

        public RecordListAdapter(Context context,ArrayList<NurseServiceBean> list ){
            this.mContext = context;
            this.mList = list;
        }


        public void setList(ArrayList<NurseServiceBean> list){
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_work_nurse_record,parent,false);
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

        private void setOnItemClickListener(WorkNurseActivity.OnItemClick listener){
            onItemClickListener = listener;
        }


        private void initView(RecordViewHolder handler, NurseServiceBean bean){
            if (bean == null) return;
            int w2 = (94*sw)/720;
            int h2 = (94*sw)/720;
            Glide.with(mContext).load(bean.getPatientHeadUrl()).apply(new RequestOptions().override(w2,h2).centerCrop()).into(handler.imageView);

            handler.timeTV.setText(bean.getAppointstarttime()+"-"+bean.getAppointendtime().substring(10,bean.getAppointendtime().length()));

            handler.nameTV.setText(bean.getPatientName());

            handler.sexTV.setText(bean.getSex());
            handler.ageTV.setText(bean.getAge()+"???");
            /**
             * TODO switch case??????????????????
             */
            switch (bean.getCheckState()){
                case "0":
                    handler.confirmStateTV.setText("?????????");
                    handler.confirmTV.setVisibility(View.VISIBLE);
                    handler.updateStateTV.setVisibility(View.GONE);
                    handler.contactTV.setVisibility(View.VISIBLE);
                    break;
                case "1":
                    handler.confirmStateTV.setText("?????????");
                    handler.confirmTV.setVisibility(View.GONE);
                    handler.updateStateTV.setVisibility(View.VISIBLE);
                    handler.contactTV.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    handler.confirmStateTV.setText("?????????");
//                    handler.confirmTV.setVisibility(View.VISIBLE);
                    handler.contactTV.setVisibility(View.GONE);
                    handler.confirmTV.setVisibility(View.GONE);
                    handler.updateStateTV.setVisibility(View.GONE);
                    break;
            }
            //???0.????????? 1.????????? 2.?????? 3.?????????
            switch (bean.getServicestate()){
                case "0":
                    handler.stateTV.setText("?????????");
                    handler.stateTV.setTextColor(getResources().getColor(R.color.color_blue_3a9efb));
                    break;
                case "1":
                    handler.stateTV.setText("?????????");
                    handler.stateTV.setTextColor(getResources().getColor(R.color.color_green));
                    break;
                case "2":
                    handler.stateTV.setText("?????????");
                    handler.stateTV.setTextColor(getResources().getColor(R.color.color_red_ccfa3c55));
                    handler.contactTV.setVisibility(View.GONE);
                    handler.confirmTV.setVisibility(View.GONE);
                    handler.updateStateTV.setVisibility(View.GONE);
                    break;
                case "3":
                    handler.stateTV.setText("??????");
                    handler.stateTV.setTextColor(getResources().getColor(R.color.color_grey_888888));
                    handler.contactTV.setVisibility(View.GONE);
                    handler.confirmTV.setVisibility(View.GONE);
                    handler.updateStateTV.setVisibility(View.GONE);
                    break;
            }
//            handler.confirmTV.setText("??????");

//            handler.btnTV.setVisibility(View.GONE);
            handler.nurseTypeTV.setText(bean.getTypeName());
            handler.nurseServiceTV.setText(bean.getServiceName());
        }
    }

    /**
     * ?????????
     */
    private static class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public RoundImageView imageView;
        public MyTextView nameTV;
        public MyTextView sexTV;
        public MyTextView ageTV;
        public MyTextView stateTV;
        public MyTextView confirmTV;
        public MyTextView confirmStateTV;
        public MyTextView contactTV;
        public MyTextView nurseTypeTV,nurseServiceTV,updateStateTV,timeTV;
        public LinearLayout nurseDescLL;

        private WorkNurseActivity.OnItemClick onItemClick;

        public RecordViewHolder(View view, WorkNurseActivity.OnItemClick onItemClick){
            super(view);
            this.onItemClick = onItemClick;
            view.setOnClickListener(this);
            imageView = (RoundImageView)view.findViewById(R.id.list_item_person_image);
            imageView.setDrawCircle();
            nameTV = (MyTextView) view.findViewById(R.id.list_item_name);
            sexTV = (MyTextView) view.findViewById(R.id.list_item_sex);
            ageTV = (MyTextView) view.findViewById(R.id.list_item_age);
            stateTV = (MyTextView) view.findViewById(R.id.list_item_state);

            timeTV = (MyTextView) view.findViewById(R.id.list_item_time);

            confirmStateTV=view.findViewById(R.id.list_item_confirm_state);

            nurseTypeTV = (MyTextView) view.findViewById(R.id.list_item_nurse_type_name);
            nurseServiceTV = (MyTextView) view.findViewById(R.id.list_item_nurse_service_name);
            nurseDescLL = (LinearLayout) view.findViewById(R.id.nurse_record_info);
            updateStateTV = (MyTextView) view.findViewById(R.id.list_item_update_state);

            confirmTV=view.findViewById(R.id.list_item_confirm);
            contactTV=view.findViewById(R.id.list_item_contact);

            confirmTV.setOnClickListener(this);
            contactTV.setOnClickListener(this);
            updateStateTV.setOnClickListener(this);
            nurseDescLL.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onItemClick != null){
                onItemClick.onItemClick(view,getLayoutPosition(),3);
            }
        }
    }

    /**
     * recycler ?????????
     */
    private class CreateRecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private Context mContext;
        private ArrayList<NurseListBean> mList;
        private OnItemClick onItemClickListener;

        public CreateRecordListAdapter(Context context,ArrayList<NurseListBean> list ){
            this.mContext = context;
            this.mList = list;
        }


        public void setList(ArrayList<NurseListBean> list){
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_stop_nurse,parent,false);
            return new CreateRecordViewHolder(view,onItemClickListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof CreateRecordViewHolder){
                initView((CreateRecordViewHolder) holder, mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private void setOnItemClickListener(OnItemClick listener){
            onItemClickListener = listener;
        }

        private void initView(
                CreateRecordViewHolder handler, NurseListBean bean){
            if (bean == null) return;
            handler.moneyTV.setText("???"+bean.getAppointAmount());
            handler.personNumTV.setText(bean.getUseNumber()+"???");
//            handler.myNameTV.setText(bean.getMyName());
            handler.typeNameTV.setText(bean.getTypeName());
            handler.serviceNameTV.setText(bean.getServiceName());
            handler.timeTV.setText(bean.getAppointStartTime()+"-"+bean.getAppointEndTime().substring(10,bean.getAppointEndTime().length()));

            //-????????????   ??? 0.????????? 1.????????????
            if ("1".equals(bean.getAppointState())){
                handler.faceTypeTV.setText("?????????");
            }else{
                handler.faceTypeTV.setText("?????????");
            }

        }
    }

    /**
     * ?????????
     */
    private static class CreateRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public MyTextView myNameTV;
        public MyTextView typeNameTV;
        public MyTextView serviceNameTV;
        public MyTextView stopLayout;
        public MyTextView timeTV;
        public MyTextView personNumTV;
        public MyTextView moneyTV;
        public MyTextView remarkTV;
        public MyTextView faceTypeTV;
        private OnItemClick onItemClick;

        public CreateRecordViewHolder(View view, OnItemClick onItemClick){
            super(view);
            this.onItemClick = onItemClick;
            view.setOnClickListener(this);
            myNameTV = (MyTextView) view.findViewById(R.id.list_item_name);
            typeNameTV = (MyTextView) view.findViewById(R.id.list_item_type);
            serviceNameTV = (MyTextView) view.findViewById(R.id.list_item_service);
            timeTV = (MyTextView) view.findViewById(R.id.list_item_time);
            personNumTV = (MyTextView) view.findViewById(R.id.list_item_person_num);
            moneyTV = (MyTextView) view.findViewById(R.id.list_item_money);
            faceTypeTV=(MyTextView) view.findViewById(R.id.list_item_face_type);
            stopLayout = (MyTextView) view.findViewById(R.id.list_item_stop_layout);
            remarkTV = (MyTextView) view.findViewById(R.id.list_item_stop_remark);
            stopLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(onItemClick != null){
                onItemClick.onItemClick(view,getLayoutPosition(),3);
            }
        }
    }



    @Override
    public void finishActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public Context getContext() {
        return this;
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
    public void reloadNurse(ArrayList<HomeCareBean> list) {
        arrayList = list;
        adapter.setList(arrayList);
    }

    @Override
    public void reloadOperation(ArrayList<OperationScheduleBean> list) {

    }

    @Override
    public void reloadOperationRecord(ArrayList<OperationBean> list) {

    }

    @Override
    public void reloadCreateOperationList(OperationListBean list) {

    }

    @Override
    public void reloadNurseRecord(ArrayList<NurseServiceBean> list) {
        array = list;
        recordListAdapter.setList(array);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void reloadCreateNurseList(ArrayList<NurseListBean> list) {
        arrayCreate = list;
        createRecordListAdapter.setList(arrayCreate);
        createRefresh.setRefreshing(false);
        if (list.size()>0){
            recordRecycler.setVisibility(View.VISIBLE);
//            noRecordImg.setVisibility(View.GONE);
//            noRecordText.setVisibility(View.GONE);
        }else{
            recordRecycler.setVisibility(View.GONE);
//            noRecordImg.setVisibility(View.VISIBLE);
//            noRecordText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void reload() {
        getData();
        if(caseTV.getText().equals("????????????")){
            presenter.getNurseRecordList(arrayList.get(topPosition).getDateStr(), String.valueOf(topIndex));
        }else {
            presenter.getStopNurseRecordList(arrayList.get(topPosition).getDateStr(), String.valueOf(topIndex));
        }

    }
}
