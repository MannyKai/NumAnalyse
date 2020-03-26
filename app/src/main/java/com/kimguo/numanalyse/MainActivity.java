package com.kimguo.numanalyse;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import com.yarolegovich.lovelydialog.ViewConfigurator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List<NumberItem> numberItemList;
    HomeBaseAdapter mAdapter;
    SharedPreferences preference;
    TextView totalTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preference = getSharedPreferences("NumbersArray",MODE_PRIVATE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });
        numberItemList = new ArrayList<>();

        totalTV = findViewById(R.id.total_tv);
        mRecyclerView = findViewById(R.id.home_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HomeBaseAdapter(R.layout.home_item_view, numberItemList);
        mAdapter.getDraggableModule().setSwipeEnabled(true);
        mAdapter.getDraggableModule().setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                totalTV.setText(String.format("共 %d 条数据",numberItemList.size()));
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                canvas.drawColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
            }
        });
        mAdapter.getDraggableModule().getItemTouchHelperCallback().setSwipeMoveFlags(ItemTouchHelper.START);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        if(numberItemList.isEmpty())
        {
            if(preference.contains("Saved"))
            {
                String numbersSaved = preference.getString("Saved","");
                if(numbersSaved.length() > 4)
                {
                    String[] numbers = numbersSaved.split("-");
                    for(String num : numbers)
                    {
                        numberItemList.add(new NumberItem(num));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
        totalTV.setText(String.format("共 %d 条数据",numberItemList.size()));
        super.onResume();
    }

    @Override
    protected void onStop() {
        saveInputed();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void saveInputed()
    {
        if(numberItemList.isEmpty()) {
            preference.edit().putString("Saved","").apply();
            return;
        }
        StringBuffer stringBuffer = new StringBuffer(numberItemList.get(0).sNumber);
        for(int i=1; i<numberItemList.size(); i++)
        {
            stringBuffer.append("-");
            stringBuffer.append(numberItemList.get(i).sNumber);
        }
        preference.edit().putString("Saved",stringBuffer.toString()).apply();
    }

   private  ViewConfigurator<EditText> configu = new ViewConfigurator() {
        @Override
        public void configureView(View v) {
            EditText tv = (EditText)v;
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextSize(30.f);
            tv.setTextColor(Color.BLACK);
            tv.setLetterSpacing(0.8f);
        }
    };
    private void showInputDialog()
    {

        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.colorPrimaryDark)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setTitle(R.string.text_input_title)
                .setMessage(R.string.text_input_message)
                .setIcon(R.drawable.ic_add_circle_black_24dp)
                .setInputFilter(R.string.text_input_error_message, new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return text.length() < 8 && text.length() > 0;
                    }
                })
                .setConfirmButton("添加", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        numberItemList.add(new NumberItem(text));
                        mAdapter.notifyDataSetChanged();
                        totalTV.setText(String.format("共 %d 条数据",numberItemList.size()));
                    }
                })
                .setNegativeButton("取消",null)
                .configureEditText(configu)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(numberItemList.isEmpty())
        {
            new LovelyInfoDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_add_alert_36dp)
                    .setTitle("提示")
                    .setMessage("没有要操作的数据，请先添加数据")
                    .setConfirmButtonText("确定").show();
            return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_do) {
            StringBuffer sBuff = new StringBuffer();
            for(NumberItem number : numberItemList)
            {
                sBuff.append(number.sNumber);
            }
            Intent toAnalyse = new Intent(MainActivity.this,AnalyseActivity.class);
            toAnalyse.putExtra("Numbers",sBuff.toString());
            startActivity(toAnalyse);
            return true;
        }else if(id == R.id.action_clear)
        {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.ic_delete_36dp)
                    .setTitle("清空数据")
                    .setMessage("确认要删除所有数据？")
                    .setPositiveButton("删除", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            numberItemList.clear();
                            totalTV.setText(String.format("共 %d 条数据",numberItemList.size()));
                            mAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("取消",null).show();
        }

        return super.onOptionsItemSelected(item);
    }

    class HomeBaseAdapter extends BaseQuickAdapter<NumberItem, BaseViewHolder> implements DraggableModule
    {
        public HomeBaseAdapter(int layoutResId, @Nullable List<NumberItem> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, NumberItem item) {
           helper.setText(R.id.text,item.sNumber);
        }

    }


    class NumberItem
    {
        String sNumber;
        public NumberItem(String strNum) {
            this.sNumber = strNum;
        }

        public String[] getNumberArray()
        {
            if(sNumber == null || sNumber.length() < 1)
            {
                return null;
            }
            return sNumber.split("");
        }



    }
}
