package com.pinit.pinitmobileapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.appyvet.materialrangebar.RangeBar;

import java.util.*;

public class FilterPanelLayout extends LinearLayout implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

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
                daySpinner.setEnabled(false);
                monthSpinner.setEnabled(false);
                yearSpinner.setEnabled(false);
                break;
            case R.id.filter_year:
                daySpinner.setEnabled(false);
                monthSpinner.setEnabled(false);
                yearSpinner.setEnabled(true);
                break;
            case R.id.filter_month:
                daySpinner.setEnabled(false);
                monthSpinner.setEnabled(true);
                yearSpinner.setEnabled(true);
                break;
            case R.id.filter_day:
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
        if (!yearSpinner.isEnabled()) {
            // All
            rangeBar.setTickEnd(2018);
            rangeBar.setRangePinsByValue(2016, 2018);
            rangeBar.setTickStart(2016);
        } else if (!monthSpinner.isEnabled()) {
            // Year
            rangeBar.setTickStart(0);
            rangeBar.setTickEnd(12);
            rangeBar.setRangePinsByValue(0, 12);
        } else if (!daySpinner.isEnabled()) {
            // Month
            int maxDays = getMaxDaysInSelectedMonthAndYear();
            rangeBar.setTickStart(0);
            rangeBar.setTickEnd(maxDays);
            rangeBar.setRangePinsByValue(0, maxDays);
        } else {
            // Day
            rangeBar.setTickStart(0);
            rangeBar.setTickEnd(24);
            rangeBar.setRangePinsByValue(0, 24);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing!
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
}
