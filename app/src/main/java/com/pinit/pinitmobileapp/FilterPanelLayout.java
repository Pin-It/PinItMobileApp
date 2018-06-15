package com.pinit.pinitmobileapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.appyvet.materialrangebar.RangeBar;

import java.text.SimpleDateFormat;
import java.util.*;

public class FilterPanelLayout extends LinearLayout implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, RangeBar.OnRangeBarChangeListener {

    private static final int NUM_YEARS_TO_SHOW = 3;

    private RadioGroup radioGroup;
    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private Spinner daySpinner;
    private RangeBar rangeBar;

    private List<Integer> years;
    private List<Integer> months;
    private List<Integer> days;
    private ArrayAdapter<Integer> yearAdapter;
    private ArrayAdapter<Integer> monthAdapter;
    private ArrayAdapter<Integer> dayAdapter;

    private TextView description;

    private OnFilterChangedListener listener;

    private FilterMode currentMode = FilterMode.ALL;

    public FilterPanelLayout(Context context) {
        super(context);
        init();
    }

    public FilterPanelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterPanelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FilterPanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View panel = inflate(getContext(), R.layout.layout_filter_panel, this);

        radioGroup = panel.findViewById(R.id.filter_period);
        yearSpinner = panel.findViewById(R.id.filter_year_picker);
        monthSpinner = panel.findViewById(R.id.filter_month_picker);
        daySpinner = panel.findViewById(R.id.filter_day_picker);
        rangeBar = panel.findViewById(R.id.filter_range);
        description = panel.findViewById(R.id.filter_description);

        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.check(R.id.filter_all);

        years = new ArrayList<>();
        months = new ArrayList<>();
        days = new ArrayList<>();

        yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        dayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, days);

        updateYearSpinner();
        updateMonthSpinner();
        updateDaySpinner();

        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);
        daySpinner.setAdapter(dayAdapter);

        yearSpinner.setOnItemSelectedListener(this);
        monthSpinner.setOnItemSelectedListener(this);
        daySpinner.setOnItemSelectedListener(this);

        rangeBar.setOnRangeBarChangeListener(this);
    }

    private void updateYearSpinner() {
        years.clear();
        int currentYear = getCurrentYear();
        for (int i = currentYear - NUM_YEARS_TO_SHOW + 1; i <= currentYear; i++) {
            years.add(i);
        }
        yearAdapter.notifyDataSetChanged();
        yearSpinner.setSelection(NUM_YEARS_TO_SHOW - 1);  // default to current year
    }

    private void updateMonthSpinner() {
        months.clear();
        int currentMonth = 1;
        int until = 12;
        if (getSelectedYear() == getCurrentYear()) {
            currentMonth = getCurrentMonth();
            until = currentMonth;
        }
        for (int i = 1; i <= until; i++) {
            months.add(i);
        }
        monthAdapter.notifyDataSetChanged();
        monthSpinner.setSelection(currentMonth - 1);
    }

    private void updateDaySpinner() {
        days.clear();
        int selectedYear = getSelectedYear();
        int selectedMonth = getSelectedMonth();
        int currentDay = 1;
        int until = 31;
        if (selectedYear == getCurrentYear() && selectedMonth == getCurrentMonth()) {
            currentDay = getCurrentDay();
            until = currentDay;
        } else if (selectedYear > 0 && selectedMonth > 0) {
            until = getMaxDaysInSelectedMonthAndYear();
        }
        for (int i = 1; i <= until; i++) {
            days.add(i);
        }
        dayAdapter.notifyDataSetChanged();
        daySpinner.setSelection(currentDay - 1);
    }

    private int getMaxDaysInSelectedMonthAndYear() {
        int year = getSelectedYear();
        int month = getSelectedMonth();
        return new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.filter_all:
                currentMode = FilterMode.ALL;
                daySpinner.setEnabled(false);
                monthSpinner.setEnabled(false);
                yearSpinner.setEnabled(false);
                break;
            case R.id.filter_year:
                currentMode = FilterMode.YEAR;
                daySpinner.setEnabled(false);
                monthSpinner.setEnabled(false);
                yearSpinner.setEnabled(true);
                break;
            case R.id.filter_month:
                currentMode = FilterMode.MONTH;
                daySpinner.setEnabled(false);
                monthSpinner.setEnabled(true);
                yearSpinner.setEnabled(true);
                break;
            case R.id.filter_day:
                currentMode = FilterMode.DAY;
                daySpinner.setEnabled(true);
                monthSpinner.setEnabled(true);
                yearSpinner.setEnabled(true);
                break;
        }
        updateRangeBar();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.filter_year_picker:
                updateMonthSpinner();
                updateDaySpinner();
                break;
            case R.id.filter_month_picker:
                updateDaySpinner();
                break;
            case R.id.filter_day_picker:
                break;
        }
        updateRangeBar();
    }

    private void updateRangeBar() {
        switch (currentMode) {
            case ALL:
                rangeBar.setTickEnd(2018);
                rangeBar.setRangePinsByValue(2016, 2018);
                rangeBar.setTickStart(2016);
                break;
            case YEAR:
                rangeBar.setTickStart(1);
                rangeBar.setTickEnd(12);
                rangeBar.setRangePinsByValue(1, 12);
                break;
            case MONTH:
                int maxDays = getMaxDaysInSelectedMonthAndYear();
                rangeBar.setTickStart(1);
                rangeBar.setTickEnd(maxDays);
                rangeBar.setRangePinsByValue(1, maxDays);
                break;
            case DAY:
                rangeBar.setTickStart(0);
                rangeBar.setTickEnd(23);
                rangeBar.setRangePinsByValue(0, 23);
                break;
        }
        onRangeChangeListener(rangeBar, rangeBar.getLeftIndex(), rangeBar.getRightIndex(), rangeBar.getLeftPinValue(), rangeBar.getRightPinValue());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing!
    }

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        updateDescription();
        if (listener != null) {
            listener.onFilterChanged(getStartTime(), getEndTime(true));
        }
    }

    private void updateDescription() {
        Calendar start = getStartTime();
        Calendar end = getEndTime(false);

        SimpleDateFormat selectedSDF = new SimpleDateFormat("");
        SimpleDateFormat sdf = new SimpleDateFormat("");
        String format = "Showing %sdangers pinned %s%s\nFrom %s to %s.";
        String all = "";
        String proposition = "in ";
        String selected = "";
        String fromStart = "";
        String toEnd = "";
        switch (currentMode) {
            case ALL:
                all = "all ";
                proposition = "";
                selectedSDF = new SimpleDateFormat("");
                sdf = new SimpleDateFormat("yyyy");
                break;
            case YEAR:
                selectedSDF = new SimpleDateFormat("yyyy");
                sdf = new SimpleDateFormat("MMMM");
                break;
            case MONTH:
                selectedSDF = new SimpleDateFormat("MMMM yyyy");
                sdf = new SimpleDateFormat("d/M");
                break;
            case DAY:
                proposition = "on ";
                selectedSDF = new SimpleDateFormat("d MMMM yyyy");
                sdf = new SimpleDateFormat("h a");
                break;
        }
        selected = selectedSDF.format(start.getTime());
        fromStart = sdf.format(start.getTime());
        toEnd = sdf.format(end.getTime());
        String text = String.format(format, all, proposition, selected, fromStart, toEnd);
        description.setText(text);
    }

    private Calendar getTimeFromSpinners() {
        int year = 1970;
        int month = 1;
        int day = 1;
        switch (currentMode) {
            case DAY:
                day = getSelectedDay();
            case MONTH:
                month = getSelectedMonth();
            case YEAR:
                year = getSelectedYear();
                break;
        }
        return new GregorianCalendar(year, month - 1, day);
    }

    private Calendar getStartTime() {
        Calendar cal = getTimeFromSpinners();

        switch (currentMode) {
            case DAY:
                // hour missing
                int hour = rangeBar.getLeftIndex();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                break;
            case MONTH:
                // day missing
                int day = Integer.valueOf(rangeBar.getLeftPinValue());
                cal.set(Calendar.DAY_OF_MONTH, day);
                break;
            case YEAR:
                // month missing
                int month = rangeBar.getLeftIndex();
                cal.set(Calendar.MONTH, month);
                break;
            case ALL:
                // year missing
                int year = Integer.valueOf(rangeBar.getLeftPinValue());
                cal.set(Calendar.YEAR, year);
                break;
        }

        return cal;
    }

    private Calendar getEndTime(boolean exclusive) {
        Calendar cal = getTimeFromSpinners();

        int extra = exclusive ? 1 : 0;

        switch (currentMode) {
            case DAY:
                // hour missing
                int hour = rangeBar.getRightIndex() + extra;
                cal.set(Calendar.HOUR_OF_DAY, hour);
                break;
            case MONTH:
                // day missing
                int day = Integer.valueOf(rangeBar.getRightPinValue()) + extra;
                cal.set(Calendar.DAY_OF_MONTH, day);
                break;
            case YEAR:
                // month missing
                int month = rangeBar.getRightIndex() + extra;
                cal.set(Calendar.MONTH, month);
                break;
            case ALL:
                // year missing
                int year = Integer.valueOf(rangeBar.getRightPinValue()) + extra;
                cal.set(Calendar.YEAR, year);
                break;
        }

        return cal;
    }

    public void setOnFilterChangedListener(OnFilterChangedListener listener) {
        this.listener = listener;
    }

    /**
     * Get today's year
     *
     * @return year (e.g. 2018)
     */
    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Get today's month
     *
     * @return 1-based month (e.g. 1, 2, ..., 12)
     */
    private int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * Get today's day in month
     *
     * @return day of month (e.g. 1, 2, ..., 31)
     */
    private int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns the year selected on the year spinner, returns -1 if nothing is selected
     *
     * @return year (e.g. 2018)
     */
    private int getSelectedYear() {
        return getSelectedSpinnerValue(yearSpinner);
    }

    /**
     * Returns the month selected on the month spinner, returns -1 if nothing is selected
     *
     * @return 1-based month (e.g. 1, 2, ..., 12)
     */
    private int getSelectedMonth() {
        return getSelectedSpinnerValue(monthSpinner);
    }

    /**
     * Returns the day selected on the day spinner, returns -1 if nothing is selected
     *
     * @return day (e.g. 1, 2, ..., 31)
     */
    private int getSelectedDay() {
        return getSelectedSpinnerValue(daySpinner);
    }

    /**
     * Returns the value selected on a specified spinner, returns -1 if nothing is selected
     *
     * @param spinner the specified spinner
     * @return the value as displayed on the spinner
     */
    private int getSelectedSpinnerValue(Spinner spinner) {
        Object value = spinner.getSelectedItem();
        if (value == null) {
            return -1;
        }
        return (int) value;
    }

    private enum FilterMode {
        ALL, YEAR, MONTH, DAY
    }
}
