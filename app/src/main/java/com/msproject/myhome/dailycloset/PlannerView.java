package com.msproject.myhome.dailycloset;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import org.joda.time.LocalDate;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class PlannerView  extends ConstraintLayout {
    TextView yearText;
    TextView monthText;
    TextView yearLiteral;
    TextView monthLiteral;
    LinearLayout calendarSelectLayout;
    ConstraintLayout calendarSelectView;
    boolean isCalendarSelectClicked;
    Context context;
    Button calendarSelectButton;
    boolean isExistToday;
    Button todayButton;
    Button previousButton;
    Button nextButton;
    LocalDate date;
    ConstraintLayout recyclerviewContainer;
    RecyclerView recyclerView;
    LinearLayout calendarHeader;
    boolean isEnglishCalendar;
    int PICK_FROM_ALBUM = 2000;
    LocalDate selectedDate;

    TextView sunday;
    TextView monday;
    TextView tuesday;
    TextView wednesday;
    TextView thursday;
    TextView friday;
    TextView saterday;




    public PlannerView(Context context) {
        super(context);
        this.context = context;
        this.isCalendarSelectClicked = false;
    }

    public PlannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.isCalendarSelectClicked = false;
    }

    @SuppressLint("WrongConstant")
    public void initView(final LocalDate localDate, final CalendarFragment plannerFragment) {
        this.date = localDate;

        removeAllViews();
        this.isExistToday = false;

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);

        final View view = inflater.inflate(R.layout.planner_view, this, false);
        addView(view);

        recyclerviewContainer = view.findViewById(R.id.recyclerview);
        recyclerView = recyclerviewContainer.findViewById(R.id.calendar_recyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false));


        todayButton = view.findViewById(R.id.calendar_today_bt);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = new LocalDate();
