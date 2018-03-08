package cn.wangxiao.crm.myprizedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyPrizeView myPrizeView;
    private String[] string = new String[]{"https://cdn.duitang.com/uploads/item/201506/29/20150629091228_aF2WC.jpeg", "http://pic13.nipic.com/20110421/7074946_131907700142_2.jpg",
            "http://pic.58pic.com/58pic/15/39/80/91k58PICXEU_1024.jpg", "http://scimg.jb51.net/allimg/160706/103-160F6095531355.jpg", "http://pic23.photophoto.cn/20120624/0010023982061468_b.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        myPrizeView = (MyPrizeView) findViewById(R.id.myPrizeView);

        final EditText editText = (EditText) findViewById(R.id.edittext);


        findViewById(R.id.xixi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // myPrizeView.startRotate(1);
                setNumber(Integer.parseInt(editText.getText().toString().trim()) );
            }
        });
    }

    public void setNumber(int number) {
        List<MyPrizeBean> myPrizeBeanList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            MyPrizeBean bean = new MyPrizeBean();
            bean.text = "" + (i + 1);
            bean.icon = string[i % string.length];
            myPrizeBeanList.add(bean);
        }
        myPrizeView.setListData(myPrizeBeanList);

        myPrizeView.startRotate(number);
    }
}
