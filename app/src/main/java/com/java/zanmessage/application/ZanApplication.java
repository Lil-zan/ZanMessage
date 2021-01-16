package com.java.zanmessage.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.java.zanmessage.R;
import com.java.zanmessage.event.ContactChangeEvent;
import com.java.zanmessage.utils.Contant;
import com.java.zanmessage.utils.DBUtils;
import com.java.zanmessage.view.activity.ChatActivity;
import com.java.zanmessage.view.activity.LoginAcitity;
import com.java.zanmessage.view.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.core.app.NotificationCompat;
import cn.leancloud.AVOSCloud;


public class ZanApplication extends Application implements EMContactListener, EMMessageListener, EMConnectionListener {


    private SoundPool mSoundPool;
    private int ringtone;
    private ActivityManager activityManager;
    private AudioManager audio;
    private NotificationManager notificationManager;
    private String CHANNEL_ID = "zan";
    private String CHANNEL_NAME = "新消息通知";
    private List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);

        //初始化环信
        initHuanxin();
        //初始化leanCloud云数据库
        initLean();
        //初始化DBUtils，给DBUtils提供全局上下文。
        DBUtils.initDBUtils(this);
        //注册监听所有activity状态
        listenerActivity();
        //获取Activity管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //获取系统铃声管理器
        audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取系统通知管理器
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //初始化doundpool和提示音
        initVolume();


    }

    //监听所有activity生命周期
    private void listenerActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityList.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityList.remove(activity);
            }
        });
    }

    //获取本地铃声和初始化soundpool
    private void initVolume() {
        //获取系统提示音
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
        //构建SoundPool播放收到信息提示音
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        //给soundpool加载铃声
        ringtone = mSoundPool.load(uri.getPath(), 1);
    }

    private void initLean() {
        //https://g7hmhyyv.lc-cn-n1-shared.com
        AVOSCloud.initialize(this, "G7HmHyYvzSmSgQkoKn8PW71z-gzGzoHsz", "SBEQNwNMsmgPn6dGJ7JH0vrd", "https://g7hmhyyv.lc-cn-n1-shared.com");
//        AVOSCloud.initializeSecurely(this, "G7HmHyYvzSmSgQkoKn8PW71z-gzGzoHsz", "https://please-replace-with-your-customized.domain.com");
//        AVIMOptions.getGlobalOptions().setDisableAutoLogin4Push(true);
    }

    private void initHuanxin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        //环信即时通讯监听好友状态
        EMClient.getInstance().contactManager().setContactListener(this);

        //环信即时通讯消息监听
        EMClient.getInstance().chatManager().addMessageListener(this);

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(this);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    @Override
    public void onContactAdded(String username) {
        //添加了好友时

        //开启发布者publisher
        EventBus.getDefault().post(new ContactChangeEvent(username, true));
    }

    @Override
    public void onContactDeleted(String username) {
        //删除了好友时
        EventBus.getDefault().post(new ContactChangeEvent(username, false));
    }

    @Override
    public void onContactInvited(String username, String reason) {
        //收到好友邀请时
        //直接接收好友邀请。
        try {
            EMClient.getInstance().contactManager().acceptInvitation(username);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFriendRequestAccepted(String username) {
        //好友请求接受时
        //通知联系人界面刷新
    }

    @Override
    public void onFriendRequestDeclined(String username) {
        //好友请求拒绝时
    }


    //收到消息
    @Override
    public void onMessageReceived(List<EMMessage> list) {
        EMMessage emMessage = list.get(0);
        EventBus.getDefault().post(emMessage);
        //收到消息做通知。
        showNotifycation(emMessage);

    }

    //铃声通知完还需要在系统通知管理器中提示。
    private void showNotifycation(EMMessage emMessage) {
        boolean isInBack = isRunInBackgroud();
        if (isInBack) {
            //自己看着都眼花，写点注解吧。getBody没什么好说的，环信这么说就这么做，为了获取消息
            EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            //点击通知之后跳转到chatactivity,为了有返回栈，要多放一个mainactivity进去
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //跳转到chatactivity，没什么好说的。
            Intent chatIntent = new Intent(this, ChatActivity.class);
            //但是不要忘记跳转到聊天是需要username从环信上获取数据的。
            chatIntent.putExtra(Contant.MY_USERNAME, emMessage.getFrom());
            Intent[] mIntent = {mainIntent, chatIntent};
            //pendingIntent需要注意的是requestCode如果写了两个通知，一定不能用同一个requestCode，否则先收到的通知点击会失效。
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(emMessage.getFrom())
                    .setContentText(body.getMessage())
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(Contant.MSG_NOTIFICATION_ID, notification);

        } else {
            mSoundPool.play(ringtone, 1, 1, 0, 0, 1);
        }
    }


    //收到透传消息
    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    //收到已读回执
    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    //收到已送达回执
    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    //消息被撤回
    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    //消息状态变动
    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }


    //判断应用是否运行在后台.
    private boolean isRunInBackgroud() {
        /*IMPORTANCE_CACHED = 400//后台
        IMPORTANCE_EMPTY = 500//空进程
        MPORTANCE_FOREGROUND = 100//在屏幕最前端,可获取焦点
        IMPORTANCE_SERVICE = 300//在服务中
        IMPORTANCE_VISIBLE = 200//在屏幕前端、获取不到焦点*/
        boolean inBackground = true;
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                    inBackground = true;
                } else if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    inBackground = false;
                } else {
                    inBackground = true;
                }
            }
        }
        return inBackground;
    }

    //环信链接监听
    @Override
    public void onConnected() {

    }

    //环信链接监听
    @Override
    public void onDisconnected(int error) {
        if (error == EMError.USER_REMOVED) {
//            onUserException(Constant.ACCOUNT_REMOVED);
        } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            //有另一台设备登陆账号。
            //遍历所有activity，并finish掉。
            for (int i = 0; i < activityList.size(); i++) {
                Activity activity = activityList.get(i);
                activity.finish();
            }
            //清空记录的activity，activity注册监听里虽然已经移除，保守点。手动再移除一遍吧
            activityList.clear();
            //重新启动一个登陆activity等待用操作
            Intent intent = new Intent(this, LoginAcitity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            onUserException(Constant.ACCOUNT_CONFLICT);
        } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
//            onUserException(Constant.ACCOUNT_FORBIDDEN);
        } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
//            onUserException(Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD);
        } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
//            onUserException(Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE);
        }
    }
}