//                viewPager.setCurrentItem(findIndex());
                recyclerView.setAdapter(new CalendarRecyclerViewAdapter(date.withDayOfMonth(1)));
                setCalendarSelectLayout();
            }
        });
        yearLiteral = view.findViewById(R.id.year_text);
        monthLiteral = view.findViewById(R.id.month_text);
        calendarHeader = view.findViewById(R.id.calendar_header);
        sunday = calendarHeader.findViewById(R.id.sun);
        monday = calendarHeader.findViewById(R.id.mon);
        tuesday = calendarHeader.findViewById(R.id.tue);
        wednesday = calendarHeader.findViewById(R.id.wed);
        thursday = calendarHeader.findViewById(R.id.thu);
        friday = calendarHeader.findViewById(R.id.fri);
        saterday = calendarHeader.findViewById(R.id.sat);

        setLanguage();


        if(this.date != null) {
            yearText = view.findViewById(R.id.calendar_year_text);
            monthText = view.findViewById(R.id.calendar_month_text);
            setCalendarSelectLayout();

            calendarSelectView = view.findViewById(R.id.calendar_select_view); // number picker부분
            calendarSelectView.setOnClickListener(new View.OnClickListener() { // 검은배경 클릭시
                @Override
                public void onClick(View view) {
                    calendarSelectView.animate().alpha(0.0f);
                    calendarSelectView.setVisibility(View.GONE);
                    isCalendarSelectClicked = false;
                    calendarSelectButton.setVisibility(View.GONE);
                }
            });



            calendarSelectLayout = view.findViewById(R.id.calendar_select_layout);
            calendarSelectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final NumberPicker yearPicker = view.findViewById(R.id.year_picker);
                    final NumberPicker monthPicker = view.findViewById(R.id.month_picker);

                    calendarSelectButton = view.findViewById(R.id.calendar_select_bt);
                    calendarSelectButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isCalendarSelectClicked = false;
                            calendarSelectView.setVisibility(View.GONE);
                            initView(new LocalDate(yearPicker.getValue(), monthPicker.getValue(), 1), plannerFragment);
                        }
                    });

                    if(!isCalendarSelectClicked) {
                        isCalendarSelectClicked = true;

                        calendarSelectButton.setVisibility(View.VISIBLE);

                        calendarSelectView.setVisibility(View.VISIBLE);
                        calendarSelectView.setAlpha(0.0f);
                        calendarSelectView.animate().alpha(1.0f);

                        monthPicker.setWrapSelectorWheel(false);
                        monthPicker.setMinValue(1);
                        monthPicker.setMaxValue(12);
                        monthPicker.setValue(localDate.monthOfYear().get());

                        yearPicker.setWrapSelectorWheel(false);
                        yearPicker.setMinValue(2000);
                        yearPicker.setMaxValue(2099);
                        yearPicker.setValue(localDate.year().get());
                    } else {
                        calendarSelectView.animate().alpha(0.0f);
                        calendarSelectView.setVisibility(View.GONE);
                        isCalendarSelectClicked = false;
                        calendarSelectButton.setVisibility(View.GONE);
                    }
                }
            });

            previousButton = view.findViewById(R.id.calendar_previous_bt);
            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    date = date.minusMonths(1);
                    recyclerView.setAdapter(new CalendarRecyclerViewAdapter(date.withDayOfMonth(1)));
                    setCalendarSelectLayout();
                }
            });
            nextButton = view.findViewById(R.id.calendar_next_bt);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    date = date.plusMonths(1);
                    recyclerView.setAdapter(new CalendarRecyclerViewAdapter(date.withDayOfMonth(1)));
                    setCalendarSelectLayout();
                }
            });
            recyclerView.setAdapter(new CalendarRecyclerViewAdapter(date.withDayOfMonth(1)));
        }
    }

    public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public LocalDate calendar;
        private int startDay;
        private int lastDay;
        private int count;

        private int calendarCount;
        private int plusCount;

        LocalDate previousCalendar;
        LocalDate nextCalendar;

        FileSaveNotifyListener fileSaveNotifyListener;
        Context mContext;
        CalendarRecyclerViewAdapter thisAdapter;

        private TakePictureDialog takePictureDialog;

        public CalendarRecyclerViewAdapter(LocalDate localDate) {
            this.calendar = localDate;
            this.startDay = this.calendar.getDayOfWeek() == 7 ? 0 : this.calendar.getDayOfWeek(); // 1일의 요일
            this.lastDay = this.calendar.toDateTimeAtStartOfDay().dayOfMonth().withMaximumValue().getDayOfWeek() == 7 ? 0 : this.calendar.toDateTimeAtStartOfDay().dayOfMonth().withMaximumValue().getDayOfWeek(); // 마지막날 요일(일요일은 7)
            this.calendarCount = this.calendar.dayOfMonth().withMaximumValue().getDayOfMonth();
            this.count = startDay + (6 - lastDay) + this.calendarCount;
            this.plusCount = 0;

        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.planner_item, parent, false);//임시
            view.setLayoutParams(new LinearLayout.LayoutParams(parent.getWidth() / 7, parent.getHeight() / (this.count / 7)));
            final ViewHolder viewHolder = new ViewHolder(view);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(viewHolder.eventExist.getVisibility() == View.VISIBLE){
                        takePictureDialog = new TakePictureDialog(context, fileSaveNotifyListener);
                        takePictureDialog.setDate(viewHolder.getLocalDate());
                        takePictureDialog.show();
                        return;
                    }
                    FileDestinationListener fileDestinationListener = new FileDestinationListener() {
                        @Override
                        public void doGallery() {
                            goToAlbum();
                            selectedDate = viewHolder.localDate;
                        }

                        @Override
                        public void doCamera() {
                            fileSaveNotifyListener = new FileSaveNotifyListener() {
                                @Override
                                public int describeContents() {
                                    return 0;
                                }

                                @Override
                                public void writeToParcel(Parcel parcel, int i) {

                                }

                                @Override
                                public void notifyDatasetChanged() {
                                    viewHolder.eventExist.setVisibility(View.VISIBLE);
                                }
                            };
                            takePictureDialog = new TakePictureDialog(context, fileSaveNotifyListener);
                            takePictureDialog.setDate(viewHolder.getLocalDate());
                            takePictureDialog.show();
                        }
                    };

                    SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
                    int language = sharedPreferences.getInt("language", 0);
                    SelectDialog selectDialog = new SelectDialog(context, fileDestinationListener, language);
                    selectDialog.show();
                    Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    Window window = selectDialog.getWindow();
                    int x = (int)(size.x * 0.8f);
                    int y = (int)(size.y * 0.35f);
                    window.setLayout(x,y);
                }
            };

            view.setOnClickListener(listener); // item click listener

            return viewHolder;
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            if (position < startDay) {// 전달
                this.previousCalendar = this.calendar.minusDays(startDay - position);
                viewHolder.setLocalDate(this.previousCalendar);
                viewHolder.dateText.setText(this.previousCalendar.dayOfMonth().getAsText());
                viewHolder.dateText.setTextColor(Color.parseColor("#A9A9A9"));
                if (isExistToday(this.previousCalendar)) {
                    viewHolder.dateText.setBackgroundResource(R.drawable.ic_brightness_1_black_16dp);
                    isExistToday = true;
                }
            } else if (position >= startDay + this.calendarCount) { // 다음달
                this.nextCalendar = this.calendar.plusDays(plusCount++);
                viewHolder.setLocalDate(this.nextCalendar);
                viewHolder.dateText.setText(this.nextCalendar.dayOfMonth().getAsText());
                viewHolder.dateText.setTextColor(Color.parseColor("#A9A9A9"));
                if (isExistToday(this.nextCalendar)) {
                    viewHolder.dateText.setBackgroundResource(R.drawable.ic_brightness_1_black_16dp);
                    isExistToday = true;
                }
            } else { // 현재달
                viewHolder.setLocalDate(this.calendar);
                viewHolder.dateText.setText(this.calendar.dayOfMonth().getAsText());
                if(this.calendar.getDayOfWeek() == 6) { // 토요일
                    viewHolder.dateText.setTextColor(Color.BLUE);
                } else if(this.calendar.getDayOfWeek() == 7) { // 일요일
                    viewHolder.dateText.setTextColor(Color.RED);
                } else {
                    viewHolder.dateText.setTextColor(Color.parseColor("#000000"));

                }
                if (isExistToday(this.calendar)) {
                    viewHolder.dateText.setBackgroundResource(R.color.colorToday);
                    viewHolder.dateText.setTextColor(Color.parseColor("#ffffff"));
                    isExistToday = true;
                }

                File file = new File(Environment.getExternalStorageDirectory()+"/Pictures/DailyCloset", convertFileName(viewHolder.getLocalDate()) + ".jpg");

                if(file.canRead()){
                    viewHolder.eventExist.setVisibility(View.VISIBLE);
                }

                this.calendar = this.calendar.plusDays(1);
            }
        }

        @Override
        public int getItemCount() {
            return this.count;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            LocalDate localDate;
            ImageView eventExist;

            public ViewHolder(View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.planner_item_day);
                eventExist = itemView.findViewById(R.id.event_exist);
            }

            public void setLocalDate(LocalDate localDate) {
                this.localDate = localDate;
            }

            public LocalDate getLocalDate() {
                return this.localDate;
            }
        }

    }

    private boolean isExistToday(LocalDate date) {
        int[] todays = new LocalDate().getValues(); // size : 3 [0] = year, [1] = month, [2] = day
        int[] dates = date.getValues();

        return todays[0] == dates[0] && todays[1] == dates[1] && todays[2] == dates[2];
    }


    private void setCalendarSelectLayout() {
        yearText.setText(date.year().getAsText());
        if(isEnglishCalendar){
            monthText.setText(getMonthString(date.monthOfYear().get()));
        }
        else{
            monthText.setText(String.valueOf(date.monthOfYear().get()));
        }
        isExistToday = isExistToday(date);

        if(!isExistToday) {
            todayButton.setVisibility(View.VISIBLE);
        } else {
            todayButton.setVisibility(View.GONE);
        }
    }


    public String convertFileName(LocalDate date){
        String str = date.toString("yyyyMMdd");
        return str;
    }

    private void setLanguage(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        int language = sharedPreferences.getInt("language", 0);
        switch(language){
            case 0:
                yearLiteral.setVisibility(View.GONE);
                monthLiteral.setVisibility(View.GONE);
                isEnglishCalendar = true;
                monday.setText("Mon");
                tuesday.setText("Tue");
                wednesday.setText("Wed");
                thursday.setText("Thu");
                friday.setText("Fri");
                saterday.setText("Sat");
                sunday.setText("Sun");
                break;
            case 1:
                yearLiteral.setVisibility(View.VISIBLE);
                monthLiteral.setVisibility(View.VISIBLE);
                yearLiteral.setText("년");
                monthLiteral.setText("월");
                monday.setText("월");
                tuesday.setText("화");
                wednesday.setText("수");
                thursday.setText("목");
                friday.setText("금");
                saterday.setText("토");
                sunday.setText("일");
                break;
            case 2:
                yearLiteral.setVisibility(View.VISIBLE);
                monthLiteral.setVisibility(View.VISIBLE);
                yearLiteral.setText("年");
                monthLiteral.setText("月");
                monday.setText("月");
                tuesday.setText("火");
                wednesday.setText("水");
                thursday.setText("木");
                friday.setText("金");
                saterday.setText("土");
                sunday.setText("日");
        }
    }

    private String getMonthString(int month){
        switch (month){
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
        }
        return null;
    }


    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        ((Activity)context).startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void saveGalleryImage(String filePath){
        File source = new File(filePath);
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures/DailyCloset", convertFileName(selectedDate) + ".jpg");

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(file) ;
            byte[] b = new byte[4096];
            int cnt = 0;
            while((cnt=fis.read(b)) != -1){
                fos.write(b, 0, cnt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
