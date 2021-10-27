package com.example.cehuacaidan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<MyBean> myBeans;
    private SlideLayout slideLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewLayout();                       //1、加载控件，只有一个listview
                                                //2、自定义控件，让控件显示具体看SlideLayout类
        loadListViewData();                     //3、使用适配器为listview加载数据
                                                //4、在getView中为自定义控件加载自定义监听
    }


    private void loadListViewData() {
        addData();                              //3-1、准备要放在listview上的数据
        createAdapter();                        //3-2、创建适配器，为lsitView加载适配器

    }

    private void createAdapter() {
        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
    }

    private void addData() {
        myBeans = new ArrayList<>();
        for(int i=0;i<100;i++){
            myBeans.add(new MyBean("Content"+i));
        }
    }

    private void initViewLayout() {
        listView = findViewById(R.id.lv_main);
    }


    /**
     * 自定义适配器类
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return myBeans.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView ==null){
                convertView = View.inflate(MainActivity.this,R.layout.item_main,null);
                viewHolder = new ViewHolder();
                viewHolder.item_content = (TextView) convertView.findViewById(R.id.item_content);
                viewHolder.item_menu = (TextView) convertView.findViewById(R.id.item_menu);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            MyBean myBean = myBeans.get(position);
            viewHolder.item_content.setText(myBean.getName());

            //一次只能打开一个item
            slideLayout = (SlideLayout) convertView;
            slideLayout.setOnStateChangeListenter(new MyOnStateChangeListenter());


            //为删除的子view添加点击事件
            viewHolder.item_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SlideLayout slideLayout = (SlideLayout) v.getParent();
                    slideLayout.closeMenu();
                    myBeans.remove(myBean);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    static class ViewHolder{
        TextView item_content;
        TextView item_menu;
    }


    /**
     * 自定义监听
     */
    private SlideLayout lastslideLayout;
    class MyOnStateChangeListenter implements SlideLayout.OnStateChangeListenter {

        /**
         * 判断关闭的和打开的是不是同一个item，如果是就把赋值为null，重新开始算
         * @param layout
         */
        @Override
        public void onClose(SlideLayout layout) {
            System.out.println(lastslideLayout+"   ======");
            if(lastslideLayout ==layout){
                lastslideLayout = null;
            }

        }

        /**
         * 当list的一个item点击的时候，判断是不是第一个点击的item如果不是就把上一次的item关闭
         * @param layout
         */
        @Override
        public void onDown(SlideLayout layout) {
            if(lastslideLayout != null && lastslideLayout!=layout){
                lastslideLayout.closeMenu();
            }

        }

        /**
         * 当item被滑动开启，则把当前的这个item保存起来
         * @param layout
         */
        @Override
        public void onOpen(SlideLayout layout) {
            lastslideLayout = layout;
        }
    }

}