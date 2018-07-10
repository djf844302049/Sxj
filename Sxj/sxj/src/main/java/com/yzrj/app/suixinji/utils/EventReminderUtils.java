package com.yzrj.app.suixinji.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by admin on 2018/6/15.
 */

public class EventReminderUtils {
    public EventReminderUtils() {

    }

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "table";
    private static String CALENDARS_ACCOUNT_NAME = "table@table.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.table";
    private static String CALENDARS_DISPLAY_NAME = "帅帅";
    public static String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

    //删除
    public static void deleteCalendarEvent(Context context, final String title, final String data, final long time) {
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null)//查询返回空值
                return;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title")) + "";
                    String eventdata = eventCursor.getString(eventCursor.getColumnIndex("description")) + "";
                    String eventstartTime = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DTSTART)) + "";
//                    Toast.makeText(context, "ss" + eventTitle + eventdata + eventstartTime, Toast.LENGTH_SHORT).show();
                    if (eventTitle.equals(title + "") && eventdata.equals(data + "") && eventstartTime.equals(time + "")) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALANDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    public static void calendarEvent(final Context context, final String type, final String title, final String data, final long time, final int fangfa, final int txtime) {
        //日历事件设置
        RequestPermissions.PermissionCallBack permissionCallBack1 = new RequestPermissions.PermissionCallBack() {
            @Override
            public void setOnPermissionListener(Boolean bo) {
                if (bo) {
                    if (!EventReminderUtils.selectCalendarEvent(context, title, data, time)) {
                        EventReminderUtils.addCalendarEvent(context, type, title, data, time, fangfa, txtime);
                    } else {
//                        Toast.makeText(context, "事件已存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "没有读写日历权限，请授权后再操作", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };
        RequestPermissions.READ_CALENDARandWRITE_CALENDAR(context, permissionCallBack1, 1002);

//不设置系统闹钟，因为系统闹钟无法删除
//系统闹钟设置
//        RequestPermissions.PermissionCallBack permissionCallBack2 = new RequestPermissions.PermissionCallBack() {
//            @Override
//            public void setOnPermissionListener(Boolean bo) {
//                if (bo) {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTimeInMillis(time);
//                    if (AppUtils.typejnr.equals(type)) {
//                        calendar.add(Calendar.DAY_OF_MONTH, -txtime);
//                    } else {
//                        calendar.add(Calendar.MINUTE, -txtime);
//                    }
//                    Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
//                    //用于标识闹铃的自定义消息。
//                    alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, title + "-" + data);
////                    闹铃的小时
//                    alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
////                    闹铃的分钟
//                    alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
////                    EXTRA_DAYS
////                    一个 ArrayList，其中包括应重复触发该闹铃的每个周日。 每一天都必须使用 Calendar 类中的某个整型值（如 MONDAY）进行声明。
////                    对于一次性闹铃，无需指定此 extra。
//                    ArrayList<Integer> arrayList = new ArrayList();
//                    arrayList.add(calendar.get(Calendar.DAY_OF_WEEK));
//                    alarmIntent.putExtra(AlarmClock.EXTRA_DAYS, arrayList);
//
//                    if (fangfa == 0) {
//                        //                    EXTRA_RINGTONE
////                    一个 content:URI，用于指定闹铃使用的铃声，也可指定 VALUE_RINGTONE_SILENT 以不使用铃声。
////                    如需使用默认铃声，则无需指定此 extra。
//                        alarmIntent.putExtra(AlarmClock.EXTRA_RINGTONE, "");
//                        //一个布尔型值，用于指定该闹铃触发时是否振动。
//                        alarmIntent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
//                    } else if (fangfa == 1) {
//                        //一个布尔型值，用于指定该闹铃触发时是否振动。
//                        alarmIntent.putExtra(AlarmClock.EXTRA_VIBRATE, false);
//                    } else {
//                        //一个布尔型值，用于指定该闹铃触发时是否振动。
//                        alarmIntent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
//                    }
//
//
//                    // 一个布尔型值，用于指定响应闹铃的应用在设置闹铃时是否应跳过其 UI。
//// 若为 true，则应用应跳过任何确认 UI，直接设置指定的闹铃。
//                    alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
//
//                    alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    // 查询要启动的组件是否存在；
//                    PackageManager packageManager = context.getPackageManager();
//                    ComponentName componentName = alarmIntent.resolveActivity(packageManager);
//                    if (componentName != null) {
//                        context.startActivity(alarmIntent);
//                    }
//                } else {
//                    Toast.makeText(context, "没有设置报警权限，请授权后再操作", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        };
//        RequestPermissions.SET_ALARM(context, permissionCallBack2, 1003);

    }

    public static boolean selectCalendarEvent(Context context, String title, String data, long startTime) {
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL),
                null, null, null, null);

        try {
            if (eventCursor == null)//查询返回空值
                return false;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title")) + "";
                    String eventdata = eventCursor.getString(eventCursor.getColumnIndex("description")) + "";
                    String eventstartTime = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DTSTART)) + "";
//                    Toast.makeText(context, "ss" + eventTitle + eventdata + eventstartTime, Toast.LENGTH_SHORT).show();
                    if (eventTitle.equals(title + "") && eventdata.equals(data + "") && eventstartTime.equals(startTime + "")) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    /**
     * 添加事件
     *
     * @param context
     */
    public static void addCalendarEvent(Context context, final String type, String title, String data, final long time, final int fangfa, final int txtime) {
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }


        ContentValues event = new ContentValues();
        event.put("title", title);    //标题
        event.put("description", data);  //内容
        // 插入账户的id
        event.put("calendar_id", calId);
        event.put(CalendarContract.Events.DTSTART, time);
        event.put(CalendarContract.Events.DTEND, time + 60 * 60 * 1000);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());  //这个是时区，必须有"Asia/Shanghai"，, CalendarContract.Calendars.CALENDAR_TIME_ZONE
        //设置一个全天事件的条目
        //event.put("allDay", 1); // 0 for false, 1 for true
        //设置地理位置
        //event.put(CalendarContract.Events.EVENT_LOCATION, "Event Location");

        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            return;
        }
        //插完日程之后必须再插入以下代码段才能实现提醒功能  //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent)); // 得到当前表的_id

        if (AppUtils.typejnr.equals(type)) {
            values.put(CalendarContract.Reminders.MINUTES, txtime * 24 * 60); // 提前10分钟有提醒
        } else {
            values.put(CalendarContract.Reminders.MINUTES, txtime); // 提前10分钟有提醒
        }

        //提醒方法
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);//如果需要有提醒,必须要有这一行
        Uri uri = context.getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
        if (uri == null) {
            // 添加闹钟提醒失败直接返回
            Toast.makeText(context, "添加闹钟失败", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    //添加日历账号
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALANDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @return
     */
    public static long getDateLong(String date_str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
