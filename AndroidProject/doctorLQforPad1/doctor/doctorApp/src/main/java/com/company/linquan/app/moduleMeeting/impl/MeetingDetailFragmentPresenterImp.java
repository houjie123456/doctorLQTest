package com.company.linquan.app.moduleMeeting.impl;

import android.util.Log;

import com.company.linquan.app.config.C;
import com.company.linquan.app.http.HttpApi;
import com.company.linquan.app.http.JSONBean;
import com.company.linquan.app.http.JSONDiscuss;
import com.company.linquan.app.http.JSONMeetingDetail;
import com.company.linquan.app.moduleMeeting.MeetingInterface;
import com.company.linquan.app.util.ToolSharePerference;
import com.company.linquan.app.util.ToolUtil;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by YC on 2018/7/25.
 */

public class MeetingDetailFragmentPresenterImp implements MeetingInterface.MeetingFragmentPresenterInterface {

    private MeetingInterface.MeetingDetailFragmentInterface view;

    public MeetingDetailFragmentPresenterImp(MeetingInterface.MeetingDetailFragmentInterface view) {
       this.view = view;
    }

    @Override
    public void getMeetingDetail(String id) {
        view.showDialog();
        Map<String,String> map = new HashMap<>();
        map.put("personID", ToolSharePerference.getStringData(view.getContext(), C.fileconfig, C.UserID));
        map.put("meetingID", id);
        map.put("isH5", "2");
        map.put("sign", ToolUtil.getSign(map));
        HttpApi.getMeetingDetail(map.get("personID"),map.get("meetingID"),map.get("isH5"),map.get("sign"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONMeetingDetail>(){
                    @Override
                    public void onCompleted() {
                        Log.i("onCompleted","onCompleted");
                        view.dismissDialog();
                    }
                    @Override
                    public void onError(Throwable e) {
                        view.dismissDialog();
                        Log.i("Throwable",e.toString());
                    }
                    @Override
                    public void onNext(JSONMeetingDetail result) {
                        Log.i("onNext","onNext");
                        //??????UI
                        if ("1".equals(result.getCode())){
                            view.reloadView(result.getInfoJson());
                        }else {
                            view.showToast(result.getMsgBox());
                        }
                    }
                });
    }

    @Override
    public void getDiscuss(String id, int page) {
        view.showDialog();
        Map<String,String> map = new HashMap<>();
        map.put("meetingID", id);
        map.put("currPage", page+"");
        map.put("sign", ToolUtil.getSign(map));
        HttpApi.getMeetingThinkList(map.get("meetingID"),map.get("currPage"),map.get("sign"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONDiscuss>(){
                    @Override
                    public void onCompleted() {
                        Log.i("onCompleted","onCompleted");
                        view.dismissDialog();
                    }
                    @Override
                    public void onError(Throwable e) {
                        view.dismissDialog();
                        Log.i("Throwable",e.toString());
                    }
                    @Override
                    public void onNext(JSONDiscuss result) {
                        Log.i("onNext","onNext");
                        //??????UI
                        if ("1".equals(result.getCode())){
                            view.setListView(result.getTable());
                        }else {
                            view.showToast(result.getMsgBox());
                        }
                    }
                });
    }

    @Override
    public void sendDiscuss(String id, String txt, String score) {
        view.showDialog();
        Map<String,String> map = new HashMap<>();
        map.put("meetingID", id);
        map.put("personID", ToolSharePerference.getStringData(view.getContext(),C.fileconfig,C.UserID));
        map.put("thinkText", txt);
        map.put("thinkScore", score);
        map.put("sign", ToolUtil.getSign(map));
        HttpApi.addMeetingThinkInfo(map.get("meetingID"),map.get("personID"),map.get("thinkText"),map.get("thinkScore"),map.get("sign"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONBean>(){
                    @Override
                    public void onCompleted() {
                        Log.i("onCompleted","onCompleted");
                        view.dismissDialog();
                    }
                    @Override
                    public void onError(Throwable e) {
                        view.dismissDialog();
                        Log.i("Throwable",e.toString());
                    }
                    @Override
                    public void onNext(JSONBean result) {
                        Log.i("onNext","onNext");
                        //??????UI
                        if ("1".equals(result.getCode())){
                            view.reloadListView();
                        }else {
                            view.showToast(result.getMsgBox());
                        }
                    }
                });
    }
}
