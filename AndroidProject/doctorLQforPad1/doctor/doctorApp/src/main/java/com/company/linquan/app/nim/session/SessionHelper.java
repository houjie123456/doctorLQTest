//package com.company.linquan.app.nim.session;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.text.TextUtils;
//import android.view.View;
//
//import com.company.linquan.app.R;
//import com.company.linquan.app.base.DemoCache;
//import com.company.linquan.app.nim.activity.AckMsgInfoActivity;
//import com.company.linquan.app.nim.activity.MessageInfoActivity;
//import com.company.linquan.app.nim.contact.activity.UserProfileActivity;
//import com.company.linquan.app.nim.session.activity.MessageHistoryActivity;
//import com.company.linquan.app.nim.session.activity.SearchMessageActivity;
//import com.netease.nim.avchatkit.TeamAVChatProfile;
//import com.netease.nim.demo.contact.activity.RobotProfileActivity;
//import com.netease.nim.demo.contact.activity.UserProfileActivity;
//import com.netease.nim.demo.redpacket.NIMRedPacketClient;
//import com.netease.nim.demo.session.action.AVChatAction;
//import com.netease.nim.demo.session.action.AckMessageAction;
//import com.netease.nim.demo.session.action.FileAction;
//import com.netease.nim.demo.session.action.GuessAction;
//import com.netease.nim.demo.session.action.RTSAction;
//import com.netease.nim.demo.session.action.RedPacketAction;
//import com.netease.nim.demo.session.action.SnapChatAction;
//import com.netease.nim.demo.session.action.TeamAVChatAction;
//import com.netease.nim.demo.session.action.TipAction;
//import com.netease.nim.demo.session.activity.AckMsgInfoActivity;
//import com.netease.nim.demo.session.activity.MessageHistoryActivity;
//import com.netease.nim.demo.session.activity.MessageInfoActivity;
//import com.netease.nim.demo.session.extension.GuessAttachment;
//import com.netease.nim.demo.session.extension.RTSAttachment;
//import com.netease.nim.demo.session.extension.RedPacketAttachment;
//import com.netease.nim.demo.session.extension.RedPacketOpenedAttachment;
//import com.netease.nim.demo.session.extension.SnapChatAttachment;
//import com.netease.nim.demo.session.extension.StickerAttachment;
//import com.netease.nim.demo.session.search.SearchMessageActivity;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderAVChat;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderDefCustom;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderFile;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderGuess;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderOpenRedPacket;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderRTS;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderRedPacket;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderSnapChat;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderSticker;
//import com.netease.nim.demo.session.viewholder.MsgViewHolderTip;
//import com.netease.nim.uikit.api.NimUIKit;
//import com.netease.nim.uikit.api.model.recent.RecentCustomization;
//import com.netease.nim.uikit.api.model.session.SessionCustomization;
//import com.netease.nim.uikit.api.model.session.SessionEventListener;
//import com.netease.nim.uikit.api.wrapper.NimMessageRevokeObserver;
//import com.netease.nim.uikit.business.session.actions.BaseAction;
//import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
//import com.netease.nim.uikit.business.session.module.MsgForwardFilter;
//import com.netease.nim.uikit.business.session.module.MsgRevokeFilter;
//import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderUnknown;
//import com.netease.nim.uikit.business.team.model.TeamExtras;
//import com.netease.nim.uikit.business.team.model.TeamRequestCode;
//import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
//import com.netease.nim.uikit.common.ui.popupmenu.NIMPopupMenu;
//import com.netease.nim.uikit.common.ui.popupmenu.PopupMenuItem;
//import com.netease.nim.uikit.common.util.sys.TimeUtil;
//import com.netease.nim.uikit.impl.cache.TeamDataCache;
//import com.netease.nim.uikit.impl.customization.DefaultRecentCustomization;
//import com.netease.nimlib.sdk.NIMClient;
//import com.netease.nimlib.sdk.avchat.constant.AVChatRecordState;
//import com.netease.nimlib.sdk.avchat.constant.AVChatType;
//import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
//import com.netease.nimlib.sdk.msg.MsgService;
//import com.netease.nimlib.sdk.msg.MsgServiceObserve;
//import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
//import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
//import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
//import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
//import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
//import com.netease.nimlib.sdk.msg.model.IMMessage;
//import com.netease.nimlib.sdk.msg.model.LocalAntiSpamResult;
//import com.netease.nimlib.sdk.msg.model.RecentContact;
//import com.netease.nimlib.sdk.robot.model.RobotAttachment;
//import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
//import com.netease.nimlib.sdk.team.model.Team;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * UIKit????????????????????????????????????
// */
//public class SessionHelper {
//
//    private static final int ACTION_HISTORY_QUERY = 0;
//    private static final int ACTION_SEARCH_MESSAGE = 1;
//    private static final int ACTION_CLEAR_MESSAGE = 2;
//
//    private static SessionCustomization p2pCustomization;
//    private static SessionCustomization normalTeamCustomization;
//    private static SessionCustomization advancedTeamCustomization;
//    private static SessionCustomization myP2pCustomization;
//    private static SessionCustomization robotCustomization;
//    private static RecentCustomization recentCustomization;
//
//    private static NIMPopupMenu popupMenu;
//    private static List<PopupMenuItem> menuItemList;
//
//    public static final boolean USE_LOCAL_ANTISPAM = true;
//
//
//    public static void init() {
//        // ????????????????????????????????????
//        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
//
//        // ???????????????????????????????????????ViewHolder
//        registerViewHolders();
//
//        // ???????????????????????????????????????
//        setSessionListener();
//
//        // ???????????????????????????
//        registerMsgForwardFilter();
//
//        // ???????????????????????????
//        registerMsgRevokeFilter();
//
//        // ???????????????????????????
//        registerMsgRevokeObserver();
//
//        NimUIKit.setCommonP2PSessionCustomization(getP2pCustomization());
//
//        NimUIKit.setCommonTeamSessionCustomization(getTeamCustomization(null));
//
//        NimUIKit.setRecentCustomization(getRecentCustomization());
//    }
//
//    public static void startP2PSession(Context context, String account) {
//        startP2PSession(context, account, null);
//    }
//
//    public static void startP2PSession(Context context, String account, IMMessage anchor) {
//        if (!DemoCache.getAccount().equals(account)) {
//            if (NimUIKit.getRobotInfoProvider().getRobotByAccount(account) != null) {
//                NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getRobotCustomization(), anchor);
//            } else {
//                NimUIKit.startP2PSession(context, account, anchor);
//            }
//        } else {
//            NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getMyP2pCustomization(), anchor);
//        }
//    }
//
//    public static void startTeamSession(Context context, String tid) {
//        startTeamSession(context, tid, null);
//    }
//
//    public static void startTeamSession(Context context, String tid, IMMessage anchor) {
//        NimUIKit.startTeamSession(context, tid, getTeamCustomization(tid), anchor);
//    }
//
//    // ??????????????????(?????? UIKIT ??????????????????????????????????????????)
//    public static void startTeamSession(Context context, String tid, Class<? extends Activity> backToClass, IMMessage anchor) {
//        NimUIKit.startChatting(context, tid, SessionTypeEnum.Team, getTeamCustomization(tid), backToClass, anchor);
//    }
//
//    // ?????????????????????????????????????????????????????????null??????
//    private static SessionCustomization getP2pCustomization() {
//        if (p2pCustomization == null) {
//            p2pCustomization = new SessionCustomization() {
//                // ????????????Activity Result??? ????????????????????????
//                @Override
//                public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
//                    super.onActivityResult(activity, requestCode, resultCode, data);
//
//                }
//
//                @Override
//                public boolean isAllowSendMessage(IMMessage message) {
//                    return checkLocalAntiSpam(message);
//                }
//
//                @Override
//                public MsgAttachment createStickerAttachment(String category, String item) {
////                    return new StickerAttachment(category, item);
//                }
//            };
//
//            // ??????
////            p2pCustomization.backgroundColor = Color.BLUE;
////            p2pCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
////            p2pCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
////            p2pCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk"
//
//            // ????????????????????????????????????????????? ??????????????????????????????????????????
//            ArrayList<BaseAction> actions = new ArrayList<>();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                actions.add(new AVChatAction(AVChatType.AUDIO));
//                actions.add(new AVChatAction(AVChatType.VIDEO));
//            }
////            actions.add(new RTSAction());
////            actions.add(new SnapChatAction());
////            actions.add(new GuessAction());
////            actions.add(new FileAction());
////            actions.add(new TipAction());
////            if (NIMRedPacketClient.isEnable()) {
////                actions.add(new RedPacketAction());
////            }
//            p2pCustomization.actions = actions;
//            p2pCustomization.withSticker = true;
//
//            // ??????ActionBar?????????????????????????????????
//            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.P2P);
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
//
//            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//
//                    MessageInfoActivity.startActivity(context, sessionId); //??????????????????
//                }
//            };
//
//
//            infoButton.iconId = R.drawable.nim_ic_message_actionbar_p2p_add;
//
//            buttons.add(cloudMsgButton);
//            buttons.add(infoButton);
//            p2pCustomization.buttons = buttons;
//        }
//
//        return p2pCustomization;
//    }
//
//    private static SessionCustomization getMyP2pCustomization() {
//        if (myP2pCustomization == null) {
//            myP2pCustomization = new SessionCustomization() {
//                // ????????????Activity Result??? ????????????????????????
//                @Override
//                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
//                    if (requestCode == TeamRequestCode.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//                        String result = data.getStringExtra(TeamExtras.RESULT_EXTRA_REASON);
//                        if (result == null) {
//                            return;
//                        }
//                        if (result.equals(TeamExtras.RESULT_EXTRA_REASON_CREATE)) {
//                            String tid = data.getStringExtra(TeamExtras.RESULT_EXTRA_DATA);
//                            if (TextUtils.isEmpty(tid)) {
//                                return;
//                            }
//
//                            startTeamSession(activity, tid);
//                            activity.finish();
//                        }
//                    }
//                }
//
//                @Override
//                public boolean isAllowSendMessage(IMMessage message) {
//                    return checkLocalAntiSpam(message);
//                }
//
//                @Override
//                public MsgAttachment createStickerAttachment(String category, String item) {
////                    return new StickerAttachment(category, item);
//                }
//            };
//
//            // ????????????????????????????????????????????? ??????????????????????????????????????????
//            ArrayList<BaseAction> actions = new ArrayList<>();
////            actions.add(new SnapChatAction());
////            actions.add(new GuessAction());
////            actions.add(new FileAction());
//            myP2pCustomization.actions = actions;
//            myP2pCustomization.withSticker = true;
//            // ??????ActionBar?????????????????????????????????
//            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.P2P);
//                }
//            };
//
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
//
//            buttons.add(cloudMsgButton);
//            myP2pCustomization.buttons = buttons;
//        }
//        return myP2pCustomization;
//    }
//
//    private static boolean checkLocalAntiSpam(IMMessage message) {
//        if (!USE_LOCAL_ANTISPAM) {
//            return true;
//        }
//        LocalAntiSpamResult result = NIMClient.getService(MsgService.class).checkLocalAntiSpam(message.getContent(), "**");
//        int operator = result == null ? 0 : result.getOperator();
//
//        switch (operator) {
//            case 1: // ?????????????????????
//                message.setContent(result.getContent());
//                return true;
//            case 2: // ????????????????????????
//                return false;
//            case 3: // ??????????????????????????????
//                message.setClientAntiSpam(true);
//                return true;
//            case 0:
//            default:
//                break;
//        }
//
//        return true;
//    }
//
//    private static SessionCustomization getRobotCustomization() {
//        if (robotCustomization == null) {
//            robotCustomization = new SessionCustomization() {
//                // ????????????Activity Result??? ????????????????????????
//                @Override
//                public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
//                    super.onActivityResult(activity, requestCode, resultCode, data);
//
//                }
//
//                @Override
//                public MsgAttachment createStickerAttachment(String category, String item) {
//                    return null;
//                }
//            };
//
//            // ??????ActionBar?????????????????????????????????
//            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.P2P);
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
//
//            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//
////                    RobotProfileActivity.start(context, sessionId); //??????????????????
//                }
//            };
//
//
//            infoButton.iconId = R.drawable.nim_ic_actionbar_robot_info;
//
//            buttons.add(cloudMsgButton);
//            buttons.add(infoButton);
//            robotCustomization.buttons = buttons;
//        }
//
//        return robotCustomization;
//    }
//
//    private static RecentCustomization getRecentCustomization() {
//        if (recentCustomization == null) {
//            recentCustomization = new DefaultRecentCustomization() {
//                @Override
//                public String getDefaultDigest(RecentContact recent) {
//                    switch (recent.getMsgType()) {
//                        case avchat:
//                            MsgAttachment attachment = recent.getAttachment();
//                            AVChatAttachment avchat = (AVChatAttachment) attachment;
//                            if (avchat.getState() == AVChatRecordState.Missed && !recent.getFromAccount().equals(NimUIKit.getAccount())) {
//                                // ??????????????????
//                                StringBuilder sb = new StringBuilder("[??????");
//                                if (avchat.getType() == AVChatType.VIDEO) {
//                                    sb.append("????????????]");
//                                } else {
//                                    sb.append("????????????]");
//                                }
//                                return sb.toString();
//                            } else if (avchat.getState() == AVChatRecordState.Success) {
//                                StringBuilder sb = new StringBuilder();
//                                if (avchat.getType() == AVChatType.VIDEO) {
//                                    sb.append("[????????????]: ");
//                                } else {
//                                    sb.append("[????????????]: ");
//                                }
//                                sb.append(TimeUtil.secToTime(avchat.getDuration()));
//                                return sb.toString();
//                            } else {
//                                if (avchat.getType() == AVChatType.VIDEO) {
//                                    return ("[????????????]");
//                                } else {
//                                    return ("[????????????]");
//                                }
//                            }
//                    }
//                    return super.getDefaultDigest(recent);
//                }
//            };
//        }
//
//        return recentCustomization;
//    }
//
//    private static SessionCustomization getTeamCustomization(String tid) {
//        if (normalTeamCustomization == null) {
//
//            // ????????????????????????????????????????????? ??????????????????????????????????????????
//            final TeamAVChatAction avChatAction = new TeamAVChatAction(AVChatType.VIDEO);
//            TeamAVChatProfile.sharedInstance().registerObserver(true);
//
//            ArrayList<BaseAction> actions = new ArrayList<>();
//            actions.add(avChatAction);
////            actions.add(new GuessAction());
////            actions.add(new FileAction());
////            if (NIMRedPacketClient.isEnable()) {
////                actions.add(new RedPacketAction());
////            }
////            actions.add(new TipAction());
//
//
//            SessionTeamCustomization.SessionTeamCustomListener listener = new SessionTeamCustomization.SessionTeamCustomListener() {
//                @Override
//                public void initPopupWindow(Context context, View view, String sessionId, SessionTypeEnum sessionTypeEnum) {
//                    initPopuptWindow(context, view, sessionId, sessionTypeEnum);
//                }
//
//                @Override
//                public void onSelectedAccountsResult(ArrayList<String> selectedAccounts) {
//                    avChatAction.onSelectedAccountsResult(selectedAccounts);
//                }
//
//                @Override
//                public void onSelectedAccountFail() {
//                    avChatAction.onSelectedAccountFail();
//                }
//            };
//            normalTeamCustomization = new SessionTeamCustomization(listener) {
//                @Override
//                public boolean isAllowSendMessage(IMMessage message) {
//                    return checkLocalAntiSpam(message);
//                }
//            };
//
//            normalTeamCustomization.actions = actions;
//        }
//
//        if (advancedTeamCustomization == null) {
//            // ????????????????????????????????????????????? ??????????????????????????????????????????
//            final TeamAVChatAction avChatAction = new TeamAVChatAction(AVChatType.VIDEO);
//            TeamAVChatProfile.sharedInstance().registerObserver(true);
//
//            ArrayList<BaseAction> actions = new ArrayList<>();
//            actions.add(avChatAction);
////            actions.add(new GuessAction());
////            actions.add(new FileAction());
////            actions.add(new AckMessageAction());
////            if (NIMRedPacketClient.isEnable()) {
////                actions.add(new RedPacketAction());
////            }
////            actions.add(new TipAction());
//
//            SessionTeamCustomization.SessionTeamCustomListener listener = new SessionTeamCustomization.SessionTeamCustomListener() {
//
//                @Override
//                public void initPopupWindow(Context context, View view, String sessionId, SessionTypeEnum sessionTypeEnum) {
//                    initPopuptWindow(context, view, sessionId, sessionTypeEnum);
//                }
//
//
//                @Override
//                public void onSelectedAccountsResult(ArrayList<String> selectedAccounts) {
//                    avChatAction.onSelectedAccountsResult(selectedAccounts);
//                }
//
//                @Override
//                public void onSelectedAccountFail() {
//                    avChatAction.onSelectedAccountFail();
//                }
//            };
//
//            advancedTeamCustomization = new SessionTeamCustomization(listener) {
//                @Override
//                public boolean isAllowSendMessage(IMMessage message) {
//                    return checkLocalAntiSpam(message);
//                }
//            };
//
//            advancedTeamCustomization.actions = actions;
//        }
//
//        if (TextUtils.isEmpty(tid)) {
//            return normalTeamCustomization;
//        } else {
//            Team team = TeamDataCache.getInstance().getTeamById(tid);
//            if (team != null && team.getType() == TeamTypeEnum.Advanced) {
//                return advancedTeamCustomization;
//            }
//        }
//        return normalTeamCustomization;
//    }
//
//    private static void registerViewHolders() {
//        NimUIKit.registerMsgItemViewHolder(FileAttachment.class, MsgViewHolderFile.class);
//        NimUIKit.registerMsgItemViewHolder(AVChatAttachment.class, MsgViewHolderAVChat.class);
////        NimUIKit.registerMsgItemViewHolder(GuessAttachment.class, MsgViewHolderGuess.class);
//        NimUIKit.registerMsgItemViewHolder(CustomAttachment.class, MsgViewHolderDefCustom.class);
//        NimUIKit.registerMsgItemViewHolder(StickerAttachment.class, MsgViewHolderSticker.class);
////        NimUIKit.registerMsgItemViewHolder(SnapChatAttachment.class, MsgViewHolderSnapChat.class);
////        NimUIKit.registerMsgItemViewHolder(RTSAttachment.class, MsgViewHolderRTS.class);
//        NimUIKit.registerTipMsgViewHolder(MsgViewHolderTip.class);
//        registerRedPacketViewHolder();
//    }
//
//    private static void registerRedPacketViewHolder() {
////        if (NIMRedPacketClient.isEnable()) {
////            NimUIKit.registerMsgItemViewHolder(RedPacketAttachment.class, MsgViewHolderRedPacket.class);
////            NimUIKit.registerMsgItemViewHolder(RedPacketOpenedAttachment.class, MsgViewHolderOpenRedPacket.class);
////        } else {
////            NimUIKit.registerMsgItemViewHolder(RedPacketAttachment.class, MsgViewHolderUnknown.class);
////            NimUIKit.registerMsgItemViewHolder(RedPacketOpenedAttachment.class, MsgViewHolderUnknown.class);
////        }
//    }
//
//    private static void setSessionListener() {
//        SessionEventListener listener = new SessionEventListener() {
//            @Override
//            public void onAvatarClicked(Context context, IMMessage message) {
//                // ????????????????????????????????????
//                if (message.getMsgType() == MsgTypeEnum.robot && message.getDirect() == MsgDirectionEnum.In) {
//                    RobotAttachment attachment = (RobotAttachment) message.getAttachment();
//                    if (attachment.isRobotSend()) {
////                        RobotProfileActivity.start(context, attachment.getFromRobotAccount());
//                        return;
//                    }
//                }
//                UserProfileActivity.start(context, message.getFromAccount());
//            }
//
//            @Override
//            public void onAvatarLongClicked(Context context, IMMessage message) {
//                // ??????????????????@????????????????????????????????????????????????????????????
//            }
//
//            @Override
//            public void onAckMsgClicked(Context context, IMMessage message) {
//                // ????????????????????????????????????????????????????????????????????????????????????????????????
//                AckMsgInfoActivity.start(context, message);
//            }
//        };
//
//        NimUIKit.setSessionListener(listener);
//    }
//
//
//    /**
//     * ?????????????????????
//     */
//    private static void registerMsgForwardFilter() {
//        NimUIKit.setMsgForwardFilter(new MsgForwardFilter() {
//            @Override
//            public boolean shouldIgnore(IMMessage message) {
//                if (message.getMsgType() == MsgTypeEnum.custom && message.getAttachment() != null
//                       ) {
//                    // ???????????????????????????????????????????????? ???????????????
//                    return true;
//                } else if (message.getMsgType() == MsgTypeEnum.robot && message.getAttachment() != null && ((RobotAttachment) message.getAttachment()).isRobotSend()) {
//                    return true; // ????????????????????????????????? ???????????????
//                }
//                return false;
//            }
//        });
//    }
//
//    /**
//     * ?????????????????????
//     */
//    private static void registerMsgRevokeFilter() {
//        NimUIKit.setMsgRevokeFilter(new MsgRevokeFilter() {
//            @Override
//            public boolean shouldIgnore(IMMessage message) {
//                if (message.getAttachment() != null) {
//                    // ???????????????????????????????????????????????? ???????????????
//                    return true;
//                } else if (DemoCache.getAccount().equals(message.getSessionId())) {
//                    // ?????????????????? ???????????????
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
//
//    private static void registerMsgRevokeObserver() {
//        NIMClient.getService(MsgServiceObserve.class).observeRevokeMessage(new NimMessageRevokeObserver(), true);
//    }
//
//
//    private static void initPopuptWindow(Context context, View view, String sessionId, SessionTypeEnum sessionTypeEnum) {
//        if (popupMenu == null) {
//            menuItemList = new ArrayList<>();
//            popupMenu = new NIMPopupMenu(context, menuItemList, listener);
//        }
//        menuItemList.clear();
//        menuItemList.addAll(getMoreMenuItems(context, sessionId, sessionTypeEnum));
//        popupMenu.notifyData();
//        popupMenu.show(view);
//    }
//
//    private static NIMPopupMenu.MenuItemClickListener listener = new NIMPopupMenu.MenuItemClickListener() {
//        @Override
//        public void onItemClick(final PopupMenuItem item) {
//            switch (item.getTag()) {
//                case ACTION_HISTORY_QUERY:
//                    MessageHistoryActivity.start(item.getContext(), item.getSessionId(), item.getSessionTypeEnum()); // ??????????????????
//                    break;
//                case ACTION_SEARCH_MESSAGE:
//                    SearchMessageActivity.start(item.getContext(), item.getSessionId(), item.getSessionTypeEnum());
//                    break;
//                case ACTION_CLEAR_MESSAGE:
//                    EasyAlertDialogHelper.createOkCancelDiolag(item.getContext(), null, "?????????????????????", true, new EasyAlertDialogHelper.OnDialogActionListener() {
//                        @Override
//                        public void doCancelAction() {
//
//                        }
//
//                        @Override
//                        public void doOkAction() {
//                            NIMClient.getService(MsgService.class).clearChattingHistory(item.getSessionId(), item.getSessionTypeEnum());
//                            MessageListPanelHelper.getInstance().notifyClearMessages(item.getSessionId());
//                        }
//                    }).show();
//                    break;
//            }
//        }
//    };
//
//    private static List<PopupMenuItem> getMoreMenuItems(Context context, String sessionId, SessionTypeEnum sessionTypeEnum) {
//        List<PopupMenuItem> moreMenuItems = new ArrayList<PopupMenuItem>();
//        moreMenuItems.add(new PopupMenuItem(context, ACTION_HISTORY_QUERY, sessionId,
//                sessionTypeEnum, DemoCache.getContext().getString(R.string.message_history_query)));
//        moreMenuItems.add(new PopupMenuItem(context, ACTION_SEARCH_MESSAGE, sessionId,
//                sessionTypeEnum, DemoCache.getContext().getString(R.string.message_search_title)));
//        moreMenuItems.add(new PopupMenuItem(context, ACTION_CLEAR_MESSAGE, sessionId,
//                sessionTypeEnum, DemoCache.getContext().getString(R.string.message_clear)));
//        return moreMenuItems;
//    }
//}
