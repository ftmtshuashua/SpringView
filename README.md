demo：[点击下载](https://raw.githubusercontent.com/ftmtshuashua/SpringView/master/app-debug.apk)

# SpringView

SpringView是一个继承于FrameLayout的轻量级弹性布局,主要负责弹动事件分发,能轻易的实现下拉\上拉动画效果。充分发挥你的想象它能为你做很多事情。

**获取 SpringView**
--------

使用 Gradle:
```
dependencies {
   compile 'com.lfp.widget:SpringViewLibrary:1.0.8'
}
```

**如何使用 SpringView**
--------

在布局文件中添加SpringView

```
<com.lfp.widget.springview.SpringView
        android:id="@+id/layout_SpringView"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

           <ListView
               android:layout_width="match_parent"
                       android:layout_height="match_parent"/>

</com.lfp.widget.springview.SpringView>
```

在代码中加载

```
SpringView mSpringView = (SpringView) findViewById(R.id.layout_SpringView);
```

 - 回弹效果

```
SpringView mSpringView = (SpringView) findViewById(R.id.layout_SpringView);
mSpringView.enableSpringback();
```

或者

```
 <com.lfp.widget.springview.SpringView
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         app:enable_springback="true"/>
```


 - 刷新与加载请参考Demo

```
Acitivity(){
    onCreate(){
        SpringView mSpringView = (SpringView) findViewById(R.id.layout_SpringView);
        //设置Head和Foot
        mSpringView.setSpringChild(mRefresh, mLoading);
        //配置刷新和加载互斥
        mLoading.setRefeshFx(mRefresh);
        mRefresh.setLoadingFx(mLoading);
        //设置完成动画执行时间
        mRefresh.setFinishAnimationDuration(1000);
    }

    SimpleHeader mRefresh = new SimpleHeader() {
            @Override
            public void onRefresh() {
                finishRefresh(); //完成刷新
            }
    };

    SimpleBottom mRefresh = new SimpleBottom() {
            @Override
            public void onLoading() {
                finishLoading(); //完成加载
            }
    };
 }

```

**问题反馈**
--------
如果遇到问题或者好的建议，请反馈到我的邮箱：ftmtshuashua@gmail.com 或者在我的博客留言

如果觉得对你有用的话，点一下右上的星星赞一下吧


